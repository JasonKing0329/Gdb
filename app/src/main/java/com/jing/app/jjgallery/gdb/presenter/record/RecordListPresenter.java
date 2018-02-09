package com.jing.app.jjgallery.gdb.presenter.record;

import android.os.AsyncTask;

import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.GdbConstants;
import com.jing.app.jjgallery.gdb.model.RecordComparator;
import com.jing.app.jjgallery.gdb.model.VideoModel;
import com.jing.app.jjgallery.gdb.model.conf.PreferenceValue;
import com.jing.app.jjgallery.gdb.model.db.RecordExtendDao;
import com.jing.app.jjgallery.gdb.view.record.IRecordListView;
import com.jing.app.jjgallery.gdb.view.record.IRecordSceneView;
import com.king.app.gdb.data.RecordCursor;
import com.jing.app.jjgallery.gdb.model.db.SceneBean;
import com.king.app.gdb.data.entity.Record;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/7/12 11:22
 */
public class RecordListPresenter {

    private final int DEFAULT_LOAD_MORE = 20;

    private IRecordListView recordListView;
    private IRecordSceneView recordSceneView;

    private RecordCursor moreCursor;

    private RecordExtendDao recordExtendDao;

    public RecordListPresenter() {
        recordExtendDao = new RecordExtendDao();
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

                // 加载可播放的需要从全部记录里通过对比video目录文件信息来挑选
                if (showCanBePlayed) {
                    cursor.offset = -1;
                    cursor.number = -1;
                }

                List<Record> list = recordExtendDao.getRecords(RecordComparator.getSortColumn(sortMode), desc, includeDeprecated, cursor, like, scene);

                // 加载可播放的需要从全部记录里通过对比video目录文件信息来挑选
                if (showCanBePlayed) {
                    list = pickCanBePlayedRecord(list);
                }
                return list;
            }
            // load all records
            else {

                List<Record> list = GdbApplication.getInstance().getDaoSession().getRecordDao()
                        .queryBuilder().build().list();
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
            List<Record> list = recordExtendDao.getRecords(RecordComparator.getSortColumn(sortMode), desc, includeDeprecated, cursor, like, scene);

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
                List<SceneBean> scenes = recordExtendDao.getSceneList();
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
                        throwable.printStackTrace();
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
