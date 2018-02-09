package com.jing.app.jjgallery.gdb.view.record;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.allure.lbanners.LMBanners;
import com.allure.lbanners.adapter.LBaseAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.jing.app.jjgallery.gdb.ActivityManager;
import com.jing.app.jjgallery.gdb.GBaseActivity;
import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.model.GdbImageProvider;
import com.jing.app.jjgallery.gdb.model.SettingProperties;
import com.jing.app.jjgallery.gdb.model.VideoModel;
import com.jing.app.jjgallery.gdb.presenter.record.RecordPresenter;
import com.jing.app.jjgallery.gdb.util.GlideApp;
import com.jing.app.jjgallery.gdb.util.GlideUtil;
import com.jing.app.jjgallery.gdb.util.LMBannerViewUtil;
import com.jing.app.jjgallery.gdb.view.pub.BannerAnimDialogFragment;
import com.jing.app.jjgallery.gdb.view.pub.PointDescLayout;
import com.jing.app.jjgallery.gdb.view.pub.dialog.VideoDialogFragment;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.RecordStar;
import com.king.app.gdb.data.entity.RecordType1v1;
import com.king.app.gdb.data.entity.RecordType3w;
import com.king.app.gdb.data.param.DataConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by JingYang on 2016/8/17 0017.
 * Description:
 */
public class RecordActivity extends GBaseActivity implements IRecordView {

    public static final String KEY_RECORD_ID = "key_record_id";

    @BindView(R.id.iv_record)
    ImageView ivRecord;
    @BindView(R.id.lmbanner)
    LMBanners lmBanners;
    @BindView(R.id.iv_play)
    ImageView ivPlay;
    @BindView(R.id.iv_setting)
    ImageView ivSetting;
    @BindView(R.id.iv_star1)
    CircularImageView ivStar1;
    @BindView(R.id.tv_star1)
    TextView tvStar1;
    @BindView(R.id.group_star1)
    LinearLayout groupStar1;
    @BindView(R.id.iv_star2)
    CircularImageView ivStar2;
    @BindView(R.id.tv_star2)
    TextView tvStar2;
    @BindView(R.id.group_star2)
    LinearLayout groupStar2;
    @BindView(R.id.tv_scene)
    TextView tvScene;
    @BindView(R.id.tv_scene_score)
    TextView tvSceneScore;
    @BindView(R.id.tv_score_total)
    TextView tvScoreTotal;
    @BindView(R.id.tv_deprecated)
    TextView tvDeprecated;
    @BindView(R.id.group_scene)
    RelativeLayout groupScene;
    @BindView(R.id.group_bareback)
    RelativeLayout groupBareback;
    @BindView(R.id.tv_path)
    TextView tvPath;
    @BindView(R.id.tv_cum)
    TextView tvCum;
    @BindView(R.id.tv_star)
    TextView tvStar;
    @BindView(R.id.tv_starc)
    TextView tvStarC;
    @BindView(R.id.tv_special)
    TextView tvSpecial;
    @BindView(R.id.tv_special_content)
    TextView tvSpecialContent;
    @BindView(R.id.group_special)
    RelativeLayout groupSpecial;
    @BindView(R.id.tv_fk)
    TextView tvFk;
    @BindView(R.id.tv_hd)
    TextView tvHd;
    @BindView(R.id.tv_basic)
    TextView tvBasic;
    @BindView(R.id.tv_feel)
    TextView tvFeel;
    @BindView(R.id.tv_bjob)
    TextView tvBjob;
    @BindView(R.id.tv_rhythm)
    TextView tvRhythm;
    @BindView(R.id.tv_foreplay)
    TextView tvForeplay;
    @BindView(R.id.tv_story)
    TextView tvStory;
    @BindView(R.id.tv_extra)
    TextView tvExtra;
    @BindView(R.id.tv_rim)
    TextView tvRim;
    @BindView(R.id.tv_cshow)
    TextView tvCshow;
    @BindView(R.id.group_fk)
    PointDescLayout groupFk;
    @BindView(R.id.group_record)
    RelativeLayout groupRecord;
    @BindView(R.id.iv_cum)
    ImageView ivCum;

    @BindView(R.id.group_star_1v1)
    LinearLayout groupStar1v1;
    @BindView(R.id.iv_3w_star1)
    CircularImageView iv3wStar1;
    @BindView(R.id.tv_3w_star1)
    TextView tv3wStar1;
    @BindView(R.id.group_3w_star1)
    LinearLayout group3wStar1;
    @BindView(R.id.iv_3w_star2)
    CircularImageView iv3wStar2;
    @BindView(R.id.tv_3w_star2)
    TextView tv3wStar2;
    @BindView(R.id.group_3w_star2)
    LinearLayout group3wStar2;
    @BindView(R.id.iv_3w_star3)
    CircularImageView iv3wStar3;
    @BindView(R.id.tv_3w_star3)
    TextView tv3wStar3;
    @BindView(R.id.group_3w_star3)
    LinearLayout group3wStar3;
    @BindView(R.id.group_star_3w)
    LinearLayout groupStar3w;
    @BindView(R.id.tv_3w_flag1)
    TextView tv3wFlag1;
    @BindView(R.id.tv_3w_flag2)
    TextView tv3wFlag2;
    @BindView(R.id.tv_3w_flag3)
    TextView tv3wFlag3;

    private Record record;
    private RecordPresenter mPresenter;

    private String videoPath;

    private List<String> headPathList;
    private BannerAnimDialogFragment bannerSettingDialog;

    private RequestOptions recordOptions;
    private RequestOptions starOptions;

    @Override
    public int getContentView() {
        return R.layout.gdb_record_1v1;
    }

    @Override
    public void initController() {
        mPresenter = new RecordPresenter(this);
        recordOptions = GlideUtil.getRecordOptions();
        starOptions = GlideUtil.getStarOptions();
    }

    @Override
    public Unbinder initView() {

        Unbinder unbinder = ButterKnife.bind(this);

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle("Record");

        mPresenter.loadRecord(getIntent().getLongExtra(KEY_RECORD_ID, -1));
        return unbinder;
    }

    @Override
    public void initBackgroundWork() {

    }

    @Override
    public boolean onSupportNavigateUp() {
        // don't use finish(), transition animation will be executed only by onBackPressed()
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mPresenter.loadRecord(intent.getLongExtra(KEY_RECORD_ID, -1));
    }

    @Override
    public void showRecord(Record record) {
        initHeadPart();

        // RecordOneVOne和RecordThree都是继承于RecordSingleScene
        if (record.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {

            groupStar3w.setVisibility(View.GONE);
            groupStar1v1.setVisibility(View.VISIBLE);
            initRecordOneVOne(record.getRecordType1v1());
        }
        else if (record.getType() == DataConstants.VALUE_RECORD_TYPE_3W) {
            groupStar3w.setVisibility(View.VISIBLE);
            groupStar1v1.setVisibility(View.GONE);
            initRecordThree(record.getRecordType3w(), record.getRelationList());
        }

        // Record公共部分
        tvScene.setText(record.getScene());
        tvPath.setText(record.getDirectory() + "/" + record.getName());
        tvHd.setText("" + record.getHdLevel());
        tvScoreTotal.setText("" + record.getScore());
        tvFeel.setText("" + record.getScoreFeel());
        if (record.getScoreBareback() > 0) {
            groupBareback.setVisibility(View.VISIBLE);
        } else {
            groupBareback.setVisibility(View.GONE);
        }
        tvCum.setText("" + record.getScoreCum());
        tvSpecial.setText("" + record.getScoreSpecial());
        if (TextUtils.isEmpty(record.getSpecialDesc())) {
            groupSpecial.setVisibility(View.GONE);
        } else {
            groupSpecial.setVisibility(View.VISIBLE);
            tvSpecialContent.setText(record.getSpecialDesc());
        }
        tvFk.setText("Passion(" + record.getScorePassion() + ")");
        tvStar.setText("" + record.getScoreStar());

        tvDeprecated.setVisibility(record.getDeprecated() == 1 ? View.VISIBLE : View.GONE);

        videoPath = VideoModel.getVideoPath(record.getName());
//        videoPath = "/storage/emulated/0/tencent/MicroMsg/WeiXin/wx_camera_1489199749192.mp4";
        if (videoPath == null) {
            ivPlay.setVisibility(View.GONE);
        } else {
            ivPlay.setVisibility(View.VISIBLE);
        }

        String cuPath = GdbImageProvider.getRecordCuPath(record.getName());
        if (!TextUtils.isEmpty(cuPath)) {
            ivCum.setVisibility(View.VISIBLE);
            GlideApp.with(this)
                    .asGif()
                    .load(cuPath)
                    .into(ivCum);
        }
    }

    private void initRecordOneVOne(RecordType1v1 record) {
        tvStory.setText("" + record.getScoreStory());
        tvSceneScore.setText("" + record.getScoreScene());
        tvBjob.setText("" + record.getScoreBjob());
        tvRhythm.setText("" + record.getScoreRhythm());
        tvForeplay.setText("" + record.getScoreForePlay());
        tvRim.setText("" + record.getScoreRim());
        tvCshow.setText("" + record.getScoreCshow());

        RecordStar top = mPresenter.getRelationTop();
        if (top == null) {
            tvStar1.setText(DataConstants.STAR_UNKNOWN);
        } else {
            tvStar1.setText(top.getStar().getName() + "(" + top.getScore() + "/" + top.getScoreC() + ")");
            Glide.with(this)
                    .load(GdbImageProvider.getStarRandomPath(top.getStar().getName(), null))
                    .apply(starOptions)
                    .into(ivStar1);
        }
        RecordStar bottom = mPresenter.getRelationBottom();
        if (bottom == null) {
            tvStar1.setText(DataConstants.STAR_UNKNOWN);
        } else {
            tvStar1.setText(bottom.getStar().getName() + "(" + bottom.getScore() + "/" + bottom.getScoreC() + ")");
            Glide.with(this)
                    .load(GdbImageProvider.getStarRandomPath(bottom.getStar().getName(), null))
                    .apply(starOptions)
                    .into(ivStar2);
        }

        initFkDetails(record);
    }

    private void initRecordThree(RecordType3w record, List<RecordStar> starList) {
        tvStory.setText("" + record.getScoreStory());
        tvSceneScore.setText("" + record.getScoreScene());
        tvStory.setText("" + record.getScoreStory());
        tvSceneScore.setText("" + record.getScoreScene());
        tvBjob.setText("" + record.getScoreBjob());
        tvRhythm.setText("" + record.getScoreRhythm());
        tvForeplay.setText("" + record.getScoreForePlay());
        tvRim.setText("" + record.getScoreRim());
        tvCshow.setText("" + record.getScoreCshow());

        // load star
        if (starList.size() > 0) {
            RecordStar star = starList.get(0);
            tv3wFlag1.setText(mPresenter.getRelationFlag(star));
            tv3wStar1.setText(star.getStar().getName() + "(" + star.getScore() + "/" + star.getScoreC() + ")");
            Glide.with(this)
                    .load(GdbImageProvider.getStarRandomPath(star.getStar().getName(), null))
                    .apply(starOptions)
                    .into(ivStar2);
        } else {
            tv3wFlag1.setText(DataConstants.STAR_UNKNOWN);
            tv3wStar1.setText(DataConstants.STAR_UNKNOWN);
        }
        if (starList.size() > 1) {
            RecordStar star = starList.get(1);
            tv3wFlag2.setText(mPresenter.getRelationFlag(star));
            tv3wStar2.setText(star.getStar().getName() + "(" + star.getScore() + "/" + star.getScoreC() + ")");
            Glide.with(this)
                    .load(GdbImageProvider.getStarRandomPath(star.getStar().getName(), null))
                    .apply(starOptions)
                    .into(ivStar2);
        } else {
            tv3wFlag2.setText(DataConstants.STAR_UNKNOWN);
            tv3wStar2.setText(DataConstants.STAR_UNKNOWN);
        }
        if (starList.size() > 2) {
            RecordStar star = starList.get(2);
            tv3wFlag3.setText(mPresenter.getRelationFlag(star));
            tv3wStar3.setText(star.getStar().getName() + "(" + star.getScore() + "/" + star.getScoreC() + ")");
            Glide.with(this)
                    .load(GdbImageProvider.getStarRandomPath(star.getStar().getName(), null))
                    .apply(starOptions)
                    .into(ivStar2);
        } else {
            tv3wFlag3.setText(DataConstants.STAR_UNKNOWN);
            tv3wStar3.setText(DataConstants.STAR_UNKNOWN);
        }
        initFkDetails(record);
    }

    private void initHeadPart() {
        boolean showImage;
        if (GdbImageProvider.hasRecordFolder(record.getName())) {
            headPathList = GdbImageProvider.getRecordPathList(record.getName());
            if (headPathList.size() <= 1) {
                showImage = true;
            } else {
                showImage = false;
                lmBanners.setVisibility(View.VISIBLE);
                ivSetting.setVisibility(View.VISIBLE);
                ivRecord.setVisibility(View.GONE);
                initBanner(headPathList);
            }
        } else {
            showImage = true;
        }

        if (showImage) {
            lmBanners.setVisibility(View.GONE);
            ivSetting.setVisibility(View.GONE);
            ivRecord.setVisibility(View.VISIBLE);
            String path = GdbImageProvider.getRecordRandomPath(record.getName(), null);
            Glide.with(this)
                    .load(path)
                    .apply(recordOptions)
                    .into(ivRecord);
        }
    }

    private void initBanner(List<String> pathList) {
        // 禁用btnStart(只在onPageScroll触发后有效)
        lmBanners.isGuide(false);
        // 显示引导圆点
//        lmBanners.hideIndicatorLayout();
        lmBanners.setIndicatorPosition(LMBanners.IndicaTorPosition.BOTTOM_MID);
        // 可以不写，因为文件名直接覆用的LMBanners-1.0.8里的res
        lmBanners.setSelectIndicatorRes(R.drawable.page_indicator_select);
        lmBanners.setUnSelectUnIndicatorRes(R.drawable.page_indicator_unselect);
        // 轮播切换时间
        lmBanners.setDurtion(SettingProperties.getGdbRecordNavAnimTime(this));
        if (SettingProperties.isGdbRecordNavAnimRandom(this)) {
            Random random = new Random();
            int type = Math.abs(random.nextInt()) % LMBannerViewUtil.ANIM_TYPES.length;
            LMBannerViewUtil.setScrollAnim(lmBanners, type);
        } else {
            LMBannerViewUtil.setScrollAnim(lmBanners, SettingProperties.getGdbRecordNavAnimType(this));
        }

        HeadBannerAdapter adapter = new HeadBannerAdapter();
        lmBanners.setAdapter(adapter, pathList);
    }

    private void initFkDetails(RecordType1v1 record) {
        List<String> keyList = new ArrayList<>();
        List<String> contentList = new ArrayList<>();
        if (record.getScoreFkType1() > 0) {
            keyList.add("For Sit");
            contentList.add(record.getScoreFkType1() + "");
        }
        if (record.getScoreFkType2() > 0) {
            keyList.add("Back Sit");
            contentList.add(record.getScoreFkType2() + " ");
        }
        if (record.getScoreFkType3() > 0) {
            keyList.add("For Stand");
            contentList.add(record.getScoreFkType3() + " ");
        }
        if (record.getScoreFkType4() > 0) {
            keyList.add("Back Stand");
            contentList.add(record.getScoreFkType4() + " ");
        }
        if (record.getScoreFkType5() > 0) {
            keyList.add("Side");
            contentList.add(record.getScoreFkType5() + " ");
        }
        if (record.getScoreFkType6() > 0) {
            keyList.add("Special");
            contentList.add(record.getScoreFkType6() + " ");
        }
        groupFk.addPoint(keyList, contentList);
    }

    private void initFkDetails(RecordType3w record) {
        List<String> keyList = new ArrayList<>();
        List<String> contentList = new ArrayList<>();
        if (record.getScoreFkType1() > 0) {
            keyList.add("For Sit");
            contentList.add(record.getScoreFkType1() + "");
        }
        if (record.getScoreFkType2() > 0) {
            keyList.add("Back Sit");
            contentList.add(record.getScoreFkType2() + " ");
        }
        if (record.getScoreFkType3() > 0) {
            keyList.add("For");
            contentList.add(record.getScoreFkType3() + " ");
        }
        if (record.getScoreFkType4() > 0) {
            keyList.add("Back");
            contentList.add(record.getScoreFkType4() + " ");
        }
        if (record.getScoreFkType5() > 0) {
            keyList.add("Side");
            contentList.add(record.getScoreFkType5() + " ");
        }
        if (record.getScoreFkType6() > 0) {
            keyList.add("Double");
            contentList.add(record.getScoreFkType6() + " ");
        }
        if (record.getScoreFkType7() > 0) {
            keyList.add("Sequence");
            contentList.add(record.getScoreFkType6() + " ");
        }
        if (record.getScoreFkType8() > 0) {
            keyList.add("Special");
            contentList.add(record.getScoreFkType6() + " ");
        }
        groupFk.addPoint(keyList, contentList);
    }

    @OnClick({R.id.group_star1, R.id.group_star2, R.id.group_scene
        , R.id.group_3w_star1, R.id.group_3w_star2, R.id.group_3w_star3})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.group_star1:
                ActivityManager.startStarActivity(RecordActivity.this, mPresenter.getRelationTop().getStarId());
                break;
            case R.id.group_star2:
                ActivityManager.startStarActivity(RecordActivity.this, mPresenter.getRelationBottom().getStarId());
                break;
            case R.id.group_scene:
                break;
            case R.id.group_3w_star1:
                RecordStar relation = mPresenter.getRelation(1);
                if (relation != null) {
                    ActivityManager.startStarActivity(RecordActivity.this, relation.getStarId());
                }
                break;
            case R.id.group_3w_star2:
                relation = mPresenter.getRelation(2);
                if (relation != null) {
                    ActivityManager.startStarActivity(RecordActivity.this, relation.getStarId());
                }
                break;
            case R.id.group_3w_star3:
                relation = mPresenter.getRelation(3);
                if (relation != null) {
                    ActivityManager.startStarActivity(RecordActivity.this, relation.getStarId());
                }
                break;
        }
    }

    @OnClick(R.id.iv_play)
    public void onClickPlay() {
        VideoDialogFragment dialog = new VideoDialogFragment();
        dialog.setRecord(record);
        dialog.setVideoPath(videoPath);
        dialog.show(getSupportFragmentManager(), "VideoDialogFragment");
    }

    @OnClick(R.id.iv_setting)
    public void onClickSetting() {
        showSettingDialog();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (lmBanners != null) {
            lmBanners.stopImageTimerTask();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (lmBanners != null) {
            lmBanners.startImageTimerTask();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (lmBanners != null) {
            lmBanners.clearImageTimerTask();
        }
    }

    private void showSettingDialog() {
        if (bannerSettingDialog == null) {
            bannerSettingDialog = new BannerAnimDialogFragment();
            bannerSettingDialog.setOnAnimSettingListener(new BannerAnimDialogFragment.OnAnimSettingListener() {
                @Override
                public void onRandomAnim(boolean random) {
                    SettingProperties.setGdbRecordNavAnimRandom(RecordActivity.this, random);
                }

                @Override
                public boolean isRandomAnim() {
                    return SettingProperties.isGdbRecordNavAnimRandom(RecordActivity.this);
                }

                @Override
                public int getAnimType() {
                    return SettingProperties.getGdbRecordNavAnimType(RecordActivity.this);
                }

                @Override
                public void onSaveAnimType(int type) {
                    SettingProperties.setGdbRecordNavAnimType(RecordActivity.this, type);
                }

                @Override
                public int getAnimTime() {
                    return SettingProperties.getGdbRecordNavAnimTime(RecordActivity.this);
                }

                @Override
                public void onSaveAnimTime(int time) {
                    SettingProperties.setGdbRecordNavAnimTime(RecordActivity.this, time);
                }

                @Override
                public void onParamsSaved() {
                    initBanner(headPathList);
                }
            });
        }
        bannerSettingDialog.show(getSupportFragmentManager(), "BannerAnimDialogFragment");
    }

    private class HeadBannerAdapter implements LBaseAdapter<String> {

        @Override
        public View getView(LMBanners lBanners, Context context, int position, String path) {
            View view = LayoutInflater.from(context).inflate(R.layout.adapter_gdb_star_list_banner, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.iv_star);

            Glide.with(GdbApplication.getInstance())
                    .load(path)
                    .apply(recordOptions)
                    .into(imageView);
            return view;
        }
    }
}
