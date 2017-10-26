package com.jing.app.jjgallery.gdb.view.record;

import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.jing.app.jjgallery.gdb.IFragmentHolder;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.model.conf.PreferenceValue;
import com.jing.app.jjgallery.gdb.util.ScreenUtils;
import com.jing.app.jjgallery.gdb.view.pub.dialog.DraggableDialogFragmentV4;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/10/26 16:00
 */
public class SortDialogFragment extends DraggableDialogFragmentV4 implements ISortContentHolder {

    private SortFragment ftSort;

    private boolean desc;

    private int sortMode;

    private OnSortListener onSortListener;

    @Override
    protected View getToolbarView(ViewGroup groupToolbar) {
        setTitle(R.string.gdb_sort_title);
        requestOkAction();
        requestCloseAction();
        return null;
    }

    @Override
    protected Fragment getContentViewFragment() {
        ftSort = new SortFragment();
        return ftSort;
    }

    @Override
    protected void onClickOk() {
        super.onClickOk();
        if (ftSort != null) {
            ftSort.onSave();
        }
    }

    @Override
    public boolean isDesc() {
        return desc;
    }

    public void setDesc(boolean desc) {
        this.desc = desc;
    }

    @Override
    public int getSortMode() {
        return sortMode;
    }

    public void setSortMode(int sortMode) {
        this.sortMode = sortMode;
    }

    @Override
    public OnSortListener getOnSortListener() {
        return onSortListener;
    }

    public void setOnSortListener(OnSortListener onSortListener) {
        this.onSortListener = onSortListener;
    }

    public static class SortFragment extends ContentFragmentV4 implements AdapterView.OnItemClickListener {

        @BindView(R.id.gdb_sort_grid)
        GridView gridView;
        @BindView(R.id.cb_deprecated)
        CheckBox cbDeprecated;
        @BindView(R.id.gdb_sort_asc)
        RadioButton gdbSortAsc;
        @BindView(R.id.gdb_sort_desc)
        RadioButton descButton;

        private ISortContentHolder holder;

        private ItemAdapter itemAdapter;

        private SortItem[] items = new SortItem[] {
                new SortItem("None", PreferenceValue.GDB_SR_ORDERBY_NONE)
                , new SortItem("Name", PreferenceValue.GDB_SR_ORDERBY_NAME)
                , new SortItem("Date", PreferenceValue.GDB_SR_ORDERBY_DATE)
                , new SortItem("Score", PreferenceValue.GDB_SR_ORDERBY_SCORE)
                , new SortItem("FK", PreferenceValue.GDB_SR_ORDERBY_FK)
                , new SortItem("Cum", PreferenceValue.GDB_SR_ORDERBY_CUM)
                , new SortItem("Star1", PreferenceValue.GDB_SR_ORDERBY_STAR1)
                , new SortItem("StarC1", PreferenceValue.GDB_SR_ORDERBY_STARCC1)
                , new SortItem("Bjob", PreferenceValue.GDB_SR_ORDERBY_BJOB)
                , new SortItem("Star2", PreferenceValue.GDB_SR_ORDERBY_STAR2)
                , new SortItem("StarC2", PreferenceValue.GDB_SR_ORDERBY_STARCC2)
                , new SortItem("Bareback", PreferenceValue.GDB_SR_ORDERBY_BAREBACK)
                , new SortItem("ScoreFeel", PreferenceValue.GDB_SR_ORDERBY_SCOREFEEL)
                , new SortItem("Story", PreferenceValue.GDB_SR_ORDERBY_STORY)
                , new SortItem("Foreplay", PreferenceValue.GDB_SR_ORDERBY_FOREPLAY)
                , new SortItem("Rim", PreferenceValue.GDB_SR_ORDERBY_RIM)
                , new SortItem("Rhythm", PreferenceValue.GDB_SR_ORDERBY_RHYTHM)
                , new SortItem("Scene", PreferenceValue.GDB_SR_ORDERBY_SCENE)
                , new SortItem("CShow", PreferenceValue.GDB_SR_ORDERBY_CSHOW)
                , new SortItem("Special", PreferenceValue.GDB_SR_ORDERBY_SPECIAL)
                , new SortItem("HD Level", PreferenceValue.GDB_SR_ORDERBY_HD)
                , new SortItem("FK-sit face", PreferenceValue.GDB_SR_ORDERBY_FK1)
                , new SortItem("FK-sit back", PreferenceValue.GDB_SR_ORDERBY_FK2)
                , new SortItem("FK-stand face", PreferenceValue.GDB_SR_ORDERBY_FK3)
                , new SortItem("FK-stand back", PreferenceValue.GDB_SR_ORDERBY_FK4)
                , new SortItem("FK-side", PreferenceValue.GDB_SR_ORDERBY_FK5)
                , new SortItem("FK-special", PreferenceValue.GDB_SR_ORDERBY_FK6)
                , new SortItem("ScoreBasic", PreferenceValue.GDB_SR_ORDERBY_SCORE_BASIC)
                , new SortItem("ScoreExtra", PreferenceValue.GDB_SR_ORDERBY_SCORE_EXTRA)
                , new SortItem("Star", PreferenceValue.GDB_SR_ORDERBY_STAR)
                , new SortItem("StarC", PreferenceValue.GDB_SR_ORDERBY_STARC)
                , new SortItem("File date", PreferenceValue.GDB_SR_ORDERBY_TIME)
                , new SortItem("File Size", PreferenceValue.GDB_SR_ORDERBY_SIZE)
        };

        private int textPadding;
        private int focusColor;

        @Override
        protected int getLayoutRes() {
            return R.layout.dlg_gdb_sort;
        }

        @Override
        protected void initView(View view) {
            ButterKnife.bind(this, view);
            textPadding = ScreenUtils.dp2px(getContext(), 20);
            focusColor = getResources().getColor(R.color.actionbar_bk_orange);

            itemAdapter = new ItemAdapter();
            // 初始化升序/降序
            if (!holder.isDesc()) {
                gdbSortAsc.setChecked(true);
            }
            // 初始化当前排序类型
            for (int i = 0; i < items.length; i ++) {
                if (holder.getSortMode() == items[i].value) {
                    itemAdapter.setSelection(i);
                    break;
                }
            }

            gridView.setAdapter(itemAdapter);
            gridView.setOnItemClickListener(this);
        }

        @Override
        protected void bindChildFragmentHolder(IFragmentHolder holder) {
            if (holder instanceof ISortContentHolder) {
                this.holder = (ISortContentHolder) holder;
            }
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            itemAdapter.setSelection(position);
            itemAdapter.notifyDataSetChanged();
        }

        public void onSave() {

            if (holder.getOnSortListener() != null) {
                holder.getOnSortListener().onSort(descButton.isChecked(), items[itemAdapter.getSelectedIndex()].value, cbDeprecated.isChecked());
            }
        }

        private class SortItem {
            String name;
            int value;
            public SortItem(String name, int value) {
                this.name = name;
                this.value = value;
            }
        }

        private class ItemAdapter extends BaseAdapter {

            private int selectedIndex;

            public void setSelection(int position) {
                selectedIndex = position;
            }

            public int getSelectedIndex() {
                return selectedIndex;
            }

            @Override
            public int getCount() {
                return items.length;
            }

            @Override
            public Object getItem(int position) {
                return items[position];
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = new TextView(getContext());
                textView.setText(items[position].name);
                textView.setGravity(Gravity.CENTER);
                textView.setPadding(0, textPadding, 0, textPadding);
                if (position == selectedIndex) {
                    textView.setBackgroundColor(focusColor);
                }
                else {
                    textView.setBackground(null);
                }
                return textView;
            }
        }
    }

    public interface OnSortListener {
        void onSort(boolean desc, int sortMode, boolean isIncludeDeprecated);
    }
}
