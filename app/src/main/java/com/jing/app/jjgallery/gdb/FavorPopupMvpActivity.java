package com.jing.app.jjgallery.gdb;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.jing.app.jjgallery.gdb.view.favor.FavorPopup;
import com.jing.app.jjgallery.gdb.view.favor.SimpleOrderDialog;
import com.jing.app.jjgallery.gdb.view.favor.record.RecordOrderPadActivity;
import com.jing.app.jjgallery.gdb.view.pub.dialog.AlertDialogFragmentV4;

/**
 * 描述: 封装需要调用FavorPopup的功能
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/14 17:20
 */
public abstract class FavorPopupMvpActivity<P extends BasePresenter> extends MvpActivity<P> implements FavorPopup.FavorView {

    private final int REQUEST_SELECT_ORDER = 1010;

    private FavorPopup favorPopup;

    private SimpleOrderDialog orderDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        favorPopup = new FavorPopup(this);
    }

    public FavorPopup getFavorPopup() {
        return favorPopup;
    }

    @Override
    public void showDeleteWarning(final String filePath) {
        AlertDialogFragmentV4 dialog = new AlertDialogFragmentV4();
        dialog.setMessage("Are you sure to delete " + filePath);
        dialog.setPositiveText(getString(R.string.ok));
        dialog.setPositiveListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onDeleteImage(filePath);
            }
        });
        dialog.setNegativeText(getString(R.string.cancel));
        dialog.show(getSupportFragmentManager(), "AlertDialogFragmentV4");
    }

    /**
     * 子类选择实现
     * @param filePath
     */
    protected void onDeleteImage(String filePath) {
    }

    @Override
    public void requestSelectOrder() {
        ActivityManager.startRecordOrderActivity(this, REQUEST_SELECT_ORDER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_ORDER) {
            if (resultCode == RESULT_OK) {
                favorPopup.onSelectOrder(data.getLongExtra(RecordOrderPadActivity.KEY_SELECTED_ID, -1));
            }
        }
    }

    @Override
    public void showRecordOrders(long recordId) {
        orderDialog = new SimpleOrderDialog();
        orderDialog.setRecordId(recordId);
        orderDialog.show(getSupportFragmentManager(), "SimpleOrderDialog");
    }
}
