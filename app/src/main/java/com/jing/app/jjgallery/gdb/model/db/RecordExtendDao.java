package com.jing.app.jjgallery.gdb.model.db;

import android.database.Cursor;
import android.text.TextUtils;

import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.model.conf.PreferenceValue;
import com.king.app.gdb.data.RecordCursor;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.RecordDao;

import org.greenrobot.greendao.query.QueryBuilder;

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

    public List<Record> getRecords(int sortMode, boolean desc, boolean includeDeprecated, RecordCursor cursor, String like, String scene) {
        RecordDao dao = GdbApplication.getInstance().getDaoSession().getRecordDao();
        QueryBuilder<Record> builder = dao.queryBuilder();
        if (!TextUtils.isEmpty(like)) {
            builder.where(RecordDao.Properties.Name.like("%" + like + "%"));
        }
        if (!TextUtils.isEmpty(scene)) {
            builder.where(RecordDao.Properties.Name.eq(scene));
        }
        if (!includeDeprecated) {
            builder.where(RecordDao.Properties.Deprecated.eq(0));
        }
        sortByColumn(builder, sortMode, desc);
        builder.offset(cursor.offset);
        builder.limit(cursor.number);
        return builder.build().list();
    }

    private void sortByColumn(QueryBuilder<Record> builder, int sortValue, boolean desc) {

        if (sortValue == PreferenceValue.GDB_SR_ORDERBY_DATE) {
            builder.orderRaw(RecordDao.Properties.LastModifyTime.columnName + (desc ? " DESC":" ASC"));
        }
        else if (sortValue == PreferenceValue.GDB_SR_ORDERBY_SCORE) {
            builder.orderRaw(RecordDao.Properties.Score.columnName + (desc ? " DESC":" ASC"));
        }
        else if (sortValue == PreferenceValue.GDB_SR_ORDERBY_PASSION) {
            builder.orderRaw(RecordDao.Properties.ScorePassion.columnName + (desc ? " DESC":" ASC"));
        }
        else if (sortValue == PreferenceValue.GDB_SR_ORDERBY_CUM) {
            builder.orderRaw(RecordDao.Properties.ScoreCum.columnName + (desc ? " DESC":" ASC"));
        }
        else if (sortValue == PreferenceValue.GDB_SR_ORDERBY_STAR) {
            builder.orderRaw(RecordDao.Properties.ScoreStar.columnName + (desc ? " DESC":" ASC"));
        }
        else if (sortValue == PreferenceValue.GDB_SR_ORDERBY_SCOREFEEL) {
            builder.orderRaw(RecordDao.Properties.ScoreFeel.columnName + (desc ? " DESC":" ASC"));
        }
        else if (sortValue == PreferenceValue.GDB_SR_ORDERBY_SPECIAL) {
            builder.orderRaw(RecordDao.Properties.ScoreSpecial.columnName + (desc ? " DESC":" ASC"));
        }
        else {// sort by name
            builder.orderRaw(RecordDao.Properties.Name.columnName + (desc ? " DESC":" ASC"));
        }
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
