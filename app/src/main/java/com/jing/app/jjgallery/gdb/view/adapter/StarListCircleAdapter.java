package com.jing.app.jjgallery.gdb.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.model.bean.StarProxy;
import com.jing.app.jjgallery.gdb.util.GlideUtil;
import com.jing.app.jjgallery.gdb.util.ListUtil;
import com.jing.app.jjgallery.gdb.util.StarRatingUtil;
import com.jing.app.jjgallery.gdb.view.pub.CircleImageView;
import com.jing.app.jjgallery.gdb.view.star.OnStarClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/10/28 0028.
 */

public class StarListCircleAdapter extends BaseRecyclerAdapter<StarListCircleAdapter.ItemHolder, StarProxy> implements View.OnClickListener
    , View.OnLongClickListener{

    private OnStarClickListener onStarClickListener;

    private RequestOptions requestOptions;

    public StarListCircleAdapter() {
        requestOptions = GlideUtil.getStarWideOptions();
    }

    public void setOnStarClickListener(OnStarClickListener listener) {
        onStarClickListener = listener;
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_gdb_starlist_circle;
    }

    @Override
    protected ItemHolder newViewHolder(View view) {
        return new ItemHolder(view);
    }

    public StarProxy getItem(int position) {
        return list.get(position);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {

        StarProxy item = list.get(position);
        holder.tvName.setText(item.getStar().getName() + " (" + item.getStar().getRecords() + ")");

        String headPath = item.getImagePath();
        holder.ivHead.setVisibility(View.VISIBLE);
        Glide.with(GdbApplication.getInstance())
                .load(headPath)
                .apply(requestOptions)
                .into(holder.ivHead);

        holder.groupItem.setTag(item);
        holder.groupItem.setOnClickListener(this);
        holder.groupItem.setOnLongClickListener(this);

        if (ListUtil.isEmpty(item.getStar().getRatings())) {
            holder.tvRating.setText(StarRatingUtil.NON_RATING);
            StarRatingUtil.updateRatingColor(holder.tvRating, null);
        }
        else {
            holder.tvRating.setText(StarRatingUtil.getRatingValue(item.getStar().getRatings().get(0).getComplex()));
            StarRatingUtil.updateRatingColor(holder.tvRating, item.getStar().getRatings().get(0));
        }
        holder.tvRating.setTag(item);
        holder.tvRating.setOnClickListener(this);
    }

    @Override
    public boolean onLongClick(View v) {
        if (v instanceof ViewGroup) {
            if (onStarClickListener != null) {
                StarProxy star = (StarProxy) v.getTag();
                onStarClickListener.onStarLongClick(star);
            }
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v instanceof ViewGroup) {
            if (onStarClickListener != null) {
                StarProxy star = (StarProxy) v.getTag();
                onStarClickListener.onStarClick(star);
            }
        }
        else if (v instanceof TextView) {
            StarProxy star = (StarProxy) v.getTag();
            if (onStarClickListener != null) {
                onStarClickListener.onUpdateRating(star.getStar().getId());
            }
        }
    }

    public void notifyStarChanged(Long starId) {
        for (int i = 0; i < getItemCount(); i ++) {
            if (list.get(i).getStar().getId() == starId) {
                notifyItemChanged(i);
                break;
            }
        }
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_head)
        CircleImageView ivHead;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_rating)
        TextView tvRating;
        @BindView(R.id.group_item)
        RelativeLayout groupItem;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
