package com.jing.app.jjgallery.gdb.presenter;

import com.jing.app.jjgallery.gdb.BasePresenter;
import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.http.AppHttpClient;
import com.jing.app.jjgallery.gdb.http.Command;
import com.jing.app.jjgallery.gdb.http.bean.data.DownloadItem;
import com.jing.app.jjgallery.gdb.http.bean.request.GdbCheckNewFileBean;
import com.jing.app.jjgallery.gdb.http.bean.request.GdbRequestMoveBean;
import com.jing.app.jjgallery.gdb.http.bean.response.AppCheckBean;
import com.jing.app.jjgallery.gdb.http.bean.response.GdbMoveResponse;
import com.jing.app.jjgallery.gdb.http.bean.response.GdbRespBean;
import com.jing.app.jjgallery.gdb.model.bean.CheckDownloadBean;
import com.jing.app.jjgallery.gdb.model.conf.Configuration;
import com.jing.app.jjgallery.gdb.util.ListUtil;
import com.jing.app.jjgallery.gdb.view.settings.IManageView;
import com.jing.app.jjgallery.gdb.view.update.GdbUpdateListener;
import com.jing.app.jjgallery.gdb.view.update.GdbUpdateManager;
import com.king.app.gdb.data.entity.FavorRecord;
import com.king.app.gdb.data.entity.FavorRecordOrder;
import com.king.app.gdb.data.entity.FavorStar;
import com.king.app.gdb.data.entity.FavorStarOrder;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.entity.StarDao;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述: all download and background task
 * <p/>作者：景阳
 * <p/>创建时间: 2017/11/22 15:54
 */
public class ManagePresenter extends BasePresenter<IManageView> {

    @Override
    public void onCreate() {

    }

    public void checkServerStatus() {
        Disposable disposable = AppHttpClient.getInstance().getAppService().isServerOnline()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<GdbRespBean>() {
                    @Override
                    public void accept(GdbRespBean gdbRespBean) throws Exception {
                        if (gdbRespBean.isOnline()) {
                            view.onServerConnected();
                        }
                        else {
                            view.onServerUnavailable();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        view.onServerUnavailable();
                    }
                });
        addDisposable(disposable);
    }

    /**
     * 通知服务器移动下载源文件
     *
     * @param type
     */
    public void requestServeMoveImages(final String type) {
        GdbRequestMoveBean bean = new GdbRequestMoveBean();
        bean.setType(type);

        Disposable disposable = AppHttpClient.getInstance().getAppService().requestMoveImages(bean)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<GdbMoveResponse>() {
                    @Override
                    public void accept(GdbMoveResponse bean) throws Exception {
                        if (bean.isSuccess()) {
                            view.onMoveImagesSuccess();
                        } else {
                            view.onMoveImagesFail();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        view.onMoveImagesFail();
                    }
                });
        addDisposable(disposable);
    }

    /**
     * 检查star更新
     */
    public void checkNewStarFile() {
        Disposable disposable = AppHttpClient.getInstance().getAppService().checkNewFile(Command.TYPE_STAR)
                .flatMap(new Function<GdbCheckNewFileBean, ObservableSource<CheckDownloadBean>>() {
                    @Override
                    public ObservableSource<CheckDownloadBean> apply(GdbCheckNewFileBean bean) throws Exception {
                        return parseCheckStarBean(bean);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CheckDownloadBean>() {
                    @Override
                    public void accept(CheckDownloadBean bean) throws Exception {
                        view.onCheckPass(bean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        view.onRequestFail();
                    }
                });
        addDisposable(disposable);
    }

    private Observable<CheckDownloadBean> parseCheckStarBean(final GdbCheckNewFileBean bean) {
        return Observable.create(new ObservableOnSubscribe<CheckDownloadBean>() {
            @Override
            public void subscribe(ObservableEmitter<CheckDownloadBean> e) throws Exception {
                List<DownloadItem> repeatList = new ArrayList<>();
                List<DownloadItem> toDownloadList = pickStarToDownload(bean.getStarItems(), repeatList);
                CheckDownloadBean cdb = new CheckDownloadBean();
                cdb.setHasNew(bean.isStarExisted());
                cdb.setDownloadList(toDownloadList);
                cdb.setRepeatList(repeatList);
                cdb.setTargetPath(Configuration.GDB_IMG_STAR);
                e.onNext(cdb);
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

    /**
     * check new records
     */
    public void checkNewRecordFile() {
        Disposable disposable = AppHttpClient.getInstance().getAppService().checkNewFile(Command.TYPE_RECORD)
                .flatMap(new Function<GdbCheckNewFileBean, ObservableSource<CheckDownloadBean>>() {
                    @Override
                    public ObservableSource<CheckDownloadBean> apply(GdbCheckNewFileBean bean) throws Exception {
                        return parseCheckRecordBean(bean);
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CheckDownloadBean>() {
                    @Override
                    public void accept(CheckDownloadBean bean) throws Exception {
                        view.onCheckPass(bean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        view.onRequestFail();
                    }
                });
        addDisposable(disposable);
    }

    private Observable<CheckDownloadBean> parseCheckRecordBean(final GdbCheckNewFileBean bean) {
        return Observable.create(new ObservableOnSubscribe<CheckDownloadBean>() {
            @Override
            public void subscribe(ObservableEmitter<CheckDownloadBean> e) throws Exception {
                List<DownloadItem> repeatList = new ArrayList<>();
                List<DownloadItem> toDownloadList = pickRecordToDownload(bean.getRecordItems(), repeatList);
                CheckDownloadBean cdb = new CheckDownloadBean();
                cdb.setHasNew(bean.isRecordExisted());
                cdb.setDownloadList(toDownloadList);
                cdb.setRepeatList(repeatList);
                cdb.setTargetPath(Configuration.GDB_IMG_RECORD);
                e.onNext(cdb);
            }
        });
    }

    /**
     * 从服务端提供的下载列表中选出已存在的和不存在的
     * @param downloadList 服务端提供的下载列表
     * @param existedList 已存在的下载内容，不能为null
     * @return 未存在的下载内容
     */
    public List<DownloadItem> pickRecordToDownload(List<DownloadItem> downloadList, List<DownloadItem> existedList) {
        List<DownloadItem> list = new ArrayList<>();
        for (DownloadItem item:downloadList) {
            // name 格式为 XXX.png
            String name = item.getName().substring(0, item.getName().lastIndexOf("."));

            String path;
            // 服务端文件处于一级目录
            if (item.getKey() == null) {
                // 检查本地一级目录是否存在
                path = Configuration.GDB_IMG_RECORD + "/" + name + ".png";
                if (!new File(path).exists()) {
                    // 检查本地二级目录是否存在
                    path = Configuration.GDB_IMG_RECORD + "/" + name + "/" + name + ".png";
                }
            }
            // 服务端文件处于二级目录
            else {
                // 只检查本地二级目录是否存在
                path = Configuration.GDB_IMG_RECORD + "/" + item.getKey() + "/" + name + ".png";
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

    /**
     * save favor in memory, insert into database after downloaded new one
     */
    private Map<String, Integer> favorMap = new HashMap<>();

    private List<FavorRecord> favorRecordList;

    private List<FavorStar> favorStarList;

    private List<FavorRecordOrder> favorRecordOrderList;

    private List<FavorStarOrder> favorStarOrderList;

    private GdbUpdateManager updateManager;

    public void checkDbUpdate() {

        updateManager = new GdbUpdateManager(view.getContext(), new GdbUpdateListener() {
            @Override
            public void onUpdateFinish() {
                // update favor to database
                updateData();
            }

            @Override
            public void onUpdateCancel() {

            }

            @Override
            public boolean consumeYes(AppCheckBean bean) {
                // 点击是后才开始进行下载更新
                startUpdate(bean);
                return true;
            }
        });
        updateManager.setFragmentManagerV4(view.getFtManager());
        updateManager.showMessageWarning();
        updateManager.startCheck();
    }

    private void startUpdate(AppCheckBean bean) {
        // save data in memory
        saveData(bean)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<AppCheckBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(AppCheckBean appCheckBean) {
                        updateManager.startDownload(appCheckBean);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.showToastLong("Pre-save data failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * save data in memory
     */
    private Observable<AppCheckBean> saveData(final AppCheckBean bean) {
        return Observable.create(new ObservableOnSubscribe<AppCheckBean>() {
            @Override
            public void subscribe(ObservableEmitter<AppCheckBean> e) throws Exception {
                // 保存star的favor字段
                favorMap.clear();
                saveFavorMap();

                // 保存favor_oder等favor相关列表
                favorRecordList = GdbApplication.getInstance().getDaoSession().getFavorRecordDao()
                        .queryBuilder().list();
                favorRecordOrderList = GdbApplication.getInstance().getDaoSession().getFavorRecordOrderDao()
                        .queryBuilder().list();
                favorStarList = GdbApplication.getInstance().getDaoSession().getFavorStarDao()
                        .queryBuilder().list();
                favorStarOrderList = GdbApplication.getInstance().getDaoSession().getFavorStarOrderDao()
                        .queryBuilder().list();
                e.onNext(bean);
            }
        });
    }

    private void saveFavorMap() {

        StarDao dao = GdbApplication.getInstance().getDaoSession().getStarDao();
        List<Star> list = dao.queryBuilder().build().list();
        for (Star star:list) {
            if (star.getFavor() > 0) {
                favorMap.put(star.getName(), star.getFavor());
            }
        }
    }

    private void updateData() {
        view.showLoading();
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                GdbApplication.getInstance().reCreateGreenDao();

                updateStarFavorFiled();
                updateFavorTables();
                e.onNext(new Object());
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Object object) {
                        view.dismissLoading();
                        view.showToastLong("Update successfully!");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.dismissLoading();
                        view.showToastLong("Update failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * update favor to database
     */
    private void updateStarFavorFiled() {
        StarDao dao = GdbApplication.getInstance().getDaoSession().getStarDao();
        List<Star> list = dao.queryBuilder().build().list();
        for (Star star:list) {
            Integer favor = favorMap.get(star.getName());
            if (favor != null && favor > 0) {
                star.setFavor(favor);
                dao.update(star);
            }
        }
    }

    private void updateFavorTables() {
        if (!ListUtil.isEmpty(favorRecordList)) {
            GdbApplication.getInstance().getDaoSession().getFavorRecordDao().insertInTx(favorRecordList);
        }
        if (!ListUtil.isEmpty(favorRecordOrderList)) {
            GdbApplication.getInstance().getDaoSession().getFavorRecordOrderDao().insertInTx(favorRecordOrderList);
        }
        if (!ListUtil.isEmpty(favorStarList)) {
            GdbApplication.getInstance().getDaoSession().getFavorStarDao().insertInTx(favorStarList);
        }
        if (!ListUtil.isEmpty(favorStarOrderList)) {
            GdbApplication.getInstance().getDaoSession().getFavorStarOrderDao().insertInTx(favorStarOrderList);
        }
    }

}
