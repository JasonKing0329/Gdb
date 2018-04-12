package com.jing.app.jjgallery.gdb.view.record.pad;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.jing.app.jjgallery.gdb.ActivityManager;
import com.jing.app.jjgallery.gdb.FavorPopupMvpActivity;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.model.VideoModel;
import com.jing.app.jjgallery.gdb.model.palette.PaletteCallback;
import com.jing.app.jjgallery.gdb.model.palette.PaletteRequestListener;
import com.jing.app.jjgallery.gdb.model.palette.PaletteResponse;
import com.jing.app.jjgallery.gdb.model.palette.ViewColorBound;
import com.jing.app.jjgallery.gdb.presenter.record.RecordPresenter;
import com.jing.app.jjgallery.gdb.util.ColorUtils;
import com.jing.app.jjgallery.gdb.util.GlideApp;
import com.jing.app.jjgallery.gdb.util.ListUtil;
import com.jing.app.jjgallery.gdb.util.ScreenUtils;
import com.jing.app.jjgallery.gdb.view.pub.PointDescLayout;
import com.jing.app.jjgallery.gdb.view.pub.dialog.VideoDialogFragment;
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
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/4/11 9:49
 */
public class RecordPageActivity extends FavorPopupMvpActivity<RecordPresenter> implements IRecordView {

    public static final String KEY_RECORD_ID = "key_record_id";

    @BindView(R.id.banner)
    ConvenientBanner banner;
    @BindView(R.id.iv_play)
    ImageView ivPlay;
    @BindView(R.id.iv_order)
    ImageView ivOrder;
    @BindView(R.id.group_fk)
    PointDescLayout groupFk;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_parent)
    TextView tvParent;
    @BindView(R.id.tv_score)
    TextView tvScore;
    @BindView(R.id.tv_scene)
    TextView tvScene;
    @BindView(R.id.tv_bareback)
    TextView tvBareback;
    @BindView(R.id.group_bottom)
    ViewGroup groupBottom;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.rv_stars)
    RecyclerView rvStars;
    @BindView(R.id.rv_stars_detail)
    RecyclerView rvStarsDetail;
    @BindView(R.id.rv_score_detail)
    RecyclerView rvScoreDetail;
    @BindView(R.id.group_detail)
    ViewGroup groupDetail;

    private RecordPageStarAdapter starAdapter;
    private RecordPageStarDetailAdapter starDetailAdapter;
    private RecordPageScoreDetailAdapter scoreDetailAdapter;

    private String videoPath;
    private boolean isFirstTimeLoadFirstPage = true;

    @Override
    protected int getContentView() {
        return R.layout.activity_record_page_pad;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        initData();
    }

    @Override
    protected void initView() {
        ColorUtils.updateIconColor(ivBack, getResources().getColor(R.color.colorPrimary));
        ColorUtils.updateIconColor(ivOrder, getResources().getColor(R.color.colorPrimary));

        // 禁止添加即显示(要改变颜色以及允许动画)
        groupFk.disableInstantShow();
        groupFk.registerItemAnimation(getPointAnim());
        groupFk.setVisibility(View.INVISIBLE);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvStars.setLayoutManager(manager);
        rvStars.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                if (position > 0) {
                    outRect.left = ScreenUtils.dp2px(10);
                }
            }
        });

        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvStarsDetail.setLayoutManager(manager);
        rvStarsDetail.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                if (position > 0) {
                    outRect.top = ScreenUtils.dp2px(10);
                }
            }
        });

        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvScoreDetail.setLayoutManager(manager);
        rvScoreDetail.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                if (position > 0) {
                    outRect.top = ScreenUtils.dp2px(10);
                }
            }
        });

        banner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                // 第一次触发时，holder里image还没有加载完成，presenter里还没有缓存
                if (isFirstTimeLoadFirstPage && position == 0) {
                    isFirstTimeLoadFirstPage = false;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            presenter.refreshBackground(position);
                        }
                    }, 900);
                }
                else {
                    presenter.refreshBackground(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        banner.startTurning(5000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        banner.stopTurning();
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
    public void loadBackground(PaletteResponse paletteResponse) {
        if (paletteResponse != null) {
            if (!ListUtil.isEmpty(paletteResponse.viewColorBounds)) {
                for (ViewColorBound bound:paletteResponse.viewColorBounds) {
                    ColorUtils.updateIconColor((ImageView) bound.view, bound.color);
                }
            }
            updateColorsBy(paletteResponse.palette);
        }

        if (groupFk.getVisibility() == View.VISIBLE) {
            groupFk.showItems(false);
        }
        // 首次出现，加载动画
        else {
            groupFk.setVisibility(View.VISIBLE);
            groupFk.showItems(true);
        }
    }

    @Override
    public void showRecord(final Record record) {

        // 延迟一些效果更好
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 如果是在xml里就注册了layoutAnimation，那么延时就不起作用，所以在这里才注册anim
                LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(RecordPageActivity.this, R.anim.layout_pad_simple_stars);
                rvStars.setLayoutAnimation(controller);
                showStars(record);
            }
        }, 1000);

        showValues(record);
    }

    private void showStars(Record record) {
        starAdapter = new RecordPageStarAdapter();
        starAdapter.setList(record.getRelationList());
        starAdapter.setOnStarItemListener(new RecordPageStarAdapter.OnStarItemListener() {
            @Override
            public void onClickStar(RecordStar relation) {
                ActivityManager.startStarPageActivity(RecordPageActivity.this, relation.getStar().getId());
            }
        });
        rvStars.setAdapter(starAdapter);

        starDetailAdapter = new RecordPageStarDetailAdapter();
        starDetailAdapter.setList(record.getRelationList());
        starDetailAdapter.setOnStarItemListener(new RecordPageStarDetailAdapter.OnStarItemListener() {
            @Override
            public void onClickStar(RecordStar relation) {
                ActivityManager.startStarPageActivity(RecordPageActivity.this, relation.getStar().getId());
            }
        });
        rvStarsDetail.setAdapter(starDetailAdapter);
    }

    private void showValues(Record record) {
        tvName.setText(record.getName());
        if (record.getDeprecated() == DataConstants.DEPRECATED) {
            tvParent.setText("(Deprecated)  " + record.getDirectory());
        }
        else {
            tvParent.setText(record.getDirectory());
        }
        tvScore.setText(String.valueOf(record.getScore()));
        tvBareback.setVisibility(record.getDeprecated() == DataConstants.DEPRECATED ? View.VISIBLE:View.GONE);
        tvScene.setText(record.getScene());

        if (record.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
            initRecordOneVOne(record.getRecordType1v1());
        } else if (record.getType() == DataConstants.VALUE_RECORD_TYPE_3W
                || record.getType() == DataConstants.VALUE_RECORD_TYPE_MULTI) {
            initRecordThree(record.getRecordType3w());
        }

        showScoreDetails();

        videoPath = VideoModel.getVideoPath(record.getName());
        if (videoPath == null) {
            ivPlay.setVisibility(View.GONE);
        } else {
            ivPlay.setVisibility(View.VISIBLE);
        }

        banner.setPages(new CBViewHolderCreator<PageHolder>() {
            @Override
            public PageHolder createHolder() {
                return new PageHolder(new PaletteCallback() {
                    // 注册那些需要根据在背景图上的位置而改变响应颜色的view
                    // 采取view所在区域颜色平均值是否是深色，从而返回view应该使用的颜色（现在只支持黑/白两种）
                    // register views to change color by its position on the background image
                    // the algorithm to pick color is based on whether its average color is deep in that area, deep then white, otherwise, black
                    @Override
                    public List<View> getTargetViews() {
                        List<View> list = new ArrayList<>();
                        list.add(ivBack);
                        list.add(ivOrder);
                        return list;
                    }

                    @Override
                    public void noPaletteResponseLoaded(int position) {
                        presenter.removePaletteCache(position);
                    }

                    @Override
                    public void onPaletteResponse(int position, PaletteResponse response) {
                        presenter.cachePaletteResponse(position, response);
                    }
                });
            }
        }, presenter.getImageList());
    }

    public static class PageHolder implements Holder<String> {

        private ImageView imageView;

        private PaletteCallback callback;

        public PageHolder(PaletteCallback callback) {
            this.callback = callback;
        }

        @Override
        public View createView(Context context) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, int position, String data) {
            GlideApp.with(context)
                    .asBitmap()
                    .load(data)
                    // need load Palette and special colors for specific views
                    .listener(new PaletteRequestListener(position, callback))
                    .into(imageView);
        }

    }

    private void showScoreDetails() {
        scoreDetailAdapter = new RecordPageScoreDetailAdapter();
        scoreDetailAdapter.setList(presenter.getScoreDetails());
        rvScoreDetail.setAdapter(scoreDetailAdapter);

        // it will be a little stuck as soon as activity started, it's better delay executing animation
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                groupDetail.startAnimation(getDetailAppear());
            }
        }, 1000);
    }

    private void initRecordOneVOne(RecordType1v1 recordType1v1) {
        initFkDetails(recordType1v1);
    }

    private void initRecordThree(RecordType3w recordType3w) {
        initFkDetails(recordType3w);
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
        groupFk.removeAllViews();
        groupFk.addPoints(keyList, contentList);
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
        groupFk.removeAllViews();
        groupFk.addPoints(keyList, contentList);
    }

    /**
     * apply suitable color for bottom bar background, text color, icon color
     * judged by background resource
     * @param palette
     */
    private void updateColorsBy(Palette palette) {
        if (palette == null) {
            return;
        }
        Palette.Swatch vibrant = palette.getVibrantSwatch();
        if (vibrant == null) {
            Palette.Swatch mute = palette.getMutedSwatch();
            if (mute == null) {
                tvName.setTextColor(getResources().getColor(R.color.white));
                tvParent.setTextColor(getResources().getColor(R.color.white));
                tvBareback.setTextColor(getResources().getColor(R.color.white));
                tvScene.setTextColor(getResources().getColor(R.color.white));
                tvScore.setTextColor(getResources().getColor(R.color.white));
                groupBottom.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            } else {
                tvName.setTextColor(mute.getTitleTextColor());
                tvParent.setTextColor(mute.getBodyTextColor());
                tvBareback.setTextColor(mute.getBodyTextColor());
                tvScene.setTextColor(mute.getBodyTextColor());
                tvScore.setTextColor(mute.getTitleTextColor());
                groupBottom.setBackgroundColor(mute.getRgb());
            }
        } else {
            tvName.setTextColor(vibrant.getTitleTextColor());
            tvParent.setTextColor(vibrant.getBodyTextColor());
            tvBareback.setTextColor(vibrant.getBodyTextColor());
            tvScene.setTextColor(vibrant.getBodyTextColor());
            tvScore.setTextColor(vibrant.getTitleTextColor());
            groupBottom.setBackgroundColor(vibrant.getRgb());
        }
        groupFk.setSwatches(palette.getSwatches());
    }

    @OnClick(R.id.tv_scene)
    public void onClickScene() {
        ActivityManager.startRecordListPadActivity(this, presenter.getRecord().getScene());
    }

    @OnClick(R.id.iv_back)
    public void onClickBack() {
        onBackPressed();
    }

    @OnClick(R.id.tv_score)
    public void onClickScore() {
        if (groupDetail.getVisibility() == View.VISIBLE) {
            groupDetail.startAnimation(getDetailDisappear());
        }
        else {
            groupDetail.startAnimation(getDetailAppear());
        }
    }

    @OnClick(R.id.iv_play)
    public void onClickPlay() {
        VideoDialogFragment dialog = new VideoDialogFragment();
        dialog.setRecord(presenter.getRecord());
        dialog.setVideoPath(videoPath);
        dialog.show(getSupportFragmentManager(), "VideoDialogFragment");
    }

    @OnClick(R.id.iv_order)
    public void onClickOrder(View view) {
        getFavorPopup().popupRecord(this, view, presenter.getRecord().getId());
    }

    /**
     * appear animation of groupDetail
     * @return
     */
    private Animation getDetailAppear() {
        groupDetail.setVisibility(View.VISIBLE);
        AnimationSet set = new AnimationSet(true);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.setDuration(500);
        Animation translation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0
            , Animation.RELATIVE_TO_SELF, -1.5f, Animation.RELATIVE_TO_SELF, 0);
        set.addAnimation(translation);
        Animation scale = new ScaleAnimation(0, 1, 0, 1
                , Animation.RELATIVE_TO_SELF, 0.1f, Animation.RELATIVE_TO_SELF, 1);
        set.addAnimation(scale);
        return set;
    }

    /**
     * disappear animation of groupDetail
     * @return
     */
    private Animation getDetailDisappear() {
        AnimationSet set = new AnimationSet(true);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.setDuration(500);
        Animation translation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0
                , Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1.5f);
        set.addAnimation(translation);
        Animation scale = new ScaleAnimation(1, 0, 1, 0
                , Animation.RELATIVE_TO_SELF, 0.1f, Animation.RELATIVE_TO_SELF, 1);
        set.addAnimation(scale);
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                groupDetail.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return set;
    }

    /**
     * appear animation for item of groupFk
     * @return
     */
    private Animation getPointAnim() {
        AnimationSet set = new AnimationSet(true);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.setDuration(500);
        Animation scale = new ScaleAnimation(0, 1, 0, 1
                , Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 1);
        set.addAnimation(scale);
        return set;
    }

}
