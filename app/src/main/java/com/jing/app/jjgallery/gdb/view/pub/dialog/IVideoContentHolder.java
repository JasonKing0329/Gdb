package com.jing.app.jjgallery.gdb.view.pub.dialog;

import com.jing.app.jjgallery.gdb.IFragmentHolder;
import com.king.app.gdb.data.entity.Record;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/7/20 15:43
 */
public interface IVideoContentHolder extends IFragmentHolder {
    String getVideoPath();
    Record getRecord();
    void setDialogWidth(int width);
}
