package com.jing.app.jjgallery.gdb.view.login;

/**
 * Created by Administrator on 2016/6/23 0023.
 */
public interface ILoginView {
    void onSignSuccess();
    void onSignFailed(int type, String msg);
}
