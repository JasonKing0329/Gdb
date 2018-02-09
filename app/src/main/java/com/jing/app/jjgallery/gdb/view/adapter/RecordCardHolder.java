package com.jing.app.jjgallery.gdb.view.adapter;

import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.model.GdbImageProvider;
import com.jing.app.jjgallery.gdb.util.FormatUtil;
import com.jing.app.jjgallery.gdb.util.GlideUtil;
import com.jing.app.jjgallery.gdb.view.pub.CircleImageView;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.param.DataConstants;

import java.util.List;

/**
 * 描述: record的卡片布局
 * <p/>作者：景阳
 * <p/>创建时间: 2017/8/9 11:53
 */
public class RecordCardHolder {
    private CardView groupCard;
    private ImageView ivRecord;
    private CircleImageView ivStar1;
    private CircleImageView ivStar2;
    private TextView tvName;
    private TextView tvScore;
    private TextView tvPath;
    private TextView tvScene;
    private TextView tvDate;
    private TextView tvDeprecated;
    private LinearLayout groupFrames;
    private RoundedImageView ivFrame1;
    private RoundedImageView ivFrame2;
    private RoundedImageView ivFrame3;

    private Star currentStar;

    public void init(View itemView) {
        groupCard = (CardView) itemView.findViewById(R.id.group_card);
        ivRecord = (ImageView) itemView.findViewById(R.id.iv_record);
        ivStar1 = (CircleImageView) itemView.findViewById(R.id.iv_star1);
        ivStar2 = (CircleImageView) itemView.findViewById(R.id.iv_star2);
        tvName = (TextView) itemView.findViewById(R.id.tv_name);
        tvScore = (TextView) itemView.findViewById(R.id.tv_score);
        tvPath = (TextView) itemView.findViewById(R.id.tv_path);
        tvScene = (TextView) itemView.findViewById(R.id.tv_scene);
        tvDate = (TextView) itemView.findViewById(R.id.tv_date);
        tvDeprecated = (TextView) itemView.findViewById(R.id.tv_deprecated);
        groupFrames = (LinearLayout) itemView.findViewById(R.id.group_frames);
        ivFrame1 = (RoundedImageView) itemView.findViewById(R.id.iv_frame1);
        ivFrame2 = (RoundedImageView) itemView.findViewById(R.id.iv_frame2);
        ivFrame3 = (RoundedImageView) itemView.findViewById(R.id.iv_frame3);

    }

    public void setCurrentStar(Star currentStar) {
        this.currentStar = currentStar;
    }

    public void bindView(Record record, int position, int endPosition, View.OnClickListener cardListener) {
        tvDate.setText(FormatUtil.formatDate(record.getLastModifyTime()));
        tvName.setText(record.getName());
        tvPath.setText(record.getDirectory());
        tvScore.setText(String.valueOf(record.getScore()));

        if (record.getDeprecated() == 1) {
            tvDeprecated.setVisibility(View.VISIBLE);
        }
        else {
            tvDeprecated.setVisibility(View.GONE);
        }
        tvScene.setText(record.getScene());
        if (record.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
            for (Star star:record.getStarList()) {
                if (star.getId() != currentStar.getId()) {
                    Glide.with(GdbApplication.getInstance())
                            .load(GdbImageProvider.getStarRandomPath(star.getName(), null))
                            .apply(GlideUtil.getStarOptions())
                            .into(ivStar1);
                    ivStar2.setVisibility(View.GONE);
                    break;
                }
            }
        }
        else if (record.getType() == DataConstants.VALUE_RECORD_TYPE_3W) {
            ivStar1.setImageResource(R.drawable.ic_def_person);
            ivStar2.setImageResource(R.drawable.ic_def_person);
            ivStar2.setVisibility(View.VISIBLE);
            ImageView[] views = new ImageView[]{ivStar1, ivStar2};
            int imageViewIndex = 0;
            for (Star star:record.getStarList()) {
                if (star.getId() == currentStar.getId()) {
                    continue;
                }

                Glide.with(GdbApplication.getInstance())
                        .load(GdbImageProvider.getStarRandomPath(star.getName(), null))
                        .apply(GlideUtil.getStarOptions())
                        .into(views[imageViewIndex]);

                imageViewIndex ++;
                if (imageViewIndex > 1) {
                    break;
                }
            }
        }

        RequestOptions recordOptions = GlideUtil.getRecordOptions();
        Glide.with(GdbApplication.getInstance())
                .load(GdbImageProvider.getRecordRandomPath(record.getName(), null))
                .apply(recordOptions)
                .into(ivRecord);

        // record's images shown on the bottom
        List<String> pathList = GdbImageProvider.getRecordPathList(record.getName());
        if (pathList.size() > 1) {
            groupFrames.setVisibility(View.VISIBLE);
            if (pathList.size() > 2) {
                Glide.with(GdbApplication.getInstance())
                        .load(pathList.get(2))
                        .apply(recordOptions)
                        .into(ivFrame3);
            }
            Glide.with(GdbApplication.getInstance())
                    .load(pathList.get(1))
                    .apply(recordOptions)
                    .into(ivFrame2);
            Glide.with(GdbApplication.getInstance())
                    .load(pathList.get(0))
                    .apply(recordOptions)
                    .into(ivFrame1);
        }
        else {
            groupFrames.setVisibility(View.GONE);
        }

        // to keep UI better, hide tvDate if the visibility of groupFrames is VISIBLE
        tvDate.setVisibility(groupFrames.getVisibility() == View.VISIBLE ? View.GONE:View.VISIBLE);

        // the last item should has margin to right
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) groupCard.getLayoutParams();
        if (position == endPosition) {
            params.rightMargin = groupCard.getContext().getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        }
        else {
            params.rightMargin = 0;
        }

        // set group listener
        groupCard.setTag(record);
        groupCard.setOnClickListener(cardListener);

    }
}