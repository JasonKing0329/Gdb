package com.jing.app.jjgallery.gdb.view.star;

import com.jing.app.jjgallery.gdb.BaseView;
import com.jing.app.jjgallery.gdb.model.bean.StarProxy;
import com.jing.app.jjgallery.gdb.view.pub.FitSideBar;

import java.util.List;
import java.util.Map;

/**
 * @desc
 * @auth 景阳
 * @time 2018/2/13 0013 11:41
 */

public interface StarListView extends BaseView {

    FitSideBar getSidebar();

    void showRichList(List<StarProxy> list, Map<Long, Boolean> mExpandMap);

    void showCircleList(List<StarProxy> list);

    void notifyRichUpdated();

    void notifyCircleUpdated();
}
