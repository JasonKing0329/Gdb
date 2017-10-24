package com.jing.app.jjgallery.gdb.model.login;

/**
 * Created by Administrator on 2016/6/23 0023.
 */
public interface LoginCallback {
    void onLoginSuccess();
    void onLoginFailed(int type, String msg);
}
