package com.jing.app.jjgallery.gdb.view.game;

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
import com.jing.app.jjgallery.gdb.model.GdbImageProvider;
import com.jing.app.jjgallery.gdb.model.bean.RandomStarBean;
import com.jing.app.jjgallery.gdb.util.GlideUtil;
import com.jing.app.jjgallery.gdb.util.ScreenUtils;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/11/7 17:23
 */
public class RandomStarAdapter extends RecyclerView.Adapter<RandomStarAdapter.ImageHolder> implements View.OnClickListener {

    private List<RandomStarBean> starList;
    private RequestOptions requestOptions;
    private int itemWidth;
    private int itemHeight;
    private int maxSpan;
    private OnStarItemListener onStarItemListener;

    public RandomStarAdapter(int maxSpan) {
        this.maxSpan = maxSpan;
        requestOptions = GlideUtil.getStarWideOptions();
    }

    @Override
    public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_random_star, parent, false));
    }

    @Override
    public void onBindViewHolder(ImageHolder holder, int position) {

        holder.tvStar.setText(starList.get(position).getName());

        holder.groupStar.setTag(position);
        holder.groupStar.setOnClickListener(this);

        Glide.with(GdbApplication.getInstance())
                .load(GdbImageProvider.getStarRandomPath(starList.get(position).getName(), null))
                .apply(requestOptions)
                .into(holder.ivStar);

        ViewGroup.LayoutParams params = holder.ivStar.getLayoutParams();
        params.width = itemWidth;
        params.height = itemHeight;
        holder.ivStar.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return starList == null ? 0:starList.size();
    }

    public void setStarList(List<RandomStarBean> starList) {
        this.starList = starList;
        if (getItemCount() > 0) {
            if (getItemCount() > maxSpan) {
                itemWidth = ScreenUtils.getScreenWidth() / maxSpan;
            }
            else {
                itemWidth = ScreenUtils.getScreenWidth() / starList.size();
            }
        }
        itemHeight = itemWidth * 9 / 16;
    }

    @Override
    public void onClick(View v) {
        if (onStarItemListener != null) {
            int position = (int) v.getTag();
            onStarItemListener.onClickStar(starList.get(position));
        }
    }

    public void setOnStarItemListener(OnStarItemListener onStarItemListener) {
        this.onStarItemListener = onStarItemListener;
    }

    public static class ImageHolder extends RecyclerView.ViewHolder {

        ViewGroup groupStar;
        ImageView ivStar;
        TextView tvStar;

        public ImageHolder(View itemView) {
            super(itemView);
            ivStar = (ImageView) itemView.findViewById(R.id.iv_star);
            groupStar = (ViewGroup) itemView.findViewById(R.id.group_star);
            tvStar = (TextView) itemView.findViewById(R.id.tv_star);
        }
    }

    public interface OnStarItemListener {
        void onClickStar(RandomStarBean bean);
    }
}
