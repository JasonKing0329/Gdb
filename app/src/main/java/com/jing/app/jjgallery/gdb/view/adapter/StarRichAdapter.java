package com.jing.app.jjgallery.gdb.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.model.bean.StarProxy;
import com.jing.app.jjgallery.gdb.util.FormatUtil;
import com.jing.app.jjgallery.gdb.util.GlideUtil;
import com.jing.app.jjgallery.gdb.util.StarRatingUtil;
import com.jing.app.jjgallery.gdb.view.star.OnStarClickListener;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.entity.StarRating;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/5/24 17:04
 */
public class StarRichAdapter extends BaseRecyclerAdapter<StarRichAdapter.RichHolder, StarProxy> {

    private Map<Long, Boolean> mExpandMap;

    private RequestOptions requestOptions;

    private OnStarClickListener onStarClickListener;

    public StarRichAdapter() {
        requestOptions = GlideUtil.getStarOptions();
    }

    public void setExpandMap(Map<Long, Boolean> mExpandMap) {
        this.mExpandMap = mExpandMap;
    }

    public void setOnStarClickListener(OnStarClickListener onStarClickListener) {
        this.onStarClickListener = onStarClickListener;
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_star_rich;
    }

    @Override
    protected RichHolder newViewHolder(View view) {
        return new RichHolder(view);
    }

    @Override
    public void onBindViewHolder(RichHolder holder, int position) {
        Star star = list.get(position).getStar();
        holder.tvName.setText(star.getName());
        holder.tvVideos.setText(star.getRecords() + " Videos");
        holder.tvType.setText(getTypeText(star));
        updateScore(holder.tvScore, star);
        updateScoreC(holder.tvScoreC, star);

        holder.tvIndex.setText(String.valueOf(position + 1));

        if (star.getRatings().size() > 0) {
            StarRating rating = star.getRatings().get(0);

            holder.tvRating.setText(StarRatingUtil.getRatingValue(rating.getComplex()));
            StarRatingUtil.updateRatingColor(holder.tvRating, rating);

            holder.tvFace.setText("Face " + StarRatingUtil.getRatingValue(rating.getFace()));
            holder.tvFace.setTextColor(StarRatingUtil.getRatingColor(rating.getFace(), holder.tvFace.getResources()));
            holder.tvBody.setText("Body " + StarRatingUtil.getRatingValue(rating.getBody()));
            holder.tvBody.setTextColor(StarRatingUtil.getRatingColor(rating.getBody(), holder.tvFace.getResources()));
            holder.tvSex.setText("Sexuality " + StarRatingUtil.getRatingValue(rating.getSexuality()));
            holder.tvSex.setTextColor(StarRatingUtil.getRatingColor(rating.getSexuality(), holder.tvFace.getResources()));
            holder.tvDk.setText("Dk " + StarRatingUtil.getRatingValue(rating.getDk()));
            holder.tvDk.setTextColor(StarRatingUtil.getRatingColor(rating.getDk(), holder.tvFace.getResources()));
            holder.tvPassion.setText("Passion " + StarRatingUtil.getRatingValue(rating.getPassion()));
            holder.tvPassion.setTextColor(StarRatingUtil.getRatingColor(rating.getPassion(), holder.tvFace.getResources()));
            holder.tvVideo.setText("Video " + StarRatingUtil.getRatingValue(rating.getVideo()));
            holder.tvVideo.setTextColor(StarRatingUtil.getRatingColor(rating.getVideo(), holder.tvFace.getResources()));
            holder.groupRating.setVisibility(View.VISIBLE);
        }
        else {
            holder.tvRating.setText(StarRatingUtil.NON_RATING);
            StarRatingUtil.updateRatingColor(holder.tvRating, null);
            holder.groupRating.setVisibility(View.GONE);
        }
        holder.tvRating.setTag(position);
        holder.tvRating.setOnClickListener(ratingListener);

        Glide.with(GdbApplication.getInstance())
                .load(list.get(position).getImagePath())
                .apply(requestOptions)
                .into(holder.ivPlayer);

        holder.groupExpand.setVisibility(mExpandMap.get(star.getId()) ? View.VISIBLE:View.GONE);
        holder.ivMore.setImageResource(mExpandMap.get(star.getId()) ? R.drawable.ic_keyboard_arrow_up_666_24dp:R.drawable.ic_keyboard_arrow_down_666_24dp);
        holder.ivMore.setTag(position);
        holder.ivMore.setOnClickListener(moreListener);

    }

    public StarProxy getItem(int position) {
        return list.get(position);
    }

    @Override
    protected void onClickItemView(View v, int position, StarProxy starProxy) {
        if (onStarClickListener != null) {
            onStarClickListener.onStarClick(starProxy);
        }
    }

    private View.OnClickListener moreListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int position = (int) view.getTag();
            long starId = list.get(position).getStar().getId();
            boolean targetExpand = !mExpandMap.get(starId);
            mExpandMap.put(starId, targetExpand);
            notifyItemChanged(position);
        }
    };

    private View.OnClickListener ratingListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int position = (int) view.getTag();
            if (onStarClickListener != null) {
                onStarClickListener.onUpdateRating(list.get(position).getStar().getId());
            }
        }
    };

    private void updateScore(TextView view, Star star) {
        StringBuffer buffer = new StringBuffer();
        if (star.getMax() > 0) {
            if (star.getMin() == star.getMax()) {
                buffer.append("score(").append(star.getMax()).append(")");
            }
            else {
                buffer.append("max(").append(star.getMax()).append(")  ")
                    .append("min(").append(star.getMin()).append(")  ")
                    .append("avg(").append(FormatUtil.formatScore(star.getAverage(), 1)).append(")");
            }
            view.setText(buffer.toString());
            view.setVisibility(View.VISIBLE);
        }
        else {
            view.setVisibility(View.GONE);
        }
    }

    private void updateScoreC(TextView view, Star star) {
        StringBuffer buffer = new StringBuffer();
        if (star.getCmax() > 0) {
            if (star.getCmax() == star.getCmin()) {
                buffer.append("C score(").append(star.getCmax()).append(")");
            }
            else {
                buffer.append("C max(").append(star.getCmax()).append(")  ")
                        .append("min(").append(star.getCmin()).append(")  ")
                        .append("avg(").append(FormatUtil.formatScore(star.getCaverage(), 1)).append(")");
            }
            view.setText(buffer.toString());
            view.setVisibility(View.VISIBLE);
        }
        else {
            view.setVisibility(View.GONE);
        }
    }

    private String getTypeText(Star star) {
        String text = "";
        if (star.getBetop() > 0) {
            text = "Top " + star.getBetop();
        }
        if (star.getBebottom() > 0) {
            if (!TextUtils.isEmpty(text)) {
                text = text + ", ";
            }
            text = text + "Bottom " + star.getBebottom();
        }
        return text;
    }

    public void notifyStarChanged(Long starId) {
        for (int i = 0; i < getItemCount(); i ++) {
            if (list.get(i).getStar().getId() == starId) {
                notifyItemChanged(i);
                break;
            }
        }
    }

    public static class RichHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.group_rating)
        TableLayout groupRating;
        @BindView(R.id.iv_player)
        ImageView ivPlayer;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_index)
        TextView tvIndex;
        @BindView(R.id.tv_videos)
        TextView tvVideos;
        @BindView(R.id.tv_type)
        TextView tvType;
        @BindView(R.id.tv_rating)
        TextView tvRating;
        @BindView(R.id.iv_more)
        ImageView ivMore;
        @BindView(R.id.tv_score)
        TextView tvScore;
        @BindView(R.id.tv_score_c)
        TextView tvScoreC;
        @BindView(R.id.tv_face)
        TextView tvFace;
        @BindView(R.id.tv_body)
        TextView tvBody;
        @BindView(R.id.tv_sex)
        TextView tvSex;
        @BindView(R.id.tv_dk)
        TextView tvDk;
        @BindView(R.id.tv_passion)
        TextView tvPassion;
        @BindView(R.id.tv_video)
        TextView tvVideo;
        @BindView(R.id.group_expand)
        LinearLayout groupExpand;

        public RichHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
