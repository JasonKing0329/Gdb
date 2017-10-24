package com.jing.app.jjgallery.gdb.model.conf;

import android.content.Context;

public class DBInfor {

	public static String DB_PATH;

	// gdata.db只支持外部扩展目录，不内置到程序中
	public static String GDB_DB_PATH = ConfManager.GDB_DB_PATH;
	public static String GDB_GAME_DB_PATH = ConfManager.GDB_GAME_DB_PATH;
	public static String GDB_FAVOR_DB_PATH = ConfManager.GDB_FAVOR_DB_PATH;

	public static boolean prepareDatabase(Context context) {
		boolean result = ConfManager.prepareDatabase(context);
		DB_PATH = ConfManager.DB_PATH;
		return  result;
	}
}
