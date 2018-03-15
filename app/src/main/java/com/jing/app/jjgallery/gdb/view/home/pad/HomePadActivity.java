package com.jing.app.jjgallery.gdb.view.home.pad;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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
import com.jing.app.jjgallery.gdb.util.GlideUtil;
import com.jing.app.jjgallery.gdb.view.update.GdbUpdateListener;
import com.jing.app.jjgallery.gdb.view.update.GdbUpdateManager;

import butterknife.BindView;

/**
 * @desc
 * @auth 景阳
 * @time 2018/2/19 0019 10:45
 */

public class HomePadActivity extends GBaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    private ImageView navHeaderView;
    private ImageView ivFolder;
    private ImageView ivFace;

    private HomePadFragment homeFragment;

    @Override
    protected int getContentView() {
        return R.layout.activity_home_pad;
    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
        initDrawer();
        initContent();
    }

    @Override
    protected void initBackgroundWork() {
        checkUpdate();
    }

    private void initContent() {
        homeFragment = new HomePadFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.group_ft_container, homeFragment, "HomePadFragment");
        ft.commit();
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
                // 涉及到额外数据保存与存储，直接跳转至setting界面执行
                ActivityManager.startManageActivity(HomePadActivity.this);
                finish();
                return true;
            }
        });
        manager.setFragmentManagerV4(getSupportFragmentManager());
        manager.startCheck();
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

}
