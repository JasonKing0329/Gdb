package com.jing.app.jjgallery.gdb.presenter.star;

import android.os.AsyncTask;

import com.jing.app.jjgallery.gdb.GdbConstants;
import com.jing.app.jjgallery.gdb.http.AppHttpClient;
import com.jing.app.jjgallery.gdb.http.Command;
import com.jing.app.jjgallery.gdb.http.bean.data.DownloadItem;
import com.jing.app.jjgallery.gdb.http.bean.request.GdbCheckNewFileBean;
import com.jing.app.jjgallery.gdb.model.GdbImageProvider;
import com.jing.app.jjgallery.gdb.model.bean.StarProxy;
import com.jing.app.jjgallery.gdb.model.conf.Configuration;
import com.jing.app.jjgallery.gdb.model.db.GdbProviderHelper;
import com.jing.app.jjgallery.gdb.presenter.ManageListPresenter;
import com.jing.app.jjgallery.gdb.util.DebugLog;
import com.jing.app.jjgallery.gdb.view.list.IManageListView;
import com.jing.app.jjgallery.gdb.view.star.IStarListHeaderView;
import com.jing.app.jjgallery.gdb.view.star.IStarListView;
import com.king.service.gdb.bean.FavorBean;
import com.king.service.gdb.bean.Star;
import com.king.service.gdb.bean.StarCountBean;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class StarListPresenter extends ManageListPresenter {

    private IStarListHeaderView starListHeaderView;
    private List<FavorBean> favorList;
    private Random random;

    public StarListPresenter(IManageListView view) {
        super(view);
        random = new Random();
    }

    /**
     * StarListActivity的主activity业务逻辑回调
     * @param starListHeaderView
     */
    public void setStarListHeaderView(IStarListHeaderView starListHeaderView) {
        this.starListHeaderView = starListHeaderView;
    }

    public void checkNewStarFile() {
        AppHttpClient.getInstance().getAppService().checkNewFile(Command.TYPE_STAR)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<GdbCheckNewFileBean>() {
                    @Override
                    public void accept(GdbCheckNewFileBean bean) throws Exception {
                        view.onCheckPass(bean.isStarExisted(), bean.getStarItems());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        view.onRequestFail();
                    }
                });
    }

    /**
     * 检查已有图片的star，将其过滤掉
     *
     * @param downloadList 服务端提供的下载列表
     * @param existedList  已存在的下载内容，不能为null
     * @return 未存在的下载内容
     */
    public List<DownloadItem> pickStarToDownload(List<DownloadItem> downloadList, List<DownloadItem> existedList) {
        List<DownloadItem> list = new ArrayList<>();
        for (DownloadItem item : downloadList) {
            // name 格式为 XXX.png
            String name = item.getName().substring(0, item.getName().lastIndexOf("."));

            String path;
            // 服务端文件处于一级目录
            if (item.getKey() == null) {
                // 检查本地一级目录是否存在
                path = Configuration.GDB_IMG_STAR + "/" + name + ".png";
                if (!new File(path).exists()) {
                    // 检查本地二级目录是否存在
                    path = Configuration.GDB_IMG_STAR + "/" + name + "/" + name + ".png";
                }
            }
            // 服务端文件处于二级目录
            else {
                // 只检查本地二级目录是否存在
                path = Configuration.GDB_IMG_STAR + "/" + item.getKey() + "/" + name + ".png";
            }

            // 检查本地一级目录是否存在
            if (new File(path).exists()) {
                item.setPath(path);
                existedList.add(item);
            } else {
                list.add(item);
            }
        }
        return list;
    }

    public void saveFavor(FavorBean bean) {
        GdbProviderHelper.getProvider().saveFavor(bean);
    }

    public void loadFavorList() {
        Observable.create(new ObservableOnSubscribe<List<FavorBean>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<FavorBean>> subscriber) throws Exception {
                favorList = GdbProviderHelper.getProvider().getFavors();
                subscriber.onNext(favorList);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<FavorBean>>() {
                    @Override
                    public void accept(List<FavorBean> favorBeen) throws Exception {
                        starListHeaderView.onFavorListLoaded();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    public FavorBean nextFavorStar() {
        if (favorList != null && favorList.size() > 0) {
            return favorList.get(Math.abs(random.nextInt() % favorList.size()));
        }
        return null;
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
                // 加载favor关系
                favorList = GdbProviderHelper.getProvider().getFavors();
                Map<Integer, FavorBean> favorMap = new HashMap<>();
                for (FavorBean bean:favorList) {
                    favorMap.put(bean.getStarId(), bean);
                }

                // 加载star list
                List<Star> list = GdbProviderHelper.getProvider().getStars(starMode);
                // 装配StarProxy
                List<StarProxy> proxyList = new ArrayList<>();
                for (Star star:list) {
                    StarProxy proxy = new StarProxy();
                    proxy.setStar(star);
                    proxy.setImagePath(GdbImageProvider.getStarRandomPath(star.getName(), null));
                    FavorBean favor = favorMap.get(star.getId());
                    proxy.setFavor(favor == null ? 0:favor.getFavor());
                    proxyList.add(proxy);
                }

                // 排序
                List<StarProxy> resultList = new ArrayList<>();
                if (sortMode == GdbConstants.STAR_SORT_RECORDS) {// order by records number
                    resultList.addAll(proxyList);
                    Collections.sort(resultList, new StarRecordsNumberComparator());
                }
                else if (sortMode == GdbConstants.STAR_SORT_FAVOR) {
                    for (StarProxy proxy:proxyList) {
                        if (proxy.getFavor() > 0) {
                            resultList.add(proxy);
                        }
                    }
                    Collections.sort(resultList, new StarFavorComparator());
                }
                else {// order by name
                    // add headers
                    // about header rules, see viewsystem/main/gdb/StarListAdapter.java
                    StarProxy star = null;
                    char index = 'A';
                    for (int i = 0; i < 26; i ++) {
                        star = new StarProxy();
                        Star s = new Star();
                        star.setStar(s);
                        s.setId(-1);
                        s.setName("" + index ++);
                        resultList.add(star);
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
        Observable.create(new ObservableOnSubscribe<StarCountBean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<StarCountBean> e) throws Exception {
                if (starMode == GdbConstants.STAR_SORT_FAVOR) {
                    e.onNext(GdbProviderHelper.getProvider().queryFavorStarCount());
                }
                else {
                    e.onNext(GdbProviderHelper.getProvider().queryStarCount());
                }
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<StarCountBean>() {
                    @Override
                    public void accept(StarCountBean starCountBean) throws Exception {
                        starListHeaderView.onStarCountLoaded(starCountBean);
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
            int result = r.getStar().getRecordNumber() - l.getStar().getRecordNumber();
            // if same, then compare name and order by name asc
            if (result == 0) {
                result = l.getStar().getName().toLowerCase().compareTo(r.getStar().getName().toLowerCase());
            }
            return result;
        }
    }

    /**
     * order by favor score
     */
    public class StarFavorComparator implements Comparator<StarProxy> {

        @Override
        public int compare(StarProxy l, StarProxy r) {

            return r.getFavor() - l.getFavor();
        }
    }

}
