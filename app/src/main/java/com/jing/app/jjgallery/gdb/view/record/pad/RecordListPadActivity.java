package com.jing.app.jjgallery.gdb.view.record.pad;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jing.app.jjgallery.gdb.ActivityManager;
import com.jing.app.jjgallery.gdb.FavorPopupMvpActivity;
import com.jing.app.jjgallery.gdb.GdbConstants;
import com.jing.app.jjgallery.gdb.MvpActivity;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.model.bean.FilterBean;
import com.jing.app.jjgallery.gdb.view.record.IRecordListHolder;
import com.jing.app.jjgallery.gdb.view.record.IRecordSceneHolder;
import com.jing.app.jjgallery.gdb.view.record.RecordSceneFragment;
import com.jing.app.jjgallery.gdb.view.record.RecordsListFragment;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.param.DataConstants;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @desc
 * @auth 景阳
 * @time 2018/2/15 0015 08:31
 */

public class RecordListPadActivity extends FavorPopupMvpActivity<RecordListPadPresenter> implements RecordListPadView, IRecordSceneHolder, IRecordListHolder {

    public static final String KEY_SCENE_NAME = "key_scene_name";

    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.tv_scene)
    TextView tvScene;
    @BindView(R.id.group_search)
    RelativeLayout groupSearch;
    @BindView(R.id.rg_tag)
    RadioGroup rgTag;

    private RecordSceneFragment ftScene;
    private RecordsListFragment ftRecords;

    private String mTitle;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        String scene = intent.getStringExtra(KEY_SCENE_NAME);
        if (!TextUtils.isEmpty(scene)) {
            tvScene.setText(scene);
            ftScene.focusToScene(scene);
            ftRecords.setScene(scene);
            ftRecords.loadNewRecords();
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_record_list_pad;
    }

    @Override
    protected void initView() {
        String scene = getIntent().getStringExtra(KEY_SCENE_NAME);
        groupSearch.setVisibility(View.INVISIBLE);

        ftScene = new RecordSceneFragment();
        if (!TextUtils.isEmpty(scene)) {
            tvScene.setText(scene);
            ftScene.setFocusScene(scene);
        } else {
            tvScene.setText(GdbConstants.KEY_SCENE_ALL);
        }
        mTitle = tvScene.getText().toString();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.group_scenes, ftScene, "RecordSceneFragment")
                .commit();

        ftRecords = new RecordsListFragment();
        if (!TextUtils.isEmpty(scene)) {
            ftRecords.setScene(scene);
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.group_records, ftRecords, "RecordsListFragment")
                .commit();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence text, int i, int i1, int i2) {
                ftRecords.filterRecord(text.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        rgTag.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                switch (id) {
                    case R.id.rb_tag_all:
                        ftScene.loadByType(0);
                        ftRecords.setRecordType(0);
                        break;
                    case R.id.rb_tag_1v1:
                        ftScene.loadByType(DataConstants.VALUE_RECORD_TYPE_1V1);
                        ftRecords.setRecordType(DataConstants.VALUE_RECORD_TYPE_1V1);
                        break;
                    case R.id.rb_tag_3w:
                        ftScene.loadByType(DataConstants.VALUE_RECORD_TYPE_3W);
                        ftRecords.setRecordType(DataConstants.VALUE_RECORD_TYPE_3W);
                        break;
                    case R.id.rb_tag_multi:
                        ftScene.loadByType(DataConstants.VALUE_RECORD_TYPE_MULTI);
                        ftRecords.setRecordType(DataConstants.VALUE_RECORD_TYPE_MULTI);
                        break;
                }
                ftRecords.loadNewRecords();
            }
        });
    }

    @Override
    protected RecordListPadPresenter createPresenter() {
        return new RecordListPadPresenter();
    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.iv_icon_back, R.id.iv_icon_close, R.id.iv_icon_search, R.id.iv_icon_sort, R.id.iv_icon_favor
            , R.id.iv_icon_sort_scene, R.id.iv_icon_color, R.id.iv_icon_filter})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_icon_back:
                finish();
                break;
            case R.id.iv_icon_close:
                closeSearch();
                break;
            case R.id.iv_icon_search:
                if (groupSearch.getVisibility() != View.VISIBLE) {
                    showSearchLayout();
                }
                break;
            case R.id.iv_icon_sort:
                ftRecords.changeSortType();
                break;
            case R.id.iv_icon_sort_scene:
                showSortPopup(view);
                break;
            case R.id.iv_icon_color:
                ftScene.editColor();
                break;
            case R.id.iv_icon_filter:
                ftRecords.changeFilter();
                break;
            case R.id.iv_icon_favor:
                ActivityManager.startRecordOrderActivity(this);
                finish();
                break;
        }
    }

    @Override
    public void updateFilter(FilterBean bean) {
        if (bean != null) {
            StringBuffer buffer = new StringBuffer();
            if (bean.isBareback()) {
                buffer.append(", ").append("Bareback");
            }
            if (bean.isInnerCum()) {
                buffer.append(", ").append("Inner cum");
            }
            if (bean.isNotDeprecated()) {
                buffer.append(", ").append("Not deprecated");
            }
            String title = buffer.toString();
            if (title.length() > 2) {
                title = title.substring(2);
                tvScene.setText(mTitle + " (" + title + ")");
            } else {
                tvScene.setText(mTitle);
            }
        } else {
            tvScene.setText(mTitle);
        }
    }

    public void showSortPopup(View anchor) {
        PopupMenu menu = new PopupMenu(this, anchor);
        menu.getMenuInflater().inflate(R.menu.sort_gdb_record_scene, menu.getMenu());
        menu.show();
        menu.setOnMenuItemClickListener(sortListener);
    }

    PopupMenu.OnMenuItemClickListener sortListener = new PopupMenu.OnMenuItemClickListener() {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_sort_by_name:
                    ftScene.sortByName();
                    break;
                case R.id.menu_sort_by_avg:
                    ftScene.sortByAvg();
                    break;
                case R.id.menu_sort_by_number:
                    ftScene.sortByNumber();
                    break;
                case R.id.menu_sort_by_max:
                    ftScene.sortByMax();
                    break;
            }
            return true;
        }
    };

    /**
     * hide search layout
     */
    public void closeSearch() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.disappear);
        groupSearch.startAnimation(animation);
        groupSearch.setVisibility(View.INVISIBLE);
    }

    /**
     * show search layout
     */
    private void showSearchLayout() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.appear);
        groupSearch.startAnimation(animation);
        groupSearch.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSelectScene(String scene) {
        mTitle = scene;
        tvScene.setText(scene);
        ftRecords.setScene(scene);
        ftRecords.loadNewRecords();
    }

    @Override
    public void showRecordPopup(View v, Record record) {
        getFavorPopup().popupRecord(this, v, record.getId());
    }
}
