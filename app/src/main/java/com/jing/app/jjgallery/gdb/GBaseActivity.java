package com.jing.app.jjgallery.gdb;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Toast;

import com.jing.app.jjgallery.gdb.util.DisplayHelper;
import com.jing.app.jjgallery.gdb.view.pub.ProgressProvider;
import com.jing.app.jjgallery.gdb.view.pub.dialog.LoadingDialog;
import com.jing.app.jjgallery.gdb.view.pub.dialog.LoadingDialogV4;
import com.jing.app.jjgallery.gdb.view.toast.TastyToast;

import butterknife.Unbinder;

/**
 * Created by Administrator on 2017/5/20 0020.
 */

public abstract class GBaseActivity extends AppCompatActivity implements ProgressProvider {

    private ProgressDialog progressDialog;
    private Unbinder unbinder;

    private LoadingDialog loadingDialog;
    private LoadingDialogV4 loadingDialogV4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        DisplayHelper.enableFullScreen();
        DisplayHelper.disableScreenshot(this);
        
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(this);

        setContentView(getContentView());
        initController();
        unbinder = initView();
        initBackgroundWork();
    }

    protected abstract int getContentView();

    protected abstract void initController();

    protected abstract Unbinder initView();

    protected abstract void initBackgroundWork();

    public void showProgress(String text) {
        progressDialog.setMessage(text);
        progressDialog.show();
    }

    public boolean dismissProgress() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
            return true;
        }
        return  false;
    }

    public void showLoading() {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog();
        }
        if (!loadingDialog.isAdded()) {
            loadingDialog.show(getFragmentManager(), "LoadingDialog");
        }
    }

    public boolean dismissLoading() {
        if (loadingDialog != null && loadingDialog.isVisible()) {
            loadingDialog.dismiss();
            return true;
        }
        return  false;
    }

    public void showLoadingV4() {
        if (loadingDialogV4 == null) {
            loadingDialogV4 = new LoadingDialogV4();
        }
        if (!loadingDialogV4.isAdded()) {
            loadingDialogV4.show(getSupportFragmentManager(), "LoadingDialogV4");
        }
    }

    public boolean dismissLoadingV4() {
        if (loadingDialogV4 != null && loadingDialogV4.isVisible()) {
            loadingDialogV4.dismiss();
            return true;
        }
        return  false;
    }

    public void showProgressCycler() {
        showProgress("loading...");
    }

    public boolean dismissProgressCycler() {
        return dismissProgress();
    }

    public void showToastLong(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    public void showToastShort(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    public void showToastLong(String text, int type) {
        showToastLib(text, type, TastyToast.LENGTH_LONG);
    }

    public void showToastShort(String text, int type) {
        showToastLib(text, type, TastyToast.LENGTH_SHORT);
    }

    public void showToastLib(String text, int type, int time) {
        switch (type) {
            case ProgressProvider.TOAST_SUCCESS:
                TastyToast.makeText(this, text, time, TastyToast.SUCCESS);
                break;
            case ProgressProvider.TOAST_ERROR:
                TastyToast.makeText(this, text, time, TastyToast.ERROR);
                break;
            case ProgressProvider.TOAST_WARNING:
                TastyToast.makeText(this, text, time, TastyToast.WARNING);
                break;
            case ProgressProvider.TOAST_INFOR:
                TastyToast.makeText(this, text, time, TastyToast.INFO);
                break;
            case ProgressProvider.TOAST_DEFAULT:
                TastyToast.makeText(this, text, time, TastyToast.DEFAULT);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }
}
