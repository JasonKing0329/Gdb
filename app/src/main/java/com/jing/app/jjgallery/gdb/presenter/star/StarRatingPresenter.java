package com.jing.app.jjgallery.gdb.presenter.star;

import android.content.Context;
import android.support.v7.graphics.Palette;
import android.widget.ImageView;

import com.jing.app.jjgallery.gdb.GdbApplication;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.model.GdbImageProvider;
import com.jing.app.jjgallery.gdb.model.palette.PaletteResponse;
import com.jing.app.jjgallery.gdb.util.ColorUtils;
import com.jing.app.jjgallery.gdb.util.FormatUtil;
import com.jing.app.jjgallery.gdb.util.StarRatingUtil;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.entity.StarDao;
import com.king.app.gdb.data.entity.StarRating;
import com.king.app.gdb.data.entity.StarRatingDao;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/5/8 19:08
 */
public class StarRatingPresenter {

    private View view;

    private Star mStar;

    private StarRating mRating;

    public StarRatingPresenter(View view) {
        this.view = view;
    }

    public void destroy() {

    }

    public void loadStarRating(long starId) {
        queryStar(starId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Star>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Star star) {
                        mStar = star;
                        String imagePath = GdbImageProvider.getStarRandomPath(star.getName(), null);
                        view.showStar(star.getName(), imagePath);
                        if (star.getRatings().size() > 0) {
                            mRating = star.getRatings().get(0);
                            view.showRating(mRating);
                            updateComplex();
                        }
                        else {
                            mRating = new StarRating();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Observable<Star> queryStar(final long starId) {
        return Observable.create(new ObservableOnSubscribe<Star>() {
            @Override
            public void subscribe(ObservableEmitter<Star> e) throws Exception {
                StarDao dao = GdbApplication.getInstance().getDaoSession().getStarDao();
                Star star = dao.queryBuilder()
                        .where(StarDao.Properties.Id.eq(starId))
                        .build().unique();
                e.onNext(star);
            }
        });
    }

    public StarRating getRating() {
        return mRating;
    }

    public void saveRating() {
        mRating.setStarId(mStar.getId());
        mRating.setComplex(calculateComplex(mRating));
        StarRatingDao dao = GdbApplication.getInstance().getDaoSession().getStarRatingDao();
        dao.insertOrReplace(mRating);
        dao.detachAll();
        mStar.resetRatings();
        view.showMessage("Save successfully");
    }

    private float calculateComplex(StarRating mRating) {
        return mRating.getBody() * 0.2f
                + mRating.getFace() * 0.18f
                + mRating.getDk() * 0.1f
                + mRating.getSexuality() * 0.25f
                + mRating.getPassion() * 0.17f
                + mRating.getVideo() * 0.1f;
    }

    public void handlePalette(PaletteResponse response) {
        if (response != null && response.palette != null) {
            view.setStarColor(getSuitableColor(response.palette));
            int color = ColorUtils.generateForgroundColorForBg(ColorUtils.averageImageColor(response.bitmap, view.getCloseIconView()));
            ColorUtils.updateIconColor(view.getCloseIconView(), color);
        }
        else {
            view.setStarColor(view.getContext().getResources().getColor(R.color.colorAccent));
        }
    }

    private int getSuitableColor(Palette palette) {
        Palette.Swatch vibrant = palette.getVibrantSwatch();
        if (vibrant == null) {
            Palette.Swatch mute = palette.getMutedSwatch();
            if (mute == null) {
                if (palette.getSwatches() == null || palette.getSwatches().size() == 0) {
                    return view.getContext().getResources().getColor(R.color.colorAccent);
                }
                else {
                    return palette.getSwatches().get(0).getRgb();
                }
            }
            else {
                return mute.getRgb();
            }
        }
        else {
            return vibrant.getRgb();
        }
    }

    public void updateComplex() {
        float complex = calculateComplex(mRating);
        view.showComplex(StarRatingUtil.getRatingValue(complex) + "(" + FormatUtil.formatScore(complex, 2) + ")");
    }

    public interface View {

        void showStar(String name, String imagePath);

        void showRating(StarRating rating);

        void showMessage(String message);

        Context getContext();

        void setStarColor(int color);

        void showComplex(String rating);

        ImageView getCloseIconView();
    }
}
