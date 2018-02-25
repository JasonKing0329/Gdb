package com.jing.app.jjgallery.gdb.view.adapter;

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
import com.jing.app.jjgallery.gdb.model.bean.RecordProxy;
import com.jing.app.jjgallery.gdb.model.conf.PreferenceValue;
import com.jing.app.jjgallery.gdb.util.DisplayHelper;
import com.jing.app.jjgallery.gdb.util.FormatUtil;
import com.jing.app.jjgallery.gdb.util.GlideUtil;
import com.jing.app.jjgallery.gdb.util.ListUtil;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.RecordType1v1;
import com.king.app.gdb.data.entity.RecordType3w;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.param.DataConstants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by 景阳 on 2016/8/6 0006.
 * 为了实现RecordList/StarRecords/RecordScene里面holderUI和数据的共享，把view的初始化和赋值抽出来作为共用体
 * RecordList/StarRecords直接是适配于RecyclerView的adapter，直接使用RecyclerView.Holder(RecordHolder)
 * RecordScene所使用的adapter被其框架封装了一层，不能直接使用RecyclerView.Holder，所以要抽出能共用的部分
 */
public class RecordViewHolder {
    private View container;
    private ImageView imageView;
    private TextView seqView;
    private TextView nameView;
    private TextView dirView;
    private TextView scoreView;
    private TextView sortScoreView;
    private TextView sceneView;
    private TextView fkView;
    private TextView cumView;
    private TextView bjobView;
    private TextView fkSubView;
    private TextView star1View;
    private TextView star2View;
    private TextView scoreBasicView;
    private TextView scoreExtraView;
    private TextView actorView;
    private ImageView ivPlay;
    private TextView tvNameSimplify;
    private ViewGroup groupDetails;
    private ViewGroup groupSimplify;
    private TextView tvFileSize;
    private TextView tvFileDate;
    private ViewGroup groupFileInfo;
    private TextView tvImgSize;
    private TextView tvImgDate;

    private int nameColorNormal, nameColorBareback;
    private View.OnClickListener onClickListener;

    /**
     * 防止刷新后加载的图片变化
     */
    private GdbImageProvider.IndexPackage indexPackage;

    public void setParameters(int nameColorNormal, int nameColorBareback, View.OnClickListener onClickListener) {
        this.nameColorNormal = nameColorNormal;
        this.nameColorBareback = nameColorBareback;
        this.onClickListener = onClickListener;

        sortScoreView.setTextColor(nameColorBareback);
    }

    public void initView(View view) {
        container = view.findViewById(R.id.record_container);
        ivPlay = (ImageView) view.findViewById(R.id.iv_play);
        imageView = (ImageView) view.findViewById(R.id.record_thumb);
        seqView = (TextView) view.findViewById(R.id.record_seq);
        nameView = (TextView) view.findViewById(R.id.record_name);
        dirView = (TextView) view.findViewById(R.id.record_dir);
        scoreView = (TextView) view.findViewById(R.id.record_score);
        sortScoreView = (TextView) view.findViewById(R.id.record_score_sort);
        sceneView = (TextView) view.findViewById(R.id.record_scene);
        fkView = (TextView) view.findViewById(R.id.record_score_fk);
        cumView = (TextView) view.findViewById(R.id.record_score_cum);
        bjobView = (TextView) view.findViewById(R.id.record_score_bjob);
        fkSubView = (TextView) view.findViewById(R.id.record_score_fk_sub);
        star1View = (TextView) view.findViewById(R.id.record_star1);
        star2View = (TextView) view.findViewById(R.id.record_star2);
        scoreBasicView = (TextView) view.findViewById(R.id.record_score_basic);
        scoreExtraView = (TextView) view.findViewById(R.id.record_score_extra);
        actorView = (TextView) view.findViewById(R.id.record_score_actor);
        tvNameSimplify = (TextView) view.findViewById(R.id.tv_name_simplify);
        groupDetails = (ViewGroup) view.findViewById(R.id.group_details);
        groupSimplify = (ViewGroup) view.findViewById(R.id.group_simplify);
        tvFileSize = (TextView) view.findViewById(R.id.tv_file_size);
        tvFileDate = (TextView) view.findViewById(R.id.tv_file_date);
        groupFileInfo = (ViewGroup) view.findViewById(R.id.group_file_info);
        tvImgSize = (TextView) view.findViewById(R.id.tv_img_size);
        tvImgDate = (TextView) view.findViewById(R.id.tv_img_date);

        if (!DisplayHelper.isTabModel(view.getContext())) {
            view.findViewById(R.id.record_show_tablet).setVisibility(View.GONE);
        }
    }

    public View getContainer() {
        return container;
    }

    /**
     * surf模式下record为null，显示文件名、本地图片、文件大小、last modify time
     * @param name
     * @param lastModifyTime
     * @param size
     */
    public void bind(String name, long lastModifyTime, long size) {
        groupDetails.setVisibility(View.GONE);
        groupSimplify.setVisibility(View.VISIBLE);
        groupFileInfo.setVisibility(View.GONE);

        tvNameSimplify.setText(name);
        ivPlay.setVisibility(View.INVISIBLE);
        seqView.setVisibility(View.INVISIBLE);
        tvFileDate.setText(FormatUtil.formatDate(lastModifyTime));
        tvFileSize.setText(FormatUtil.formatSize(size));
        bindCommon(null, name);
    }

    /**
     * 在图片底部显示size, last modify time
     * surf模式下record不为空，显示文件大小、last modify time
     * call this before call bind(Record item, int position, int sortMode)
     * @param lastModifyTime
     * @param size
     */
    public void bindExtra(long lastModifyTime, long size) {
        groupFileInfo.setVisibility(View.VISIBLE);
        tvImgDate.setText(FormatUtil.formatDate(lastModifyTime));
        tvImgSize.setText(FormatUtil.formatSize(size));
    }

    public void bind(RecordProxy item, int sortMode) {
        bind(item.getRecord(), item.getPositionInHeader(), sortMode);
    }

    public void bind(Record item, int position, int sortMode) {

        groupDetails.setVisibility(View.VISIBLE);
        groupSimplify.setVisibility(View.GONE);
        groupFileInfo.setVisibility(View.GONE);

        bindCommon(item, item.getName());

        // deprecated item
        if (item.getDeprecated() == 1) {
            container.setBackgroundColor(container.getContext().getResources().getColor(R.color.record_deprecated_bg));
        }
        else {
            container.setBackground(container.getContext().getResources().getDrawable(R.drawable.ripple_rect_grey));
        }

        // can be played in device
        if (VideoModel.getVideoPath(item.getName()) == null) {
            ivPlay.setVisibility(View.INVISIBLE);
        }
        else {
            ivPlay.setVisibility(View.VISIBLE);
        }

        seqView.setText("" + "" + (position + 1));
        nameView.setText("" + item.getName());
        dirView.setText("" + item.getDirectory());
        scoreView.setText("" + "" + item.getScore());
        scoreBasicView.setText("special(" + item.getScoreSpecial() + ")");
        scoreExtraView.setText("special desc(" + item.getSpecialDesc() + ")");
        fkView.setText("" + "fk(" + item.getScorePassion() + ")");
        cumView.setText("" + "cum(" + item.getScoreCum() + ")");
        if (item.getScoreBareback() > 0) {
            nameView.setTextColor(nameColorBareback);
        }
        else {
            nameView.setTextColor(nameColorNormal);
        }

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
        actorView.setText(starText);

        if (item.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
            RecordType1v1 recordType1v1 = item.getRecordType1v1();
            sceneView.setText("" + item.getScene() + "(" + recordType1v1.getScoreScene() + ")");
            bjobView.setText("" + "bjob(" + recordType1v1.getScoreBjob() + ")");
            StringBuffer buffer = new StringBuffer();
            if (recordType1v1.getScoreFkType1() != 0) {
                buffer.append("坐面(").append(recordType1v1.getScoreFkType1()).append(")   ");
            }
            if (recordType1v1.getScoreFkType2() != 0) {
                buffer.append("坐背(").append(recordType1v1.getScoreFkType2()).append(")   ");
            }
            if (recordType1v1.getScoreFkType3() != 0) {
                buffer.append("立面(").append(recordType1v1.getScoreFkType3()).append(")   ");
            }
            if (recordType1v1.getScoreFkType4() != 0) {
                buffer.append("立背(").append(recordType1v1.getScoreFkType4()).append(")   ");
            }
            if (recordType1v1.getScoreFkType5() != 0) {
                buffer.append("侧(").append(recordType1v1.getScoreFkType5()).append(")   ");
            }
            if (recordType1v1.getScoreFkType6() != 0) {
                buffer.append("特殊(").append(recordType1v1.getScoreFkType6()).append(")   ");
            }
            String text = buffer.toString();
            if (text.length() > 0) {
                fkSubView.setText("" + text.substring(0, text.length() - 3));
            }
        }
        else if (item.getType() == DataConstants.VALUE_RECORD_TYPE_3W
                || item.getType() == DataConstants.VALUE_RECORD_TYPE_MULTI) {
            RecordType3w recordType3w = item.getRecordType3w();
            sceneView.setText("" + item.getScene() + "(" + recordType3w.getScoreScene() + ")");
            bjobView.setText("" + "bjob(" + recordType3w.getScoreBjob() + ")");
        }

        try {
            showSortScore(item, sortMode);
        } catch (Exception e) {

        }
    }

    private void bindCommon(Record item, String name) {
        container.setTag(item);
        if (onClickListener != null) {
            container.setOnClickListener(onClickListener);
        }

        // image
        String path;
        if (indexPackage == null) {
            indexPackage = new GdbImageProvider.IndexPackage();
            path = GdbImageProvider.getRecordRandomPath(name, indexPackage);
        }
        else {
            try {
                path = GdbImageProvider.getRecordPath(name, indexPackage.index);
            } catch (Exception e) {
                path = GdbImageProvider.getRecordRandomPath(name, indexPackage);
            }
        }

        RequestOptions recordOptions = GlideUtil.getRecordOptions();
        Glide.with(GdbApplication.getInstance())
                .load(path)
                .apply(recordOptions)
                .into(imageView);
    }

    private void showSortScore(Record item, int sortMode) throws NullPointerException{
        switch (sortMode) {
            case PreferenceValue.GDB_SR_ORDERBY_DATE:
                sortScoreView.setVisibility(View.VISIBLE);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = sdf.format(new Date(item.getLastModifyTime()));
                sortScoreView.setText(date);
                break;
            case PreferenceValue.GDB_SR_ORDERBY_BAREBACK:
                sortScoreView.setVisibility(View.VISIBLE);
                sortScoreView.setText("" + item.getScoreBareback());
                break;
            case PreferenceValue.GDB_SR_ORDERBY_BJOB:
                sortScoreView.setVisibility(View.VISIBLE);
                if (item.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                    sortScoreView.setText("" + item.getRecordType1v1().getScoreBjob());
                }
                else if (item.getType() == DataConstants.VALUE_RECORD_TYPE_3W
                        || item.getType() == DataConstants.VALUE_RECORD_TYPE_MULTI) {
                    sortScoreView.setText("" + item.getRecordType3w().getScoreBjob());
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_CSHOW:
                sortScoreView.setVisibility(View.VISIBLE);
                if (item.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                    sortScoreView.setText("" + item.getRecordType1v1().getScoreCshow());
                }
                else if (item.getType() == DataConstants.VALUE_RECORD_TYPE_3W
                        || item.getType() == DataConstants.VALUE_RECORD_TYPE_MULTI) {
                    sortScoreView.setText("" + item.getRecordType3w().getScoreCshow());
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_CUM:
                sortScoreView.setVisibility(View.VISIBLE);
                sortScoreView.setText("" + item.getScoreCum());
                break;
            case PreferenceValue.GDB_SR_ORDERBY_PASSION:
                sortScoreView.setVisibility(View.VISIBLE);
                sortScoreView.setText("" + item.getScorePassion());
                break;
            case PreferenceValue.GDB_SR_ORDERBY_FK1:
                sortScoreView.setVisibility(View.VISIBLE);
                if (item.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                    sortScoreView.setText("" + item.getRecordType1v1().getScoreFkType1());
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_FK2:
                sortScoreView.setVisibility(View.VISIBLE);
                if (item.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                    sortScoreView.setText("" + item.getRecordType1v1().getScoreFkType2());
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_FK3:
                sortScoreView.setVisibility(View.VISIBLE);
                if (item.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                    sortScoreView.setText("" + item.getRecordType1v1().getScoreFkType3());
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_FK4:
                sortScoreView.setVisibility(View.VISIBLE);
                if (item.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                    sortScoreView.setText("" + item.getRecordType1v1().getScoreFkType4());
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_FK5:
                sortScoreView.setVisibility(View.VISIBLE);
                if (item.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                    sortScoreView.setText("" + item.getRecordType1v1().getScoreFkType5());
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_FK6:
                sortScoreView.setVisibility(View.VISIBLE);
                if (item.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                    sortScoreView.setText("" + item.getRecordType1v1().getScoreFkType6());
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_FOREPLAY:
                sortScoreView.setVisibility(View.VISIBLE);
                if (item.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                    sortScoreView.setText("" + item.getRecordType1v1().getScoreForePlay());
                }
                else if (item.getType() == DataConstants.VALUE_RECORD_TYPE_3W
                        || item.getType() == DataConstants.VALUE_RECORD_TYPE_MULTI) {
                    sortScoreView.setText("" + item.getRecordType3w().getScoreForePlay());
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_HD:
                sortScoreView.setVisibility(View.VISIBLE);
                sortScoreView.setText("" + item.getHdLevel());
                break;
            case PreferenceValue.GDB_SR_ORDERBY_RHYTHM:
                sortScoreView.setVisibility(View.VISIBLE);
                if (item.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                    sortScoreView.setText("" + item.getRecordType1v1().getScoreRhythm());
                }
                else if (item.getType() == DataConstants.VALUE_RECORD_TYPE_3W
                        || item.getType() == DataConstants.VALUE_RECORD_TYPE_MULTI) {
                    sortScoreView.setText("" + item.getRecordType3w().getScoreRhythm());
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_RIM:
                sortScoreView.setVisibility(View.VISIBLE);
                if (item.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                    sortScoreView.setText("" + item.getRecordType1v1().getScoreRim());
                }
                else if (item.getType() == DataConstants.VALUE_RECORD_TYPE_3W
                        || item.getType() == DataConstants.VALUE_RECORD_TYPE_MULTI) {
                    sortScoreView.setText("" + item.getRecordType3w().getScoreRim());
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_SCENE:
                sortScoreView.setVisibility(View.VISIBLE);
                if (item.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                    sortScoreView.setText("" + item.getRecordType1v1().getScoreScene());
                }
                else if (item.getType() == DataConstants.VALUE_RECORD_TYPE_3W
                        || item.getType() == DataConstants.VALUE_RECORD_TYPE_MULTI) {
                    sortScoreView.setText("" + item.getRecordType3w().getScoreScene());
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_SCOREFEEL:
                sortScoreView.setVisibility(View.VISIBLE);
                sortScoreView.setText("" + item.getScoreFeel());
                break;
            case PreferenceValue.GDB_SR_ORDERBY_SPECIAL:
                sortScoreView.setVisibility(View.VISIBLE);
                sortScoreView.setText("" + item.getScoreSpecial());
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
                sortScoreView.setVisibility(View.VISIBLE);
                if (item.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                    sortScoreView.setText("" + item.getRecordType1v1().getScoreStory());
                }
                else if (item.getType() == DataConstants.VALUE_RECORD_TYPE_3W
                        || item.getType() == DataConstants.VALUE_RECORD_TYPE_MULTI) {
                    sortScoreView.setText("" + item.getRecordType3w().getScoreStory());
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_SCORE_BASIC:
                break;
            case PreferenceValue.GDB_SR_ORDERBY_SCORE_EXTRA:
                break;
            case PreferenceValue.GDB_SR_ORDERBY_STAR:
                sortScoreView.setVisibility(View.VISIBLE);
                sortScoreView.setText("" + item.getScoreStar());
                break;
            case PreferenceValue.GDB_SR_ORDERBY_STARC:
                break;
            default:
                sortScoreView.setVisibility(View.GONE);
                break;
        }

        if (sortMode == PreferenceValue.GDB_SR_ORDERBY_SCORE) {
            scoreView.setTextColor(nameColorBareback);
        }
        else {
            scoreView.setTextColor(nameColorNormal);
        }
    }

    public void hideIndexView() {
        seqView.setVisibility(View.INVISIBLE);
    }
}
