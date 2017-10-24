package com.jing.app.jjgallery.gdb.presenter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import com.jing.app.jjgallery.gdb.BuildConfig;
import com.jing.app.jjgallery.gdb.http.AppHttpClient;
import com.jing.app.jjgallery.gdb.http.Command;
import com.jing.app.jjgallery.gdb.http.bean.response.AppCheckBean;
import com.jing.app.jjgallery.gdb.http.bean.response.GdbRespBean;
import com.jing.app.jjgallery.gdb.model.conf.Configuration;
import com.jing.app.jjgallery.gdb.view.update.IUpdateView;

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
public class UpdatePresenter {

    private IUpdateView updateView;
    private CompositeDisposable compositeDisposable;

    public UpdatePresenter(IUpdateView view) {
        updateView = view;
        compositeDisposable = new CompositeDisposable();
    }

    public static String getAppVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void checkAppUpdate(Context context) {
        final String versionName = getAppVersionName(context);
        Disposable disposable = AppHttpClient.getInstance().getAppService().isServerOnline()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<GdbRespBean>() {
                    @Override
                    public void onNext(@NonNull GdbRespBean gdbRespBean) {
                        if (gdbRespBean.isOnline()) {
                            requestCheckAppUpdate(versionName);
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

    private void requestCheckAppUpdate(String versionName) {
        Disposable disposable = AppHttpClient.getInstance().getAppService().checkAppUpdate(Command.TYPE_APP, versionName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<AppCheckBean>() {
                    @Override
                    public void onNext(@NonNull AppCheckBean appCheckBean) {
                        if (appCheckBean.isAppUpdate()) {
                            updateView.onAppUpdateFound(appCheckBean);
                        }
                        else {
                            updateView.onAppIsLatest();
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
    public void installApp(Context context, String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", new File(path));
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    public void clearAppFolder() {
        File file = new File(Configuration.APP_DIR_CONF_APP);
        File files[] = file.listFiles();
        for (File f:files) {
            f.delete();
        }
    }
}
