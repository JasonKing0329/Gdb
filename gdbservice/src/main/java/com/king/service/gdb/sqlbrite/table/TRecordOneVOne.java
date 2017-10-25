package com.king.service.gdb.sqlbrite.table;

import android.database.Cursor;

import com.king.service.gdb.bean.RecordOneVOne;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/8/29 16:38
 */
public class TRecordOneVOne {
    // 表名
    public static final String TABLE_NAME = "record_1v1";

    // 表字段

    public static final Function<Cursor, RecordOneVOne> MAPPER_TABLE = new Function<Cursor, RecordOneVOne>() {

        @Override
        public RecordOneVOne apply(@NonNull Cursor cursor) throws Exception {
            return parseRecords(cursor);
        }
    };

    public static RecordOneVOne parseRecords(Cursor cursor) {
        RecordOneVOne record = new RecordOneVOne();
        record.setId(cursor.getInt(1));
        record.setSequence(cursor.getInt(2));
        record.setSceneName(cursor.getString(3));
        record.setDirectory(cursor.getString(4));
        record.setName(cursor.getString(7));
        record.setHDLevel(cursor.getInt(8));
        record.setScore(cursor.getInt(9));
        record.setScoreBasic(cursor.getInt(10));
        record.setScoreExtra(cursor.getInt(11));
        record.setScoreFeel(cursor.getInt(12));
        record.setScoreStar1(cursor.getInt(13));
        record.setScoreStar2(cursor.getInt(14));
        record.setScoreStar(cursor.getInt(15));
        record.setScoreStarC1(cursor.getInt(16));
        record.setScoreStarC2(cursor.getInt(17));
        record.setScoreStarC(cursor.getInt(18));
        record.setScoreRhythm(cursor.getInt(19));
        record.setScoreForePlay(cursor.getInt(20));
        record.setScoreBJob(cursor.getInt(21));
        record.setScoreFkType1(cursor.getInt(22));
        record.setScoreFkType2(cursor.getInt(23));
        record.setScoreFkType3(cursor.getInt(24));
        record.setScoreFkType4(cursor.getInt(25));
        record.setScoreFkType5(cursor.getInt(26));
        record.setScoreFkType6(cursor.getInt(27));
        record.setScoreFk(cursor.getInt(28));
        record.setScoreCum(cursor.getInt(29));
        record.setScoreScene(cursor.getInt(30));
        record.setScoreStory(cursor.getInt(31));
        record.setScoreNoCond(cursor.getInt(32));
        record.setScoreCShow(cursor.getInt(33));
        record.setScoreRim(cursor.getInt(34));
        record.setScoreSpeicial(cursor.getInt(35));
        record.setStar1Id(cursor.getInt(36));
        record.setStar2Id(cursor.getInt(37));
        record.setLastModifyTime(cursor.getLong(38));
        record.setSpecialDesc(cursor.getString(39));
        record.setDeprecated(Integer.parseInt(cursor.getString(40)));
        return record;
    }

}
