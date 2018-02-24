package com.jing.app.jjgallery.gdb.view.record;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jing.app.jjgallery.gdb.IFragmentHolder;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.model.bean.FilterBean;
import com.jing.app.jjgallery.gdb.view.pub.dialog.DraggableDialogFragmentV4;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/2/24 15:06
 */
public class FilterDialogFragment extends DraggableDialogFragmentV4 {

    private FilterFragment ftFilter;

    private FilterBean filterBean;

    private OnFilterListener onFilterListener;

    @Override
    protected View getToolbarView(ViewGroup groupToolbar) {
        setTitle("Filter");
        requestOkAction();
        requestCloseAction();
        return null;
    }

    @Override
    protected Fragment getContentViewFragment() {
        ftFilter = new FilterFragment();
        ftFilter.setFilterBean(filterBean);
        ftFilter.setOnFilterListener(onFilterListener);
        return ftFilter;
    }

    public void setFilterBean(FilterBean filterBean) {
        this.filterBean = filterBean;
    }

    @Override
    protected void onClickOk() {
        ftFilter.onSave();
    }

    public void setOnFilterListener(OnFilterListener onFilterListener) {
        this.onFilterListener = onFilterListener;
    }

    public static class FilterFragment extends ContentFragmentV4 {

        @BindView(R.id.tv_tag_bareback)
        TextView tvTagBareback;
        @BindView(R.id.tv_tag_inner)
        TextView tvTagInner;
        @BindView(R.id.tv_tag_nd)
        TextView tvTagNd;

        Unbinder unbinder;

        private FilterBean filterBean;

        private OnFilterListener onFilterListener;

        @Override
        protected int getLayoutRes() {
            return R.layout.dialog_filter;
        }

        @Override
        protected void initView(View view) {
            unbinder = ButterKnife.bind(this, view);

            if (filterBean == null) {
                filterBean = new FilterBean();
            }
            else {
                if (filterBean.isBareback()) {
                    tvTagBareback.setSelected(true);
                }
                if (filterBean.isInnerCum()) {
                    tvTagInner.setSelected(true);
                }
                if (filterBean.isNotDeprecated()) {
                    tvTagNd.setSelected(true);
                }
            }
        }

        @Override
        protected void bindChildFragmentHolder(IFragmentHolder holder) {

        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            if (unbinder != null) {
                unbinder.unbind();
            }
        }

        public void setFilterBean(FilterBean filterBean) {
            this.filterBean = filterBean;
        }

        @OnClick({R.id.tv_tag_bareback, R.id.tv_tag_inner, R.id.tv_tag_nd})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.tv_tag_bareback:
                    tvTagBareback.setSelected(!tvTagBareback.isSelected());
                    filterBean.setBareback(tvTagBareback.isSelected());
                    break;
                case R.id.tv_tag_inner:
                    tvTagInner.setSelected(!tvTagInner.isSelected());
                    filterBean.setInnerCum(tvTagInner.isSelected());
                    break;
                case R.id.tv_tag_nd:
                    tvTagNd.setSelected(!tvTagNd.isSelected());
                    filterBean.setNotDeprecated(tvTagNd.isSelected());
                    break;
            }
        }

        public void onSave() {
            if (onFilterListener != null) {
                onFilterListener.onFilter(filterBean);
            }
        }

        public void setOnFilterListener(OnFilterListener onFilterListener) {
            this.onFilterListener = onFilterListener;
        }
    }

    public interface OnFilterListener {
        void onFilter(FilterBean bean);
    }
}
