package com.jing.app.jjgallery.gdb.view.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.view.View;

import com.jing.app.jjgallery.gdb.BaseView;
import com.jing.app.jjgallery.gdb.GBaseActivity;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.http.Command;
import com.jing.app.jjgallery.gdb.http.bean.data.DownloadItem;
import com.jing.app.jjgallery.gdb.model.bean.CheckDownloadBean;
import com.jing.app.jjgallery.gdb.model.bean.DownloadDialogBean;
import com.jing.app.jjgallery.gdb.presenter.ManagePresenter;
import com.jing.app.jjgallery.gdb.service.FileService;
import com.jing.app.jjgallery.gdb.view.download.v4.DownloadDialogFragmentV4;
import com.jing.app.jjgallery.gdb.view.pub.dialog.AlertDialogFragmentV4;

import java.util.List;

import butterknife.OnClick;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/11/22 15:52
 */
public class ManageActivity extends GBaseActivity implements IManageView {

    private ManagePresenter presenter;

    @Override
    protected int getContentView() {
        return R.layout.activity_manage;
    }

    @Override
    protected void initController() {
        presenter = new ManagePresenter(this);
    }

    @Override
    protected void initView() {
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle("Resource manage");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void initBackgroundWork() {

    }

    @OnClick({R.id.group_down_stars, R.id.group_down_records, R.id.group_move_stars
            , R.id.group_move_records, R.id.group_clear, R.id.group_check_server})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.group_down_stars:
                showLoadingV4();
                presenter.checkNewStarFile();
                break;
            case R.id.group_down_records:
                showLoadingV4();
                presenter.checkNewRecordFile();
                break;
            case R.id.group_move_stars:
                showConfirmCancel(getString(R.string.gdb_move_star), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showLoadingV4();
                        presenter.requestServeMoveImages(Command.TYPE_STAR);
                    }
                }, null);
                break;
            case R.id.group_move_records:
                showConfirmCancel(getString(R.string.gdb_move_record), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showLoadingV4();
                        presenter.requestServeMoveImages(Command.TYPE_RECORD);
                    }
                }, null);
                break;
            case R.id.group_clear:
                showConfirmCancel(getString(R.string.gdb_clear_useless), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showToastLong("Run on background...");
                        startService(new Intent().setClass(ManageActivity.this, FileService.class));
                    }
                }, null);
                break;
            case R.id.group_check_server:
                showLoadingV4();
                presenter.checkServerStatus();
                break;
        }
    }

    @Override
    public void onServerConnected() {
        showToastLong(getString(R.string.gdb_server_online), BaseView.TOAST_SUCCESS);
        dismissLoadingV4();
    }

    @Override
    public void onServerUnavailable() {
        showToastLong(getString(R.string.gdb_server_offline), BaseView.TOAST_ERROR);
        dismissLoadingV4();
    }

    @Override
    public void onMoveImagesSuccess() {
        showToastLong(getString(R.string.success), BaseView.TOAST_INFOR);
        dismissLoadingV4();
    }

    @Override
    public void onMoveImagesFail() {
        showToastLong(getString(R.string.failed), BaseView.TOAST_INFOR);
        dismissLoadingV4();
    }

    @Override
    public void onCheckPass(CheckDownloadBean checkDownloadBean) {
        if (checkDownloadBean.isHasNew()) {
            DownloadDialogFragmentV4 downloadDialogFragment = new DownloadDialogFragmentV4();
            DownloadDialogBean bean = new DownloadDialogBean();
            bean.setDownloadList(checkDownloadBean.getDownloadList());
            bean.setExistedList(checkDownloadBean.getRepeatList());
            bean.setSavePath(checkDownloadBean.getTargetPath());
            bean.setShowPreview(true);
            downloadDialogFragment.setDialogBean(bean);
            downloadDialogFragment.setOnDownloadListener(new DownloadDialogFragmentV4.OnDownloadListener() {
                @Override
                public void onDownloadFinish(DownloadItem item) {

                }

                @Override
                public void onDownloadFinish(List<DownloadItem> downloadList) {
                    showToastLong(getString(R.string.gdb_download_done), BaseView.TOAST_SUCCESS);
                }
            });
            downloadDialogFragment.show(getSupportFragmentManager(), "DownloadDialogFragmentV4");
        } else {
            showToastLong(getString(R.string.gdb_no_new_images), BaseView.TOAST_INFOR);
        }
        dismissLoadingV4();
    }

    @Override
    public void onRequestFail() {
        showToastLong(getString(R.string.gdb_request_fail), BaseView.TOAST_ERROR);
        dismissLoadingV4();
    }

    /**
     *
     * @param message
     */
    private void showConfirmCancel(String message, DialogInterface.OnClickListener positiveListener
            , DialogInterface.OnClickListener negativeListener) {
        AlertDialogFragmentV4 dialog = new AlertDialogFragmentV4();
        dialog.setMessage(message);
        dialog.setPositiveText(getString(R.string.yes));
        dialog.setPositiveListener(positiveListener);
        dialog.setNegativeText(getString(R.string.no));
        if (negativeListener != null) {
            dialog.setNegativeListener(negativeListener);
        }
        dialog.show(getSupportFragmentManager(), "AlertDialogFragmentV4");
    }

    @Override
    protected void onDestroy() {
        if (presenter != null) {
            presenter.dispose();
        }
        super.onDestroy();
    }
}
