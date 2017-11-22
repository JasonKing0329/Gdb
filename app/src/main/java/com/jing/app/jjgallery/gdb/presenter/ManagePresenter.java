package com.jing.app.jjgallery.gdb.presenter;

import com.jing.app.jjgallery.gdb.http.AppHttpClient;
import com.jing.app.jjgallery.gdb.http.Command;
import com.jing.app.jjgallery.gdb.http.bean.data.DownloadItem;
import com.jing.app.jjgallery.gdb.http.bean.request.GdbCheckNewFileBean;
import com.jing.app.jjgallery.gdb.http.bean.request.GdbRequestMoveBean;
import com.jing.app.jjgallery.gdb.http.bean.response.GdbMoveResponse;
import com.jing.app.jjgallery.gdb.http.bean.response.GdbRespBean;
import com.jing.app.jjgallery.gdb.model.conf.Configuration;
import com.jing.app.jjgallery.gdb.view.settings.IManageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述: all download and background task
 * <p/>作者：景阳
 * <p/>创建时间: 2017/11/22 15:54
 */
public class ManagePresenter {

    private IManageView view;

    public ManagePresenter(IManageView view) {
        this.view = view;
    }

    public void checkServerStatus() {
        AppHttpClient.getInstance().getAppService().isServerOnline()
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
    }

    /**
     * 通知服务器移动下载源文件
     *
     * @param type
     */
    public void requestServeMoveImages(final String type) {
        GdbRequestMoveBean bean = new GdbRequestMoveBean();
        bean.setType(type);

        AppHttpClient.getInstance().getAppService().requestMoveImages(bean)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<GdbMoveResponse>() {
                    @Override
                    public void accept(GdbMoveResponse bean) throws Exception {
                        if (bean.isSuccess()) {
                            if (type.equals(Command.TYPE_RECORD)) {
                                view.onMoveImagesSuccess();
                            } else if (type.equals(Command.TYPE_STAR)) {
                                view.onMoveImagesSuccess();
                            }
                        } else {
                            if (type.equals(Command.TYPE_RECORD)) {
                                view.onMoveImagesFail();
                            } else if (type.equals(Command.TYPE_STAR)) {
                                view.onMoveImagesFail();
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        if (type.equals(Command.TYPE_RECORD)) {
                            view.onMoveImagesFail();
                        } else if (type.equals(Command.TYPE_STAR)) {
                            view.onMoveImagesFail();
                        }
                    }
                });
    }

    /**
     * 检查star更新
     */
    public void checkNewStarFile() {
        AppHttpClient.getInstance().getAppService().checkNewFile(Command.TYPE_STAR)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<GdbCheckNewFileBean>() {
                    @Override
                    public void accept(GdbCheckNewFileBean bean) throws Exception {
                        List<DownloadItem> repeatList = new ArrayList<>();
                        List<DownloadItem> toDownloadList = pickStarToDownload(bean.getStarItems(), repeatList);
                        view.onCheckPass(bean.isStarExisted(), toDownloadList, repeatList
                                , Configuration.GDB_IMG_STAR);
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

    /**
     * check new records
     */
    public void checkNewRecordFile() {
        AppHttpClient.getInstance().getAppService().checkNewFile(Command.TYPE_RECORD)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<GdbCheckNewFileBean>() {
                    @Override
                    public void accept(GdbCheckNewFileBean bean) throws Exception {
                        List<DownloadItem> repeatList = new ArrayList<>();
                        List<DownloadItem> toDownloadList = pickRecordToDownload(bean.getRecordItems(), repeatList);
                        view.onCheckPass(bean.isRecordExisted(), toDownloadList, repeatList
                                , Configuration.GDB_IMG_RECORD);
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

}
