package com.jing.app.jjgallery.gdb.view.update;

import com.jing.app.jjgallery.gdb.http.bean.response.AppCheckBean;

/**
 * Created by Administrator on 2016/9/6.
 */
public interface IUpdateView {
    void onAppUpdateFound(AppCheckBean bean);
    void onAppIsLatest();
    void onServiceDisConnected();
    void onRequestError();
}
