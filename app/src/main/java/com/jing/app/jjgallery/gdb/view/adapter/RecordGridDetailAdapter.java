package com.jing.app.jjgallery.gdb.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jing.app.jjgallery.gdb.R;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.Star;

import java.util.List;

/**
 * 描述: only for pad landscape
 * <p/>作者：景阳
 * <p/>创建时间: 2017/8/7 11:44
 */
public class RecordGridDetailAdapter extends RecyclerView.Adapter<RecordGridDetailHolder> {

    private List<Record> list;
    private Star currentStar;
    private OnDetailActionListener onDetailListener;

    private int sortMode;

    public void setCurrentStar(Star currentStar) {
        this.currentStar = currentStar;
    }

    public void setSortMode(int sortMode) {
        this.sortMode = sortMode;
    }

    public void setOnDetailListener(OnDetailActionListener onDetailListener) {
        this.onDetailListener = onDetailListener;
    }

    public void setRecordList(List<Record> recordList) {
        this.list = recordList;
    }

    @Override
    public RecordGridDetailHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_gdb_record_grid_detail, parent, false);
        return new RecordGridDetailHolder(view);
    }

    @Override
    public void onBindViewHolder(RecordGridDetailHolder holder, int position) {
        Record record = list.get(position);
        holder.setCurrentStar(currentStar);
        holder.setSortMode(sortMode);
        holder.bindView(record, position, onDetailListener);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public interface OnDetailActionListener {
        void onClickCardItem(View v, Record record);
        void onPopupMenu(View v, Record record);
        void onClickStar(View v, Star star);
        void onClickScene(View v, String scene);
    }

}
