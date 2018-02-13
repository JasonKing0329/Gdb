package com.jing.app.jjgallery.gdb.view.game;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;

import com.jing.app.jjgallery.gdb.ActivityManager;
import com.jing.app.jjgallery.gdb.IFragmentHolder;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.model.bean.RandomStarBean;
import com.jing.app.jjgallery.gdb.presenter.game.RandomStarPresenter;
import com.jing.app.jjgallery.gdb.util.DisplayHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/11/7 14:12
 */
public class RandomStarFragment extends BaseRandomFragment implements IRandomStarView, RandomStarAdapter.OnStarItemListener {

    @BindView(R.id.radio_from_all)
    RadioButton radioFromAll;
    @BindView(R.id.radio_from_favor)
    RadioButton radioFromFavor;
    @BindView(R.id.cb_type_all)
    CheckBox cbTypeAll;
    @BindView(R.id.cb_type_1)
    CheckBox cbType1;
    @BindView(R.id.cb_type_0)
    CheckBox cbType0;
    @BindView(R.id.cb_type_half)
    CheckBox cbTypeHalf;
    @BindView(R.id.et_random_num)
    EditText etRandomNum;
    @BindView(R.id.cb_only)
    CheckBox cbOnly;
    @BindView(R.id.rv_stars)
    RecyclerView rvStars;

    private RandomStarAdapter adapter;
    private RandomStarPresenter presenter;
    private GridLayoutManager layoutManager;

    @Override
    public String getTitle() {
        return "Stars";
    }

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_random_star;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);

        presenter = new RandomStarPresenter(this);
    }

    @OnClick({R.id.btn_clear, R.id.btn_random})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_clear:
                break;
            case R.id.btn_random:
                presenter.random(radioFromAll.isChecked(), radioFromFavor.isChecked()
                    , cbTypeAll.isChecked(), cbType1.isChecked(), cbType0.isChecked(), cbTypeHalf.isChecked()
                    , etRandomNum.getText().toString(), cbOnly.isChecked());
                break;
        }
    }

    @OnClick({R.id.cb_type_all, R.id.cb_type_0, R.id.cb_type_1, R.id.cb_type_half})
    public void onCheckBoxClicked(View view) {
        switch (view.getId()) {
            case R.id.cb_type_all:
                if (cbTypeAll.isChecked()) {
                    cbType0.setChecked(true);
                    cbType1.setChecked(true);
                    cbTypeHalf.setChecked(true);
                }
                else {
                    cbType0.setChecked(false);
                    cbType1.setChecked(false);
                    cbTypeHalf.setChecked(false);
                }
                break;
            case R.id.cb_type_0:
                if (cbType0.isChecked()) {
                    if (cbType1.isChecked() && cbTypeHalf.isChecked()) {
                        cbTypeAll.setChecked(true);
                    }
                }
                else {
                    cbTypeAll.setChecked(false);
                }
                break;
            case R.id.cb_type_1:
                if (cbType1.isChecked()) {
                    if (cbType0.isChecked() && cbTypeHalf.isChecked()) {
                        cbTypeAll.setChecked(true);
                    }
                }
                else {
                    cbTypeAll.setChecked(false);
                }
                break;
            case R.id.cb_type_half:
                if (cbTypeHalf.isChecked()) {
                    if (cbType1.isChecked() && cbType0.isChecked()) {
                        cbTypeAll.setChecked(true);
                    }
                }
                else {
                    cbTypeAll.setChecked(false);
                }
                break;
        }
    }

    @Override
    public void onErrorMessage(String msg) {
        showToastLong(msg);
    }

    @Override
    public void onRandomStar(List<RandomStarBean> starList) {
        int maxSpan = DisplayHelper.isTabModel(getActivity()) ? 4:3;
        int span = starList.size() > maxSpan ? maxSpan:starList.size();
        if (layoutManager == null) {
            layoutManager = new GridLayoutManager(getActivity(), span);
            rvStars.setLayoutManager(layoutManager);
        }
        else {
            layoutManager.setSpanCount(span);
        }
        if (adapter == null) {
            adapter = new RandomStarAdapter(maxSpan);
            adapter.setStarList(starList);
            adapter.setOnStarItemListener(this);
            rvStars.setAdapter(adapter);
        }
        else {
            adapter.setStarList(starList);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClickStar(RandomStarBean bean) {
        ActivityManager.startStarActivity(getActivity(), bean.getStarId());
    }
}
