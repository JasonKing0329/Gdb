package com.jing.app.jjgallery.gdb.view.game;

import com.jing.app.jjgallery.gdb.model.bean.RandomStarBean;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/11/7 16:50
 */
public interface IRandomStarView {
    void onErrorMessage(String msg);

    void onRandomStar(List<RandomStarBean> starList);
}
