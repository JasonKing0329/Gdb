package com.jing.app.jjgallery.gdb.view.surf;

import com.jing.app.jjgallery.gdb.http.bean.data.FileBean;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/7/27 11:21
 */
public interface ISurfLocalView {

    void onFolderReceived(List<FileBean> list);

    void onSortFinished();
}
