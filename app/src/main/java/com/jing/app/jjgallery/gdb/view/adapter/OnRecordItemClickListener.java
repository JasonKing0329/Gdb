package com.jing.app.jjgallery.gdb.view.adapter;

import android.view.View;

import com.king.app.gdb.data.entity.Record;

/**
 * @desc
 * @auth 景阳
 * @time 2018/2/15 0015 13:42
 */

public interface OnRecordItemClickListener {
    void onClickRecordItem(View v, Record record);
    void onPopupMenu(View v, Record record);
}
