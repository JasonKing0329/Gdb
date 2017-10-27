package com.jing.app.jjgallery.gdb.presenter.star;

import android.os.AsyncTask;

import com.jing.app.jjgallery.gdb.model.GdbImageProvider;
import com.jing.app.jjgallery.gdb.model.RecordComparator;
import com.jing.app.jjgallery.gdb.model.bean.StarProxy;
import com.jing.app.jjgallery.gdb.model.conf.DBInfor;
import com.jing.app.jjgallery.gdb.model.conf.PreferenceValue;
import com.jing.app.jjgallery.gdb.model.db.GdbProviderHelper;
import com.jing.app.jjgallery.gdb.view.star.IStarView;
import com.king.service.gdb.GDBProvider;
import com.king.service.gdb.bean.FavorBean;
import com.king.service.gdb.bean.Record;
import com.king.service.gdb.bean.Star;

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

    public void saveFavor(FavorBean bean) {
        GdbProviderHelper.getProvider().saveFavor(bean);
    }

    public boolean isStarFavor(int starId) {
        return GdbProviderHelper.getProvider().isStarFavor(starId);
    }

    public void loadStar(final int starId) {
        Observable.create(new ObservableOnSubscribe<StarProxy>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<StarProxy> e) throws Exception {
                // load star
                Star star = GdbProviderHelper.getProvider().queryStarById(starId);
                // load records
                GdbProviderHelper.getProvider().loadStarRecords(star);

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

}
