package com.jing.app.jjgallery.gdb.view.record.pad;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.util.GlideApp;
import com.jing.app.jjgallery.gdb.view.adapter.BaseRecyclerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/4/18 9:53
 */
public class PagePagerAdapter extends BaseRecyclerAdapter<PagePagerAdapter.PagerHolder, String> implements View.OnClickListener {

    private OnItemClickListener onItemClickListener;

    private int selection = -1;

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_record_page_pager;
    }

    @Override
    protected PagerHolder newViewHolder(View view) {
        return new PagerHolder(view);
    }

    @Override
    public void onBindViewHolder(PagerHolder holder, int position) {
        GlideApp.with(holder.ivItem.getContext())
                .load(list.get(position))
                .error(R.drawable.default_cover)
                .into(holder.ivItem);

        holder.groupItem.setSelected(position == selection);
        holder.groupItem.setTag(position);
        holder.groupItem.setOnClickListener(this);
    }

    public void updateSelection(int selection) {
        if (selection != this.selection) {
            int lastSelection = this.selection;
            this.selection = selection;
            if (lastSelection != -1) {
                notifyItemChanged(lastSelection);
            }
            notifyItemChanged(selection);
        }
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        if (position != selection) {
            updateSelection(position);

            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position);
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public static class PagerHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.group_item)
        ViewGroup groupItem;

        @BindView(R.id.iv_item)
        ImageView ivItem;

        public PagerHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
