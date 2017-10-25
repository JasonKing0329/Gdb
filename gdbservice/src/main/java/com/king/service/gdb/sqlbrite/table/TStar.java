package com.king.service.gdb.sqlbrite.table;

import android.database.Cursor;

import com.king.service.gdb.bean.Star;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/8/29 16:38
 */
public class TStar {
    // 表名
    public static final String TABLE_NAME = "star";

    // 表字段
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_RECORDS = "records";
    public static final String COLUMN_BETOP = "betop";
    public static final String COLUMN_BEBOTTOM = "bebottom";
    public static final String COLUMN_AVERAGE = "average";
    public static final String COLUMN_MAX = "max";
    public static final String COLUMN_MIN = "min";
    public static final String COLUMN_CAVERAGE = "caverage";
    public static final String COLUMN_CMAX = "cmax";
    public static final String COLUMN_CMIN = "cmin";

    public static final Function<Cursor, Star> MAPPER_TABLE = new Function<Cursor, Star>() {

        @Override
        public Star apply(@NonNull Cursor cursor) throws Exception {
            return parseStar(cursor);
        }
    };

    public static Star parseStar(Cursor cursor) {
        Star bean = new Star();
        bean.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
        bean.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
        bean.setRecordNumber(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECORDS)));
        bean.setBeTop(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BETOP)));
        bean.setBeBottom(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BEBOTTOM)));
        bean.setAverage(cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_AVERAGE)));
        bean.setMax(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MAX)));
        bean.setMin(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MIN)));
        bean.setcAverage(cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_CAVERAGE)));
        bean.setcMax(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CMAX)));
        bean.setcMin(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CMIN)));
        return bean;
    }

}
