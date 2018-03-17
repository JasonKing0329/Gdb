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
public class RecordOrderPadActivity extends GBaseActivity implements RecordOrderHolder, RecordItemHolder {

    public static final String KEY_SELECT_MODE = "key_select_mode";

    public static final String KEY_SELECTED_ID = "key_selected_id";

    private boolean isSelectMode;

    private boolean isDrag;

    private boolean isDelete;

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
    @BindView(R.id.iv_icon_search)
    ImageView ivIconSearch;
    @BindView(R.id.iv_icon_drag)
    ImageView ivIconDrag;
    @BindView(R.id.iv_icon_add)
    ImageView ivIconAdd;
    @BindView(R.id.iv_icon_delete)
    ImageView ivIconDelete;

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
        if (isSelectMode) {
            ivIconDelete.setVisibility(View.GONE);
            ivIconDrag.setVisibility(View.GONE);
        }
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
            showOrderActionbar();
            getSupportFragmentManager().beginTransaction()
                    .hide(ftItem)
                    .show(ftOrder)
                    .commit();
        }
    }

    private void showOrderActionbar() {
        ivIconAdd.setVisibility(View.VISIBLE);
        ivIconSearch.setVisibility(View.VISIBLE);
        ivIconDrag.setVisibility(View.VISIBLE);
    }

    private void showItemActionbar() {
        ivIconAdd.setVisibility(View.GONE);
        ivIconSearch.setVisibility(View.GONE);
        ivIconDrag.setVisibility(View.GONE);
    }

    @OnClick({R.id.iv_icon_back, R.id.iv_icon_close, R.id.iv_icon_search, R.id.iv_icon_sort
            , R.id.iv_icon_add, R.id.iv_icon_delete, R.id.iv_icon_drag, R.id.tv_ok, R.id.tv_cancel})
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
            case R.id.iv_icon_delete:
                isDelete = true;
                if (ftOrder.isVisible()) {
                    ftOrder.showSelectMode(true);
                }
                else if (ftItem.isVisible()) {
                    ftItem.showSelectMode(true);
                }
                showActionConfirmStatus(true);
                break;
            case R.id.tv_ok:
                if (isDrag) {
                    setDrag(false);
                    ftOrder.dragDone();
                }
                if (isDelete) {
                    if (ftOrder.isVisible()) {
                        ftOrder.deleteSelectedItems();
                    }
                    else if (ftItem.isVisible()) {
                        ftItem.deleteSelectedItems();
                        cancelDeleteStatus();
                    }
                }
                break;
            case R.id.tv_cancel:
                if (isDrag) {
                    setDrag(false);
                    ftOrder.dragCanceled();
                }
                if (isDelete) {
                    if (ftOrder.isVisible()) {
                        ftOrder.showSelectMode(false);
                    }
                    else if (ftItem.isVisible()) {
                        ftItem.showSelectMode(false);
                    }
                    cancelDeleteStatus();
                }
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
        } else {
            showOrder(orderId);
        }
    }

    private void showOrder(long orderId) {
        showItemActionbar();
        if (ftItem == null) {
            ftItem = RecordItemFragment.newInstance(orderId);
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.activity_right_in, R.anim.activity_left_out)
                    .hide(ftOrder)
                    .add(R.id.group_ft_container, ftItem, "RecordItemFragment")
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.activity_right_in, R.anim.activity_left_out)
                    .hide(ftOrder)
                    .show(ftItem)
                    .commit();
            ftItem.showOrder(orderId);
        }
    }

    @Override
    public void setDrag(boolean isDrag) {
        this.isDrag = isDrag;
        showActionConfirmStatus(isDrag);
    }

    @Override
    public void cancelDeleteStatus() {
        isDelete = false;
        showActionConfirmStatus(false);
    }

    public void showActionConfirmStatus(boolean confirm) {
        if (confirm) {
            groupConfirm.setVisibility(View.VISIBLE);
            groupIcon.setVisibility(View.INVISIBLE);
        } else {
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
            showOrderActionbar();
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.activity_left_in, R.anim.activity_right_out)
                    .hide(ftItem)
                    .show(ftOrder)
                    .commit();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void notifyOrderChanged(long orderId) {
        ftOrder.notifyOrderChanged(orderId);
    }
}
