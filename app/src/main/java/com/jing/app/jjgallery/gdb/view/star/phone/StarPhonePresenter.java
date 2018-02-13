package com.jing.app.jjgallery.gdb.view.star.phone;

import com.jing.app.jjgallery.gdb.BasePresenter;
import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.GdbConstants;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.entity.StarDao;
import com.king.app.gdb.data.param.DataConstants;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @desc
 * @auth 景阳
 * @time 2018/2/13 0013 11:43
 */

public class StarPhonePresenter extends BasePresenter<StarPhoneView> {

    private Random random;

    private List<Star> favorList;

    @Override
    public void onCreate() {
        random = new Random();
    }

    public void loadTitles(final int curSortMode) {
        Observable.create(new ObservableOnSubscribe<List<Integer>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Integer>> e) throws Exception {
                List<Integer> countList = new ArrayList<>();
                countList.add((int) queryStarCount(DataConstants.STAR_MODE_ALL, curSortMode == GdbConstants.STAR_SORT_FAVOR));
                countList.add((int) queryStarCount(DataConstants.STAR_MODE_TOP, curSortMode == GdbConstants.STAR_SORT_FAVOR));
                countList.add((int) queryStarCount(DataConstants.STAR_MODE_BOTTOM, curSortMode == GdbConstants.STAR_SORT_FAVOR));
                countList.add((int) queryStarCount(DataConstants.STAR_MODE_HALF, curSortMode == GdbConstants.STAR_SORT_FAVOR));
                e.onNext(countList);
            }
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

    private long queryStarCount(String mode, boolean isFavor) {
        StarDao dao = GdbApplication.getInstance().getDaoSession().getStarDao();
        QueryBuilder<Star> builder = dao.queryBuilder();
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
        if (isFavor) {
            builder.where(StarDao.Properties.Favor.gt(0));
        }
        return builder.buildCount().count();
    }


    public void loadFavorList() {
        Observable.create(new ObservableOnSubscribe<List<Star>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<Star>> subscriber) throws Exception {
                StarDao dao = GdbApplication.getInstance().getDaoSession().getStarDao();
                favorList = dao.queryBuilder()
                        .where(StarDao.Properties.Favor.gt(0))
                        .build().list();
                subscriber.onNext(favorList);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Star>>() {
                    @Override
                    public void accept(List<Star> list) throws Exception {
                        view.onFavorListLoaded();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    public Star nextFavorStar() {
        if (favorList != null && favorList.size() > 0) {
            return favorList.get(Math.abs(random.nextInt() % favorList.size()));
        }
        return null;
    }

}
