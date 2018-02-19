package com.jing.app.jjgallery.gdb.view.recommend;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.allure.lbanners.LMBanners;
import com.allure.lbanners.adapter.LBaseAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jing.app.jjgallery.gdb.ActivityManager;
import com.jing.app.jjgallery.gdb.BaseFragmentV4;
import com.jing.app.jjgallery.gdb.GBaseActivity;
import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.IFragmentHolder;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.model.FilterHelper;
import com.jing.app.jjgallery.gdb.model.GdbImageProvider;
import com.jing.app.jjgallery.gdb.model.SettingProperties;
import com.jing.app.jjgallery.gdb.presenter.GdbGuidePresenter;
import com.jing.app.jjgallery.gdb.util.DisplayHelper;
import com.jing.app.jjgallery.gdb.util.GlideUtil;
import com.jing.app.jjgallery.gdb.util.LMBannerViewUtil;
import com.jing.app.jjgallery.gdb.util.ListUtil;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.Star;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2016/12/26 0026.
 */

public class RecommendFragment extends BaseFragmentV4 implements IRecommend, View.OnClickListener {

    private ProgressBar progressBar;
    private LMBanners lmBanners;
    private ItemAdapter itemAdapter;

    private GdbGuidePresenter gdbGuidePresenter;
    private IRecommendHolder recommendHolder;
    private FilterHelper filterHelper;

    /**
     * 过滤器对话框
     */
    private RecordFilterDialogFragment filterDialog;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {
        recommendHolder = (IRecommendHolder) holder;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.dialog_gdb_recommand;
    }

    @Override
    protected void initView(View view) {
        lmBanners = (LMBanners) view.findViewById(R.id.gdb_rec_banners);
        initBanner();
        
        progressBar = (ProgressBar) view.findViewById(R.id.gdb_recommend_progress);
        view.findViewById(R.id.gdb_recommend_previous).setOnClickListener(this);
        view.findViewById(R.id.gdb_recommend_next).setOnClickListener(this);
        view.findViewById(R.id.gdb_recommend_setting).setOnClickListener(this);

        gdbGuidePresenter = recommendHolder.getPresenter();
        gdbGuidePresenter.setRecommendView(this);

        filterHelper = new FilterHelper();
        // 设置过滤器
        gdbGuidePresenter.setFilterModel(filterHelper.getFilters());
        // 加载所有记录，通过onRecordRecommand回调
        gdbGuidePresenter.initialize();
    }

    @Override
    public void onRecordsLoaded(List<Record> list) {
        recommendHolder.onRecommendRecordsLoaded();
        gdbGuidePresenter.recommendNext();
    }

    @Override
    public void onRecordRecommand(Record record) {
        progressBar.setVisibility(View.GONE);

        // 采用getView时生成随机推荐，这里初始化3个item就够了（LMBanner内部也是根据view pager设置下标
        // 来循环的）
        List<Record> list = new ArrayList<>();
        list.add(record);
        list.add(record);
        list.add(record);
        itemAdapter = new ItemAdapter();
        lmBanners.setAdapter(itemAdapter, list);

        // 这里一定要加载完后再设置可见，因为LMBanners的内部代码里有一个btnStart，本来是受isGuide的控制
        // 但是1.0.8版本里只在onPageScroll里面判断了这个属性。导致如果一开始LMBanners处于可见状态，
        // adapter里还没有数据，btnStart就会一直显示在那里，知道开始触发onPageScroll才会隐藏
        // 本来引入library，在setGuide把btnStart的visibility置为gone就可以了，但是这个项目已经引入了很多module了，就不再引入了
        lmBanners.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRecordsLoadedFailed(String message) {
        showToastLong("Load recommend failed: " + message);
    }

    private String getRecordStarText(Record record) {

        List<Star> starList = record.getStarList();
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
        return starText;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gdb_recommend_next:
                // LMBanner没有提供设置翻滚的方法
//                gdbGuidePresenter.recommendNext();
                break;
            case R.id.gdb_recommend_previous:
                // LMBanner没有提供设置翻滚的方法
//                gdbGuidePresenter.recommendPrevious();
                break;
            case R.id.gdb_recommend_setting:
                if (filterDialog == null) {
                    filterDialog = new RecordFilterDialogFragment();
                    filterDialog.setOnRecordFilterActionListener(new RecordFilterDialogFragment.OnRecordFilterActionListener() {
                        @Override
                        public void onSaveFilterModel(com.jing.app.jjgallery.gdb.model.bean.recommend.FilterModel model) {
                            gdbGuidePresenter.setFilterModel(model);
                            gdbGuidePresenter.recommendNext();
                        }
                    });

                    filterDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            // filterDialog中animation是即时保存的，需要在对话框消失的时候重新执行banner的相关设定
                            initBanner();
                        }
                    });
                }

                filterDialog.show(getFragmentManager(), "RecordFilterDialogFragment");
                break;
        }
    }

    /**
     * LMBanner并没有完全像BaseAdapter那样设计结构
     * 也无法采用ViewHolder缓存机制，本处只用了3个item，直接这样也就行了
     */
    private class ItemAdapter implements LBaseAdapter<Record>, View.OnClickListener {

        private ImageView imageView;
        private TextView starView;
        private ViewGroup group;
        private RequestOptions recordOptions;

        public ItemAdapter() {
            recordOptions = GlideUtil.getRecordOptions();
        }

        @Override
        public View getView(LMBanners lBanners, Context context, int position, Record data) {
            View view = LayoutInflater.from(context).inflate(R.layout.adapter_gdb_recommend_item, null);

            imageView = (ImageView) view.findViewById(R.id.gdb_recommend_image);
            starView = (TextView) view.findViewById(R.id.gdb_recommend_star);
            group = (ViewGroup) view.findViewById(R.id.gdb_recommend_click_group);

            // 采用随机生成模式
            data = gdbGuidePresenter.newRecord();
            // 没有匹配的记录
            if (data == null) {
                ((GBaseActivity) getActivity()).showToastLong(getString(R.string.gdb_rec_no_match));
                return view;
            }

            Glide.with(GdbApplication.getInstance())
                    .load(GdbImageProvider.getRecordRandomPath(data.getName(), null))
                    .apply(recordOptions)
                    .into(imageView);

            starView.setText(getRecordStarText(data));

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (DisplayHelper.isTabModel(getActivity())) {
                        ActivityManager.startRecordListPadActivity(getActivity());
                    }
                    else {
                        ActivityManager.startRecordListActivity(getActivity());
                    }
                }
            });

            group.setTag(data);
            group.setOnClickListener(this);
            return view;
        }

        @Override
        public void onClick(View v) {
            Record record = (Record) v.getTag();
            // start record activity
            ActivityManager.startRecordActivity(getContext(), record);
        }
    }

    private void initBanner() {
        // 禁用btnStart(只在onPageScroll触发后有效)
        lmBanners.isGuide(false);
        // 不显示引导圆点
        lmBanners.hideIndicatorLayout();
        // 轮播切换时间
        lmBanners.setDurtion(SettingProperties.getGdbRecommendAnimTime());

//        mLBanners.setAutoPlay(true);//自动播放
//        mLBanners.setVertical(false);//是否锤子播放
//        mLBanners.setScrollDurtion(2000);//两页切换时间
//        mLBanners.setCanLoop(true);//循环播放
//        mLBanners.setSelectIndicatorRes(R.drawable.guide_indicator_select);//选中的原点
//        mLBanners.setUnSelectUnIndicatorRes(R.drawable.guide_indicator_unselect);//未选中的原点
//        //若自定义原点到底部的距离,默认20,必须在setIndicatorWidth之前调用
//        mLBanners.setIndicatorBottomPadding(30);
//        mLBanners.setIndicatorWidth(10);//原点默认为5dp
//        mLBanners.setIndicatorPosition(LMBanners.IndicaTorPosition.BOTTOM_MID);//设置原点显示位置

        if (SettingProperties.isGdbRecmmendAnimRandom(getActivity())) {
            Random random = new Random();
            int type = Math.abs(random.nextInt()) % LMBannerViewUtil.ANIM_TYPES.length;
            LMBannerViewUtil.setScrollAnim(lmBanners, type);
        }
        else {
            LMBannerViewUtil.setScrollAnim(lmBanners, SettingProperties.getGdbRecommendAnimType(getActivity()));
        }
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

}
