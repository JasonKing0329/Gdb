package com.jing.app.jjgallery.gdb.view.adapter;

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
import com.jing.app.jjgallery.gdb.model.VideoModel;
import com.jing.app.jjgallery.gdb.model.conf.PreferenceValue;
import com.jing.app.jjgallery.gdb.util.GlideUtil;
import com.jing.app.jjgallery.gdb.util.ListUtil;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.param.DataConstants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/8/6 0006.
 */
public class RecordGridHolder extends RecyclerListAdapter.ViewHolder<Record> {

    @BindView(R.id.iv_image)
    ImageView ivImage;
    @BindView(R.id.tv_stars)
    TextView tvStars;
    @BindView(R.id.iv_play)
    ImageView ivPlay;
    @BindView(R.id.tv_sort)
    TextView tvSort;
    @BindView(R.id.tv_seq)
    TextView tvSeq;
    @BindView(R.id.group_record)
    ViewGroup groupRecord;
    
    private int sortMode;
    private View.OnClickListener onClickListener;

    public RecordGridHolder(ViewGroup parent) {
        this(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_gdb_record_grid, parent, false));
    }

    private RecordGridHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void setParameters(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public void bind(Record item, int position) {
        tvSeq.setText(String.valueOf(position + 1));

        List<Star> starList = item.getStarList();
        StringBuffer starBuffer = new StringBuffer();
        if (!ListUtil.isEmpty(starList)) {
            for (Star star:starList) {
                starBuffer.append("&").append(star.getName());
            }
        }
        String starText = starBuffer.toString();
        if (starText.length() > 1) {
            starText = starText.substring(1);
        }
        tvStars.setText(starText);

        // can be played in device
        if (VideoModel.getVideoPath(item.getName()) == null) {
            ivPlay.setVisibility(View.INVISIBLE);
        }
        else {
            ivPlay.setVisibility(View.VISIBLE);
        }

        String path = GdbImageProvider.getRecordRandomPath(item.getName(), null);
        RequestOptions recordOptions = GlideUtil.getRecordOptions();
        Glide.with(GdbApplication.getInstance())
                .load(path)
                .apply(recordOptions)
                .into(ivImage);

        showSortScore(item);

        groupRecord.setTag(item);
        groupRecord.setOnClickListener(onClickListener);
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

}
