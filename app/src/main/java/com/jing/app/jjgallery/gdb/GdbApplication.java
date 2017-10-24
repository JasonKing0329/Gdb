package com.jing.app.jjgallery.gdb;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;

import com.jing.app.jjgallery.gdb.util.DebugLog;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/10/24 14:15
 */
public class GdbApplication extends Application {

    private static GdbApplication instance;

    private List<Activity> activityList;

    private Activity currentActivity;

    public static GdbApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        activityList = new ArrayList<>();

        // logger
        Logger.addLogAdapter(new AndroidLogAdapter());

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                DebugLog.e(activity.getClass().getName());
                activityList.add(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                DebugLog.e(activity.getClass().getName());
                currentActivity = activity;
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                DebugLog.e(activity.getClass().getName());

                // 最后一个activity结束
                if (activityList.size() == 1 && activityList.get(0) == activity) {
                    stopAllService(activity);
                }

                activityList.remove(activity);

                if (activity == currentActivity) {
                    currentActivity = null;
                }
                if (activityList.size() == 0) {
                    currentActivity = null;
                }
            }
        });
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    private void stopAllService(Activity activity) {
    }

    @Override
    public void onTerminate() {
        DebugLog.e();
        if (currentActivity != null) {
            stopAllService(currentActivity);
        }
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        DebugLog.e();
        super.onLowMemory();
    }

    public void closeAll() {

        // 关闭所有服务
        if (currentActivity != null) {
            stopAllService(currentActivity);
        }

        // 关闭除了认证以外所有的activity
        if (activityList != null) {
            try {
                for (int i = 0; i < activityList.size(); i ++) {
                    activityList.get(i).finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * use number 21 to mark, make codes runs well under android L
     * @return
     */
    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * use number 21 to mark, make codes runs well under android L
     * @return
     */
    public static boolean isM() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

}
