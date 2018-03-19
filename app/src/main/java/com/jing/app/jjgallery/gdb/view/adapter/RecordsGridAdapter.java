package com.jing.app.jjgallery.gdb.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;

import com.king.app.gdb.data.entity.Record;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JingYang on 2016/8/2 0002.
 * Description:
 */
public class RecordsGridAdapter extends RecyclerView.Adapter<RecordGridHolder> implements View.OnClickListener {

    private List<Record> recordList;
    private OnRecordItemClickListener itemClickListener;

    private int sortMode;
    private boolean selectMode;

    private SparseBooleanArray checkMap;

    public RecordsGridAdapter(List<Record> list) {
        this.recordList = list;
        checkMap = new SparseBooleanArray();
    }

    public void setRecordList(List<Record> recordList) {
        this.recordList = recordList;
    }

    public void setItemClickListener(OnRecordItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    /**
     * @param sortMode PreferenceValue.GDB_SR_ORDERBY_XXX
     */
    public void setSortMode(int sortMode) {
        this.sortMode = sortMode;
    }

    @Override
    public RecordGridHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecordGridHolder holder = new RecordGridHolder(parent);
        holder.setParameters(this, popupListener);
        holder.setSelectMode(selectMode);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecordGridHolder holder, int position) {
        holder.setSortMode(sortMode);
        holder.setSelectMode(selectMode);
        holder.bind(recordList.get(position), position, checkMap.get(position));
    }

    @Override
    public int getItemCount() {
        return recordList == null ? 0:recordList.size();
    }

    @Override
    public void onClick(View v) {
        if (selectMode) {
            int position = (int) v.getTag();
            if (checkMap.get(position)) {
                checkMap.put(position, false);
            }
            else {
                checkMap.put(position, true);
            }
            notifyItemChanged(position);
        }
        else {
            if (itemClickListener != null) {
                Record record = (Record) v.getTag();
                itemClickListener.onClickRecordItem(v, record);
            }
        }
    }

    View.OnClickListener popupListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                Record record = (Record) v.getTag();
                itemClickListener.onPopupMenu(v, record);
            }
        }
    };

    public void setSelectMode(boolean selectMode) {
        this.selectMode = selectMode;
        checkMap.clear();
    }

    public List<Record> getSelectedItems() {
        List<Record> list = new ArrayList<>();
        for (int i = 0; i < getItemCount(); i ++) {
            if (checkMap.get(i)) {
                list.add(recordList.get(i));
            }
        }
        return list;
    }
}
