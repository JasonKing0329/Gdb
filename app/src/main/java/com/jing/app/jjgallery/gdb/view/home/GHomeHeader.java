package com.jing.app.jjgallery.gdb.view.home;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jing.app.jjgallery.gdb.ActivityManager;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.model.bean.StarProxy;
import com.jing.app.jjgallery.gdb.view.pub.cardslider.CardSnapHelper;
import com.jing.app.jjgallery.gdb.view.recommend.RecommendFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/5/20 0020.
 */

public class GHomeHeader extends RecyclerView.ViewHolder {

    @BindView(R.id.rv_stars)
    RecyclerView rvStars;

    private OnStarListener onStarListener;

    private HomeStarAdapter adapter;

    public GHomeHeader(View itemView, FragmentManager fragmentManager, OnStarListener onStarListener) {
        super(itemView);
        this.onStarListener = onStarListener;
        ButterKnife.bind(this, itemView);
        initRecommentd(fragmentManager);
    }

    private void initRecommentd(FragmentManager fragmentManager) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        RecommendFragment fragment = new RecommendFragment();
        ft.add(R.id.group_recommend, fragment, "RecommendFragment");
        ft.commit();
    }

    public void bindView(GHomeBean bean) {

        if (adapter == null) {
            adapter = new HomeStarAdapter();
            adapter.setOnStarListener(onStarListener);
            adapter.setList(bean.getStarList());
            rvStars.setAdapter(adapter);
            // 只能attach一次
            new CardSnapHelper().attachToRecyclerView(rvStars);
        }
        else {
            adapter.setList(bean.getStarList());
            adapter.notifyDataSetChanged();
        }

        rvStars.scrollToPosition(0);
    }

    @OnClick({R.id.group_recommend, R.id.group_starlist, R.id.group_game, R.id.group_surf})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.group_recommend:
                ActivityManager.startRecordListActivity((Activity) view.getContext());
                break;
            case R.id.group_starlist:
                onStarListener.onStarGroupClicked();
                break;
            case R.id.group_game:
                ActivityManager.startGameActivity((Activity) view.getContext());
                break;
            case R.id.group_surf:
                ActivityManager.startSurfHttpActivity((Activity) view.getContext());
                break;
        }
    }

    public interface OnStarListener {
        void onStarGroupClicked();
        void onStarClicked(StarProxy starProxy);
    }
}
