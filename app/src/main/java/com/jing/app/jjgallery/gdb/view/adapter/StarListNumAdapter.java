package com.jing.app.jjgallery.gdb.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.siyamed.shapeimageview.CircularImageView;

import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.model.bean.StarProxy;
import com.jing.app.jjgallery.gdb.presenter.star.StarListPresenter;
import com.jing.app.jjgallery.gdb.util.GlideUtil;
import com.jing.app.jjgallery.gdb.util.ListUtil;
import com.jing.app.jjgallery.gdb.util.StarRatingUtil;
import com.jing.app.jjgallery.gdb.view.star.OnStarClickListener;

import java.util.List;

/**
 * Created by Administrator on 2016/11/27 0027.
 */

public class StarListNumAdapter extends RecyclerView.Adapter<StarListNumAdapter.ViewHolder> implements View.OnClickListener {

    private OnStarClickListener onStarClickListener;
    private StarListPresenter mPresenter;

    private RequestOptions requestOptions;

    private List<StarProxy> originList;

    public StarListNumAdapter(List<StarProxy> list) {
        this.originList = list;
        requestOptions = GlideUtil.getStarOptions();
    }

    public void setOnStarClickListener(OnStarClickListener listener) {
        onStarClickListener = listener;
    }

    public void setPresenter(StarListPresenter mPresenter) {
        this.mPresenter = mPresenter;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_gdb_starlist_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        StarProxy item = originList.get(position);
        holder.name.setText(item.getStar().getName() + " (" + item.getStar().getRecords() + ")");
        String headPath = item.getImagePath();
        if (headPath == null) {
            holder.imageView.setVisibility(View.GONE);
        }
        else {
            holder.imageView.setVisibility(View.VISIBLE);
            Glide.with(GdbApplication.getInstance())
                    .load(headPath)
                    .apply(requestOptions)
                    .into(holder.imageView);
        }
        holder.name.setTag(item);
        holder.name.setOnClickListener(this);

        if (ListUtil.isEmpty(item.getStar().getRatings())) {
            holder.ratingView.setText(StarRatingUtil.NON_RATING);
            StarRatingUtil.updateRatingColor(holder.ratingView, null);
        }
        else {
            holder.ratingView.setText(StarRatingUtil.getRatingValue(item.getStar().getRatings().get(0).getComplex()));
            StarRatingUtil.updateRatingColor(holder.ratingView, item.getStar().getRatings().get(0));
        }
        holder.ratingView.setTag(item);
        holder.ratingView.setOnClickListener(this);
    }

    public StarProxy getItem(int position) {
        return originList.get(position);
    }

    @Override
    public int getItemCount() {
        return originList == null ? 0:originList.size();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.gdb_star_name) {
            if (onStarClickListener != null) {
                StarProxy star = (StarProxy) v.getTag();
                onStarClickListener.onStarClick(star);
            }
        }
        else if (v.getId() == R.id.gdb_star_rating) {
            final int position = (int) v.getTag();
            if (onStarClickListener != null) {
                onStarClickListener.onUpdateRating(originList.get(position).getStar().getId());
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        CircularImageView imageView;
        TextView ratingView;
        
        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.gdb_star_name);
            imageView = (CircularImageView) itemView.findViewById(R.id.gdb_star_headimg);
            ratingView = itemView.findViewById(R.id.gdb_star_rating);
        }
    }
}
