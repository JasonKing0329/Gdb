package com.jing.app.jjgallery.gdb.view.game;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.model.GdbImageProvider;
import com.jing.app.jjgallery.gdb.util.GlideUtil;
import com.king.service.gdb.bean.Star;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/11/7 17:23
 */
public class RandomStarAdapter extends RecyclerView.Adapter<RandomStarAdapter.ImageHolder> {

    private List<Star> starList;
    private RequestOptions requestOptions;

    public RandomStarAdapter() {
        requestOptions = GlideUtil.getStarWideOptions();
    }

    @Override
    public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_random_star, parent, false));
    }

    @Override
    public void onBindViewHolder(ImageHolder holder, int position) {
        Glide.with(GdbApplication.getInstance())
                .load(GdbImageProvider.getStarRandomPath(starList.get(position).getName(), null))
                .into(holder.ivStar);
    }

    @Override
    public int getItemCount() {
        return starList == null ? 0:starList.size();
    }

    public void setStarList(List<Star> starList) {
        this.starList = starList;
    }

    public static class ImageHolder extends RecyclerView.ViewHolder {

        ImageView ivStar;
        public ImageHolder(View itemView) {
            super(itemView);
            ivStar = (ImageView) itemView.findViewById(R.id.iv_star);
        }
    }
}
