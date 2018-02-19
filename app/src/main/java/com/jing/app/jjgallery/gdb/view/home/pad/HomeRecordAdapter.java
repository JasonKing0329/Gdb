package com.jing.app.jjgallery.gdb.view.home.pad;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.model.GdbImageProvider;
import com.jing.app.jjgallery.gdb.model.VideoModel;
import com.jing.app.jjgallery.gdb.util.GlideUtil;
import com.jing.app.jjgallery.gdb.util.ListUtil;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.Star;

import java.util.List;

/**
 * @desc
 * @auth 景阳
 * @time 2018/2/19 0019 15:44
 */

public class HomeRecordAdapter extends RecyclerView.Adapter {

    private final int TYPE_HEAD = 0;
    private final int TYPE_ITEM = 1;

    private List<Object> list;

    private OnListListener onListListener;

    private RequestOptions recordOptions;

    public HomeRecordAdapter() {
        recordOptions = GlideUtil.getRecordOptions();
    }

    @Override
    public int getItemViewType(int position) {
        int type = TYPE_HEAD;
        if (list.get(position) instanceof Record) {
            type = TYPE_ITEM;
        }
        return type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEAD) {
            return new HeadHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_home_pad_head, parent, false));
        }
        else {
            return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_home_record_list, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof HeadHolder) {
            onBindHead((HeadHolder) holder, (String) list.get(position));
        }
        else {
            onBindItem((ItemHolder) holder, (Record) list.get(position));
        }
    }

    private void onBindHead(HeadHolder holder, String text) {
        holder.tvHead.setText(text);
    }

    private void onBindItem(ItemHolder holder, Record record) {
        holder.groupItem.setTag(record);
        holder.groupItem.setOnClickListener(itemListener);

        Glide.with(GdbApplication.getInstance())
                .load(GdbImageProvider.getRecordRandomPath(record.getName(), null))
                .apply(recordOptions)
                .into(holder.ivRecord);

        List<Star> starList = record.getStarList();
        StringBuffer starBuffer = new StringBuffer();
        if (!ListUtil.isEmpty(starList)) {
            for (Star star:starList) {
                starBuffer.append("&").append(star.getName());
            }
        }
        String starText = starBuffer.toString();
        if (starText.length() > 1) {
            starText = starText.substring(1);
        }
        holder.tvStar.setText(starText);

        holder.tvDate.setVisibility(View.GONE);

        // deprecated item
        if (record.getDeprecated() == 1) {
            holder.tvDeprecated.setVisibility(View.VISIBLE);
        }
        else {
            holder.tvDeprecated.setVisibility(View.GONE);
        }

        // can be played in device
        if (VideoModel.getVideoPath(record.getName()) == null) {
            holder.ivPlay.setVisibility(View.GONE);
        }
        else {
            holder.ivPlay.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0:list.size();
    }

    public void setList(List<Object> list) {
        this.list = list;
    }

    private View.OnClickListener itemListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (onListListener != null) {
                Record record = (Record) view.getTag();
                onListListener.onClickItem(view, record);
            }
        }
    };

    public void setOnListListener(OnListListener onListListener) {
        this.onListListener = onListListener;
    }

    public int getSpanSize(int position) {
        if (list.get(position) instanceof Record) {
            return 1;
        }
        else {
            return 2;
        }
    }

    public static class HeadHolder extends RecyclerView.ViewHolder {

        TextView tvHead;

        public HeadHolder(View itemView) {
            super(itemView);
            tvHead = (TextView) itemView.findViewById(R.id.tv_head);
        }
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        ViewGroup groupItem;
        RoundedImageView ivRecord;
        TextView tvDate;
        TextView tvStar;
        TextView tvDeprecated;
        ImageView ivPlay;

        public ItemHolder(View itemView) {
            super(itemView);
            ivRecord = (RoundedImageView) itemView.findViewById(R.id.iv_record_image);
            groupItem = (ViewGroup) itemView.findViewById(R.id.group_item);
            tvDate = (TextView) itemView.findViewById(R.id.tv_record_date);
            tvStar = (TextView) itemView.findViewById(R.id.tv_record_star);
            tvDeprecated = (TextView) itemView.findViewById(R.id.tv_deprecated);
            ivPlay = (ImageView) itemView.findViewById(R.id.iv_play);
        }
    }

    public interface OnListListener {
        void onLoadMore();
        void onClickItem(View view, Record record);
    }
}
