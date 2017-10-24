package com.jing.app.jjgallery.gdb.view.download.v4;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;

import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.http.bean.data.DownloadItem;
import com.jing.app.jjgallery.gdb.model.bean.DownloadDialogBean;
import com.jing.app.jjgallery.gdb.view.download.IDownloadContentHolder;
import com.jing.app.jjgallery.gdb.view.pub.dialog.DraggableDialogFragmentV4;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述: 前台调用后台进行下载，不支持后台下载（即fragment结束后download状态不可控）
 * <p/>作者：景阳
 * <p/>创建时间: 2017/7/24 17:18
 */
public class DownloadDialogFragmentV4 extends DraggableDialogFragmentV4 implements IDownloadContentHolder {

    private OnDownloadListener onDownloadListener;

    private DownloadDialogBean dialogBean;

    /**
     * preview page, could be not shown or show only once
     */
    private DownloadPreviewFragmentV4 ftPreview;

    /**
     * download list page
     */
    private DownloadListFragmentV4 ftList;

    @Override
    protected View getToolbarView(ViewGroup groupToolbar) {
        setTitle(R.string.download_title);
        requestCloseAction();
        return null;
    }

    @Override
    protected Fragment getContentViewFragment() {

        // 由于暂不支持后台静默下载，因此设置不可触碰其他区域关闭
        setCancelable(false);

        if (dialogBean.isShowPreview()) {
            ftPreview = new DownloadPreviewFragmentV4();
            return ftPreview;
        }
        else {
            initListFragment();
            return ftList;
        }
    }

    private void initListFragment() {
        if (ftList == null) {
            ftList = new DownloadListFragmentV4();
            ftList.setOnDownloadListener(onDownloadListener);
        }
    }

    public void setOnDownloadListener(OnDownloadListener onDownloadListener) {
        this.onDownloadListener = onDownloadListener;
    }

    public void setDialogBean(DownloadDialogBean dialogBean) {
        this.dialogBean = dialogBean;
    }

    @Override
    public List<DownloadItem> getDownloadList() {
        return dialogBean.getDownloadList();
    }

    @Override
    public List<DownloadItem> getExistedList() {
        return dialogBean.getExistedList();
    }

    @Override
    public String getSavePath() {
        return dialogBean.getSavePath();
    }

    @Override
    public void dismissDialog() {
        dismiss();
    }

    @Override
    public void startDownload() {
        showListFragment();
    }

    /**
     * preview page is only shown once, so replace fragment
     */
    private void showListFragment() {
        initListFragment();
        replaceContentFragment(ftList, "ListFragment");
    }

    @Override
    public void addDownloadItems(List<DownloadItem> checkedItems) {
        if (dialogBean.getDownloadList() == null) {
            dialogBean.setDownloadList(new ArrayList<DownloadItem>());
        }
        dialogBean.getDownloadList().addAll(checkedItems);
    }

    public interface OnDownloadListener {
        void onDownloadFinish(DownloadItem item);

        void onDownloadFinish(List<DownloadItem> downloadList);
    }
}
