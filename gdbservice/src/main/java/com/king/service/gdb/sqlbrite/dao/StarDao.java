package com.king.service.gdb.sqlbrite.dao;

import android.database.Cursor;

import com.king.service.gdb.bean.GDBProperites;
import com.king.service.gdb.bean.Star;
import com.king.service.gdb.bean.StarCountBean;
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

    public StarCountBean queryStarCount() {
        StarCountBean bean = new StarCountBean();
        String sql = "SELECT (SELECT COUNT(id) FROM star) AS _all\n" +
                "\t, (SELECT COUNT(id) FROM star WHERE betop>0 AND bebottom=0) AS top\n" +
                "\t, (SELECT COUNT(id) FROM star WHERE bebottom>0 AND betop=0) AS bottom\n" +
                "\t, (SELECT COUNT(id) FROM star WHERE betop>0 AND bebottom>0) AS half";
        String[] args = new String[]{};
        Cursor cursor = null;
        try {
            cursor = gDataDb.query(sql, args);
            while (cursor.moveToNext()) {
                bean.setAllNumber(cursor.getInt(0));
                bean.setTopNumber(cursor.getInt(1));
                bean.setBottomNumber(cursor.getInt(2));
                bean.setHalfNumber(cursor.getInt(3));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return bean;
    }

    public List<Star> getStars(String starMode) {
        String where = null;
        if (GDBProperites.STAR_MODE_TOP.equals(starMode)) {
            where = "betop>0 AND bebottom=0";
        }
        else if (GDBProperites.STAR_MODE_BOTTOM.equals(starMode)) {
            where = "bebottom>0 AND betop=0";
        }
        else if (GDBProperites.STAR_MODE_HALF.equals(starMode)) {
            where = "bebottom>0 AND betop>0";
        }
        List<Star> list = new ArrayList<>();
        String sql = "SELECT * FROM " + TStar.TABLE_NAME;
        if (where != null) {
            sql = sql + " WHERE " + where;
        }
        String[] args = new String[]{};
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
}
