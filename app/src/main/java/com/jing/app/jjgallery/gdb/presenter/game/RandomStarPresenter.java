package com.jing.app.jjgallery.gdb.presenter.game;

import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.model.bean.RandomStarBean;
import com.jing.app.jjgallery.gdb.util.DebugLog;
import com.jing.app.jjgallery.gdb.view.game.IRandomStarView;
import com.king.app.gdb.data.entity.RecordStar;
import com.king.app.gdb.data.entity.RecordStarDao;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.entity.StarDao;
import com.king.app.gdb.data.param.DataConstants;

import org.greenrobot.greendao.query.QueryBuilder;

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
    private Random random;

    private boolean isFromAll, isFromFavor, isTypeAll, isType1, isType0, isTypeHalf;

    public RandomStarPresenter(IRandomStarView view) {
        this.view = view;
        random = new Random();
    }

    public void random(boolean fromAll, final boolean isFavor
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
        if (fromAll == isFromAll && isFavor == isFromFavor
                && typeAll == isTypeAll && type1 == isType1 && type0 == isType0 && typeHalf == isTypeHalf
                && starList != null) {
            randomFromStarList(nNumber, isOnly);
        }
        // 数据源如果变化重新加载数据源
        else {
            DebugLog.e("load list");
            isFromAll = fromAll;
            isFromFavor = isFavor;
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

                    if (isFavor) {
                    }
                    else {
                        List<Star> slist = new ArrayList<>();
                        if (typeAll) {
                            slist = queryStar(DataConstants.STAR_MODE_ALL, isFavor);
                        }
                        // 多选关系
                        else {
                            if (type0) {
                                List<Star> list = queryStar(DataConstants.STAR_MODE_BOTTOM, isFavor);
                                slist.addAll(list);
                            }
                            if (type1) {
                                List<Star> list = queryStar(DataConstants.STAR_MODE_TOP, isFavor);
                                slist.addAll(list);
                            }
                            if (typeHalf) {
                                List<Star> list = queryStar(DataConstants.STAR_MODE_HALF, isFavor);
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

    private List<Star> queryStar(String mode, boolean isFavor) {
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
        return builder.build().list();
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
