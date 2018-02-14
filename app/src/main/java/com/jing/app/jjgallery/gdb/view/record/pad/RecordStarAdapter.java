package com.jing.app.jjgallery.gdb.view.record.pad;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.model.GdbImageProvider;
import com.jing.app.jjgallery.gdb.util.GlideUtil;
import com.king.app.gdb.data.entity.RecordStar;
import com.king.app.gdb.data.param.DataConstants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @desc
 * @auth 景阳
 * @time 2018/2/14 0014 10:41
 */

public class RecordStarAdapter extends RecyclerView.Adapter<RecordStarAdapter.StarHolder> implements View.OnClickListener {

    private List<RecordStar> list;

    private RequestOptions starOptions;

    private OnStarItemListener onStarItemListener;

    public RecordStarAdapter() {
        starOptions = GlideUtil.getStarOptions();
    }

    @Override
    public StarHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new StarHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_record_pad_star, parent, false));
    }

    @Override
    public void onBindViewHolder(StarHolder holder, int position) {
        RecordStar star = list.get(position);
        holder.tvName.setText(star.getStar().getName());
        StringBuffer buffer = new StringBuffer();
        buffer.append(DataConstants.getTextForType(star.getType()));
        if (star.getScore() != 0 || star.getScoreC() != 0) {
            buffer.append("(").append(star.getScore()).append("/").append(star.getScoreC()).append(")");
        }
        holder.tvFlag.setText(buffer.toString());
        Glide.with(holder.ivStar.getContext())
                .load(GdbImageProvider.getStarRandomPath(star.getStar().getName(), null))
                .apply(starOptions)
                .into(holder.ivStar);

        holder.groupStar.setTag(position);
        holder.groupStar.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void setList(List<RecordStar> list) {
        this.list = list;
    }

    public void setOnStarItemListener(OnStarItemListener onStarItemListener) {
        this.onStarItemListener = onStarItemListener;
    }

    @Override
    public void onClick(View view) {
        int position = (int) view.getTag();
        onStarItemListener.onClickStar(list.get(position));
    }

    public static class StarHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.group_star)
        ViewGroup groupStar;
        @BindView(R.id.iv_star)
        CircularImageView ivStar;
        @BindView(R.id.tv_flag)
        TextView tvFlag;
        @BindView(R.id.tv_name)
        TextView tvName;

        public StarHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnStarItemListener {
        void onClickStar(RecordStar relation);
    }
}
