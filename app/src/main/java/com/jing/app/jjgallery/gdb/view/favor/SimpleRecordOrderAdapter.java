package com.jing.app.jjgallery.gdb.view.favor;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.util.GlideUtil;
import com.jing.app.jjgallery.gdb.view.adapter.BaseRecyclerAdapter;
import com.king.app.gdb.data.entity.FavorRecordOrder;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/15 11:04
 */
public class SimpleRecordOrderAdapter extends BaseRecyclerAdapter<SimpleRecordOrderAdapter.OrderHolder, FavorRecordOrder>
    implements View.OnClickListener {

    private OnAddListener onAddListener;

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_record_orders;
    }

    @Override
    protected OrderHolder newViewHolder(View view) {
        return new OrderHolder(view);
    }

    public void setOnAddListener(OnAddListener onAddListener) {
        this.onAddListener = onAddListener;
    }

    @Override
    public void onBindViewHolder(OrderHolder holder, int position) {
        if (position == getItemCount() - 1) {
            holder.groupOrder.setVisibility(View.INVISIBLE);
            holder.ivAdd.setVisibility(View.VISIBLE);
            holder.ivAdd.setOnClickListener(this);
        }
        else {
            FavorRecordOrder order = list.get(position);
            holder.tvName.setText(order.getName());

            RequestOptions options = GlideUtil.getRecordOptions();
            Glide.with(holder.ivImage.getContext())
                    .load(order.getCoverUrl())
                    .apply(options)
                    .into(holder.ivImage);
            holder.ivAdd.setVisibility(View.INVISIBLE);
            holder.ivAdd.setOnClickListener(null);
            holder.groupOrder.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + 1;
    }

    @Override
    public void onClick(View view) {
        if (onAddListener != null) {
            onAddListener.onAddOrder();
        }
    }

    public static class OrderHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_image)
        ImageView ivImage;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.iv_add)
        ImageView ivAdd;
        @BindView(R.id.group_order)
        ViewGroup groupOrder;

        public OrderHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnAddListener {
        void onAddOrder();
    }
}
