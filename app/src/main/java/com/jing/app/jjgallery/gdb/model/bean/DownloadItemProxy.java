package com.jing.app.jjgallery.gdb.model.bean;

import com.jing.app.jjgallery.gdb.http.bean.data.DownloadItem;

/**
 * Created by Administrator on 2016/9/2.
 */
public class DownloadItemProxy {
    private DownloadItem item;
    private int progress;

    public DownloadItem getItem() {
        return item;
    }

    public void setItem(DownloadItem item) {
        this.item = item;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
