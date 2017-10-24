package com.jing.app.jjgallery.gdb.view.download;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.http.bean.data.DownloadItem;
import com.jing.app.jjgallery.gdb.util.GlideUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/5 0005.
 */
public class DownloadExistAdapter extends RecyclerView.Adapter<DownloadExistAdapter.ItemHolder> {

    private List<DownloadItem> itemList;
    private SparseBooleanArray checkMap;
    private RequestOptions requestOptions;

    public DownloadExistAdapter(List<DownloadItem> list) {
        itemList = list;
        checkMap = new SparseBooleanArray();
        requestOptions = GlideUtil.getDownloadPreviewOptions();
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_download_exist_item, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0:itemList.size();
    }

    public List<DownloadItem> getCheckedItems() {
        List<DownloadItem> list = new ArrayList<>();
        if (itemList != null) {
            for (int i = 0; i < itemList.size(); i ++) {
                if (checkMap.get(i)) {
                    list.add(itemList.get(i));
                }
            }
        }
        return list;
    }

    public void selectAll() {
        if (itemList != null) {
            for (int i = 0; i < itemList.size(); i ++) {
                checkMap.put(i, true);
            }
        }
    }

    public void unSelectAll() {
        if (itemList != null) {
            for (int i = 0; i < itemList.size(); i ++) {
                checkMap.put(i, false);
            }
        }
    }

    class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ViewGroup group;
        private TextView name;
        private ImageView image;
        private CheckBox check;
        private int position;

        public ItemHolder(View view) {
            super(view);
            group = (ViewGroup) view.findViewById(R.id.download_exist_group);
            name = (TextView) view.findViewById(R.id.download_exist_name);
            image = (ImageView) view.findViewById(R.id.download_exist_image);
            check = (CheckBox) view.findViewById(R.id.download_exist_check);
        }

        public void bind(int position) {
            this.position = position;
            group.setOnClickListener(this);
            if (itemList.get(position).getKey() != null) {
                name.setText(itemList.get(position).getKey() + "/" + itemList.get(position).getName());
            }
            else {
                name.setText(itemList.get(position).getName());
            }
            check.setChecked(checkMap.get(position));

            Glide.with(GdbApplication.getInstance())
                    .load(itemList.get(position).getPath())
                    .apply(requestOptions)
                    .into(image);
        }

        @Override
        public void onClick(View v) {
            if (checkMap.get(position)) {
                checkMap.put(position, false);
            }
            else {
                checkMap.put(position, true);
            }
            notifyItemChanged(position);
        }
    }
}
