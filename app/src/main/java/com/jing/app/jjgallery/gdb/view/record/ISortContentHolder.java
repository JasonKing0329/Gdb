package com.jing.app.jjgallery.gdb.view.record;

import com.jing.app.jjgallery.gdb.IFragmentHolder;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/10/26 16:02
 */
public interface ISortContentHolder extends IFragmentHolder {
    int getSortMode();
    boolean isDesc();
    SortDialogFragment.OnSortListener getOnSortListener();
}
