package com.jing.app.jjgallery.gdb.view.surf;

import com.jing.app.jjgallery.gdb.IFragmentHolder;
import com.jing.app.jjgallery.gdb.http.bean.data.FileBean;
import com.jing.app.jjgallery.gdb.presenter.surf.SurfHttpPresenter;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/7/31 13:46
 */
public interface ISurfHttpHolder extends IFragmentHolder {

    SurfHttpPresenter getPresenter();

    void startProgress();

    void endProgress();

    void onClickSurfFolder(FileBean fileBean);
}
