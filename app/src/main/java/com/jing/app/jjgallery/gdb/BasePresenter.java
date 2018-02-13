package com.jing.app.jjgallery.gdb;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * @desc
 * @auth 景阳
 * @time 2018/2/12 0012 20:34
 */

public abstract class BasePresenter<T extends BaseView> {

    protected T view;

    private CompositeDisposable compositeDisposable;

    public BasePresenter() {
        compositeDisposable = new CompositeDisposable();
        onCreate();
    }

    public void onAttach(T view) {
        this.view = view;
    }

    public abstract void onCreate();

    public void onDestroy() {
        compositeDisposable.clear();
    }

    protected void addDisposable(Disposable disposable) {
        compositeDisposable.add(disposable);
    }
}
