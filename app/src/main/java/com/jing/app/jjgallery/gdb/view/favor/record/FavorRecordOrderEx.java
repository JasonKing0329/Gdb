package com.jing.app.jjgallery.gdb.view.favor.record;

import com.king.app.gdb.data.entity.FavorRecordOrder;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/14 11:03
 */
public class FavorRecordOrderEx {

    private FavorRecordOrder order;

    private String cover;

    private List<String> thumbItems;

    public FavorRecordOrder getOrder() {
        return order;
    }

    public void setOrder(FavorRecordOrder order) {
        this.order = order;
    }

    public List<String> getThumbItems() {
        return thumbItems;
    }

    public void setThumbItems(List<String> thumbItems) {
        this.thumbItems = thumbItems;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
}
