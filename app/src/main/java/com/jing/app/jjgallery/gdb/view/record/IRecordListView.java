package com.jing.app.jjgallery.gdb.view.record;

import com.jing.app.jjgallery.gdb.BaseView;
import com.king.app.gdb.data.entity.Record;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/7/12 11:23
 */
public interface IRecordListView extends BaseView {
    void showRecordList(List<Record> list);
    void showMoreRecords(List<Record> list);
}
