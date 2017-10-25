package com.king.service.gdb.sqlbrite.dao;

import android.database.Cursor;

import com.king.service.gdb.bean.Star;
import com.king.service.gdb.sqlbrite.table.TStar;
import com.squareup.sqlbrite2.BriteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/10/25 13:25
 */
public class StarDao {
    private final BriteDatabase gDataDb;

    public StarDao(BriteDatabase gDataDb) {
        this.gDataDb = gDataDb;
    }

    public List<Star> getRandomStars(int number) {
        List<Star> list = new ArrayList<>();
        String sql = "SELECT * FROM " + TStar.TABLE_NAME + " ORDER BY RANDOM() limit ?";
        String[] args = new String[]{String.valueOf(number)};
        Cursor cursor = null;
        try {
            cursor = gDataDb.query(sql, args);
            while (cursor.moveToNext()) {
                list.add(TStar.parseStar(cursor));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    public Star queryStarById(int id) {
        String sql = "SELECT * FROM " + TStar.TABLE_NAME + " WHERE id=?";
        String[] args = new String[]{String.valueOf(id)};
        Cursor cursor = null;
        try {
            cursor = gDataDb.query(sql, args);
            while (cursor.moveToNext()) {
                return TStar.parseStar(cursor);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }
}
