package com.jing.app.jjgallery.gdb.view.pub.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/7/25 15:28
 */
public class AlertDialogFragmentV4 extends DialogFragment {

    private String title;

    private String message;

    private String positiveText;

    private String negativeText;

    private String neutralText;

    private CharSequence[] items;

    private DialogInterface.OnClickListener itemListener;

    private DialogInterface.OnClickListener positiveListener;

    private DialogInterface.OnClickListener negativeListener;

    private DialogInterface.OnClickListener neutralListener;

    private DialogInterface.OnDismissListener dismissListener;

    public AlertDialogFragmentV4 setTitle(String title) {
        this.title = title;
        return this;
    }

    public AlertDialogFragmentV4 setMessage(String message) {
        this.message = message;
        return this;
    }

    public AlertDialogFragmentV4 setPositiveText(String positiveText) {
        this.positiveText = positiveText;
        return this;
    }

    public AlertDialogFragmentV4 setNegativeText(String negativeText) {
        this.negativeText = negativeText;
        return this;
    }

    public AlertDialogFragmentV4 setNeutralText(String neutralText) {
        this.neutralText = neutralText;
        return this;
    }

    public AlertDialogFragmentV4 setPositiveListener(DialogInterface.OnClickListener positiveListener) {
        this.positiveListener = positiveListener;
        return this;
    }

    public AlertDialogFragmentV4 setNegativeListener(DialogInterface.OnClickListener negativeListener) {
        this.negativeListener = negativeListener;
        return this;
    }

    public AlertDialogFragmentV4 setNeutralListener(DialogInterface.OnClickListener neutralListener) {
        this.neutralListener = neutralListener;
        return this;
    }

    public AlertDialogFragmentV4 setDismissListener(DialogInterface.OnDismissListener dismissListener) {
        this.dismissListener = dismissListener;
        return this;
    }

    public AlertDialogFragmentV4 setItems(CharSequence[] items, final DialogInterface.OnClickListener listener) {
        this.items = items;
        this.itemListener = listener;
        return this;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)
                .setMessage(message);
        if (items != null) {
            builder.setItems(items, itemListener);
        }
        if (positiveText != null) {
            builder.setPositiveButton(positiveText, positiveListener);
        }
        if (negativeText != null) {
            builder.setNegativeButton(negativeText, negativeListener);
        }
        if (neutralText != null) {
            builder.setNeutralButton(neutralText, neutralListener);
        }
        builder.setOnDismissListener(dismissListener);
        return builder.create();
    }
}
