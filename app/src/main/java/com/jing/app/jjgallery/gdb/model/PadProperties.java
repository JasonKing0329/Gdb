package com.jing.app.jjgallery.gdb.model;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.model.conf.PreferenceKey;

/**
 * Created by JingYang on 2016/6/24 0024.
 * Description:
 */
public class PadProperties {

    private static final String getString(String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(GdbApplication.getInstance());
        return sp.getString(key, "");
    }

    private static final void setString(String key, String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(GdbApplication.getInstance());
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private static final int getInt(String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(GdbApplication.getInstance());
        return sp.getInt(key, -1);
    }

    private static final void setInt(String key, int value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(GdbApplication.getInstance());
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    private static final boolean getBoolean(String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(GdbApplication.getInstance());
        return sp.getBoolean(key, false);
    }

    private static final void setBoolean(String key, boolean value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(GdbApplication.getInstance());
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * PAD_STAR_RECORDS_VIEW_MODE
     * @return
     */
    public static int getStarRecordViewMode() {
        return getInt(PreferenceKey.PAD_STAR_RECORDS_VIEW_MODE);
    }

    /**
     * PAD_STAR_RECORDS_VIEW_MODE
     * @param mode
     */
    public static void setStarRecordViewMode(int mode) {
        setInt(PreferenceKey.PAD_STAR_RECORDS_VIEW_MODE, mode);
    }

    /**
     * PAD_ORDER_RECORD_SORT
     * @return
     */
    public static int getRecordOrderSortType() {
        return getInt(PreferenceKey.PAD_ORDER_RECORD_SORT);
    }

    /**
     * PAD_ORDER_RECORD_SORT
     * @param mode
     */
    public static void setRecordOrderSortType(int mode) {
        setInt(PreferenceKey.PAD_ORDER_RECORD_SORT, mode);
    }

    /**
     * PAD_ORDER_ITEM_RECORD_SORT
     * @return  PreferenceValue.GDB_SR_ORDERBY_XX
     */
    public static int getRecordOrderItemSortType() {
        return getInt(PreferenceKey.PAD_ORDER_ITEM_RECORD_SORT);
    }

    /**
     * PAD_ORDER_ITEM_RECORD_SORT
     * @param mode
     */
    public static void setRecordOrderItemSortType(int mode) {
        setInt(PreferenceKey.PAD_ORDER_ITEM_RECORD_SORT, mode);
    }

    /**
     * PAD_ORDER_RECORD_SORT
     * @return
     */
    public static boolean isRecordOrderItemSortDesc() {
        return getBoolean(PreferenceKey.PAD_ORDER_ITEM_RECORD_DESC);
    }

    /**
     * PAD_ORDER_ITEM_RECORD_DESC
     * @param desc
     */
    public static void setRecordOrderItemSortDesc(boolean desc) {
        setBoolean(PreferenceKey.PAD_ORDER_ITEM_RECORD_DESC, desc);
    }
}
