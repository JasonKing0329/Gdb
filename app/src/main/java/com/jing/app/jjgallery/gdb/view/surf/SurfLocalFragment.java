package com.jing.app.jjgallery.gdb.view.surf;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.jing.app.jjgallery.gdb.GdbConstants;
import com.jing.app.jjgallery.gdb.IFragmentHolder;
import com.jing.app.jjgallery.gdb.http.bean.data.FileBean;
import com.jing.app.jjgallery.gdb.presenter.surf.SurfLocalPresenter;
import com.jing.app.jjgallery.gdb.util.FileUtil;

import java.util.List;

/**
 * 描述:local files explore
 * <p/>作者：景阳
 * <p/>创建时间: 2017/10/30 14:27
 */
public class SurfLocalFragment extends SurfFragment implements ISurfLocalView, SurfAdapter.OnSurfItemActionListener<FileBean> {

    private SurfLocalPresenter presenter;
    private ISurfLocalHolder holder;

    private List<FileBean> fileList;

    @Override
    protected void initView(View view) {
        presenter = new SurfLocalPresenter(this);
        super.initView(view);
    }

    /**
     * set current folder of current fragment
     * @param fileBean
     */
    public void setFolder(FileBean fileBean) {
        this.folderBean = fileBean;
    }

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {
        if (holder instanceof ISurfLocalHolder) {
            this.holder = (ISurfLocalHolder) holder;
        }
    }

    @Override
    public void loadFolder() {
        holder.startProgress();
        presenter.surf(folderBean, holder.getSortMode(), holder.isSortDesc());
    }

    @Override
    public void onFolderReceived(List<FileBean> list) {
        fileList = list;
        updateSurfList(list);
        holder.endProgress();
    }

    @Override
    protected SurfAdapter.OnSurfItemActionListener getOnSurfItemActionListener() {
        return this;
    }

    @Override
    public void onSortFinished() {
        notifyDataSetChanged();
    }

    public void onSortFiles() {
        presenter.sortFileList(fileList, holder.getSortMode(), holder.isSortDesc());
    }

    @Override
    public void onClickSurfFolder(FileBean fileBean) {
        holder.onClickSurfFolder(fileBean);
    }

    @Override
    public void onClickSurfFile(FileBean file) {
        if (FileUtil.isImageFile(file.getPath())) {
            // 作为文件选择器
            if (GdbConstants.REQUEST_SELECT_IMAGE == getActivity().getIntent().getIntExtra(GdbConstants.KEY_REQEUST_CODE, -1)) {
                Intent intent = new Intent();
                intent.putExtra(GdbConstants.DATA_SELECT_IMAGE, file.getPath());
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        }
    }
}
