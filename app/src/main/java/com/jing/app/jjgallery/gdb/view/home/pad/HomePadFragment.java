package com.jing.app.jjgallery.gdb.view.home.pad;

import android.animation.Animator;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jing.app.jjgallery.gdb.ActivityManager;
import com.jing.app.jjgallery.gdb.IFragmentHolder;
import com.jing.app.jjgallery.gdb.MvpFragmentV4;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.model.GdbImageProvider;
import com.jing.app.jjgallery.gdb.model.bean.StarProxy;
import com.jing.app.jjgallery.gdb.model.bean.recommend.FilterModel;
import com.jing.app.jjgallery.gdb.util.ColorUtils;
import com.jing.app.jjgallery.gdb.util.GlideUtil;
import com.jing.app.jjgallery.gdb.view.home.GHomeHeader;
import com.jing.app.jjgallery.gdb.view.home.HomeStarAdapter;
import com.jing.app.jjgallery.gdb.view.pub.AutoLoadMoreRecyclerView;
import com.jing.app.jjgallery.gdb.view.pub.cardslider.CardSnapHelper;
import com.jing.app.jjgallery.gdb.view.recommend.RecordFilterDialogFragment;
import com.king.app.gdb.data.entity.Record;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @desc
 * @auth 景阳
 * @time 2018/2/19 0019 13:53
 */

public class HomePadFragment extends MvpFragmentV4<HomePadPresenter> implements HomePadView, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.iv_rec1)
    ImageView ivRec1;
    @BindView(R.id.iv_rec2)
    ImageView ivRec2;
    @BindView(R.id.iv_rec3)
    ImageView ivRec3;
    @BindView(R.id.sr_refresh)
    SwipeRefreshLayout srRefresh;
    @BindView(R.id.rv_stars)
    RecyclerView rvStars;
    @BindView(R.id.tv_icon_record)
    TextView tvIconRecord;
    @BindView(R.id.tv_icon_star)
    TextView tvIconStar;
    @BindView(R.id.tv_icon_surf)
    TextView tvIconSurf;
    @BindView(R.id.rv_records)
    AutoLoadMoreRecyclerView rvRecords;

    private HomeStarAdapter adapter;

    private RequestOptions recordOption;

    private HomeRecordAdapter homeRecordAdapter;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_home_pad;
    }

    @Override
    protected void initView(View view) {
        srRefresh.setOnRefreshListener(this);

        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return homeRecordAdapter.getSpanSize(position);
            }
        });
        rvRecords.setLayoutManager(manager);
        rvRecords.setItemAnimator(new DefaultItemAnimator());
        rvRecords.setEnableLoadMore(true);
        rvRecords.setOnLoadMoreListener(new AutoLoadMoreRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                presenter.loadMore();
            }
        });

        updateIconBg(tvIconRecord);
        updateIconBg(tvIconStar);
        updateIconBg(tvIconSurf);
    }

    private void updateIconBg(TextView view) {
        GradientDrawable drawable = (GradientDrawable) view.getBackground();
        drawable.setColor(ColorUtils.randomWhiteTextBgColor());
        view.setBackground(drawable);
    }

    @Override
    public void notifyMoreRecords(int startOffset) {
        homeRecordAdapter.notifyItemInserted(startOffset);
    }

    @Override
    protected HomePadPresenter createPresenter() {
        return new HomePadPresenter();
    }

    @Override
    protected void initData() {
        recordOption = GlideUtil.getRecordAnimOptions();
        presenter.loadHomeData();
    }

    @Override
    public void postShowStars(final List<StarProxy> stars) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showStars(stars);
            }
        });
    }

    private void showStars(List<StarProxy> stars) {
        if (adapter == null) {
            adapter = new HomeStarAdapter();
            adapter.setOnStarListener(new GHomeHeader.OnStarListener() {
                @Override
                public void onStarGroupClicked() {

                }

                @Override
                public void onStarClicked(StarProxy starProxy) {
                    ActivityManager.startStarPageActivity(getActivity(), starProxy.getStar().getId());
                }
            });
            adapter.setList(stars);
            rvStars.setAdapter(adapter);
            // 只能attach一次
            new CardSnapHelper().attachToRecyclerView(rvStars);
        }
        else {
            adapter.setList(stars);
            adapter.notifyDataSetChanged();
        }

        rvStars.scrollToPosition(0);
    }

    @Override
    public void postShowRecommends(final List<Record> list) {
        if (srRefresh.isRefreshing()) {
            srRefresh.setRefreshing(false);
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showRecommends(list);
            }
        });
    }

    private void showRecommends(List<Record> list) {
        startRevealView(1000);
        try {
            Glide.with(getActivity())
                    .load(GdbImageProvider.getRecordRandomPath(list.get(0).getName(), null))
                    .apply(recordOption)
                    .into(ivRec1);
            Glide.with(getActivity())
                    .load(GdbImageProvider.getRecordRandomPath(list.get(1).getName(), null))
                    .apply(recordOption)
                    .into(ivRec2);
            Glide.with(getActivity())
                    .load(GdbImageProvider.getRecordRandomPath(list.get(2).getName(), null))
                    .apply(recordOption)
                    .into(ivRec3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startRevealView(int animTime) {
        Animator anim = ViewAnimationUtils.createCircularReveal(srRefresh, (int) srRefresh.getX()
                , (int) srRefresh.getY() + srRefresh.getHeight(), 0, (float) Math.hypot(srRefresh.getWidth(), srRefresh.getHeight()));
        anim.setDuration(animTime);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }

    @Override
    public void showRecords(List<Object> list) {
        if (homeRecordAdapter == null) {
            homeRecordAdapter = new HomeRecordAdapter();
            homeRecordAdapter.setList(list);
            homeRecordAdapter.setOnListListener(new HomeRecordAdapter.OnListListener() {
                @Override
                public void onLoadMore() {

                }

                @Override
                public void onClickItem(View view, Record record) {
                    ActivityManager.startRecordPadActivity(getActivity(), record);
                }
            });
            rvRecords.setAdapter(homeRecordAdapter);
        }
        else {
            homeRecordAdapter.setList(list);
            homeRecordAdapter.notifyDataSetChanged();
        }
    }

    @OnClick({R.id.iv_rec1, R.id.iv_rec2, R.id.iv_rec3})
    public void onClickRecommend(View view) {
        Record record = null;
        switch (view.getId()) {
            case R.id.iv_rec1:
                record = presenter.getRecommendedRecord(0);
                break;
            case R.id.iv_rec2:
                record = presenter.getRecommendedRecord(1);
                break;
            case R.id.iv_rec3:
                record = presenter.getRecommendedRecord(2);
                break;
        }
        ActivityManager.startRecordPadActivity(getActivity(), record);
    }

    @OnClick({R.id.group_record, R.id.group_star, R.id.group_surf, R.id.fab_setting, R.id.fab_top})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_setting:
                showRecommendSetting();
                break;
            case R.id.fab_top:
                rvRecords.scrollToPosition(0);
                break;
            case R.id.group_record:
                ActivityManager.startRecordListPadActivity(getActivity());
                break;
            case R.id.group_star:
                ActivityManager.startStarPadActivity(getActivity());
                break;
            case R.id.group_surf:
                ActivityManager.startSurfHttpActivity(getActivity());
                break;
        }
    }

    private void showRecommendSetting() {
        RecordFilterDialogFragment filterDialog = new RecordFilterDialogFragment();
        filterDialog.setOnRecordFilterActionListener(new RecordFilterDialogFragment.OnRecordFilterActionListener() {
            @Override
            public void onSaveFilterModel(FilterModel model) {
                presenter.resetTimer();
                presenter.refreshRec();
            }
        });
        filterDialog.show(getChildFragmentManager(), "RecordFilterDialogFragment");
    }

    @Override
    public void onRefresh() {
        presenter.refreshRecAndStars();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
    }
}
