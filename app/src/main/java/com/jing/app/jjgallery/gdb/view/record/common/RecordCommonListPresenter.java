package com.jing.app.jjgallery.gdb.view.record.common;

import com.jing.app.jjgallery.gdb.BasePresenter;
import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.model.RecordComparator;
import com.jing.app.jjgallery.gdb.model.conf.PreferenceValue;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.RecordDao;
import com.king.app.gdb.data.entity.RecordStar;
import com.king.app.gdb.data.entity.RecordStarDao;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.entity.StarDao;

import org.greenrobot.greendao.query.QueryBuilder;

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
 * @desc
 * @auth 景阳
 * @time 2018/2/13 0013 14:49
 */

public class RecordCommonListPresenter extends BasePresenter<RecordCommonListView> {

    private Star mStar;

    @Override
    public void onCreate() {

    }

    public Star getStar() {
        return mStar;
    }

    public void loadRecords(final Star star, final int sortMode, final boolean sortDesc) {
        mStar = star;
        sortRecords(star.getRecordList(), sortMode, sortDesc)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Record>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<Record> list) {
                        view.showRecords(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.showToastLong("Load records error: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public Observable<Star> queryStar(final long starId) {
        return Observable.create(new ObservableOnSubscribe<Star>() {
            @Override
            public void subscribe(ObservableEmitter<Star> e) throws Exception {
                StarDao dao = GdbApplication.getInstance().getDaoSession().getStarDao();
                e.onNext(dao.queryBuilder().where(StarDao.Properties.Id.eq(starId)).build().unique());
            }
        });
    }

    public Observable<List<Record>> loadRecords(final long starId) {
        return Observable.create(new ObservableOnSubscribe<List<Record>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Record>> e) throws Exception {
                RecordDao dao = GdbApplication.getInstance().getDaoSession().getRecordDao();
                QueryBuilder<Record> builder = dao.queryBuilder();
                builder.join(RecordStar.class, RecordStarDao.Properties.StarId)
                        .where(RecordStarDao.Properties.StarId.eq(starId));
                e.onNext(builder.build().list());
            }
        });
    }

    public Observable<List<Record>> sortRecords(final List<Record> recordList, final int sortMode, final boolean desc) {
        return Observable.create(new ObservableOnSubscribe<List<Record>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Record>> e) throws Exception {
                if (sortMode != PreferenceValue.GDB_SR_ORDERBY_NONE) {
                    Collections.sort(recordList, new RecordComparator(sortMode, desc));
                }
                e.onNext(recordList);
            }
        });
    }

}
