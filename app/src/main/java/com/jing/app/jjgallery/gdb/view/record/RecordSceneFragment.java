package com.jing.app.jjgallery.gdb.view.record;

import android.content.DialogInterface;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jing.app.jjgallery.gdb.GdbConstants;
import com.jing.app.jjgallery.gdb.IFragmentHolder;
import com.jing.app.jjgallery.gdb.MvpFragmentV4;
import com.jing.app.jjgallery.gdb.R;
import com.jing.app.jjgallery.gdb.model.SettingProperties;
import com.jing.app.jjgallery.gdb.model.bean.HsvColorBean;
import com.jing.app.jjgallery.gdb.model.db.SceneBean;
import com.jing.app.jjgallery.gdb.presenter.record.RecordScenePresenter;
import com.jing.app.jjgallery.gdb.view.adapter.RecordSceneNameAdapter;
import com.jing.app.jjgallery.gdb.view.pub.dialog.HsvColorDialogFragment;

import java.util.List;

import butterknife.BindView;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/7/12 16:12
 */
public class RecordSceneFragment extends MvpFragmentV4<RecordScenePresenter> implements IRecordSceneView {

    private IRecordSceneHolder holder;

    @BindView(R.id.rv_scenes)
    RecyclerView rvScenes;

    private HsvColorDialogFragment colorDialog;

    private RecordSceneNameAdapter sceneAdapter;

    private List<SceneBean> sceneList;

    private int curSortType;

    private String focusScene;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {
        this.holder = (IRecordSceneHolder) holder;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_gdb_scene;
    }

    @Override
    protected void initView(View view) {
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 3);
        rvScenes.setLayoutManager(manager);
    }

    @Override
    protected RecordScenePresenter createPresenter() {
        return new RecordScenePresenter();
    }

    @Override
    protected void initData() {
        curSortType = GdbConstants.SCENE_SORT_NAME;
        presenter.loadRecordScenes();
    }

    @Override
    public void showScenes(List<SceneBean> data) {
        sceneList = data;
        sceneAdapter = new RecordSceneNameAdapter(data);
        sceneAdapter.setOnSceneItemClickListener(new RecordSceneNameAdapter.OnSceneItemClickListener() {
            @Override
            public void onSceneItemClick(String scene) {
                holder.onSelectScene(scene);
            }
        });
        rvScenes.setAdapter(sceneAdapter);

        // 初始化仅一次
        if (focusScene != null) {
            focusToScene(focusScene);
            focusScene = null;
        }
    }

    public void sortByName() {
        if (curSortType != GdbConstants.SCENE_SORT_NAME) {
            curSortType = GdbConstants.SCENE_SORT_NAME;
            presenter.sortScenes(curSortType);
            sceneAdapter.notifyDataSetChanged();
        }
    }

    public void sortByAvg() {
        if (curSortType != GdbConstants.SCENE_SORT_AVG) {
            curSortType = GdbConstants.SCENE_SORT_AVG;
            presenter.sortScenes(curSortType);
            sceneAdapter.notifyDataSetChanged();
        }
    }

    public void sortByNumber() {
        if (curSortType != GdbConstants.SCENE_SORT_NUMBER) {
            curSortType = GdbConstants.SCENE_SORT_NUMBER;
            presenter.sortScenes(curSortType);
            sceneAdapter.notifyDataSetChanged();
        }
    }

    public void sortByMax() {
        if (curSortType != GdbConstants.SCENE_SORT_MAX) {
            curSortType = GdbConstants.SCENE_SORT_MAX;
            presenter.sortScenes(curSortType);
            sceneAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void sortFinished(List<SceneBean> list) {
        sceneAdapter.setList(list);
        sceneAdapter.notifyDataSetChanged();
    }

    public void editColor() {
        if (colorDialog == null) {
            colorDialog = new HsvColorDialogFragment();
            colorDialog.setHsvColorBean(SettingProperties.getGdbSceneColor());
            colorDialog.setOnHsvColorListener(new HsvColorDialogFragment.OnHsvColorListener() {
                @Override
                public void onPreviewHsvColor(HsvColorBean hsvColorBean) {
                    sceneAdapter.updateBgColors(hsvColorBean);
                }

                @Override
                public void onSaveColor(HsvColorBean hsvColorBean) {
                    SettingProperties.setGdbSceneHsvColor(hsvColorBean);
                }
            });
        }
        colorDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                sceneAdapter.updateBgColors(SettingProperties.getGdbSceneColor());
            }
        });
        colorDialog.show(getFragmentManager(), "HsvColorDialogFragment");
    }

    public void setFocusScene(String focusScene) {
        this.focusScene = focusScene;
    }

    public void focusToScene(String scene) {
        this.focusScene = scene;
        int position = presenter.getFocusScenePosition(scene);
        sceneAdapter.setSelection(position);
        sceneAdapter.notifyDataSetChanged();
        rvScenes.scrollToPosition(position);
    }
}
