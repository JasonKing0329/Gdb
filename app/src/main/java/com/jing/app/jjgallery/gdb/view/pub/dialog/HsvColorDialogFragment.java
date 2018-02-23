package com.jing.app.jjgallery.gdb.view.pub.dialog;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.jing.app.jjgallery.gdb.IFragmentHolder;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.model.bean.HsvColorBean;
import com.jing.app.jjgallery.gdb.util.ScreenUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/10/26 15:14
 */
public class HsvColorDialogFragment extends DraggableDialogFragmentV4 {

    private ColorFragment ftColor;

    private OnHsvColorListener onHsvColorListener;

    private HsvColorBean hsvColorBean;

    private DialogInterface.OnDismissListener onDismissListener;

    @Override
    protected View getToolbarView(ViewGroup groupToolbar) {
        requestSaveAction();
        requestCloseAction();
        setTitle("Color");
        return null;
    }

    @Override
    protected Fragment getContentViewFragment() {
        ftColor = new ColorFragment();
        ftColor.setOnHsvColorListener(onHsvColorListener);
        ftColor.setHsvColorBean(hsvColorBean);
        getDialog().setOnDismissListener(onDismissListener);
        return ftColor;
    }

    @Override
    protected int getMinWidth() {
        return ScreenUtils.getScreenWidth(getActivity()) * 1 / 2;
    }

    @Override
    protected int getMaxHeight() {
        return ScreenUtils.getScreenHeight(getActivity()) * 2 / 3;
    }

    public void setOnHsvColorListener(OnHsvColorListener onHsvColorListener) {
        this.onHsvColorListener = onHsvColorListener;
    }

    @Override
    protected void onClickSave() {
        super.onClickSave();
        if (ftColor != null) {
            ftColor.onClickSave();
        }
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public void setHsvColorBean(HsvColorBean hsvColorBean) {
        this.hsvColorBean = hsvColorBean;
    }

    public static class ColorFragment extends ContentFragmentV4 {

        @BindView(R.id.et_start)
        EditText etStart;
        @BindView(R.id.et_angle)
        EditText etAngle;
        @BindView(R.id.et_s)
        EditText etS;
        @BindView(R.id.et_v)
        EditText etV;
        @BindView(R.id.cb_stable)
        CheckBox cbSv;
        @BindView(R.id.group_sv)
        ViewGroup groupSv;

        private HsvColorBean hsvColorBean;

        private OnHsvColorListener onHsvColorListener;

        @Override
        protected int getLayoutRes() {
            return R.layout.dlg_hsv_color;
        }

        @Override
        protected void initView(View view) {
            ButterKnife.bind(this, view);
            if (hsvColorBean == null) {
                hsvColorBean = new HsvColorBean();
            }
            etStart.setText(String.valueOf(hsvColorBean.gethStart()));
            etAngle.setText(String.valueOf(hsvColorBean.gethArg()));
            etS.setText(String.valueOf(hsvColorBean.getS()));
            etV.setText(String.valueOf(hsvColorBean.getV()));
            if (hsvColorBean.getS() >=0 || hsvColorBean.getV() >= 0) {
                groupSv.setVisibility(View.VISIBLE);
                cbSv.setChecked(true);
            }
            else {
                groupSv.setVisibility(View.INVISIBLE);
            }
            cbSv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean check) {
                    groupSv.setVisibility(check ? View.VISIBLE:View.INVISIBLE);
                }
            });
        }

        @Override
        protected void bindChildFragmentHolder(IFragmentHolder holder) {

        }

        @OnClick(R.id.btn_preview)
        public void onViewClicked() {
            if (onHsvColorListener != null) {
                String start = etStart.getText().toString().trim();
                if (TextUtils.isEmpty(start)) {
                    return;
                }
                String angle = etAngle.getText().toString().trim();
                if (TextUtils.isEmpty(angle)) {
                    return;
                }
                hsvColorBean.sethStart(Integer.parseInt(start));
                hsvColorBean.sethArg(Integer.parseInt(angle));

                if (groupSv.getVisibility() == View.VISIBLE) {
                    String s = etS.getText().toString().trim();
                    if (TextUtils.isEmpty(s)) {
                        return;
                    }
                    String v = etV.getText().toString().trim();
                    if (TextUtils.isEmpty(v)) {
                        return;
                    }
                    hsvColorBean.setS(Float.parseFloat(s));
                    hsvColorBean.setV(Float.parseFloat(v));
                }

                try {
                    onHsvColorListener.onPreviewHsvColor(hsvColorBean);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }

        public void setOnHsvColorListener(OnHsvColorListener onHsvColorListener) {
            this.onHsvColorListener = onHsvColorListener;
        }

        public void onClickSave() {
            String start = etStart.getText().toString().trim();
            if (TextUtils.isEmpty(start)) {
                return;
            }
            String angle = etAngle.getText().toString().trim();
            if (TextUtils.isEmpty(angle)) {
                return;
            }
            hsvColorBean.sethStart(Integer.parseInt(start));
            hsvColorBean.sethArg(Integer.parseInt(angle));

            if (groupSv.getVisibility() == View.VISIBLE) {
                String s = etS.getText().toString().trim();
                if (TextUtils.isEmpty(s)) {
                    return;
                }
                String v = etV.getText().toString().trim();
                if (TextUtils.isEmpty(v)) {
                    return;
                }
                hsvColorBean.setS(Float.parseFloat(s));
                hsvColorBean.setV(Float.parseFloat(v));
            }
            try {
                if (onHsvColorListener != null) {
                    onHsvColorListener.onSaveColor(hsvColorBean);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        public void setHsvColorBean(HsvColorBean hsvColorBean) {
            this.hsvColorBean = hsvColorBean;
        }
    }

    public interface OnHsvColorListener {
        void onPreviewHsvColor(HsvColorBean hsvColorBean);
        void onSaveColor(HsvColorBean hsvColorBean);
    }
}
