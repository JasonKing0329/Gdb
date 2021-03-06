package com.jing.app.jjgallery.gdb;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jing.app.jjgallery.gdb.view.pub.dialog.LoadingDialog;
import com.jing.app.jjgallery.gdb.view.toast.TastyToast;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/5/19 14:24
 */
public abstract class BaseFragment extends Fragment implements BaseView {

    private ProgressDialog progressDialog;

    private LoadingDialog loadingDialog;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IFragmentHolder) {
            bindFragmentHolder((IFragmentHolder) context);
        }
    }

    protected abstract void bindFragmentHolder(IFragmentHolder holder);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(getLayoutRes(), container, false);
        initView(view);
        return view;
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
            loadingDialog = new LoadingDialog();
        }
        if (!loadingDialog.isAdded()) {
            loadingDialog.show(getFragmentManager(), "LoadingDialog");
        }
    }

    @Override
    public boolean dismissLoading() {
        if (loadingDialog != null && loadingDialog.isVisible()) {
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
