package com.jing.app.jjgallery.gdb.view.star.phone;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.allure.lbanners.LMBanners;
import com.allure.lbanners.adapter.LBaseAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jing.app.jjgallery.gdb.ActivityManager;
import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.GdbConstants;
import com.jing.app.jjgallery.gdb.MvpActivity;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.model.GdbImageProvider;
import com.jing.app.jjgallery.gdb.model.SettingProperties;
import com.jing.app.jjgallery.gdb.model.conf.PreferenceValue;
import com.jing.app.jjgallery.gdb.util.GlideUtil;
import com.jing.app.jjgallery.gdb.util.LMBannerViewUtil;
import com.jing.app.jjgallery.gdb.view.pub.ActionBar;
import com.jing.app.jjgallery.gdb.view.pub.BannerAnimDialogFragment;
import com.jing.app.jjgallery.gdb.view.star.IStarListHolder;
import com.jing.app.jjgallery.gdb.view.star.StarListFragment;
import com.jing.app.jjgallery.gdb.view.star.StarListPagerAdapter;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.param.DataConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/7/12 9:33
 */
public class StarPhoneActivity extends MvpActivity<StarPhonePresenter> implements IStarListHolder, StarPhoneView {

    private final String[] titles = new String[]{
            "All", "1", "0", "0.5"
    };

    @BindView(R.id.lmbanner)
    LMBanners lmBanners;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.tv_index)
    TextView tvIndex;

    private StarListPagerAdapter pagerAdapter;

    private ActionBar actionBar;
    private BannerAnimDialogFragment bannerSettingDialog;

    private int curSortMode;

    /**
     * 控制detail index显示的timer
     */
    private Disposable indexDisposable;
    private String curDetailIndex;

    @Override
    public int getContentView() {
        getSupportActionBar().hide();
        return R.layout.activity_gdb_star_list;
    }

    @Override
    protected void initView() {
        initActionbar();
        initBanner();

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pagerAdapter.getItem(position).onRefresh(curSortMode);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected StarPhonePresenter createPresenter() {
        return new StarPhonePresenter();
    }

    @Override
    protected void initData() {
        curSortMode = GdbConstants.STAR_SORT_NAME;
        // load favor list for banner, 回调在onFavorListLoaded
        presenter.loadFavorList();
        // 查询tabLayout的数据，回调在onStarCountLoaded
        presenter.loadTitles(curSortMode);
    }

    private void initActionbar() {
        actionBar = new ActionBar(this, findViewById(R.id.group_actionbar));
        actionBar.setActionIconListener(iconListener);
        actionBar.setActionMenuListener(menuListener);
        actionBar.setActionSearchListener(searchListener);
        actionBar.clearActionIcon();
        actionBar.addMenuIcon();
        actionBar.addSearchIcon();
        actionBar.addBackIcon();
        actionBar.addSortByNumIcon();
        actionBar.addFavorIcon();
        actionBar.addIndexIcon();
        actionBar.setTitle(getString(R.string.gdb_title_star));
    }

    private void initBanner() {
        // 禁用btnStart(只在onPageScroll触发后有效)
        lmBanners.isGuide(false);
        // 不显示引导圆点
        lmBanners.hideIndicatorLayout();
        // 轮播切换时间
        lmBanners.setDurtion(SettingProperties.getGdbStarListNavAnimTime(this));
        if (SettingProperties.isGdbStarListNavAnimRandom(this)) {
            Random random = new Random();
            int type = Math.abs(random.nextInt()) % LMBannerViewUtil.ANIM_TYPES.length;
            LMBannerViewUtil.setScrollAnim(lmBanners, type);
        }
        else {
            LMBannerViewUtil.setScrollAnim(lmBanners, SettingProperties.getGdbStarListNavAnimType(this));
        }
    }

    @Override
    public void onFavorListLoaded() {

        progress.setVisibility(View.GONE);

        // show banner

        // 采用getView时生成随机推荐，这里初始化3个item就够了（LMBanner内部也是根据view pager设置下标
        // 来循环的）
        List<Star> list = new ArrayList<>();
        list.add(new Star());
        list.add(new Star());
        list.add(new Star());
        HeadBannerAdapter adapter = new HeadBannerAdapter();
        lmBanners.setAdapter(adapter, list);

        // 这里一定要加载完后再设置可见，因为LMBanners的内部代码里有一个btnStart，本来是受isGuide的控制
        // 但是1.0.8版本里只在onPageScroll里面判断了这个属性。导致如果一开始LMBanners处于可见状态，
        // adapter里还没有数据，btnStart就会一直显示在那里，知道开始触发onPageScroll才会隐藏
        // 本来引入library，在setGuide把btnStart的visibility置为gone就可以了，但是这个项目已经引入了很多module了，就不再引入了
        lmBanners.setVisibility(View.VISIBLE);
    }

    @Override
    public void showTitles(int all, int top, int bottom, int half) {
        if (pagerAdapter == null) {
            pagerAdapter = new StarListPagerAdapter(getSupportFragmentManager());
            StarListFragment fragmentAll = StarListFragment.newInstance(DataConstants.STAR_MODE_ALL);
            pagerAdapter.addFragment(fragmentAll, titles[0] + "(" + all + ")");
            StarListFragment fragment1 = StarListFragment.newInstance(DataConstants.STAR_MODE_TOP);
            pagerAdapter.addFragment(fragment1, titles[1] + "(" + top + ")");
            StarListFragment fragment0 = StarListFragment.newInstance(DataConstants.STAR_MODE_BOTTOM);
            pagerAdapter.addFragment(fragment0, titles[2] + "(" + bottom + ")");
            StarListFragment fragment05 = StarListFragment.newInstance(DataConstants.STAR_MODE_HALF);
            pagerAdapter.addFragment(fragment05, titles[3] + "(" + half + ")");
            viewpager.setAdapter(pagerAdapter);
            tabLayout.addTab(tabLayout.newTab().setText(titles[0]));
            tabLayout.addTab(tabLayout.newTab().setText(titles[1]));
            tabLayout.addTab(tabLayout.newTab().setText(titles[2]));
            tabLayout.addTab(tabLayout.newTab().setText(titles[3]));
            tabLayout.setupWithViewPager(viewpager);
        }
        else {
            tabLayout.removeAllTabs();
            tabLayout.addTab(tabLayout.newTab().setText(titles[0] + "(" + all + ")"));
            tabLayout.addTab(tabLayout.newTab().setText(titles[1] + "(" + top + ")"));
            tabLayout.addTab(tabLayout.newTab().setText(titles[2] + "(" + bottom + ")"));
            tabLayout.addTab(tabLayout.newTab().setText(titles[3] + "(" + half + ")"));
        }
    }

    @Override
    public boolean dispatchClickStar(Star star) {
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (lmBanners != null) {
            lmBanners.stopImageTimerTask();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (indexDisposable != null) {
            indexDisposable.dispose();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (lmBanners != null) {
            lmBanners.startImageTimerTask();
        }

        // 控制tvIndex在切换显示列表后的隐藏状况
        indexDisposable = Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (tvIndex != null && tvIndex.getVisibility() == View.VISIBLE) {
                            if (viewpager != null && pagerAdapter != null) {
                                if (pagerAdapter.getItem(viewpager.getCurrentItem()).isNotScrolling()) {
                                    tvIndex.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (lmBanners != null) {
            lmBanners.clearImageTimerTask();
        }
    }

    private ActionBar.ActionIconListener iconListener = new ActionBar.ActionIconListener() {
        @Override
        public void onBack() {
            finish();
        }

        @Override
        public void onIconClick(View view) {
            switch (view.getId()) {
                case R.id.actionbar_sort_by_num:
                    if (view.isSelected()) {
                        view.setSelected(false);
                        curSortMode = GdbConstants.STAR_SORT_NAME;
                    } else {
                        view.setSelected(true);
                        findViewById(R.id.actionbar_favor).setSelected(false);
                        curSortMode = GdbConstants.STAR_SORT_RECORDS;
                    }
                    pagerAdapter.getItem(viewpager.getCurrentItem()).updateSortType(curSortMode);
                    // 更新tabLayout的数据，回调在onStarCountLoaded
                    presenter.loadTitles(curSortMode);
                    break;
                case R.id.actionbar_index:
                    changeSideBarVisible();
                    break;
                case R.id.actionbar_favor:
                    if (view.isSelected()) {
                        view.setSelected(false);
                        curSortMode = GdbConstants.STAR_SORT_NAME;
                    } else {
                        view.setSelected(true);
                        findViewById(R.id.actionbar_sort_by_num).setSelected(false);
                        curSortMode = GdbConstants.STAR_SORT_RATING;
                    }
                    pagerAdapter.getItem(viewpager.getCurrentItem()).updateSortType(curSortMode);
                    // 更新tabLayout的数据，回调在onStarCountLoaded
                    presenter.loadTitles(curSortMode);
                    break;
            }
        }
    };

    private ActionBar.ActionMenuListener menuListener = new ActionBar.ActionMenuListener() {
        @Override
        public void createMenu(MenuInflater menuInflater, Menu menu) {
            loadMenu(menuInflater, menu);
        }

        @Override
        public void onPrepareMenu(MenuInflater menuInflater, Menu menu) {
            loadMenu(menuInflater, menu);
        }

        private void loadMenu(MenuInflater menuInflater, Menu menu) {
            menu.clear();
            menuInflater.inflate(R.menu.gdb_star_list, menu);
            if (SettingProperties.getStarListViewMode() == PreferenceValue.STAR_LIST_VIEW_RICH) {
                menu.findItem(R.id.menu_gdb_view_mode).setTitle(R.string.menu_view_mode_circle);
            }
            else {
                menu.findItem(R.id.menu_gdb_view_mode).setTitle(R.string.menu_view_mode_rich);
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_gdb_view_mode:
                    if (item.getTitle().toString().equals(getString(R.string.menu_view_mode_rich))) {
                        SettingProperties.setStarListViewMode(PreferenceValue.STAR_LIST_VIEW_RICH);
                    }
                    else {
                        SettingProperties.setStarListViewMode(PreferenceValue.STAR_LIST_VIEW_CIRCLE);
                    }
                    pagerAdapter.onViewModeChanged();
                    break;
                case R.id.menu_gdb_expand_all:
                    pagerAdapter.getItem(viewpager.getCurrentItem()).setExpandAll(true);
                    break;
                case R.id.menu_gdb_collapse_all:
                    pagerAdapter.getItem(viewpager.getCurrentItem()).setExpandAll(false);
                    break;
            }
            return false;
        }
    };

    private ActionBar.ActionSearchListener searchListener = new ActionBar.ActionSearchListener() {
        @Override
        public void onTextChanged(String text, int start, int before, int count) {
            pagerAdapter.getItem(viewpager.getCurrentItem()).filterStar(text);
        }
    };

    @Override
    public void hideDetailIndex() {
        tvIndex.setVisibility(View.GONE);
    }

    @Override
    public void updateDetailIndex(String name) {
        if (tvIndex.getVisibility() != View.VISIBLE) {
            tvIndex.setVisibility(View.VISIBLE);
        }

        String newIndex = getAvailableIndex(name);
        if (!newIndex.equals(curDetailIndex)) {
            curDetailIndex = newIndex;
            tvIndex.setText(newIndex);
        }
    }

    /**
     * 最多支持3个字母
     * @param name
     * @return
     */
    private String getAvailableIndex(String name) {
        if (name.length() > 2) {
            return name.substring(0,3);
        }
        else if (name.length() > 1) {
            return name.substring(0,2);
        }
        else if (name.length() > 0) {
            return name.substring(0,1);
        }
        return "";
    }

    public void changeSideBarVisible() {
        pagerAdapter.getItem(viewpager.getCurrentItem()).toggleSidebar();
    }

    @Override
    public void onBackPressed() {
        if (actionBar != null && actionBar.isSearchVisible()) {
            actionBar.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    @OnClick({R.id.group_setting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.group_setting:
                showSettingDialog();
                break;
        }
    }

    private void showSettingDialog() {
        if (bannerSettingDialog == null) {
            bannerSettingDialog = new BannerAnimDialogFragment();
            bannerSettingDialog.setOnAnimSettingListener(new BannerAnimDialogFragment.OnAnimSettingListener() {

                @Override
                public void onRandomAnim(boolean random) {
                    SettingProperties.setGdbStarListNavAnimRandom(StarPhoneActivity.this, random);
                }

                @Override
                public boolean isRandomAnim() {
                    return SettingProperties.isGdbStarListNavAnimRandom(StarPhoneActivity.this);
                }

                @Override
                public int getAnimType() {
                    return SettingProperties.getGdbStarListNavAnimType(StarPhoneActivity.this);
                }

                @Override
                public void onSaveAnimType(int type) {
                    SettingProperties.setGdbStarListNavAnimType(StarPhoneActivity.this, type);
                }

                @Override
                public int getAnimTime() {
                    return SettingProperties.getGdbStarListNavAnimTime(StarPhoneActivity.this);
                }

                @Override
                public void onSaveAnimTime(int time) {
                    SettingProperties.setGdbStarListNavAnimTime(StarPhoneActivity.this, time);
                }

                @Override
                public void onParamsSaved() {
                    initBanner();
                }
            });
        }
        bannerSettingDialog.show(getSupportFragmentManager(), "BannerAnimDialogFragment");
    }

    private void onClickBannerItem(Star bean) {
        ActivityManager.startStarActivity(this, bean.getId());
    }

    private class HeadBannerAdapter implements LBaseAdapter<Star>, View.OnClickListener {

        private RequestOptions requestOptions;

        public HeadBannerAdapter() {
            requestOptions = GlideUtil.getRecordOptions();
        }

        @Override
        public View getView(LMBanners lBanners, Context context, int position, Star bean) {
            View view = LayoutInflater.from(context).inflate(R.layout.adapter_gdb_star_list_banner, null);

            bean = presenter.nextFavorStar();
            if (bean != null) {
                ImageView imageView = (ImageView) view.findViewById(R.id.iv_star);
                String path = GdbImageProvider.getStarRandomPath(bean.getName(), null);

                Glide.with(GdbApplication.getInstance())
                        .load(path)
                        .apply(requestOptions)
                        .into(imageView);

                RelativeLayout groupContainer = (RelativeLayout) view.findViewById(R.id.group_container);
                groupContainer.setTag(bean);
                groupContainer.setOnClickListener(this);
            }
            return view;
        }

        @Override
        public void onClick(View v) {
            Star bean = (Star) v.getTag();
            onClickBannerItem(bean);
        }

    }
}
