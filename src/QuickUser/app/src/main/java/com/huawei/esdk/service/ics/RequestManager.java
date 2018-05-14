package com.huawei.esdk.service.ics;


import com.huawei.esdk.service.ics.model.request.GetConnectRequest;
import com.huawei.esdk.service.ics.model.request.GetTextConnectRequest;
import com.huawei.esdk.service.ics.model.request.LoginRequest;
import com.huawei.esdk.service.ics.model.request.SendMsgRequest;
import com.huawei.esdk.service.ics.model.response.ResultResponse;
import com.huawei.esdk.service.ics.model.response.DropCallResponse;
import com.huawei.esdk.service.ics.model.response.GetEventResponse;
import com.huawei.esdk.service.ics.model.response.QueueInfoResponse;
import com.huawei.esdk.service.ics.model.response.SendMsgResponse;
import com.huawei.esdk.service.ics.model.response.StopConfResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


/**
 * RequestManager
 */
public interface RequestManager
{
    /**
     * 登录
     *
     * @param username username
     * @param vdnId    vdnId
     * @param body     body
     * @return call
     */
    @Headers({
            "Accept: application/json",
            "Accept-Encoding: gzip,deflate",
            "Content-Type: application/json",
            "guid: "
    })
    @POST("/icsgateway/resource/onlinewecc/{vdnId}/{username}/login")
    Call<ResultResponse> login(@Path("username") String username, @Path("vdnId") String vdnId,
                               @Body LoginRequest body);


    /**
     * 建立文字链接
     *
     * @param guid     guid
     * @param cookie   cookie
     * @param username username
     * @param vdnId    vdnId
     * @param body     body
     * @return call
     */
    @Headers({
            "Accept: application/json",
            "Accept-Encoding: gzip,deflate",
            "Content-Type: application/json",
    })
    @POST("/icsgateway/resource/realtimecall/{vdnId}/{username}/docreatecall")
    Call<ResultResponse> getTextConnect(@Header("guid") String guid, @Header("cookie") String cookie,
                                        @Path("username") String username,
                                        @Path("vdnId") String vdnId, @Body GetTextConnectRequest body);


    /**
     * 发送消息
     *
     * @param guid     guid
     * @param cookie   cookie
     * @param username username
     * @param vdnId    vdnId
     * @param body     body
     * @return call
     */
    @Headers({
            "Accept: application/json",
            "Accept-Encoding: gzip,deflate",
            "Content-Type: application/json",
    })
    @POST("/icsgateway/resource/realtimecall/{vdnId}/{username}/dosendmessage")
    Call<SendMsgResponse> sendMessage(@Header("guid") String guid, @Header("cookie") String cookie,
                                      @Path("username") String username,
                                      @Path("vdnId") String vdnId, @Body SendMsgRequest body);

    /**
     * 注销
     *
     * @param guid     guid
     * @param cookie   cookie
     * @param username username
     * @param vdnId    vdnId
     * @return call
     */
    @Headers({
            "Accept: application/json",
            "Accept-Encoding: gzip,deflate",
    })
    @DELETE("/icsgateway/resource/onlinewecc/{vdnId}/{username}/logout")
    Call<ResultResponse> logOut(@Header("guid") String guid, @Header("cookie") String cookie,
                                @Path("username") String username,
                                @Path("vdnId") String vdnId);


    /**
     * 轮询消息
     *
     * @param guid     guid
     * @param cookie   cookie
     * @param username username
     * @param vdnId    vdnId
     * @return call
     */
    @Headers({
            "Accept: application/json",
            "Accept-Encoding: gzip,deflate",
            "Content-Type:application/json; charset=utf-8"
    })
    @GET("/icsgateway/resource/icsevent/{vdnId}/{username}")
    Call<GetEventResponse> loop(@Header("guid") String guid, @Header("cookie") String cookie,
                                @Path("username") String username,
                                @Path("vdnId") String vdnId);

    /**
     * 排队信息
     *
     * @param guid     guid
     * @param cookie   cookie
     * @param username username
     * @param vdnId    vdnId
     * @param callId   callId
     * @return call
     */
    @Headers({
            "Accept: application/json",
            "Accept-Encoding: gzip,deflate",
    })
    @GET("/icsgateway/resource/realtimecall/{vdnId}/{username}/getcallqueue")
    Call<QueueInfoResponse> getQueueInfo(@Header("guid") String guid, @Header("cookie") String cookie,
                                         @Path("username") String username,
                                         @Path("vdnId") String vdnId, @Query("callId") String callId);

    /**
     * 建立语音、视频链接
     * MS模式
     *
     * @param guid     guid
     * @param cookie   cookie
     * @param username username
     * @param vdnId    vdnId
     * @param body     body
     * @return call
     */
    @Headers({
            "Accept: application/json",
            "Accept-Encoding: gzip,deflate",
            "Content-Type: application/json",
    })
    @POST("/icsgateway/resource/realtimecall/{vdnId}/{username}/docreatecall")
    Call<ResultResponse> getMSConnect(@Header("guid") String guid, @Header("cookie") String cookie,
                                      @Path("username") String username,
                                      @Path("vdnId") String vdnId, @Body GetConnectRequest body);

    /**
     * 释放呼叫
     *
     * @param guid     guid
     * @param cookie   cookie
     * @param username username
     * @param vdnId    vdnId
     * @param callId   callId
     * @return call
     */
    @Headers({
            "Accept: application/json",
            "Accept-Encoding: gzip,deflate",
    })
    @DELETE("/icsgateway/resource/realtimecall/{vdnId}/{username}/dodropcall")
    Call<DropCallResponse> dropCall(@Header("guid") String guid, @Header("cookie") String cookie,
                                    @Path("username") String username,
                                    @Path("vdnId") String vdnId, @Query("callId") String callId);

    /**
     * 取消排队
     *
     * @param guid     guid
     * @param cookie   cookie
     * @param username username
     * @param vdnId    vdnId
     * @param callId   callId
     * @return call
     */
    @Headers({
            "Accept: application/json",
            "Accept-Encoding: gzip,deflate",
    })
    @DELETE("/icsgateway/resource/realtimecall/{vdnId}/{username}/docancelqueuecall")
    Call<DropCallResponse> cancelQueue(@Header("guid") String guid, @Header("cookie") String cookie,
                                       @Path("username") String username,
                                       @Path("vdnId") String vdnId, @Query("callId") String callId);


    /**
     * 获取验证码
     * @param guid     guid
     * @param cookie   cookie
     * @param vdnId    vdnId
     * @param username username
     * @return call
     */
    @Headers({
            "Accept: application/json",
            "Accept-Encoding: gzip,deflate",
    })
    @GET("/icsgateway/resource/verifycode/{vdnId}/{username}/verifycodeforcall")
    Call<ResultResponse> getVerifycode(@Header("guid") String guid, @Header("cookie") String cookie,
                                       @Path("vdnId") String vdnId,
                                       @Path("username") String username);

    /**
     * MS会议
     *
     * @param guid     guid
     * @param cookie     cookie
     * @param username username
     * @param vdnId    vdnId
     * @param callId   callId
     * @return call
     */
    @Headers({
            "Accept: application/json",
            "Accept-Encoding: gzip,deflate",
    })
    @POST("/icsgateway/resource/meetingcall/{vdnId}/{username}/requestmeeting")
    Call<ResultResponse> startConf(@Header("guid") String guid, @Header("cookie") String cookie,
                                   @Path("username") String username,
                                   @Path("vdnId") String vdnId, @Query("callId") String callId);

    /**
     * MS stop会议
     *
     * @param guid     guid
     * @param cookie     cookie
     * @param username username
     * @param vdnId    vdnId
     * @param confId   confId
     * @return call
     */
    @Headers({
            "Accept: application/json",
            "Accept-Encoding: gzip,deflate",
    })
    @POST("/icsgateway/resource/meetingcall/{vdnId}/{username}/stopmeeting")
    Call<StopConfResponse> stopConf(@Header("guid") String guid, @Header("cookie") String cookie,
                                    @Path("username") String username,
                                    @Path("vdnId") String vdnId, @Query("confId") String confId);

}
