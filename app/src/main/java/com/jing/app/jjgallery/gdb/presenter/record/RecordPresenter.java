package com.jing.app.jjgallery.gdb.presenter.record;

import android.text.TextUtils;

import com.jing.app.jjgallery.gdb.model.GdbImageProvider;
import com.jing.app.jjgallery.gdb.model.bean.Star3W;
import com.jing.app.jjgallery.gdb.model.bean.StarProxy;
import com.jing.app.jjgallery.gdb.model.db.GdbProviderHelper;
import com.jing.app.jjgallery.gdb.view.record.IRecordView;
import com.king.service.gdb.bean.GDBProperites;
import com.king.service.gdb.bean.RecordThree;
import com.king.service.gdb.bean.Star;

import java.util.ArrayList;
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
 * <p/>创建时间: 2017/7/13 10:40
 */
public class RecordPresenter {

    private IRecordView recordView;

    private List<Star3W> star3wList;

    public RecordPresenter(IRecordView recordView) {
        this.recordView = recordView;
    }

    public void loadStar(final int starId) {
        Observable.create(new ObservableOnSubscribe<StarProxy>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<StarProxy> e) throws Exception {
                // load star object and star's records
                Star star = GdbProviderHelper.getProvider().queryStarById(starId);
                StarProxy proxy = new StarProxy();
                proxy.setStar(star);

                // load image path of star
                String headPath = GdbImageProvider.getStarRandomPath(star.getName(), null);
                proxy.setImagePath(headPath);
                e.onNext(proxy);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<StarProxy>() {
                    @Override
                    public void accept(StarProxy starProxy) throws Exception {
                        recordView.onStarLoaded(starProxy);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    private List<Star3W> parseStar3WList(RecordThree record) {
        List<Star3W> knownList = new ArrayList<>();
        List<Star> stars = record.getStarTopList();
        if (stars != null && stars.size() > 0) {
            String[] scores = null;
            String[] scoresC = null;
            if (!TextUtils.isEmpty(record.getScoreTop())) {
                scores = record.getScoreTop().split(",");
            }
            if (!TextUtils.isEmpty(record.getScoreTopC())) {
                scoresC = record.getScoreTopC().split(",");
            }
            for (int i = 0; i < stars.size(); i ++) {
                try {
                    Star3W star3w = new Star3W();
                    knownList.add(star3w);
                    star3w.setStar(stars.get(i));
                    star3w.setFlag(GDBProperites.STAR_3W_FLAG_TOP);
                    if (scores != null && scores.length > i) {
                        star3w.setScore((int) Double.parseDouble(scores[i]));
                    }
                    if (scoresC != null && scoresC.length > i) {
                        star3w.setScoreC((int) Double.parseDouble(scoresC[i]));
                    }
                } catch (Exception e) {
                    continue;
                }
            }
        }

        stars = record.getStarBottomList();
        if (stars != null && stars.size() > 0) {
            String[] scores = null;
            String[] scoresC = null;
            if (!TextUtils.isEmpty(record.getScoreBottom())) {
                scores = record.getScoreBottom().split(",");
            }
            if (!TextUtils.isEmpty(record.getScoreBottomC())) {
                scoresC = record.getScoreBottomC().split(",");
            }
            for (int i = 0; i < stars.size(); i ++) {
                try {
                    Star3W star3w = new Star3W();
                    knownList.add(star3w);
                    star3w.setStar(stars.get(i));
                    star3w.setFlag(GDBProperites.STAR_3W_FLAG_BOTTOM);
                    if (scores != null && scores.length > i) {
                        star3w.setScore((int) Double.parseDouble(scores[i]));
                    }
                    if (scoresC != null && scoresC.length > i) {
                        star3w.setScoreC((int) Double.parseDouble(scoresC[i]));
                    }
                } catch (Exception e) {
                    continue;
                }
            }
        }

        stars = record.getStarMixList();
        if (stars != null && stars.size() > 0) {
            String[] scores = null;
            String[] scoresC = null;
            if (!TextUtils.isEmpty(record.getScoreMix())) {
                scores = record.getScoreMix().split(",");
            }
            if (!TextUtils.isEmpty(record.getScoreMixC())) {
                scoresC = record.getScoreMixC().split(",");
            }
            for (int i = 0; i < stars.size(); i ++) {
                try {
                    Star3W star3w = new Star3W();
                    knownList.add(star3w);
                    star3w.setStar(stars.get(i));
                    star3w.setFlag(GDBProperites.STAR_3W_FLAG_MIX);
                    if (scores != null && scores.length > i) {
                        star3w.setScore((int) Double.parseDouble(scores[i]));
                    }
                    if (scoresC != null && scoresC.length > i) {
                        star3w.setScoreC((int) Double.parseDouble(scoresC[i]));
                    }
                } catch (Exception e) {
                    continue;
                }
            }
        }
        return knownList;
    }

    public List<Star3W> getStar3WList(RecordThree record) {
        if (star3wList == null)  {
            star3wList = parseStar3WList(record);
        }
        return star3wList;
    }

}
