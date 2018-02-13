package com.jing.app.jjgallery.gdb.view.record.common;

import com.jing.app.jjgallery.gdb.BaseView;
import com.king.app.gdb.data.entity.Record;

import java.util.List;

/**
 * @desc
 * @auth 景阳
 * @time 2018/2/13 0013 14:49
 */

public interface RecordCommonListView extends BaseView {
    void showRecords(List<Record> list);
}
