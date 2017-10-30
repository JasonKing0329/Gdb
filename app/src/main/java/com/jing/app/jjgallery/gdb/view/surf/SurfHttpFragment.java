package com.jing.app.jjgallery.gdb.view.surf;

import com.jing.app.jjgallery.gdb.ActivityManager;
import com.jing.app.jjgallery.gdb.IFragmentHolder;
import com.jing.app.jjgallery.gdb.http.HttpConstants;
import com.jing.app.jjgallery.gdb.http.bean.data.FileBean;
import com.jing.app.jjgallery.gdb.model.SettingProperties;
import com.jing.app.jjgallery.gdb.model.bean.HttpSurfFileBean;
import com.jing.app.jjgallery.gdb.util.DebugLog;
import com.king.service.gdb.bean.Record;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述: server files explore
 * <p/>作者：景阳
 * <p/>创建时间: 2017/10/30 13:15
 */
public class SurfHttpFragment extends SurfFragment implements SurfAdapter.OnSurfItemActionListener<Record> {

    private ISurfHttpHolder holder;

    private List<HttpSurfFileBean> surfFileList;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {
        this.holder = (ISurfHttpHolder) holder;
    }

    /**
     * load folder finished
     * the callback of loadFolder
     * @param list
     */
    public void onFolderReceived(List<HttpSurfFileBean> list) {
        surfFileList = list;
        List<FileBean> targetList = new ArrayList<>();
        if (targetList != null) {
            for (FileBean bean:list) {
                targetList.add(bean);
            }
        }
        updateSurfList(targetList);
        holder.endProgress();
    }

    /**
     * current file list
     * @return
     */
    public List<HttpSurfFileBean> getSurfFileList() {
        return surfFileList;
    }

    @Override
    public void loadFolder() {
        holder.startProgress();
        DebugLog.e(folderBean.getPath());
        if (HttpConstants.FOLDER_TYPE_CONTENT.equals(folderBean.getPath())) {
            holder.getPresenter().surf(HttpConstants.FOLDER_TYPE_CONTENT, null, SettingProperties.isGdbSurfRelated());
        }
        else {
            holder.getPresenter().surf(HttpConstants.FOLDER_TYPE_FOLDER, folderBean.getPath(), SettingProperties.isGdbSurfRelated());
        }
    }

    @Override
    protected SurfAdapter.OnSurfItemActionListener getOnSurfItemActionListener() {
        return this;
    }

    @Override
    public void onClickSurfFolder(FileBean fileBean) {
        holder.onClickSurfFolder(fileBean);
    }

    @Override
    public void onClickSurfFile(Record record) {
        if (record != null) {
            ActivityManager.startRecordActivity(getActivity(), record);
        }
    }

}
