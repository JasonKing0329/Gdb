package com.jing.app.jjgallery.gdb.view.star.phone;

import com.jing.app.jjgallery.gdb.BaseView;

/**
 * @desc
 * @auth 景阳
 * @time 2018/2/13 0013 11:20
 */

public interface StarPhoneView extends BaseView {
    void showTitles(int all, int top, int bottom, int half);

    void onFavorListLoaded();
}
