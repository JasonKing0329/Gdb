package com.king.service.gdb.sqlbrite.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.king.service.gdb.bean.FavorBean;
import com.king.service.gdb.sqlbrite.table.TFavor;
import com.squareup.sqlbrite2.BriteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/10/25 13:34
 */
public class FavorDao {
    private final BriteDatabase gDataDb;

    public FavorDao(BriteDatabase gDataDb) {
        this.gDataDb = gDataDb;
    }

    public List<FavorBean> getRandomFavors(int number) {
        List<FavorBean> list = new ArrayList<>();
        String sql = "SELECT * FROM " + TFavor.TABLE_NAME + " ORDER BY RANDOM() limit " + number;
        String[] args = new String[]{};
        Cursor cursor = null;
        try {
            cursor = gDataDb.query(sql, args);
            while (cursor.moveToNext()) {
                list.add(TFavor.parseFavorBean(cursor));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    public List<FavorBean> getFavors() {
        return getTopFavors(0);
    }

    public List<FavorBean> getTopFavors(int number) {
        List<FavorBean> list = new ArrayList<>();
        String sql = "SELECT * FROM " + TFavor.TABLE_NAME + " ORDER BY favor DESC";
        if (number > 0) {
            sql = sql.concat(" LIMIT ").concat(String.valueOf(number));
        }
        String[] args = new String[]{};
        Cursor cursor = null;
        try {
            cursor = gDataDb.query(sql, args);
            while (cursor.moveToNext()) {
                list.add(TFavor.parseFavorBean(cursor));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    public void saveFavor(FavorBean bean) {
        ContentValues values = new ContentValues();
        values.put(TFavor.COLUMN_STAR_ID, bean.getStarId());
        values.put(TFavor.COLUMN_NAME, bean.getStarName());
        values.put(TFavor.COLUMN_FAVOR, bean.getFavor());
        boolean isExist = isStarFavor(bean.getId());
        if (isExist) {
            gDataDb.update(TFavor.TABLE_NAME, values, "star_id=?", new String[]{String.valueOf(bean.getStarId())});
        }
        else {
            gDataDb.insert(TFavor.TABLE_NAME, values);
        }
    }

    public boolean isStarFavor(int starId) {
        String sql = "SELECT favor FROM " + TFavor.TABLE_NAME + " WHERE star_id=?";
        String[] args = new String[]{String.valueOf(starId)};
        boolean isExist = false;
        Cursor cursor = null;
        try {
            cursor = gDataDb.query(sql, args);
            while (cursor.moveToNext()) {
                if (cursor.getInt(0) > 0) {
                    isExist = true;
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return isExist;
    }
}
