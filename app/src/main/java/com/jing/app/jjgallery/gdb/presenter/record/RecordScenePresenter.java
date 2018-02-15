package com.jing.app.jjgallery.gdb.presenter.record;

import com.jing.app.jjgallery.gdb.BasePresenter;
import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.GdbConstants;
import com.jing.app.jjgallery.gdb.model.db.RecordExtendDao;
import com.jing.app.jjgallery.gdb.model.db.SceneBean;
import com.jing.app.jjgallery.gdb.view.record.IRecordSceneView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @desc
 * @auth 景阳
 * @time 2018/2/15 0015 08:59
 */

public class RecordScenePresenter extends BasePresenter<IRecordSceneView> {

    private RecordExtendDao recordExtendDao;

    private List<SceneBean> mSceneList;

    @Override
    public void onCreate() {
        recordExtendDao = new RecordExtendDao();
    }

    /**
     * load scenes
     */
    public void loadRecordScenes() {
        Observable.create(new ObservableOnSubscribe<List<SceneBean>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<SceneBean>> e) throws Exception {
                List<SceneBean> scenes = recordExtendDao.getSceneList();
                SceneBean bean = new SceneBean();
                bean.setScene(GdbConstants.KEY_SCENE_ALL);
                bean.setNumber((int) GdbApplication.getInstance().getDaoSession().getRecordDao().queryBuilder().buildCount().count());
                scenes.add(0, bean);
                e.onNext(scenes);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<SceneBean>>() {
                    @Override
                    public void accept(List<SceneBean> list) throws Exception {
                        mSceneList = list;
                        view.showScenes(list);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    public int getFocusScenePosition(String focusScene) {
        for (int i = 0; i < mSceneList.size(); i ++) {
            if (mSceneList.get(i).getScene().equals(focusScene)) {
                return i;
            }
        }
        return 0;
    }

    /**
     *
     * @param curSortType see GdbConstants.SCENE_SORT_XXX
     */
    public void sortScenes(final int curSortType) {
        Observable.create(new ObservableOnSubscribe<List<SceneBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<SceneBean>> e) throws Exception {
                List<SceneBean> sceneList = mSceneList.subList(1, mSceneList.size() - 1);
                switch (curSortType) {
                    case GdbConstants.SCENE_SORT_NAME:
                        Collections.sort(sceneList, new SceneNameComparator());
                        break;
                    case GdbConstants.SCENE_SORT_NUMBER:
                        Collections.sort(sceneList, new SceneNumberComparator());
                        break;
                    case GdbConstants.SCENE_SORT_AVG:
                        Collections.sort(sceneList, new SceneAverageComparator());
                        break;
                    case GdbConstants.SCENE_SORT_MAX:
                        Collections.sort(sceneList, new SceneMaxComparator());
                        break;
                }
                sceneList.add(0, mSceneList.get(0));
                mSceneList = sceneList;
                e.onNext(sceneList);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<SceneBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<SceneBean> list) {
                        view.sortFinished(list);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public class SceneNameComparator implements Comparator<SceneBean> {

        @Override
        public int compare(SceneBean l, SceneBean r) {
            if (l == null || r == null) {
                return 0;
            }

            return l.getScene().toLowerCase().compareTo(r.getScene().toLowerCase());
        }
    }

    public class SceneAverageComparator implements Comparator<SceneBean> {

        @Override
        public int compare(SceneBean l, SceneBean r) {
            if (l == null || r == null) {
                return 0;
            }

            if (r.getAverage() - l.getAverage() > 0) {
                return 1;
            }
            else if (r.getAverage() - l.getAverage() < 0) {
                return -1;
            }
            else {
                return 0;
            }
        }
    }

    public class SceneNumberComparator implements Comparator<SceneBean> {

        @Override
        public int compare(SceneBean l, SceneBean r) {
            if (l == null || r == null) {
                return 0;
            }

            if (r.getNumber() - l.getNumber() > 0) {
                return 1;
            }
            else if (r.getNumber() - l.getNumber() < 0) {
                return -1;
            }
            else {
                return 0;
            }
        }
    }

    public class SceneMaxComparator implements Comparator<SceneBean> {

        @Override
        public int compare(SceneBean l, SceneBean r) {
            if (l == null || r == null) {
                return 0;
            }

            if (r.getMax() - l.getMax() > 0) {
                return 1;
            }
            else if (r.getMax() - l.getMax() < 0) {
                return -1;
            }
            else {
                return 0;
            }
        }
    }

}
