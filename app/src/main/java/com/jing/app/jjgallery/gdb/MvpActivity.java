package com.jing.app.jjgallery.gdb;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * @desc
 * @auth 景阳
 * @time 2018/2/12 0012 20:33
 */

public abstract class MvpActivity<T extends BasePresenter> extends GBaseActivity {

    protected T presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initController() {
        presenter = createPresenter();
        presenter.onAttach(this);
    }

    protected abstract T createPresenter();

    @Override
    protected void initBackgroundWork() {
        initData();
    }

    protected abstract void initData();

    @Override
    protected void onDestroy() {
        if (presenter != null) {
            presenter.onDestroy();
        }
        super.onDestroy();
    }
}
