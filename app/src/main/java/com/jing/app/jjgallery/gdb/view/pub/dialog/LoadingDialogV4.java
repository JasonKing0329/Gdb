package com.jing.app.jjgallery.gdb.view.pub.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.jing.app.jjgallery.gdb.R;


/**
 * 描述:loading对话框
 * <p/>作者：李纬杰
 * <br/>创建时间: 2017/4/19
 */

public class LoadingDialogV4 extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setCancelable(false);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.LoadingDialog);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_loading, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setGravity(Gravity.CENTER);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        if (isAdded()) {
            ft.show(this);
        }
        else {
            ft.add(this, tag);
        }
        ft.commitAllowingStateLoss();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
