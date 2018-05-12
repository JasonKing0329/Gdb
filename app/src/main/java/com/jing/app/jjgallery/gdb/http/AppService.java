package com.jing.app.jjgallery.gdb.http;

import com.jing.app.jjgallery.gdb.http.bean.request.FolderRequest;
import com.jing.app.jjgallery.gdb.http.bean.request.GdbCheckNewFileBean;
import com.jing.app.jjgallery.gdb.http.bean.request.GdbRequestMoveBean;
import com.jing.app.jjgallery.gdb.http.bean.request.GetStarRatingsRequest;
import com.jing.app.jjgallery.gdb.http.bean.request.UploadStarRatingRequest;
import com.jing.app.jjgallery.gdb.http.bean.response.AppCheckBean;
import com.jing.app.jjgallery.gdb.http.bean.response.BaseResponse;
import com.jing.app.jjgallery.gdb.http.bean.response.FolderResponse;
import com.jing.app.jjgallery.gdb.http.bean.response.GdbMoveResponse;
import com.jing.app.jjgallery.gdb.http.bean.response.GdbRespBean;
import com.jing.app.jjgallery.gdb.http.bean.response.GetStarRatingResponse;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
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

    @GET("checkNew")
    Observable<GdbCheckNewFileBean> checkNewFile(@Query("type") String type);

    @POST("requestMove")
    Observable<GdbMoveResponse> requestMoveImages(@Body GdbRequestMoveBean data);

    @POST("surfFolder")
    Observable<FolderResponse> requestSurf(@Body FolderRequest data);

    @POST("uploadStarRatings")
    Observable<BaseResponse> uploadStarRatings(@Body UploadStarRatingRequest data);

    @POST("getStarRatings")
    Observable<BaseResponse<GetStarRatingResponse>> getStarRatings(@Body GetStarRatingsRequest data);
}
