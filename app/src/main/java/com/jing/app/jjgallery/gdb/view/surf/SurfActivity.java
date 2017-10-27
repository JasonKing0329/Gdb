package com.jing.app.jjgallery.gdb.view.surf;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jing.app.jjgallery.gdb.ActivityManager;
import com.jing.app.jjgallery.gdb.GBaseActivity;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.http.HttpConstants;
import com.jing.app.jjgallery.gdb.http.bean.data.FileBean;
import com.jing.app.jjgallery.gdb.model.bean.SurfFileBean;
import com.jing.app.jjgallery.gdb.presenter.surf.SurfPresenter;
import com.jing.app.jjgallery.gdb.view.record.SortDialogFragment;
import com.king.lib.jindicator.IndicatorView;
import com.king.service.gdb.bean.Record;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/7/27 10:57
 */
public class SurfActivity extends GBaseActivity implements ISurfView, ISurfHolder, SurfAdapter.OnSurfItemActionListener {

    @BindView(R.id.fab_top)
    FloatingActionButton fabTop;
    @BindView(R.id.indicator)
    IndicatorView indicatorView;

    private SurfPresenter presenter;

    private int currentSortMode;
    private boolean currentSortDesc;

    private SurfFragmentTree ftTree;

    @Override
    protected int getContentView() {
        return R.layout.activity_gdb_surf;
    }

    @Override
    protected void initController() {
        presenter = new SurfPresenter(this);

        ftTree = new SurfFragmentTree();
    }

    @Override
    protected Unbinder initView() {
        Unbinder unbinder = ButterKnife.bind(this);

        indicatorView.addPath("Content");
        indicatorView.setPathIndicatorListener(new IndicatorView.PathIndicatorListener() {
            @Override
            public void onClickPath(int index, String path) {
                if (index != ftTree.level) {
                    backToFragment(index);
                }
            }
        });

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle("Surf");

        fabTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftTree.fragment.goTop();
            }
        });

        initFragment();
        return unbinder;
    }

    @Override
    protected void initBackgroundWork() {

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (ftTree.parent != null) {
            if (indicatorView.isBackable()) {
                indicatorView.backToUpper();
            }
            backToUpperFragment();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gdb_surf, menu);
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
    public void onFolderReceived(List<SurfFileBean> list) {
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
        indicatorView.addPath(fileBean.getName());
        showNewFragment(fileBean);
    }

    /**
     * initialize root directory
     */
    private void initFragment() {
        FileBean bean = new FileBean();
        bean.setPath(HttpConstants.FOLDER_TYPE_CONTENT);
        bean.setName(HttpConstants.FOLDER_TYPE_CONTENT);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ftTree.fragment = new SurfFragment();
        ftTree.fragment.setFolder(bean);
        ftTree.fragment.setOnSurfItemActionListener(this);
        ft.add(R.id.group_ft_container, ftTree.fragment, "SurfFragment_" + ftTree.level);
        ft.commit();
    }

    /**
     * click sub folder to start a sub-level folder by new fragment
     * @param fileBean
     */
    private void showNewFragment(FileBean fileBean) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        SurfFragmentTree node = new SurfFragmentTree();
        node.fragment = new SurfFragment();
        node.fragment.setFolder(fileBean);
        node.fragment.setOnSurfItemActionListener(this);
        node.level = ftTree.level + 1;
        node.parent = ftTree;
        ftTree.child = node;
        ft.add(R.id.group_ft_container, node.fragment, "SurfFragment_" + node.level).hide(ftTree.fragment);
        ft.commit();

        // update current node
        ftTree = node;
    }

    /**
     * back to upper folder
     */
    private void backToUpperFragment() {
        if (ftTree.parent != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.show(ftTree.parent.fragment).remove(ftTree.fragment);
            ft.commit();
            ftTree.child = null;
            // update current node
            ftTree = ftTree.parent;
        }
    }

    /**
     * back to fragment at target level
     * @param level target level should less than current level
     */
    private void backToFragment(int level) {
        SurfFragmentTree tree = ftTree;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        while (tree != null && level != tree.level) {
            tree.child = null;
            ft.hide(tree.fragment);
            ft.remove(tree.fragment);
            tree = tree.parent;
        }
        tree.child = null;
        ft.show(tree.fragment);

        ft.commit();

        // update current node
        ftTree = tree;
    }

    @Override
    public void onClickSurfRecord(Record record) {
        if (record != null) {
            ActivityManager.startRecordActivity(this, record);
        }
    }

    @Override
    public SurfPresenter getPresenter() {
        return presenter;
    }
}
