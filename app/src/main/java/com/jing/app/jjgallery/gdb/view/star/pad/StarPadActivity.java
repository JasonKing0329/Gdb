package com.jing.app.jjgallery.gdb.view.star.pad;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jing.app.jjgallery.gdb.GdbConstants;
import com.jing.app.jjgallery.gdb.MvpActivity;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.model.SettingProperties;
import com.jing.app.jjgallery.gdb.view.pub.WaveSideBarView;
import com.jing.app.jjgallery.gdb.view.record.common.RecordCommonListFragment;
import com.jing.app.jjgallery.gdb.view.star.IStarListHolder;
import com.jing.app.jjgallery.gdb.view.star.StarListFragment;
import com.jing.app.jjgallery.gdb.view.star.StarListPagerAdapter;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.param.DataConstants;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @desc
 * @auth 景阳
 * @time 2018/2/13 0013 11:06
 */

public class StarPadActivity extends MvpActivity<StarPadPresenter> implements StarPadView, IStarListHolder {

    private final String[] titles = new String[]{
            "All", "1", "0", "0.5"
    };

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    @BindView(R.id.viewpager)
    ViewPager viewpager;

    @BindView(R.id.tv_index)
    TextView tvIndex;
    @BindView(R.id.iv_icon_sort)
    ImageView ivIconSort;
    @BindView(R.id.iv_icon_mode)
    ImageView ivIconMode;
    @BindView(R.id.iv_icon_num)
    ImageView ivIconNum;
    @BindView(R.id.iv_icon_index)
    ImageView ivIconIndex;
    @BindView(R.id.side_bar)
    WaveSideBarView sideBar;

    private int curSortMode;

    private StarListPagerAdapter pagerAdapter;

    /**
     * 控制detail index显示的timer
     */
    private Disposable indexDisposable;
    private String curDetailIndex;

    private RecordCommonListFragment ftRecord;

    @Override
    protected int getContentView() {
        return R.layout.activity_star_pad;
    }

    @Override
    protected void onResume() {
        super.onResume();
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
    protected void initView() {
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pagerAdapter.getItem(position).reloadStarList(curSortMode);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        sideBar.setOnTouchLetterChangeListener(new WaveSideBarView.OnTouchLetterChangeListener() {
            @Override
            public void onLetterChange(String letter) {
                pagerAdapter.getItem(viewpager.getCurrentItem()).onLetterChange(letter);
            }
        });

        initRecordFragment();

        if (SettingProperties.isStarPadRecordsCardMode()) {
            ivIconMode.setImageResource(R.drawable.ic_panorama_horizontal_3f51b5_36dp);
        }
        else {
            ivIconMode.setImageResource(R.drawable.ic_panorama_vertical_3f51b5_36dp);
        }
    }

    private void initRecordFragment() {
        ftRecord = new RecordCommonListFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.group_ft_record, ftRecord, "RecordCommonListFragment")
                .commit();
    }

    @Override
    protected StarPadPresenter createPresenter() {
        return new StarPadPresenter();
    }

    @Override
    protected void initData() {
        curSortMode = GdbConstants.STAR_SORT_NAME;
        presenter.loadTitles(curSortMode);
    }

    public void changeSideBarVisible() {
        sideBar.setVisibility(sideBar.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        invalidateSideBar();
    }

    private void invalidateSideBar() {
        if (sideBar.getVisibility() == View.VISIBLE) {
            // post刷新mSideBarView，根据调试发现重写初始化后WaveSideBarView会重新执行onMeasure(width,height=0)->onDraw->onMeasure(width,height=正确值)
            // 缺少重新onDraw的过程，因此通过delay执行mSideBarView.invalidate()可以激活onDraw事件，根据正确的值重新绘制
            // 用mSideBarView.post/postDelayed总是不准确
            sideBar.post(new Runnable() {
                @Override
                public void run() {
                    sideBar.requestLayout();
                    sideBar.invalidate();
                }
            });
        }
    }

    @Override
    public void showTitles(int all, int top, int bottom, int half) {
        if (pagerAdapter == null) {
            pagerAdapter = new StarListPagerAdapter(getSupportFragmentManager());
            StarListFragment fragmentAll = new StarListFragment();
            fragmentAll.setStarMode(DataConstants.STAR_MODE_ALL);
            fragmentAll.setSortMode(curSortMode);
            pagerAdapter.addFragment(fragmentAll, titles[0] + "(" + all + ")");
            StarListFragment fragment1 = new StarListFragment();
            fragment1.setStarMode(DataConstants.STAR_MODE_TOP);
            fragment1.setSortMode(curSortMode);
            pagerAdapter.addFragment(fragment1, titles[1] + "(" + top + ")");
            StarListFragment fragment0 = new StarListFragment();
            fragment0.setStarMode(DataConstants.STAR_MODE_BOTTOM);
            fragment0.setSortMode(curSortMode);
            pagerAdapter.addFragment(fragment0, titles[2] + "(" + bottom + ")");
            StarListFragment fragment05 = new StarListFragment();
            fragment05.setStarMode(DataConstants.STAR_MODE_HALF);
            fragment05.setSortMode(curSortMode);
            pagerAdapter.addFragment(fragment05, titles[3] + "(" + half + ")");
            viewpager.setAdapter(pagerAdapter);
            tabLayout.addTab(tabLayout.newTab().setText(titles[0]));
            tabLayout.addTab(tabLayout.newTab().setText(titles[1]));
            tabLayout.addTab(tabLayout.newTab().setText(titles[2]));
            tabLayout.addTab(tabLayout.newTab().setText(titles[3]));
            tabLayout.setupWithViewPager(viewpager);
        } else {
            tabLayout.removeAllTabs();
            tabLayout.addTab(tabLayout.newTab().setText(titles[0] + "(" + all + ")"));
            tabLayout.addTab(tabLayout.newTab().setText(titles[1] + "(" + top + ")"));
            tabLayout.addTab(tabLayout.newTab().setText(titles[2] + "(" + bottom + ")"));
            tabLayout.addTab(tabLayout.newTab().setText(titles[3] + "(" + half + ")"));
        }
    }

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
     *
     * @param name
     * @return
     */
    private String getAvailableIndex(String name) {
        if (name.length() > 2) {
            return name.substring(0, 3);
        } else if (name.length() > 1) {
            return name.substring(0, 2);
        } else if (name.length() > 0) {
            return name.substring(0, 1);
        }
        return "";
    }

    @Override
    public boolean dispatchClickStar(Star star) {
        ftRecord.showStarRecords(star);
        return true;
    }

    @Override
    protected void onDestroy() {
        if (indexDisposable != null) {
            indexDisposable.dispose();
        }
        super.onDestroy();
    }

    @OnClick({R.id.iv_icon_sort, R.id.iv_icon_mode, R.id.iv_icon_num, R.id.iv_icon_index})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_icon_sort:
                ftRecord.onClickSort();
                break;
            case R.id.iv_icon_mode:
                if (SettingProperties.isStarPadRecordsCardMode()) {
                    SettingProperties.setStarPadRecordsCardMode(false);
                    ivIconMode.setImageResource(R.drawable.ic_panorama_vertical_3f51b5_36dp);
                }
                else {
                    SettingProperties.setStarPadRecordsCardMode(true);
                    ivIconMode.setImageResource(R.drawable.ic_panorama_horizontal_3f51b5_36dp);
                }
                ftRecord.refresh();
                break;
            case R.id.iv_icon_index:
                changeSideBarVisible();
                break;
            case R.id.iv_icon_num:
                if (view.isSelected()) {
                    view.setSelected(false);
                    ivIconIndex.setVisibility(View.VISIBLE);
                    curSortMode = GdbConstants.STAR_SORT_NAME;
                    changeSideBarVisible();
                } else {
                    view.setSelected(true);
                    ivIconIndex.setVisibility(View.GONE);
                    curSortMode = GdbConstants.STAR_SORT_RECORDS;
                    if (sideBar.getVisibility() == View.VISIBLE) {
                        sideBar.setVisibility(View.GONE);
                    }
                }
                pagerAdapter.getItem(viewpager.getCurrentItem()).reloadStarList(curSortMode);
                // 更新tabLayout的数据，回调在onStarCountLoaded
                presenter.loadTitles(curSortMode);
                break;
        }
    }
}
