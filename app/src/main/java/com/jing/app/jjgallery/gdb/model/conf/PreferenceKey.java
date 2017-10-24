package com.jing.app.jjgallery.gdb.model.conf;

/**
 * Created by JingYang on 2016/6/24 0024.
 * Description:
 */
public class PreferenceKey {

    /**
     * configuration
     */
    // 是否第一次登录APP
    public static final String PREF_APP_INITED = "pref_app_inited";
    public static final String PREF_VERSION = "pref_version";

    /**
     * setting
     */
    // 主界面
    public static final String PREF_HOME_VIEW = "pref_general_list_home";
    // 启用指纹验证
    public static final String PREF_SAFETY_FP = "pref_safety_fingerprint";

    /**
     * gdb
     */
    // gdb record list排序方式
    public static final String PREF_GDB_RECORD_ORDER = "pref_gdb_record_order";
    // gdb star record排序方式
    public static final String PREF_GDB_STAR_RECORD_ORDER = "pref_gdb_star_record_order";
    // gdb filter model(json string)
    public static final String PREF_GDB_FILTER_MODEL = "pref_gdb_filter_model";
    // guide game image
    public static final String PREF_GDB_GAME_BG = "pref_gdb_guide_game_bg";
    // guide star image
    public static final String PREF_GDB_STAR_BG = "pref_gdb_guide_star_bg";
    // guide record image
    public static final String PREF_GDB_RECORD_BG = "pref_gdb_guide_record_bg";
    // guide nav header
    public static final String PREF_GDB_NAV_HEADER_BG = "pref_gdb_nav_header_bg";
    // gdb game scene
    public static final String PREF_GDB_GAME_SCENES = "pref_gdb_game_scenes";

    /**
     * http
     */
    // server url
    public static final String PREF_HTTP_SERVER = "pref_http_server";
    // 最大并发下载数量
    public static final String PREF_MAX_DOWNLOAD = "pref_http_download_max";
    // 检查更新
    public static final String PREF_CHECK_UPDATE = "pref_http_update";
    // 检查gdb数据库更新
    public static final String PREF_CHECK_UPDATE_GDB = "pref_http_update_gdb";
    // 备份数据
    public static final String PREF_CHECK_BACKUP = "pref_http_backup";

    /**
     * gdb
     */
    // latest records number
    public static final String PREF_GDB_LATEST_NUM = "pref_gdb_latest_num";
    // no image mode
    public static final String PREF_GDB_NO_IMAGE = "pref_gdb_no_image";
    // random animation of recommend item
    public static final String PREF_GDB_REC_ANIM_RANDOM = "pref_gdb_rec_anim_random";
    // fixed animation of recommend item
    public static final String PREF_GDB_REC_ANIM_TYPE = "pref_gdb_rec_anim_fix_type";
    // animation time
    public static final String PREF_GDB_REC_ANIM_TIME = "pref_gdb_rec_anim_time";
    // scene hsv star
    public static final String PREF_GDB_SCENE_HSV_START = "pref_gdb_scene_hsv_start";
    // scene hsv angle
    public static final String PREF_GDB_SCENE_HSV_ANGLE = "pref_gdb_scene_hsv_angle";
    // head path of gdb home nav header
    public static final String PREF_GDB_NAV_HEAD_RANDOM = "pref_gdb_nav_head_random";
    // random animation of recommend item
    public static final String PREF_GDB_STAR_LIST_NAV_ANIM_RANDOM = "pref_gdb_star_list_nav_anim_random";
    // fixed animation of star_list_navommend item
    public static final String PREF_GDB_STAR_LIST_NAV_ANIM_TYPE = "pref_gdb_star_list_nav_anim_fix_type";
    // animation time
    public static final String PREF_GDB_STAR_LIST_NAV_ANIM_TIME = "pref_gdb_star_list_nav_anim_time";
    // random animation of record
    public static final String PREF_GDB_RECORD_NAV_ANIM_RANDOM = "pref_gdb_record_nav_anim_random";
    // fixed animation of record item
    public static final String PREF_GDB_RECORD_NAV_ANIM_TYPE = "pref_gdb_record_nav_anim_fix_type";
    // animation time
    public static final String PREF_GDB_RECORD_NAV_ANIM_TIME = "pref_gdb_record_nav_anim_time";
    // random animation of star
    public static final String PREF_GDB_STAR_NAV_ANIM_RANDOM = "pref_gdb_star_nav_anim_random";
    // fixed animation of star item
    public static final String PREF_GDB_STAR_NAV_ANIM_TYPE = "pref_gdb_star_nav_anim_fix_type";
    // animation time
    public static final String PREF_GDB_STAR_NAV_ANIM_TIME = "pref_gdb_star_nav_anim_time";
    // relate surf to record
    public static final String PREF_GDB_SURF_RELATE = "pref_gdb_surf_relate";
    // list orientation of swipe view
    public static final String PREF_GDB_SWIPE_LIST_ORIENTATION = "pref_gdb_swipe_list_orientation";
    // star record list, card mode
    public static final String PREF_GDB_STAR_RECORDS_CARD = "pref_gdb_star_records_card";
}
