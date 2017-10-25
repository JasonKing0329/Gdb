package com.jing.app.jjgallery.gdb.presenter;

import com.jing.app.jjgallery.gdb.http.AppHttpClient;
import com.jing.app.jjgallery.gdb.http.Command;
import com.jing.app.jjgallery.gdb.http.bean.data.DownloadItem;
import com.jing.app.jjgallery.gdb.http.bean.request.GdbRequestMoveBean;
import com.jing.app.jjgallery.gdb.http.bean.response.GdbMoveResponse;
import com.jing.app.jjgallery.gdb.http.bean.response.GdbRespBean;
import com.jing.app.jjgallery.gdb.view.list.IManageListView;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述: 封装一级star list和record list都有的检测更新的通用操作
 * <p/>作者：景阳
 * <p/>创建时间: 2017/7/12 10:47
 */
public class ManageListPresenter {

    protected IManageListView view;

    public ManageListPresenter(IManageListView view) {
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
                        view.onServerUnavailable();
                    }
                });
    }

    /**
     * 将下载文件进行全部加密，回调在onDownloadItemEncrypted
     *
     * @param downloadList
     */
    public void finishDownload(List<DownloadItem> downloadList) {

        // 弃用加密
    }

    /**
     * 通知服务器移动下载源文件
     *
     * @param type
     * @param downloadItems
     */
    public void requestServeMoveImages(final String type, List<DownloadItem> downloadItems) {
        GdbRequestMoveBean bean = new GdbRequestMoveBean();
        bean.setType(type);
        bean.setDownloadList(downloadItems);

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
                        if (type.equals(Command.TYPE_RECORD)) {
                            view.onMoveImagesFail();
                        } else if (type.equals(Command.TYPE_STAR)) {
                            view.onMoveImagesFail();
                        }
                    }
                });
    }

}
