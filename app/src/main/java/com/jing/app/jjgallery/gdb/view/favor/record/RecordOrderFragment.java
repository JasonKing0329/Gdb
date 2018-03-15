package com.jing.app.jjgallery.gdb.view.favor.record;

import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.jing.app.jjgallery.gdb.MvpHolderFragmentV4;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.util.DebugLog;
import com.jing.app.jjgallery.gdb.util.ScreenUtils;
import com.jing.app.jjgallery.gdb.view.adapter.BaseRecyclerAdapter;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemStateChangedListener;

import java.util.List;

import butterknife.BindView;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/15 14:43
 */
public class RecordOrderFragment extends MvpHolderFragmentV4<RecordOrderPresenter, RecordOrderHolder> implements RecordOrderView {

    @BindView(R.id.rv_orders)
    SwipeMenuRecyclerView rvOrders;

    private RecordOrderAdapter orderAdapter;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_record_order;
    }

    @Override
    protected void initView(View view) {
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 4);
        rvOrders.setLayoutManager(manager);
        rvOrders.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                if (position % 4 != 0) {
                    outRect.left = ScreenUtils.dp2px(12);
                }
            }
        });
        rvOrders.setOnItemMoveListener(onItemMoveListener);// 监听拖拽和侧滑删除，更新UI和数据源。
        rvOrders.setOnItemStateChangedListener(mOnItemStateChangedListener); // 监听Item的手指状态，拖拽、侧滑、松开。
    }

    @Override
    protected RecordOrderPresenter createPresenter() {
        return new RecordOrderPresenter();
    }

    @Override
    protected void initData() {
        presenter.loadOrders();
    }

    @Override
    public void showOrders(List<FavorRecordOrderEx> list) {
        if (orderAdapter == null) {
            orderAdapter = new RecordOrderAdapter();
            orderAdapter.setList(list);
            orderAdapter.setMenuRecyclerView(rvOrders);
            orderAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<FavorRecordOrderEx>() {
                @Override
                public void onClickItem(int position, FavorRecordOrderEx data) {
                    if (!orderAdapter.isDrag()) {
                        holder.onClickOrder(data.getOrder().getId());
                    }
                }
            });
            rvOrders.setAdapter(orderAdapter);
        }
        else {
            orderAdapter.setList(list);
            orderAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void focusToItem(long focusId) {
        rvOrders.scrollToPosition(orderAdapter.getItemPosition(focusId));
    }

    /**
     * 点击修改custom顺序
     */
    public void doDrag() {
        if (presenter.isDraggable()) {
            holder.showActionbarDragStatus(true);
            orderAdapter.enableDrag(true);
            orderAdapter.notifyDataSetChanged();
        }
        else {
            showToastLong("Drag action is only supported in custom sort mode");
        }
    }

    /**
     * 保存拖拽后的顺序
     */
    public void dragDone() {
        orderAdapter.enableDrag(false);
        orderAdapter.notifyDataSetChanged();
        presenter.saveDragResult(orderAdapter.getList());
    }

    /**
     * 取消并还原拖拽后的顺序
     */
    public void dragCanceled() {
        orderAdapter.enableDrag(false);
        presenter.loadOrders();
    }

    public int getSortPopupMenu() {
        return R.menu.sort_order_record;
    }

    public PopupMenu.OnMenuItemClickListener getSortListener() {
        return sortListener;
    }

    PopupMenu.OnMenuItemClickListener sortListener = new PopupMenu.OnMenuItemClickListener() {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_sort_by_name:
                    presenter.sortByName();
                    break;
                case R.id.menu_sort_by_number:
                    presenter.sortByNumber();
                    break;
                case R.id.menu_sort_by_custom:
                    presenter.sortByCustom();
                    break;
                case R.id.menu_sort_by_time_create:
                    presenter.sortByCreateTime();
                    break;
                case R.id.menu_sort_by_time_modify:
                    presenter.sortByModifyTime();
                    break;
            }
            return true;
        }
    };

    public void addNewOrder(String name) {
        presenter.addOrder(name);
    }

    /**
     * 监听拖拽和侧滑删除，更新UI和数据源。
     */
    private OnItemMoveListener onItemMoveListener = new OnItemMoveListener() {
        @Override
        public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
            // 不同的ViewType不能拖拽换位置。
            if (srcHolder.getItemViewType() != targetHolder.getItemViewType()) {
                return false;
            }

            // 真实的Position：通过ViewHolder拿到的position都需要减掉HeadView的数量。
            int fromPosition = srcHolder.getAdapterPosition() - rvOrders.getHeaderItemCount();
            int toPosition = targetHolder.getAdapterPosition() - rvOrders.getHeaderItemCount();

            orderAdapter.swapData(fromPosition, toPosition);
            return true;// 返回true表示处理了并可以换位置，返回false表示你没有处理并不能换位置。
        }

        @Override
        public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {

        }

    };

    /**
     * Item的拖拽/侧滑删除时，手指状态发生变化监听。
     */
    private OnItemStateChangedListener mOnItemStateChangedListener = new OnItemStateChangedListener() {
        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState == OnItemStateChangedListener.ACTION_STATE_DRAG) {
                DebugLog.e("状态：拖拽");
                // 拖拽的时候背景就透明了，这里我们可以添加一个特殊背景。
                viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.lightgrey));
            } else if (actionState == OnItemStateChangedListener.ACTION_STATE_SWIPE) {
                DebugLog.e("状态：滑动删除");
            } else if (actionState == OnItemStateChangedListener.ACTION_STATE_IDLE) {
                DebugLog.e("状态：手指松开");

                // 在手松开的时候还原背景。
//                ViewCompat.setBackground(viewHolder.itemView, ContextCompat.getDrawable(RecordOrderPadActivity.this, R.drawable.white));
            }
        }
    };

}
