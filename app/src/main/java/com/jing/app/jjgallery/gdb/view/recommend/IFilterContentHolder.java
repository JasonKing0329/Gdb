package com.jing.app.jjgallery.gdb.view.recommend;

import com.jing.app.jjgallery.gdb.IFragmentHolder;
import com.jing.app.jjgallery.gdb.model.bean.recommend.FilterModel;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/10/26 9:25
 */
public interface IFilterContentHolder extends IFragmentHolder {
    FilterModel getFilterModel();
    void onSaveFilterModel();
}
