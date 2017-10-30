package com.jing.app.jjgallery.gdb.presenter.surf;

import com.jing.app.jjgallery.gdb.http.AppHttpClient;
import com.jing.app.jjgallery.gdb.http.bean.data.FileBean;
import com.jing.app.jjgallery.gdb.http.bean.request.FolderRequest;
import com.jing.app.jjgallery.gdb.http.bean.response.FolderResponse;
import com.jing.app.jjgallery.gdb.model.RecordComparator;
import com.jing.app.jjgallery.gdb.model.bean.HttpSurfFileBean;
import com.jing.app.jjgallery.gdb.model.conf.PreferenceValue;
import com.jing.app.jjgallery.gdb.model.db.GdbProviderHelper;
import com.jing.app.jjgallery.gdb.view.surf.ISurfHttpView;

import com.king.service.gdb.bean.Record;

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
 * 描述: presenter for server files explore
 * <p/>作者：景阳
 * <p/>创建时间: 2017/7/27 11:10
 */
public class SurfHttpPresenter {

    private ISurfHttpView surfView;

    public SurfHttpPresenter(ISurfHttpView surfView) {
        this.surfView = surfView;
    }

    public void surf(String type, String folder, final boolean relateToDatabase) {
        FolderRequest request = new FolderRequest();
        request.setType(type);
        request.setFolder(folder);
        AppHttpClient.getInstance().getAppService().requestSurf(request)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<FolderResponse>() {
                    @Override
                    public void accept(FolderResponse bean) throws Exception {
                        List<HttpSurfFileBean> list = new ArrayList<>();
                        for (FileBean fb:bean.getFileList()) {
                            HttpSurfFileBean sfb = new HttpSurfFileBean();
                            sfb.setName(fb.getName());
                            sfb.setPath(fb.getPath());
                            sfb.setFolder(fb.isFolder());
                            sfb.setExtra(fb.getExtra());
                            sfb.setLastModifyTime(fb.getLastModifyTime());
                            sfb.setSize(fb.getSize());
                            sfb.setParentBean(fb.getParentBean());
                            list.add(sfb);
                        }
                        surfView.onFolderReceived(list);
                        if (relateToDatabase) {
                            relateToDatabase(list);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        surfView.onRequestFail();
                    }
                });
    }

    public void relateToDatabase(final List<HttpSurfFileBean> list) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < list.size(); i ++) {
                    HttpSurfFileBean bean = list.get(i);
                    if (!bean.isFolder()) {
                        Record record = GdbProviderHelper.getProvider().getRecordByName(bean.getName());
                        bean.setRecord(record);
                        e.onNext(i);
                    }
                }
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer index) throws Exception {
                        surfView.onRecordRelated(index);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    public void sortFileList(List<HttpSurfFileBean> surfFileList, int sortMode, boolean desc) {
        Collections.sort(surfFileList, new SurfComparator(sortMode, desc));
        surfView.onSortFinished();
    }

    private class SurfComparator implements Comparator<HttpSurfFileBean> {

        private int sortMode;
        private boolean desc;

        private SurfComparator(int sortMode, boolean desc) {
            this.sortMode = sortMode;
            this.desc = desc;
        }

        @Override
        public int compare(HttpSurfFileBean o1, HttpSurfFileBean o2) {
            int result;
            // 按名称排序时，文件夹排在最前面
            if (sortMode == PreferenceValue.GDB_SR_ORDERBY_NAME) {
                // 同为文件/文件夹，按名称
                if (o1.isFolder() == o2.isFolder()) {
                    if (desc) {
                        result = o2.getName().toLowerCase().compareTo(o1.getName().toLowerCase());
                    }
                    else {
                        result = o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
                    }
                }
                // 一个为文件，一个为文件夹，文件夹永远在前
                else {
                    result = o1.isFolder() ? -1:1;
                }
            }
            else if (sortMode == PreferenceValue.GDB_SR_ORDERBY_TIME) {
                if (o1.getLastModifyTime() - o2.getLastModifyTime() < 0) {
                    result = desc ? 1:-1;
                }
                else if (o1.getLastModifyTime() - o2.getLastModifyTime() > 0) {
                    result = desc ? -1:1;
                }
                else {
                    result = 0;
                }
            }
            else if (sortMode == PreferenceValue.GDB_SR_ORDERBY_SIZE) {
                if (o1.getSize() - o2.getSize() < 0) {
                    result = desc ? 1:-1;
                }
                else if (o1.getSize() - o2.getSize() > 0) {
                    result = desc ? -1:1;
                }
                else {
                    result = 0;
                }
            }
            // 按照record记录排序
            else {
                Record lr = o1.getRecord();
                Record rr = o2.getRecord();
                // 都有record记录，按mode和desc排在最前面
                if (lr != null && rr != null) {
                    RecordComparator.setSortMode(sortMode);
                    RecordComparator.setDesc(desc);
                    result = RecordComparator.compareRecord(lr, rr);
                }
                // 没有record记录的排在后面
                else if (lr == null && rr != null) {
                    result = 1;
                }
                else if (lr != null && rr == null) {
                    result = -1;
                }
                else {
                    result = 0;
                }
            }
            return result;
        }
    }
}
