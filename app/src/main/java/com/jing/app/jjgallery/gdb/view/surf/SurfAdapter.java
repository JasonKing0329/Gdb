package com.jing.app.jjgallery.gdb.view.surf;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.siyamed.shapeimageview.BubbleImageView;
import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.http.bean.data.FileBean;
import com.jing.app.jjgallery.gdb.model.GdbImageProvider;
import com.jing.app.jjgallery.gdb.model.bean.HttpSurfFileBean;
import com.jing.app.jjgallery.gdb.util.FileUtil;
import com.jing.app.jjgallery.gdb.util.FormatUtil;
import com.jing.app.jjgallery.gdb.util.GlideApp;
import com.jing.app.jjgallery.gdb.util.GlideUtil;
import com.jing.app.jjgallery.gdb.view.adapter.RecordHolder;
import com.king.app.gdb.data.entity.Record;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/7/27 11:26
 */
public class SurfAdapter extends RecyclerView.Adapter {

    private final int TYPE_FOLDER = 1;

    private final int TYPE_FILE = 2;

    private final int TYPE_HTTP_RECORD = 3;

    private List<FileBean> list;

    private OnSurfItemActionListener onSurfItemActionListener;

    private RequestOptions imageOptions;

    public SurfAdapter(List<FileBean> list) {
        this.list = list;
        imageOptions = GlideUtil.getRecordOptions();
    }

    public void setList(List<FileBean> list) {
        this.list = list;
    }

    public void setOnSurfItemActionListener(OnSurfItemActionListener onSurfItemActionListener) {
        this.onSurfItemActionListener = onSurfItemActionListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).isFolder()) {
            return TYPE_FOLDER;
        }
        else {
            if (list.get(position) instanceof HttpSurfFileBean) {
                return TYPE_HTTP_RECORD;
            }
            else {
                return TYPE_FILE;
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOLDER) {
            return new FolderHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_gdb_surf_folder, parent, false));
        }
        // server record files
        else if (viewType == TYPE_HTTP_RECORD) {
            RecordHolder holder = new RecordHolder(parent);
            holder.setParameters(parent.getContext().getResources().getColor(R.color.gdb_record_text_normal_light)
                    , parent.getContext().getResources().getColor(R.color.gdb_record_text_bareback_light), serverFileListener);
            return holder;
        }
        else {
            return new FileHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_surf_file, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FileBean bean = list.get(position);
        if (holder instanceof FolderHolder) {
            FolderHolder fHolder = (FolderHolder) holder;
            fHolder.tvName.setText(bean.getName());
            fHolder.tvSize.setText(FormatUtil.formatSize(bean.getSize()));
            fHolder.tvDate.setText(FormatUtil.formatDate(bean.getLastModifyTime()));
            fHolder.tvName.setText(bean.getName());
            fHolder.groupFolder.setTag(bean);
            fHolder.groupFolder.setOnClickListener(folderListener);
            fHolder.groupFolder.setTag(bean);
            fHolder.groupFolder.setOnLongClickListener(folderLongClickListener);
        }
        // server record files
        else if (bean instanceof HttpSurfFileBean) {
            RecordHolder rHolder = (RecordHolder) holder;
            rHolder.hideIndexView();
            Record record = ((HttpSurfFileBean) bean).getRecord();
            if (record == null) {
                rHolder.bind(bean.getName(), bean.getLastModifyTime(), bean.getSize());
            }
            else {
                rHolder.bind(record, position);
                rHolder.bindExtra(bean.getLastModifyTime(), bean.getSize());
            }
        }
        // normal files
        else {
            FileHolder fHolder = (FileHolder) holder;
            fHolder.tvName.setText(bean.getName());
            fHolder.tvDate.setText(FormatUtil.formatDate(bean.getLastModifyTime()));
            fHolder.tvSize.setText(FormatUtil.formatSize(bean.getSize()));
            fHolder.groupContainer.setTag(bean);
            fHolder.groupContainer.setOnClickListener(fileListener);
            fHolder.groupContainer.setTag(bean);
            fHolder.groupContainer.setOnLongClickListener(fileLongClickListener);

            if (FileUtil.isGifFile(bean.getPath())) {
                String imgPath = GdbImageProvider.parseFilePath(bean.getPath());
                GlideApp.with(GdbApplication.getInstance())
                        .asGif()
                        .load(imgPath)
                        // 用了error和placeHolder gif就不起作用了
//                        .apply(imageOptions)
                        .into(fHolder.ivThumb);
            }
            else if (FileUtil.isImageFile(bean.getPath())) {
                String imgPath = GdbImageProvider.parseFilePath(bean.getPath());
                Glide.with(GdbApplication.getInstance())
                        .load(imgPath)
                        .apply(imageOptions)
                        .into(fHolder.ivThumb);
            }
            else {
                fHolder.ivThumb.setImageResource(R.drawable.ic_unknow);
            }
        }
    }

    View.OnClickListener serverFileListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (onSurfItemActionListener != null) {
                Record record = (Record) v.getTag();
                onSurfItemActionListener.onClickSurfFile(record);
            }
        }
    };

    View.OnClickListener fileListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (onSurfItemActionListener != null) {
                FileBean bean = (FileBean) v.getTag();
                onSurfItemActionListener.onClickSurfFile(bean);
            }
        }
    };

    View.OnLongClickListener fileLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (onSurfItemActionListener != null) {
                FileBean bean = (FileBean) v.getTag();
                onSurfItemActionListener.onLongClickSurfFile(bean);
            }
            return true;
        }
    };

    View.OnClickListener folderListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (onSurfItemActionListener != null) {
                FileBean bean = (FileBean) v.getTag();
                onSurfItemActionListener.onClickSurfFolder(bean);
            }
        }
    };

    View.OnLongClickListener folderLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (onSurfItemActionListener != null) {
                FileBean bean = (FileBean) v.getTag();
                onSurfItemActionListener.onLongClickSurfFolder(bean);
            }
            return true;
        }
    };

    @Override
    public int getItemCount() {
        return list == null ? 0:list.size();
    }

    public static class FolderHolder extends RecyclerView.ViewHolder {

        LinearLayout groupFolder;
        TextView tvName;
        TextView tvSize;
        TextView tvDate;

        public FolderHolder(View itemView) {
            super(itemView);

            groupFolder = (LinearLayout) itemView.findViewById(R.id.group_folder);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvSize = (TextView) itemView.findViewById(R.id.tv_size);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
        }
    }

    public static class FileHolder extends RecyclerView.ViewHolder {

        RelativeLayout groupContainer;
        BubbleImageView ivThumb;
        TextView tvName;
        TextView tvDate;
        TextView tvSize;

        public FileHolder(View itemView) {
            super(itemView);

            groupContainer = (RelativeLayout) itemView.findViewById(R.id.group_container);
            ivThumb = (BubbleImageView) itemView.findViewById(R.id.iv_thumb);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
            tvSize = (TextView) itemView.findViewById(R.id.tv_size);
        }
    }

    public interface OnSurfItemActionListener<T> {
        void onClickSurfFolder(FileBean fileBean);
        void onLongClickSurfFolder(FileBean bean);
        void onClickSurfFile(T file);
        void onLongClickSurfFile(T bean);
    }
}
