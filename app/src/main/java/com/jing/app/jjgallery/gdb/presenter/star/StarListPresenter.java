package com.jing.app.jjgallery.gdb.presenter.star;

import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.GdbConstants;
import com.jing.app.jjgallery.gdb.model.GdbImageProvider;
import com.jing.app.jjgallery.gdb.model.SettingProperties;
import com.jing.app.jjgallery.gdb.model.bean.StarProxy;
import com.jing.app.jjgallery.gdb.model.conf.PreferenceValue;
import com.jing.app.jjgallery.gdb.view.star.IStarListHeaderView;
import com.jing.app.jjgallery.gdb.view.star.IStarListView;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.entity.StarDao;
import com.king.app.gdb.data.param.DataConstants;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

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
 * <p/>创建时间: 2017/7/12 9:35
 */
public class StarListPresenter {

    private IStarListHeaderView starListHeaderView;
    private Random random;
    private List<Star> favorList;

    public StarListPresenter() {
        random = new Random();
    }

    /**
     * StarListActivity的主activity业务逻辑回调
     * @param starListHeaderView
     */
    public void setStarListHeaderView(IStarListHeaderView starListHeaderView) {
        this.starListHeaderView = starListHeaderView;
    }

    public void saveFavor(Star star) {
        StarDao dao = GdbApplication.getInstance().getDaoSession().getStarDao();
        dao.update(star);
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
                        starListHeaderView.onFavorListLoaded();
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

    /**
     *
     * @param starMode
     * @param sortMode
     * @param starListView StarListFragment的回调
     */
    public void loadStarList(final String starMode, final int sortMode, final IStarListView starListView) {
        Observable.create(new ObservableOnSubscribe<List<StarProxy>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<StarProxy>> e) throws Exception {

                // 加载star list
                List<Star> list = queryStar(starMode, sortMode == GdbConstants.STAR_SORT_FAVOR);
                // 装配StarProxy
                List<StarProxy> proxyList = new ArrayList<>();
                for (Star star:list) {
                    StarProxy proxy = new StarProxy();
                    proxy.setStar(star);
                    proxy.setImagePath(GdbImageProvider.getStarRandomPath(star.getName(), null));
                    proxyList.add(proxy);
                }

                // 排序
                List<StarProxy> resultList = new ArrayList<>();
                if (sortMode == GdbConstants.STAR_SORT_RECORDS) {// order by records number
                    resultList.addAll(proxyList);
                    Collections.sort(resultList, new StarRecordsNumberComparator());
                }
                else {
                    // order by name
                    // 只有list列表支持sticky header
                    if (SettingProperties.getStarListViewMode() == PreferenceValue.STAR_LIST_VIEW_LIST) {
                        // add headers
                        // about header rules, see viewsystem/main/gdb/StarListAdapter.java
                        StarProxy star = null;
                        char index = 'A';
                        for (int i = 0; i < 26; i ++) {
                            star = new StarProxy();
                            Star s = new Star();
                            star.setStar(s);
                            s.setId(-1l);
                            s.setName("" + index ++);
                            resultList.add(star);
                        }
                    }

                    resultList.addAll(proxyList);
                    Collections.sort(resultList, new StarNameComparator());
                }

                e.onNext(resultList);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<StarProxy>>() {
                    @Override
                    public void accept(List<StarProxy> starProxies) throws Exception {
                        starListView.onLoadStarList(starProxies);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        starListView.onLoadStarError(throwable.getMessage());
                    }
                });
    }

    public void queryIndicatorData(final int starMode) {
        Observable.create(new ObservableOnSubscribe<List<Integer>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<Integer>> e) throws Exception {
                List<Integer> countList = new ArrayList<>();
                countList.add((int) queryStarCount(DataConstants.STAR_MODE_ALL, starMode == GdbConstants.STAR_SORT_FAVOR));
                countList.add((int) queryStarCount(DataConstants.STAR_MODE_TOP, starMode == GdbConstants.STAR_SORT_FAVOR));
                countList.add((int) queryStarCount(DataConstants.STAR_MODE_BOTTOM, starMode == GdbConstants.STAR_SORT_FAVOR));
                countList.add((int) queryStarCount(DataConstants.STAR_MODE_HALF, starMode == GdbConstants.STAR_SORT_FAVOR));
                e.onNext(countList);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Integer>>() {
                    @Override
                    public void accept(List<Integer> countList) throws Exception {
                        starListHeaderView.onStarCountLoaded(countList);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    /**
     * order by name
     */
    public class StarNameComparator implements Comparator<StarProxy> {

        @Override
        public int compare(StarProxy l, StarProxy r) {
            if (l == null || r == null) {
                return 0;
            }

            return l.getStar().getName().toLowerCase().compareTo(r.getStar().getName().toLowerCase());
        }
    }

    /**
     * order by records number
     */
    public class StarRecordsNumberComparator implements Comparator<StarProxy> {

        @Override
        public int compare(StarProxy l, StarProxy r) {
            if (l == null || r == null) {
                return 0;
            }

            // order by record number desc
            int result = r.getStar().getRecords() - l.getStar().getRecords();
            // if same, then compare name and order by name asc
            if (result == 0) {
                result = l.getStar().getName().toLowerCase().compareTo(r.getStar().getName().toLowerCase());
            }
            return result;
        }
    }
}
