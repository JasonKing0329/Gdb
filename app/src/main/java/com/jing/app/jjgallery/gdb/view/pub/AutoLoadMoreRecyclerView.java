package com.jing.app.jjgallery.gdb.view.pub;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.jing.app.jjgallery.gdb.util.DebugLog;

/**
 * 描述: 上拉到底部自动执行加载更多的recycler view
 * 只支持纵向布局
 * <p/>作者：景阳
 * <p/>创建时间: 2017/5/23 16:14
 */
public class AutoLoadMoreRecyclerView extends RecyclerView {

    private ASListener asListener;

    private OnLoadMoreListener onLoadMoreListener;

    public AutoLoadMoreRecyclerView(Context context) {
        super(context);
        init();
    }

    public AutoLoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setEnableLoadMore(boolean enableLoadMore) {
        if (enableLoadMore) {
            addOnScrollListener(asListener);
        }
        else {
            removeOnScrollListener(asListener);
        }
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    private void init() {
        asListener = new ASListener();
    }

    private class ASListener extends OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (onLoadMoreListener != null) {
                DebugLog.e("canScrollVertically " + canScrollVertically(1));
                // 滑到底部了
                if (!canScrollVertically(1)) {
                    onLoadMoreListener.onLoadMore();
                }
//                if (getLayoutManager() instanceof GridLayoutManager) {
//                    if (!getLayoutManager().canScrollVertically()) {
//                        onLoadMoreListener.onLoadMore();
//                    }
//                }
//                else if (getLayoutManager() instanceof LinearLayoutManager) {
//                    if (!getLayoutManager().canScrollVertically()) {
//                        onLoadMoreListener.onLoadMore();
//                    }
//                }
            }
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}
