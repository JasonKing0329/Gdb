package com.jing.app.jjgallery.gdb.view.update;

import com.jing.app.jjgallery.gdb.http.bean.response.AppCheckBean;

/**
 * Created by Administrator on 2016/9/6.
 */
public interface GdbUpdateListener {
    void onUpdateFinish();
    void onUpdateCancel();

    boolean consumeYes(AppCheckBean bean);
}
