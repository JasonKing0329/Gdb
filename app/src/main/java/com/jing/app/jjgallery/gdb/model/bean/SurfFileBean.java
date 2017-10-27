package com.jing.app.jjgallery.gdb.model.bean;

import com.jing.app.jjgallery.gdb.http.bean.data.FileBean;
import com.king.service.gdb.bean.Record;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/7/28 10:43
 */
public class SurfFileBean extends FileBean {
    private Record record;

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }
}
