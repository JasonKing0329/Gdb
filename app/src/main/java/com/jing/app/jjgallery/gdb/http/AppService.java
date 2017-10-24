package com.jing.app.jjgallery.gdb.http;

import com.jing.app.jjgallery.gdb.http.bean.response.AppCheckBean;
import com.jing.app.jjgallery.gdb.http.bean.response.GdbRespBean;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2016/9/5.
 */
public interface AppService {
    @GET("online")
    Observable<GdbRespBean> isServerOnline();

    @GET("checkNew")
    Observable<AppCheckBean> checkAppUpdate(@Query("type") String type, @Query("version") String version);

    @GET("checkNew")
    Observable<AppCheckBean> checkGdbDatabaseUpdate(@Query("type") String type, @Query("version") String version);
}
