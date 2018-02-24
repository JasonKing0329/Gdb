package com.jing.app.jjgallery.gdb.view.star.pad;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jing.app.jjgallery.gdb.MvpActivity;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.util.ScreenUtils;
import com.jing.app.jjgallery.gdb.view.pub.dialog.AlertDialogFragmentV4;
import com.jing.app.jjgallery.gdb.view.record.common.RecordCommonListFragment;
import com.king.app.gdb.data.entity.Star;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @desc
 * @auth 景阳
 * @time 2018/2/15 0015 19:18
 */

public class StarPageActivity extends MvpActivity<StarPagePresenter> implements StarPageView {

    public static final String KEY_STAR_ID = "key_star_id";

    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_tag_video)
    TextView tvTagVideo;
    @BindView(R.id.tv_tag_top)
    TextView tvTagTop;
    @BindView(R.id.tv_tag_bottom)
    TextView tvTagBottom;
    @BindView(R.id.rv_star)
    RecyclerView rvStar;

    private RecordCommonListFragment ftRecord;

    private StarPageStarAdapter starAdapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_star_page_pad;
    }

    @Override
    protected void initView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvStar.setLayoutManager(manager);

        rvStar.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = ScreenUtils.dp2px(10);
                outRect.bottom = ScreenUtils.dp2px(10);
            }
        });

        ftRecord = new RecordCommonListFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.group_records, ftRecord, "RecordCommonListFragment")
                .commit();
    }

    @Override
    protected StarPagePresenter createPresenter() {
        return new StarPagePresenter();
    }

    @Override
    protected void initData() {
        presenter.loadStar(getIntent().getLongExtra(KEY_STAR_ID, -1));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        tvTagTop.setSelected(false);
        tvTagBottom.setSelected(false);
        tvTagVideo.setSelected(true);
        initData();
    }

    @Override
    public void showStar(Star star) {

        tvName.setText(star.getName());
        tvTagVideo.setText("Videos  " + star.getRecords());
        if (star.getBetop() > 0) {
            tvTagTop.setText("Top  " + star.getBetop());
            tvTagTop.setVisibility(View.VISIBLE);
        }
        else {
            tvTagTop.setVisibility(View.GONE);
        }
        if (star.getBebottom() > 0) {
            tvTagBottom.setText("Bottom  " + star.getBebottom());
            tvTagBottom.setVisibility(View.VISIBLE);
        }
        else {
            tvTagBottom.setVisibility(View.GONE);
        }
        tvTagVideo.setSelected(true);

        starAdapter = new StarPageStarAdapter();
        starAdapter.setPathList(presenter.getStarImages());
        starAdapter.setOnItemClickListener(new StarPageStarAdapter.OnItemClickListener() {
            @Override
            public void onDeleteItem(final String filePath) {
                AlertDialogFragmentV4 dialog = new AlertDialogFragmentV4();
                dialog.setMessage("Are you sure to delete " + filePath);
                dialog.setPositiveText(getString(R.string.ok));
                dialog.setPositiveListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        presenter.deleteFile(filePath);
                        starAdapter.removeItem(filePath);
                    }
                });
                dialog.setNegativeText(getString(R.string.cancel));
                dialog.show(getSupportFragmentManager(), "AlertDialogFragmentV4");
            }
        });
        rvStar.setAdapter(starAdapter);

        ftRecord.showStarRecords(star);
    }

    @OnClick({R.id.iv_icon_back, R.id.iv_icon_sort})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_icon_back:
                finish();
                break;
            case R.id.iv_icon_sort:
                ftRecord.onClickSort();
                break;
        }
    }

    @OnClick({R.id.tv_tag_video, R.id.tv_tag_top, R.id.tv_tag_bottom})
    public void onClickStarTag(View view) {
        switch (view.getId()) {
            case R.id.tv_tag_video:
                if (!tvTagVideo.isSelected()) {
                    tvTagVideo.setSelected(true);
                    tvTagTop.setSelected(false);
                    tvTagBottom.setSelected(false);
                    ftRecord.showStarAllRecords();
                }
                break;
            case R.id.tv_tag_top:
                if (!tvTagTop.isSelected()) {
                    tvTagTop.setSelected(true);
                    tvTagVideo.setSelected(false);
                    tvTagBottom.setSelected(false);
                    ftRecord.showStarTopRecords();
                }
                break;
            case R.id.tv_tag_bottom:
                if (!tvTagBottom.isSelected()) {
                    tvTagBottom.setSelected(true);
                    tvTagTop.setSelected(false);
                    tvTagVideo.setSelected(false);
                    ftRecord.showStarBottomRecords();
                }
                break;
        }
    }
}
