package com.jing.app.jjgallery.gdb.view.favor.record;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jing.app.jjgallery.gdb.ActivityManager;
import com.jing.app.jjgallery.gdb.MvpHolderFragmentV4;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.model.PadProperties;
import com.jing.app.jjgallery.gdb.view.adapter.OnRecordItemClickListener;
import com.jing.app.jjgallery.gdb.view.adapter.RecordsGridAdapter;
import com.jing.app.jjgallery.gdb.view.record.SortDialogFragment;
import com.king.app.gdb.data.entity.Record;

import java.util.List;

import butterknife.BindView;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/15 15:43
 */
public class RecordItemFragment extends MvpHolderFragmentV4<RecordItemPresenter, RecordItemHolder> implements RecordItemView {

    private static final String KEY_ORDER_ID = "order_id";

    @BindView(R.id.rv_items)
    RecyclerView rvItems;

    private RecordsGridAdapter mGridAdapter;

    private int currentSortMode = -1;
    private boolean currentSortDesc = true;

    public static RecordItemFragment newInstance(long orderId) {
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_ORDER_ID, orderId);
        RecordItemFragment fragment = new RecordItemFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_record_order_item;
    }

    @Override
    protected void initView(View view) {
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 4);
        rvItems.setLayoutManager(manager);
    }

    @Override
    protected RecordItemPresenter createPresenter() {
        return new RecordItemPresenter();
    }

    @Override
    protected void initData() {
        currentSortMode = PadProperties.getRecordOrderItemSortType();
        currentSortDesc = PadProperties.isRecordOrderItemSortDesc();
        reload();
    }

    public void showOrder(long orderId) {
        getArguments().putLong(KEY_ORDER_ID, orderId);
        reload();
    }

    @Override
    public void showOrderItems(List<Record> records) {
        if (mGridAdapter == null) {
            mGridAdapter = new RecordsGridAdapter(records);
            mGridAdapter.setSortMode(currentSortMode);
            mGridAdapter.setItemClickListener(new OnRecordItemClickListener() {
                @Override
                public void onClickRecordItem(View v, Record record) {
                    ActivityManager.startRecordPadActivity(getActivity(), record);
                }

                @Override
                public void onPopupMenu(View v, Record record) {

                }
            });
            rvItems.setAdapter(mGridAdapter);
        }
        else {
            mGridAdapter.setSortMode(currentSortMode);
            mGridAdapter.setRecordList(records);
            mGridAdapter.notifyDataSetChanged();
        }
    }

    public void showSelectMode(boolean show) {
        mGridAdapter.setSelectMode(show);
        mGridAdapter.notifyDataSetChanged();
    }

    public void deleteSelectedItems() {
        presenter.deleteItem(mGridAdapter.getSelectedItems());
    }

    @Override
    public void deleteDone(boolean notifyRefresh) {
        mGridAdapter.setSelectMode(false);
        if (notifyRefresh) {
            mGridAdapter.notifyDataSetChanged();
        }

        long orderId = getArguments().getLong(KEY_ORDER_ID);
        if (holder != null) {
            holder.notifyOrderChanged(orderId);
        }
    }

    public void changeSortType() {
        SortDialogFragment dialog = new SortDialogFragment();
        dialog.setDesc(currentSortDesc);
        dialog.setSortMode(currentSortMode);
        dialog.setOnSortListener(new SortDialogFragment.OnSortListener() {
            @Override
            public void onSort(boolean desc, int sortMode) {
                if (currentSortMode != sortMode || currentSortDesc != desc) {
                    currentSortMode = sortMode;
                    currentSortDesc = desc;
                    reload();
                }
            }
        });
        dialog.show(getChildFragmentManager(), "SortDialogFragment");
    }

    private void reload() {
        long orderId = getArguments().getLong(KEY_ORDER_ID);
        presenter.loadOrder(orderId, currentSortMode, currentSortDesc);
    }

}
