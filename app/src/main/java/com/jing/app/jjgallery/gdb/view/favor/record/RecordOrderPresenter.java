package com.jing.app.jjgallery.gdb.view.favor.record;

import android.text.TextUtils;

import com.jing.app.jjgallery.gdb.BasePresenter;
import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.model.GdbImageProvider;
import com.jing.app.jjgallery.gdb.model.PadProperties;
import com.jing.app.jjgallery.gdb.util.ListUtil;
import com.king.app.gdb.data.entity.FavorRecordDao;
import com.king.app.gdb.data.entity.FavorRecordOrder;
import com.king.app.gdb.data.entity.FavorRecordOrderDao;
import com.king.app.gdb.data.entity.Record;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

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
 * <p/>创建时间: 2018/3/14 13:01
 */
public class RecordOrderPresenter extends BasePresenter<RecordOrderView> {

    private final int SORT_NAME = 0;
    private final int SORT_NUMBER = 1;
    private final int SORT_CUSTOM = 2;
    private final int SORT_TIME_CREATE = 3;
    private final int SORT_TIME_MODIFY = 4;

    private int mSortType;

    @Override
    public void onCreate() {
        mSortType = PadProperties.getRecordOrderSortType();
    }

    public void loadOrders() {
        loadOrders(0);
    }

    /**
     * load orders and focus to specify item
     * @param focusId
     */
    public void loadOrders(final long focusId) {
        view.showLoading();
        queryOrders()
                .flatMap(new Function<List<FavorRecordOrder>, ObservableSource<List<FavorRecordOrderEx>>>() {
                    @Override
                    public ObservableSource<List<FavorRecordOrderEx>> apply(List<FavorRecordOrder> list) throws Exception {
                        return parseOrders(list);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<FavorRecordOrderEx>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<FavorRecordOrderEx> list) {
                        view.dismissLoading();
                        view.showOrders(list);
                        if (focusId != 0) {
                            view.focusToItem(focusId);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.dismissLoading();
                        view.showToastLong("Load error: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public Observable<List<FavorRecordOrder>> queryOrders() {
        return Observable.create(new ObservableOnSubscribe<List<FavorRecordOrder>>() {
            @Override
            public void subscribe(ObservableEmitter<List<FavorRecordOrder>> e) throws Exception {
                FavorRecordOrderDao dao = GdbApplication.getInstance().getDaoSession().getFavorRecordOrderDao();
                QueryBuilder<FavorRecordOrder> builder = dao.queryBuilder();
                switch (mSortType) {
                    case SORT_NUMBER:
                        builder.orderDesc(FavorRecordOrderDao.Properties.Number);
                        break;
                    case SORT_TIME_CREATE:
                        builder.orderDesc(FavorRecordOrderDao.Properties.CreateTime);
                        break;
                    case SORT_TIME_MODIFY:
                        builder.orderDesc(FavorRecordOrderDao.Properties.UpdateTime);
                        break;
                    case SORT_CUSTOM:
                        builder.orderAsc(FavorRecordOrderDao.Properties.SortSeq);
                        break;
                    case SORT_NAME:
                    default:
                        builder.orderAsc(FavorRecordOrderDao.Properties.Name);
                        break;
                }
                List<FavorRecordOrder> list = builder.build().list();
                e.onNext(list);
            }
        });
    }

    public static FavorRecordOrderEx parseFromOrder(FavorRecordOrder order) {
        FavorRecordOrderEx orderEx = new FavorRecordOrderEx();
        orderEx.setOrder(order);
        if (TextUtils.isEmpty(order.getCoverUrl())) {
            if (!ListUtil.isEmpty(order.getRecordList())) {
                int index = Math.abs(new Random().nextInt()) % order.getRecordList().size();
                order.setCoverUrl(GdbImageProvider.getRecordRandomPath(order.getRecordList().get(index).getName(), null));
            }
        }
        else {
            orderEx.setCover(order.getCoverUrl());
        }

        if (!ListUtil.isEmpty(order.getRecordList())) {
            List<Record> temp = new ArrayList<>();
            for (int i = 0; i < order.getRecordList().size(); i ++) {
                temp.add(order.getRecordList().get(i));
            }
            List<String> thumbs = new ArrayList<>();
            for (int i = 0; i < 3 && temp.size() > 0; i ++) {
                int index = Math.abs(new Random().nextInt()) % temp.size();
                thumbs.add(GdbImageProvider.getRecordRandomPath(temp.get(index).getName(), null));
                temp.remove(index);
            }
            orderEx.setThumbItems(thumbs);
        }
        return orderEx;
    }

    public Observable<List<FavorRecordOrderEx>> parseOrders(final List<FavorRecordOrder> list) {
        return Observable.create(new ObservableOnSubscribe<List<FavorRecordOrderEx>>() {
            @Override
            public void subscribe(ObservableEmitter<List<FavorRecordOrderEx>> e) throws Exception {
                List<FavorRecordOrderEx> exList = new ArrayList<>();
                for (FavorRecordOrder order:list) {
                    FavorRecordOrderEx orderEx = parseFromOrder(order);
                    exList.add(orderEx);
                }
                e.onNext(exList);
            }
        });
    }

    public void addOrder(String name) {
        view.showLoading();
        addNewOrder(name)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<FavorRecordOrder>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(FavorRecordOrder order) {
                        view.dismissLoading();
                        loadOrders(order.getId());
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.dismissLoading();
                        view.showToastLong("Load error: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public Observable<FavorRecordOrder> addNewOrder(final String name) {
        return Observable.create(new ObservableOnSubscribe<FavorRecordOrder>() {
            @Override
            public void subscribe(ObservableEmitter<FavorRecordOrder> e) throws Exception {
                FavorRecordOrder order = new FavorRecordOrder();
                order.setName(name);
                order.setCreateTime(new Date());
                order.setUpdateTime(order.getCreateTime());

                FavorRecordOrderDao dao = GdbApplication.getInstance().getDaoSession().getFavorRecordOrderDao();
                dao.insert(order);
                e.onNext(order);
            }
        });
    }

    public void saveDragResult(final List<FavorRecordOrderEx> list) {
        view.showLoading();
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                FavorRecordOrderDao dao = GdbApplication.getInstance().getDaoSession().getFavorRecordOrderDao();
                List<FavorRecordOrder> orderList = new ArrayList<>();
                for (int i = 0; i < list.size(); i ++) {
                    list.get(i).getOrder().setSortSeq(i);
                    orderList.add(list.get(i).getOrder());
                }
                dao.updateInTx(orderList);
                e.onNext(new Object());
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Object order) {
                        view.dismissLoading();
                        view.showToastLong("Save successfully");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.dismissLoading();
                        view.showToastLong("Load error: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void sortByName() {
        if (mSortType != SORT_NAME) {
            mSortType = SORT_NAME;
            PadProperties.setRecordOrderSortType(SORT_NAME);
            loadOrders();
        }
    }

    public void sortByNumber() {
        if (mSortType != SORT_NUMBER) {
            mSortType = SORT_NUMBER;
            PadProperties.setRecordOrderSortType(SORT_NUMBER);
            loadOrders();
        }
    }

    public void sortByCreateTime() {
        if (mSortType != SORT_TIME_CREATE) {
            mSortType = SORT_TIME_CREATE;
            PadProperties.setRecordOrderSortType(SORT_TIME_CREATE);
            loadOrders();
        }
    }

    public void sortByModifyTime() {
        if (mSortType != SORT_TIME_MODIFY) {
            mSortType = SORT_TIME_MODIFY;
            PadProperties.setRecordOrderSortType(SORT_TIME_MODIFY);
            loadOrders();
        }
    }

    public void sortByCustom() {
        if (mSortType != SORT_CUSTOM) {
            mSortType = SORT_CUSTOM;
            PadProperties.setRecordOrderSortType(SORT_CUSTOM);
            loadOrders();
        }
    }

    public boolean isDraggable() {
        if (mSortType == SORT_CUSTOM) {
            return true;
        }
        return false;
    }

    public void deleteItem(List<FavorRecordOrderEx> list) {
        if (ListUtil.isEmpty(list)) {
            view.deleteDone(true);
            return;
        }

        if (list.size() == 1) {
            if (list.get(0).getOrder().getNumber() == 0) {
                executeDelete(list);
            }
            else {
                String message = list.get(0).getOrder().getName() + " includes sub items, do you really want to delete it?";
                view.warningDeleteOrder(message, list);
                return;
            }
        }
        else {
            // 有包含子项的order只支持单独删除
            boolean hasSub = false;
            for (FavorRecordOrderEx order:list) {
                if (order.getOrder().getNumber() > 0) {
                    hasSub = true;
                    break;
                }
            }
            if (hasSub) {
                view.showToastLong("Found order included sub items. Please delete it independently.");
                return;
            }

            else {
                executeDelete(list);
            }
        }
    }

    public void executeDelete(final List<FavorRecordOrderEx> list) {

        view.showLoading();

        deleteOrders(list)
                .flatMap(new Function<Object, ObservableSource<List<FavorRecordOrder>>>() {
                    @Override
                    public ObservableSource<List<FavorRecordOrder>> apply(Object o) throws Exception {
                        return queryOrders();
                    }
                })
                .flatMap(new Function<List<FavorRecordOrder>, ObservableSource<List<FavorRecordOrderEx>>>() {
                    @Override
                    public ObservableSource<List<FavorRecordOrderEx>> apply(List<FavorRecordOrder> orders) throws Exception {
                        return parseOrders(orders);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<FavorRecordOrderEx>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<FavorRecordOrderEx> orderExes) {
                        view.dismissLoading();
                        view.deleteDone(false);
                        view.showOrders(orderExes);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.dismissLoading();
                        view.deleteDone(false);
                        view.showToastLong("Delete error: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Observable<Object> deleteOrders(final List<FavorRecordOrderEx> list) {
        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                FavorRecordOrderDao orderDao = GdbApplication.getInstance().getDaoSession().getFavorRecordOrderDao();
                FavorRecordDao dao = GdbApplication.getInstance().getDaoSession().getFavorRecordDao();
                for (FavorRecordOrderEx orderEx:list) {
                    orderDao.delete(orderEx.getOrder());
                    dao.queryBuilder()
                            .where(FavorRecordDao.Properties.OrderId.eq(orderEx.getOrder().getId()))
                            .buildDelete()
                            .executeDeleteWithoutDetachingEntities();
                }
                orderDao.detachAll();
                dao.detachAll();
                e.onNext(new Object());
            }
        });
    }
}
