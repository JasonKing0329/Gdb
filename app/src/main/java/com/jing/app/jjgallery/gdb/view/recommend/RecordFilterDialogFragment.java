package com.jing.app.jjgallery.gdb.view.recommend;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

import com.jing.app.jjgallery.gdb.IFragmentHolder;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.model.SettingProperties;
import com.jing.app.jjgallery.gdb.model.bean.recommend.FilterModel;
import com.jing.app.jjgallery.gdb.util.LMBannerViewUtil;
import com.jing.app.jjgallery.gdb.util.ScreenUtils;
import com.jing.app.jjgallery.gdb.view.pub.dialog.DraggableDialogFragmentV4;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/10/26 9:19
 */
public class RecordFilterDialogFragment extends DraggableDialogFragmentV4 implements IFilterContentHolder {

    private FilterFragment ftFilter;

    private FilterModel filterModel;

    private OnRecordFilterActionListener onRecordFilterActionListener;
    private DialogInterface.OnDismissListener onDismissListener;

    @Override
    protected View getToolbarView(ViewGroup groupToolbar) {
        requestCloseAction();
        requestSaveAction();
        setTitle(getContext().getString(R.string.gdb_rec_filter_title));
        return null;
    }

    @Override
    protected Fragment getContentViewFragment() {
        ftFilter = new FilterFragment();
        if (onDismissListener != null) {
            getDialog().setOnDismissListener(onDismissListener);
        }
        return ftFilter;
    }

    public void setmFilterModel(FilterModel filterModel) {
        this.filterModel = filterModel;
    }

    @Override
    public FilterModel getFilterModel() {
        return filterModel;
    }

    @Override
    public void onSaveFilterModel() {
        if (onRecordFilterActionListener != null) {
            onRecordFilterActionListener.onSaveFilterModel(filterModel);
        }
    }

    @Override
    protected void onClickSave() {
        if (ftFilter != null) {
            ftFilter.onSave();
        }
        super.onClickSave();
    }

    public void setOnRecordFilterActionListener(OnRecordFilterActionListener onRecordFilterActionListener) {
        this.onRecordFilterActionListener = onRecordFilterActionListener;
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    /**
     * 覆盖父类
     * @return
     */
    protected int getMaxHeight() {
        return ScreenUtils.getScreenHeight(getActivity()) * 4 / 5;
    }

    public static class FilterFragment extends ContentFragmentV4 {

        @BindView(R.id.cb_nr)
        CheckBox cbNr;
        @BindView(R.id.recommend_list)
        RecyclerView recyclerView;
        @BindView(R.id.rb_random)
        RadioButton rbRandom;
        @BindView(R.id.rb_fixed)
        RadioButton rbFix;
        @BindView(R.id.et_time)
        EditText etTime;

        private IFilterContentHolder holder;

        private FilterAdapter mAdapter;

        @Override
        protected int getLayoutRes() {
            return R.layout.gdb_recommend_filter;
        }

        @Override
        protected void initView(View view) {
            ButterKnife.bind(this, view);

            cbNr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    holder.getFilterModel().setSupportNR(isChecked);
                }
            });
            rbFix.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAnimationSelector();
                }
            });
            rbRandom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked) {
                        SettingProperties.setGdbRecmmendAnimRandom(getContext(), true);
                    }
                }
            });
            rbFix.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked) {
                        SettingProperties.setGdbRecmmendAnimRandom(getContext(), false);
                    }
                }
            });
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            DefaultItemAnimator animator = new DefaultItemAnimator();
            recyclerView.setItemAnimator(animator);

            boolean isRandom = SettingProperties.isGdbRecmmendAnimRandom(getContext());
            if (isRandom) {
                rbRandom.setChecked(true);
                rbFix.setText("Fixed");
            }
            else {
                rbFix.setChecked(true);
                try {
                    rbFix.setText(formatFixedText(LMBannerViewUtil.ANIM_TYPES[SettingProperties.getGdbRecommendAnimType(getContext())]));
                } catch (Exception e) {
                    e.printStackTrace();
                    rbFix.setText(formatFixedText(LMBannerViewUtil.ANIM_TYPES[0]));
                }
            }

            etTime.setText(String.valueOf(SettingProperties.getGdbRecommendAnimTime(getContext())));

            cbNr.setChecked(holder.getFilterModel().isSupportNR());
            mAdapter = new FilterAdapter(holder.getFilterModel().getList());
            recyclerView.setAdapter(mAdapter);
        }

        @Override
        protected void bindChildFragmentHolder(IFragmentHolder holder) {
            if (holder instanceof IFilterContentHolder) {
                this.holder = (IFilterContentHolder) holder;
            }
        }

        private String formatFixedText(String type) {
            return "Fixed (" + type + ")";
        }

        private void showAnimationSelector() {
            new AlertDialog.Builder(getContext())
                    .setTitle(null)
                    .setItems(LMBannerViewUtil.ANIM_TYPES, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            rbFix.setText(formatFixedText(LMBannerViewUtil.ANIM_TYPES[which]));
                            SettingProperties.setGdbRecommendAnimType(getContext(), which);
                        }
                    }).show();
        }

        public void onSave() {
            int time;
            try {
                time = Integer.parseInt(etTime.getText().toString());
            } catch (Exception e) {
                time = 0;
            }
            // 至少2S
            if (time < 2000) {
                time = 2000;
            }
            SettingProperties.setGdbRecommendAnimTime(getContext(), time);
            holder.onSaveFilterModel();
        }
    }

    public interface OnRecordFilterActionListener {
        void onSaveFilterModel(FilterModel model);
    }
}