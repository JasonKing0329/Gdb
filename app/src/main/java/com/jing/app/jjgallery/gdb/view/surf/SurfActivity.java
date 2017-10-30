package com.jing.app.jjgallery.gdb.view.surf;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.View;

import com.jing.app.jjgallery.gdb.GBaseActivity;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.http.bean.data.FileBean;
import com.king.lib.jindicator.IndicatorView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 描述: 文件目录浏览，封装本地文件、网络文件浏览的公共部分
 * 处理层级fragment的管理，切换
 * <p/>作者：景阳
 * <p/>创建时间: 2017/7/27 10:57
 */
public abstract class SurfActivity<T extends SurfFragment> extends GBaseActivity {

    @BindView(R.id.fab_top)
    FloatingActionButton fabTop;
    @BindView(R.id.indicator)
    IndicatorView indicatorView;

    protected SurfFragmentTree<T> ftTree;

    @Override
    protected int getContentView() {
        return R.layout.activity_gdb_surf;
    }

    @Override
    protected void initController() {
        ftTree = new SurfFragmentTree();
    }

    @Override
    protected Unbinder initView() {
        Unbinder unbinder = ButterKnife.bind(this);

        indicatorView.addPath(getRootPathName());
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

    protected abstract String getRootPathName();

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

    protected void onClickFolder(FileBean fileBean) {
        indicatorView.addPath(fileBean.getName());
        showNewFragment(fileBean);
    }

    /**
     * initialize root directory
     */
    private void initFragment() {

        FileBean bean = createRootFileBean();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ftTree.fragment = newSurfFragment();
        ftTree.fragment.setFolder(bean);
        ft.add(R.id.group_ft_container, ftTree.fragment, "SurfFragment_" + ftTree.level);
        ft.commit();
    }

    protected abstract FileBean createRootFileBean();

    protected abstract T newSurfFragment();

    /**
     * click sub folder to start a sub-level folder by new fragment
     * @param fileBean
     */
    private void showNewFragment(FileBean fileBean) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        SurfFragmentTree node = new SurfFragmentTree();
        node.fragment = newSurfFragment();
        node.fragment.setFolder(fileBean);
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

}
