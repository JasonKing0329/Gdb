package com.king.service.gdb.sqlbrite.dao;

import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.king.service.gdb.RecordCursor;
import com.king.service.gdb.bean.Record;
import com.king.service.gdb.bean.RecordOneVOne;
import com.king.service.gdb.bean.RecordSingleScene;
import com.king.service.gdb.bean.RecordThree;
import com.king.service.gdb.bean.SceneBean;
import com.king.service.gdb.sqlbrite.table.ISortRecord;
import com.king.service.gdb.sqlbrite.table.TRecord3W;
import com.king.service.gdb.sqlbrite.table.TRecordOneVOne;
import com.king.service.gdb.sqlbrite.table.VScene;
import com.squareup.sqlbrite2.BriteDatabase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.R.id.list;
import static android.R.id.switch_widget;
import static com.king.service.gdb.DBConstants.TABLE_RECORD_1V1;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/10/25 13:34
 */
public class RecordDao {

    private BriteDatabase gDataDb;

    public RecordDao(BriteDatabase gDataDb) {
        this.gDataDb = gDataDb;
        initViews();
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

    public List<Record> getRecords(String sortColumn, boolean desc, boolean includeDeprecated, RecordCursor cursor, String nameLike, String scene) {
        List<Record> list = new ArrayList<>();
        // 加载两张表的前number条数据，进行排序筛选出最终的number条数据
        List<RecordOneVOne> oList = queryOneVOneRecords(sortColumn, desc, includeDeprecated, cursor.from1v1, cursor.number, nameLike, scene);
        List<RecordThree> tList = query3WRecords(sortColumn, desc, includeDeprecated, cursor.from3w, cursor.number, nameLike, scene);
        for (RecordOneVOne record:oList) {
            list.add(record);
        }
        for (RecordThree record:tList) {
            list.add(record);
        }
        Collections.sort(list, new RecordComparator(sortColumn, desc));

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

    public List<RecordOneVOne> queryOneVOneRecords(String sortColumn, boolean desc, boolean includeDeprecated, int from, int number, String nameLike, String scene) {
        List<RecordOneVOne> list = new ArrayList<>();
        // 由于parseRecords里是从1开始的，因此用_fake占0位
        StringBuffer buffer = new StringBuffer("SELECT '1' AS _fake, * FROM ");
        buffer.append(TRecordOneVOne.TABLE_NAME).append(" WHERE 1=1");
        if (!TextUtils.isEmpty(nameLike)) {
            buffer.append(" AND name LIKE '%").append(nameLike).append("%'");
        }
        if (!includeDeprecated) {
            buffer.append(" AND deprecated=0");
        }
        if (!TextUtils.isEmpty(scene)) {
            buffer.append(" AND scene='").append(scene).append("'");
        }
        if (!TextUtils.isEmpty(sortColumn)) {
            buffer.append(" ORDER BY ").append(sortColumn);
        }
        buffer.append(" ").append(desc ? "DESC":"ASC");
        if (from != -1 && number != -1) {
            buffer.append(" LIMIT ").append(from).append(",").append(number);
        }
        String sql = buffer.toString();
        Log.e("RecordDao", "queryOneVOneRecords " + sql);

        String[] args = new String[]{};
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

    public List<RecordThree> query3WRecords(String sortColumn, boolean desc, boolean includeDeprecated, int from, int number, String nameLike, String scene) {
        List<RecordThree> list = new ArrayList<>();
        // 由于parseRecords里是从1开始的，因此用_fake占0位
        StringBuffer buffer = new StringBuffer("SELECT '1' AS _fake, * FROM ");
        buffer.append(TRecord3W.TABLE_NAME).append(" WHERE 1=1");
        if (!TextUtils.isEmpty(nameLike)) {
            buffer.append(" AND name LIKE '%").append(nameLike).append("%'");
        }
        if (!includeDeprecated) {
            buffer.append(" AND deprecated=0");
        }
        if (!TextUtils.isEmpty(scene)) {
            buffer.append(" AND scene='").append(scene).append("'");
        }
        if (!TextUtils.isEmpty(sortColumn)) {
            buffer.append(" ORDER BY ").append(sortColumn);
        }
        buffer.append(" ").append(desc ? "DESC":"ASC");
        if (from != -1 && number != -1) {
            buffer.append(" LIMIT ").append(from).append(",").append(number);
        }
        String sql = buffer.toString();
        Log.e("RecordDao", "query3WRecords " + sql);
        String[] args = new String[]{};
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

    public Record getRecordByName(String name) {
        Record record = getOneVOneRecordByName(name);
        if (record == null) {
            record = get3WRecordByName(name);
        }
        return record;
    }

    public Record getOneVOneRecordByName(String name) {
        Record record = null;
        // name里带单引号在sqlite中要作为特殊符号转化
        if (name.contains("'")) {
            name = name.replace("'", "''");
        }
        List<Record> list = new ArrayList<>();
        // 由于parseRecords里是从1开始的，因此用_fake占0位
        String sql = "SELECT '1' AS _fake, * FROM " + TRecordOneVOne.TABLE_NAME + " WHERE name=?";
        String[] args = new String[]{name};
        Cursor cursor = null;
        try {
            cursor = gDataDb.query(sql, args);
            while (cursor.moveToNext()) {
                record = TRecordOneVOne.parseRecords(cursor);
                list.add(record);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return record;
    }

    public Record get3WRecordByName(String name) {
        Record record = null;
        // name里带单引号在sqlite中要作为特殊符号转化
        if (name.contains("'")) {
            name = name.replace("'", "''");
        }
        // 由于parseRecords里是从1开始的，因此用_fake占0位
        String sql = "SELECT '1' AS _fake, * FROM " + TRecord3W.TABLE_NAME + " WHERE name=?";
        String[] args = new String[]{name};
        Cursor cursor = null;
        try {
            cursor = gDataDb.query(sql, args);
            if (cursor.moveToNext()) {
                record = TRecord3W.parse3WRecords(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return record;
    }

    public List<SceneBean> getSceneList() {
        List<SceneBean> list = new ArrayList<>();
        String sql = "SELECT scene, COUNT(scene) AS count, AVG(score) AS average, MAX(score) AS max FROM " + VScene.TABLE_NAME + " GROUP BY scene ORDER BY scene";
        String[] args = new String[]{};
        Cursor cursor = null;
        try {
            cursor = gDataDb.query(sql, args);
            parseSceneBean(cursor, list);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    private void parseSceneBean(Cursor cursor, List<SceneBean> list) throws SQLException {
        while (cursor.moveToNext()) {
            SceneBean bean = new SceneBean();
            bean.setScene(cursor.getString(0));
            bean.setNumber(cursor.getInt(1));
            bean.setAverage(cursor.getFloat(2));
            bean.setMax(cursor.getInt(3));
            list.add(bean);
        }
    }

    /**
     * 创建视图
     */
    public void initViews() {
        if (!isViewExist(VScene.TABLE_NAME)) {
            VScene.create(gDataDb);
        }
    }

    private boolean isViewExist(String tableName) {
        boolean isExist = false;
        String sql = "SELECT * FROM sqlite_master where type=? and name=?";
        String[] args = new String[]{"view", tableName};
        Cursor cursor = null;
        try {
            cursor = gDataDb.query(sql, args);
            if (cursor.moveToNext()) {
                isExist = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return isExist;
    }

    private class RecordComparator implements Comparator<Record> {

        private String sortColumn;
        private boolean desc;

        public RecordComparator(String sortColumn, boolean desc) {
            this.sortColumn = sortColumn;
            this.desc = desc;
        }

        @Override
        public int compare(Record o1, Record o2) {

            long result = 0;
            try {
                switch (sortColumn) {
                    case ISortRecord.COLUMN_BJOB:
                        result = ((RecordSingleScene)o2).getScoreBJob() - ((RecordSingleScene)o1).getScoreBJob();
                        break;
                    case ISortRecord.COLUMN_CSHOW:
                        result = ((RecordSingleScene)o2).getScoreCShow() - ((RecordSingleScene)o1).getScoreCShow();
                        break;
                    case ISortRecord.COLUMN_CUM:
                        result = ((RecordSingleScene)o2).getScoreCum() - ((RecordSingleScene)o1).getScoreCum();
                        break;
                    case ISortRecord.COLUMN_DATE:
                        result = o2.getLastModifyTime() - o1.getLastModifyTime();
                        break;
                    case ISortRecord.COLUMN_FK:
                        result = ((RecordSingleScene)o2).getScoreFk() - ((RecordSingleScene)o1).getScoreFk();
                        break;
                    case ISortRecord.COLUMN_FOREPLAY:
                        result = ((RecordSingleScene)o2).getScoreForePlay() - ((RecordSingleScene)o1).getScoreForePlay();
                        break;
                    case ISortRecord.COLUMN_NAME:
                        result = o2.getName().compareTo(o1.getName());
                        break;
                    case ISortRecord.COLUMN_RHYTHM:
                        result = ((RecordSingleScene)o2).getScoreRhythm() - ((RecordSingleScene)o1).getScoreRhythm();
                        break;
                    case ISortRecord.COLUMN_RIM:
                        result = ((RecordSingleScene)o2).getScoreRim() - ((RecordSingleScene)o1).getScoreRim();
                        break;
                    case ISortRecord.COLUMN_SCENE:
                        result = ((RecordSingleScene)o2).getScoreScene() - ((RecordSingleScene)o1).getScoreScene();
                        break;
                    case ISortRecord.COLUMN_SCORE:
                        result = o2.getScore() - o1.getScore();
                        break;
                    case ISortRecord.COLUMN_SPECIAL:
                        result = ((RecordSingleScene)o2).getScoreSpeicial() - ((RecordSingleScene)o1).getScoreSpeicial();
                        break;
                    case ISortRecord.COLUMN_STAR:
                        result = ((RecordSingleScene)o2).getScoreStar() - ((RecordSingleScene)o1).getScoreStar();
                        break;
                    case ISortRecord.COLUMN_STARC:
                        result = ((RecordSingleScene)o2).getScoreStarC() - ((RecordSingleScene)o1).getScoreStarC();
                        break;
                    case ISortRecord.COLUMN_STORY:
                        result = o2.getScoreStory() - o1.getScoreStory();
                        break;
                }
            } catch (Exception e) {

            }

            if (desc) {
                if (result > 0) {
                    return 1;
                }
                else if (result < 0) {
                    return -1;
                }
            }
            else {
                if (result > 0) {
                    return -1;
                }
                else if (result < 0) {
                    return 1;
                }
            }
            return 0;
        }
    }
}
