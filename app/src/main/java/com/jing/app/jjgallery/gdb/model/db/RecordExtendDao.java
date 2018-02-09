package com.jing.app.jjgallery.gdb.model.db;

import android.database.Cursor;

import com.jing.app.jjgallery.gdb.GdbApplication;
import com.king.app.gdb.data.RecordCursor;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.RecordDao;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/2/9 15:50
 */
public class RecordExtendDao {

    public List<Record> getLatestRecords(RecordCursor recordCursor) {
        RecordDao dao = GdbApplication.getInstance().getDaoSession().getRecordDao();
        return dao.queryBuilder()
                .orderDesc(RecordDao.Properties.LastModifyTime)
                .offset(recordCursor.offset)
                .limit(recordCursor.number)
                .build().list();
    }

    public List<Record> getRecords(String sortColumn, boolean desc, boolean includeDeprecated, RecordCursor cursor, String like, String scene) {
        return null;
    }

    public List<SceneBean> getSceneList() {
        List<SceneBean> list = new ArrayList<>();
        String sql = "SELECT scene, COUNT(scene) AS count, AVG(score) AS average, MAX(score) AS max FROM "
                + RecordDao.TABLENAME + " GROUP BY scene ORDER BY scene";
        Cursor cursor = GdbApplication.getInstance().getDaoSession().getDatabase()
                .rawQuery(sql, new String[]{});
        while (cursor.moveToNext()) {
            list.add(parseSceneBean(cursor));
        }
        return list;
    }

    private SceneBean parseSceneBean(Cursor cursor) {
        SceneBean bean = new SceneBean();
        bean.setScene(cursor.getString(0));
        bean.setNumber(cursor.getInt(1));
        bean.setAverage(cursor.getDouble(2));
        bean.setMax(cursor.getInt(3));
        return bean;
    }
}
