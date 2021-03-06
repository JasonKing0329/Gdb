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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<Record> list = recordDao.getAllRecords();

        // 经测试1000条record，缓存star可以由2s多缩短到500ms以内
        Map<Integer, Star> starMap = new HashMap<>();
        for (Record mRecord:list){
            loadStarForReocrd(mRecord, starMap);
        }

        return list;
    }

    @Override
    public List<Record> getAll3WRecords() {
        return null;
    }

    @Override
    public List<Record> getLatestRecords(RecordCursor cursor) {

        List<Record> list = recordDao.getLatestRecords(cursor);

        // 本方法中list数据较少，不用采取缓存star
        for (Record mRecord:list){
            loadStarForReocrd(mRecord);
        }
        return list;
    }

    @Override
    public void loadStarRecords(Star mStar) {

        // load records
        List<Record> list = new ArrayList<>();
        list.addAll(recordDao.loadStarRecords(mStar.getId()));

        // load stars
        for (Record mRecord:list){
            loadStarForReocrd(mRecord);
        }

        mStar.setRecordList(list);
        mStar.setRecordNumber(list.size());
    }

    /**
     * 无缓存，直接查询star
     * @param mRecord
     */
    private void loadStarForReocrd(Record mRecord) {
        loadStarForReocrd(mRecord, null);
    }

    /**
     *
     * @param mRecord
     * @param cacheMap 缓存star，节省大量记录重复查询star的时间
     */
    private void loadStarForReocrd(Record mRecord, Map<Integer, Star> cacheMap) {
        if (mRecord instanceof RecordOneVOne) {
            RecordOneVOne record = (RecordOneVOne) mRecord;
            record.setStar1(getOrQueryStar(record.getStar1Id(), cacheMap));
            record.setStar1(getOrQueryStar(record.getStar1Id(), cacheMap));
            record.setStar2(getOrQueryStar(record.getStar2Id(), cacheMap));
        }
        else if (mRecord instanceof RecordThree) {
            RecordThree record = (RecordThree) mRecord;
            String ids = record.getStarTopId();
            if (!TextUtils.isEmpty(ids)) {
                String[] starIds = ids.split(",");
                for (String startId:starIds) {
                    Star star = getOrQueryStar(Integer.parseInt(startId), cacheMap);
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
                    Star star = getOrQueryStar(Integer.parseInt(startId), cacheMap);
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
                    Star star = getOrQueryStar(Integer.parseInt(startId), cacheMap);
                    if (record.getStarMixList() == null) {
                        record.setStarMixList(new ArrayList<Star>());
                    }
                    record.getStarMixList().add(star);
                }
            }
        }
    }

    private Star getOrQueryStar(int starId, Map<Integer, Star> cacheMap) {

        if (cacheMap != null && cacheMap.get(starId) != null) {
            return cacheMap.get(starId);
        }

        Star star = queryStarById(starId);
        if (cacheMap != null) {
            cacheMap.put(star.getId(), star);
        }
        return star;
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
        return favorDao.getTopFavors(number);
    }

    @Override
    public List<FavorBean> getRandomFavors(int number) {
        return favorDao.getRandomFavors(number);
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
    public List<Record> getRecords(String sortColumn, boolean desc, boolean includeDeprecated, RecordCursor cursor, String nameLike, String scene) {
        List<Record> list = recordDao.getRecords(sortColumn, desc, includeDeprecated, cursor, nameLike, scene);
        // load stars
        for (Record mRecord:list){
            loadStarForReocrd(mRecord);
        }
        return list;
    }

    @Override
    public List<SceneBean> getSceneList() {
        return recordDao.getSceneList();
    }

    @Override
    public List<Record> getRecordsByScene(String scene) {
        return null;
    }

    @Override
    public Record getRecordByName(String name) {
        Record record = recordDao.getRecordByName(name);
        // load star
        loadStarForReocrd(record);
        return record;
    }

    @Override
    public boolean isFavorTableExist() {
        return false;
    }

    @Override
    public void createFavorTable() {

    }
}
