package com.king.service.gdb.sqlbrite.dao;

import android.database.Cursor;

import com.king.service.gdb.bean.Record;
import com.king.service.gdb.bean.RecordOneVOne;
import com.king.service.gdb.bean.RecordThree;
import com.king.service.gdb.sqlbrite.table.TRecord3W;
import com.king.service.gdb.sqlbrite.table.TRecordOneVOne;
import com.squareup.sqlbrite2.BriteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/10/25 13:34
 */
public class RecordDao {

    private BriteDatabase gDataDb;

    public RecordDao(BriteDatabase gDataDb) {
        this.gDataDb = gDataDb;
    }

    public List<Record> loadStarRecords(int starId) {

        List<Record> list = new ArrayList<>();
        // load 1v1 records
        // 由于parseRecords里是从1开始的，因此用_fake占0位
        String sql = "SELECT '1' AS _fake, * FROM " + TRecordOneVOne.TABLE_NAME + " WHERE star1_id=? OR star2_id=?";
        String[] args = new String[]{String.valueOf(starId), String.valueOf(starId)};
        Cursor cursor = null;
        try {
            cursor = gDataDb.query(sql, args);
            while (cursor.moveToNext()) {
                RecordOneVOne record = TRecordOneVOne.parseRecords(cursor);
                list.add(record);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        // load 3w records
        // 由于parseRecords里是从1开始的，因此用_fake占0位
        sql = "SELECT '1' AS _fake, * FROM record_3w where \n" +
                "(starTop_id = ? OR starTop_id like ? OR starTop_id like ? OR starTop_id like ?)\n" +
                "OR (starBottom_id = ? OR starBottom_id like ? OR starBottom_id like ? OR starBottom_id like ?)\n" +
                "OR (starMix_id = ? OR starMix_id like ? OR starMix_id like ? OR starMix_id like ?)";
        args = new String[]{String.valueOf(starId), starId + ",%", "%," + starId, "%," + starId + ",%"
            , String.valueOf(starId), starId + ",%", "%," + starId, "%," + starId + ",%"
            , String.valueOf(starId), starId + ",%", "%," + starId, "%," + starId + ",%"};
        cursor = null;
        try {
            cursor = gDataDb.query(sql, args);
            while (cursor.moveToNext()) {
                RecordThree record = TRecord3W.parse3WRecords(cursor);
                list.add(record);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }
}
