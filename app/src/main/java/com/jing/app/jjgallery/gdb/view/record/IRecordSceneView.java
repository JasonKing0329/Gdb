package com.jing.app.jjgallery.gdb.view.record;

import com.jing.app.jjgallery.gdb.model.db.SceneBean;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/7/12 16:24
 */
public interface IRecordSceneView {
    void onScenesLoaded(List<SceneBean> data);
}
