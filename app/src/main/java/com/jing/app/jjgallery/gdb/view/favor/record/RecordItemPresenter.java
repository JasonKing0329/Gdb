package com.jing.app.jjgallery.gdb.view.favor.record;

import com.jing.app.jjgallery.gdb.BasePresenter;
import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.model.PadProperties;
import com.jing.app.jjgallery.gdb.model.RecordComparator;
import com.jing.app.jjgallery.gdb.util.ListUtil;
import com.king.app.gdb.data.entity.FavorRecordDao;
import com.king.app.gdb.data.entity.FavorRecordOrder;
import com.king.app.gdb.data.entity.FavorRecordOrderDao;
import com.king.app.gdb.data.entity.Record;

import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/15 15:44
 */
public class RecordItemPresenter extends BasePresenter<RecordItemView> {

    private FavorRecordOrder mOrder;

    private int mSortMode;
    private boolean mSortDesc;

    @Override
    public void onCreate() {

    }

    public void loadOrder(long orderId, int sortMode, boolean sortDesc) {
        mSortMode = sortMode;
        mSortDesc = sortDesc;
        view.showLoading();
        queryOrderRecords(orderId)
                .flatMap(new Function<List<Record>, ObservableSource<List<Record>>>() {
                    @Override
                    public ObservableSource<List<Record>> apply(List<Record> records) throws Exception {
                        return sortRecords(records);
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
                    public void onNext(List<Record> records) {
                        PadProperties.setRecordOrderItemSortType(mSortMode);
                        PadProperties.setRecordOrderItemSortDesc(mSortDesc);
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
                List<Record> list = mOrder.getRecordList();
                e.onNext(list);
            }
        });
    }

    private ObservableSource<List<Record>> sortRecords(final List<Record> records) {
        return Observable.create(new ObservableOnSubscribe<List<Record>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Record>> e) throws Exception {
                Collections.sort(records, new RecordComparator(mSortMode, mSortDesc));
                e.onNext(records);
            }
        });
    }

    public FavorRecordOrder getOrder() {
        return mOrder;
    }

    public void deleteItem(List<Record> selectedItems) {
        if (ListUtil.isEmpty(selectedItems)) {
            view.deleteDone(true);
            return;
        }

        view.showLoading();
        // delete, refresh, sort
        deleteOrderItems(selectedItems)
                .flatMap(new Function<Object, ObservableSource<List<Record>>>() {
                    @Override
                    public ObservableSource<List<Record>> apply(Object o) throws Exception {
                        return new Observable<List<Record>>() {
                            @Override
                            protected void subscribeActual(Observer<? super List<Record>> observer) {
                                GdbApplication.getInstance().getDaoSession().getFavorRecordOrderDao().refresh(mOrder);
                                // mOrder已经在内存中查过了，所以如果直接query甚至refresh，里面的recordList还是从内存中取了旧值，需要reset
                                mOrder.resetRecordList();
                                observer.onNext(mOrder.getRecordList());
                            }
                        };
                    }
                })
                .flatMap(new Function<List<Record>, ObservableSource<List<Record>>>() {
                    @Override
                    public ObservableSource<List<Record>> apply(List<Record> records) throws Exception {
                        return sortRecords(records);
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
                    public void onNext(List<Record> records) {
                        view.dismissLoading();
                        view.deleteDone(false);
                        view.showOrderItems(records);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.dismissLoading();
                        view.showToastLong("Delete item failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Observable<Object> deleteOrderItems(final List<Record> list) {
        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                FavorRecordDao dao = GdbApplication.getInstance().getDaoSession().getFavorRecordDao();
                // 删除关联关系
                for (Record record:list) {
                    dao.queryBuilder()
                            .where(FavorRecordDao.Properties.OrderId.eq(mOrder.getId())
                                , FavorRecordDao.Properties.RecordId.eq(record.getId()))
                            .buildDelete()
                            .executeDeleteWithoutDetachingEntities();
                }
                dao.detachAll();

                // 更新数量
                long count = dao.queryBuilder()
                        .where(FavorRecordDao.Properties.OrderId.eq(mOrder.getId()))
                        .buildCount().count();
                mOrder.setNumber((int) count);
                FavorRecordOrderDao orderDao = GdbApplication.getInstance().getDaoSession().getFavorRecordOrderDao();
                orderDao.update(mOrder);

                orderDao.detachAll();
                e.onNext(new Object());
            }
        });
    }

}
