package com.jing.app.jjgallery.gdb.presenter.record;

import android.os.AsyncTask;

import com.jing.app.jjgallery.gdb.GdbConstants;
import com.jing.app.jjgallery.gdb.http.AppHttpClient;
import com.jing.app.jjgallery.gdb.http.Command;
import com.jing.app.jjgallery.gdb.http.bean.data.DownloadItem;
import com.jing.app.jjgallery.gdb.http.bean.request.GdbCheckNewFileBean;
import com.jing.app.jjgallery.gdb.model.RecordComparator;
import com.jing.app.jjgallery.gdb.model.VideoModel;
import com.jing.app.jjgallery.gdb.model.conf.Configuration;
import com.jing.app.jjgallery.gdb.model.conf.DBInfor;
import com.jing.app.jjgallery.gdb.model.conf.PreferenceValue;
import com.jing.app.jjgallery.gdb.model.db.GdbProviderHelper;
import com.jing.app.jjgallery.gdb.presenter.ManageListPresenter;
import com.jing.app.jjgallery.gdb.view.list.IManageListView;
import com.jing.app.jjgallery.gdb.view.record.IRecordListView;
import com.jing.app.jjgallery.gdb.view.record.IRecordSceneView;
import com.king.service.gdb.GDBProvider;
import com.king.service.gdb.RecordCursor;
import com.king.service.gdb.bean.Record;
import com.king.service.gdb.bean.SceneBean;

import org.reactivestreams.Subscriber;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/7/12 11:22
 */
public class RecordListPresenter extends ManageListPresenter {

    private final int DEFAULT_LOAD_MORE = 20;

    private IRecordListView recordListView;
    private IRecordSceneView recordSceneView;

    private RecordCursor moreCursor;

    /**
     * 如果使用检测服务器端数据功能，view不能为空
     * @param view
     */
    public RecordListPresenter(IManageListView view) {
        super(view);
    }

    public void setRecordListView(IRecordListView recordListView) {
        this.recordListView = recordListView;
    }

    public void setRecordSceneView(IRecordSceneView recordSceneView) {
        this.recordSceneView = recordSceneView;
    }

    public void newRecordCursor() {
        moreCursor = new RecordCursor();
        moreCursor.number = DEFAULT_LOAD_MORE;
    }

    public void checkNewRecordFile() {
        AppHttpClient.getInstance().getAppService().checkNewFile(Command.TYPE_RECORD)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<GdbCheckNewFileBean>() {
                    @Override
                    public void accept(GdbCheckNewFileBean bean) throws Exception {
                        view.onCheckPass(bean.isRecordExisted(), bean.getRecordItems());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        view.onRequestFail();
                    }
                });
    }

    /**
     * 从服务端提供的下载列表中选出已存在的和不存在的
     * @param downloadList 服务端提供的下载列表
     * @param existedList 已存在的下载内容，不能为null
     * @return 未存在的下载内容
     */
    public List<DownloadItem> pickRecordToDownload(List<DownloadItem> downloadList, List<DownloadItem> existedList) {
        List<DownloadItem> list = new ArrayList<>();
        for (DownloadItem item:downloadList) {
            // name 格式为 XXX.png
            String name = item.getName().substring(0, item.getName().lastIndexOf("."));

            String path;
            // 服务端文件处于一级目录
            if (item.getKey() == null) {
                // 检查本地一级目录是否存在
                path = Configuration.GDB_IMG_RECORD + "/" + name + ".png";
                if (!new File(path).exists()) {
                    // 检查本地二级目录是否存在
                    path = Configuration.GDB_IMG_RECORD + "/" + name + "/" + name + ".png";
                }
            }
            // 服务端文件处于二级目录
            else {
                // 只检查本地二级目录是否存在
                path = Configuration.GDB_IMG_RECORD + "/" + item.getKey() + "/" + name + ".png";
            }

            // 检查本地一级目录是否存在
            if (new File(path).exists()) {
                item.setPath(path);
                existedList.add(item);
            } else {
                list.add(item);
            }
        }
        return list;
    }

    /**
     *
     * @param sortMode see PreferenceValue.GDB_SR_ORDERBY_XXX
     * @param desc
     * @param showDeprecated deprecated attribute in Record
     * @param showCanBePlayed there is video in specific disk path
     * @param like name like %like%
     * @param whereScene scene=whereScene
     */
    public void loadRecordList(int sortMode, boolean desc, boolean showDeprecated, boolean showCanBePlayed, String like, String whereScene) {
        // 偏移量从0开始
        newRecordCursor();
        new LoadRecordListTask().execute(sortMode, desc, showDeprecated, showCanBePlayed, moreCursor, like, whereScene);
    }
    /**
     *
     * @param sortMode see PreferenceValue.GDB_SR_ORDERBY_XXX
     * @param desc
     * @param showDeprecated deprecated attribute in Record
     * @param showCanBePlayed there is video in specific disk path
     * @param like name like %like%
     * @param whereScene scene=whereScene
     */
    public void loadMoreRecords(int sortMode, boolean desc, boolean showDeprecated, boolean showCanBePlayed, String like, String whereScene) {
        new LoadMoreRecordsTask().execute(sortMode, desc, showDeprecated, showCanBePlayed, moreCursor, like, whereScene);
    }

    /**
     * 重新加载records
     */
    private class LoadRecordListTask extends AsyncTask<Object, Void, List<Record>> {
        @Override
        protected void onPostExecute(List<Record> list) {

            recordListView.onLoadRecordList(list);

            super.onPostExecute(list);
        }

        @Override
        protected List<Record> doInBackground(Object... params) {
            // load limited records
            if (params.length > 2) {
                int sortMode = (Integer) params[0];
                boolean desc = (Boolean) params[1];
                boolean includeDeprecated = (Boolean) params[2];
                boolean showCanBePlayed = (Boolean) params[3];
                RecordCursor cursor = (RecordCursor) params[4];
                String like = (String) params[5];
                String scene = (String) params[6];
                // 磁盘上文件不多，加载全部供挑选
//                if (showCanBePlayed) {
//                    from = -1;
//                    number = -1;
//                }
                List<Record> list = GdbProviderHelper.getProvider().getRecords(RecordComparator.getSortColumn(sortMode), desc, includeDeprecated, cursor, like, scene);

                if (showCanBePlayed) {
                    list = pickCanBePlayedRecord(list);
                }
                return list;
            }
            // load all records
            else {
                List<Record> list = GdbProviderHelper.getProvider().getAllRecords();
                sortRecords(list, (Integer) params[0], (Boolean) params[1]);
                return list;
            }
        }
    }

    private List<Record> pickCanBePlayedRecord(List<Record> list) {
        List<Record> rList = new ArrayList<>();
        for (Record record:list) {
            if (VideoModel.getVideoPath(record.getName()) != null) {
                rList.add(record);
            }
        }
        return rList;
    }

    /**
     * 加载更多records
     */
    private class LoadMoreRecordsTask extends AsyncTask<Object, Void, List<Record>> {
        @Override
        protected void onPostExecute(List<Record> list) {

            recordListView.onMoreRecordsLoaded(list);

            super.onPostExecute(list);
        }

        @Override
        protected List<Record> doInBackground(Object... params) {
            int sortMode = (Integer) params[0];
            boolean desc = (Boolean) params[1];
            boolean includeDeprecated = (Boolean) params[2];
            boolean showCanBePlayed = (Boolean) params[3];
            RecordCursor cursor = (RecordCursor) params[4];
            String like = (String) params[5];
            String scene = (String) params[6];
            List<Record> list = GdbProviderHelper.getProvider().getRecords(RecordComparator.getSortColumn(sortMode), desc, includeDeprecated, cursor, like, scene);

            if (showCanBePlayed) {
                list = pickCanBePlayedRecord(list);
            }
            return list;
        }
    }

    /**
     * sort records
     * @param recordList
     * @param sortMode
     * @param desc
     */
    public void sortRecords(List<Record> recordList, int sortMode, boolean desc) {
        if (sortMode != PreferenceValue.GDB_SR_ORDERBY_NONE) {
            Collections.sort(recordList, new RecordComparator(sortMode, desc));
        }
    }

    /**
     * load scenes
     */
    public void loadRecordScenes() {
        Observable.create(new ObservableOnSubscribe<List<SceneBean>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<SceneBean>> e) throws Exception {
                List<SceneBean> scenes = GdbProviderHelper.getProvider().getSceneList();
                e.onNext(scenes);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<SceneBean>>() {
                    @Override
                    public void accept(List<SceneBean> sceneBeen) throws Exception {
                        recordSceneView.onScenesLoaded(sceneBeen);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    /**
     *
     * @param sceneList
     * @param curSortType see GdbConstants.SCENE_SORT_XXX
     */
    public void sortScenes(List<SceneBean> sceneList, int curSortType) {
        switch (curSortType) {
            case GdbConstants.SCENE_SORT_NAME:
                Collections.sort(sceneList, new SceneNameComparator());
                break;
            case GdbConstants.SCENE_SORT_NUMBER:
                Collections.sort(sceneList, new SceneNumberComparator());
                break;
            case GdbConstants.SCENE_SORT_AVG:
                Collections.sort(sceneList, new SceneAverageComparator());
                break;
            case GdbConstants.SCENE_SORT_MAX:
                Collections.sort(sceneList, new SceneMaxComparator());
                break;
        }
    }

    public class SceneNameComparator implements Comparator<SceneBean> {

        @Override
        public int compare(SceneBean l, SceneBean r) {
            if (l == null || r == null) {
                return 0;
            }

            return l.getScene().toLowerCase().compareTo(r.getScene().toLowerCase());
        }
    }

    public class SceneAverageComparator implements Comparator<SceneBean> {

        @Override
        public int compare(SceneBean l, SceneBean r) {
            if (l == null || r == null) {
                return 0;
            }

            if (r.getAverage() - l.getAverage() > 0) {
                return 1;
            }
            else if (r.getAverage() - l.getAverage() < 0) {
                return -1;
            }
            else {
                return 0;
            }
        }
    }

    public class SceneNumberComparator implements Comparator<SceneBean> {

        @Override
        public int compare(SceneBean l, SceneBean r) {
            if (l == null || r == null) {
                return 0;
            }

            if (r.getNumber() - l.getNumber() > 0) {
                return 1;
            }
            else if (r.getNumber() - l.getNumber() < 0) {
                return -1;
            }
            else {
                return 0;
            }
        }
    }

    public class SceneMaxComparator implements Comparator<SceneBean> {

        @Override
        public int compare(SceneBean l, SceneBean r) {
            if (l == null || r == null) {
                return 0;
            }

            if (r.getMax() - l.getMax() > 0) {
                return 1;
            }
            else if (r.getMax() - l.getMax() < 0) {
                return -1;
            }
            else {
                return 0;
            }
        }
    }

}
