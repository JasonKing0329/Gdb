package com.jing.app.jjgallery.gdb.view.star.pad;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.jing.app.jjgallery.gdb.FavorPopupMvpActivity;
import com.jing.app.jjgallery.gdb.GdbConstants;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.model.PadProperties;
import com.jing.app.jjgallery.gdb.model.SettingProperties;
import com.jing.app.jjgallery.gdb.model.conf.PreferenceValue;
import com.jing.app.jjgallery.gdb.view.record.common.RecordCommonListFragment;
import com.jing.app.jjgallery.gdb.view.record.common.RecordCommonListHolder;
import com.jing.app.jjgallery.gdb.view.star.IStarListHolder;
import com.jing.app.jjgallery.gdb.view.star.StarListFragment;
import com.jing.app.jjgallery.gdb.view.star.StarListPagerAdapter;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.param.DataConstants;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceAlignmentEnum;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.SimpleCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

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

public class StarPadActivity extends FavorPopupMvpActivity<StarPadPresenter> implements StarPadView, IStarListHolder, RecordCommonListHolder {

    private final String[] titles = new String[]{
            "All", "1", "0", "0.5"
    };

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    @BindView(R.id.viewpager)
    ViewPager viewpager;

    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_index)
    TextView tvIndex;
    @BindView(R.id.iv_icon_sort)
    ImageView ivIconSort;
    @BindView(R.id.iv_icon_mode)
    ImageView ivIconMode;
    @BindView(R.id.bmb_menu)
    BoomMenuButton bmbMenu;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.group_search)
    RelativeLayout groupSearch;
    @BindView(R.id.iv_more)
    ImageView ivMore;

    private int curSortMode;

    private StarListPagerAdapter pagerAdapter;

    /**
     * 控制detail index显示的timer
     */
    private Disposable indexDisposable;
    private String curDetailIndex;

    private RecordCommonListFragment ftRecord;

    private PopupMenu popupMenu;

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

        updateModeIcon();

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pagerAdapter.getItem(position).updateSortType(curSortMode);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        initRecordFragment();

        initBoomButton();

        groupSearch.setVisibility(View.GONE);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence text, int i, int i1, int i2) {
                pagerAdapter.getItem(viewpager.getCurrentItem()).filterStar(text.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ivMore.setOnClickListener(view -> showMore(view));
    }

    private void showMore(View view) {
        if (popupMenu == null) {
            popupMenu = new PopupMenu(this, view);
            loadMenu(popupMenu.getMenuInflater(), popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.menu_gdb_sidebar:
                        pagerAdapter.getItem(viewpager.getCurrentItem()).toggleSidebar();
                        break;
                    case R.id.menu_gdb_expand_all:
                        pagerAdapter.getItem(viewpager.getCurrentItem()).setExpandAll(true);
                        break;
                    case R.id.menu_gdb_collapse_all:
                        pagerAdapter.getItem(viewpager.getCurrentItem()).setExpandAll(false);
                        break;
                }
                return false;
            });
        }
        popupMenu.show();
    }

    private void loadMenu(MenuInflater menuInflater, Menu menu) {
        menu.clear();
        menuInflater.inflate(R.menu.gdb_star_list_pad, menu);
    }

    private void updateModeIcon() {
        if (PadProperties.getStarRecordViewMode() == PreferenceValue.PAD_STAR_RECORDS_GRID2) {
            ivIconMode.setImageResource(R.drawable.ic_dns_white_36dp);
        }
        else {
            ivIconMode.setImageResource(R.drawable.ic_view_module_white_36dp);
        }
    }

    private void initRecordFragment() {
        ftRecord = new RecordCommonListFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.group_ft_record, ftRecord, "RecordCommonListFragment")
                .commit();
    }

    private void initBoomButton() {
        // 修改了源码，image自适应为button的一半中间，不需要再设置imagePadding了
//        int padding = bmbMenu.getContext().getResources().getDimensionPixelSize(R.dimen.boom_menu_icon_padding);
        int radius = bmbMenu.getContext().getResources().getDimensionPixelSize(R.dimen.boom_menu_btn_radius);
        bmbMenu.setButtonEnum(ButtonEnum.SimpleCircle);
        bmbMenu.setButtonRadius(radius);
        bmbMenu.setPiecePlaceEnum(PiecePlaceEnum.DOT_5_1);
        bmbMenu.setButtonPlaceEnum(ButtonPlaceEnum.Vertical);
        bmbMenu.setButtonPlaceAlignmentEnum(ButtonPlaceAlignmentEnum.BL);
        bmbMenu.setButtonLeftMargin(getResources().getDimensionPixelSize(R.dimen.home_pop_menu_left));
        bmbMenu.setButtonBottomMargin(getResources().getDimensionPixelSize(R.dimen.home_pop_menu_bottom));
        bmbMenu.setButtonVerticalMargin(getResources().getDimensionPixelSize(R.dimen.boom_menu_btn_margin_ver));
        bmbMenu.addBuilder(new SimpleCircleButton.Builder()
                .normalImageRes(R.drawable.ic_view_quilt_white_36dp)
                .buttonRadius(radius)
                .listener(bmClickListener));
        bmbMenu.addBuilder(new SimpleCircleButton.Builder()
                .normalImageRes(R.drawable.ic_sort_by_alpha_white_36dp)
                .buttonRadius(radius)
                .listener(bmClickListener));
        bmbMenu.addBuilder(new SimpleCircleButton.Builder()
                .normalImageRes(R.drawable.ic_looks_one_white_36dp)
                .buttonRadius(radius)
//                .imagePadding(new Rect(padding, padding, padding, padding))
                .listener(bmClickListener));
        bmbMenu.addBuilder(new SimpleCircleButton.Builder()
                .normalImageRes(R.drawable.ic_favorite_white_36dp)
                .buttonRadius(radius)
                .listener(bmClickListener));
        bmbMenu.addBuilder(new SimpleCircleButton.Builder()
                .normalImageRes(R.drawable.ic_arrow_upward_white_36dp)
                .buttonRadius(radius)
                .listener(bmClickListener));
    }

    @Override
    protected StarPadPresenter createPresenter() {
        return new StarPadPresenter();
    }

    @Override
    protected void initData() {
        curSortMode = GdbConstants.STAR_SORT_NAME;
        presenter.loadTitles();
    }

    public void showSideBar() {
        pagerAdapter.getItem(viewpager.getCurrentItem()).showSideBar(true);
    }

    OnBMClickListener bmClickListener = new OnBMClickListener() {
        @Override
        public void onBoomButtonClick(int index) {

            switch (index) {
                case 0:// name
                    if (SettingProperties.getStarListViewMode() == PreferenceValue.STAR_LIST_VIEW_RICH) {
                        SettingProperties.setStarListViewMode(PreferenceValue.STAR_LIST_VIEW_CIRCLE);
                    }
                    else {
                        SettingProperties.setStarListViewMode(PreferenceValue.STAR_LIST_VIEW_RICH);
                    }
                    pagerAdapter.onViewModeChanged();
                    break;
                case 1:// name
                    curSortMode = GdbConstants.STAR_SORT_NAME;
                    showSideBar();
                    pagerAdapter.getItem(viewpager.getCurrentItem()).updateSortType(curSortMode);
                    // 更新tabLayout的数据，回调在onStarCountLoaded
                    presenter.loadTitles();
                    break;
                case 2:// number
                    curSortMode = GdbConstants.STAR_SORT_RECORDS;
                    pagerAdapter.getItem(viewpager.getCurrentItem()).updateSortType(curSortMode);
                    // 更新tabLayout的数据，回调在onStarCountLoaded
                    presenter.loadTitles();
                    break;
                case 3:// rating
                    curSortMode = GdbConstants.STAR_SORT_RATING;
                    pagerAdapter.getItem(viewpager.getCurrentItem()).updateSortType(curSortMode);
                    // 更新tabLayout的数据，回调在onStarCountLoaded
                    presenter.loadTitles();
                    break;
                case 4:// go top
                    pagerAdapter.getItem(viewpager.getCurrentItem()).goTop();
                    break;
            }
        }
    };

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
        tvName.setText(star.getName());
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

    @OnClick({R.id.iv_icon_sort, R.id.iv_icon_search, R.id.iv_icon_close, R.id.iv_icon_mode})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_icon_sort:
                ftRecord.onClickSort();
                break;
            case R.id.iv_icon_search:
                if (groupSearch.getVisibility() != View.VISIBLE) {
                    showSearchLayout();
                    tabLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.iv_icon_close:
                etSearch.setText("");
                closeSearch();
                break;
            case R.id.iv_icon_mode:
                if (PadProperties.getStarRecordViewMode() == PreferenceValue.PAD_STAR_RECORDS_GRID2) {
                    PadProperties.setStarRecordViewMode(PreferenceValue.PAD_STAR_RECORDS_GRID1);
                }
                else {
                    PadProperties.setStarRecordViewMode(PreferenceValue.PAD_STAR_RECORDS_GRID2);
                }
                updateModeIcon();
                ftRecord.refresh();
                break;
        }
    }

    /**
     * hide search layout
     */
    public void closeSearch() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.disappear);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                groupSearch.setVisibility(View.INVISIBLE);
                tabLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        groupSearch.startAnimation(animation);
    }

    /**
     * show search layout
     */
    private void showSearchLayout() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.appear);
        groupSearch.startAnimation(animation);
        groupSearch.setVisibility(View.VISIBLE);
    }

    @Override
    public void showRecordPopup(View anchor, Record record) {
        getFavorPopup().popupRecord(this, anchor, record.getId());
    }
}
