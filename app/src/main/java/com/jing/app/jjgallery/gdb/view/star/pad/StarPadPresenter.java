package com.jing.app.jjgallery.gdb.view.star.pad;

import com.jing.app.jjgallery.gdb.BasePresenter;
import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.GdbConstants;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.entity.StarDao;
import com.king.app.gdb.data.param.DataConstants;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @desc
 * @auth 景阳
 * @time 2018/2/13 0013 11:21
 */

public class StarPadPresenter extends BasePresenter<StarPadView> {
    @Override
    public void onCreate() {

    }

    public void loadTitles() {
        Observable.create((ObservableOnSubscribe<List<Integer>>) e -> {
            List<Integer> countList = new ArrayList<>();
            countList.add((int) queryStarCount(DataConstants.STAR_MODE_ALL));
            countList.add((int) queryStarCount(DataConstants.STAR_MODE_TOP));
            countList.add((int) queryStarCount(DataConstants.STAR_MODE_BOTTOM));
            countList.add((int) queryStarCount(DataConstants.STAR_MODE_HALF));
            e.onNext(countList);
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<Integer> list) {
                        view.showTitles(list.get(0), list.get(1), list.get(2), list.get(3));
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.showToastLong("Load title error: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private long queryStarCount(String mode) {
        StarDao dao = GdbApplication.getInstance().getDaoSession().getStarDao();
        QueryBuilder<Star> builder = dao.queryBuilder();

        // don't show star without records
        builder.where(StarDao.Properties.Records.gt(0));

        if (DataConstants.STAR_MODE_TOP.equals(mode)) {
            builder.where(StarDao.Properties.Betop.gt(0)
                    , StarDao.Properties.Bebottom.eq(0));
        }
        else if (DataConstants.STAR_MODE_BOTTOM.equals(mode)) {
            builder.where(StarDao.Properties.Bebottom.gt(0)
                    , StarDao.Properties.Betop.eq(0));
        }
        else if (DataConstants.STAR_MODE_HALF.equals(mode)) {
            builder.where(StarDao.Properties.Bebottom.gt(0)
                    , StarDao.Properties.Betop.gt(0));
        }
        return builder.buildCount().count();
    }

}
