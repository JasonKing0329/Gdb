package com.jing.app.jjgallery.gdb.view.home;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jing.app.jjgallery.gdb.ActivityManager;
import com.jing.app.jjgallery.gdb.GBaseActivity;
import com.jing.app.jjgallery.gdb.GdbConstants;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.http.bean.response.AppCheckBean;
import com.jing.app.jjgallery.gdb.model.GdbImageProvider;
import com.jing.app.jjgallery.gdb.model.SettingProperties;
import com.jing.app.jjgallery.gdb.model.VideoModel;
import com.jing.app.jjgallery.gdb.presenter.GdbGuidePresenter;
import com.jing.app.jjgallery.gdb.util.GlideUtil;
import com.jing.app.jjgallery.gdb.view.recommend.IRecommendHolder;
import com.jing.app.jjgallery.gdb.view.update.GdbUpdateListener;
import com.jing.app.jjgallery.gdb.view.update.GdbUpdateManager;

import butterknife.BindView;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/5/19 13:33
 */
public class GHomeActivity extends GBaseActivity implements NavigationView.OnNavigationItemSelectedListener
    , IHomeHolder, IRecommendHolder{

    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private ImageView navHeaderView;
    private ImageView ivFolder;
    private ImageView ivFace;

    private GdbGuidePresenter mPresenter;

    private GHomeFragment homeFragment;

    @Override
    public int getContentView() {
        return R.layout.activity_gdb_home_v4;
    }

    @Override
    public void initController() {
        mPresenter = new GdbGuidePresenter();
        VideoModel.loadVideos(this);
    }

    @Override
    public void initView() {
        initActionBar();
        initDrawer();
        initContent();
    }

    private void initActionBar() {
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        toolbar.setBackgroundColor(getResources().getColor(R.color.actionbar_bk_light));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        // xml里设置不管用，在上面设置也不管用，必须在drawerLayout.setDrawerListener设置完之后才管用
        toolbar.setNavigationIcon(R.drawable.ic_menu_purple_200_24dp);
    }

    @Override
    public void initBackgroundWork() {
        checkUpdate();
    }

    @Override
    protected void onDestroy() {
        VideoModel.clear();
        super.onDestroy();
    }

    private void initDrawer() {
        navView.setNavigationItemSelectedListener(this);
        navView.setItemIconTintList(null);
        navHeaderView = (ImageView) navView.getHeaderView(0).findViewById(R.id.nav_header_bg);
        ivFolder = (ImageView) navView.getHeaderView(0).findViewById(R.id.iv_folder);
        ivFace = (ImageView) navView.getHeaderView(0).findViewById(R.id.iv_face);
        ivFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingProperties.setGdbNavHeadRandom(true);
                focusOnRandom();
            }
        });
        ivFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        if (SettingProperties.isGdbNavHeadRandom()) {
            focusOnRandom();
        }
        else {
            focusOnFolder();
        }
    }

    private void focusOnFolder() {
        ivFolder.setSelected(true);
        ivFace.setSelected(false);
        String path = GdbImageProvider.getNavHeadImage();
        Glide.with(this)
                .load(path)
                .apply(GlideUtil.getRecordOptions())
                .into(navHeaderView);
    }

    private void focusOnRandom() {
        ivFace.setSelected(true);
        ivFolder.setSelected(false);
        Glide.with(this)
                .load(GdbImageProvider.getRandomNavHeadImage())
                .apply(GlideUtil.getRecordOptions())
                .into(navHeaderView);
    }

    private void initContent() {
        homeFragment = new GHomeFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.group_ft_container, homeFragment, "GHomeFragment");
        ft.commit();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_setting) {
            ActivityManager.startSettingActivity(this);
        }
        else if (id == R.id.nav_exit) {
            finish();
        }
        else if (id == R.id.nav_main) {
            ActivityManager.startSurfLocalActivity(this);
        }
        else if (id == R.id.nav_swipe_star) {
            ActivityManager.startStarSwipeActivity(this);
        }
        else if (id == R.id.nav_random) {
            ActivityManager.startRandomActivity(this);
        }
        else if (id == R.id.nav_update) {
            ActivityManager.startManageActivity(this);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * check gdb database update
     */
    private void checkUpdate() {
        GdbUpdateManager manager = new GdbUpdateManager(this, new GdbUpdateListener() {
            @Override
            public void onUpdateFinish() {

            }

            @Override
            public void onUpdateCancel() {

            }

            @Override
            public boolean consumeYes(AppCheckBean bean) {
                // 涉及到额外数据保存与存储，直接跳转至manage界面执行
                ActivityManager.startManageActivity(GHomeActivity.this);
                finish();
                return true;
            }
        });
        manager.setFragmentManagerV4(getSupportFragmentManager());
        manager.startCheck();
    }

    @Override
    public GdbGuidePresenter getPresenter() {
        return mPresenter;
    }

    @Override
    public void onRecommendRecordsLoaded() {

    }

    private void selectImage() {
        ActivityManager.startSurfLocalActivity(this, GdbConstants.REQUEST_SELECT_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GdbConstants.REQUEST_SELECT_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    String imagePath = data.getStringExtra(GdbConstants.DATA_SELECT_IMAGE);
                    SettingProperties.setGdbNavHeadRandom(false);
                    GdbImageProvider.saveNavHeadImage(imagePath);
                    focusOnFolder();
                }
                break;
        }
    }

}
