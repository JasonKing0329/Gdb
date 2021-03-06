package com.jing.app.jjgallery.gdb.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.allure.lbanners.LMBanners;
import com.allure.lbanners.adapter.LBaseAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.model.GdbImageProvider;
import com.jing.app.jjgallery.gdb.model.SettingProperties;
import com.jing.app.jjgallery.gdb.model.bean.StarProxy;
import com.jing.app.jjgallery.gdb.util.FormatUtil;
import com.jing.app.jjgallery.gdb.util.GlideUtil;
import com.jing.app.jjgallery.gdb.util.LMBannerViewUtil;
import com.jing.app.jjgallery.gdb.util.ListUtil;
import com.jing.app.jjgallery.gdb.util.StarRatingUtil;
import com.jing.app.jjgallery.gdb.view.pub.PullZoomRecyclerView;
import com.jing.app.jjgallery.gdb.view.pub.dialog.DefaultDialogManager;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.Star;

import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2016/7/30 0030.
 * 采用PullZoomRecyclerView的适配方式
 * 实际上是基于RecyclerView.Adapter的模式，
 * PullZoomHeaderHolder作为header部分
 * RecordHolder作为列表视图的records适配器
 * CardHolder作为卡片视图的records适配器
 * 由于所使用的第三方PullZoomRecyclerView只能适配其RecyclerListAdapter方式，并且同意设置了纵向布局
 * 而UI上是保持header固定，点击header上的按钮进行header下方的卡片/列表模式的切换，卡片是横向布局的，列表是纵向布局的
 * 因此，只能从adapter的另外两个holder上作切换
 * 增加isCardMode变量标记当前是否为卡片模式
 */
public class StarRecordsAdapter extends RecyclerListAdapter {

    /**
     * 父类RecyclerListAdapter是靠class来区别view type的，卡片模式下采用了横向recyclerView作为item，这里用这个类作为标记，没有实际功能
     */
    private class CardItem {

    }

    public interface OnRecordItemClickListener {
        void onClickRecordItem(Pair<View, String>[] pairs, Record record);
        void showAnimSetting();
        void onUpdateRating(Long id);
    }

    private PullZoomHeaderHolder headerHolder;

    private PullZoomRecyclerView recyclerView;
    protected List<Record> listData;
    private StarProxy star;

    private int nameColorNormal, nameColorBareback;
    private OnRecordItemClickListener itemClickListener;

    private int sortMode;

    private boolean isStarFavor;

    /**
     * true卡片模式，false列表模式
     */
    private boolean isCardMode;

    private List<String> headPathList;

    /**
     * 专场动画需要，作为全局变量
     */
    private ViewGroup zoomHeaderContainer;

    private RequestOptions requestOptions;

    public StarRecordsAdapter(StarProxy star, PullZoomRecyclerView recyclerView) {
        this.star = star;
        this.recyclerView = recyclerView;
        listData = star.getStar().getRecordList();
        nameColorNormal = recyclerView.getContext().getResources().getColor(R.color.gdb_record_text_normal_light);
        nameColorBareback = recyclerView.getContext().getResources().getColor(R.color.gdb_record_text_bareback_light);

        isCardMode = SettingProperties.isGdbStarRecordsCardMode();

        requestOptions = GlideUtil.getRecordOptions();

        /**
         * header
         */
        addViewType(TYPE_HEADER, new ViewHolderFactory<PullZoomHeaderHolder>() {
            @Override
            public PullZoomHeaderHolder onCreateViewHolder(ViewGroup parent) {
                headerHolder = new PullZoomHeaderHolder(parent);
                return headerHolder;
            }
        });
        /**
         * 纵向列表
         */
        addViewType(Record.class, new ViewHolderFactory<RecordHolder>() {
            @Override
            public RecordHolder onCreateViewHolder(ViewGroup parent) {
                RecordHolder holder = new RecordHolder(parent);
                holder.setParameters(nameColorNormal, nameColorBareback, recordItemListener);
                return holder;
            }
        });
        /**
         * 横向卡片
         */
        addViewType(CardItem.class, new ViewHolderFactory<CardHolder>() {
            @Override
            public CardHolder onCreateViewHolder(ViewGroup parent) {
                CardHolder holder = new CardHolder(parent);
                return holder;
            }
        });

    }

    public void setStarFavor(boolean starFavor) {
        this.isStarFavor = starFavor;
    }

    public void setItemClickListener(OnRecordItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setSortMode(int sortMode) {
        this.sortMode = sortMode;
    }

    @Override
    public Object getItem(int position) {
        // 第0个item固定为header
        if (position == 0) {
            return ITEM_HEADER;
        }
        // 卡片模式下一共只有2个item，第2个即为横向卡片布局，父类的getItemViewType是通过对象.getClass来判断ViewHolder类型的
        // 返回CardItem对象
        if (isCardMode) {
            return new CardItem();
        }
        // 列表模式下返回Record对象
        else {
            return listData.get(--position);
        }
    }

    @Override
    public int getItemCount() {

        // 卡片模式下一共只有2个item，第一个为header，第2个即为横向卡片布局
        if (isCardMode) {
            return 2;
        }
        // 列表模式下第一个为header，后面的是纵向的record item
        else {
            return listData.size() + 1;
        }
    }

    /**
     * 覆盖父类的方法，这个PullZoom open source是把第recyclerView第0个当成pull image，
     * 但是现在RecordHolder是跟record list界面共用的一个holder，那里是正常的用第0个index的
     * 所以覆盖父类的实现方法，当holder的类型是RecordHolder，将position - 1。父类的getItem其实就是listData.get(position - 1)
     * 但是这里要把正确的position传给RecordHolder
     *
     * PullZoomHeaderHolder还是实现父类的方法
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof PullZoomHeaderHolder) {
            headerHolder = (PullZoomHeaderHolder) holder;
            super.onBindViewHolder(holder, position);
        }
        // 列表模式item
        else if (holder instanceof RecordHolder) {
            RecordHolder vh = (RecordHolder) holder;
            vh.setSortMode(sortMode);
            vh.bind(listData.get(position - 1), position - 1);
        }
        // 卡片模式item，刷新整个横向recyclerView
        else if (holder instanceof CardHolder) {
            CardHolder ch = (CardHolder) holder;
            ch.bind(null, 1);
        }
    }

    private View.OnClickListener recordItemListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                Record record = (Record) v.getTag();

                // set anchor views of transition animation
                Pair<View, String>[] pairs = new Pair[3];
                pairs[0] = Pair.create(v.findViewById(R.id.record_thumb), v.getContext().getString(R.string.anim_record_page_img));
                pairs[1] = Pair.create(v.findViewById(R.id.record_score), v.getContext().getString(R.string.anim_record_page_score));
                pairs[2] = Pair.create(v.findViewById(R.id.record_scene), v.getContext().getString(R.string.anim_record_page_scene));
                itemClickListener.onClickRecordItem(pairs, record);
            }
        }
    };

    public void refreshBanner() {
        headerHolder.initBanner(headPathList);
    }

    private class PullZoomHeaderHolder extends RecyclerListAdapter.ViewHolder<Object> {
        private ImageView zoomView;
        private TextView tvRating;
        private ImageView ivCardMode;
        private ImageView ivSetting;
        private TextView nameView;
        private TextView numberView;
        private TextView typeView;
        private TextView scoreView;
        private TextView cscoreView;
        private LMBanners lmBanners;

        public PullZoomHeaderHolder(ViewGroup parent) {
            this(LayoutInflater.from(recyclerView.getContext()).inflate(R.layout.adapter_gdb_star_header, parent, false));
        }

        public PullZoomHeaderHolder(View view) {
            super(view);
            zoomView = (ImageView) view.findViewById(R.id.gdb_star_header_image);
            ivSetting = (ImageView) view.findViewById(R.id.iv_setting);
            ivCardMode = (ImageView) view.findViewById(R.id.iv_card_mode);
            lmBanners = (LMBanners) view.findViewById(R.id.lmbanner);
            tvRating = view.findViewById(R.id.tv_rating);
            zoomHeaderContainer = (ViewGroup) view.findViewById(R.id.gdb_star_header_container);
            nameView = (TextView) view.findViewById(R.id.gdb_star_header_name);
            numberView = (TextView) view.findViewById(R.id.gdb_star_header_number);
            typeView = (TextView) view.findViewById(R.id.gdb_star_header_type);
            scoreView = (TextView) view.findViewById(R.id.gdb_star_header_score);
            cscoreView = (TextView) view.findViewById(R.id.gdb_star_header_cscore);
            updateCardModeIcon();
            initHeadPart();
        }

        private void initHeadPart() {
            boolean showImage;
            if (GdbImageProvider.hasStarFolder(star.getStar().getName())) {
                headPathList = GdbImageProvider.getStarPathList(star.getStar().getName());
                if (headPathList.size() <= 1) {
                    showImage = true;
                }
                else {
                    showImage = false;
                    lmBanners.setVisibility(View.VISIBLE);
                    ivSetting.setVisibility(View.VISIBLE);
                    zoomView.setVisibility(View.GONE);
                    recyclerView.setZoomView(lmBanners);
                    initBanner(headPathList);
                }
            }
            else {
                showImage = true;
            }

            if (showImage) {
                lmBanners.setVisibility(View.GONE);
                ivSetting.setVisibility(View.GONE);
                zoomView.setVisibility(View.VISIBLE);
                recyclerView.setZoomView(zoomView);
                String path = GdbImageProvider.getStarRandomPath(star.getStar().getName(), null);
                Glide.with(GdbApplication.getInstance())
                        .load(path)
                        .apply(requestOptions)
                        .into(zoomView);
            }
        }

        public void initBanner(List<String> pathList) {
            // 禁用btnStart(只在onPageScroll触发后有效)
            lmBanners.isGuide(false);
            // 显示引导圆点
//            lmBanners.hideIndicatorLayout();
            lmBanners.setIndicatorPosition(LMBanners.IndicaTorPosition.BOTTOM_MID);
            // 可以不写，因为文件名直接覆用的LMBanners-1.0.8里的res
            lmBanners.setSelectIndicatorRes(R.drawable.page_indicator_select);
            lmBanners.setUnSelectUnIndicatorRes(R.drawable.page_indicator_unselect);
            // 轮播切换时间
            lmBanners.setDurtion(SettingProperties.getGdbStarNavAnimTime(lmBanners.getContext()));
            if (SettingProperties.isGdbStarNavAnimRandom(lmBanners.getContext())) {
                Random random = new Random();
                int type = Math.abs(random.nextInt()) % LMBannerViewUtil.ANIM_TYPES.length;
                LMBannerViewUtil.setScrollAnim(lmBanners, type);
            }
            else {
                LMBannerViewUtil.setScrollAnim(lmBanners, SettingProperties.getGdbStarNavAnimType(lmBanners.getContext()));
            }

            HeadBannerAdapter adapter = new HeadBannerAdapter();
            lmBanners.setAdapter(adapter, pathList);
        }

        @Override
        public void bind(Object item, int position) {
            recyclerView.setHeaderContainer(zoomHeaderContainer);

            Glide.with(GdbApplication.getInstance())
                    .load(star.getImagePath())
                    .apply(requestOptions)
                    .into(zoomView);

            nameView.setText(star.getStar().getName());
            numberView.setText(String.format(recyclerView.getContext().getString(R.string.gdb_star_file_numbers), listData.size()));

            StringBuffer buffer = new StringBuffer();
            if (star.getStar().getBetop() > 0) {
                buffer.append("top(").append(star.getStar().getBetop()).append(")");
                buffer.append("   ");
            }
            if (star.getStar().getBebottom() > 0) {
                buffer.append("bottom(").append(star.getStar().getBebottom()).append(")");
            }
            typeView.setText(buffer.toString());

            buffer = new StringBuffer();
            buffer.append("avg(").append(FormatUtil.formatScore(star.getStar().getAverage(), 1)).append(")")
                    .append("  ").append("max(").append(star.getStar().getMax()).append(")")
                    .append("  ").append("min(").append(star.getStar().getMin()).append(")");
            scoreView.setText(buffer.toString());

            buffer = new StringBuffer();
            buffer.append("cock avg(").append(FormatUtil.formatScore(star.getStar().getCaverage(), 1)).append(")")
                    .append("  ").append("max(").append(star.getStar().getCmax()).append(")")
                    .append("  ").append("min(").append(star.getStar().getCmax()).append(")");
            cscoreView.setText(buffer.toString());

            if (ListUtil.isEmpty(star.getStar().getRatings())) {
                tvRating.setText(StarRatingUtil.NON_RATING);
                StarRatingUtil.updateRatingColor(tvRating, null);
            }
            else {
                tvRating.setText(StarRatingUtil.getRatingValue(star.getStar().getRatings().get(0).getComplex()));
                StarRatingUtil.updateRatingColor(tvRating, star.getStar().getRatings().get(0));
            }

            tvRating.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onUpdateRating(star.getStar().getId());
                    }
                }
            });

            ivSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.showAnimSetting();
                }
            });

            ivCardMode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SettingProperties.isGdbStarRecordsCardMode()) {
                        SettingProperties.setGdbStarRecordsCardMode(false);
                        isCardMode = false;
                    }
                    else {
                        SettingProperties.setGdbStarRecordsCardMode(true);
                        isCardMode = true;
                    }
                    updateCardModeIcon();
                    notifyDataSetChanged();
                }
            });
        }

        private void updateCardModeIcon() {
            boolean isHorizontal = SettingProperties.isGdbStarRecordsCardMode();
            if (isHorizontal) {
                ivCardMode.setImageResource(R.drawable.ic_panorama_vertical_white_36dp);
            }
            else {
                ivCardMode.setImageResource(R.drawable.ic_panorama_horizontal_white_36dp);
            }
        }
    }

    private class HeadBannerAdapter implements LBaseAdapter<String> {

        @Override
        public View getView(LMBanners lBanners, Context context, int position, String path) {
            View view = LayoutInflater.from(context).inflate(R.layout.adapter_gdb_star_list_banner, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.iv_star);
            Glide.with(GdbApplication.getInstance())
                    .load(path)
                    .apply(requestOptions)
                    .into(imageView);
            return view;
        }
    }

    private class CardHolder extends RecyclerListAdapter.ViewHolder {

        RecyclerView recyclerView;
        private RecordCardAdapter adapter;

        public CardHolder(@NonNull ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_gdb_star_record_card_parent, parent, false));
            recyclerView = (RecyclerView) getRootView().findViewById(R.id.rv_records);
            LinearLayoutManager manager = new LinearLayoutManager(parent.getContext());
            manager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(manager);
        }

        @Override
        public void bind(Object item, int position) {
            if (adapter == null) {
                adapter = new RecordCardAdapter();
                adapter.setOnCardActionListener(new RecordCardAdapter.OnCardActionListener() {
                    @Override
                    public void onClickCardItem(View v, Record record) {
                        if (itemClickListener != null) {
                            Pair<View, String>[] pairs;
                            pairs = new Pair[3];
                            pairs[0] = Pair.create(v.findViewById(R.id.iv_record), v.getContext().getString(R.string.anim_record_page_img));
                            pairs[1] = Pair.create(v.findViewById(R.id.tv_score), v.getContext().getString(R.string.anim_record_page_score));
                            pairs[2] = Pair.create(v.findViewById(R.id.tv_scene), v.getContext().getString(R.string.anim_record_page_scene));
                            itemClickListener.onClickRecordItem(pairs, record);
                        }
                    }
                });
                adapter.setCurrentStar(star.getStar());
                adapter.setRecordList(listData);
                recyclerView.setAdapter(adapter);
            }
            else {
                adapter.setCurrentStar(star.getStar());
                adapter.setRecordList(listData);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
