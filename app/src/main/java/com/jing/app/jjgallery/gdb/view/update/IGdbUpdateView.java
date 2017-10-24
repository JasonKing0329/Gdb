package com.jing.app.jjgallery.gdb.view.update;

import com.jing.app.jjgallery.gdb.http.bean.response.AppCheckBean;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/10/24 17:57
 */
public interface IGdbUpdateView {
    void onGdbDatabaseFound(AppCheckBean bean);
    void onGdbDatabaseIsLatest();
    void onServiceDisConnected();
    void onRequestError();
}
