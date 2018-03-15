package com.jing.app.jjgallery.gdb;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jing.app.jjgallery.gdb.view.pub.dialog.LoadingDialogV4;
import com.jing.app.jjgallery.gdb.view.toast.TastyToast;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/5/19 14:24
 */
public abstract class BaseFragmentV4 extends Fragment implements BaseView {

    private ProgressDialog progressDialog;

    private LoadingDialogV4 loadingDialog;

    private Unbinder unbinder;

    protected abstract void bindFragmentHolder(IFragmentHolder holder);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (getActivity() instanceof IFragmentHolder) {
            bindFragmentHolder((IFragmentHolder) getActivity());
        }

        if (getParentFragment() instanceof IFragmentHolder) {
            bindFragmentHolder((IFragmentHolder) getParentFragment());
        }

        View view = inflater.inflate(getLayoutRes(), container, false);
        unbinder = ButterKnife.bind(this, view);
        initView(view);
        return view;
    }

    @Override
    public void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }

    protected abstract int getLayoutRes();

    protected abstract void initView(View view);

    public void showProgressCycler() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getString(R.string.loading));
        }
        progressDialog.show();
    }

    public boolean dismissProgressCycler() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
            return true;
        }
        return false;
    }

    @Override
    public void showProgress(String text) {
        progressDialog.setMessage(text);
        progressDialog.show();
    }

    @Override
    public boolean dismissProgress() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
            return true;
        }
        return  false;
    }

    @Override
    public void showLoading() {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialogV4();
        }
        if (!loadingDialog.isAdded()) {
            loadingDialog.show(getChildFragmentManager(), "LoadingDialogV4");
        }
    }

    @Override
    public boolean dismissLoading() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
            return true;
        }
        return  false;
    }

    @Override
    public void showToastLong(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showToastShort(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showToastLong(String text, int type) {
        showToastLib(text, type, TastyToast.LENGTH_LONG);
    }

    @Override
    public void showToastShort(String text, int type) {
        showToastLib(text, type, TastyToast.LENGTH_SHORT);
    }

    public void showToastLib(String text, int type, int time) {
        switch (type) {
            case BaseView.TOAST_SUCCESS:
                TastyToast.makeText(getActivity(), text, time, TastyToast.SUCCESS);
                break;
            case BaseView.TOAST_ERROR:
                TastyToast.makeText(getActivity(), text, time, TastyToast.ERROR);
                break;
            case BaseView.TOAST_WARNING:
                TastyToast.makeText(getActivity(), text, time, TastyToast.WARNING);
                break;
            case BaseView.TOAST_INFOR:
                TastyToast.makeText(getActivity(), text, time, TastyToast.INFO);
                break;
            case BaseView.TOAST_DEFAULT:
                TastyToast.makeText(getActivity(), text, time, TastyToast.DEFAULT);
                break;
        }
    }

}
