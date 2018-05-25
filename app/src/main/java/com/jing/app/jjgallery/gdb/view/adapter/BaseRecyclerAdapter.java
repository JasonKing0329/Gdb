package com.jing.app.jjgallery.gdb.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/14 15:12
 */
public abstract class BaseRecyclerAdapter<VH extends RecyclerView.ViewHolder, T> extends RecyclerView.Adapter<VH> {

    protected List<T> list;

    protected OnItemClickListener<T> onItemClickListener;

    public void setList(List<T> list) {
        this.list = list;
    }

    public List<T> getList() {
        return list;
    }

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(getItemLayoutRes(), parent, false);
        final VH holder = newViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getLayoutPosition();
                onClickItemView(v, position, list.get(position));
            }
        });
        return holder;
    }

    /**
     * 子类可选择覆盖
     * @param v
     * @param position
     * @param t
     */
    protected void onClickItemView(View v, int position, T t) {
        if (onItemClickListener != null) {
            onItemClickListener.onClickItem(position, t);
        }
    }

    protected abstract int getItemLayoutRes();

    protected abstract VH newViewHolder(View view);

    @Override
    public int getItemCount() {
        return list == null ? 0:list.size();
    }

    public interface OnItemClickListener<T> {
        void onClickItem(int position, T data);
    }
}
