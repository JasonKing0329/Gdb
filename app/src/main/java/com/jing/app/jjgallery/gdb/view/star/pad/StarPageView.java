package com.jing.app.jjgallery.gdb.view.star.pad;

import android.widget.TextView;

import com.jing.app.jjgallery.gdb.BaseView;
import com.king.app.gdb.data.entity.Star;

/**
 * @desc
 * @auth 景阳
 * @time 2018/2/15 0015 19:17
 */

public interface StarPageView extends BaseView {
    void showStar(Star star);

    void showRating(String rating);

    TextView getRatingTextView();
}
