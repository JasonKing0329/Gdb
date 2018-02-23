package com.jing.app.jjgallery.gdb.view.record.pad;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.util.GlideUtil;
import com.jing.app.jjgallery.gdb.view.star.pad.StarPageStarAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @desc
 * @auth 景阳
 * @time 2018/2/14 0014 10:41
 */

public class RecordPadAdapter extends RecyclerView.Adapter<RecordPadAdapter.RecordHolder> implements View.OnClickListener {

    private List<String> pathList;

    private RequestOptions recordOptions;

    private OnItemClickListener onItemClickListener;

    public RecordPadAdapter(){
        recordOptions = GlideUtil.getRecordOptions();
    }

    @Override
    public RecordHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecordHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_record_pad_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecordHolder holder, int position) {
        if (pathList.get(position).toLowerCase().endsWith(".gif")) {
            Glide.with(holder.ivRecord.getContext())
                    .asGif()
                    .load(pathList.get(position))
                    .into(holder.ivRecord);
        }
        else {
            Glide.with(holder.ivRecord.getContext())
                    .load(pathList.get(position))
                    .apply(recordOptions)
                    .into(holder.ivRecord);
        }

        holder.groupCard.setTag(pathList.get(position));
        holder.groupCard.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return pathList == null ? 0:pathList.size();
    }

    public void setPathList(List<String> pathList) {
        this.pathList = pathList;
    }

    @Override
    public void onClick(View view) {
        final String filePath = (String) view.getTag();
        PopupMenu menu = new PopupMenu(view.getContext(), view);
        menu.getMenuInflater().inflate(R.menu.popup_image, menu.getMenu());
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_delete:
                        onItemClickListener.onDeleteItem(filePath);
                        break;
                }
                return true;
            }
        });
        menu.show();
    }

    public void removeItem(String filePath) {
        int position = pathList.indexOf(filePath);
        if (position >= 0) {
            pathList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public static class RecordHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.group_card)
        ViewGroup groupCard;

        @BindView(R.id.iv_record)
        ImageView ivRecord;

        public RecordHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClickListener {
        void onDeleteItem(String filePath);
    }
}
