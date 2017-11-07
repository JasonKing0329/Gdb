package com.jing.app.jjgallery.gdb.presenter.game;

import com.jing.app.jjgallery.gdb.model.db.GdbProviderHelper;
import com.jing.app.jjgallery.gdb.view.game.IRandomStarView;
import com.king.service.gdb.bean.FavorBean;
import com.king.service.gdb.bean.GDBProperites;
import com.king.service.gdb.bean.Star;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/11/7 17:15
 */
public class RandomStarPresenter {

    private IRandomStarView view;
    private List<FavorBean> favorList;
    private List<Star> starList;
    private Random random;

    public RandomStarPresenter(IRandomStarView view) {
        this.view = view;
        random = new Random();
    }

    public void random(final boolean fromAll, final boolean fromFavor
            , boolean typeAll, boolean type1, boolean type0, boolean typeHalf
            , String number, boolean isOnly) {
        // 先实现单选的功能
        Observable.create(new ObservableOnSubscribe<List<Star>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Star>> e) throws Exception {
                if (fromFavor) {
                    // 加载favor关系
                    if (favorList == null) {
                        favorList = GdbProviderHelper.getProvider().getFavors();
                    }
//                        Map<Integer, FavorBean> favorMap = new HashMap<>();
//                        for (FavorBean bean:favorList) {
//                            favorMap.put(bean.getStarId(), bean);
//                        }
                    Star star = new Star();
                    int position = Math.abs(random.nextInt()) % favorList.size();
                    star.setName(favorList.get(position).getStarName());
                    star.setId(favorList.get(position).getStarId());
                    List<Star> list = new ArrayList<>();
                    list.add(star);
                    e.onNext(list);
                }
                else {
                    if (starList == null) {
                        // 加载star list
                        starList = GdbProviderHelper.getProvider().getStars(GDBProperites.STAR_MODE_ALL);
                    }
                    int position = Math.abs(random.nextInt()) % starList.size();
                    List<Star> list = new ArrayList<>();
                    list.add(starList.get(position));
                    e.onNext(list);
                }
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Star>>() {
                    @Override
                    public void accept(List<Star> stars) throws Exception {

                        view.onRandomStar(starList);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }
}
