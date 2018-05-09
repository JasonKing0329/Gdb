package com.jing.app.jjgallery.gdb.view.pub.dialog;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.model.palette.PaletteCallback;
import com.jing.app.jjgallery.gdb.model.palette.PaletteRequestListener;
import com.jing.app.jjgallery.gdb.model.palette.PaletteResponse;
import com.jing.app.jjgallery.gdb.presenter.star.StarRatingPresenter;
import com.jing.app.jjgallery.gdb.util.GlideApp;
import com.jing.app.jjgallery.gdb.util.StarRatingUtil;
import com.jing.app.jjgallery.gdb.view.pub.StarRatingView;
import com.king.app.gdb.data.entity.StarRating;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/5/8 17:03
 */
public class StarRatingDialog extends BaseDialogFragmentV4 implements StarRatingPresenter.View, StarRatingView.OnStarChangeListener {

    @BindView(R.id.iv_star)
    ImageView ivStar;
    @BindView(R.id.tv_star)
    TextView tvStar;
    @BindView(R.id.tv_rating)
    TextView tvRating;
    @BindView(R.id.star_face)
    StarRatingView starFace;
    @BindView(R.id.tv_face)
    TextView tvFace;
    @BindView(R.id.star_body)
    StarRatingView starBody;
    @BindView(R.id.tv_body)
    TextView tvBody;
    @BindView(R.id.star_dk)
    StarRatingView starDk;
    @BindView(R.id.tv_dk)
    TextView tvDk;
    @BindView(R.id.star_sex)
    StarRatingView starSex;
    @BindView(R.id.tv_sex)
    TextView tvSex;
    @BindView(R.id.star_passion)
    StarRatingView starPassion;
    @BindView(R.id.tv_passion)
    TextView tvPassion;
    @BindView(R.id.star_video)
    StarRatingView starVideo;
    @BindView(R.id.tv_video)
    TextView tvVideo;

    private long starId;

    private StarRatingPresenter presenter;

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_star_rating;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);

        starFace.setOnStarChangeListener(this);
        starBody.setOnStarChangeListener(this);
        starDk.setOnStarChangeListener(this);
        starPassion.setOnStarChangeListener(this);
        starVideo.setOnStarChangeListener(this);
        starSex.setOnStarChangeListener(this);

        presenter = new StarRatingPresenter(this);
        presenter.loadStarRating(starId);
    }

    public void setStarId(long starId) {
        this.starId = starId;
    }

    @Override
    public void showStar(String name, String imagePath) {
        tvStar.setText(name);

        GlideApp.with(getContext())
                .asBitmap()
                .load(imagePath)
                .listener(new PaletteRequestListener(0, new PaletteCallback() {
                    @Override
                    public List<View> getTargetViews() {
                        return null;
                    }

                    @Override
                    public void noPaletteResponseLoaded(int position) {

                    }

                    @Override
                    public void onPaletteResponse(int position, PaletteResponse response) {
                        presenter.handlePalette(response);
                    }
                }))
                .error(R.drawable.ic_def_person_wide)
                .into(ivStar);
    }

    @Override
    public void showRating(StarRating rating) {
        starFace.setCheckNumber(rating.getFace());
        tvFace.setText(StarRatingUtil.getRatingValue(rating.getFace()));
        starBody.setCheckNumber(rating.getBody());
        tvBody.setText(StarRatingUtil.getRatingValue(rating.getBody()));
        starDk.setCheckNumber(rating.getDk());
        tvDk.setText(StarRatingUtil.getRatingValue(rating.getDk()));
        starSex.setCheckNumber(rating.getSexuality());
        tvSex.setText(StarRatingUtil.getRatingValue(rating.getSexuality()));
        starPassion.setCheckNumber(rating.getPassion());
        tvPassion.setText(StarRatingUtil.getRatingValue(rating.getPassion()));
        starVideo.setCheckNumber(rating.getVideo());
        tvVideo.setText(StarRatingUtil.getRatingValue(rating.getVideo()));
    }

    @Override
    public void onStarChanged(StarRatingView view, float checkedStar) {
        String rateValue = StarRatingUtil.getRatingValue(checkedStar);
        switch (view.getId()) {
            case R.id.star_face:
                presenter.getRating().setFace(checkedStar);
                tvFace.setText(rateValue);
                break;
            case R.id.star_body:
                presenter.getRating().setBody(checkedStar);
                tvBody.setText(rateValue);
                break;
            case R.id.star_dk:
                presenter.getRating().setDk(checkedStar);
                tvDk.setText(rateValue);
                break;
            case R.id.star_passion:
                presenter.getRating().setPassion(checkedStar);
                tvPassion.setText(rateValue);
                break;
            case R.id.star_video:
                presenter.getRating().setVideo(checkedStar);
                tvVideo.setText(rateValue);
                break;
            case R.id.star_sex:
                presenter.getRating().setSexuality(checkedStar);
                tvSex.setText(rateValue);
                break;
        }
        presenter.updateComplex();
    }

    @OnClick(R.id.tv_save)
    public void onViewClicked() {
        presenter.saveRating();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setStarColor(int color) {
        starVideo.setStarColor(color);
        starSex.setStarColor(color);
        starPassion.setStarColor(color);
        starDk.setStarColor(color);
        starFace.setStarColor(color);
        starBody.setStarColor(color);
    }

    @Override
    public void showComplex(String rating) {
        tvRating.setText(rating);
    }
}
