package com.jing.app.jjgallery.gdb.view.settings;

import android.content.Context;
import android.support.v4.app.FragmentManager;

import com.jing.app.jjgallery.gdb.BaseView;
import com.jing.app.jjgallery.gdb.model.bean.CheckDownloadBean;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/11/22 15:56
 */
public interface IManageView extends BaseView {
    void onServerConnected();

    void onServerUnavailable();

    void onMoveImagesSuccess();

    void onMoveImagesFail();

    void onCheckPass(CheckDownloadBean bean);

    void onRequestFail();

    Context getContext();

    FragmentManager getFtManager();
}
