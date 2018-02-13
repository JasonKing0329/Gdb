package com.jing.app.jjgallery.gdb.view.star;

import com.jing.app.jjgallery.gdb.BaseView;
import com.jing.app.jjgallery.gdb.model.bean.StarProxy;

import java.util.List;

/**
 * @desc
 * @auth 景阳
 * @time 2018/2/13 0013 11:41
 */

public interface StarListView extends BaseView {

    void onLoadStarList(List<StarProxy> list);
    void onLoadStarError(String message);
}
