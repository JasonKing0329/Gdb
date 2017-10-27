package com.jing.app.jjgallery.gdb.presenter;

import com.jing.app.jjgallery.gdb.http.UploadClient;
import com.jing.app.jjgallery.gdb.model.conf.ConfManager;
import com.jing.app.jjgallery.gdb.model.conf.Configuration;
import com.jing.app.jjgallery.gdb.util.DebugLog;
import com.jing.app.jjgallery.gdb.view.settings.IUploadView;

import org.reactivestreams.Subscriber;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2016/9/7.
 */
public class UploadPresenter {

    private IUploadView uploadView;

    public UploadPresenter(IUploadView uploadView) {
        this.uploadView = uploadView;
    }

    public void uploadAppData(){

        //组装partMap对象
        Map<String, RequestBody> partMap = new HashMap<>();

        // gdb database
        File file = new File(ConfManager.GDB_DB_PATH);
        RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        partMap.put("file\"; filename=\""+file.getName(), fileBody);

        // app database
        file = new File(ConfManager.DB_PATH);
        fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        partMap.put("file\"; filename=\""+file.getName(), fileBody);

        // preference
        file = new File(Configuration.EXTEND_RES_COLOR);
        fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        partMap.put("file\"; filename=\""+file.getName(), fileBody);

        // preference
        file = new File(Configuration.APP_DIR_CONF_PREF_DEF);
        File files[] = file.listFiles();
        if (files.length > 0) {
            file = files[0];
        }
        fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        partMap.put("file\"; filename=\""+file.getName(), fileBody);

        //使用RxJava方式调度任务并监听
        new UploadClient().getUploadService().upload(partMap)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        DebugLog.e("upload");
                        uploadView.onUploadSuccess();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        DebugLog.e("upload" + throwable.getMessage());
                        uploadView.onUploadFail();
                    }
                });
    }
}
