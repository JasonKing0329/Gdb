package com.king.service.gdb.sqlbrite.table;

import android.database.Cursor;

import com.king.service.gdb.bean.RecordThree;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/8/29 16:38
 */
public class TRecord3W {
    // 表名
    public static final String TABLE_NAME = "record_3w";

    // 表字段

    public static final Function<Cursor, RecordThree> MAPPER_TABLE = new Function<Cursor, RecordThree>() {

        @Override
        public RecordThree apply(@NonNull Cursor cursor) throws Exception {
            return parse3WRecords(cursor);
        }
    };

    public static RecordThree parse3WRecords(Cursor cursor) {
        RecordThree record = new RecordThree();
        record.setId(cursor.getInt(1));
        record.setSequence(cursor.getInt(2));
        record.setSceneName(cursor.getString(3));
        record.setDirectory(cursor.getString(4));
        record.setStarTopName(cursor.getString(5));
        record.setStarBottomName(cursor.getString(6));
        record.setStarMixName(cursor.getString(7));
        record.setName(cursor.getString(8));
        record.setHDLevel(cursor.getInt(9));
        record.setScore(cursor.getInt(10));
        record.setScoreFeel(cursor.getInt(11));
        record.setScoreTop(cursor.getString(12));
        record.setScoreBottom(cursor.getString(13));
        record.setScoreMix(cursor.getString(14));
        record.setScoreStar(cursor.getInt(15));
        record.setScoreTopC(cursor.getString(16));
        record.setScoreBottomC(cursor.getString(17));
        record.setScoreMixC(cursor.getString(18));
        record.setScoreStarC(cursor.getInt(19));
        record.setScoreRhythm(cursor.getInt(20));
        record.setScoreForePlay(cursor.getInt(21));
        record.setScoreBJob(cursor.getInt(22));
        record.setScoreFkType1(cursor.getInt(23));
        record.setScoreFkType2(cursor.getInt(24));
        record.setScoreFkType3(cursor.getInt(25));
        record.setScoreFkType4(cursor.getInt(26));
        record.setScoreFkType5(cursor.getInt(27));
        record.setScoreFkType6(cursor.getInt(28));
        record.setScoreFkType7(cursor.getInt(29));
        record.setScoreFkType8(cursor.getInt(30));
        record.setScoreFk(cursor.getInt(31));
        record.setScoreCum(cursor.getInt(32));
        record.setScoreScene(cursor.getInt(33));
        record.setScoreStory(cursor.getInt(34));
        record.setScoreNoCond(cursor.getInt(35));
        record.setScoreCShow(cursor.getInt(36));
        record.setScoreRim(cursor.getInt(37));
        record.setScoreSpeicial(cursor.getInt(38));
        record.setStarTopId(cursor.getString(39));
        record.setStarBottomId(cursor.getString(40));
        record.setStarMixId(cursor.getString(41));
        record.setLastModifyTime(cursor.getLong(42));
        record.setSpecialDesc(cursor.getString(43));
        record.setDeprecated(cursor.getInt(44));
        return record;
    }

}
