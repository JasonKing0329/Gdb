package com.jing.app.jjgallery.gdb.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.model.bean.StarProxy;
import com.jing.app.jjgallery.gdb.presenter.star.StarListPresenter;
import com.jing.app.jjgallery.gdb.util.GlideUtil;
import com.jing.app.jjgallery.gdb.view.pub.dialog.DefaultDialogManager;
import com.jing.app.jjgallery.gdb.view.star.OnStarClickListener;
import com.king.service.gdb.bean.FavorBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/10/28 0028.
 */

public class StarListGridAdapter extends RecyclerView.Adapter<StarListGridAdapter.ItemHolder> implements View.OnClickListener {

    private OnStarClickListener onStarClickListener;
    private StarListPresenter mPresenter;

    private RequestOptions requestOptions;

    private List<StarProxy> originList;

    public StarListGridAdapter(List<StarProxy> list) {
        this.originList = list;
        requestOptions = GlideUtil.getStarOptions();
    }

    public void setOnStarClickListener(OnStarClickListener listener) {
        onStarClickListener = listener;
    }

    public void setPresenter(StarListPresenter mPresenter) {
        this.mPresenter = mPresenter;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_gdb_starlist_grid, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {

        StarProxy item = originList.get(position);
        holder.tvName.setText(item.getStar().getName() + " (" + item.getStar().getRecordNumber() + ")");

        String headPath = item.getImagePath();
        holder.ivHead.setVisibility(View.VISIBLE);
        Glide.with(GdbApplication.getInstance())
                .load(headPath)
                .apply(requestOptions)
                .into(holder.ivHead);

        holder.groupItem.setTag(item);
        holder.groupItem.setOnClickListener(this);

        holder.ivFavor.setTag(position);
        holder.ivFavor.setOnClickListener(this);

        if (item.getFavor() > 0) {
            holder.ivFavor.setSelected(true);
        }
        else {
            holder.ivFavor.setSelected(false);
        }
    }

    private void saveFavor(StarProxy starProxy, int favor) {
        FavorBean bean = new FavorBean();
        bean.setStarId(starProxy.getStar().getId());
        bean.setStarName(starProxy.getStar().getName());
        bean.setFavor(favor);
        mPresenter.saveFavor(bean);
    }

    @Override
    public int getItemCount() {
        return originList == null ? 0 : originList.size();
    }

    public int getLetterPosition(String letter){
        // originList should be ordered by ASC
        if (letter.length() > 1) {
            return -1;
        }
        for (int i = 0 ; i < getItemCount(); i++){
            char value = originList.get(i).getStar().getName().toUpperCase().charAt(0);
            char index = letter.toUpperCase().charAt(0);
            if(value == index){
                return i;
            }

            // 已超过当前letter无须再比较下去
            if (value > index) {
                break;
            }
        }
        return -1;
    }

    @Override
    public void onClick(View v) {
        if (v instanceof ViewGroup) {
            if (onStarClickListener != null) {
                StarProxy star = (StarProxy) v.getTag();
                onStarClickListener.onStarClick(star);
            }
        }
        else if (v instanceof ImageView) {
            final int position = (int) v.getTag();
            if (originList.get(position).getFavor() > 0) {
                originList.get(position).setFavor(0);
                saveFavor(originList.get(position), 0);
                notifyItemChanged(position);
            }
            else {
                new DefaultDialogManager().openInputDialog(v.getContext(), new DefaultDialogManager.OnDialogActionListener() {
                    @Override
                    public void onOk(String name) {
                        try {
                            int favor = Integer.parseInt(name);
                            originList.get(position).setFavor(favor);
                            saveFavor(originList.get(position), favor);
                            notifyItemChanged(position);
                        } catch (Exception e) {
                            originList.get(position).setFavor(0);
                        }
                    }
                });
            }
        }
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_head)
        ImageView ivHead;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.iv_favor)
        ImageView ivFavor;
        @BindView(R.id.group_item)
        RelativeLayout groupItem;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
