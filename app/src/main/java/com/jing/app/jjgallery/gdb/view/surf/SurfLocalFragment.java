package com.jing.app.jjgallery.gdb.view.surf;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;

import com.jing.app.jjgallery.gdb.GdbConstants;
import com.jing.app.jjgallery.gdb.IFragmentHolder;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.http.bean.data.FileBean;
import com.jing.app.jjgallery.gdb.presenter.surf.SurfLocalPresenter;
import com.jing.app.jjgallery.gdb.util.FileUtil;
import com.jing.app.jjgallery.gdb.view.pub.dialog.AlertDialogFragmentV4;
import com.jing.app.jjgallery.gdb.view.pub.dialog.DefaultDialogManager;

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
    public void onLongClickSurfFolder(final FileBean bean) {
        AlertDialogFragmentV4 dialog = new AlertDialogFragmentV4();
        dialog.setTitle(null);
        dialog.setItems(getResources().getStringArray(R.array.menu_surf_item), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    // 文件夹包含更多文件，再次警告
                    warningDeleteFolder(bean);
                }
            }
        });
        dialog.show(getFragmentManager(), "AlertDialogFragmentV4");
    }

    private void warningDeleteFolder(final FileBean bean) {
        int count = presenter.countFolderFiles(bean);
        String msg = String.format(getString(R.string.warning_delete_folder), count);
        new DefaultDialogManager().showOptionDialogFragment(getFragmentManager(), getString(R.string.warning)
                , msg, getString(R.string.ok), null, getString(R.string.cancel)
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.deleteFile(bean);
                        loadFolder();
                    }
                }, null, null, null);
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

    @Override
    public void onLongClickSurfFile(final FileBean bean) {
        AlertDialogFragmentV4 dialog = new AlertDialogFragmentV4();
        dialog.setTitle(null);
        dialog.setItems(getResources().getStringArray(R.array.menu_surf_item), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    presenter.deleteFile(bean);
                    loadFolder();
                }
            }
        });
        dialog.show(getFragmentManager(), "AlertDialogFragmentV4");
    }
}
