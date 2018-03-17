package com.jing.app.jjgallery.gdb.view.favor.record;

import com.jing.app.jjgallery.gdb.BaseView;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/14 13:01
 */
public interface RecordOrderView extends BaseView {
    void showOrders(List<FavorRecordOrderEx> list);

    void focusToItem(long focusId);

    void deleteDone(boolean notifyRefresh);

    void warningDeleteOrder(String message, List<FavorRecordOrderEx> list);
}
