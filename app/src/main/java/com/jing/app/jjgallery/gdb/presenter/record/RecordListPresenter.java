package com.jing.app.jjgallery.gdb.presenter.record;

import com.jing.app.jjgallery.gdb.BasePresenter;
import com.jing.app.jjgallery.gdb.GdbConstants;
import com.jing.app.jjgallery.gdb.model.RecordComparator;
import com.jing.app.jjgallery.gdb.model.VideoModel;
import com.jing.app.jjgallery.gdb.model.conf.PreferenceValue;
import com.jing.app.jjgallery.gdb.model.db.RecordExtendDao;
import com.jing.app.jjgallery.gdb.view.record.IRecordListView;
import com.king.app.gdb.data.RecordCursor;
import com.king.app.gdb.data.entity.Record;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/7/12 11:22
 */
public class RecordListPresenter extends BasePresenter<IRecordListView> {

    private int DEFAULT_LOAD_MORE = 20;

    private RecordCursor moreCursor;

    private RecordExtendDao recordExtendDao;

    @Override
    public void onCreate() {
        recordExtendDao = new RecordExtendDao();
    }

    public void newRecordCursor() {
        moreCursor = new RecordCursor();
        moreCursor.number = DEFAULT_LOAD_MORE;
    }

    public void updateDefaultLoad(int number) {
        DEFAULT_LOAD_MORE = number;
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
        queryRecords(sortMode, desc, showDeprecated, showCanBePlayed, like, whereScene)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Record>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<Record> list) {
                        view.showRecordList(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.showToastLong("Load records error: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
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
        queryRecords(sortMode, desc, showDeprecated, showCanBePlayed, like, whereScene)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Record>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<Record> list) {
                        view.showMoreRecords(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.showToastLong("Load records error: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Observable<List<Record>> queryRecords(final int sortMode, final boolean desc, final boolean showDeprecated
            , final boolean showCanBePlayed, final String like, final String whereScene) {
        return Observable.create(new ObservableOnSubscribe<List<Record>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Record>> e) throws Exception {
                // 加载可播放的需要从全部记录里通过对比video目录文件信息来挑选
                if (showCanBePlayed) {
                    moreCursor.offset = -1;
                    moreCursor.number = -1;
                }

                String scene = whereScene;
                if (GdbConstants.KEY_SCENE_ALL.equals(whereScene)) {
                    scene = null;
                }
                List<Record> list = recordExtendDao.getRecords(sortMode, desc, showDeprecated, moreCursor, like, scene);
                moreCursor.offset += list.size();

                // 加载可播放的需要从全部记录里通过对比video目录文件信息来挑选
                if (showCanBePlayed) {
                    list = pickCanBePlayedRecord(list);
                }
                e.onNext(list);
            }
        });
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

}
