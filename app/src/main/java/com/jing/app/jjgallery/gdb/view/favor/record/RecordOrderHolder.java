package com.jing.app.jjgallery.gdb.view.favor.record;

import com.jing.app.jjgallery.gdb.IFragmentHolder;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/15 14:51
 */
public interface RecordOrderHolder extends IFragmentHolder {
    void onClickOrder(long id);

    void setDrag(boolean isDrag);

    void cancelDeleteStatus();
}
