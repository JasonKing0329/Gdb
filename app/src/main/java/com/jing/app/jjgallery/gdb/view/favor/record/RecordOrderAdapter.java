package com.jing.app.jjgallery.gdb.view.favor.record;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.util.GlideUtil;
import com.jing.app.jjgallery.gdb.util.ListUtil;
import com.jing.app.jjgallery.gdb.view.adapter.BaseRecyclerAdapter;
import com.king.app.gdb.data.entity.FavorRecordOrder;
import com.king.app.gdb.data.entity.FavorRecordOrderDao;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/14 11:01
 */
public class RecordOrderAdapter extends BaseRecyclerAdapter<RecordOrderAdapter.OrderHolder, FavorRecordOrderEx>
    implements View.OnClickListener {

    private RequestOptions coverOptions;
    private boolean isDrag;
    private SwipeMenuRecyclerView menuRecyclerView;
    private boolean isSelectMode;

    private SparseBooleanArray checkMap;

    public RecordOrderAdapter() {
        coverOptions = GlideUtil.getRecordOptions();
        checkMap = new SparseBooleanArray();
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_favor_item;
    }

    @Override
    protected OrderHolder newViewHolder(View view) {
        return new OrderHolder(view);
    }

    public void setMenuRecyclerView(SwipeMenuRecyclerView menuRecyclerView) {
        this.menuRecyclerView = menuRecyclerView;
    }

    @Override
    public void onBindViewHolder(OrderHolder holder, int position) {
        FavorRecordOrder order = list.get(position).getOrder();

        holder.mMenuRecyclerView = menuRecyclerView;

        holder.tvName.setText(order.getName());
        holder.tvNumber.setText(String.valueOf(order.getNumber()));

        Glide.with(holder.ivCover.getContext())
                .load(list.get(position).getCover())
                .apply(coverOptions)
                .into(holder.ivCover);

        holder.groupOrder.setTag(position);
        holder.groupOrder.setOnClickListener(this);

        holder.ivDrag.setVisibility(isDrag ? View.VISIBLE:View.INVISIBLE);

        List<String> thumbs = list.get(position).getThumbItems();
        if (ListUtil.isEmpty(thumbs)) {
            holder.ivImage1.setVisibility(View.INVISIBLE);
            holder.ivImage2.setVisibility(View.INVISIBLE);
            holder.ivImage3.setVisibility(View.INVISIBLE);
        }
        else {
            holder.ivImage1.setVisibility(View.VISIBLE);
            Glide.with(holder.ivImage1.getContext())
                    .load(thumbs.get(0))
                    .apply(coverOptions)
                    .into(holder.ivImage1);

            if (thumbs.size() > 1) {
                holder.ivImage2.setVisibility(View.VISIBLE);
                Glide.with(holder.ivImage2.getContext())
                        .load(thumbs.get(1))
                        .apply(coverOptions)
                        .into(holder.ivImage2);
            }
            else {
                holder.ivImage2.setVisibility(View.INVISIBLE);
            }

            if (thumbs.size() > 2) {
                holder.ivImage3.setVisibility(View.VISIBLE);
                Glide.with(holder.ivImage3.getContext())
                        .load(thumbs.get(2))
                        .apply(coverOptions)
                        .into(holder.ivImage3);
            }
            else {
                holder.ivImage3.setVisibility(View.INVISIBLE);
            }
        }

        holder.cbCheck.setVisibility(isSelectMode ? View.VISIBLE:View.GONE);
        holder.cbCheck.setChecked(checkMap.get(position));
    }

    public int getItemPosition(long focusId) {
        for (int i = 0; i < getItemCount(); i ++) {
            if (focusId == list.get(i).getOrder().getId()) {
                return i;
            }
        }
        return 0;
    }

    public void enableDrag(boolean drag) {
        this.isDrag = drag;
    }

    public boolean isDrag() {
        return isDrag;
    }

    public void swapData(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(list, i, i + 1);
            }
        }
        else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(list, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onClick(View view) {
        if (isSelectMode) {
            int position = (int) view.getTag();
            if (checkMap.get(position)) {
                checkMap.put(position, false);
            }
            else {
                checkMap.put(position, true);
            }
            notifyItemChanged(position);
        }
        else {
            if (onItemClickListener != null) {
                int position = (int) view.getTag();
                onItemClickListener.onClickItem(position, list.get(position));
            }
        }
    }

    public void notifyOrderChanged(long orderId) {
        for (int i = 0; i < getItemCount(); i ++) {
            if (orderId == list.get(i).getOrder().getId()) {
                refreshItem(list.get(i));
                notifyItemChanged(i);
                break;
            }
        }
    }

    private void refreshItem(FavorRecordOrderEx item) {
        FavorRecordOrderDao dao = GdbApplication.getInstance().getDaoSession().getFavorRecordOrderDao();
        dao.refresh(item.getOrder());
        FavorRecordOrderEx orderEx = RecordOrderPresenter.parseFromOrder(item.getOrder());
        item.setCover(orderEx.getCover());
        item.setOrder(orderEx.getOrder());
        item.setThumbItems(orderEx.getThumbItems());
    }

    public void setSelectMode(boolean selectMode) {
        this.isSelectMode = selectMode;
        checkMap.clear();
    }

    public List<FavorRecordOrderEx> getSelectedItems() {
        List<FavorRecordOrderEx> resultList = new ArrayList<>();
        for (int i = 0; i < getItemCount(); i ++) {
            if (checkMap.get(i)) {
                resultList.add(list.get(i));
            }
        }
        return resultList;
    }

    public static class OrderHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {

        @BindView(R.id.iv_cover)
        ImageView ivCover;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_number)
        TextView tvNumber;
        @BindView(R.id.iv_image1)
        ImageView ivImage1;
        @BindView(R.id.iv_image2)
        ImageView ivImage2;
        @BindView(R.id.iv_image3)
        ImageView ivImage3;
        @BindView(R.id.group_order)
        CardView groupOrder;
        @BindView(R.id.iv_drag)
        ImageView ivDrag;
        @BindView(R.id.cb_check)
        CheckBox cbCheck;

        SwipeMenuRecyclerView mMenuRecyclerView;

        public OrderHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            ivDrag.setOnTouchListener(this);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN: {
                    mMenuRecyclerView.startDrag(this);
                    break;
                }
            }
            return false;
        }
    }
}
