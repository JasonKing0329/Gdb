package com.jing.app.jjgallery.gdb.presenter.star;

import android.text.TextUtils;
import android.view.View;

import com.jing.app.jjgallery.gdb.BasePresenter;
import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.GdbConstants;
import com.jing.app.jjgallery.gdb.model.GdbImageProvider;
import com.jing.app.jjgallery.gdb.model.SettingProperties;
import com.jing.app.jjgallery.gdb.model.bean.StarProxy;
import com.jing.app.jjgallery.gdb.model.conf.PreferenceValue;
import com.jing.app.jjgallery.gdb.util.DisplayHelper;
import com.jing.app.jjgallery.gdb.view.star.IndexEmitter;
import com.jing.app.jjgallery.gdb.view.star.StarListView;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.entity.StarDao;
import com.king.app.gdb.data.param.DataConstants;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/7/12 9:35
 */
public class StarListPresenter extends BasePresenter<StarListView> {

    private int currentViewMode;

    // see GdbConstants.STAR_SORT_XXX
    private int mSortType;

    // see GDBProperties.STAR_MODE_XXX
    private String mStarType;

    private List<StarProxy> mFullList;

    private List<StarProxy> mList;

    private Map<Long, Boolean> mExpandMap;

    private String mKeyword;

    private IndexEmitter indexEmitter;

    @Override
    public void onCreate() {
        if (DisplayHelper.isTabModel()) {
            SettingProperties.setStarListViewMode(PreferenceValue.STAR_LIST_VIEW_CIRCLE);
            currentViewMode = PreferenceValue.STAR_LIST_VIEW_CIRCLE;
        }
        else {
            SettingProperties.setStarListViewMode(PreferenceValue.STAR_LIST_VIEW_RICH);
            currentViewMode = PreferenceValue.STAR_LIST_VIEW_RICH;
        }
        mSortType = GdbConstants.SCENE_SORT_NAME;
        mExpandMap = new HashMap<>();
        indexEmitter = new IndexEmitter();
    }

    public void setStarType(String mStarType) {
        this.mStarType = mStarType;
    }

    public void setSortType(int sortType) {
        this.mSortType = sortType;
    }

    public int getSortType() {
        return mSortType;
    }

    public void saveFavor(Star star) {
        StarDao dao = GdbApplication.getInstance().getDaoSession().getStarDao();
        dao.update(star);
    }

    private List<Star> queryStar(String mode, boolean isFavor) {
        StarDao dao = GdbApplication.getInstance().getDaoSession().getStarDao();
        QueryBuilder<Star> builder = dao.queryBuilder();

        // don't show star without records
        builder.where(StarDao.Properties.Records.gt(0));

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

    private Observable<List<StarProxy>> loadStarProxy() {
        return Observable.create(e -> {
            // 加载star list
            List<Star> list = queryStar(mStarType, false);
            // 装配StarProxy
            List<StarProxy> proxyList = new ArrayList<>();
            for (Star star:list) {
                star.getRatings();
                StarProxy proxy = new StarProxy();
                proxy.setStar(star);
                proxy.setImagePath(GdbImageProvider.getStarRandomPath(star.getName(), null));
                proxyList.add(proxy);
            }
            e.onNext(proxyList);
            e.onComplete();
        });
    }

    private Observable<List<StarProxy>> sortStars() {
        return Observable.create(e -> {
            if (mSortType == GdbConstants.STAR_SORT_RECORDS) {// order by records number
                Collections.sort(mList, new StarRecordsNumberComparator());
            }
            else if (mSortType == GdbConstants.STAR_SORT_FAVOR) {// order by favor
                Collections.sort(mList, new StarFavorComparator());
            }
            else if (mSortType == GdbConstants.STAR_SORT_RATING) {// order by rating
                Collections.sort(mList, new StarRatingComparator());
            }
            else {
                // order by name
                Collections.sort(mList, new StarNameComparator());
            }
            e.onNext(mList);
            e.onComplete();
        });
    }

    public void loadStarList() {
        currentViewMode = SettingProperties.getStarListViewMode();
        view.showLoading();
        view.getSidebar().clear();
        loadStarProxy()
                .flatMap(list -> {
                    mFullList = list;
                    mExpandMap.clear();
                    mList = new ArrayList<>();
                    for (StarProxy proxy:mFullList) {
                        mList.add(proxy);
                    }
                    // 默认收起
                    setExpandAll(false);
                    return sortStars();
                })
                .flatMap(list -> createIndexes())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(String index) {
                        view.getSidebar().addIndex(index);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.dismissLoading();
                        view.showToastLong(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        view.dismissLoading();
                        view.getSidebar().build();
                        view.getSidebar().setVisibility(View.VISIBLE);
                        if (currentViewMode == PreferenceValue.STAR_LIST_VIEW_CIRCLE) {
                            view.showCircleList(mList);
                        }
                        else {
                            view.showRichList(mList, mExpandMap);
                        }
                    }
                });
    }

    private Observable<String> createIndexes() {
        return Observable.create(e -> {
            indexEmitter.clear();
            switch (mSortType) {
                case GdbConstants.SCENE_SORT_NUMBER:
                    indexEmitter.createRecordsIndex(e, mList);
                    break;
                case GdbConstants.STAR_SORT_RATING:
                    indexEmitter.createRatingIndex(e, mList);
                    break;
                default:
                    indexEmitter.createNameIndex(e, mList);
                    break;
            }
            e.onComplete();
        });
    }

    public void sortStarList(final int sortType) {
        view.showLoading();
        setSortType(sortType);
        view.getSidebar().clear();
        sortStars()
                .flatMap(list ->  createIndexes())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(String index) {
                        view.getSidebar().addIndex(index);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.dismissLoading();
                        view.showToastLong("Sort stars failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        view.dismissLoading();
                        view.getSidebar().build();
                        view.getSidebar().setVisibility(View.VISIBLE);
                        if (currentViewMode == PreferenceValue.STAR_LIST_VIEW_CIRCLE) {
                            view.notifyCircleUpdated();
                        }
                        else {
                            view.notifyRichUpdated();
                        }
                    }
                });

    }

    /**
     * filter by inputted text
     * @param text
     */
    public void filter(String text) {
        if (!text.equals(mKeyword)) {
            filterObservable(filterByText(text), false);
        }
    }

    private void filterObservable(Observable<Boolean> observable, boolean showLoading) {
        if (showLoading) {
            view.showLoading();
        }
        view.getSidebar().clear();
        observable
                .flatMap(filtered -> sortStars())
                .flatMap(aBoolean -> createIndexes())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(String index) {
                        view.getSidebar().addIndex(index);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (showLoading) {
                            view.dismissLoading();
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (showLoading) {
                            view.dismissLoading();
                        }
                        view.getSidebar().build();
                        if (currentViewMode == PreferenceValue.STAR_LIST_VIEW_CIRCLE) {
                            view.showCircleList(mList);
                        }
                        else {
                            view.showRichList(mList, mExpandMap);
                        }
                    }
                });
    }

    private Observable<Boolean> filterByText(String text) {
        return Observable.create(e -> {
            mList.clear();
            mKeyword = text;
            for (int i = 0; i < mFullList.size(); i ++) {
                if (TextUtils.isEmpty(text)) {
                    mList.add(mFullList.get(i));
                }
                else {
                    if (isMatchForKeyword(mFullList.get(i), text)) {
                        mList.add(mFullList.get(i));
                    }
                }
            }
            e.onNext(true);
            e.onComplete();
        });
    }

    private boolean isMatchForKeyword(StarProxy starProxy, String text) {
        return starProxy.getStar().getName().toLowerCase().contains(text.toLowerCase());
    }

    public void setExpandAll(boolean expandAll) {
        mExpandMap.clear();
        for (int i = 0; i < mList.size(); i ++) {
            mExpandMap.put(mList.get(i).getStar().getId(), expandAll);
        }
    }

    public String getDetailIndex(int position) {
        return mList.get(position).getStar().getName();
    }

    public int getLetterPosition(String letter) {
        return indexEmitter.getPlayerIndexMap().get(letter).start;
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

    /**
     * order by rating
     */
    public class StarRatingComparator implements Comparator<StarProxy> {

        @Override
        public int compare(StarProxy l, StarProxy r) {
            if (l == null || r == null) {
                return 0;
            }
            float left;
            try {
                left = l.getStar().getRatings().get(0).getComplex();
            } catch (Exception e) {
                left = 0;
            }
            float right;
            try {
                right = r.getStar().getRatings().get(0).getComplex();
            } catch (Exception e) {
                right = 0;
            }

            if (right - left < 0) {
                return -1;
            }
            else if (right - left > 0) {
                return 1;
            }
            else {
                // if same, then compare name and order by name asc
                return l.getStar().getName().toLowerCase().compareTo(r.getStar().getName().toLowerCase());
            }
        }
    }
}
