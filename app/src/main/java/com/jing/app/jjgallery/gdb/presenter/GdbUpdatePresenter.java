package com.jing.app.jjgallery.gdb.presenter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.jing.app.jjgallery.gdb.http.AppHttpClient;
import com.jing.app.jjgallery.gdb.http.Command;
import com.jing.app.jjgallery.gdb.http.bean.response.AppCheckBean;
import com.jing.app.jjgallery.gdb.http.bean.response.GdbRespBean;
import com.jing.app.jjgallery.gdb.model.db.GPropertiesExtendDao;
import com.jing.app.jjgallery.gdb.view.update.IGdbUpdateView;

import java.io.File;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/9/6.
 */
public class GdbUpdatePresenter {

    private IGdbUpdateView updateView;
    private CompositeDisposable compositeDisposable;

    public GdbUpdatePresenter(IGdbUpdateView view) {
        updateView = view;
        compositeDisposable = new CompositeDisposable();
    }

    public void checkGdbDatabase() {
        Disposable disposable = AppHttpClient.getInstance().getAppService().isServerOnline()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<GdbRespBean>() {
                    @Override
                    public void onNext(@NonNull GdbRespBean gdbRespBean) {
                        if (gdbRespBean.isOnline()) {
                            requestGdbDatabase();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        updateView.onServiceDisConnected();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        compositeDisposable.add(disposable);
    }

    public static String getDbVersionName() {
        return new GPropertiesExtendDao().getVersion();
    }

    private void requestGdbDatabase() {
        String versionName = getDbVersionName();
        Disposable disposable = AppHttpClient.getInstance().getAppService().checkGdbDatabaseUpdate(Command.TYPE_GDB_DATABASE, versionName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<AppCheckBean>() {
                    @Override
                    public void onNext(@NonNull AppCheckBean appCheckBean) {
                        if (appCheckBean.isGdbDatabaseUpdate()) {
                            updateView.onGdbDatabaseFound(appCheckBean);
                        }
                        else {
                            updateView.onGdbDatabaseIsLatest();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        updateView.onRequestError();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        compositeDisposable.add(disposable);
    }

    /**
     * 安装应用
     */
    public void installApp(Activity activity, String path) {
        Uri uri = Uri.fromFile(new File(path));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        activity.startActivity(intent);
    }

}
