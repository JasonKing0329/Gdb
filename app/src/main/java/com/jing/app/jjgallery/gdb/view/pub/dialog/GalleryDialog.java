package com.jing.app.jjgallery.gdb.view.pub.dialog;

import android.content.DialogInterface;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;

import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.util.ScreenUtils;
import com.jing.app.jjgallery.gdb.view.record.pad.PagePagerAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/4/18 10:49
 */
public class GalleryDialog extends BaseDialogFragmentV4 {

    Unbinder unbinder;

    @BindView(R.id.rv_gallery)
    RecyclerView rvPager;

    private List<String> list;

    private int currentPage;

    private PagePagerAdapter adapter;

    private PagePagerAdapter.OnItemClickListener onItemClickListener;

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_gallery;
    }

    @Override
    protected void initView(View view) {
        unbinder = ButterKnife.bind(this, view);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvPager.setLayoutManager(manager);
        rvPager.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                if (position > 0) {
                    outRect.left = ScreenUtils.dp2px(15);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        // 在initView里不起作用
        setWidth(ScreenUtils.getScreenWidth());

        if (adapter == null) {
            adapter = new PagePagerAdapter();
            adapter.setList(list);
            rvPager.setAdapter(adapter);
        }
        else {
            adapter.setList(list);
            adapter.notifyDataSetChanged();
        }
        adapter.setOnItemClickListener(onItemClickListener);
        adapter.updateSelection(currentPage);
        rvPager.scrollToPosition(currentPage);
    }

    public void setImageList(List<String> list) {
        this.list = list;
    }

    public void setOnItemClickListener(PagePagerAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
        if (isVisible()) {
            adapter.updateSelection(currentPage);
            rvPager.scrollToPosition(currentPage);
        }
    }
}
