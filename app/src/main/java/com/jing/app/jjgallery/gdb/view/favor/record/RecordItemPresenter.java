package com.jing.app.jjgallery.gdb.view.favor.record;

import com.jing.app.jjgallery.gdb.BasePresenter;
import com.jing.app.jjgallery.gdb.GdbApplication;
import com.king.app.gdb.data.entity.FavorRecordOrder;
import com.king.app.gdb.data.entity.FavorRecordOrderDao;
import com.king.app.gdb.data.entity.Record;

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
 * <p/>创建时间: 2018/3/15 15:44
 */
public class RecordItemPresenter extends BasePresenter<RecordItemView> {

    private FavorRecordOrder mOrder;

    @Override
    public void onCreate() {

    }

    public void loadOrder(long orderId) {
        view.showLoading();
        queryOrderRecords(orderId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Record>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<Record> records) {
                        view.dismissLoading();
                        view.showOrderItems(records);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.dismissLoading();
                        view.showToastLong("Query order failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Observable<List<Record>> queryOrderRecords(final long orderId) {
        return Observable.create(new ObservableOnSubscribe<List<Record>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Record>> e) throws Exception {
                FavorRecordOrderDao dao = GdbApplication.getInstance().getDaoSession().getFavorRecordOrderDao();
                mOrder = dao.queryBuilder()
                        .where(FavorRecordOrderDao.Properties.Id.eq(orderId))
                        .build().unique();
                e.onNext(mOrder.getRecordList());
            }
        });
    }

    public FavorRecordOrder getOrder() {
        return mOrder;
    }
}
