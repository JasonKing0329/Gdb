package com.jing.app.jjgallery.gdb.view.surf;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.jing.app.jjgallery.gdb.BaseFragmentV4;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.http.bean.data.FileBean;
import com.jing.app.jjgallery.gdb.view.pub.AutoLoadMoreRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 描述: 只负责list的展示与发起加载的时机，响应外部的排序、管理以及发起操作
 * 服务端文件目录、本地目录的公共处理部分
 * <p/>作者：景阳
 * <p/>创建时间: 2017/7/31 13:27
 */
public abstract class SurfFragment extends BaseFragmentV4 {

    @BindView(R.id.rv_files)
    AutoLoadMoreRecyclerView rvFiles;

    protected FileBean folderBean;
    private SurfAdapter surfAdapter;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_gdb_surf_list;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvFiles.setLayoutManager(manager);

        rvFiles.setEnableLoadMore(true);
        rvFiles.setOnLoadMoreListener(new AutoLoadMoreRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

            }
        });

        loadFolder();
    }

    /**
     * go to the top of list
     */
    public void goTop() {
        rvFiles.scrollToPosition(0);
    }

    /**
     * set current folder of current fragment
     * @param fileBean
     */
    public void setFolder(FileBean fileBean) {
        this.folderBean = fileBean;
    }

    /**
     * load file list of current folder
     * callback at onFolderReceived
     */
    public abstract void loadFolder();

    /**
     * update current file list
     * @param list
     */
    protected void updateSurfList(List<FileBean> list) {
        if (surfAdapter == null) {
            surfAdapter = new SurfAdapter(list);
            surfAdapter.setOnSurfItemActionListener(getOnSurfItemActionListener());
            rvFiles.setAdapter(surfAdapter);
        } else {
            surfAdapter.setList(list);
            surfAdapter.notifyDataSetChanged();
        }
    }

    protected abstract SurfAdapter.OnSurfItemActionListener getOnSurfItemActionListener();

    /**
     * notify item changed in index
     * @param index
     */
    public void notifyItemChanged(int index) {
        surfAdapter.notifyItemChanged(index);
    }

    /**
     * surfFileList data changed, but the surfFileList reference is not changed
     */
    public void notifyDataSetChanged() {
        surfAdapter.notifyDataSetChanged();
    }
}
