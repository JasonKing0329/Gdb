package com.jing.app.jjgallery.gdb.view.surf;

import android.view.Menu;
import android.view.MenuItem;

import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.http.HttpConstants;
import com.jing.app.jjgallery.gdb.http.bean.data.FileBean;
import com.jing.app.jjgallery.gdb.model.bean.HttpSurfFileBean;
import com.jing.app.jjgallery.gdb.presenter.surf.SurfHttpPresenter;
import com.jing.app.jjgallery.gdb.view.record.SortDialogFragment;

import java.util.List;

/**
 * 描述: server files explore
 * <p/>作者：景阳
 * <p/>创建时间: 2017/10/30 13:18
 */
public class SurfHttpActivity extends SurfActivity<SurfHttpFragment> implements ISurfHttpView, ISurfHttpHolder {

    protected int currentSortMode;
    protected boolean currentSortDesc;

    private SurfHttpPresenter presenter;

    @Override
    protected void initController() {
        super.initController();
        presenter = new SurfHttpPresenter(this);
    }

    @Override
    protected String getRootPathName() {
        return "Contents";
    }

    @Override
    protected FileBean createRootFileBean() {
        FileBean bean = new FileBean();
        bean.setPath(HttpConstants.FOLDER_TYPE_CONTENT);
        bean.setName(HttpConstants.FOLDER_TYPE_CONTENT);
        return bean;
    }

    @Override
    protected SurfHttpFragment newSurfFragment() {
        return new SurfHttpFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gdb_surf_http, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_gdb_surf_relate:
                presenter.relateToDatabase(ftTree.fragment.getSurfFileList());
                break;
            case R.id.menu_gdb_surf_sort:
                showSortDialog();
                break;
            case R.id.menu_gdb_surf_refresh:
                ftTree.fragment.loadFolder();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSortDialog() {
        SortDialogFragment sortDialog = new SortDialogFragment();
        sortDialog.setSortMode(currentSortMode);
        sortDialog.setDesc(currentSortDesc);
        sortDialog.setOnSortListener(new SortDialogFragment.OnSortListener() {
            @Override
            public void onSort(boolean desc, int sortMode, boolean isIncludeDeprecated) {
                currentSortDesc = desc;
                currentSortMode = sortMode;
                presenter.sortFileList(ftTree.fragment.getSurfFileList(), currentSortMode, currentSortDesc);
            }
        });
        sortDialog.show(getSupportFragmentManager(), "SortDialogFragment");
    }

    @Override
    public void onRequestFail() {
        dismissProgress();
        showToastLong(getString(R.string.gdb_request_fail));
    }

    @Override
    public void onFolderReceived(List<HttpSurfFileBean> list) {
        ftTree.fragment.onFolderReceived(list);
    }

    @Override
    public void onRecordRelated(int index) {
        if (!isDestroyed()) {
            ftTree.fragment.notifyItemChanged(index);
        }
    }

    @Override
    public void onSortFinished() {
        ftTree.fragment.notifyDataSetChanged();
    }

    @Override
    public void startProgress() {
        showProgress(getString(R.string.loading));
    }

    @Override
    public void endProgress() {
        dismissProgress();
    }

    @Override
    public void onClickSurfFolder(FileBean fileBean) {
        onClickFolder(fileBean);
    }

    @Override
    public SurfHttpPresenter getPresenter() {
        return presenter;
    }
}
