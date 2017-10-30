package com.jing.app.jjgallery.gdb.view.surf;

import android.view.Menu;
import android.view.MenuItem;

import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.http.bean.data.FileBean;
import com.jing.app.jjgallery.gdb.model.conf.Configuration;
import com.jing.app.jjgallery.gdb.model.conf.PreferenceValue;

/**
 * 描述: local files explore
 * <p/>作者：景阳
 * <p/>创建时间: 2017/10/30 14:36
 */
public class SurfLocalActivity extends SurfActivity<SurfLocalFragment> implements ISurfLocalHolder {

    private int currentSortMode;
    private boolean currentSortDesc;

    @Override
    protected void initController() {
        currentSortMode = PreferenceValue.GDB_SR_ORDERBY_NAME;
        currentSortDesc = false;
        super.initController();
    }

    @Override
    protected String getRootPathName() {
        return "/img";
    }

    @Override
    protected FileBean createRootFileBean() {
        FileBean bean = new FileBean();
        bean.setPath(Configuration.APP_DIR_IMG);
        bean.setFolder(true);
        return bean;
    }

    @Override
    protected SurfLocalFragment newSurfFragment() {
        return new SurfLocalFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gdb_surf_local, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort_by_name:
                if (ftTree != null && ftTree.fragment != null) {
                    // 名称升序
                    currentSortMode = PreferenceValue.GDB_SR_ORDERBY_NAME;
                    currentSortDesc = false;
                    ftTree.fragment.onSortFiles();
                }
                break;
            case R.id.menu_sort_by_date:
                if (ftTree != null && ftTree.fragment != null) {
                    // 时间降序
                    currentSortMode = PreferenceValue.GDB_SR_ORDERBY_TIME;
                    currentSortDesc = true;
                    ftTree.fragment.onSortFiles();
                }
                break;
            case R.id.menu_sort_by_size:
                if (ftTree != null && ftTree.fragment != null) {
                    // 大小降序
                    currentSortMode = PreferenceValue.GDB_SR_ORDERBY_SIZE;
                    currentSortDesc = true;
                    ftTree.fragment.onSortFiles();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
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
    public int getSortMode() {
        return currentSortMode;
    }

    @Override
    public boolean isSortDesc() {
        return currentSortDesc;
    }

    @Override
    public void onClickSurfFolder(FileBean fileBean) {
        onClickFolder(fileBean);
    }
}
