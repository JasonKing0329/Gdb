package com.jing.app.jjgallery.gdb.model.conf;

import android.content.Context;

import com.jing.app.jjgallery.gdb.util.StorageUtil;

import java.io.File;
import java.io.IOException;

public class Configuration {

	public static final boolean DEBUG = true;
	public static final String DB_NAME = "gdata.db";
	public static final String TAG_AUTO_VIEW = "fe_autoview";
	public static final String TAG_CONFIG = "fe_Configuration";
	
	public static final String SDCARD = StorageUtil.getInnerStoragePath();
	public static final String EXTERNAL_SDCARD_HTC = "/storage/ext_sd";
	public static final String EXTERNAL_SDCARD_SAMSUNG = "/storage/extSdCard";
	
	public static final String APP_ROOT = SDCARD + "/fileencrypt";
	public static final String APP_DIR_IMG = APP_ROOT + "/img";
	public static final String APP_DIR_CROP_IMG = APP_DIR_IMG + "/crop";
	public static final String DOWNLOAD_IMAGE_DEFAULT = APP_DIR_IMG + "/download";
	public static final String GDB_IMG = APP_DIR_IMG + "/gdb";
	public static final String GDB_IMG_STAR = GDB_IMG + "/star";
	public static final String GDB_IMG_RECORD = GDB_IMG + "/record";
	public static final String APP_DIR_IMG_SAVEAS = APP_ROOT + "/saveas";
	public static final String APP_DIR_DB_HISTORY = APP_ROOT + "/history";
	public static final String APP_DIR_GAME = APP_ROOT + "/game";

	public static final String APP_DIR_EXPORT = APP_ROOT + "/export";

	public static final String DATABASE = "fileencrypt";
	public static final String EXTEND_RES_DIR = APP_ROOT + "/res";
	public static final String EXTEND_RES_COLOR = EXTEND_RES_DIR + "/color.xml";
	public static final String EXTEND_RES_DIMEN = EXTEND_RES_DIR + "/dimens.xml";
	public static final String ASSETS_RES_COLOR = "res/color.xml";
	public static final String ASSETS_RES_DIMEN = "res/dimens.xml";
	public static final String ASSETS_RES_GDB_GAME = "gdata_game.db";

	public static final String APP_DIR_CONF = APP_ROOT + "/conf";
	public static final String APP_DIR_CONF_PREF = APP_DIR_CONF + "/shared_prefs";
	public static final String APP_DIR_CONF_PREF_DEF = APP_DIR_CONF_PREF + "/default";
	public static final String APP_DIR_CONF_CRASH = APP_DIR_CONF + "/crash";
	public static final String APP_DIR_CONF_APP = APP_DIR_CONF + "/app";

	public static boolean init() {
		File file = new File(APP_ROOT);
		try {
			if (!file.exists()) {
				file.mkdir();
			}
			file = new File(APP_DIR_IMG);
			if (!file.exists()) {
				file.mkdir();
			}
			file = new File(APP_DIR_CROP_IMG);
			if (!file.exists()) {
				file.mkdir();
			}
			file = new File(DOWNLOAD_IMAGE_DEFAULT);
			if (!file.exists()) {
				file.mkdir();
			}
			file = new File(GDB_IMG);
			if (!file.exists()) {
				file.mkdir();
			}
			file = new File(GDB_IMG_STAR);
			if (!file.exists()) {
				file.mkdir();
			}
			file = new File(GDB_IMG_RECORD);
			if (!file.exists()) {
				file.mkdir();
			}
			file = new File(APP_DIR_IMG_SAVEAS);
			if (!file.exists()) {
				file.mkdir();
			}
			file = new File(APP_DIR_DB_HISTORY);
			if (!file.exists()) {
				file.mkdir();
			}
			file = new File(APP_DIR_GAME);
			if (!file.exists()) {
				file.mkdir();
			}
			file = new File(APP_DIR_EXPORT);
			if (!file.exists()) {
				file.mkdir();
			}
			file = new File(EXTEND_RES_DIR);
			if (!file.exists()) {
				file.mkdir();
			}
			file = new File(APP_DIR_CONF);
			if (!file.exists()) {
				file.mkdir();
			}
			file = new File(APP_DIR_CONF_PREF);
			if (!file.exists()) {
				file.mkdir();
			}
			file = new File(APP_DIR_CONF_PREF_DEF);
			if (!file.exists()) {
				file.mkdir();
			}
			file = new File(APP_DIR_CONF_CRASH);
			if (!file.exists()) {
				file.mkdir();
			}
			file = new File(APP_DIR_CONF_APP);
			if (!file.exists()) {
				file.mkdir();
			}

			createNoMedia();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 遍历程序所有目录，创建.nomedia文件
	 */
	public static void createNoMedia() {
		File file = new File(APP_ROOT);
		createNoMedia(file);
	}

	/**
	 * 遍历file下所有目录，创建.nomedia文件
	 * @param file
	 */
	public static void createNoMedia(File file) {
		File[] files = file.listFiles();
		for (File f:files) {
			if (f.isDirectory()) {
				createNoMedia(f);
			}
		}
		File nomediaFile = new File(file.getPath() + "/.nomedia");
		if (!nomediaFile.exists()) {
			try {
				new File(file.getPath(), ".nomedia").createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static String getGdbVideoDir(Context context) {
		return StorageUtil.getOutterStoragePath(context) + "/video";
	}
}
