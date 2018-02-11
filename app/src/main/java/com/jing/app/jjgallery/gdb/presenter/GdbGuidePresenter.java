package com.jing.app.jjgallery.gdb.presenter;

import android.os.AsyncTask;

import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.GdbConstants;
import com.jing.app.jjgallery.gdb.model.GdbImageProvider;
import com.jing.app.jjgallery.gdb.model.bean.StarProxy;
import com.jing.app.jjgallery.gdb.model.bean.recommend.FilterBean;
import com.jing.app.jjgallery.gdb.model.bean.recommend.FilterModel;
import com.jing.app.jjgallery.gdb.model.db.StarExtendDao;
import com.jing.app.jjgallery.gdb.util.DebugLog;
import com.jing.app.jjgallery.gdb.view.home.GHomeBean;
import com.jing.app.jjgallery.gdb.view.home.IHomeView;
import com.jing.app.jjgallery.gdb.view.recommend.IRecommend;
import com.king.app.gdb.data.RecordCursor;
import com.jing.app.jjgallery.gdb.model.db.RecordExtendDao;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.RecordDao;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.param.DataConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/11/27 0027.
 * the presenter of gdb guide view
 */

public class GdbGuidePresenter {

    private final int NUM_LOAD_MORE = 10;

    private IRecommend recommendView;
    private List<Record> recordList;

    /**
     * 一共缓存3个推荐，previous, next and current
     */
    private Record previousRecord, currentRecord, nextRecord;
    /**
     * 过滤器
     */
    private FilterModel filterModel;
    private RecordCursor recordCursor;

    private RecordExtendDao recordExtendDao;

    private StarExtendDao starExtendDao;

    public GdbGuidePresenter() {
        recordExtendDao = new RecordExtendDao();
        starExtendDao = new StarExtendDao();
    }

    public GdbGuidePresenter(IRecommend recommendView) {
        this();
        this.recommendView = recommendView;
    }

    public void setRecommendView(IRecommend recommendView) {
        this.recommendView = recommendView;
    }

    public void initialize() {
        Observable.create(new ObservableOnSubscribe<List<Record>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<Record>> e) throws Exception {
                RecordDao dao = GdbApplication.getInstance().getDaoSession().getRecordDao();
                recordList = dao.queryBuilder().build().list();
                e.onNext(recordList);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Record>>() {
                    @Override
                    public void accept(List<Record> list) throws Exception {
                        if (recommendView != null) {
                            recommendView.onRecordsLoaded(list);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        if (recommendView != null) {
                            recommendView.onRecordsLoadedFailed(throwable.getMessage());
                        }
                    }
                });
    }

    public void recommendNext() {
        if (nextRecord == null) {
            previousRecord = currentRecord;
            currentRecord = newRecord();
            nextRecord = null;
            recommendView.onRecordRecommand(currentRecord);
        }
        else {
            recommendView.onRecordRecommand(nextRecord);
            previousRecord = currentRecord;
            currentRecord = nextRecord;
            nextRecord = null;
        }
    }

    public void recommendPrevious() {
        if (previousRecord == null) {
            nextRecord = currentRecord;
            currentRecord = newRecord();
            previousRecord = null;
            recommendView.onRecordRecommand(currentRecord);
        }
        else {
            recommendView.onRecordRecommand(previousRecord);
            nextRecord = currentRecord;
            currentRecord = previousRecord;
            previousRecord = null;
        }
    }

    /**
     * 获得新记录
     * @return
     */
    public Record newRecord() {
        if (recordList == null || recordList.size() == 0) {
            return null;
        }
        // 没有设置过滤器的情况，直接随机位置
        if (filterModel == null) {
            Random random = new Random();
            int index = Math.abs(random.nextInt()) % recordList.size();
            return recordList.get(index);
        }
        else {// 打乱当前所有记录，选出第一个符合过滤器条件的记录
            Collections.shuffle(recordList);
            boolean pass;
            for (Record record:recordList) {
                pass = true;
                // 记录是NR并且过滤器勾选了支持NR才判定为通过
                if (record.getHdLevel() == DataConstants.RECORD_HD_NR && filterModel.isSupportNR()) {
                    pass = true;
                }
                // 普通记录，以及是NR但是过滤器没有勾选NR，需要检测其他过滤项
                else {
                    for (int i = 0; i < filterModel.getList().size(); i ++) {
                        boolean result = checkPassFilterItem(record, filterModel.getList().get(i));
                        pass = pass && result;
                    }
                }
                if (pass) {
                    return record;
                }
            }
            return null;
        }
    }

    /**
     * 检查是否通过过滤项
     * @param record
     * @param filterBean
     * @return
     */
    private boolean checkPassFilterItem(Record record, FilterBean filterBean) {
        int min = filterBean.getMin();
        int max = filterBean.getMax();
        // 只设置了min，没有设置max
        if (filterBean.getMax() == 0) {
            max = Integer.MAX_VALUE;
        }

        // 如果没勾上，直接返回符合条件
        if (!filterBean.isEnable()
                // 也没有设置条件
                || min == 0 && max == Integer.MAX_VALUE
                // min 大于了 max是不合理情况，也视为符合条件
                || min > max) {
            return true;
        }

        int targetScore = getTargetScore(record, filterBean.getKeyword());
        DebugLog.e("targetScore = " + targetScore);
        return targetScore >= min && targetScore <= max;
    }

    /**
     * 根据过滤器的keyword判断score对应的record参数
     *
     * @param record
     * @param keyword
     * @return
     */
    private int getTargetScore(Record record, String keyword) {
        if (keyword.equals(GdbConstants.FILTER_KEY_SCORE)) {
            return record.getScore();
        }
        else if (keyword.equals(GdbConstants.FILTER_KEY_SCORE_STORY)) {
            if (record.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                return record.getRecordType1v1().getScoreStory();
            }
            else if (record.getType() == DataConstants.VALUE_RECORD_TYPE_3W) {
                return record.getRecordType3w().getScoreStory();
            }
        }
        else if (keyword.equals(GdbConstants.FILTER_KEY_SCORE_DEPRECATED)) {
            return record.getDeprecated();
        }
        else if (keyword.equals(GdbConstants.FILTER_KEY_SCORE_CUM)) {
            return record.getScoreCum();
        }
        else if (keyword.equals(GdbConstants.FILTER_KEY_SCORE_FK)) {
            return record.getScorePassion();
        }
        else if (keyword.equals(GdbConstants.FILTER_KEY_SCORE_STAR)) {
            return record.getScoreStar();
        }
        else if (keyword.equals(GdbConstants.FILTER_KEY_SCORE_BAREBACK)) {
            return record.getScoreBareback();
        }
        else if (keyword.equals(GdbConstants.FILTER_KEY_SCORE_BJOB)) {
            if (record.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                return record.getRecordType1v1().getScoreBjob();
            }
            else if (record.getType() == DataConstants.VALUE_RECORD_TYPE_3W) {
                return record.getRecordType3w().getScoreBjob();
            }
        }
        else if (keyword.equals(GdbConstants.FILTER_KEY_SCORE_RHYTHM)) {
            if (record.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                return record.getRecordType1v1().getScoreRhythm();
            }
            else if (record.getType() == DataConstants.VALUE_RECORD_TYPE_3W) {
                return record.getRecordType3w().getScoreRhythm();
            }
        }
        else if (keyword.equals(GdbConstants.FILTER_KEY_SCORE_RIM)) {
            if (record.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                return record.getRecordType1v1().getScoreRim();
            }
            else if (record.getType() == DataConstants.VALUE_RECORD_TYPE_3W) {
                return record.getRecordType3w().getScoreRim();
            }
        }
        else if (keyword.equals(GdbConstants.FILTER_KEY_SCORE_SCECE)) {
            if (record.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                return record.getRecordType1v1().getScoreScene();
            }
            else if (record.getType() == DataConstants.VALUE_RECORD_TYPE_3W) {
                return record.getRecordType3w().getScoreScene();
            }
        }
        else if (keyword.equals(GdbConstants.FILTER_KEY_SCORE_CSHOW)) {
            if (record.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                return record.getRecordType1v1().getScoreCshow();
            }
            else if (record.getType() == DataConstants.VALUE_RECORD_TYPE_3W) {
                return record.getRecordType3w().getScoreCshow();
            }
        }
        else if (keyword.equals(GdbConstants.FILTER_KEY_SCORE_SPECIAL)) {
            return record.getScoreSpecial();
        }
        else if (keyword.equals(GdbConstants.FILTER_KEY_SCORE_FOREPLAY)) {
            if (record.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                return record.getRecordType1v1().getScoreForePlay();
            }
            else if (record.getType() == DataConstants.VALUE_RECORD_TYPE_3W) {
                return record.getRecordType3w().getScoreForePlay();
            }
        }
        return 0;
    }

    public Record getCurrentRecord() {
        return currentRecord;
    }

    /**
     * 注入过滤器
     * 注入时清空当前缓存的previous, next
     * @param filterModel
     */
    public void setFilterModel(FilterModel filterModel) {
        this.filterModel = filterModel;
        previousRecord = null;
        nextRecord = null;
        currentRecord = null;
    }

    public List<Record> getLatestRecord(int number) {
        recordCursor = new RecordCursor();
        recordCursor.offset = 0;
        recordCursor.number = number;
        List<Record> list = recordExtendDao.getLatestRecords(recordCursor);
        recordCursor.offset += list.size();
        return list;
    }

    /**
     * 获取home主页数据
     */
    public void loadHomeData(final IHomeView homeView) {
        Observable.create(new ObservableOnSubscribe<GHomeBean>() {
            @Override
            public void subscribe(ObservableEmitter<GHomeBean> e) throws Exception {
                GHomeBean homeBean = new GHomeBean();
                homeBean.setRecordList(getLatestRecord(NUM_LOAD_MORE));

                List<StarProxy> starList = new ArrayList<>();

                // 随机获取N个favor
                List<Star> favorList = starExtendDao.getRandomFavors(10);
                for (int i = 0; i < favorList.size(); i ++) {
                    StarProxy proxy = new StarProxy();
                    Star star = favorList.get(i);
                    proxy.setStar(star);
                    proxy.setImagePath(GdbImageProvider.getStarRandomPath(star.getName(), null));
                    starList.add(proxy);
                }
                homeBean.setStarList(starList);

                e.onNext(homeBean);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<GHomeBean>() {
                    @Override
                    public void accept(GHomeBean data) throws Exception {
                        if (homeView != null) {
                            homeView.onHomeDataLoaded(data);
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        homeView.onHomeDataLoadFailed(throwable.getMessage());
                    }
                });
    }

    /**
     * home 主页列表加载更多数据
     * @param homeView
     */
    public void loadMore(IHomeView homeView) {
        LoadMoreTask task = new LoadMoreTask(homeView);
        // 采用任务队列保证单位时间内只执行一次loadMore
        addToTask(task);
    }

    /**
     * 丢弃快速到达的相同task，保证一次只load限定的数量
     * @param task
     */
    private synchronized void addToTask(LoadMoreTask task) {
        // 队列为空方可执行
        if (executeQueue.size() == 0) {
            // 入队
            executeQueue.offer(task);
            // 执行结束后出队
            task.execute();
        }
    }

    private Queue<LoadMoreTask> executeQueue = new LinkedList<>();


    /**
     * 加载全部记录
     */
    private class LoadMoreTask extends AsyncTask<Integer, Void, List<Record>> {

        private final IHomeView homeView;

        public LoadMoreTask(IHomeView homeView) {
            this.homeView = homeView;
        }

        @Override
        protected void onPostExecute(List<Record> list) {

            if (homeView != null) {
                homeView.onMoreRecordsLoaded(list);
            }

            // 任务执行完成后清空任务队列
            executeQueue.poll();
            super.onPostExecute(list);
        }

        @Override
        protected List<Record> doInBackground(Integer... params) {
            List<Record> list = recordExtendDao.getLatestRecords(recordCursor);
            recordCursor.offset += list.size();
            return list;
        }
    }

}
