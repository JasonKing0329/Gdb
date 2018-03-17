package com.jing.app.jjgallery.gdb.view.favor.record;

import com.jing.app.jjgallery.gdb.BaseView;
import com.king.app.gdb.data.entity.Record;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/15 15:44
 */
public interface RecordItemView extends BaseView {
    void showOrderItems(List<Record> records);

    void deleteDone(boolean notifyRefresh);
}
