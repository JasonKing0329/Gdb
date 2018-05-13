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
import com.jing.app.jjgallery.gdb.util.ListUtil;
import com.jing.app.jjgallery.gdb.util.StarRatingUtil;
import com.king.app.gdb.data.entity.StarRating;

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
            holder.groupName.setVisibility(View.VISIBLE);
            holder.tvName.setText(list.get(position).getStar().getName());
            List<StarRating> ratings = list.get(position).getStar().getRatings();
            if (ListUtil.isEmpty(ratings)) {
                holder.tvRating.setText(StarRatingUtil.NON_RATING);
            }
            else {
                holder.tvRating.setText(StarRatingUtil.getRatingValue(ratings.get(0).getComplex()));
            }
        }
        else {
            holder.groupName.setVisibility(View.GONE);
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
        ViewGroup groupName;
        ImageView imageView;
        TextView tvName;
        TextView tvRating;

        public SliderCard(View itemView) {
            super(itemView);
            groupCard = (ViewGroup) itemView.findViewById(R.id.group_card);
            groupName = (ViewGroup) itemView.findViewById(R.id.group_name);
            imageView = (ImageView) itemView.findViewById(R.id.iv_thumb);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvRating = (TextView) itemView.findViewById(R.id.tv_rating);
        }
    }
}
