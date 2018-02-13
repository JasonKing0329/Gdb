package com.jing.app.jjgallery.gdb.view.game;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.jing.app.jjgallery.gdb.GBaseActivity;
import com.jing.app.jjgallery.gdb.R;

import butterknife.BindView;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/11/7 13:34
 */
public class RandomActivity extends GBaseActivity {

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewpager;

    private RandomAdapter adapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_random;
    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {

        getSupportActionBar().hide();

        adapter = new RandomAdapter(getSupportFragmentManager());
        adapter.addFragment(new RandomNumFragment());
        adapter.addFragment(new RandomStarFragment());
        viewpager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewpager);

    }

    @Override
    protected void initBackgroundWork() {

    }

}
