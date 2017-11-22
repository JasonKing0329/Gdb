package com.jing.app.jjgallery.gdb.view.settings;

import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.view.View;

import com.jing.app.jjgallery.gdb.GBaseActivity;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.http.Command;
import com.jing.app.jjgallery.gdb.http.bean.data.DownloadItem;
import com.jing.app.jjgallery.gdb.model.bean.DownloadDialogBean;
import com.jing.app.jjgallery.gdb.presenter.ManagePresenter;
import com.jing.app.jjgallery.gdb.view.download.v4.DownloadDialogFragmentV4;
import com.jing.app.jjgallery.gdb.view.pub.ProgressProvider;
import com.jing.app.jjgallery.gdb.view.pub.dialog.AlertDialogFragmentV4;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

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
    protected Unbinder initView() {
        Unbinder unbinder = ButterKnife.bind(this);

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle("Resource manage");

        return unbinder;
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
                optionServerAction(Command.TYPE_STAR, getString(R.string.gdb_move_star));
                break;
            case R.id.group_move_records:
                optionServerAction(Command.TYPE_RECORD, getString(R.string.gdb_move_record));
                break;
            case R.id.group_clear:
                break;
            case R.id.group_check_server:
                showLoadingV4();
                presenter.checkServerStatus();
                break;
        }
    }

    @Override
    public void onServerConnected() {
        showToastLong(getString(R.string.gdb_server_online), ProgressProvider.TOAST_SUCCESS);
        dismissLoadingV4();
    }

    @Override
    public void onServerUnavailable() {
        showToastLong(getString(R.string.gdb_server_offline), ProgressProvider.TOAST_ERROR);
        dismissLoadingV4();
    }

    @Override
    public void onMoveImagesSuccess() {
        showToastLong(getString(R.string.success), ProgressProvider.TOAST_INFOR);
        dismissLoadingV4();
    }

    @Override
    public void onMoveImagesFail() {
        showToastLong(getString(R.string.failed), ProgressProvider.TOAST_INFOR);
        dismissLoadingV4();
    }

    @Override
    public void onCheckPass(boolean hasNew, List<DownloadItem> toDownloadList, final List<DownloadItem> repeatList, String savePath) {
        if (hasNew) {
            DownloadDialogFragmentV4 downloadDialogFragment = new DownloadDialogFragmentV4();
            DownloadDialogBean bean = new DownloadDialogBean();
            bean.setDownloadList(toDownloadList);
            bean.setExistedList(repeatList);
            bean.setSavePath(savePath);
            bean.setShowPreview(true);
            downloadDialogFragment.setDialogBean(bean);
            downloadDialogFragment.setOnDownloadListener(new DownloadDialogFragmentV4.OnDownloadListener() {
                @Override
                public void onDownloadFinish(DownloadItem item) {

                }

                @Override
                public void onDownloadFinish(List<DownloadItem> downloadList) {
                    showToastLong(getString(R.string.gdb_download_done), ProgressProvider.TOAST_SUCCESS);
                }
            });
            downloadDialogFragment.show(getSupportFragmentManager(), "DownloadDialogFragmentV4");
        } else {
            showToastLong(getString(R.string.gdb_no_new_images), ProgressProvider.TOAST_INFOR);
        }
        dismissLoadingV4();
    }

    @Override
    public void onRequestFail() {
        showToastLong(getString(R.string.gdb_request_fail), ProgressProvider.TOAST_ERROR);
        dismissLoadingV4();
    }

    /**
     * request server move original image files
     *
     * @param type
     */
    private void optionServerAction(final String type, String message) {
        AlertDialogFragmentV4 dialog = new AlertDialogFragmentV4();
        dialog.setMessage(message);
        dialog.setPositiveText(getString(R.string.yes));
        dialog.setPositiveListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showLoadingV4();
                presenter.requestServeMoveImages(type);
            }
        });
        dialog.setNegativeText(getString(R.string.no));
        dialog.show(getSupportFragmentManager(), "AlertDialogFragmentV4");
    }

}
