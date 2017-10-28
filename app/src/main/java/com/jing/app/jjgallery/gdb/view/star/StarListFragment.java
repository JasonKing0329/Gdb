package com.jing.app.jjgallery.gdb.view.star;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jing.app.jjgallery.gdb.ActivityManager;
import com.jing.app.jjgallery.gdb.BaseFragmentV4;
import com.jing.app.jjgallery.gdb.GdbConstants;
import com.jing.app.jjgallery.gdb.IFragmentHolder;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.model.SettingProperties;
import com.jing.app.jjgallery.gdb.model.bean.StarProxy;
import com.jing.app.jjgallery.gdb.model.conf.PreferenceValue;
import com.jing.app.jjgallery.gdb.util.DisplayHelper;
import com.jing.app.jjgallery.gdb.view.adapter.StarListAdapter;
import com.jing.app.jjgallery.gdb.view.adapter.StarListGridAdapter;
import com.jing.app.jjgallery.gdb.view.adapter.StarListNumAdapter;
import com.jing.app.jjgallery.gdb.view.pub.PinnedHeaderDecoration;
import com.jing.app.jjgallery.gdb.view.pub.ProgressProvider;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/30 0030.
 */
public class StarListFragment extends BaseFragmentV4 implements OnStarClickListener, IStarListView {

    @BindView(R.id.rv_star)
    RecyclerView rvStar;

    private int mSortMode;

    private StarListAdapter mNameAdapter;
    private StarListGridAdapter mGridAdapter;
    private StarListNumAdapter mNumberAdapter;
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

        currentViewMode = SettingProperties.getStarListViewMode();

        rvStar.setLayoutManager(new LinearLayoutManager(getActivity()));

        final PinnedHeaderDecoration decoration = new PinnedHeaderDecoration();
        decoration.registerTypePinnedHeader(1, new PinnedHeaderDecoration.PinnedHeaderCreator() {
            @Override
            public boolean create(RecyclerView parent, int adapterPosition) {
                return true;
            }
        });
        rvStar.addItemDecoration(decoration);

        holder.getPresenter().loadStarList(curStarMode, curSortMode, this);
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
            mGridAdapter.setPresenter(holder.getPresenter());
            mGridAdapter.setOnStarClickListener(this);

            int column = 2;
            if (DisplayHelper.isTabModel(getActivity())) {
                column = 3;
            }
            rvStar.setLayoutManager(new GridLayoutManager(getActivity(), column));
            rvStar.setAdapter(mGridAdapter);
        }
        else {
            rvStar.setLayoutManager(new LinearLayoutManager(getActivity()));
            if (mSortMode == GdbConstants.STAR_SORT_RECORDS) {
                mNumberAdapter = new StarListNumAdapter(list);
                mNumberAdapter.setPresenter(holder.getPresenter());
                mNumberAdapter.setOnStarClickListener(this);
                rvStar.setAdapter(mNumberAdapter);
            } else {
                mNameAdapter = new StarListAdapter(getActivity(), list);
                mNameAdapter.setPresenter(holder.getPresenter());
                mNameAdapter.setOnStarClickListener(this);
                rvStar.setAdapter(mNameAdapter);
            }
        }
    }

    @Override
    public void onLoadStarError(String message) {
        if (getActivity() instanceof ProgressProvider) {
            ((ProgressProvider) getActivity()).showToastLong(message);
        }
    }

    @Override
    public void onStarClick(StarProxy star) {
        ActivityManager.startStarActivity(getActivity(), star.getStar());
    }

    public void filterStar(String text) {
        if (mNameAdapter != null) {
            mNameAdapter.onStarFilter(text);
        }
    }

    public void reloadStarList(int sortMode) {
        if (curSortMode != sortMode) {
            curSortMode = sortMode;
            holder.getPresenter().loadStarList(curStarMode, curSortMode, this);
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
        if (holder != null && holder.getPresenter() != null) {
            holder.getPresenter().loadStarList(curStarMode, curSortMode, this);
        }
    }
}
