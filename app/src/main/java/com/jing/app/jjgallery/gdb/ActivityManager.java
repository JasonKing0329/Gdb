package com.jing.app.jjgallery.gdb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.jing.app.jjgallery.gdb.model.conf.ConfManager;
import com.jing.app.jjgallery.gdb.view.game.RandomActivity;
import com.jing.app.jjgallery.gdb.view.home.GHomeActivity;
import com.jing.app.jjgallery.gdb.view.record.phone.RecordListActivity;
import com.jing.app.jjgallery.gdb.view.record.pad.RecordListPadActivity;
import com.jing.app.jjgallery.gdb.view.record.pad.RecordPadActivity;
import com.jing.app.jjgallery.gdb.view.record.phone.RecordPhoneActivity;
import com.jing.app.jjgallery.gdb.view.settings.ManageActivity;
import com.jing.app.jjgallery.gdb.view.settings.SettingsActivity;
import com.jing.app.jjgallery.gdb.view.star.StarActivity;
import com.jing.app.jjgallery.gdb.view.star.StarSwipeActivity;
import com.jing.app.jjgallery.gdb.view.star.pad.StarPadActivity;
import com.jing.app.jjgallery.gdb.view.star.phone.StarPhoneActivity;
import com.jing.app.jjgallery.gdb.view.surf.SurfHttpActivity;
import com.jing.app.jjgallery.gdb.view.surf.SurfLocalActivity;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.Star;

import java.io.File;

/**
 * Created by JingYang on 2016/7/12 0012.
 * Description:
 */
public class ActivityManager {

    public static boolean startHomeActivity(Activity from) {
        if (!new File(ConfManager.GDB_DB_PATH).exists()) {
            ((BaseView) from).showToastLong(from.getString(R.string.gdb_no_conf), BaseView.TOAST_WARNING);
            return false;
        }
        Intent intent = new Intent().setClass(from, GHomeActivity.class);
        from.startActivity(intent);
        return true;
    }

    public static boolean startStarSwipeActivity(Activity from) {
        Intent intent = new Intent().setClass(from, StarSwipeActivity.class);
        from.startActivity(intent);
        return true;
    }

    public static boolean startStarListActivity(Activity from) {
        Intent intent = new Intent().setClass(from, StarPhoneActivity.class);
        from.startActivity(intent);
        return true;
    }

    public static boolean startStarPadActivity(Activity from) {
        Intent intent = new Intent().setClass(from, StarPadActivity.class);
        from.startActivity(intent);
        return true;
    }

    public static boolean startRecordListActivity(Activity from) {
        Intent intent = new Intent().setClass(from, RecordListActivity.class);
        from.startActivity(intent);
        return true;
    }

    public static boolean startRecordListPadActivity(Activity from, String scene) {
        Intent intent = new Intent().setClass(from, RecordListPadActivity.class);
        if (!TextUtils.isEmpty(scene)) {
            intent.putExtra(RecordListPadActivity.KEY_SCENE_NAME, scene);
        }
        from.startActivity(intent);
        return true;
    }

    public static boolean startRecordListPadActivity(Activity from) {
        startRecordListPadActivity(from, null);
        return true;
    }

    public static boolean startRandomActivity(Activity from) {
        Intent intent = new Intent().setClass(from, RandomActivity.class);
        from.startActivity(intent);
        return true;
    }

    public static void startSettingActivity(Activity from) {
        from.startActivity(new Intent().setClass(from, SettingsActivity.class));
    }

    public static void reload(Activity from) {
        Intent intent = from.getIntent();
        from.overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        from.finish();
        from.overridePendingTransition(0, 0);
        from.startActivity(intent);
    }

    public static void startStarActivity(Activity from, long starId) {
        Bundle bundle = new Bundle();
        bundle.putLong(StarActivity.KEY_STAR_ID, starId);
        Intent intent = new Intent().setClass(from, StarActivity.class);
        intent.putExtras(bundle);
        from.startActivity(intent);
    }

    public static void startStarActivity(Activity from, Star star) {
        startStarActivity(from, star.getId());
    }

    public static void startRecordPadActivity(Context from, Record record) {
        startRecordPadActivity(from, record, null);
    }

    public static void startRecordActivity(Context from, Record record) {
        startRecordActivity(from, record, null);
    }

    /**
     * support transition animation
     * @param from
     * @param record
     * @param pairs transition转场动画
     */
    public static void startRecordPadActivity(Context from, Record record, android.util.Pair<View, String>[] pairs) {
        Intent intent = new Intent().setClass(from, RecordPadActivity.class);
        intent.putExtra(RecordPadActivity.KEY_RECORD_ID, record.getId());
        from.startActivity(intent);
//        if (from instanceof Activity) {
//            if (pairs == null) {
//                from.startActivity(intent);
//            }
//            else {
//                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) from, pairs);
//                from.startActivity(intent, options.toBundle());
//            }
//        }
    }

    /**
     * support transition animation
     * @param from
     * @param record
     * @param pairs transition转场动画
     */
    public static void startRecordActivity(Context from, Record record, android.util.Pair<View, String>[] pairs) {
        Intent intent = new Intent().setClass(from, RecordPhoneActivity.class);
        intent.putExtra(RecordPhoneActivity.KEY_RECORD_ID, record.getId());
        from.startActivity(intent);
//        if (from instanceof Activity) {
//            if (pairs == null) {
//                from.startActivity(intent);
//            }
//            else {
//                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) from, pairs);
//                from.startActivity(intent, options.toBundle());
//            }
//        }
    }

    public static boolean startGameActivity(Activity from) {
//        if (!new File(ConfManager.GDB_GAME_DB_PATH).exists()) {
//            ((ProgressProvider) from).showToastLong(from.getString(R.string.gdb_no_conf), ProgressProvider.TOAST_WARNING);
//            return false;
//        }
//        Intent intent = new Intent().setClass(from, SeasonActivity.class);
//        from.startActivity(intent);
        return true;
    }

    public static void startSurfHttpActivity(Activity from) {
        Intent intent = new Intent().setClass(from, SurfHttpActivity.class);
        from.startActivity(intent);
    }

    /**
     * SurfLocalActivity作为文件浏览器
     * @param from
     */
    public static void startSurfLocalActivity(Activity from) {
        Intent intent = new Intent().setClass(from, SurfLocalActivity.class);
        from.startActivity(intent);
    }

    /**
     * SurfLocalActivity作为文件选择器
     * @param from
     * @param requestCode
     */
    public static void startSurfLocalActivity(Activity from, int requestCode) {
        Intent intent = new Intent().setClass(from, SurfLocalActivity.class);
        intent.putExtra(GdbConstants.KEY_REQEUST_CODE, requestCode);
        from.startActivityForResult(intent, requestCode);
    }

    public static void startManageActivity(Activity from) {
        Intent intent = new Intent().setClass(from, ManageActivity.class);
        from.startActivity(intent);
    }

}
