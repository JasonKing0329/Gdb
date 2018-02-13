package com.jing.app.jjgallery.gdb.view.game;

import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.jing.app.jjgallery.gdb.IFragmentHolder;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.presenter.game.RandomNumPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/11/7 14:08
 */
public class RandomNumFragment extends BaseRandomFragment implements IRandomNumView {

    @BindView(R.id.et_random_num)
    EditText etRandomNum;
    @BindView(R.id.et_random_min)
    EditText etRandomMin;
    @BindView(R.id.et_random_max)
    EditText etRandomMax;
    @BindView(R.id.cb_only)
    CheckBox cbOnly;
    @BindView(R.id.tv_result)
    TextView tvResult;

    private RandomNumPresenter presenter;

    @Override
    public String getTitle() {
        return "Number";
    }

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_random_num;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);

        presenter = new RandomNumPresenter(this);
    }

    @OnClick({R.id.btn_clear, R.id.btn_random})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_clear:
                tvResult.setText("");
                break;
            case R.id.btn_random:
                executeRandom();
                break;
        }
    }

    private void executeRandom() {
        presenter.randomNum(etRandomNum.getText().toString(), etRandomMin.getText().toString(), etRandomMax.getText().toString()
            , cbOnly.isChecked());
    }

    @Override
    public void onErrorMessage(String msg) {
        showToastLong(msg);
    }

    @Override
    public void showRandomResult(String result) {
        tvResult.setText(result);
    }
}
