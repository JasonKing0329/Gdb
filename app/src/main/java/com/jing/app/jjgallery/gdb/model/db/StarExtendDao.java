package com.jing.app.jjgallery.gdb.model.db;

import com.jing.app.jjgallery.gdb.GdbApplication;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.entity.StarDao;
import com.king.app.gdb.data.entity.StarRating;
import com.king.app.gdb.data.entity.StarRatingDao;

import org.greenrobot.greendao.query.Join;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/2/9 15:50
 */
public class StarExtendDao {

    public List<Star> getRandomFavors(int number) {
        StarDao dao = GdbApplication.getInstance().getDaoSession().getStarDao();
        return dao.queryBuilder()
                .where(StarDao.Properties.Favor.gt(0))
                .orderRaw("RANDOM()")
                .limit(number)
                .build().list();
    }

    public List<Star> getRandomRatingAbove(float complex, int number) {
        StarDao dao = GdbApplication.getInstance().getDaoSession().getStarDao();
        QueryBuilder<Star> builder = dao.queryBuilder();
        Join<Star, StarRating> join = builder.join(StarRating.class, StarRatingDao.Properties.StarId);
        join.where(StarRatingDao.Properties.Complex.ge(complex));
        return builder
                .orderRaw("RANDOM()")
                .limit(number)
                .build().list();
    }

    public List<Star> getRandomStars(int number) {
        StarDao dao = GdbApplication.getInstance().getDaoSession().getStarDao();
        return dao.queryBuilder()
                .orderRaw("RANDOM()")
                .limit(number)
                .build().list();
    }
}
