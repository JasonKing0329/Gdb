package com.jing.app.jjgallery.gdb.view.home.pad;

import com.jing.app.jjgallery.gdb.BaseView;
import com.jing.app.jjgallery.gdb.model.bean.StarProxy;
import com.king.app.gdb.data.entity.Record;

import java.util.List;

/**
 * @desc
 * @auth 景阳
 * @time 2018/2/19 0019 10:33
 */

public interface HomePadView extends BaseView {
    void postShowStars(List<StarProxy> stars);

    void postShowRecommends(List<Record> list);

    void showRecords(List<Object> list);

    void notifyMoreRecords(int startOffset);
}
