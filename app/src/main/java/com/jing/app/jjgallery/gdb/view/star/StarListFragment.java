package com.jing.app.jjgallery.gdb.view.star;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.jing.app.jjgallery.gdb.ActivityManager;
import com.jing.app.jjgallery.gdb.GdbConstants;
import com.jing.app.jjgallery.gdb.IFragmentHolder;
import com.jing.app.jjgallery.gdb.MvpFragmentV4;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.model.SettingProperties;
import com.jing.app.jjgallery.gdb.model.bean.StarProxy;
import com.jing.app.jjgallery.gdb.model.conf.PreferenceValue;
import com.jing.app.jjgallery.gdb.presenter.star.StarListPresenter;
import com.jing.app.jjgallery.gdb.util.DisplayHelper;
import com.jing.app.jjgallery.gdb.view.adapter.StarListAdapter;
import com.jing.app.jjgallery.gdb.view.adapter.StarListCircleAdapter;
import com.jing.app.jjgallery.gdb.view.adapter.StarListGridAdapter;
import com.jing.app.jjgallery.gdb.view.adapter.StarListNumAdapter;
import com.jing.app.jjgallery.gdb.view.pub.PinnedHeaderDecoration;
import com.jing.app.jjgallery.gdb.view.pub.dialog.StarRatingDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/30 0030.
 */
public class StarListFragment extends MvpFragmentV4<StarListPresenter> implements OnStarClickListener, StarListView {

    @BindView(R.id.rv_star)
    RecyclerView rvStar;

    private int mSortMode;

    private StarListAdapter mNameAdapter;
    private StarListGridAdapter mGridAdapter;
    private StarListNumAdapter mNumberAdapter;
    private StarListCircleAdapter mCircleAdapter;
    private IStarListHolder holder;

    private int currentViewMode;

    // see GDBProperties.STAR_MODE_XXX
    private String curStarMode;
    // see GdbConstants.STAR_SORT_XXX
    private int curSortMode;

    public void setStarMode(String curStarMode) {
        this.curStarMode = curStarMode;
    }

    public void setSortMode(int curSortMode) {
        this.curSortMode = curSortMode;
    }

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {
        this.holder = (IStarListHolder) holder;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.page_gdb_starlist;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);

        if (DisplayHelper.isTabModel(getActivity())) {
            currentViewMode = PreferenceValue.STAR_LIST_VIEW_CIRCLE;
        }
        else {
            currentViewMode = SettingProperties.getStarListViewMode();
        }

        rvStar.setLayoutManager(new LinearLayoutManager(getActivity()));

        final PinnedHeaderDecoration decoration = new PinnedHeaderDecoration();
        decoration.registerTypePinnedHeader(1, new PinnedHeaderDecoration.PinnedHeaderCreator() {
            @Override
            public boolean create(RecyclerView parent, int adapterPosition) {
                return true;
            }
        });
        rvStar.addItemDecoration(decoration);

        rvStar.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                // 按音序排列，在滑动过程中显示当前的详细index
                if (curSortMode == GdbConstants.STAR_SORT_NAME) {
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
                if (recyclerView.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
                    updateDetailIndex();
                }
            }
        });
    }

    @Override
    protected StarListPresenter createPresenter() {
        return new StarListPresenter();
    }

    @Override
    protected void initData() {

        presenter.loadStarList(curStarMode, curSortMode);
    }

    private void updateDetailIndex() {
        int position = -1;
        RecyclerView.LayoutManager manager = rvStar.getLayoutManager();
        if (manager instanceof LinearLayoutManager) {
            LinearLayoutManager lm = (LinearLayoutManager) manager;
            position = lm.findFirstVisibleItemPosition();
        }
        else if (manager instanceof GridLayoutManager) {
            GridLayoutManager gm = (GridLayoutManager) manager;
            position = gm.findFirstVisibleItemPosition();
        }
        String name = null;
        if (position != -1) {
            if (currentViewMode == PreferenceValue.STAR_LIST_VIEW_GRID) {
                if (mGridAdapter != null) {
                    name = mGridAdapter.getItem(position).getStar().getName();
                }
            }
            else if (currentViewMode == PreferenceValue.STAR_LIST_VIEW_CIRCLE) {
                if (mCircleAdapter != null) {
                    name = mCircleAdapter.getItem(position).getStar().getName();
                }
            }
            else if (mSortMode == GdbConstants.STAR_SORT_RECORDS) {
                if (mNumberAdapter != null) {
                    name = mNumberAdapter.getItem(position).getStar().getName();
                }
            }
            else {
                if (mNameAdapter != null) {
                    name = mNameAdapter.getItem(position).getStar().getName();
                }
            }
        }
        if (!TextUtils.isEmpty(name)) {
            holder.updateDetailIndex(name);
        }
    }

    public void onLetterChange(String letter) {
        if (currentViewMode == PreferenceValue.STAR_LIST_VIEW_GRID) {
            if (curSortMode == GdbConstants.STAR_SORT_NAME) {
                int pos = mGridAdapter.getLetterPosition(letter);

                if (pos != -1) {
                    rvStar.scrollToPosition(pos);
                }
            }
        }
        else if (currentViewMode == PreferenceValue.STAR_LIST_VIEW_CIRCLE) {
            if (curSortMode == GdbConstants.STAR_SORT_NAME) {
                int pos = mCircleAdapter.getLetterPosition(letter);

                if (pos != -1) {
                    rvStar.scrollToPosition(pos);
                }
            }
        }
        else {
            int pos = mNameAdapter.getLetterPosition(letter);

            if (pos != -1) {
                rvStar.scrollToPosition(pos);
            }
        }
    }

    @Override
    public void onLoadStarList(List<StarProxy> list) {
        if (currentViewMode == PreferenceValue.STAR_LIST_VIEW_GRID) {
            mGridAdapter = new StarListGridAdapter(list);
            mGridAdapter.setPresenter(presenter);
            mGridAdapter.setOnStarClickListener(this);

            int column = 2;
            if (DisplayHelper.isTabModel(getActivity())) {
                column = 3;
            }
            rvStar.setLayoutManager(new GridLayoutManager(getActivity(), column));
            rvStar.setAdapter(mGridAdapter);
        }
        else if (currentViewMode == PreferenceValue.STAR_LIST_VIEW_CIRCLE) {
            mCircleAdapter = new StarListCircleAdapter(list);
            mCircleAdapter.setPresenter(presenter);
            mCircleAdapter.setOnStarClickListener(this);

            int column = 2;
            rvStar.setLayoutManager(new GridLayoutManager(getActivity(), column));
            rvStar.setAdapter(mCircleAdapter);
        }
        else {
            rvStar.setLayoutManager(new LinearLayoutManager(getActivity()));
            if (mSortMode == GdbConstants.STAR_SORT_RECORDS) {
                mNumberAdapter = new StarListNumAdapter(list);
                mNumberAdapter.setPresenter(presenter);
                mNumberAdapter.setOnStarClickListener(this);
                rvStar.setAdapter(mNumberAdapter);
            } else {
                mNameAdapter = new StarListAdapter(getActivity(), list);
                mNameAdapter.setPresenter(presenter);
                mNameAdapter.setOnStarClickListener(this);
                rvStar.setAdapter(mNameAdapter);
            }
        }
    }

    @Override
    public void onLoadStarError(String message) {
        showToastLong(message);
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
        dialog.show(getChildFragmentManager(), "StarRatingDialog");
    }

    public void filterStar(String text) {
        // 平板
        if (mCircleAdapter != null) {
            mCircleAdapter.onStarFilter(text);
        }
        // 网格视图
        if (mGridAdapter != null) {
            mGridAdapter.onStarFilter(text);
        }
        // 列表视图
        if (mNameAdapter != null) {
            mNameAdapter.onStarFilter(text);
        }
    }

    public void reloadStarList(int sortMode) {
        if (curSortMode != sortMode) {
            curSortMode = sortMode;
            presenter.loadStarList(curStarMode, curSortMode);
        }
    }

    public void refreshList() {
        mNameAdapter.notifyDataSetChanged();
    }

    /**
     * 菜单网格/列表视图变化
     */
    public void onViewModeChanged() {
        if (currentViewMode != SettingProperties.getStarListViewMode()) {
            currentViewMode = SettingProperties.getStarListViewMode();
            updateViewMode();
        }
    }

    private void updateViewMode() {
        if (holder != null) {
            presenter.loadStarList(curStarMode, curSortMode);
        }
    }

    public boolean isNotScrolling() {
        return rvStar.getScrollState() == RecyclerView.SCROLL_STATE_IDLE;
    }

    public void goTop() {
        rvStar.scrollToPosition(0);
    }
}
