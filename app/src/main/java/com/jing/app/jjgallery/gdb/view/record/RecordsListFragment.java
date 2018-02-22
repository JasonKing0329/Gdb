package com.jing.app.jjgallery.gdb.view.record;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Pair;
import android.view.View;

import com.jing.app.jjgallery.gdb.ActivityManager;
import com.jing.app.jjgallery.gdb.IFragmentHolder;
import com.jing.app.jjgallery.gdb.MvpFragmentV4;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.model.SettingProperties;
import com.jing.app.jjgallery.gdb.presenter.record.RecordListPresenter;
import com.jing.app.jjgallery.gdb.util.DebugLog;
import com.jing.app.jjgallery.gdb.util.DisplayHelper;
import com.jing.app.jjgallery.gdb.view.adapter.OnRecordItemClickListener;
import com.jing.app.jjgallery.gdb.view.adapter.RecordsGridAdapter;
import com.jing.app.jjgallery.gdb.view.adapter.RecordsListAdapter;
import com.jing.app.jjgallery.gdb.view.pub.AutoLoadMoreRecyclerView;
import com.king.app.gdb.data.entity.Record;

import java.util.List;

import butterknife.BindView;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/5/24 9:59
 */
public class RecordsListFragment extends MvpFragmentV4<RecordListPresenter> implements IRecordListView, OnRecordItemClickListener {

    @BindView(R.id.rv_records)
    AutoLoadMoreRecyclerView rvRecords;

    private IRecordListHolder holder;
    private RecordsListAdapter mAdapter;
    private RecordsGridAdapter mGridAdapter;

    private int currentSortMode = -1;
    private boolean currentSortDesc = true;
    private boolean showDeprecated = true;
    private boolean showCanBePlayed;

    private List<Record> recordList;
    /**
     * search input keywords
     */
    private String keywords;
    /**
     * scene keywords
     */
    private String keyScene;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {
        this.holder = (IRecordListHolder) holder;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.page_gdb_recordlist;
    }

    @Override
    protected void initView(View view) {

        if (DisplayHelper.isTabModel(getActivity())) {
            GridLayoutManager manager = new GridLayoutManager(getActivity(), 3);
            rvRecords.setLayoutManager(manager);
            rvRecords.setEnableLoadMore(true);
            //3列的倍数
            presenter.updateDefaultLoad(21);
        }
        else {
            rvRecords.setLayoutManager(new LinearLayoutManager(getActivity()));
            rvRecords.setEnableLoadMore(true);
        }
        rvRecords.setOnLoadMoreListener(loadMoreListener);

    }

    private AutoLoadMoreRecyclerView.OnLoadMoreListener loadMoreListener = new AutoLoadMoreRecyclerView.OnLoadMoreListener() {
        @Override
        public void onLoadMore() {
            // showCanBePlayed情况下已加载全部
            if (!showCanBePlayed) {
                loadMoreRecords();
            }
        }
    };

    @Override
    protected RecordListPresenter createPresenter() {
        return new RecordListPresenter();
    }

    @Override
    protected void initData() {
        currentSortMode = SettingProperties.getGdbRecordOrderMode(getActivity());
        // 加载records
        loadNewRecords();
    }

    /**
     * actionbar 输入字符
     */
    public void filterRecord(String text) {
        if (mAdapter != null || mGridAdapter != null) {
            this.keywords = text;
            loadNewRecords();
        }
    }

    /**
     * 修改排序类型、关键词变化，重新加载list
     */
    public void loadNewRecords() {
        // 重新加载records
        presenter.loadRecordList(currentSortMode, currentSortDesc, showDeprecated, showCanBePlayed
                , keywords, keyScene);
    }

    /**
     * loadNewRecords 回调
     * @param list
     */
    @Override
    public void showRecordList(List<Record> list) {
        this.recordList = list;
        // activity已结束
        if (getActivity() == null || getActivity().isDestroyed()) {
            DebugLog.e("activity finished");
            return;
        }
        if (DisplayHelper.isTabModel(getActivity())) {
            if (mGridAdapter == null) {
                mGridAdapter = new RecordsGridAdapter(list);
                mGridAdapter.setSortMode(currentSortMode);
                mGridAdapter.setItemClickListener(this);
                rvRecords.setAdapter(mGridAdapter);
            }
            else {
                mGridAdapter.setRecordList(list);
                mGridAdapter.setSortMode(currentSortMode);
                mGridAdapter.notifyDataSetChanged();
            }
        }
        else {
            if (mAdapter == null) {
                mAdapter = new RecordsListAdapter(getActivity(), list);
                mAdapter.setSortMode(currentSortMode);
                mAdapter.setItemClickListener(this);
                rvRecords.setAdapter(mAdapter);
            }
            else {
                mAdapter.setRecordList(list);
                mAdapter.setSortMode(currentSortMode);
                mAdapter.notifyDataSetChanged();
            }
        }
        // 回到顶端
        rvRecords.scrollToPosition(0);
    }


    /**
     * 不改变排序模式、不改变关键词，仅在滑动到底部后自动加载更多
     */
    private void loadMoreRecords() {
        // 加到当前size后
        presenter.loadMoreRecords(currentSortMode, currentSortDesc, showDeprecated, showCanBePlayed
                , keywords, keyScene);
    }

    /**
     * loadMoreRecords 回调
     * @param list
     */
    @Override
    public void showMoreRecords(List<Record> list) {
        int originSize;
        if (DisplayHelper.isTabModel(getActivity())) {
            originSize = mGridAdapter.getItemCount();
            recordList.addAll(list);
            mGridAdapter.notifyItemRangeInserted(originSize - 1, list.size());
        }
        else {
            originSize = mAdapter.getItemCount();
            recordList.addAll(list);
            mAdapter.notifyItemRangeInserted(originSize - 1, list.size());
        }
    }

    public void changeSortType() {
        SortDialogFragment dialog = new SortDialogFragment();
        dialog.setDesc(currentSortDesc);
        dialog.setSortMode(currentSortMode);
        dialog.setOnSortListener(new SortDialogFragment.OnSortListener() {
            @Override
            public void onSort(boolean desc, int sortMode, boolean isIncludeDeprecated) {
                if (currentSortMode != sortMode || currentSortDesc != desc || showDeprecated != isIncludeDeprecated) {
                    currentSortMode = sortMode;
                    currentSortDesc = desc;
                    showDeprecated = isIncludeDeprecated;
                    SettingProperties.setGdbRecordOrderMode(getActivity(), currentSortMode);
                    loadNewRecords();
                }
            }
        });
        dialog.show(getFragmentManager(), "SortDialogFragment");
    }

    public void refreshList() {
        mAdapter.notifyDataSetChanged();
    }

    public void showCanPlayList(boolean canPlay) {
        showCanBePlayed = canPlay;
        loadNewRecords();
    }

    @Override
    public void onClickRecordItem(View v, Record record) {
        // set anchor views of transition animation
        Pair<View, String>[] pairs = new Pair[3];
        pairs[0] = Pair.create(v.findViewById(R.id.record_thumb), getString(R.string.anim_record_page_img));
        pairs[1] = Pair.create(v.findViewById(R.id.record_score), getString(R.string.anim_record_page_score));
        pairs[2] = Pair.create(v.findViewById(R.id.record_scene), getString(R.string.anim_record_page_scene));
        if (DisplayHelper.isTabModel(getActivity())) {
            ActivityManager.startRecordPadActivity(getActivity(), record, pairs);
        }
        else {
            ActivityManager.startRecordActivity(getActivity(), record, pairs);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        loadNewRecords();
    }

    public void setScene(String scene) {
        this.keyScene = scene;
    }

}
