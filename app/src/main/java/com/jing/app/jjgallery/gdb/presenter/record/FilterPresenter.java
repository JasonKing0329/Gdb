package com.jing.app.jjgallery.gdb.presenter.record;

import com.google.gson.Gson;
import com.jing.app.jjgallery.gdb.GdbConstants;
import com.jing.app.jjgallery.gdb.model.SettingProperties;
import com.jing.app.jjgallery.gdb.model.bean.recommend.FilterBean;
import com.jing.app.jjgallery.gdb.model.bean.recommend.FilterModel;
import com.jing.app.jjgallery.gdb.util.DebugLog;
import com.king.service.gdb.sqlbrite.table.ISortRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/3 0003.
 * the filter handle process
 */

public class FilterPresenter {

    public FilterModel getFilters() {
        String json = SettingProperties.getGdbFilterModel();
        DebugLog.e(json);
        Gson gson = new Gson();
        try {
            FilterModel modle = gson.fromJson(json, FilterModel.class);
            if (modle == null) {
                return createFilters();
            }
            return modle;
        } catch (Exception e) {
            e.printStackTrace();
            return createFilters();
        }
    }
    public void saveFilters(FilterModel modle) {
        Gson gson = new Gson();
        String data = gson.toJson(modle);
        DebugLog.e(data);
        SettingProperties.saveGdbFilterModel(data);
    }

    /**
     * 重新创建默认filter
     * @return
     */
    public FilterModel createFilters() {
        String[] keys = new String[] {
                GdbConstants.FILTER_KEY_SCORE,
                GdbConstants.FILTER_KEY_SCORE_CUM, GdbConstants.FILTER_KEY_SCORE_FK,
                GdbConstants.FILTER_KEY_SCORE_STAR,
                GdbConstants.FILTER_KEY_SCORE_STARC,
                GdbConstants.FILTER_KEY_SCORE_BJOB, GdbConstants.FILTER_KEY_SCORE_BAREBACK,
                GdbConstants.FILTER_KEY_SCORE_STORY, GdbConstants.FILTER_KEY_SCORE_RHYTHM,
                GdbConstants.FILTER_KEY_SCORE_SCECE, GdbConstants.FILTER_KEY_SCORE_RIM,
                GdbConstants.FILTER_KEY_SCORE_CSHOW, GdbConstants.FILTER_KEY_SCORE_SPECIAL,
                GdbConstants.FILTER_KEY_SCORE_FOREPLAY, GdbConstants.FILTER_KEY_SCORE_DEPRECATED
        };
        String[] keyFileds = new String[] {
                ISortRecord.COLUMN_SCORE,
                ISortRecord.COLUMN_CUM, ISortRecord.COLUMN_FK,
                ISortRecord.COLUMN_STAR,
                ISortRecord.COLUMN_STARC,
                ISortRecord.COLUMN_BJOB, ISortRecord.COLUMN_BAREBACK,
                ISortRecord.COLUMN_STORY, ISortRecord.COLUMN_RHYTHM,
                ISortRecord.COLUMN_SCENE, ISortRecord.COLUMN_RIM,
                ISortRecord.COLUMN_CSHOW, ISortRecord.COLUMN_SPECIAL,
                ISortRecord.COLUMN_FOREPLAY, ISortRecord.COLUMN_DEPRECATED
        };
        List<FilterBean> list = new ArrayList<>();
        FilterModel modle = new FilterModel();
        modle.setSupportNR(false);
        modle.setList(list);
        for (int i = 0; i < keys.length; i ++) {
            FilterBean bean = new FilterBean();
            bean.setKeyword(keys[i]);
            bean.setKeywordFiled(keyFileds[i]);
            list.add(bean);
        }
        saveFilters(modle);
        return modle;
    }
}
