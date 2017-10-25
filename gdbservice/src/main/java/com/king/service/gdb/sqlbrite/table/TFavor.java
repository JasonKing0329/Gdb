package com.king.service.gdb.sqlbrite.table;

import android.database.Cursor;

import com.king.service.gdb.bean.FavorBean;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/8/29 16:38
 */
public class TFavor {
    // 表名
    public static final String TABLE_NAME = "favor";

    // 表字段
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_STAR_ID = "star_id";
    public static final String COLUMN_NAME = "star_name";
    public static final String COLUMN_FAVOR = "favor";

    public static final Function<Cursor, FavorBean> MAPPER_TABLE = new Function<Cursor, FavorBean>() {

        @Override
        public FavorBean apply(@NonNull Cursor cursor) throws Exception {
            return parseFavorBean(cursor);
        }
    };

    public static FavorBean parseFavorBean(Cursor cursor) {
        FavorBean bean = new FavorBean();
        bean.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
        bean.setStarId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STAR_ID)));
        bean.setStarName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
        bean.setFavor(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FAVOR)));
        return bean;
    }

}
