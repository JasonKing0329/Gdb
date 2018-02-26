package com.jing.app.jjgallery.gdb.view.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.jing.app.jjgallery.gdb.ActivityManager;
import com.jing.app.jjgallery.gdb.BaseView;
import com.jing.app.jjgallery.gdb.GBaseActivity;
import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.http.BaseUrl;
import com.jing.app.jjgallery.gdb.model.SettingProperties;
import com.jing.app.jjgallery.gdb.model.conf.ConfManager;
import com.jing.app.jjgallery.gdb.model.conf.Configuration;
import com.jing.app.jjgallery.gdb.model.login.LoginParams;
import com.jing.app.jjgallery.gdb.presenter.LoginPresenter;
import com.jing.app.jjgallery.gdb.util.DBExportor;
import com.jing.app.jjgallery.gdb.util.DebugLog;
import com.jing.app.jjgallery.gdb.util.DisplayHelper;
import com.jing.app.jjgallery.gdb.util.PermissionUtil;
import com.jing.app.jjgallery.gdb.view.pub.ProgressButton;
import com.jing.app.jjgallery.gdb.view.pub.dialog.DefaultDialogManager;
import com.jing.app.jjgallery.gdb.view.settings.SettingsActivity;
import com.jing.app.jjgallery.gdb.view.update.UpdateListener;
import com.jing.app.jjgallery.gdb.view.update.UpdateManager;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class LoginActivity extends GBaseActivity implements ILoginView {

    @BindView(R.id.actv_user)
    AutoCompleteTextView actvUser;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.group_input)
    LinearLayout groupInput;
    @BindView(R.id.scroll_form)
    ScrollView scrollForm;
    @BindView(R.id.progressButton)
    ProgressButton progressButton;
    @BindView(R.id.group_init)
    LinearLayout groupInit;

    private LoginPresenter loginPresenter;

    private UpdateManager updateManager;

    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void initController() {
        loginPresenter = new LoginPresenter(this, this);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initBackgroundWork() {
        // android6.0及以上需要动态分配权限
        // 先获取读写以及存储权限，不然没法执行init里面的一些初始化操作
        if (GdbApplication.isM()) {
            if (PermissionUtil.isStoragePermitted(this)) {
                showInitProgress();
                // 执行初始化操作
                startInit();
            }
            else {
                PermissionUtil.requestStoragePermission(this, 1);
                PermissionUtil.requestOtherPermission(this);
            }
        }
        else {
            showInitProgress();
            // 执行初始化操作
            startInit();
        }
    }

    private void showInitProgress() {
        groupInit.setVisibility(View.VISIBLE);
        progressButton.startAnimating();
    }

    private void hideInitProgress() {
        groupInit.setVisibility(View.GONE);
        progressButton.stopAnimating();
    }

    private void startInit() {
        DBExportor.execute();
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                // 文件目录
                Configuration.init();
                // 数据库
//                if (!DBInfor.prepareDatabase(LoginActivity.this)) {
//                    showToastLong(getString(R.string.error_database_create_fail), ProgressProvider.TOAST_ERROR);
//                }
                // 已有数据库升级
//                Configuration.initVersionChange();
                // 初始化参数
//                Configuration.initParams(LoginActivity.this);
                // 拷贝assets里的resource xml以及database
                ConfManager.initParams(LoginActivity.this);
                // 加载扩展resource资源
//                JResource.initializeColors();
                // create dao
                GdbApplication.getInstance().createGreenDao();
                // 检查扩展配置
                boolean hasPref = ConfManager.checkExtendConf();

                e.onNext(hasPref);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean hasPref) throws Exception {
                        ActivityManager.startHomePadActivity(LoginActivity.this);
                        if (hasPref) {
                            new DefaultDialogManager().showWarningActionDialog(LoginActivity.this
                                    , getResources().getString(R.string.login_extend_pref_exist)
                                    , getResources().getString(R.string.yes)
                                    , null
                                    , getResources().getString(R.string.no)
                                    , new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                                loadExtendPref();
                                            }
                                            else {
                                                afterPrefCheck();
                                                hideInitProgress();
                                            }
                                        }
                                    });
                        }
                        else {
                            afterPrefCheck();
                            hideInitProgress();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    private void loadExtendPref() {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {

                ConfManager.replaceExtendPref();
                e.onNext(true);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean hasPref) throws Exception {

                        afterPrefCheck();
                        hideInitProgress();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    private void afterPrefCheck() {
        // 设置base url
        BaseUrl.getInstance().setBaseUrl(SettingProperties.getGdbServerBaseUrl(this));

//        applyExtendColors();
        // Open SettingActivity when application is started for the first time.
        // Application will be considered as initialized only after sign in successfully.
        if (TextUtils.isEmpty(SettingProperties.getGdbServerBaseUrl(this))) {
            showToastLong(getString(R.string.server_not_conf), BaseView.TOAST_WARNING);
            startSetting();
        }
        else {
            showPage();
        }
    }

    private void startSetting() {
        Intent intent = new Intent().setClass(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void showPage() {
        if (SettingProperties.isFingerPrintEnable() && loginPresenter.isFingerPrintEnabled()) {
            loginPresenter.signFingerPrint();
        }
        else {
            showLoginForm();
        }

        updateManager = new UpdateManager(this);
        updateManager.setFragmentManagerV4(getSupportFragmentManager());
        updateManager.setUpdateListener(new UpdateListener() {
            @Override
            public void onUpdateDialogShow() {
                loginPresenter.cancelFingerCheck();
            }

            @Override
            public void onUpdateDialogDismiss() {
                // 取消下载才重新check
                if (!updateManager.isUpdating()) {
                    if (SettingProperties.isFingerPrintEnable() && loginPresenter.isFingerPrintEnabled()) {
                        loginPresenter.signFingerPrint();
                    }
                    else {
                        showLoginForm();
                    }
                }
            }
        });
        updateManager.startCheck();
    }

    private void showLoginForm() {
        groupInput.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSignSuccess() {
        // Application will be considered as initialized only after sign in successfully.
        if (!SettingProperties.isAppInited()) {
            SettingProperties.setAppInited();
        }

        superUser();
    }

    @Override
    public void onSignFailed(int type, String msg) {
        switch (type) {
            case LoginParams.TYPE_ERROR_WRONG_PWD:
                showToastLong(msg, BaseView.TOAST_ERROR);
                break;
            case LoginParams.TYPE_ERROR_CANCEL_FINGERPRINT:
                if (!updateManager.isShowing()) {
                    finish();
                }
                break;
            case LoginParams.TYPE_ERROR_WRONG_FINGERPRINT:
                break;
            case LoginParams.TYPE_ERROR_UNREGIST_FINGERPRINT:
                showLoginForm();
                showToastLong(msg, BaseView.TOAST_ERROR);
                break;
        }
    }

    protected void superUser() {
//		startService(new Intent().setClass(this, FileDBService.class));
        new DefaultDialogManager().showWarningActionDialog(this
                , getResources().getString(R.string.login_start_service_insert)
                , getResources().getString(R.string.yes)
                , getResources().getString(R.string.no)
                , getResources().getString(R.string.action_settings)
                , new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            ActivityManager.startManageActivity(LoginActivity.this);
                            finish();
                        }
                        else if (which == DialogInterface.BUTTON_NEGATIVE) {
                            startSetting();
                        }
                        else {//netrual, all no
                            onServiceDone();
                        }
                    }
                });
    }

    public void onServiceDone() {
//        showProgress("compressing");
//        compressFiles();
//        new HomeSelecter(this).startDefaultHome(this, null);
        if (DisplayHelper.isTabModel(this)) {
            ActivityManager.startHomePadActivity(this);
        }
        else {
            ActivityManager.startHomeActivity(this);
        }
        finish();
    }

    private void compressFiles() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> e) throws Exception {
                File tempFolder = new File(Configuration.APP_DIR_IMG + "_temp_");
                if (!tempFolder.exists()) {
                    tempFolder.mkdir();
                }
                File file = new File(Configuration.GDB_IMG_STAR);
                traverseCompressFile(file, tempFolder);
                file = new File(Configuration.GDB_IMG_RECORD);
                traverseCompressFile(file, tempFolder);
                tempFolder.delete();
                e.onNext(null);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        dismissProgress();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        dismissProgress();
                        throwable.printStackTrace();
                    }
                });
    }

    private void traverseCompressFile(File file, File tempFolder) {
        if (file.isDirectory()) {
            File files[] = file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return !pathname.getName().endsWith(".nomedia");
                }
            });
            for (File f:files) {
                traverseCompressFile(f, tempFolder);
            }
        }
        else {
            // 200K以下的不再压缩
            if (file.length() < 204800) {
                return;
            }
            try {
                DebugLog.e("compress " + file.getPath());
                File target = new Compressor(GdbApplication.getInstance())
                        .setMaxWidth(1080)
                        .setMaxHeight(607)
                        .setQuality(75)
                        .setCompressFormat(Bitmap.CompressFormat.WEBP)
                        .setDestinationDirectoryPath(tempFolder.getPath())
                        .compressToFile(file);

                file.delete();
                target.renameTo(file);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    @OnClick(R.id.btn_login)
    public void onViewClicked() {
        loginPresenter.sign(actvUser.getText().toString(), etPwd.getText().toString());

    }
}
