package com.jing.app.jjgallery.gdb.presenter.star;

import com.jing.app.jjgallery.gdb.model.GdbImageProvider;
import com.jing.app.jjgallery.gdb.model.RecordComparator;
import com.jing.app.jjgallery.gdb.model.bean.StarProxy;
import com.jing.app.jjgallery.gdb.model.conf.PreferenceValue;
import com.jing.app.jjgallery.gdb.model.db.GdbProviderHelper;
import com.jing.app.jjgallery.gdb.util.DebugLog;
import com.jing.app.jjgallery.gdb.view.star.IStarSwipeView;
import com.king.service.gdb.bean.FavorBean;
import com.king.service.gdb.bean.Record;
import com.king.service.gdb.bean.Star;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * <p/>创建时间: 2017/8/4 11:20
 */
public class StarSwipePresenter {

    private final int LOAD_NUM = 4;

    private IStarSwipeView swipeView;

    private Map<Integer, FavorBean> favorMap;

    public StarSwipePresenter(IStarSwipeView swipeView) {
        this.swipeView = swipeView;
    }

    public void loadNewStars() {
        Observable.create(new ObservableOnSubscribe<List<StarProxy>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<StarProxy>> e) throws Exception {

                // query favor map
                if (favorMap == null) {
                    favorMap = new HashMap<>();
                    List<FavorBean> list = GdbProviderHelper.getProvider().getFavors();
                    for (FavorBean bean:list) {
                        favorMap.put(bean.getStarId(), bean);
                    }
                }

                // load LOAD_NUM stars
                List<Star> starList = GdbProviderHelper.getProvider().getRandomStars(LOAD_NUM);
                List<StarProxy> slist = new ArrayList<>();

                for (Star star:starList) {

                    // load records for each star
                    // 很奇怪如果是从这里装配后，后面onStarLoaded之后执行的代码调试的时候也是对的，但SwipeFlingAdapterView就是显示不出来
                    GdbProviderHelper.getProvider().loadStarRecords(star);
                    DebugLog.e(star.getName() + " records:" + star.getRecordList().size());

                    StarProxy proxy = new StarProxy();
                    proxy.setStar(star);
                    // load image path
                    proxy.setImagePath(GdbImageProvider.getStarRandomPath(star.getName(), null));
                    // relate favor bean to star
                    FavorBean bean = favorMap.get(star.getId());
                    if (bean == null) {
                        proxy.setFavor(0);
                    }
                    else {
                        proxy.setFavor(bean.getFavor());
                        proxy.setFavorBean(bean);
                    }
                    slist.add(proxy);
                }

                e.onNext(slist);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<StarProxy>>() {
                    @Override
                    public void accept(List<StarProxy> list) throws Exception {
                        swipeView.onStarLoaded(list);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    public void saveFavor(FavorBean bean) {
        GdbProviderHelper.getProvider().saveFavor(bean);
        GdbProviderHelper.getProvider().saveFavor(bean);
    }

    public void sortRecords(List<Record> recordList, int sortMode, boolean desc) {
        if (sortMode != PreferenceValue.GDB_SR_ORDERBY_NONE) {
            Collections.sort(recordList, new RecordComparator(sortMode, desc));
        }
    }

}
