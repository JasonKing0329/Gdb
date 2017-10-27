package com.jing.app.jjgallery.gdb.model;

import com.jing.app.jjgallery.gdb.model.conf.Configuration;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/7/21 10:51
 */
public class GdbImageProvider {

    /**
     *
     * @param name
     * @param indexPackage save the real index, can be null
     * @return
     */
    public static String getRecordRandomPath(String name, IndexPackage indexPackage) {
        return getImagePath(Configuration.GDB_IMG_RECORD, name, -1, indexPackage);
    }

    public static String getRecordPath(String name, int index) {
        return getImagePath(Configuration.GDB_IMG_RECORD, name, index, null);
    }

    public static List<String> getRecordPathList(String name) {
        return getImagePathList(Configuration.GDB_IMG_RECORD, name);
    }

    public static boolean hasRecordFolder(String name) {
        return hasFolder(Configuration.GDB_IMG_RECORD, name);
    }

    /**
     *
     * @param name
     * @param indexPackage save the real index, can be null
     * @return
     */
    public static String getStarRandomPath(String name, IndexPackage indexPackage) {
        return getImagePath(Configuration.GDB_IMG_STAR, name, -1, indexPackage);
    }

    public static String getStarPath(String name, int index) {
        return getImagePath(Configuration.GDB_IMG_STAR, name, index, null);
    }

    public static List<String> getStarPathList(String name) {
        return getImagePathList(Configuration.GDB_IMG_STAR, name);
    }

    public static boolean hasStarFolder(String name) {
        return hasFolder(Configuration.GDB_IMG_STAR, name);
    }

    private static boolean hasFolder(String parent, String name) {
        File file = new File(parent + "/" + name);
        return file.exists() && file.isDirectory();
    }

    /**
     *
     * @param parent
     * @param name
     * @param index if random, then -1
     * @param indexPackage save the true index, can be null
     * @return
     */
    private static String getImagePath(String parent, String name, int index, IndexPackage indexPackage) {
        if (SettingProperties.isGdbNoImageMode()) {
            return "";
        }

        String path;
        if (hasFolder(parent, name)) {
            File file = new File(parent + "/" + name);
            List<File> fileList = new ArrayList<>();
            getImageFiles(file, fileList);
            if (fileList.size() == 0) {
                path = parent + "/" + name;
                if (!name.endsWith(".png")) {
                    path = path.concat(".png");
                }
            }
            else {
                if (index == -1 || index >= fileList.size()) {
                    int pos = Math.abs(new Random().nextInt()) % fileList.size();
                    if (indexPackage != null) {
                        indexPackage.index = pos;
                    }
                    return fileList.get(pos).getPath();
                }
                else {
                    return fileList.get(index).getPath();
                }
            }
        }
        else {
            path = parent + "/" + name;
            if (!name.endsWith(".png")) {
                path = path.concat(".png");
            }
        }
        return path;
    }

    /**
     * v2.0.2 it supported multi-level directories since v2.0.1
     * @param file
     * @param list
     */
    private static void getImageFiles(File file, List<File> list) {
        if (file.isDirectory()) {
            File[] files = file.listFiles(fileFilter);
            for (File f:files) {
                getImageFiles(f, list);
            }
        }
        else {
            list.add(file);
        }
    }

    private static FileFilter fileFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            return !file.getName().endsWith(".nomedia");
        }
    };

    private static List<String> getImagePathList(String parent, String name) {
        List<String> list = new ArrayList<>();
        File file = new File(parent + "/" + name);
        List<File> fileList = new ArrayList<>();
        getImageFiles(file, fileList);
        if (fileList != null) {
            for (File f:fileList) {
                if (SettingProperties.isGdbNoImageMode()) {
                    list.add("");
                }
                else {
                    list.add(f.getPath());
                }
            }
            Collections.shuffle(list);
        }
        return list;
    }

    public static class IndexPackage {
        public int index;
    }
}
