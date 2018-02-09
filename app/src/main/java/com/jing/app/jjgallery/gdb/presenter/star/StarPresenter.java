package com.jing.app.jjgallery.gdb.presenter.star;

import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.model.GdbImageProvider;
import com.jing.app.jjgallery.gdb.model.RecordComparator;
import com.jing.app.jjgallery.gdb.model.bean.StarProxy;
import com.jing.app.jjgallery.gdb.model.conf.PreferenceValue;
import com.jing.app.jjgallery.gdb.view.star.IStarView;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.entity.StarDao;

import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/7/13 10:35
 */
public class StarPresenter {

    private IStarView starView;

    public StarPresenter(IStarView starView) {
        this.starView = starView;
    }

    public void sortRecords(List<Record> recordList, int sortMode, boolean desc) {
        if (sortMode != PreferenceValue.GDB_SR_ORDERBY_NONE) {
            Collections.sort(recordList, new RecordComparator(sortMode, desc));
        }
    }

    public boolean isStarFavor(Star star) {
        return star.getFavor() > 0;
    }

    public void loadStar(final long starId) {
        Observable.create(new ObservableOnSubscribe<StarProxy>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<StarProxy> e) throws Exception {
                StarDao dao = GdbApplication.getInstance().getDaoSession().getStarDao();
                // load star
                Star star = dao.queryBuilder()
                        .where(StarDao.Properties.Id.eq(starId))
                        .build().unique();
                // load records
                star.getRecordList();

                StarProxy proxy = new StarProxy();
                proxy.setStar(star);

                // load image path of star
                String headPath = GdbImageProvider.getStarRandomPath(star.getName(), null);
                proxy.setImagePath(headPath);
                e.onNext(proxy);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<StarProxy>() {
                    @Override
                    public void accept(StarProxy starProxy) throws Exception {
                        starView.onStarLoaded(starProxy);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    public void saveFavor(Star star) {
        StarDao dao = GdbApplication.getInstance().getDaoSession().getStarDao();
        dao.update(star);
    }
}
