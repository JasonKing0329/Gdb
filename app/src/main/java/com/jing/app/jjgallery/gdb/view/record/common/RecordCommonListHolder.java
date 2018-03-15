package com.jing.app.jjgallery.gdb.view.record.common;

import android.view.View;

import com.jing.app.jjgallery.gdb.IFragmentHolder;
import com.king.app.gdb.data.entity.Record;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/15 9:22
 */
public interface RecordCommonListHolder extends IFragmentHolder {
    void showRecordPopup(View anchor, Record record);
}
