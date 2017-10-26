package com.king.service.gdb;

import com.king.service.gdb.bean.FavorBean;
import com.king.service.gdb.bean.Record;
import com.king.service.gdb.bean.RecordOneVOne;
import com.king.service.gdb.bean.SceneBean;
import com.king.service.gdb.bean.Star;
import com.king.service.gdb.bean.StarCountBean;

import java.util.List;

/**
 * Created by Administrator on 2016/7/30 0030.
 */
public interface GDBProvider {

    /**
     * 关闭所有数据库连接
     */
    void close();

    /**
     * 查询版本号
     * @return
     */
    String getVersionName();

    /**
     * 查询所有的1v1 record
     * @return
     */
    List<RecordOneVOne> getOneVOneRecords();

    /**
     * 查询所有的star
     * @return
     * @param starMode
     */
    List<Star> getStars(String starMode);

    /**
     * 查询star对应的所有record
     * @param starId
     * @return
     */
    Star getStarRecords(int starId);

    /**
     * 查询star
     * @param name
     * @return
     */
    Star queryStarByName(String name);

    /**
     * 查询star
     * @param id
     * @return
     */
    Star queryStarById(int id);

    /**
     * 查询所有的record
     * @return
     */
    List<Record> getAllRecords();

    /**
     * 查询所有的record_3w
     * @return
     */
    List<Record> getAll3WRecords();

    /**
     * 查询cursor指定的最近记录
     * @param cursor 定义record_1v1/record_3w的起始位置，以及总的record数量
     * @return
     */
    List<Record> getLatestRecords(RecordCursor cursor);

    /**
     * 加载star对应的record数量
     * @param star
     */
    void loadStarRecords(Star star);

    StarCountBean queryStarCount();

    StarCountBean queryFavorStarCount();

    /**
     * 随机查询star
     * @return
     */
    List<Star> getRandomStars(int number);

    /**
     * 随机查询record
     * @return
     */
    List<Record> getRandomRecords(int number);

    /**
     * 查询所有的favor数据
     * @return
     */
    List<FavorBean> getFavors();

    /**
     * 查询所有的favor数据
     * @return
     */
    List<FavorBean> getTopFavors(int number);

    boolean isStarFavor(int starId);

    /**
     * 更新favor数据
     * @return
     */
    void saveFavor(FavorBean bean);

    /**
     * 创建favor表数据
     * @param favorList
     */
    void saveFavorList(List<FavorBean> favorList);

    /**
     * name符合关键词nameLike，按sortColumn desc/asc 排序，从第from条记录开始取number条记录
     * @param sortColumn
     * @param desc
     * @param cursor
     * @param nameLike
     * @return
     */
    List<Record> getRecords(String sortColumn, boolean desc, boolean includeDeprecated, RecordCursor cursor, String nameLike, String scene);

    /**
     * scene list
     * @return
     */
    List<SceneBean> getSceneList();

    /**
     * get records by scene name
     * @param scene
     * @return
     */
    List<Record> getRecordsByScene(String scene);

    /**
     * get record by name
     * @param name
     * @return
     */
    Record getRecordByName(String name);

    boolean isFavorTableExist();

    void createFavorTable();
}
