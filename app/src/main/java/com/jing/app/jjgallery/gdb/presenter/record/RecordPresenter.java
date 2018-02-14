package com.jing.app.jjgallery.gdb.presenter.record;

import com.jing.app.jjgallery.gdb.BasePresenter;
import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.model.conf.Configuration;
import com.jing.app.jjgallery.gdb.view.record.phone.IRecordView;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.RecordDao;
import com.king.app.gdb.data.entity.RecordStar;
import com.king.app.gdb.data.param.DataConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
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
 * <p/>创建时间: 2017/7/13 10:40
 */
public class RecordPresenter extends BasePresenter<IRecordView> {

    private Record mRecord;

    @Override
    public void onCreate() {

    }

    public void loadRecord(final long recordId) {
        Observable.create(new ObservableOnSubscribe<Record>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Record> e) throws Exception {
                RecordDao dao = GdbApplication.getInstance().getDaoSession().getRecordDao();
                // load star object and star's records
                Record record = dao.queryBuilder()
                        .where(RecordDao.Properties.Id.eq(recordId))
                        .build().unique();
                e.onNext(record);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Record>() {
                    @Override
                    public void accept(Record record) throws Exception {
                        mRecord = record;
                        view.showRecord(record);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    public RecordStar getRelationTop() {
        for (RecordStar relation:mRecord.getRelationList()) {
            if (relation.getType() == DataConstants.VALUE_RELATION_TOP) {
                return relation;
            }
        }
        return null;
    }

    public RecordStar getRelationBottom() {
        for (RecordStar relation:mRecord.getRelationList()) {
            if (relation.getType() == DataConstants.VALUE_RELATION_BOTTOM) {
                return relation;
            }
        }
        return null;
    }

    public String getRelationFlag(RecordStar relation) {
        if (relation.getType() == DataConstants.VALUE_RELATION_BOTTOM) {
            return DataConstants.STAR_3W_FLAG_BOTTOM;
        }
        else if (relation.getType() == DataConstants.VALUE_RELATION_TOP) {
            return DataConstants.STAR_3W_FLAG_TOP;
        }
        else {
            return DataConstants.STAR_3W_FLAG_MIX;
        }
    }

    public RecordStar getRelation(int index) {
        try {
            return mRecord.getRelationList().get(index);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Record getRecord() {
        return mRecord;
    }

    public List<String> getRecordImages() {
        List<String> list = new ArrayList<>();
        // record目录
        File file = new File(Configuration.GDB_IMG_RECORD + "/" + mRecord.getName() + ".png");
        if (file.exists()) {
            list.add(file.getPath());
        }
        // record/name目录
        file = new File(Configuration.GDB_IMG_RECORD + "/" + mRecord.getName());
        if (file.exists()) {
            File files[] = file.listFiles();
            for (File f:files) {
                if (!f.isDirectory() && !f.getName().equals(".nomedia")) {
                    list.add(f.getPath());
                }
            }
        }
        // shuffle
        Collections.shuffle(list);

        // cu 优先
        file = new File(Configuration.GDB_IMG_RECORD + "/" + mRecord.getName() + "/cu");
        if (file.exists()) {
            File files[] = file.listFiles();
            for (File f:files) {
                if (!f.isDirectory() && !f.getName().equals(".nomedia")) {
                    list.add(0, f.getPath());
                }
            }
        }
        return list;
    }
}
