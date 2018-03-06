package com.jing.app.jjgallery.gdb.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.model.bean.HsvColorBean;
import com.jing.app.jjgallery.gdb.model.conf.ConfManager;
import com.jing.app.jjgallery.gdb.model.conf.PreferenceKey;
import com.jing.app.jjgallery.gdb.model.conf.PreferenceValue;

/**
 * Created by JingYang on 2016/6/24 0024.
 * Description:
 */
public class SettingProperties {

    /**
     * 保存默认配置到扩展目录作为备份
     */
    public static void saveAsDefaultPreference() {
        ConfManager.saveDefaultPref();
    }

    /**
     * shaprePreference文件版本(com.jing.app.jjgallery_preferences.xml)
     */
    public static String getPrefVersion() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GdbApplication.getInstance());
        return preferences.getString(PreferenceKey.PREF_VERSION, null);
    }

    /**
     * shaprePreference文件版本(com.jing.app.jjgallery_preferences.xml)
     * @param version version name
     */
    public static void setPrefVersion(String version) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GdbApplication.getInstance());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PreferenceKey.PREF_VERSION, version);
        editor.commit();
    }

    /**
     * 指纹验证
     */
    public static boolean isFingerPrintEnable() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GdbApplication.getInstance());
        return preferences.getBoolean(PreferenceKey.PREF_SAFETY_FP, false);
    }

    /**
     * 程序是否第一次打开（第一次登录成功前均属于第一次）
     */
    public static boolean isAppInited() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GdbApplication.getInstance());
        return preferences.getBoolean(PreferenceKey.PREF_APP_INITED, false);
    }

    /**
     * 设置程序第一次登陆成功
     */
    public static void setAppInited() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GdbApplication.getInstance());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PreferenceKey.PREF_APP_INITED, true);
        editor.commit();
        saveAsDefaultPreference();
    }

    /**
     * 主界面
     */
    public static int getStartViewMode() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GdbApplication.getInstance());
        String home = preferences.getString(PreferenceKey.PREF_HOME_VIEW, null);
        if (home != null) {
            try {
                return Integer.parseInt(home);
            } catch (Exception e) {
                e.printStackTrace();
                return  0;
            }
        }
        return 0;
    }

    /**
     * 保存key value
     * @param key preference key
     * @param value preference value
     */
    public static void savePreference(String key, String value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GdbApplication.getInstance());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
        saveAsDefaultPreference();
    }

    /**
     * get value by key
     * @param key preference key
     * @return preference value
     */
    public static String getPreference(String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GdbApplication.getInstance());
        return preferences.getString(key, null);
    }

    /**
     * gdb star record界面默认排序模式
     * @return see PreferenceValue.ORDERBY_XXX
     */
    public static int getGdbStarRecordOrderMode(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String mode = preferences.getString(PreferenceKey.PREF_GDB_STAR_RECORD_ORDER, "" + PreferenceValue.GDB_SR_ORDERBY_NONE);
        return Integer.parseInt(mode);
    }

    /**
     * 设置gdb star record默认排序模式
     * @param mode see PreferenceValue.SORDER_COVER_XXX
     */
    public static void setGdbStarRecordOrderMode(Context context, int mode) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PreferenceKey.PREF_GDB_STAR_RECORD_ORDER, "" + mode);
        editor.commit();
        saveAsDefaultPreference();
    }

    /**
     * gdb star pad record界面默认排序模式
     * @return see PreferenceValue.ORDERBY_XXX
     */
    public static int getStarPadRecordOrderMode() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GdbApplication.getInstance());
        String mode = preferences.getString(PreferenceKey.PREF_GDB_STAR_PAD_RECORD_ORDER, "" + PreferenceValue.GDB_SR_ORDERBY_NONE);
        return Integer.parseInt(mode);
    }

    /**
     * 设置gdb star pad record默认排序模式
     * @param mode see PreferenceValue.SORDER_COVER_XXX
     */
    public static void setStarPadRecordOrderMode(int mode) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GdbApplication.getInstance());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PreferenceKey.PREF_GDB_STAR_PAD_RECORD_ORDER, "" + mode);
        editor.commit();
        saveAsDefaultPreference();
    }

    /**
     * 设置gdb star pad record默认排序模式
     */
    public static boolean isStarPadRecordOrderDesc() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GdbApplication.getInstance());
        return preferences.getBoolean(PreferenceKey.PREF_GDB_STAR_PAD_RECORD_DESC, false);
    }

    /**
     * 设置gdb star pad record默认排序模式
     */
    public static void setStarPadRecordOrderDesc(boolean desc) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GdbApplication.getInstance());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PreferenceKey.PREF_GDB_STAR_PAD_RECORD_DESC, desc);
        editor.commit();
        saveAsDefaultPreference();
    }

    /**
     * gdb record list界面默认排序模式
     * @return see PreferenceValue.ORDERBY_XXX
     */
    public static int getGdbRecordOrderMode(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String mode = preferences.getString(PreferenceKey.PREF_GDB_RECORD_ORDER, "" + PreferenceValue.GDB_SR_ORDERBY_NONE);
        return Integer.parseInt(mode);
    }

    /**
     * 设置gdb star record默认排序模式
     * @param mode see PreferenceValue.SORDER_COVER_XXX
     */
    public static void setGdbRecordOrderMode(Context context, int mode) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PreferenceKey.PREF_GDB_RECORD_ORDER, "" + mode);
        editor.commit();
        saveAsDefaultPreference();
    }

    /**
     * http服务器站点
     */
    public static String getGdbServerBaseUrl(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String url = preferences.getString(PreferenceKey.PREF_HTTP_SERVER, "");
        return url;
    }

    /**
     * 最大并发下载数量
     */
    public static int getMaxDownloadItem(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String num = preferences.getString(PreferenceKey.PREF_MAX_DOWNLOAD, "" + PreferenceValue.HTTP_MAX_DOWNLOAD);
        return Integer.parseInt(num);
    }

    /**
     * gdb filter model
     */
    public static String getGdbFilterModel() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GdbApplication.getInstance());
        String json = preferences.getString(PreferenceKey.PREF_GDB_FILTER_MODEL, "");
        return json;
    }

    /**
     * gdb filter model
     */
    public static void saveGdbFilterModel(String json) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GdbApplication.getInstance());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PreferenceKey.PREF_GDB_FILTER_MODEL, json);
        editor.apply();
    }

    /**
     * gdb game scenes
     */
    public static String getGdbGameScenes() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GdbApplication.getInstance());
        String scene = preferences.getString(PreferenceKey.PREF_GDB_GAME_SCENES, "");
        return scene;
    }

    /**
     * gdb filter model
     */
    public static void saveGdbGameScenes(Context context, String scenes) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PreferenceKey.PREF_GDB_GAME_SCENES, scenes);
        editor.apply();
    }

    /**
     * latest records number
     */
    public static int getGdbLatestRecordsNumber(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String num = preferences.getString(PreferenceKey.PREF_GDB_LATEST_NUM, "" + PreferenceValue.GDB_LATEST_NUM);
        return Integer.parseInt(num);
    }

    /**
     * latest records number
     */
    public static boolean isGdbNoImageMode() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GdbApplication.getInstance());
        return preferences.getBoolean(PreferenceKey.PREF_GDB_NO_IMAGE, true);
    }

    /**
     * animation random mode
     */
    public static void setGdbRecmmendAnimRandom(Context context, boolean isRandom) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PreferenceKey.PREF_GDB_REC_ANIM_RANDOM, isRandom);
        editor.commit();
    }

    /**
     * animation random mode
     */
    public static boolean isGdbRecmmendAnimRandom(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(PreferenceKey.PREF_GDB_REC_ANIM_RANDOM, true);
    }

    /**
     * animation type
     */
    public static void setGdbRecommendAnimType(Context context, int mode) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PreferenceKey.PREF_GDB_REC_ANIM_TYPE, mode);
        editor.commit();
    }

    /**
     * animation type
     */
    public static int getGdbRecommendAnimType(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int mode = preferences.getInt(PreferenceKey.PREF_GDB_REC_ANIM_TYPE, 0);
        return mode;
    }

    /**
     * animation time
     */
    public static void setGdbRecommendAnimTime(Context context, int time) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PreferenceKey.PREF_GDB_REC_ANIM_TIME, time);
        editor.commit();
    }

    /**
     * animation type
     */
    public static int getGdbRecommendAnimTime() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GdbApplication.getInstance());
        int mode = preferences.getInt(PreferenceKey.PREF_GDB_REC_ANIM_TIME, 5000);
        return mode;
    }

    /**
     * scene hsv color
     */
    public static HsvColorBean getGdbSceneColor() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GdbApplication.getInstance());
        String json = preferences.getString(PreferenceKey.PREF_GDB_SCENE_HSV_COLOR, null);
        Gson gson = new Gson();
        try {
            HsvColorBean bean = gson.fromJson(json, HsvColorBean.class);
            if (bean == null) {
                return new HsvColorBean();
            }
            return bean;
        } catch (Exception e) {
            return new HsvColorBean();
        }
    }

    /**
     * scene hsv color
     */
    public static void setGdbSceneHsvColor(HsvColorBean color) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GdbApplication.getInstance());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PreferenceKey.PREF_GDB_SCENE_HSV_COLOR, new Gson().toJson(color));
        editor.commit();
    }

    /**
     * random head path in home nav header
     */
    public static void setGdbNavHeadRandom(boolean isRandom) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GdbApplication.getInstance());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PreferenceKey.PREF_GDB_NAV_HEAD_RANDOM, isRandom);
        editor.commit();
    }

    /**
     * random head path in home nav header
     */
    public static boolean isGdbNavHeadRandom() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GdbApplication.getInstance());
        return preferences.getBoolean(PreferenceKey.PREF_GDB_NAV_HEAD_RANDOM, true);
    }

    /**
     * animation random mode
     */
    public static void setGdbStarListNavAnimRandom(Context context, boolean isRandom) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PreferenceKey.PREF_GDB_STAR_LIST_NAV_ANIM_RANDOM, isRandom);
        editor.commit();
    }

    /**
     * animation random mode
     */
    public static boolean isGdbStarListNavAnimRandom(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(PreferenceKey.PREF_GDB_STAR_LIST_NAV_ANIM_RANDOM, true);
    }

    /**
     * animation type
     */
    public static void setGdbStarListNavAnimType(Context context, int mode) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PreferenceKey.PREF_GDB_STAR_LIST_NAV_ANIM_TYPE, mode);
        editor.commit();
    }

    /**
     * animation type
     */
    public static int getGdbStarListNavAnimType(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int mode = preferences.getInt(PreferenceKey.PREF_GDB_STAR_LIST_NAV_ANIM_TYPE, 0);
        return mode;
    }

    /**
     * animation time
     */
    public static void setGdbStarListNavAnimTime(Context context, int time) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PreferenceKey.PREF_GDB_STAR_LIST_NAV_ANIM_TIME, time);
        editor.commit();
    }

    /**
     * animation type
     */
    public static int getGdbStarListNavAnimTime(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int mode = preferences.getInt(PreferenceKey.PREF_GDB_STAR_LIST_NAV_ANIM_TIME, 5000);
        return mode;
    }

    /**
     * animation random mode
     */
    public static void setGdbRecordNavAnimRandom(Context context, boolean isRandom) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PreferenceKey.PREF_GDB_RECORD_NAV_ANIM_RANDOM, isRandom);
        editor.commit();
    }

    /**
     * animation random mode
     */
    public static boolean isGdbRecordNavAnimRandom(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(PreferenceKey.PREF_GDB_RECORD_NAV_ANIM_RANDOM, true);
    }

    /**
     * animation type
     */
    public static void setGdbRecordNavAnimType(Context context, int mode) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PreferenceKey.PREF_GDB_RECORD_NAV_ANIM_TYPE, mode);
        editor.commit();
    }

    /**
     * animation type
     */
    public static int getGdbRecordNavAnimType(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int mode = preferences.getInt(PreferenceKey.PREF_GDB_RECORD_NAV_ANIM_TYPE, 0);
        return mode;
    }

    /**
     * animation time
     */
    public static void setGdbRecordNavAnimTime(Context context, int time) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PreferenceKey.PREF_GDB_RECORD_NAV_ANIM_TIME, time);
        editor.commit();
    }

    /**
     * animation type
     */
    public static int getGdbRecordNavAnimTime(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int mode = preferences.getInt(PreferenceKey.PREF_GDB_RECORD_NAV_ANIM_TIME, 5000);
        return mode;
    }

    /**
     * animation random mode
     */
    public static void setGdbStarNavAnimRandom(Context context, boolean isRandom) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PreferenceKey.PREF_GDB_STAR_NAV_ANIM_RANDOM, isRandom);
        editor.commit();
    }

    /**
     * animation random mode
     */
    public static boolean isGdbStarNavAnimRandom(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(PreferenceKey.PREF_GDB_STAR_NAV_ANIM_RANDOM, true);
    }

    /**
     * animation type
     */
    public static void setGdbStarNavAnimType(Context context, int mode) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PreferenceKey.PREF_GDB_STAR_NAV_ANIM_TYPE, mode);
        editor.commit();
    }

    /**
     * animation type
     */
    public static int getGdbStarNavAnimType(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int mode = preferences.getInt(PreferenceKey.PREF_GDB_STAR_NAV_ANIM_TYPE, 0);
        return mode;
    }

    /**
     * animation time
     */
    public static void setGdbStarNavAnimTime(Context context, int time) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PreferenceKey.PREF_GDB_STAR_NAV_ANIM_TIME, time);
        editor.commit();
    }

    /**
     * animation type
     */
    public static int getGdbStarNavAnimTime(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int mode = preferences.getInt(PreferenceKey.PREF_GDB_STAR_NAV_ANIM_TIME, 5000);
        return mode;
    }

    /**
     * relate surf to record
     */
    public static void setGdbSurfRelated(boolean isRandom) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GdbApplication.getInstance());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PreferenceKey.PREF_GDB_SURF_RELATE, isRandom);
        editor.commit();
    }

    /**
     * relate surf to record
     */
    public static boolean isGdbSurfRelated() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GdbApplication.getInstance());
        return preferences.getBoolean(PreferenceKey.PREF_GDB_SURF_RELATE, true);
    }

    /**
     * relate surf to record
     * @param isHorizontal
     */
    public static void setGdbSwipeListOrientation(boolean isHorizontal) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GdbApplication.getInstance());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PreferenceKey.PREF_GDB_SWIPE_LIST_ORIENTATION, isHorizontal);
        editor.commit();
    }

    /**
     * relate surf to record
     */
    public static boolean isGdbSwipeListHorizontal() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GdbApplication.getInstance());
        return preferences.getBoolean(PreferenceKey.PREF_GDB_SWIPE_LIST_ORIENTATION, false);
    }

    /**
     * star record list, card mode
     * @param cardMode
     */
    public static void setGdbStarRecordsCardMode(boolean cardMode) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GdbApplication.getInstance());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PreferenceKey.PREF_GDB_STAR_RECORDS_CARD, cardMode);
        editor.commit();
    }

    /**
     * star record list, card mode
     */
    public static boolean isGdbStarRecordsCardMode() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GdbApplication.getInstance());
        return preferences.getBoolean(PreferenceKey.PREF_GDB_STAR_RECORDS_CARD, false);
    }

    /**
     * star pad record list, card mode
     * @param cardMode
     */
    public static void setStarRecordsCardMode(boolean cardMode) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GdbApplication.getInstance());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PreferenceKey.PREF_GDB_STAR_PAD_RECORDS_CARD, cardMode);
        editor.commit();
    }

    /**
     * star pad record list, card mode
     */
    public static boolean isStarRecordsCardMode() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GdbApplication.getInstance());
        return preferences.getBoolean(PreferenceKey.PREF_GDB_STAR_PAD_RECORDS_CARD, false);
    }

    /**
     * star list grid/list mode
     * @param mode see PreferenceValue.STAR_LIST_VIEW_XXX
     */
    public static void setStarListViewMode(int mode) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GdbApplication.getInstance());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PreferenceKey.PREF_STAR_LIST_VIEW_MODE, mode);
        editor.commit();
    }

    /**
     * animation type
     * @return see PreferenceValue.STAR_LIST_VIEW_XXX
     */
    public static int getStarListViewMode() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GdbApplication.getInstance());
        int mode = preferences.getInt(PreferenceKey.PREF_STAR_LIST_VIEW_MODE, 0);
        return mode;
    }

}
