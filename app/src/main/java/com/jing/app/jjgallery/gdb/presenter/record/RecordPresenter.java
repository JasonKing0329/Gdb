package com.jing.app.jjgallery.gdb.presenter.record;

import android.text.TextUtils;

import com.jing.app.jjgallery.gdb.BasePresenter;
import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.model.GdbImageProvider;
import com.jing.app.jjgallery.gdb.model.conf.Configuration;
import com.jing.app.jjgallery.gdb.model.palette.PaletteResponse;
import com.jing.app.jjgallery.gdb.view.record.pad.TitleValueBean;
import com.jing.app.jjgallery.gdb.view.record.phone.IRecordView;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.RecordDao;
import com.king.app.gdb.data.entity.RecordStar;
import com.king.app.gdb.data.entity.RecordType1v1;
import com.king.app.gdb.data.entity.RecordType3w;
import com.king.app.gdb.data.param.DataConstants;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/7/13 10:40
 */
public class RecordPresenter extends BasePresenter<IRecordView> {

    private Record mRecord;

    private List<String> mImageList;

    private Map<Integer, PaletteResponse> paletteMap;

    @Override
    public void onCreate() {
        paletteMap = new HashMap<>();
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
        }).flatMap(new Function<Record, ObservableSource<Record>>() {
            @Override
            public ObservableSource<Record> apply(final Record record) throws Exception {
                return new ObservableSource<Record>() {
                    @Override
                    public void subscribe(Observer<? super Record> observer) {
//                        mImageList = GdbImageProvider.getRecordPathList(record.getName());
                        mImageList = GdbImageProvider.getRecordPathList("123");
                        Collections.shuffle(mImageList);
                        observer.onNext(record);
                    }
                };
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
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

    public void deleteFile(String filePath) {
        new File(filePath).delete();
    }

    public List<TitleValueBean> getScoreDetails() {
        List<TitleValueBean> list = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        newValue(list, sdf.format(new Date(mRecord.getLastModifyTime())));
        newTitleValue(list, "HD level", mRecord.getHdLevel());
        newTitleValue(list, "Feel", mRecord.getScoreFeel());
        newTitleValue(list, "Stars", mRecord.getScoreStar());
        newTitleValue(list, "Passion", mRecord.getScorePassion());
        newTitleValue(list, "Cum", mRecord.getScoreCum());
        newTitleValue(list, "Special", mRecord.getScoreSpecial());
        newValue(list, mRecord.getSpecialDesc());
        if (mRecord.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
            RecordType1v1 record = mRecord.getRecordType1v1();
            newTitleValue(list, "BJob", record.getScoreBjob());
            newTitleValue(list, "Scene", record.getScoreScene());
            newTitleValue(list, "CShow", record.getScoreCshow());
            newTitleValue(list, "Rhythm", record.getScoreRhythm());
            newTitleValue(list, "Story", record.getScoreStory());
            newTitleValue(list, "Rim", record.getScoreRim());
            newTitleValue(list, "Foreplay", record.getScoreForePlay());
        } else if (mRecord.getType() == DataConstants.VALUE_RECORD_TYPE_3W
                || mRecord.getType() == DataConstants.VALUE_RECORD_TYPE_MULTI) {
            RecordType3w record = mRecord.getRecordType3w();
            newTitleValue(list, "BJob", record.getScoreBjob());
            newTitleValue(list, "Scene", record.getScoreScene());
            newTitleValue(list, "CShow", record.getScoreCshow());
            newTitleValue(list, "Rhythm", record.getScoreRhythm());
            newTitleValue(list, "Story", record.getScoreStory());
            newTitleValue(list, "Rim", record.getScoreRim());
            newTitleValue(list, "Foreplay", record.getScoreForePlay());
        }
        return list;
    }

    private void newValue(List<TitleValueBean> list, String value) {
        if (!TextUtils.isEmpty(value)) {
            TitleValueBean bean = new TitleValueBean();
            bean.setValue(value);
            list.add(bean);
        }
    }

    private void newTitleValue(List<TitleValueBean> list, String title, int value) {
        if (value > 0) {
            TitleValueBean bean = new TitleValueBean();
            bean.setTitle(title);
            bean.setValue(String.valueOf(value));
            list.add(bean);
        }
    }

    public void refreshBackground(int position) {
        view.loadBackground(paletteMap.get(position));
    }

    public List<String> getImageList() {
        return mImageList;
    }

    public void cachePaletteResponse(int position, PaletteResponse response) {
        paletteMap.put(position, response);
    }

    public void removePaletteCache(int position) {
        paletteMap.remove(position);
    }
}
