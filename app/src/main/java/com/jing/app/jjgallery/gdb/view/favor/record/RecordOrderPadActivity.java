package com.jing.app.jjgallery.gdb.view.favor.record;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.jing.app.jjgallery.gdb.GBaseActivity;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.util.DebugLog;
import com.jing.app.jjgallery.gdb.view.pub.dialog.DefaultDialogManager;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/14 13:02
 */
public class RecordOrderPadActivity extends GBaseActivity implements RecordOrderHolder {

    public static final String KEY_SELECT_MODE = "key_select_mode";

    public static final String KEY_SELECTED_ID = "key_selected_id";

    private boolean isSelectMode;

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.group_search)
    ViewGroup groupSearch;
    @BindView(R.id.group_icon)
    ViewGroup groupIcon;
    @BindView(R.id.group_confirm)
    ViewGroup groupConfirm;
    @BindView(R.id.iv_icon_sort)
    ImageView ivIconSort;

    private RecordOrderFragment ftOrder;
    private RecordItemFragment ftItem;

    @Override
    protected int getContentView() {
        return R.layout.activity_favor_pad;
    }

    @Override
    protected void initView() {
        groupSearch.setVisibility(View.INVISIBLE);
        groupConfirm.setVisibility(View.GONE);

        ftOrder = new RecordOrderFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.group_ft_container, ftOrder, "RecordOrderFragment")
                .commit();
    }

    @Override
    protected void initController() {
        isSelectMode = getIntent().getBooleanExtra(KEY_SELECT_MODE, false);
    }

    @Override
    protected void initBackgroundWork() {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        DebugLog.e();
        super.onNewIntent(intent);
        setIntent(intent);
        isSelectMode = intent.getBooleanExtra(KEY_SELECT_MODE, false);
        if (ftItem != null && ftItem.isVisible()) {
            getSupportFragmentManager().beginTransaction()
                    .hide(ftItem)
                    .show(ftOrder)
                    .commit();
        }
    }

    @OnClick({R.id.iv_icon_back, R.id.iv_icon_close, R.id.iv_icon_search, R.id.iv_icon_sort
            , R.id.iv_icon_add, R.id.iv_icon_drag, R.id.tv_ok, R.id.tv_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_icon_back:
                onBackPressed();
                break;
            case R.id.iv_icon_close:
                closeSearch();
                break;
            case R.id.iv_icon_search:
                if (groupSearch.getVisibility() != View.VISIBLE) {
                    showSearchLayout();
                }
                break;
            case R.id.iv_icon_sort:
                showSortPopup(view);
                break;
            case R.id.iv_icon_drag:
                ftOrder.doDrag();
                break;
            case R.id.tv_ok:
                showActionbarDragStatus(false);
                ftOrder.dragDone();
                break;
            case R.id.tv_cancel:
                showActionbarDragStatus(false);
                ftOrder.dragCanceled();
                break;
            case R.id.iv_icon_add:
                addNewOrder();
                break;
        }
    }

    @Override
    public void onClickOrder(long orderId) {
        if (isSelectMode) {
            Intent intent = new Intent();
            intent.putExtra(KEY_SELECTED_ID, orderId);
            setResult(RESULT_OK, intent);
            finish();
        }
        else {
            showOrder(orderId);
        }
    }

    private void showOrder(long orderId) {
        if (ftItem == null) {
            ftItem = RecordItemFragment.newInstance(orderId);
            getSupportFragmentManager().beginTransaction()
                    .hide(ftOrder)
                    .add(R.id.group_ft_container, ftItem, "RecordItemFragment")
                    .commit();
        }
        else {
            getSupportFragmentManager().beginTransaction()
                    .hide(ftOrder)
                    .show(ftItem)
                    .commit();
            ftItem.showOrder(orderId);
        }
    }

    @Override
    public void showActionbarDragStatus(boolean isDrag) {
        if (isDrag) {
            groupConfirm.setVisibility(View.VISIBLE);
            groupIcon.setVisibility(View.INVISIBLE);
        }
        else {
            groupConfirm.setVisibility(View.GONE);
            groupIcon.setVisibility(View.VISIBLE);
        }
    }

    public void showSortPopup(View anchor) {
        PopupMenu menu = new PopupMenu(this, anchor);
        menu.getMenuInflater().inflate(ftOrder.getSortPopupMenu(), menu.getMenu());
        menu.show();
        menu.setOnMenuItemClickListener(ftOrder.getSortListener());
    }

    /**
     * hide search layout
     */
    public void closeSearch() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.disappear);
        groupSearch.startAnimation(animation);
        groupSearch.setVisibility(View.INVISIBLE);
    }

    /**
     * show search layout
     */
    private void showSearchLayout() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.appear);
        groupSearch.startAnimation(animation);
        groupSearch.setVisibility(View.VISIBLE);
    }

    private void addNewOrder() {
        new DefaultDialogManager().openInputDialog(this, "Create new folder", new DefaultDialogManager.OnDialogActionListener() {
            @Override
            public void onOk(String name) {
                ftOrder.addNewOrder(name);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (ftItem != null && ftItem.isVisible()) {
            getSupportFragmentManager().beginTransaction()
                    .hide(ftItem)
                    .show(ftOrder)
                    .commit();
        }
        else {
            super.onBackPressed();
        }
    }
}
