package com.jing.app.jjgallery.gdb.view.star;

import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.jing.app.jjgallery.gdb.ActivityManager;
import com.jing.app.jjgallery.gdb.GdbConstants;
import com.jing.app.jjgallery.gdb.IFragmentHolder;
import com.jing.app.jjgallery.gdb.MvpFragmentV4;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.model.bean.StarProxy;
import com.jing.app.jjgallery.gdb.presenter.star.StarListPresenter;
import com.jing.app.jjgallery.gdb.util.ScreenUtils;
import com.jing.app.jjgallery.gdb.view.adapter.StarListCircleAdapter;
import com.jing.app.jjgallery.gdb.view.adapter.StarRichAdapter;
import com.jing.app.jjgallery.gdb.view.pub.FitSideBar;
import com.jing.app.jjgallery.gdb.view.pub.dialog.StarRatingDialog;

import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by Administrator on 2016/7/30 0030.
 */
public class StarListFragment extends MvpFragmentV4<StarListPresenter> implements OnStarClickListener, StarListView {

    private static final String ARG_STAR_TYPE = "star_type";
    @BindView(R.id.sidebar)
    FitSideBar sidebar;
    @BindView(R.id.tv_index_popup)
    TextView tvIndexPopup;
    @BindView(R.id.rv_list)
    RecyclerView rvList;

    public static StarListFragment newInstance(String type) {
        StarListFragment fragment = new StarListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_STAR_TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    private StarListCircleAdapter mCircleAdapter;
    private StarRichAdapter mRichAdapter;

    private IStarListHolder holder;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {
        this.holder = (IStarListHolder) holder;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_star_rich;
    }

    @Override
    protected void initView(View view) {

        presenter.setStarType(getArguments().getString(ARG_STAR_TYPE));

        rvList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                // 按音序排列，在滑动过程中显示当前的详细index
                if (presenter.getSortType() == GdbConstants.STAR_SORT_NAME) {
                    switch (newState) {
                        case RecyclerView.SCROLL_STATE_DRAGGING:
                            updateDetailIndex();
                            break;
                        case RecyclerView.SCROLL_STATE_SETTLING:
                            break;
                        default:
                            holder.hideDetailIndex();
                            break;
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                //在这里进行第二次滚动（最后的距离）
                if (needMove) {
                    needMove = false;
                    //获取要置顶的项在当前屏幕的位置，mIndex是记录的要置顶项在RecyclerView中的位置
                    int n = nSelection - ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                    if (n >= 0 && n < recyclerView.getChildCount()) {
                        recyclerView.scrollBy(0, recyclerView.getChildAt(n).getTop()); //滚动到顶部
                    }
                }

                if (recyclerView.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
                    updateDetailIndex();
                }
            }
        });

        sidebar.setOnSidebarStatusListener(new FitSideBar.OnSidebarStatusListener() {
            @Override
            public void onChangeFinished() {
                tvIndexPopup.setVisibility(View.GONE);
            }

            @Override
            public void onSideIndexChanged(String index) {
                int selection = presenter.getLetterPosition(index);
                scrollToPosition(selection);

                tvIndexPopup.setText(index);
                tvIndexPopup.setVisibility(View.VISIBLE);
            }
        });
    }

    private boolean needMove;
    private int nSelection;

    private void scrollToPosition(int selection) {
        nSelection = selection;
        final LinearLayoutManager manager = (LinearLayoutManager) rvList.getLayoutManager();
        int fir = manager.findFirstVisibleItemPosition();
        int end = manager.findLastVisibleItemPosition();
        if (selection <= fir) {
            rvList.scrollToPosition(selection);
        } else if (selection <= end) {
            int top = rvList.getChildAt(selection - fir).getTop();
            rvList.scrollBy(0, top);
        } else {
            //当要置顶的项在当前显示的最后一项的后面时
            rvList.scrollToPosition(selection);
            //记录当前需要在RecyclerView滚动监听里面继续第二次滚动
            needMove = true;
        }
    }

    @Override
    protected StarListPresenter createPresenter() {
        return new StarListPresenter();
    }

    @Override
    protected void initData() {
        presenter.loadStarList();
    }

    @Override
    public FitSideBar getSidebar() {
        return sidebar;
    }

    @Override
    public void showRichList(List<StarProxy> list, Map<Long, Boolean> mExpandMap) {
        rvList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvList.removeItemDecoration(richDecoration);
        rvList.addItemDecoration(richDecoration);

        mRichAdapter = new StarRichAdapter();
        mRichAdapter.setList(list);
        mRichAdapter.setExpandMap(mExpandMap);
        mRichAdapter.setOnStarClickListener(this);
        rvList.setAdapter(mRichAdapter);
    }

    private RecyclerView.ItemDecoration richDecoration = new RecyclerView.ItemDecoration() {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.top = ScreenUtils.dp2px(10);
        }
    };

    @Override
    public void showCircleList(List<StarProxy> list) {
        rvList.removeItemDecoration(richDecoration);
        int column = 2;
        rvList.setLayoutManager(new GridLayoutManager(getActivity(), column));

        mCircleAdapter = new StarListCircleAdapter();
        mCircleAdapter.setList(list);
        mCircleAdapter.setOnStarClickListener(this);
        rvList.setAdapter(mCircleAdapter);
    }

    @Override
    public void notifyCircleUpdated() {
        mCircleAdapter.notifyDataSetChanged();
    }

    @Override
    public void notifyRichUpdated() {
        mRichAdapter.notifyDataSetChanged();
    }

    private void updateDetailIndex() {
        int position = -1;
        RecyclerView.LayoutManager manager = rvList.getLayoutManager();
        if (manager instanceof LinearLayoutManager) {
            LinearLayoutManager lm = (LinearLayoutManager) manager;
            position = lm.findFirstVisibleItemPosition();
        } else if (manager instanceof GridLayoutManager) {
            GridLayoutManager gm = (GridLayoutManager) manager;
            position = gm.findFirstVisibleItemPosition();
        }
        String name = null;
        if (position != -1) {
            name = presenter.getDetailIndex(position);
        }
        if (!TextUtils.isEmpty(name)) {
            holder.updateDetailIndex(name);
        }
    }

    @Override
    public void onStarLongClick(StarProxy star) {
        ActivityManager.startStarPageActivity(getActivity(), star.getStar().getId());
    }

    @Override
    public void onStarClick(StarProxy star) {
        if (holder != null && holder.dispatchClickStar(star.getStar())) {
            return;
        }
        ActivityManager.startStarActivity(getActivity(), star.getStar());
    }

    @Override
    public void onUpdateRating(Long starId) {
        StarRatingDialog dialog = new StarRatingDialog();
        dialog.setStarId(starId);
        dialog.setOnDismissListener(dialog1 -> {
            if (mCircleAdapter != null) {
                mCircleAdapter.notifyStarChanged(starId);
            }
            if (mRichAdapter != null) {
                mRichAdapter.notifyStarChanged(starId);
            }
        });
        dialog.show(getChildFragmentManager(), "StarRatingDialog");
    }

    public void filterStar(String text) {
        presenter.filter(text);
    }

    public void updateSortType(int sortMode) {
        presenter.sortStarList(sortMode);
    }

    /**
     * circle/rich视图变化
     */
    public void onViewModeChanged() {
        if (presenter != null) {
            presenter.loadStarList();
        }
    }

    public void onRefresh(int sortType) {
        if (presenter != null) {
            presenter.setSortType(sortType);
            presenter.loadStarList();
        }
    }

    public boolean isNotScrolling() {
        return rvList.getScrollState() == RecyclerView.SCROLL_STATE_IDLE;
    }

    public void goTop() {
        rvList.scrollToPosition(0);
    }

    public void showSideBar(boolean show) {
        sidebar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void toggleSidebar() {
        sidebar.setVisibility(sidebar.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
    }

    public void setExpandAll(boolean expand) {
        if (presenter != null) {
            presenter.setExpandAll(expand);
            if (mCircleAdapter != null) {
                mCircleAdapter.notifyDataSetChanged();
            }
            if (mRichAdapter != null) {
                mRichAdapter.notifyDataSetChanged();
            }
        }
    }
}
