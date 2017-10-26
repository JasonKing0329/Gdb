package com.jing.app.jjgallery.gdb;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jing.app.jjgallery.gdb.model.ObjectCache;
import com.jing.app.jjgallery.gdb.model.conf.ConfManager;
import com.jing.app.jjgallery.gdb.view.home.GHomeActivity;
import com.jing.app.jjgallery.gdb.view.pub.ProgressProvider;
import com.jing.app.jjgallery.gdb.view.record.RecordActivity;
import com.jing.app.jjgallery.gdb.view.record.RecordListActivity;
import com.jing.app.jjgallery.gdb.view.settings.SettingsActivity;
import com.jing.app.jjgallery.gdb.view.star.StarActivity;
import com.jing.app.jjgallery.gdb.view.star.StarListActivity;
import com.jing.app.jjgallery.gdb.view.star.StarSwipeActivity;
import com.king.service.gdb.bean.Record;
import com.king.service.gdb.bean.Star;

import java.io.File;

/**
 * Created by JingYang on 2016/7/12 0012.
 * Description:
 */
public class ActivityManager {

    public static boolean startHomeActivity(Activity from) {
        if (!new File(ConfManager.GDB_DB_PATH).exists()) {
            ((ProgressProvider) from).showToastLong(from.getString(R.string.gdb_no_conf), ProgressProvider.TOAST_WARNING);
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
        Intent intent = new Intent().setClass(from, StarListActivity.class);
        from.startActivity(intent);
        return true;
    }

    public static boolean startRecordListActivity(Activity from) {
        Intent intent = new Intent().setClass(from, RecordListActivity.class);
        from.startActivity(intent);
        return true;
    }

    public static void startSettingActivity(Activity from) {
        from.startActivity(new Intent().setClass(from, SettingsActivity.class));
    }

    public static void startSettingActivity(Activity from, int requestCode) {
        from.startActivityForResult(new Intent().setClass(from, SettingsActivity.class), requestCode);
    }

    public static void reload(Activity from) {
        Intent intent = from.getIntent();
        from.overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        from.finish();
        from.overridePendingTransition(0, 0);
        from.startActivity(intent);
    }

    public static void startStarActivity(Activity from, int starId) {
        Bundle bundle = new Bundle();
        bundle.putInt(StarActivity.KEY_STAR_ID, starId);
        Intent intent = new Intent().setClass(from, StarActivity.class);
        intent.putExtras(bundle);
        from.startActivity(intent);
    }

    public static void startStarActivity(Activity from, Star star) {
        startStarActivity(from, star.getId());
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
    public static void startRecordActivity(Context from, Record record, android.util.Pair<View, String>[] pairs) {
        ObjectCache.putData(record);
        Intent intent = new Intent().setClass(from, RecordActivity.class);
        if (from instanceof Activity) {
            if (pairs == null) {
                from.startActivity(intent);
            }
            else {
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) from, pairs);
                from.startActivity(intent, options.toBundle());
            }
        }
    }

    public static boolean startGDBGameActivity(Activity from, Bundle bundle) {
//        if (!new File(ConfManager.GDB_GAME_DB_PATH).exists()) {
//            ((ProgressProvider) from).showToastLong(from.getString(R.string.gdb_no_conf), ProgressProvider.TOAST_WARNING);
//            return false;
//        }
//        Intent intent = new Intent().setClass(from, SeasonActivity.class);
//        if (bundle == null) {
//            from.startActivity(intent);
//            applyAnimation(from);
//        }
//        else {
//            ActivityCompat.startActivity(from, intent, bundle);
//        }
        return true;
    }

    public static void startGdbSurfActivity(Activity from, Bundle bundle) {
//        Intent intent = new Intent().setClass(from, com.jing.app.jjgallery.gdb.view.surf.SurfActivity.class);
//        if (bundle == null) {
//            from.startActivity(intent);
//            applyAnimation(from);
//        }
//        else {
//            ActivityCompat.startActivity(from, intent, bundle);
//        }
    }

    public static void startGdbGameGroupActivity(Activity from, int seasonId) {
//        Intent intent = new Intent().setClass(from, GroupActivity.class);
//        intent.putExtra(GroupActivity.KEY_SEASON_ID, seasonId);
//        from.startActivity(intent);
//        applyAnimation(from);
    }

    public static void startGdbGameBattleActivity(Activity from, int seasonId) {
//        Intent intent = new Intent().setClass(from, BattleActivity.class);
//        intent.putExtra(BattleActivity.KEY_SEASON_ID, seasonId);
//        from.startActivity(intent);
//        applyAnimation(from);
    }

    public static void startGdbGameCrossActivity(Activity from, int seasonId) {
//        Intent intent = new Intent().setClass(from, CrossActivity.class);
//        intent.putExtra(CrossActivity.KEY_SEASON_ID, seasonId);
//        from.startActivity(intent);
//        applyAnimation(from);
    }

}
