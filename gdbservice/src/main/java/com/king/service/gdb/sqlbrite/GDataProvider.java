package com.king.service.gdb.sqlbrite;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.king.service.gdb.GDBProvider;
import com.king.service.gdb.RecordCursor;
import com.king.service.gdb.bean.FavorBean;
import com.king.service.gdb.bean.Record;
import com.king.service.gdb.bean.RecordOneVOne;
import com.king.service.gdb.bean.RecordThree;
import com.king.service.gdb.bean.SceneBean;
import com.king.service.gdb.bean.Star;
import com.king.service.gdb.bean.StarCountBean;
import com.king.service.gdb.sqlbrite.dao.FavorDao;
import com.king.service.gdb.sqlbrite.dao.RecordDao;
import com.king.service.gdb.sqlbrite.dao.StarDao;
import com.squareup.sqlbrite2.BriteDatabase;
import com.squareup.sqlbrite2.SqlBrite;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.schedulers.Schedulers;

import static com.king.service.gdb.DBConstants.TABLE_CONF;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/10/25 10:22
 */
public class GDataProvider implements GDBProvider {

    private GDataSqlHelper gDataHelper;
    private GDataFavorSqlHelper favorHelper;
    private BriteDatabase gDataDb;
    private BriteDatabase favorDb;

    private RecordDao recordDao;
    private StarDao starDao;
    private FavorDao favorDao;

    public GDataProvider(Context context) {
        createGDataDb(context);
        createFavorDb(context);
        recordDao = new RecordDao(gDataDb);
        starDao = new StarDao(gDataDb);
        favorDao = new FavorDao(favorDb);
    }

    private void createGDataDb(Context context) {
        gDataHelper = new GDataSqlHelper(context);
        SqlBrite sqlBrite = new SqlBrite.Builder().logger(new SqlBrite.Logger() {
            @Override
            public void log(String message) {
                Log.e("GDataProvider", message);
            }
        }).build();
        gDataDb = sqlBrite.wrapDatabaseHelper(gDataHelper, Schedulers.io());
        gDataDb.setLoggingEnabled(true);
    }

    private void createFavorDb(Context context) {
        favorHelper = new GDataFavorSqlHelper(context);
        SqlBrite sqlBrite = new SqlBrite.Builder().logger(new SqlBrite.Logger() {
            @Override
            public void log(String message) {
                Log.e("GDataProvider", message);
            }
        }).build();
        favorDb = sqlBrite.wrapDatabaseHelper(favorHelper, Schedulers.io());
        favorDb.setLoggingEnabled(true);
    }

    @Override
    public void close() {
        if (favorDb != null) {
            favorDb.close();
        }
        if (favorHelper != null) {
            favorHelper.close();
        }
        if (gDataDb != null) {
            gDataDb.close();
        }
        if (gDataHelper != null) {
            gDataHelper.close();
        }
    }

    @Override
    public String getVersionName() {
        String sql = "SELECT value FROM " + TABLE_CONF + " WHERE key=?";
        String[] args = new String[]{"version"};
        String version = null;
        Cursor cursor = null;
        try {
            cursor = gDataDb.query(sql, args);
            while (cursor.moveToNext()) {
                version = cursor.getString(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return version;
    }

    @Override
    public List<RecordOneVOne> getOneVOneRecords() {
        return null;
    }

    @Override
    public List<Star> getStars(String starMode) {
        return starDao.getStars(starMode);
    }

    @Override
    public Star getStarRecords(int starId) {
        return null;
    }

    @Override
    public Star queryStarByName(String name) {
        return null;
    }

    @Override
    public Star queryStarById(int id) {
        return starDao.queryStarById(id);
    }

    @Override
    public List<Record> getAllRecords() {
        return null;
    }

    @Override
    public List<Record> getAll3WRecords() {
        return null;
    }

    @Override
    public List<Record> getLatestRecords(RecordCursor cursor) {
        return null;
    }

    @Override
    public void loadStarRecords(Star mStar) {

        // load records
        List<Record> list = new ArrayList<>();
        list.addAll(recordDao.loadStarRecords(mStar.getId()));

        // load stars
        for (Record mRecord:list){
            if (mRecord instanceof RecordOneVOne) {
                RecordOneVOne record = (RecordOneVOne) mRecord;
                record.setStar1(queryStarById(record.getStar1Id()));
                record.setStar2(queryStarById(record.getStar2Id()));
            }
            else if (mRecord instanceof RecordThree) {
                RecordThree record = (RecordThree) mRecord;
                String ids = record.getStarTopId();
                if (!TextUtils.isEmpty(ids)) {
                    String[] starIds = ids.split(",");
                    for (String startId:starIds) {
                        Star star = queryStarById(Integer.parseInt(startId));
                        if (record.getStarTopList() == null) {
                            record.setStarTopList(new ArrayList<Star>());
                        }
                        record.getStarTopList().add(star);
                    }
                }
                ids = record.getStarBottomId();
                if (!TextUtils.isEmpty(ids)) {
                    String[] starIds = ids.split(",");
                    for (String startId:starIds) {
                        Star star = queryStarById(Integer.parseInt(startId));
                        if (record.getStarBottomList() == null) {
                            record.setStarBottomList(new ArrayList<Star>());
                        }
                        record.getStarBottomList().add(star);
                    }
                }
                ids = record.getStarMixId();
                if (!TextUtils.isEmpty(ids)) {
                    String[] starIds = ids.split(",");
                    for (String startId:starIds) {
                        Star star = queryStarById(Integer.parseInt(startId));
                        if (record.getStarMixList() == null) {
                            record.setStarMixList(new ArrayList<Star>());
                        }
                        record.getStarMixList().add(star);
                    }
                }

            }
        }

        mStar.setRecordList(list);
        mStar.setRecordNumber(list.size());
    }

    @Override
    public StarCountBean queryStarCount() {
        return starDao.queryStarCount();
    }

    @Override
    public StarCountBean queryFavorStarCount() {
        return null;
    }

    @Override
    public List<Star> getRandomStars(int number) {
        return starDao.getRandomStars(number);
    }

    @Override
    public List<Record> getRandomRecords(int number) {
        return null;
    }

    @Override
    public List<FavorBean> getFavors() {
        return favorDao.getFavors();
    }

    @Override
    public List<FavorBean> getTopFavors(int number) {
        return null;
    }

    @Override
    public boolean isStarFavor(int starId) {
        return favorDao.isStarFavor(starId);
    }

    @Override
    public void saveFavor(FavorBean bean) {
        favorDao.saveFavor(bean);
    }

    @Override
    public void saveFavorList(List<FavorBean> favorList) {

    }

    @Override
    public List<Record> getRecords(String sortColumn, boolean desc, boolean includeDeprecated, int from, int number, String nameLike, String scene) {
        return null;
    }

    @Override
    public List<SceneBean> getSceneList() {
        return null;
    }

    @Override
    public List<Record> getRecordsByScene(String scene) {
        return null;
    }

    @Override
    public Record getRecordByName(String name) {
        return null;
    }

    @Override
    public boolean isFavorTableExist() {
        return false;
    }

    @Override
    public void createFavorTable() {

    }
}
