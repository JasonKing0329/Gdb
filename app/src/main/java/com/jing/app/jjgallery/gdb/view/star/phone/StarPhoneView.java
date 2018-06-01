package com.jing.app.jjgallery.gdb.view.star.phone;

import com.jing.app.jjgallery.gdb.BaseView;
import com.jing.app.jjgallery.gdb.view.star.StarListFragment;

/**
 * @desc
 * @auth 景阳
 * @time 2018/2/13 0013 11:20
 */

public interface StarPhoneView extends BaseView {
    void showTitles(int all, int top, int bottom, int half);

    StarListFragment getCurrentPage();

    void updateMenuViewMode(String title);
}
