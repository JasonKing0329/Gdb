package com.jing.app.jjgallery.gdb.view.home.pad;

import com.jing.app.jjgallery.gdb.BasePresenter;
import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.model.FilterHelper;
import com.jing.app.jjgallery.gdb.model.GdbImageProvider;
import com.jing.app.jjgallery.gdb.model.RecommendModel;
import com.jing.app.jjgallery.gdb.model.SettingProperties;
import com.jing.app.jjgallery.gdb.model.bean.StarProxy;
import com.jing.app.jjgallery.gdb.model.bean.recommend.FilterModel;
import com.jing.app.jjgallery.gdb.model.db.RecordExtendDao;
import com.jing.app.jjgallery.gdb.model.db.StarExtendDao;
import com.jing.app.jjgallery.gdb.util.StarRatingUtil;
import com.king.app.gdb.data.RecordCursor;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.RecordDao;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.param.DataConstants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @desc
 * @auth 景阳
 * @time 2018/2/19 0019 10:33
 */

public class HomePadPresenter extends BasePresenter<HomePadView> {

    private final int NUM_LOAD_MORE = 20;

    private StarExtendDao starExtendDao;

    private RecordExtendDao recordExtendDao;

    private FilterModel filterModel;

    private FilterHelper filterHelper;

    private RecommendModel recommendModel;

    private List<Record> recommendList;

    private List<Record> recommendedList;

    private RecordCursor recordCursor;

    private List<Object> mRecordList;

    private String mLastDay;

    private Disposable timerDisposable;

    @Override
    public void onCreate() {
        starExtendDao = new StarExtendDao();
        recordExtendDao = new RecordExtendDao();
        filterHelper = new FilterHelper();
        recommendModel = new RecommendModel();
        mRecordList = new ArrayList<>();
        recordCursor = new RecordCursor();
        recordCursor.offset = 0;
        recordCursor.number = NUM_LOAD_MORE;
    }

    public void loadHomeData() {
        queryStars()
                .flatMap(new Function<List<StarProxy>, ObservableSource<List<Record>>>() {
                    @Override
                    public ObservableSource<List<Record>> apply(List<StarProxy> stars) throws Exception {
                        view.postShowStars(stars);
                        return queryRecommend();
                    }
                })
                .flatMap(new Function<List<Record>, ObservableSource<List<Object>>>() {
                    @Override
                    public ObservableSource<List<Object>> apply(List<Record> list) throws Exception {
                        createTimer();
                        view.postShowRecommends(list);
                        return queryRecords();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Object>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<Object> list) {
                        view.showRecords(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.showToastLong("Load data error: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void refreshRec() {
        queryRecommend()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Record>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<Record> list) {
                        view.postShowRecommends(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.showToastLong("Load data error: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void refreshRecAndStars() {
        queryStars()
                .flatMap(new Function<List<StarProxy>, ObservableSource<List<Record>>>() {
                    @Override
                    public ObservableSource<List<Record>> apply(List<StarProxy> stars) throws Exception {
                        view.postShowStars(stars);
                        return queryRecommend();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Record>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<Record> list) {
                        view.postShowRecommends(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.showToastLong("Load data error: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void createTimer() {
        int time = SettingProperties.getGdbRecommendAnimTime();
        if (time == 0) {
            time = 8000;
        }
        timerDisposable = Observable.interval(time, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        refreshRec();
                    }
                });
    }

    private Observable<List<StarProxy>> queryStars() {
        return Observable.create(new ObservableOnSubscribe<List<StarProxy>>() {
            @Override
            public void subscribe(ObservableEmitter<List<StarProxy>> e) throws Exception {
                List<StarProxy> starList = new ArrayList<>();
                // 随机获取N个favor
                List<Star> favorList = starExtendDao.getRandomRatingAbove(StarRatingUtil.RATING_VALUE_CP, 10);
                for (int i = 0; i < favorList.size(); i ++) {
                    StarProxy proxy = new StarProxy();
                    Star star = favorList.get(i);
                    proxy.setStar(star);
                    proxy.setImagePath(GdbImageProvider.getStarRandomPath(star.getName(), null));
                    starList.add(proxy);
                }
                // 不够则从star中随机抽取剩余所需
                if (favorList.size() < 10) {
                    int size = 10 - favorList.size();
                    favorList.addAll(starExtendDao.getRandomStars(size));
                }

                for (Star star:favorList) {
                    StarProxy proxy = new StarProxy();
                    proxy.setStar(star);
                    proxy.setImagePath(GdbImageProvider.getStarRandomPath(star.getName(), null));
                    starList.add(proxy);
                }
                e.onNext(starList);
            }
        });
    }

    private Observable<List<Record>> queryRecommend() {
        return Observable.create(new ObservableOnSubscribe<List<Record>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Record>> e) throws Exception {
                filterModel = filterHelper.getFilters();
                recommendList = GdbApplication.getInstance().getDaoSession().getRecordDao()
                        .queryBuilder()
                        .orderDesc(RecordDao.Properties.LastModifyTime)
                        .build().list();
                recommendedList = new ArrayList<>();
                recommendedList.add(newRecord());
                recommendedList.add(newRecord());
                recommendedList.add(newRecord());
                e.onNext(recommendedList);
            }
        });
    }

    public Record getRecommendedRecord(int index) {
        return recommendedList.get(index);
    }

    private Observable<List<Object>> queryRecords() {
        return Observable.create(new ObservableOnSubscribe<List<Object>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Object>> e) throws Exception {

                List<Record> recordList = recordExtendDao.getLatestRecords(recordCursor);
                recordCursor.offset += recordList.size();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                for (Record record: recordList) {
                    String day = sdf.format(new Date(record.getLastModifyTime()));
                    if (!day.equals(mLastDay)) {
                        mRecordList.add(day);
                        mLastDay = day;
                    }
                    mRecordList.add(record);
                }
                e.onNext(mRecordList);
            }
        });
    }

    /**
     * 获得新记录
     * @return
     */
    public Record newRecord() {
        if (recommendList == null || recommendList.size() == 0) {
            return null;
        }
        // 没有设置过滤器的情况，直接随机位置
        if (filterModel == null) {
            Random random = new Random();
            int index = Math.abs(random.nextInt()) % recommendList.size();
            return recommendList.get(index);
        }
        else {// 打乱当前所有记录，选出第一个符合过滤器条件的记录
            Collections.shuffle(recommendList);
            boolean pass;
            for (Record record: recommendList) {
                pass = true;
                // 记录是NR并且过滤器勾选了支持NR才判定为通过
                if (record.getHdLevel() == DataConstants.RECORD_HD_NR && filterModel.isSupportNR()) {
                    pass = true;
                }
                // 普通记录，以及是NR但是过滤器没有勾选NR，需要检测其他过滤项
                else {
                    boolean result = recommendModel.checkItem(record, filterModel);
                    pass = pass && result;
                }
                if (pass) {
                    return record;
                }
            }
            return null;
        }
    }

    public void loadMore() {
        final int startOffset = recordCursor.offset;
        queryRecords()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Object>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<Object> objects) {
                        view.notifyMoreRecords(startOffset + 1);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.showToastLong("loadMore error: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timerDisposable != null) {
            timerDisposable.dispose();
        }
    }

    public void resetTimer() {
        if (timerDisposable != null) {
            timerDisposable.dispose();
        }
        createTimer();
    }

    public void onStop() {
        if (timerDisposable != null) {
            timerDisposable.dispose();
        }
    }

    public void onResume() {
        if (timerDisposable != null) {
            createTimer();
        }
    }
}
