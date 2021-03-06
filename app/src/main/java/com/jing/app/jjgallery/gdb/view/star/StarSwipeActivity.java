package com.jing.app.jjgallery.gdb.view.star;

import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jing.app.jjgallery.gdb.ActivityManager;
import com.jing.app.jjgallery.gdb.GBaseActivity;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.model.SettingProperties;
import com.jing.app.jjgallery.gdb.model.bean.StarProxy;
import com.jing.app.jjgallery.gdb.model.conf.PreferenceValue;
import com.jing.app.jjgallery.gdb.presenter.star.StarSwipePresenter;
import com.jing.app.jjgallery.gdb.util.DisplayHelper;
import com.jing.app.jjgallery.gdb.util.StarRatingUtil;
import com.jing.app.jjgallery.gdb.view.adapter.OnRecordItemClickListener;
import com.jing.app.jjgallery.gdb.view.adapter.RecordCardAdapter;
import com.jing.app.jjgallery.gdb.view.adapter.RecordsListAdapter;
import com.jing.app.jjgallery.gdb.view.adapter.SwipeAdapter;
import com.jing.app.jjgallery.gdb.view.pub.dialog.DefaultDialogManager;
import com.jing.app.jjgallery.gdb.view.pub.dialog.StarRatingDialog;
import com.jing.app.jjgallery.gdb.view.pub.swipeview.SwipeFlingAdapterView;
import com.jing.app.jjgallery.gdb.view.record.SortDialogFragment;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.entity.StarRating;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/8/4 11:18
 */
public class StarSwipeActivity extends GBaseActivity implements IStarSwipeView {

    @BindView(R.id.rv_records)
    RecyclerView rvRecords;
    @BindView(R.id.rv_records_hor)
    RecyclerView rvRecordsHor;
    @BindView(R.id.sfv_stars)
    SwipeFlingAdapterView sfvStars;
    @BindView(R.id.iv_orientation)
    ImageView ivOrientation;
    @BindView(R.id.tv_rating)
    TextView tvRating;

    private StarSwipePresenter presenter;
    private SwipeAdapter adapter;
    private List<StarProxy> starList;

    private RecordsListAdapter recordsListAdapter;
    private RecordCardAdapter recordCardAdapter;

    private int currentSortMode = PreferenceValue.GDB_SR_ORDERBY_NONE;
    private boolean currentSortDesc = true;

    @Override
    protected int getContentView() {
        return R.layout.activity_gdb_rec_star;
    }

    @Override
    protected void initController() {
        presenter = new StarSwipePresenter(this);
        starList = new ArrayList<>();
    }

    @Override
    protected void initView() {
        getSupportActionBar().hide();

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvRecords.setLayoutManager(manager);

        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvRecordsHor.setLayoutManager(manager);

        boolean isHorizontal = SettingProperties.isGdbSwipeListHorizontal();
        rvRecordsHor.setVisibility(isHorizontal ? View.VISIBLE:View.GONE);
        rvRecords.setVisibility(isHorizontal ? View.GONE:View.VISIBLE);

        tvRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StarRatingDialog dialog = new StarRatingDialog();
                dialog.setStarId(getCurrentStar().getStar().getId());
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        refreshRating();
                    }
                });
                dialog.show(getSupportFragmentManager(), "StarRatingDialog");
            }
        });

        sfvStars.setMinStackInAdapter(3);
        sfvStars.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                starList.remove(0);
                adapter.setList(starList);
                adapter.notifyDataSetChanged();
            }

            /**
             * not favor or cancel favor
             * @param dataObject
             */
            @Override
            public void onLeftCardExit(Object dataObject) {
                updateRecords();
            }

            /**
             * mark favor if not already be favored
             * @param dataObject
             */
            @Override
            public void onRightCardExit(Object dataObject) {
                updateRecords();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                presenter.loadNewStars();
            }

            @Override
            public void onScroll(float progress, float scrollXProgress) {

            }
        });
    }

    @Override
    protected void initBackgroundWork() {
        presenter.loadNewStars();
    }

    @Override
    public void onStarLoaded(List<StarProxy> list) {
        starList.addAll(list);
        if (adapter == null) {
            adapter = new SwipeAdapter();
            adapter.setList(list);
            adapter.setOnSwipeItemListener(new SwipeAdapter.OnSwipeItemListener() {
                @Override
                public void onClickStar(StarProxy star) {
                    if (DisplayHelper.isTabModel(StarSwipeActivity.this)) {
                        ActivityManager.startStarPageActivity(StarSwipeActivity.this, star.getStar().getId());
                    }
                    else {
                        ActivityManager.startStarActivity(StarSwipeActivity.this, star.getStar());
                    }
                }
            });
            sfvStars.setAdapter(adapter);

            updateRecords();
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private StarProxy getCurrentStar() {
        try {
            return starList.get(0);
        } catch (Exception e) {
            return null;
        }
    }


    private void updateRecords() {
        Star star = getCurrentStar().getStar();
        presenter.sortRecords(star.getRecordList(), currentSortMode, currentSortDesc);
        if (SettingProperties.isGdbSwipeListHorizontal()) {
            rvRecords.setVisibility(View.GONE);
            updateHorizontalList(star);
        }
        else {
            rvRecordsHor.setVisibility(View.GONE);
            updateVerticalList(star);
        }
        sfvStars.invalidate();
        sfvStars.requestLayout();

        refreshRating();
    }

    private void refreshRating() {
        List<StarRating> ratings = getCurrentStar().getStar().getRatings();
        if (ratings.size() == 0) {
            tvRating.setText(StarRatingUtil.NON_RATING);
            StarRatingUtil.updateRatingColor(tvRating, null);
        }
        else {
            tvRating.setText(StarRatingUtil.getRatingValue(ratings.get(0).getComplex()));
            StarRatingUtil.updateRatingColor(tvRating, ratings.get(0));
        }
    }

    /**
     * horizontal card list
     * @param star
     */
    private void updateHorizontalList(Star star) {
        if (recordCardAdapter == null) {
            recordCardAdapter = new RecordCardAdapter();
            recordCardAdapter.setOnCardActionListener(new RecordCardAdapter.OnCardActionListener() {
                @Override
                public void onClickCardItem(View v, Record record) {
                    Pair<View, String>[] pairs;
                    pairs = new Pair[3];
                    pairs[0] = Pair.create(v.findViewById(R.id.iv_record), getString(R.string.anim_record_page_img));
                    pairs[1] = Pair.create(v.findViewById(R.id.tv_score), getString(R.string.anim_record_page_score));
                    pairs[2] = Pair.create(v.findViewById(R.id.tv_scene), getString(R.string.anim_record_page_scene));
                    ActivityManager.startRecordActivity(StarSwipeActivity.this, record, pairs);
                }
            });
            recordCardAdapter.setRecordList(star.getRecordList());
            recordCardAdapter.setCurrentStar(star);
            rvRecordsHor.setAdapter(recordCardAdapter);
        } else {
            recordCardAdapter.setRecordList(star.getRecordList());
            recordCardAdapter.setCurrentStar(star);
            recordCardAdapter.notifyDataSetChanged();
        }

        if (star.getRecordList() == null || star.getRecordList().size() == 0) {
            rvRecordsHor.setVisibility(View.GONE);
        } else {
            rvRecordsHor.setVisibility(View.VISIBLE);
            rvRecordsHor.scrollToPosition(0);
        }
    }

    /**
     * get top star image view of sfvStars
     * @return
     */
    private View getCurrentStarView() {
        // SwipeFlingAdapterView is extended from AdapterView, which is similar with FrameLayout in layout method.
        // That means, the view will be put by layer from bottom to top, the top child is the last array index child
        View topView = sfvStars.getChildAt(sfvStars.getChildCount() - 1);
        return topView.findViewById(R.id.iv_star);
    }

    /**
     * vertical normal list
     * @param star
     */
    private void updateVerticalList(Star star) {
        if (recordsListAdapter == null) {
            recordsListAdapter = new RecordsListAdapter(this, star.getRecordList());
            recordsListAdapter.setItemClickListener(new OnRecordItemClickListener() {
                @Override
                public void onClickRecordItem(View v, Record record) {
                    // set anchor views of transition animation
                    Pair<View, String>[] pairs = new Pair[3];
                    pairs[0] = Pair.create(v.findViewById(R.id.record_thumb), getString(R.string.anim_record_page_img));
                    pairs[1] = Pair.create(v.findViewById(R.id.record_score), getString(R.string.anim_record_page_score));
                    pairs[2] = Pair.create(v.findViewById(R.id.record_scene), getString(R.string.anim_record_page_scene));
                    ActivityManager.startRecordActivity(StarSwipeActivity.this, record, pairs);
                }

                @Override
                public void onPopupMenu(View v, Record record) {

                }
            });
            rvRecords.setAdapter(recordsListAdapter);
        } else {
            recordsListAdapter.setRecordList(star.getRecordList());
            recordsListAdapter.notifyDataSetChanged();
        }

        if (star.getRecordList() == null || star.getRecordList().size() == 0) {
            rvRecords.setVisibility(View.GONE);
        } else {
            rvRecords.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.iv_list, R.id.iv_back, R.id.iv_orientation, R.id.iv_sort})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_list:
                if (DisplayHelper.isTabModel(this)) {
                    ActivityManager.startStarPadActivity(this);
                }
                else {
                    ActivityManager.startStarListActivity(this);
                }
                finish();
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_orientation:
                boolean isHorizontal = SettingProperties.isGdbSwipeListHorizontal();
                if (isHorizontal) {
                    SettingProperties.setGdbSwipeListOrientation(false);
                    ivOrientation.setImageResource(R.drawable.ic_panorama_horizontal_3f51b5_36dp);
                }
                else {
                    SettingProperties.setGdbSwipeListOrientation(true);
                    ivOrientation.setImageResource(R.drawable.ic_panorama_vertical_3f51b5_36dp);
                }
                updateRecords();
                break;
            case R.id.iv_sort:
                SortDialogFragment dialog = new SortDialogFragment();
                dialog.setDesc(currentSortDesc);
                dialog.setSortMode(currentSortMode);
                dialog.setOnSortListener(new SortDialogFragment.OnSortListener() {
                    @Override
                    public void onSort(boolean desc, int sortMode) {
                        if (currentSortMode != sortMode || currentSortDesc != desc) {
                            currentSortMode = sortMode;
                            currentSortDesc = desc;
                            refreshRecordList();
                        }
                    }
                });
                dialog.show(getSupportFragmentManager(), "SortDialogFragment");
                break;
        }
    }

    private void refreshRecordList() {
        presenter.sortRecords(getCurrentStar().getStar().getRecordList(), currentSortMode, currentSortDesc);
        if (recordCardAdapter != null) {
            recordCardAdapter.notifyDataSetChanged();
        }
        if (recordsListAdapter != null) {
            recordsListAdapter.notifyDataSetChanged();
        }
    }
}
