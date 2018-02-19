package com.jing.app.jjgallery.gdb.view.home;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.model.bean.StarProxy;
import com.jing.app.jjgallery.gdb.util.DisplayHelper;
import com.jing.app.jjgallery.gdb.util.GlideUtil;

import java.util.List;

public class HomeStarAdapter extends RecyclerView.Adapter<HomeStarAdapter.SliderCard> implements View.OnClickListener {

    private List<StarProxy> list;
    private GHomeHeader.OnStarListener onStarListener;

    private RequestOptions starOptions;

    public HomeStarAdapter() {
        starOptions = GlideUtil.getStarOptions();
    }

    @Override
    public SliderCard onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.adapter_home_star_item, parent, false);

        return new SliderCard(view);
    }

    @Override
    public void onBindViewHolder(SliderCard holder, final int position) {

        Glide.with(GdbApplication.getInstance())
                .load(list.get(position).getImagePath())
                .apply(starOptions)
                .into(holder.imageView);

        holder.groupCard.setTag(position);
        holder.groupCard.setOnClickListener(this);

        if (DisplayHelper.isTabModel(holder.tvName.getContext())) {
            holder.tvName.setVisibility(View.VISIBLE);
            holder.tvName.setText(list.get(position).getStar().getName());
        }
        else {
            holder.tvName.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0:list.size();
    }

    public void setList(List<StarProxy> list) {
        this.list = list;
    }

    public void setOnStarListener(GHomeHeader.OnStarListener onStarListener) {
        this.onStarListener = onStarListener;
    }

    @Override
    public void onClick(View v) {
        if (onStarListener != null) {
            int position = (int) v.getTag();
            onStarListener.onStarClicked(list.get(position));
        }

    }

    public static class SliderCard extends RecyclerView.ViewHolder {

        ViewGroup groupCard;
        ImageView imageView;
        TextView tvName;

        public SliderCard(View itemView) {
            super(itemView);
            groupCard = (ViewGroup) itemView.findViewById(R.id.group_card);
            imageView = (ImageView) itemView.findViewById(R.id.iv_thumb);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
        }
    }
}
