package com.jing.app.jjgallery.gdb.presenter.game;

import com.jing.app.jjgallery.gdb.model.bean.RandomStarBean;
import com.jing.app.jjgallery.gdb.model.db.GdbProviderHelper;
import com.jing.app.jjgallery.gdb.util.DebugLog;
import com.jing.app.jjgallery.gdb.view.game.IRandomStarView;
import com.king.service.gdb.bean.FavorBean;
import com.king.service.gdb.bean.GDBProperites;
import com.king.service.gdb.bean.Star;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    private List<RandomStarBean> starList;
    private List<FavorBean> favorList;
    private Random random;

    private boolean isFromAll, isFromFavor, isTypeAll, isType1, isType0, isTypeHalf;

    public RandomStarPresenter(IRandomStarView view) {
        this.view = view;
        random = new Random();
    }

    public void random(boolean fromAll, final boolean fromFavor
            , final boolean typeAll, final boolean type1, final boolean type0, final boolean typeHalf
            , final String number, final boolean isOnly) {

        int nNumber = 1;
        try {
            nNumber = Integer.parseInt(number);
        } catch (Exception e) {
            view.onErrorMessage("输入数量有误");
            return;
        }
        if (nNumber == 0) {
            view.onErrorMessage("输入数量不能为0");
            return;
        }

        // 数据源没有变化，直接从当前starList产生数据
        if (fromAll == isFromAll && fromFavor == isFromFavor
                && typeAll == isTypeAll && type1 == isType1 && type0 == isType0 && typeHalf == isTypeHalf
                && starList != null) {
            randomFromStarList(nNumber, isOnly);
        }
        // 数据源如果变化重新加载数据源
        else {
            DebugLog.e("load list");
            isFromAll = fromAll;
            isFromFavor = fromFavor;
            isType0 = type0;
            isType1 = type1;
            isTypeAll = typeAll;
            isTypeHalf = typeHalf;
            final int finalNNumber = nNumber;
            Observable.create(new ObservableOnSubscribe<Object>() {
                @Override
                public void subscribe(ObservableEmitter<Object> e) throws Exception {

                    if (starList == null) {
                        starList = new ArrayList<>();
                    }
                    else {
                        starList.clear();
                    }

                    if (fromFavor) {
                        // 加载favor关系
                        if (favorList == null) {
                            favorList = GdbProviderHelper.getProvider().getFavors();
                        }

                        for (FavorBean bean:favorList) {
                            Star star = GdbProviderHelper.getProvider().queryStarById(bean.getStarId());
                            if (star != null) {
                                if (typeAll) {
                                    RandomStarBean rsb = new RandomStarBean();
                                    rsb.setStarId(star.getId());
                                    rsb.setName(star.getName());
                                    starList.add(rsb);
                                }
                                // 多选关系
                                else {
                                    if (type1) {
                                        if (star.getBeTop() > 0 && star.getBeBottom() == 0) {
                                            RandomStarBean rsb = new RandomStarBean();
                                            rsb.setStarId(star.getId());
                                            rsb.setName(star.getName());
                                            starList.add(rsb);
                                        }
                                    }
                                    if (type0) {
                                        if (star.getBeBottom() > 0 && star.getBeTop() == 0) {
                                            RandomStarBean rsb = new RandomStarBean();
                                            rsb.setStarId(star.getId());
                                            rsb.setName(star.getName());
                                            starList.add(rsb);
                                        }
                                    }
                                    if (typeHalf) {
                                        if (star.getBeBottom() > 0 && star.getBeTop() > 0) {
                                            RandomStarBean rsb = new RandomStarBean();
                                            rsb.setStarId(star.getId());
                                            rsb.setName(star.getName());
                                            starList.add(rsb);
                                        }
                                    }
                                }
                            }
                        }

                        e.onNext(new Object());
                    }
                    else {
                        List<Star> slist = new ArrayList<>();
                        if (typeAll) {
                            slist = GdbProviderHelper.getProvider().getStars(GDBProperites.STAR_MODE_ALL);
                        }
                        // 多选关系
                        else {
                            if (type0) {
                                List<Star> list = GdbProviderHelper.getProvider().getStars(GDBProperites.STAR_MODE_BOTTOM);
                                slist.addAll(list);
                            }
                            if (type1) {
                                List<Star> list = GdbProviderHelper.getProvider().getStars(GDBProperites.STAR_MODE_TOP);
                                slist.addAll(list);
                            }
                            if (typeHalf) {
                                List<Star> list = GdbProviderHelper.getProvider().getStars(GDBProperites.STAR_MODE_HALF);
                                slist.addAll(list);
                            }
                        }
                        for (Star star:slist) {
                            RandomStarBean rsb = new RandomStarBean();
                            rsb.setStarId(star.getId());
                            rsb.setName(star.getName());
                            starList.add(rsb);
                        }
                        e.onNext(new Object());
                    }
                }
            }).observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object obj) throws Exception {

                            randomFromStarList(finalNNumber, isOnly);

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                        }
                    });
        }

    }

    private void randomFromStarList(int number, boolean isOnly) {
        DebugLog.e();
        if (starList.size() < number) {
            view.onErrorMessage("没有足够的可随机数量，可随机数量（" + starList.size() + "）");
            return;
        }
        List<RandomStarBean> list = new ArrayList<>();
        if (isOnly) {
            Collections.shuffle(starList);
            for (int i = 0; i < number && i <starList.size(); i ++) {
                list.add(starList.get(i));
            }
        }
        else {
            for (int i = 0; i < number; i ++) {
                int position = Math.abs(random.nextInt()) % starList.size();
                list.add(starList.get(position));
            }
        }
        view.onRandomStar(list);
    }
}
