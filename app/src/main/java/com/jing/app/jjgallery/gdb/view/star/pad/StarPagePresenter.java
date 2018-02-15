package com.jing.app.jjgallery.gdb.view.star.pad;

import com.jing.app.jjgallery.gdb.BasePresenter;
import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.model.conf.Configuration;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.entity.StarDao;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
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
 * @time 2018/2/15 0015 19:18
 */

public class StarPagePresenter extends BasePresenter<StarPageView> {

    private Star mStar;

    @Override
    public void onCreate() {

    }

    public void loadStar(final long starId) {
        Observable.create(new ObservableOnSubscribe<Star>() {
            @Override
            public void subscribe(ObservableEmitter<Star> e) throws Exception {
                Star star = GdbApplication.getInstance().getDaoSession().getStarDao()
                        .queryBuilder()
                        .where(StarDao.Properties.Id.eq(starId))
                        .build().unique();
                e.onNext(star);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Star>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Star star) {
                        mStar = star;
                        view.showStar(star);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.showToastLong("Load star error: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public Star getStar() {
        return mStar;
    }

    public List<String> getStarImages() {
        List<String> list = new ArrayList<>();
        // record目录
        File file = new File(Configuration.GDB_IMG_STAR + "/" + mStar.getName() + ".png");
        if (file.exists()) {
            list.add(file.getPath());
        }
        // record/name目录
        file = new File(Configuration.GDB_IMG_STAR + "/" + mStar.getName());
        if (file.exists()) {
            File files[] = file.listFiles();
            for (File f:files) {
                if (!f.isDirectory() && !f.getName().equals(".nomedia")) {
                    list.add(f.getPath());
                }
            }
        }
        // shuffle
        Collections.shuffle(list);
        return list;
    }
}
