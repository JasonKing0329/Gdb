package com.jing.app.jjgallery.gdb.view.record.pad;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jing.app.jjgallery.gdb.ActivityManager;
import com.jing.app.jjgallery.gdb.MvpActivity;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.model.VideoModel;
import com.jing.app.jjgallery.gdb.presenter.record.RecordPresenter;
import com.jing.app.jjgallery.gdb.util.ScreenUtils;
import com.jing.app.jjgallery.gdb.view.pub.PointDescLayout;
import com.jing.app.jjgallery.gdb.view.record.phone.IRecordView;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.RecordStar;
import com.king.app.gdb.data.entity.RecordType1v1;
import com.king.app.gdb.data.entity.RecordType3w;
import com.king.app.gdb.data.param.DataConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @desc
 * @auth 景阳
 * @time 2018/2/14 0014 10:29
 */

public class RecordPadActivity extends MvpActivity<RecordPresenter> implements IRecordView {

    public static final String KEY_RECORD_ID = "key_record_id";

    @BindView(R.id.rv_images)
    RecyclerView rvImages;
    @BindView(R.id.rv_stars)
    RecyclerView rvStars;
    @BindView(R.id.tv_score_total)
    TextView tvScoreTotal;
    @BindView(R.id.tv_deprecated)
    TextView tvDeprecated;
    @BindView(R.id.tv_scene)
    TextView tvScene;
    @BindView(R.id.tv_scene_score)
    TextView tvSceneScore;
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
    @BindView(R.id.tv_special)
    TextView tvSpecial;
    @BindView(R.id.tv_special_content)
    TextView tvSpecialContent;
    @BindView(R.id.group_special)
    RelativeLayout groupSpecial;
    @BindView(R.id.tv_fk)
    TextView tvFk;
    @BindView(R.id.group_fk)
    PointDescLayout groupFk;
    @BindView(R.id.tv_hd)
    TextView tvHd;
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
    @BindView(R.id.tv_rim)
    TextView tvRim;
    @BindView(R.id.tv_cshow)
    TextView tvCshow;
    @BindView(R.id.iv_play)
    ImageView ivPlay;

    private String videoPath;

    private RecordPadAdapter recordPadAdapter;
    private RecordStarAdapter starAdapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_record_pad;
    }

    @Override
    protected void initView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvImages.setLayoutManager(manager);
        rvImages.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = ScreenUtils.dp2px(10);
                outRect.bottom = ScreenUtils.dp2px(10);
            }
        });
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvStars.setLayoutManager(manager);
    }

    @Override
    protected RecordPresenter createPresenter() {
        return new RecordPresenter();
    }

    @Override
    protected void initData() {
        presenter.loadRecord(getIntent().getLongExtra(KEY_RECORD_ID, -1));
    }

    @Override
    public void showRecord(Record record) {

        showImages(record);

        showStars(record);

        showValues(record);
    }

    private void showImages(Record record) {
        recordPadAdapter = new RecordPadAdapter();
        recordPadAdapter.setPathList(presenter.getRecordImages());
        rvImages.setAdapter(recordPadAdapter);
    }

    private void showStars(Record record) {
        starAdapter = new RecordStarAdapter();
        starAdapter.setList(record.getRelationList());
        starAdapter.setOnStarItemListener(new RecordStarAdapter.OnStarItemListener() {
            @Override
            public void onClickStar(RecordStar relation) {
                ActivityManager.startStarActivity(RecordPadActivity.this, relation.getStar());
            }
        });
        rvStars.setAdapter(starAdapter);
    }

    private void showValues(Record record) {
        // RecordOneVOne和RecordThree都是继承于RecordSingleScene
        if (record.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
            initRecordOneVOne(record.getRecordType1v1());
        } else if (record.getType() == DataConstants.VALUE_RECORD_TYPE_3W) {
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

    }

    private void initRecordOneVOne(RecordType1v1 record) {
        tvStory.setText("" + record.getScoreStory());
        tvSceneScore.setText("" + record.getScoreScene());
        tvBjob.setText("" + record.getScoreBjob());
        tvRhythm.setText("" + record.getScoreRhythm());
        tvForeplay.setText("" + record.getScoreForePlay());
        tvRim.setText("" + record.getScoreRim());
        tvCshow.setText("" + record.getScoreCshow());

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

        initFkDetails(record);
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

    @OnClick(R.id.group_scene)
    public void onClick() {

    }
}
