package com.jing.app.jjgallery.gdb.view.download;

import com.jing.app.jjgallery.gdb.IFragmentHolder;
import com.jing.app.jjgallery.gdb.http.bean.data.DownloadItem;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/7/25 11:50
 */
public interface IDownloadContentHolder extends IFragmentHolder {

    List<DownloadItem> getDownloadList();

    List<DownloadItem> getExistedList();

    String getSavePath();

    void dismissDialog();

    void startDownload();

    void addDownloadItems(List<DownloadItem> checkedItems);
}
