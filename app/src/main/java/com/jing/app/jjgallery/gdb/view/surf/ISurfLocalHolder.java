package com.jing.app.jjgallery.gdb.view.surf;

import com.jing.app.jjgallery.gdb.IFragmentHolder;
import com.jing.app.jjgallery.gdb.http.bean.data.FileBean;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/7/31 13:46
 */
public interface ISurfLocalHolder extends IFragmentHolder {

    void startProgress();

    void endProgress();

    int getSortMode();

    boolean isSortDesc();

    void onClickSurfFolder(FileBean fileBean);
}
