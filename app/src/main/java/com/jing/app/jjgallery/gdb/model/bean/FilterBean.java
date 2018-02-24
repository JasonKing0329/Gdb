package com.jing.app.jjgallery.gdb.model.bean;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/2/24 15:04
 */
public class FilterBean {

    private boolean isBareback;

    private boolean isInnerCum;

    private boolean isNotDeprecated;

    public boolean isBareback() {
        return isBareback;
    }

    public void setBareback(boolean bareback) {
        isBareback = bareback;
    }

    public boolean isInnerCum() {
        return isInnerCum;
    }

    public void setInnerCum(boolean innerCum) {
        isInnerCum = innerCum;
    }

    public boolean isNotDeprecated() {
        return isNotDeprecated;
    }

    public void setNotDeprecated(boolean notDeprecated) {
        isNotDeprecated = notDeprecated;
    }
}
