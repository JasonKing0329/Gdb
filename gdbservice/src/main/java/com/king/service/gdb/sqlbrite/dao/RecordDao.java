package com.king.service.gdb.sqlbrite.dao;

import android.database.Cursor;

import com.king.service.gdb.RecordCursor;
import com.king.service.gdb.bean.Record;
import com.king.service.gdb.bean.RecordOneVOne;
import com.king.service.gdb.bean.RecordThree;
import com.king.service.gdb.sqlbrite.table.TRecord3W;
import com.king.service.gdb.sqlbrite.table.TRecordOneVOne;
import com.squareup.sqlbrite2.BriteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    public List<Record> getAllRecords() {
        List<Record> list = new ArrayList<>();
        list.addAll(queryAllOneVOneRecords());
        list.addAll(queryAll3WRecords());
        return list;
    }

    /**
     * 获取cursor定义的最近所有种类记录
     * @param cursor
     * @return
     */
    public List<Record> getLatestRecords(RecordCursor cursor) {
        List<Record> list = new ArrayList<>();

        // 加载两张表的前number条数据，进行排序筛选出最终的number条数据
        List<RecordOneVOne> oList = queryLatestOneVOneRecords(cursor.from1v1, cursor.number);
        List<RecordThree> tList = queryLatest3WRecords(cursor.from3w, cursor.number);
        for (RecordOneVOne record:oList) {
            list.add(record);
        }
        for (RecordThree record:tList) {
            list.add(record);
        }
        Collections.sort(list, new Comparator<Record>() {
            @Override
            public int compare(Record o1, Record o2) {
                long num = o2.getLastModifyTime() - o1.getLastModifyTime();
                if (num > 0) {
                    return 1;
                }
                else if (num < 0) {
                    return -1;
                }
                return 0;
            }
        });

        // 更新cursor的位置
        List<Record> finalList = new ArrayList<>();
        int count1v1 = 0, count3w = 0;
        for (int i = 0; i < cursor.number && i < list.size(); i ++) {
            Record record = list.get(i);
            if (record instanceof RecordOneVOne) {
                count1v1 ++;
            }
            else if (record instanceof RecordThree) {
                count3w ++;
            }
            finalList.add(record);
        }
        cursor.from1v1 += count1v1;
        cursor.from3w += count3w;
        return finalList;
    }

    public List<RecordOneVOne> queryAllOneVOneRecords() {
        return queryLatestOneVOneRecords(-1, -1);
    }

    public List<RecordOneVOne> queryOneVOneRecords(int number) {
        return queryLatestOneVOneRecords(-1, number);
    }

    public List<RecordOneVOne> queryLatestOneVOneRecords(int from, int number) {
        List<RecordOneVOne> list = new ArrayList<>();
        // 由于parseRecords里是从1开始的，因此用_fake占0位
        String sql = "SELECT '1' AS _fake, * FROM " + TRecordOneVOne.TABLE_NAME + " ORDER BY lastModifyDate DESC";
        String[] args = new String[]{};
        if (from == -1 && number != -1) {
            sql = sql.concat(" LIMIT ?");
            args = new String[]{String.valueOf(number)};
        }
        else if (from != -1 && number != -1) {
            sql = sql.concat(" LIMIT ?,?");
            args = new String[]{String.valueOf(from), String.valueOf(number)};
        }
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
        return list;
    }

    public List<RecordOneVOne> queryAll3WRecords() {
        return queryLatestOneVOneRecords(-1, -1);
    }

    public List<RecordOneVOne> query3WRecords(int number) {
        return queryLatestOneVOneRecords(-1, number);
    }

    public List<RecordThree> queryLatest3WRecords(int from, int number) {
        List<RecordThree> list = new ArrayList<>();
        // 由于parseRecords里是从1开始的，因此用_fake占0位
        String sql = "SELECT '1' AS _fake, * FROM " + TRecord3W.TABLE_NAME + " ORDER BY lastModifyDate DESC";
        String[] args = new String[]{};
        if (from == -1 && number != -1) {
            sql = sql.concat(" LIMIT ?");
            args = new String[]{String.valueOf(number)};
        }
        else if (from != -1 && number != -1) {
            sql = sql.concat(" LIMIT ?,?");
            args = new String[]{String.valueOf(from), String.valueOf(number)};
        }
        Cursor cursor = null;
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
