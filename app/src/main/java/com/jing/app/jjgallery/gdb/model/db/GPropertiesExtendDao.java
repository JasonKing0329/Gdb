package com.jing.app.jjgallery.gdb.model.db;

import com.jing.app.jjgallery.gdb.GdbApplication;
import com.king.app.gdb.data.entity.GProperties;
import com.king.app.gdb.data.entity.GPropertiesDao;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.entity.StarDao;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/2/9 15:50
 */
public class GPropertiesExtendDao {

    public String getVersion() {
        GPropertiesDao dao = GdbApplication.getInstance().getDaoSession().getGPropertiesDao();
        return dao.queryBuilder()
                .where(GPropertiesDao.Properties.Key.eq("version"))
                .build().unique().getValue();
    }
}
