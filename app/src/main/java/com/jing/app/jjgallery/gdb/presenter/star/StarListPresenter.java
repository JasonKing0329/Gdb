package com.jing.app.jjgallery.gdb.presenter.star;

import com.jing.app.jjgallery.gdb.BasePresenter;
import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.GdbConstants;
import com.jing.app.jjgallery.gdb.model.GdbImageProvider;
import com.jing.app.jjgallery.gdb.model.SettingProperties;
import com.jing.app.jjgallery.gdb.model.bean.StarProxy;
import com.jing.app.jjgallery.gdb.model.conf.PreferenceValue;
import com.jing.app.jjgallery.gdb.util.DisplayHelper;
import com.jing.app.jjgallery.gdb.view.star.StarListView;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.entity.StarDao;
import com.king.app.gdb.data.param.DataConstants;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
 * <p/>创建时间: 2017/7/12 9:35
 */
public class StarListPresenter extends BasePresenter<StarListView> {

    @Override
    public void onCreate() {
        if (DisplayHelper.isTabModel()) {
            SettingProperties.setStarListViewMode(PreferenceValue.STAR_LIST_VIEW_CIRCLE);
        }
    }

    public void saveFavor(Star star) {
        StarDao dao = GdbApplication.getInstance().getDaoSession().getStarDao();
        dao.update(star);
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

    /**
     *
     * @param starMode
     * @param sortMode
     */
    public void loadStarList(final String starMode, final int sortMode) {
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
                else if (sortMode == GdbConstants.STAR_SORT_FAVOR) {// order by records number
                    resultList.addAll(proxyList);
                    Collections.sort(resultList, new StarFavorComparator());
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
                        view.onLoadStarList(starProxies);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        view.onLoadStarError(throwable.getMessage());
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

    /**
     * order by favor
     */
    public class StarFavorComparator implements Comparator<StarProxy> {

        @Override
        public int compare(StarProxy l, StarProxy r) {
            if (l == null || r == null) {
                return 0;
            }

            // order by record number desc
            int result = r.getStar().getFavor() - l.getStar().getFavor();
            // if same, then compare name and order by name asc
            if (result == 0) {
                result = l.getStar().getName().toLowerCase().compareTo(r.getStar().getName().toLowerCase());
            }
            return result;
        }
    }
}
