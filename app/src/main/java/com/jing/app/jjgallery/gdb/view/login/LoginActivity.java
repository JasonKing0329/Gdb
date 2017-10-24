package com.jing.app.jjgallery.gdb.view.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.jing.app.jjgallery.gdb.ActivityManager;
import com.jing.app.jjgallery.gdb.GBaseActivity;
import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.http.BaseUrl;
import com.jing.app.jjgallery.gdb.model.SettingProperties;
import com.jing.app.jjgallery.gdb.model.conf.ConfManager;
import com.jing.app.jjgallery.gdb.model.conf.Configuration;
import com.jing.app.jjgallery.gdb.model.login.LoginParams;
import com.jing.app.jjgallery.gdb.presenter.LoginPresenter;
import com.jing.app.jjgallery.gdb.util.PermissionUtil;
import com.jing.app.jjgallery.gdb.view.pub.ProgressButton;
import com.jing.app.jjgallery.gdb.view.pub.ProgressProvider;
import com.jing.app.jjgallery.gdb.view.pub.dialog.DefaultDialogManager;
import com.jing.app.jjgallery.gdb.view.settings.SettingsActivity;
import com.jing.app.jjgallery.gdb.view.update.UpdateListener;
import com.jing.app.jjgallery.gdb.view.update.UpdateManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
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

    private boolean executeInsertProcess;
    private boolean isServiceBound;

    private UpdateManager updateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void initController() {
        loginPresenter = new LoginPresenter(this, this);
    }

    @Override
    protected Unbinder initView() {
        Unbinder unbinder = ButterKnife.bind(this);
        return unbinder;
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

                // 检查扩展配置
                boolean hasPref = ConfManager.checkExtendConf();

                e.onNext(hasPref);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean hasPref) throws Exception {

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
            showToastLong(getString(R.string.server_not_conf), ProgressProvider.TOAST_WARNING);
            startSetting();
        }
        else {
            if (SettingProperties.isAppInited()) {
                showPage();
            }
            else {
                startSetting();
            }
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
                showToastLong(msg, ProgressProvider.TOAST_ERROR);
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
                showToastLong(msg, ProgressProvider.TOAST_ERROR);
                break;
        }
    }

    protected void superUser() {
//		startService(new Intent().setClass(this, FileDBService.class));
        new DefaultDialogManager().showWarningActionDialog(this
                , getResources().getString(R.string.login_start_service_insert)
                , getResources().getString(R.string.yes)
                , getResources().getString(R.string.allno)
                , getResources().getString(R.string.action_settings)
                , new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            executeInsertProcess = true;
//                            showLoading();
//                            if (!isServiceRunning()) {
//                                isServiceBound = bindService(new Intent().setClass(LoginActivity.this, FileDBService.class)
//                                        , connection, BIND_AUTO_CREATE);
//                            }
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
//        new HomeSelecter(this).startDefaultHome(this, null);
        ActivityManager.startStarSwipeActivity(this);
        finish();
    }

    @OnClick(R.id.btn_login)
    public void onViewClicked() {
        loginPresenter.sign(actvUser.getText().toString(), etPwd.getText().toString());

    }
}
