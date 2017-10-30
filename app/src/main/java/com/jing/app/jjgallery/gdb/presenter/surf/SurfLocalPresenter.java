package com.jing.app.jjgallery.gdb.presenter.surf;

import com.jing.app.jjgallery.gdb.http.bean.data.FileBean;
import com.jing.app.jjgallery.gdb.model.conf.PreferenceValue;
import com.jing.app.jjgallery.gdb.view.surf.ISurfLocalView;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述: presenter for local files explore
 * <p/>作者：景阳
 * <p/>创建时间: 2017/7/27 11:10
 */
public class SurfLocalPresenter {

    private ISurfLocalView surfView;

    public SurfLocalPresenter(ISurfLocalView surfView) {
        this.surfView = surfView;
    }

    private FileFilter fileFilter = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            return !pathname.getName().endsWith(".nomedia");
        }
    };

    public void surf(final FileBean folderBean, final int sortMode, final boolean sortDesc) {
        Observable.create(new ObservableOnSubscribe<List<FileBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<FileBean>> e) throws Exception {
                File dir = new File(folderBean.getPath());
                File[] files = dir.listFiles(fileFilter);
                List<FileBean> list = new ArrayList<>();
                for (File file:files) {
                    FileBean bean = new FileBean();
                    bean.setFolder(file.isDirectory());
                    bean.setName(file.getName());
                    bean.setPath(file.getPath());
                    bean.setLastModifyTime(file.lastModified());
                    bean.setParentBean(folderBean);
                    countFileSize(file, bean);
                    list.add(bean);
                }

                Collections.sort(list, new SurfComparator(sortMode, sortDesc));
                e.onNext(list);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<FileBean>>() {
                    @Override
                    public void accept(List<FileBean> list) throws Exception {
                        surfView.onFolderReceived(list);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    private void countFileSize(File file, FileBean bean) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f:files) {
                countFileSize(f, bean);
            }
        }
        else {
            bean.setSize(bean.getSize() + file.length());
        }
    }

    public void sortFileList(List<FileBean> surfFileList, int sortMode, boolean desc) {
        Collections.sort(surfFileList, new SurfComparator(sortMode, desc));
        surfView.onSortFinished();
    }

    private class SurfComparator implements Comparator<FileBean> {

        private int sortMode;
        private boolean desc;

        private SurfComparator(int sortMode, boolean desc) {
            this.sortMode = sortMode;
            this.desc = desc;
        }

        @Override
        public int compare(FileBean o1, FileBean o2) {
            int result = 0;
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
            return result;
        }
    }
}
