package com.jing.app.jjgallery.gdb.view.home;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Pair;
import android.view.View;

import com.jing.app.jjgallery.gdb.ActivityManager;
import com.jing.app.jjgallery.gdb.BaseFragmentV4;
import com.jing.app.jjgallery.gdb.IFragmentHolder;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.model.bean.StarProxy;
import com.jing.app.jjgallery.gdb.util.DisplayHelper;
import com.jing.app.jjgallery.gdb.view.pub.AutoLoadMoreRecyclerView;
import com.king.app.gdb.data.entity.Record;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 描述: 本页面的设计为纵向展示
 * top recommend
 * star recommend
 * game
 * record list(这里设计为上拉加载N条，可以无限加载下去)
 *
 * 在做record list的功能时，一开始是将recycler view嵌套在NestedScrollView布局中
 * ，整个布局为NestedScrollView纵向排列上述4个布局。
 * 在调试过程中发现，几次load more之后，程序就因为out of memory而崩溃。
 * 原因是嵌套后的recycler view没有执行自身的覆用holder机制，调试的时候发现比如在10条增加至20条后
 * ，onCreateViewHolder执行了将近20次，同样的，20条增至30条，onCreateViewHolder执行了将近30次，也
 * 导致onBindViewHolder在每一次刷新都将所有的item进行了刷新，所以太多的加载加密图片任务导致了程序
 * 的卡顿和崩溃
 *
 * 所以，考虑到性能必须让recycler view执行其覆用Holder的机制
 * 就将top recommend， star recommend， game部分作为recycler view的header 部分
 * 整个页面不用scroll父布局，直接用recycler view的整体滑动
 *
 * <p/>作者：景阳
 * <p/>创建时间: 2017/5/19 13:48
 */
public class GHomeFragment extends BaseFragmentV4 implements IHomeView, GHomeRecordListAdapter.OnListListener
    , SwipeRefreshLayout.OnRefreshListener{

    @BindView(R.id.sr_refresh)
    SwipeRefreshLayout srRefresh;
    @BindView(R.id.rv_records)
    AutoLoadMoreRecyclerView rvRecords;

    private IHomeHolder homeHolder;

    private GHomeRecordListAdapter listAdapter;

    private GHomeBean homeBean;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_home_v4;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);

        srRefresh.setOnRefreshListener(this);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvRecords.setLayoutManager(manager);
        rvRecords.setItemAnimator(new DefaultItemAnimator());
        rvRecords.setEnableLoadMore(true);
        rvRecords.setOnLoadMoreListener(new AutoLoadMoreRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                homeHolder.getPresenter().loadMore(GHomeFragment.this);
            }
        });

        loadHomeData();
    }

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {
        homeHolder = (IHomeHolder) holder;
    }

    private void loadHomeData() {
        homeHolder.getPresenter().loadHomeData(this);
    }

    @Override
    public void onHomeDataLoaded(GHomeBean bean) {

        homeBean = bean;

        if (listAdapter == null) {
            listAdapter = new GHomeRecordListAdapter(bean.getRecordList());
            listAdapter.setHomeBean(homeBean);
            listAdapter.setFragmentManager(getChildFragmentManager());
            listAdapter.setOnListListener(this);
            listAdapter.setOnStarListener(new GHomeHeader.OnStarListener() {
                @Override
                public void onStarGroupClicked() {
                    if (DisplayHelper.isTabModel(getActivity())) {
                        ActivityManager.startStarPadActivity(getActivity());
                    }
                    else {
                        ActivityManager.startStarListActivity(getActivity());
                    }
                }

                @Override
                public void onStarClicked(StarProxy starProxy) {
                    if (DisplayHelper.isTabModel(getActivity())) {
                        ActivityManager.startStarPageActivity(getActivity(), starProxy.getStar().getId());
                    }
                    else {
                        ActivityManager.startStarActivity(getActivity(), starProxy.getStar());
                    }
                }
            });
            rvRecords.setAdapter(listAdapter);
        }
        else {
            listAdapter.setHomeBean(homeBean);
            listAdapter.updateList(bean.getRecordList());
            listAdapter.notifyDataSetChanged();
        }

        srRefresh.setRefreshing(false);
    }

    @Override
    public void onMoreRecordsLoaded(List<Record> list) {
        int originSize = listAdapter.getItemCount();
        homeBean.getRecordList().addAll(list);
        // notifyDataSetChanged会有闪屏现象，用notifyItemRangeInserted结合默认动画就比较理想了
//        listAdapter.notifyDataSetChanged();
        listAdapter.notifyItemRangeInserted(originSize - 1, list.size());
    }

    @Override
    public void onHomeDataLoadFailed(String message) {
        showToastLong("Load home data error: " + message);
    }

    @Override
    public void onLoadMore() {
        homeHolder.getPresenter().loadMore(this);
    }

    @Override
    public void onClickItem(View view, Record record) {
        // set anchor views of transition animation
        Pair<View, String>[] pairs = new Pair[1];
        pairs[0] = Pair.create(view.findViewById(R.id.iv_record_image), getString(R.string.anim_record_page_img));
        ActivityManager.startRecordActivity(getActivity(), record, pairs);
    }

    @Override
    public void onRefresh() {
        loadHomeData();
    }

    @OnClick(R.id.fab_top)
    public void onGoTop() {
        rvRecords.scrollToPosition(0);
    }
}
