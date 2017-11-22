package com.jing.app.jjgallery.gdb.view.record;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.jing.app.jjgallery.gdb.GBaseActivity;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.presenter.record.RecordListPresenter;
import com.jing.app.jjgallery.gdb.view.pub.ActionBar;

import butterknife.Unbinder;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/7/12 11:20
 */
public class RecordListActivity extends GBaseActivity implements IRecordListHolder {

    private RecordListPresenter recordPresenter;
    private RecordsListFragment recordFragment;

    private ActionBar actionBar;
    
    @Override
    public int getContentView() {
        getSupportActionBar().hide();
        return R.layout.activity_gdb_record_list;
    }


    @Override
    protected void initController() {
        recordPresenter = new RecordListPresenter();
    }

    @Override
    protected Unbinder initView() {
        initActionbar();
        onRecordListPage();
        return null;
    }

    @Override
    protected void initBackgroundWork() {

    }

    private void initActionbar() {
        actionBar = new ActionBar(this, findViewById(R.id.group_actionbar));
        actionBar.setActionIconListener(iconListener);
        actionBar.setActionMenuListener(menuListener);
        actionBar.setActionSearchListener(searchListener);
        actionBar.clearActionIcon();
        actionBar.addSortIcon();
        actionBar.addSearchIcon();
        actionBar.addShowIcon();
        actionBar.addMenuIcon();
        actionBar.addBackIcon();
        actionBar.addPlayIcon();
        actionBar.setTitle(getString(R.string.gdb_title_record));
    }

    public void onRecordListPage() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (recordFragment == null) {
            recordFragment = new RecordsListFragment();
        }

        ft.replace(R.id.group_ft_container, recordFragment, "RecordsListFragment");
        ft.commit();
    }

    private ActionBar.ActionIconListener iconListener = new ActionBar.ActionIconListener() {
        @Override
        public void onBack() {
            finish();
        }

        @Override
        public void onIconClick(View view) {
            switch (view.getId()) {
                case R.id.actionbar_sort:
                    recordFragment.changeSortType();
                    break;
                case R.id.actionbar_show:
                    startActivity(new Intent(RecordListActivity.this, RecordSceneActivity.class));
                    finish();
                    break;
                case R.id.actionbar_play:
                    if (view.isSelected()) {
                        view.setSelected(false);
                        recordFragment.showCanPlayList(false);
                    }
                    else {
                        view.setSelected(true);
                        recordFragment.showCanPlayList(true);
                    }
                    break;
            }
        }
    };

    private ActionBar.ActionMenuListener menuListener = new ActionBar.ActionMenuListener() {
        @Override
        public void createMenu(MenuInflater menuInflater, Menu menu) {
            loadMenu(menuInflater, menu);
        }

        @Override
        public void onPrepareMenu(MenuInflater menuInflater, Menu menu) {
            loadMenu(menuInflater, menu);
        }

        private void loadMenu(MenuInflater menuInflater, Menu menu) {
            menu.clear();
            menuInflater.inflate(R.menu.gdb_record_list, menu);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {

            }
            return false;
        }
    };

    private ActionBar.ActionSearchListener searchListener = new ActionBar.ActionSearchListener() {
        @Override
        public void onTextChanged(String text, int start, int before, int count) {
            recordFragment.filterRecord(text);
        }
    };

    @Override
    public RecordListPresenter getPresenter() {
        return recordPresenter;
    }
}
