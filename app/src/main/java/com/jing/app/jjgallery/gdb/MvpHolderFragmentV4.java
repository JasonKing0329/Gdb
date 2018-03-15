package com.jing.app.jjgallery.gdb;

/**
 * 描述: 封装泛型IFragmentHolder的MvpFragmentV4
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/15 14:53
 */
public abstract class MvpHolderFragmentV4<P extends BasePresenter, FH extends IFragmentHolder> extends MvpFragmentV4<P> {

    protected FH holder;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {
        if (holder != null) {
            this.holder = (FH) holder;
        }
    }
}
