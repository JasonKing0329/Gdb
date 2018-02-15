package com.jing.app.jjgallery.gdb.view.record.common;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jing.app.jjgallery.gdb.ActivityManager;
import com.jing.app.jjgallery.gdb.IFragmentHolder;
import com.jing.app.jjgallery.gdb.MvpFragmentV4;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.model.SettingProperties;
import com.jing.app.jjgallery.gdb.util.ScreenUtils;
import com.jing.app.jjgallery.gdb.view.adapter.OnRecordItemClickListener;
import com.jing.app.jjgallery.gdb.view.adapter.RecordCardAdapter;
import com.jing.app.jjgallery.gdb.view.adapter.RecordsListAdapter;
import com.jing.app.jjgallery.gdb.view.record.SortDialogFragment;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.Star;

import java.util.List;

import butterknife.BindView;

/**
 * @desc
 * @auth 景阳
 * @time 2018/2/13 0013 14:49
 */

public class RecordCommonListFragment extends MvpFragmentV4<RecordCommonListPresenter> implements RecordCommonListView {

    @BindView(R.id.rv_records)
    RecyclerView rvRecords;

    private RecordsListAdapter mListAdapter;

    private RecordCardAdapter mCardAdapter;

    private RecyclerView.ItemDecoration mCardDecoration;
    private RecyclerView.ItemDecoration mListDecoration;

    private int currentSortMode = -1;
    private boolean currentSortDesc = true;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_record_list_common;
    }

    @Override
    protected void initView(View view) {
        mCardDecoration = new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                if (position % 2 == 0) {
                    outRect.left = ScreenUtils.dp2px(20);
                }
                outRect.top = ScreenUtils.dp2px(10);
                outRect.bottom = ScreenUtils.dp2px(10);
            }
        };
        mListDecoration = new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.left = ScreenUtils.dp2px(20);
                outRect.top = ScreenUtils.dp2px(5);
                outRect.bottom = ScreenUtils.dp2px(5);
            }
        };
    }

    @Override
    protected RecordCommonListPresenter createPresenter() {
        return new RecordCommonListPresenter();
    }

    @Override
    protected void initData() {
        currentSortMode = SettingProperties.getStarPadRecordOrderMode();
        currentSortDesc = SettingProperties.isStarPadRecordOrderDesc();
    }

    @Override
    public void showRecords(List<Record> list) {
        if (SettingProperties.isStarPadRecordsCardMode()) {
            GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
            rvRecords.setLayoutManager(manager);
            rvRecords.removeItemDecoration(mCardDecoration);
            rvRecords.removeItemDecoration(mListDecoration);
            rvRecords.addItemDecoration(mCardDecoration);

            if (mCardAdapter == null) {
                mCardAdapter = new RecordCardAdapter();
                mCardAdapter.setCurrentStar(presenter.getStar());
                mCardAdapter.setRecordList(list);
                mCardAdapter.setOnCardActionListener(new RecordCardAdapter.OnCardActionListener() {
                    @Override
                    public void onClickCardItem(View v, Record record) {
                        ActivityManager.startRecordPadActivity(getActivity(), record);
                    }
                });
                rvRecords.setAdapter(mCardAdapter);
            }
            else {
                mCardAdapter.setCurrentStar(presenter.getStar());
                mCardAdapter.setRecordList(list);
                rvRecords.setAdapter(mCardAdapter);
            }
        }
        else {
            LinearLayoutManager manager = new LinearLayoutManager(getActivity());
            manager.setOrientation(LinearLayoutManager.VERTICAL);
            rvRecords.setLayoutManager(manager);
            rvRecords.removeItemDecoration(mCardDecoration);
            rvRecords.removeItemDecoration(mListDecoration);
            rvRecords.addItemDecoration(mListDecoration);

            if (mListAdapter == null) {
                mListAdapter = new RecordsListAdapter(getActivity(), list);
                mListAdapter.setSortMode(currentSortMode);
                mListAdapter.setItemClickListener(new OnRecordItemClickListener() {
                    @Override
                    public void onClickRecordItem(View v, Record record) {
                        ActivityManager.startRecordPadActivity(getActivity(), record);
                    }
                });
                rvRecords.setAdapter(mListAdapter);
            }
            else {
                mListAdapter.setSortMode(currentSortMode);
                mListAdapter.setRecordList(list);
                rvRecords.setAdapter(mListAdapter);
            }
        }
    }

    public void showStarRecords(Star star) {
        presenter.loadRecords(star, currentSortMode, currentSortDesc);
    }

    public void onClickSort() {
        SortDialogFragment dialog = new SortDialogFragment();
        dialog.setDesc(currentSortDesc);
        dialog.setSortMode(currentSortMode);
        dialog.setOnSortListener(new SortDialogFragment.OnSortListener() {
            @Override
            public void onSort(boolean desc, int sortMode, boolean isIncludeDeprecated) {
                if (currentSortMode != sortMode || currentSortDesc != desc) {
                    currentSortMode = sortMode;
                    currentSortDesc = desc;
                    SettingProperties.setStarPadRecordOrderMode(currentSortMode);
                    SettingProperties.setStarPadRecordOrderDesc(currentSortDesc);
                    refresh();
                }
            }
        });
        dialog.show(getFragmentManager(), "SortDialogFragment");
    }

    public void refresh() {
        presenter.loadRecords(presenter.getStar(), currentSortMode, currentSortDesc);
    }
}
