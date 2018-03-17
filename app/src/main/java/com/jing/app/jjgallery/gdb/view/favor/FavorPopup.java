package com.jing.app.jjgallery.gdb.view.favor;

import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.jing.app.jjgallery.gdb.BaseView;
import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.R;
import com.king.app.gdb.data.entity.FavorRecord;
import com.king.app.gdb.data.entity.FavorRecordDao;
import com.king.app.gdb.data.entity.FavorRecordOrder;
import com.king.app.gdb.data.entity.FavorRecordOrderDao;

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
 * <p/>创建时间: 2018/3/14 17:04
 */
public class FavorPopup {

    private FavorView view;

    private long mOrderId;

    private Observable actionObservable;

    public interface FavorView extends BaseView {
        void requestSelectOrder();

        void showDeleteWarning(String filePath);

        void showRecordOrders(long recordId);
    }

    public FavorPopup(FavorView view) {
        this.view = view;
    }

    public void popupRecord(Context context, View anchor, final long recordId) {
        PopupMenu menu = new PopupMenu(context, anchor);
        menu.getMenuInflater().inflate(R.menu.favor_add_record, menu.getMenu());
        menu.show();
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_add_to_order:
                        actionObservable = addToOrder(recordId);
                        view.requestSelectOrder();
                        break;
                    case R.id.menu_view_order:
                        actionObservable = addToOrder(recordId);
                        view.showRecordOrders(recordId);
                        break;
                }
                return true;
            }
        });
    }

    public Observable<FavorRecord> addToOrder(final long recordId) {
        return Observable.create(new ObservableOnSubscribe<FavorRecord>() {
            @Override
            public void subscribe(ObservableEmitter<FavorRecord> e) throws Exception {
                FavorRecordDao dao = GdbApplication.getInstance().getDaoSession().getFavorRecordDao();
                // check if already exist
                long relationCount = dao.queryBuilder()
                        .where(FavorRecordDao.Properties.OrderId.eq(mOrderId)
                            , FavorRecordDao.Properties.RecordId.eq(recordId))
                        .buildCount().count();
                if (relationCount != 0) {
                    e.onError(new Exception("Record is already in target order"));
                    e.onComplete();
                    return;
                }

                // insert record
                FavorRecord record = new FavorRecord();
                record.setOrderId(mOrderId);
                record.setRecordId(recordId);
                dao.insert(record);
                long count = dao.queryBuilder()
                        .where(FavorRecordDao.Properties.OrderId.eq(mOrderId))
                        .buildCount().count();

                // update item numbers of order
                FavorRecordOrderDao orderDao = GdbApplication.getInstance().getDaoSession().getFavorRecordOrderDao();
                FavorRecordOrder order = orderDao.queryBuilder()
                        .where(FavorRecordOrderDao.Properties.Id.eq(mOrderId))
                        .build().unique();
                order.setNumber((int) count);
                orderDao.update(order);

                // 添加后一定要执行清除缓存，否则很容易出现下次查询从缓存里取出了旧值
                orderDao.detachAll();
                e.onNext(record);
            }
        });
    }

    public void popupImage(Context context, View anchor, final String filePath) {
        PopupMenu menu = new PopupMenu(context, anchor);
        menu.getMenuInflater().inflate(R.menu.popup_image, menu.getMenu());
        menu.show();
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_delete:
                        view.showDeleteWarning(filePath);
                        break;
                    case R.id.menu_set_as_cover:
                        actionObservable = setAsCover(filePath);
                        view.requestSelectOrder();
                        break;
                }
                return true;
            }
        });
    }

    public Observable<FavorRecordOrder> setAsCover(final String filePath) {
        return Observable.create(new ObservableOnSubscribe<FavorRecordOrder>() {
            @Override
            public void subscribe(ObservableEmitter<FavorRecordOrder> e) throws Exception {
                FavorRecordOrderDao dao = GdbApplication.getInstance().getDaoSession().getFavorRecordOrderDao();
                FavorRecordOrder order = dao.queryBuilder()
                        .where(FavorRecordOrderDao.Properties.Id.eq(mOrderId))
                        .build().unique();
                order.setCoverUrl(filePath);

                dao.detachAll();
                dao.update(order);
                e.onNext(order);
            }
        });
    }

    public void onSelectOrder(long orderId) {
        mOrderId = orderId;
        actionObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object object) {
                        view.showToastLong("Save successfully");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.showToastLong("Save failed:" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}
