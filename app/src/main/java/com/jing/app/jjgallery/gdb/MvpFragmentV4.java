package com.jing.app.jjgallery.gdb;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @desc
 * @auth 景阳
 * @time 2018/2/12 0012 20:42
 */

public abstract class MvpFragmentV4<T extends BasePresenter> extends BaseFragmentV4 {

    protected T presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        presenter = createPresenter();
        presenter.onAttach(this);
        View view =  super.onCreateView(inflater, container, savedInstanceState);
        initData();
        return view;
    }

    protected abstract T createPresenter();

    protected abstract void initData();

    @Override
    public void onDestroyView() {
        if (presenter != null) {
            presenter.onDestroy();
        }
        super.onDestroyView();
    }
}
