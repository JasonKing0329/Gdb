package com.jing.app.jjgallery.gdb.model;

import com.jing.app.jjgallery.gdb.model.conf.PreferenceValue;
import com.king.app.gdb.data.entity.Record;

import java.util.Comparator;

/**
 * Created by 景阳 on 2016/12/19.
 * the comparator of record sort
 */

public class RecordComparator implements Comparator<Record> {

    private static int sortMode;
    private static boolean desc;
    public RecordComparator(int sortMode, boolean desc) {
        this.sortMode = sortMode;
        this.desc = desc;
    }

    @Override
    public int compare(Record lhs, Record rhs) {
        return compareRecord(lhs, rhs);
    }

    public static void setSortMode(int sortMode) {
        RecordComparator.sortMode = sortMode;
    }

    public static void setDesc(boolean desc) {
        RecordComparator.desc = desc;
    }

    /**
     * make this static to support be called by other program
     * @param left
     * @param right
     * @return
     */
    public static int compareRecord(Record left, Record right) {
        int result = 0;
        switch (sortMode) {
            case PreferenceValue.GDB_SR_ORDERBY_NAME:// asc
                if (desc) {
                    result = right.getName().toLowerCase().compareTo(left.getName().toLowerCase());
                }
                else {
                    result = left.getName().toLowerCase().compareTo(right.getName().toLowerCase());
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_DATE:
                if (desc) {
                    long lr = right.getLastModifyTime() - left.getLastModifyTime();
                    // 直接把负数long型强制转化为int会有问题，所以还是要通过判断来返回真正的带符号的整型
                    if (lr > 0) {
                        return 1;
                    }
                    else if (lr < 0) {
                        return -1;
                    }
                    else {
                        return 0;
                    }
                }
                else {
                    result = (int) (left.getLastModifyTime() - right.getLastModifyTime());
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_SCORE:
                if (desc) {
                    result = right.getScore() - left.getScore();
                }
                else {
                    result = left.getScore() - right.getScore();
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_FK:
                if (desc) {
                    result = right.getScorePassion() - left.getScorePassion();
                }
                else {
                    result = left.getScorePassion() - right.getScorePassion();
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_CUM:
                if (desc) {
                    result = right.getScoreCum() - left.getScoreCum();
                }
                else {
                    result = left.getScoreCum() - right.getScoreCum();
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_BJOB:
                break;
            case PreferenceValue.GDB_SR_ORDERBY_STAR1:
                break;
            case PreferenceValue.GDB_SR_ORDERBY_STAR2:
                break;
            case PreferenceValue.GDB_SR_ORDERBY_STARCC1:
                break;
            case PreferenceValue.GDB_SR_ORDERBY_STARCC2:
                break;
            case PreferenceValue.GDB_SR_ORDERBY_BAREBACK:
                if (desc) {
                    result = right.getScoreBareback() - left.getScoreBareback();
                }
                else {
                    result = left.getScoreBareback() - right.getScoreBareback();
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_SCOREFEEL:
                if (desc) {
                    result = right.getScoreFeel() - left.getScoreFeel();
                }
                else {
                    result = left.getScoreFeel() - right.getScoreFeel();
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_STORY:
                break;
            case PreferenceValue.GDB_SR_ORDERBY_FOREPLAY:
                break;
            case PreferenceValue.GDB_SR_ORDERBY_RIM:
                break;
            case PreferenceValue.GDB_SR_ORDERBY_RHYTHM:
                break;
            case PreferenceValue.GDB_SR_ORDERBY_SCENE:
                break;
            case PreferenceValue.GDB_SR_ORDERBY_CSHOW:
                break;
            case PreferenceValue.GDB_SR_ORDERBY_SPECIAL:
                if (desc) {
                    result = right.getScoreSpecial() - left.getScoreSpecial();
                }
                else {
                    result = left.getScoreSpecial() - right.getScoreSpecial();
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_HD:
                if (desc) {
                    result = right.getHdLevel() - left.getHdLevel();
                }
                else {
                    result = left.getHdLevel() - right.getHdLevel();
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_FK1:
                break;
            case PreferenceValue.GDB_SR_ORDERBY_FK2:
                break;
            case PreferenceValue.GDB_SR_ORDERBY_FK3:
                break;
            case PreferenceValue.GDB_SR_ORDERBY_FK4:
                break;
            case PreferenceValue.GDB_SR_ORDERBY_FK5:
                break;
            case PreferenceValue.GDB_SR_ORDERBY_FK6:
                break;
            case PreferenceValue.GDB_SR_ORDERBY_SCORE_BASIC:
                break;
            case PreferenceValue.GDB_SR_ORDERBY_SCORE_EXTRA:
                break;
            case PreferenceValue.GDB_SR_ORDERBY_STAR:
                if (desc) {
                    result = right.getScoreStar() - left.getScoreStar();
                }
                else {
                    result = left.getScoreStar() - right.getScoreStar();
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_STARC:
                break;
        }
        return result;
    }

    public static String getSortColumn(int sortMode) {
        String column = null;
        switch (sortMode) {
            case PreferenceValue.GDB_SR_ORDERBY_NAME:// asc
                column = "name";
                break;
            case PreferenceValue.GDB_SR_ORDERBY_DATE:// asc
                column = "lastModifyDate";
                break;
            case PreferenceValue.GDB_SR_ORDERBY_SCORE:
                column = "score";
                break;
            case PreferenceValue.GDB_SR_ORDERBY_FK:
                column = "scoreFk";
                break;
            case PreferenceValue.GDB_SR_ORDERBY_CUM:
                column = "scoreCum";
                break;
            case PreferenceValue.GDB_SR_ORDERBY_BJOB:
                column = "scoreBJob";
                break;
            case PreferenceValue.GDB_SR_ORDERBY_STAR1:
                column = "scoreStar1";
                break;
            case PreferenceValue.GDB_SR_ORDERBY_STAR2:
                column = "scoreStar2";
                break;
            case PreferenceValue.GDB_SR_ORDERBY_STARCC1:
                column = "scoreStarC1";
                break;
            case PreferenceValue.GDB_SR_ORDERBY_STARCC2:
                column = "scoreStarC2";
                break;
            case PreferenceValue.GDB_SR_ORDERBY_BAREBACK:
                column = "scoreNoCond";
                break;
            case PreferenceValue.GDB_SR_ORDERBY_SCOREFEEL:
                column = "scoreFeel";
                break;
            case PreferenceValue.GDB_SR_ORDERBY_STORY:
                column = "scoreStory";
                break;
            case PreferenceValue.GDB_SR_ORDERBY_FOREPLAY:
                column = "scoreForePlay";
                break;
            case PreferenceValue.GDB_SR_ORDERBY_RIM:
                column = "scoreRim";
                break;
            case PreferenceValue.GDB_SR_ORDERBY_RHYTHM:
                column = "scoreRhythm";
                break;
            case PreferenceValue.GDB_SR_ORDERBY_SCENE:
                column = "scoreScene";
                break;
            case PreferenceValue.GDB_SR_ORDERBY_CSHOW:
                column = "scoreCShow";
                break;
            case PreferenceValue.GDB_SR_ORDERBY_SPECIAL:
                column = "scoreSpecial";
                break;
            case PreferenceValue.GDB_SR_ORDERBY_HD:
                column = "HDLevel";
                break;
            case PreferenceValue.GDB_SR_ORDERBY_FK1:
                column = "scoreFkType1";
                break;
            case PreferenceValue.GDB_SR_ORDERBY_FK2:
                column = "scoreFkType2";
                break;
            case PreferenceValue.GDB_SR_ORDERBY_FK3:
                column = "scoreFkType3";
                break;
            case PreferenceValue.GDB_SR_ORDERBY_FK4:
                column = "scoreFkType4";
                break;
            case PreferenceValue.GDB_SR_ORDERBY_FK5:
                column = "scoreFkType5";
                break;
            case PreferenceValue.GDB_SR_ORDERBY_FK6:
                column = "scoreFkType6";
                break;
            case PreferenceValue.GDB_SR_ORDERBY_SCORE_BASIC:
                column = "scoreBasic";
                break;
            case PreferenceValue.GDB_SR_ORDERBY_SCORE_EXTRA:
                column = "scoreExtra";
                break;
            case PreferenceValue.GDB_SR_ORDERBY_STAR:
                column = "scoreStar";
                break;
            case PreferenceValue.GDB_SR_ORDERBY_STARC:
                column = "scoreStarC";
                break;
        }
        return column;
    }
}
