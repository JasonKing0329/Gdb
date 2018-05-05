package com.jing.app.jjgallery.gdb.view.adapter;

import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.model.GdbImageProvider;
import com.jing.app.jjgallery.gdb.model.VideoModel;
import com.jing.app.jjgallery.gdb.model.conf.PreferenceValue;
import com.jing.app.jjgallery.gdb.util.GlideUtil;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.param.DataConstants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 描述: only for pad landscape
 * <p/>作者：景阳
 * <p/>创建时间: 2018/2/22 16:28
 */
public class RecordGridDetailHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @BindView(R.id.iv_image)
    ImageView ivImage;
    @BindView(R.id.iv_play)
    ImageView ivPlay;
    @BindView(R.id.tv_path)
    TextView tvPath;
    @BindView(R.id.tv_scene)
    TextView tvScene;
    @BindView(R.id.tv_sort)
    TextView tvSort;
    @BindView(R.id.tv_seq)
    TextView tvSeq;
    @BindView(R.id.tv_star)
    TextView tvStar;
    @BindView(R.id.tv_star1)
    TextView tvStar1;
    @BindView(R.id.tv_star_more)
    TextView tvStarMore;
    @BindView(R.id.tv_special)
    TextView tvSpecial;
    @BindView(R.id.tv_pics)
    TextView tvPics;
    @BindView(R.id.group_record)
    RelativeLayout groupRecord;

    private RecordGridDetailAdapter.OnDetailActionListener onDetailActionListener;

    private Star currentStar;

    private int sortMode;

    public RecordGridDetailHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bindView(Record record, int position, RecordGridDetailAdapter.OnDetailActionListener cardListener) {

        if (record.getDeprecated() == DataConstants.DEPRECATED) {
            groupRecord.setBackgroundColor(groupRecord.getResources().getColor(R.color.record_deprecated_bg));
        }
        else {
            groupRecord.setBackgroundColor(groupRecord.getResources().getColor(R.color.white));
        }

        onDetailActionListener = cardListener;
        groupRecord.setTag(record);
        groupRecord.setOnClickListener(this);

        tvSeq.setText(String.valueOf(position + 1));
        tvSeq.setTag(record);
        tvSeq.setOnClickListener(this);

        tvPath.setText(record.getDirectory() + "/" + record.getName());
        tvScene.setText(record.getScene());
        tvScene.setTag(record.getScene());
        tvScene.setOnClickListener(this);

        RequestOptions recordOptions = GlideUtil.getRecordOptions();
        Glide.with(GdbApplication.getInstance())
                .load(GdbImageProvider.getRecordRandomPath(record.getName(), null))
                .apply(recordOptions)
                .into(ivImage);

        if (TextUtils.isEmpty(VideoModel.getVideoPath(record.getName()))) {
            ivPlay.setVisibility(View.GONE);
        }
        else {
            ivPlay.setVisibility(View.VISIBLE);
        }

        if (TextUtils.isEmpty(record.getSpecialDesc())) {
            tvSpecial.setVisibility(View.INVISIBLE);
        }
        else {
            tvSpecial.setText(record.getSpecialDesc());
            tvSpecial.setVisibility(View.VISIBLE);
        }

        tvPics.setText("(" + GdbImageProvider.getRecordPicNumber(record.getName()) + " pics)");


        setStarTag(record);

        showSortScore(record);
    }

    /**
     * 无论有几个star，都只显示前两个，有多余的显示...
     * @param record
     */
    private void setStarTag(Record record) {
        List<Star> starList = record.getStarList();
        List<Star> targetList = new ArrayList<>();
        if (starList.size() > 0) {
            for (Star star:starList) {
                if (star.getId() != currentStar.getId()) {
                    // 只要不是currentStar，计数
                    targetList.add(star);
                }
            }
        }

        if (targetList.size() > 0) {
            tvStar.setText(targetList.get(0).getName());
            tvStar.setVisibility(View.VISIBLE);
            tvStar.setTag(targetList.get(0));
            tvStar.setOnClickListener(this);
            updateTagBg(tvStar);
        }
        else {
            tvStar.setVisibility(View.GONE);
        }
        if (targetList.size() > 1) {
            tvStar1.setText(targetList.get(1).getName());
            tvStar1.setVisibility(View.VISIBLE);
            tvStar1.setTag(targetList.get(1));
            tvStar1.setOnClickListener(this);
            updateTagBg(tvStar1);
        }
        else {
            tvStar1.setVisibility(View.GONE);
        }
        if (targetList.size() > 2) {
            tvStarMore.setVisibility(View.VISIBLE);
            updateTagBg(tvStarMore);
        }
        else {
            tvStarMore.setVisibility(View.GONE);
        }
    }

    private void updateTagBg(TextView tvStar) {
        GradientDrawable drawable = (GradientDrawable) tvStar.getBackground();
        drawable.setColor(tvStar.getResources().getColor(R.color.actionbar_bk_blue));
        tvStar.setBackground(drawable);
    }

    public void setCurrentStar(Star currentStar) {
        this.currentStar = currentStar;
    }

    public void setSortMode(int sortMode) {
        this.sortMode = sortMode;
    }

    private void showSortScore(Record item) throws NullPointerException{
        switch (sortMode) {
            case PreferenceValue.GDB_SR_ORDERBY_SCORE:
                tvSort.setText(String.valueOf(item.getScore()));
                break;
            case PreferenceValue.GDB_SR_ORDERBY_DATE:
                tvSort.setVisibility(View.VISIBLE);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = sdf.format(new Date(item.getLastModifyTime()));
                tvSort.setText(date);
                break;
            case PreferenceValue.GDB_SR_ORDERBY_BAREBACK:
                tvSort.setVisibility(View.VISIBLE);
                tvSort.setText("" + item.getScoreBareback());
                break;
            case PreferenceValue.GDB_SR_ORDERBY_BJOB:
                tvSort.setVisibility(View.VISIBLE);
                if (item.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                    tvSort.setText("" + item.getRecordType1v1().getScoreBjob());
                }
                else if (item.getType() == DataConstants.VALUE_RECORD_TYPE_3W
                        || item.getType() == DataConstants.VALUE_RECORD_TYPE_MULTI) {
                    tvSort.setText("" + item.getRecordType3w().getScoreBjob());
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_CSHOW:
                tvSort.setVisibility(View.VISIBLE);
                if (item.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                    tvSort.setText("" + item.getRecordType1v1().getScoreCshow());
                }
                else if (item.getType() == DataConstants.VALUE_RECORD_TYPE_3W
                        || item.getType() == DataConstants.VALUE_RECORD_TYPE_MULTI) {
                    tvSort.setText("" + item.getRecordType3w().getScoreCshow());
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_CUM:
                tvSort.setVisibility(View.VISIBLE);
                tvSort.setText("" + item.getScoreCum());
                break;
            case PreferenceValue.GDB_SR_ORDERBY_PASSION:
                tvSort.setVisibility(View.VISIBLE);
                tvSort.setText("" + item.getScorePassion());
                break;
            case PreferenceValue.GDB_SR_ORDERBY_FK1:
                tvSort.setVisibility(View.VISIBLE);
                if (item.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                    tvSort.setText("" + item.getRecordType1v1().getScoreFkType1());
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_FK2:
                tvSort.setVisibility(View.VISIBLE);
                if (item.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                    tvSort.setText("" + item.getRecordType1v1().getScoreFkType2());
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_FK3:
                tvSort.setVisibility(View.VISIBLE);
                if (item.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                    tvSort.setText("" + item.getRecordType1v1().getScoreFkType3());
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_FK4:
                tvSort.setVisibility(View.VISIBLE);
                if (item.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                    tvSort.setText("" + item.getRecordType1v1().getScoreFkType4());
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_FK5:
                tvSort.setVisibility(View.VISIBLE);
                if (item.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                    tvSort.setText("" + item.getRecordType1v1().getScoreFkType5());
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_FK6:
                tvSort.setVisibility(View.VISIBLE);
                if (item.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                    tvSort.setText("" + item.getRecordType1v1().getScoreFkType6());
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_FOREPLAY:
                tvSort.setVisibility(View.VISIBLE);
                if (item.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                    tvSort.setText("" + item.getRecordType1v1().getScoreForePlay());
                }
                else if (item.getType() == DataConstants.VALUE_RECORD_TYPE_3W
                        || item.getType() == DataConstants.VALUE_RECORD_TYPE_MULTI) {
                    tvSort.setText("" + item.getRecordType3w().getScoreForePlay());
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_HD:
                tvSort.setVisibility(View.VISIBLE);
                tvSort.setText("" + item.getHdLevel());
                break;
            case PreferenceValue.GDB_SR_ORDERBY_RHYTHM:
                tvSort.setVisibility(View.VISIBLE);
                if (item.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                    tvSort.setText("" + item.getRecordType1v1().getScoreRhythm());
                }
                else if (item.getType() == DataConstants.VALUE_RECORD_TYPE_3W
                        || item.getType() == DataConstants.VALUE_RECORD_TYPE_MULTI) {
                    tvSort.setText("" + item.getRecordType3w().getScoreRhythm());
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_RIM:
                tvSort.setVisibility(View.VISIBLE);
                if (item.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                    tvSort.setText("" + item.getRecordType1v1().getScoreRim());
                }
                else if (item.getType() == DataConstants.VALUE_RECORD_TYPE_3W
                        || item.getType() == DataConstants.VALUE_RECORD_TYPE_MULTI) {
                    tvSort.setText("" + item.getRecordType3w().getScoreRim());
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_SCENE:
                tvSort.setVisibility(View.VISIBLE);
                if (item.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                    tvSort.setText("" + item.getRecordType1v1().getScoreScene());
                }
                else if (item.getType() == DataConstants.VALUE_RECORD_TYPE_3W
                        || item.getType() == DataConstants.VALUE_RECORD_TYPE_MULTI) {
                    tvSort.setText("" + item.getRecordType3w().getScoreScene());
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_SCOREFEEL:
                tvSort.setVisibility(View.VISIBLE);
                tvSort.setText("" + item.getScoreFeel());
                break;
            case PreferenceValue.GDB_SR_ORDERBY_SPECIAL:
                tvSort.setVisibility(View.VISIBLE);
                tvSort.setText("" + item.getScoreSpecial());
                break;
            case PreferenceValue.GDB_SR_ORDERBY_STAR1:
                break;
            case PreferenceValue.GDB_SR_ORDERBY_STAR2:
                break;
            case PreferenceValue.GDB_SR_ORDERBY_STARCC1:
                break;
            case PreferenceValue.GDB_SR_ORDERBY_STARCC2:
                break;
            case PreferenceValue.GDB_SR_ORDERBY_STORY:
                tvSort.setVisibility(View.VISIBLE);
                if (item.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                    tvSort.setText("" + item.getRecordType1v1().getScoreStory());
                }
                else if (item.getType() == DataConstants.VALUE_RECORD_TYPE_3W
                        || item.getType() == DataConstants.VALUE_RECORD_TYPE_MULTI) {
                    tvSort.setText("" + item.getRecordType3w().getScoreStory());
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_SCORE_BASIC:
                break;
            case PreferenceValue.GDB_SR_ORDERBY_SCORE_EXTRA:
                break;
            case PreferenceValue.GDB_SR_ORDERBY_STAR:
                tvSort.setVisibility(View.VISIBLE);
                tvSort.setText("" + item.getScoreStar());
                break;
            case PreferenceValue.GDB_SR_ORDERBY_STARC:
                break;
            default:
                tvSort.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        Object obj = view.getTag();
        if (obj instanceof Record) {
            if (view.getId() == R.id.tv_seq) {
                onDetailActionListener.onPopupMenu(view, (Record) obj);
            }
            else {
                onDetailActionListener.onClickCardItem(view, (Record) obj);
            }
        }
        else if (obj instanceof Star) {
            onDetailActionListener.onClickStar(view, (Star) obj);
        }
        else {
            onDetailActionListener.onClickScene(view, (String) obj);
        }
    }

}
