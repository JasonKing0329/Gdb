package com.jing.app.jjgallery.gdb.model.db;

import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.util.DebugLog;
import com.king.service.gdb.GDBProvider;
import com.king.service.gdb.sqlbrite.GDataProvider;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/10/25 10:48
 */
public class GdbProviderHelper {

    private static GDBProvider instance;

    public static void createProvider() {
        DebugLog.e();
        instance = new GDataProvider(new GDataContext(GdbApplication.getInstance()));
    }

    public static GDBProvider getProvider() {
        if (instance == null) {
            createProvider();
        }
        return instance;
    }

    public static void close() {
        if (instance != null) {
            DebugLog.e();
            instance.close();
            instance = null;
        }
    }
}
