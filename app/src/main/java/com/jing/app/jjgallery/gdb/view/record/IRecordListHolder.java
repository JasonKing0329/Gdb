package com.jing.app.jjgallery.gdb.view.record;

import android.view.View;

import com.jing.app.jjgallery.gdb.IFragmentHolder;
import com.jing.app.jjgallery.gdb.model.bean.FilterBean;
import com.king.app.gdb.data.entity.Record;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/7/12 10:39
 */
public interface IRecordListHolder extends IFragmentHolder {

    void updateFilter(FilterBean bean);

    void showRecordPopup(View v, Record record);
}
