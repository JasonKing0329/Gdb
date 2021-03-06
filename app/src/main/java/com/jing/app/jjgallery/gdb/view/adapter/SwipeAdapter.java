package com.jing.app.jjgallery.gdb.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.model.GdbImageProvider;
import com.jing.app.jjgallery.gdb.model.bean.StarProxy;
import com.jing.app.jjgallery.gdb.util.GlideUtil;
import com.king.app.gdb.data.entity.Record;

import java.util.List;
import java.util.Random;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/8/4 14:04
 */
public class SwipeAdapter extends BaseAdapter {

    private List<StarProxy> list;
    private Random random;
    private OnSwipeItemListener onSwipeItemListener;

    private RequestOptions recordOptions;
    private RequestOptions starOptions;

    public SwipeAdapter() {
        random = new Random();
        recordOptions = GlideUtil.getRecordOptions();
        starOptions = GlideUtil.getStarOptions();
    }

    public void setOnSwipeItemListener(OnSwipeItemListener onSwipeItemListener) {
        this.onSwipeItemListener = onSwipeItemListener;
    }

    public void setList(List<StarProxy> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_gdb_star_card, parent, false);
            holder = new ViewHolder();
            holder.ivRecord = (ImageView) convertView.findViewById(R.id.iv_record);
            holder.ivStar = (ImageView) convertView.findViewById(R.id.iv_star);
            holder.tvStarName = (TextView) convertView.findViewById(R.id.tv_star_name);
            holder.ivFavor = (ImageView) convertView.findViewById(R.id.iv_favor);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        StarProxy star = list.get(position);
        holder.tvStarName.setText(star.getStar().getName());

        Glide.with(GdbApplication.getInstance())
                .load(star.getImagePath())
                .apply(starOptions)
                .into(holder.ivStar);
        
        List<Record> list = star.getStar().getRecordList();
        if (list != null && list.size() > 0) {
            int index = Math.abs(random.nextInt()) % list.size();
            String path = GdbImageProvider.getRecordRandomPath(list.get(index).getName(), null);
            Glide.with(GdbApplication.getInstance())
                    .load(path)
                    .apply(recordOptions)
                    .into(holder.ivRecord);

        }
        else {
            holder.ivRecord.setImageResource(R.drawable.default_cover);
        }

        holder.ivFavor.setVisibility(View.GONE);

        holder.ivStar.setTag(R.id.gdb_swipe_star_tag, star);
        holder.ivStar.setOnClickListener(starListener);
        return convertView;
    }

    private View.OnClickListener starListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (onSwipeItemListener != null) {
                StarProxy star = (StarProxy) view.getTag(R.id.gdb_swipe_star_tag);
                onSwipeItemListener.onClickStar(star);
            }
        }
    };

    private class ViewHolder {
        ImageView ivRecord;
        ImageView ivStar;
        TextView tvStarName;
        ImageView ivFavor;
    }

    public interface OnSwipeItemListener {
        void onClickStar(StarProxy star);
    }
}
