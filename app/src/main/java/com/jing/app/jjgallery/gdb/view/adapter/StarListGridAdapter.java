package com.jing.app.jjgallery.gdb.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.model.bean.StarProxy;
import com.jing.app.jjgallery.gdb.presenter.star.StarListPresenter;
import com.jing.app.jjgallery.gdb.util.GlideUtil;
import com.jing.app.jjgallery.gdb.util.ListUtil;
import com.jing.app.jjgallery.gdb.util.StarRatingUtil;
import com.jing.app.jjgallery.gdb.view.star.OnStarClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/10/28 0028.
 */

public class StarListGridAdapter extends RecyclerView.Adapter<StarListGridAdapter.ItemHolder> implements View.OnClickListener {

    private OnStarClickListener onStarClickListener;
    private StarListPresenter mPresenter;

    private RequestOptions requestOptions;

    private List<StarProxy> originList;
    private List<StarProxy> curList;

    public StarListGridAdapter(List<StarProxy> list) {
        this.originList = list;
        curList = new ArrayList<>();
        if (originList != null) {
            for (StarProxy proxy:originList) {
                curList.add(proxy);
            }
        }
        requestOptions = GlideUtil.getStarWideOptions();
    }

    public void setOnStarClickListener(OnStarClickListener listener) {
        onStarClickListener = listener;
    }

    public void setPresenter(StarListPresenter mPresenter) {
        this.mPresenter = mPresenter;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_gdb_starlist_grid, parent, false));
    }

    public StarProxy getItem(int position) {
        return curList.get(position);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {

        StarProxy item = curList.get(position);
        holder.tvName.setText(item.getStar().getName() + " (" + item.getStar().getRecords() + ")");

        String headPath = item.getImagePath();
        holder.ivHead.setVisibility(View.VISIBLE);
        Glide.with(GdbApplication.getInstance())
                .load(headPath)
                .apply(requestOptions)
                .into(holder.ivHead);

        holder.groupItem.setTag(item);
        holder.groupItem.setOnClickListener(this);

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
    public int getItemCount() {
        return curList == null ? 0 : curList.size();
    }

    public int getLetterPosition(String letter){
        // curList should be ordered by ASC
        if (letter.length() > 1) {
            return -1;
        }
        for (int i = 0 ; i < getItemCount(); i++){
            char value = curList.get(i).getStar().getName().toUpperCase().charAt(0);
            char index = letter.toUpperCase().charAt(0);
            if(value == index){
                return i;
            }

            // 已超过当前letter无须再比较下去
            if (value > index) {
                break;
            }
        }
        return -1;
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

    /**
     * 按名称模糊过滤
     * @param name
     */
    public void onStarFilter(String name) {
        curList.clear();
        if (originList != null) {
            for (StarProxy proxy:originList) {
                if (TextUtils.isEmpty(name)) {
                    curList.add(proxy);
                }
                else {
                    if (proxy.getStar().getName().toLowerCase().contains(name.toLowerCase())) {
                        curList.add(proxy);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_head)
        ImageView ivHead;
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
