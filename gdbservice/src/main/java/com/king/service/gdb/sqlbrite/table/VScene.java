package com.king.service.gdb.sqlbrite.table;

import com.squareup.sqlbrite2.BriteDatabase;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/8/29 16:38
 */
public class VScene {
    // 表名
    public static final String TABLE_NAME = "view_scene";

    // 表字段
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_SCENE = "scene";
    public static final String COLUMN_SCORE = "score";

    public static void create(BriteDatabase gDataDb) {
        String sql = "CREATE VIEW view_scene AS\n" +
                "SELECT id, scene, score FROM " + TRecordOneVOne.TABLE_NAME +
                " UNION\n" +
                " SELECT id, scene, score FROM " + TRecord3W.TABLE_NAME;
        gDataDb.execute(sql);
    }
}
