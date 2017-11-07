package com.jing.app.jjgallery.gdb.view.game;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/11/7 13:37
 */
public class RandomAdapter extends FragmentPagerAdapter {

    private List<BaseRandomFragment> ftList;

    public RandomAdapter(FragmentManager fm) {
        super(fm);
        ftList = new ArrayList<>();
    }

    public void addFragment(BaseRandomFragment fragment) {
        ftList.add(fragment);
    }

    @Override
    public BaseRandomFragment getItem(int position) {
        return ftList.get(position);
    }

    @Override
    public int getCount() {
        return ftList == null ? 0:ftList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return ftList.get(position).getTitle();
    }
}
