package com.jing.app.jjgallery.gdb.view.pub.dialog;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.jing.app.jjgallery.gdb.IFragmentHolder;
import com.jing.app.jjgallery.gdb.R;
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

    private int colorStart;

    private int colorAngle;
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
        ftColor.setColorStart(colorStart);
        ftColor.setColorAngle(colorAngle);
        getDialog().setOnDismissListener(onDismissListener);
        return ftColor;
    }

    @Override
    protected int getMinWidth() {
        return ScreenUtils.getScreenWidth(getActivity()) * 1 / 2;
    }

    public void setOnHsvColorListener(OnHsvColorListener onHsvColorListener) {
        this.onHsvColorListener = onHsvColorListener;
    }

    public void setColorStart(int colorStart) {
        this.colorStart = colorStart;
    }

    public void setColorAngle(int colorAngle) {
        this.colorAngle = colorAngle;
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

    public static class ColorFragment extends ContentFragmentV4 {

        @BindView(R.id.et_start)
        EditText etStart;
        @BindView(R.id.et_angle)
        EditText etAngle;

        private int colorStart;

        private int colorAngle;

        private OnHsvColorListener onHsvColorListener;

        @Override
        protected int getLayoutRes() {
            return R.layout.dlg_hsv_color;
        }

        @Override
        protected void initView(View view) {
            ButterKnife.bind(this, view);
            etStart.setText(String.valueOf(colorStart));
            etAngle.setText(String.valueOf(colorAngle));
        }

        @Override
        protected void bindChildFragmentHolder(IFragmentHolder holder) {

        }

        @OnClick(R.id.btn_preview)
        public void onViewClicked() {
            if (onHsvColorListener != null) {
                String start = etStart.getText().toString().trim();
                String angle = etAngle.getText().toString().trim();
                if (!TextUtils.isEmpty(start) && !TextUtils.isEmpty(angle)) {
                    try {
                        onHsvColorListener.onPreviewHsvColor(Integer.parseInt(start), Integer.parseInt(angle));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void setOnHsvColorListener(OnHsvColorListener onHsvColorListener) {
            this.onHsvColorListener = onHsvColorListener;
        }

        public void setColorStart(int colorStart) {
            this.colorStart = colorStart;
        }

        public void setColorAngle(int colorAngle) {
            this.colorAngle = colorAngle;
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
            try {
                if (onHsvColorListener != null) {
                    onHsvColorListener.onSaveColor(Integer.parseInt(start), Integer.parseInt(angle));
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    public interface OnHsvColorListener {
        void onPreviewHsvColor(int start, int angle);
        void onSaveColor(int start, int angle);
    }
}
