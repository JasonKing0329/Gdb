package com.jing.app.jjgallery.gdb.view.home;

import com.jing.app.jjgallery.gdb.model.bean.StarProxy;
import com.king.app.gdb.data.entity.Record;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/5/19 13:37
 */
public class GHomeBean {

    private List<StarProxy> starList;
    private List<Record> recordList;

    public List<StarProxy> getStarList() {
        return starList;
    }

    public void setStarList(List<StarProxy> starList) {
        this.starList = starList;
    }

    public List<Record> getRecordList() {
        return recordList;
    }

    public void setRecordList(List<Record> recordList) {
        this.recordList = recordList;
    }
}
