package com.huawei.esdk.service.ics;

import com.huawei.esdk.Constant;
import com.huawei.esdk.MobileCC;
import com.huawei.esdk.CCAPP;
import com.huawei.esdk.conference.ConferenceInfo;
import com.huawei.esdk.conference.ConferenceMgr;
import com.huawei.esdk.service.ics.model.BroadMsg;
import com.huawei.esdk.NotifyMessage;
import com.huawei.esdk.service.ics.model.QueueInfo;
import com.huawei.esdk.service.ics.model.RequestCode;
import com.huawei.esdk.service.ics.model.RequestInfo;
import com.huawei.esdk.service.call.CallManager;
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
import com.huawei.esdk.utils.LogUtil;
import com.huawei.esdk.utils.StringUtils;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created on 2016/5/10.
 */
public final class ICSService
{
    private static final Object LOCKOBJECT = new Object();
    public static final int MEDIA_TYPE_TEXT = 1;
    public static final int MEDIA_TYPE_AUDIO = 2;
    private static final String TAG = "ICSService";
    private static ICSService instance;
    private Retrofit localRetrofit = null;
    private OkHttpClient client =  new OkHttpClient();
    private boolean isNat = false;
    private Timer timer = null;
    private int num = 0;
    private static int DEFAULT_TIMEOUT = 15000;

    private ICSService()
    {
    }

    /**
     *
     * @return ICSService
     */
    public static ICSService getInstance()
    {
        synchronized (LOCKOBJECT)
        {
            if (null == instance)
            {
                instance = new ICSService();
            }
            return instance;
        }
    }

    /**
     * 开启任务
     */
    private void beginTask()
    {
        beginLoop();
    }

    /**
     * 登录
     *
     * @param name name
     */
    public void login(String name)
    {
        Retrofit retrofit = getRetrofit();
        if (null == retrofit)
        {
            return;
        }
        RequestManager requestManager = retrofit.create(RequestManager.class);
        Call call = requestManager.login(name, SystemConfig.getInstance().getVndID(),
                new LoginRequest(StringUtils.getIpAddress(), "", SystemConfig.getInstance().getServerIp()));
        if (null == call)
        {
            return;
        }
        call.enqueue(new Callback<ResultResponse>()
        {
            private BroadMsg broadMsg = new BroadMsg(NotifyMessage.AUTH_MSG_ON_LOGIN);

            @Override
            public void onResponse(Call<ResultResponse> call, Response<ResultResponse> response)
            {
                ResultResponse model = response.body();

                if (null == model)
                {
                    //返回信息出为空
                    sendBroadcastWithError(broadMsg, NotifyMessage.RET_ERROR_RESPONSE);
                    return;
                }

                logInfo(TAG, "login" + " " + model.getMessage());

                String retcode = model.getRetcode();

                if (!MobileCC.MESSAGE_OK.equals(retcode))
                {
                    //返回信息出错
                    sendBroadcast(broadMsg, retcode);
                    return;
                }

                SystemConfig.getInstance().setLogin(true);
                logInfo(TAG, "login" + " " + "login success");
                logInfo(TAG, "login" + " " + "Retcode:" + model.getRetcode() + "\n Result:" + model.getResult());

                if (null == response.headers())
                {
                    logError(TAG, "header is null");
                    return;
                }

                String guid = getGUID(response.headers());
                String cookie = getCookies(response.headers());

                if (!StringUtils.isEmpty(guid))
                {
                    SystemConfig.getInstance().setGuid(guid);
                }

                if (!StringUtils.isEmpty(cookie))
                {
                    SystemConfig.getInstance().setCookie(cookie);
                }

                //发送登录成功的广播
                sendBroadcast(broadMsg, retcode);

                //登陆成功后开始轮询
                beginTask();
            }

            @Override
            public void onFailure(Call<ResultResponse> call, Throwable t)
            {
                //网络访问异常
                logError(TAG, "login" + " " + "login -> t = " + t.getMessage());
                sendBroadcastWithError(broadMsg, NotifyMessage.RET_ERROR_NETWORK);
            }
        });
    }

    /**
     * 获取cookie
     *
     * @param headers Headers
     * @return cookies String
     */
    private String getCookies(Headers headers)
    {
        StringBuilder builder = new StringBuilder();
        List<String> cookies = headers.values("Set-Cookie");
        if (null == cookies)
        {
            return "";
        }

        //对多个cookie进行拼接，以“;”隔开
        for(String s : cookies)
        {
            builder.append(s);
            builder.append(";");
        }

        //删除最后一次拼接的多余的“;”
        if (builder.length() > 1)
        {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

    /**
     * 获取guid
     *
     * @param headers Headers
     * @return guid String
     */
    private String getGUID(Headers headers)
    {
        String guid = "";

        String setGUID = headers.get("Set-GUID");
        if (null == setGUID || !setGUID.contains("="))
        {
            return guid;
        }
        String[] array = setGUID.split("=");
        if (array.length > 1)
        {
            guid = array[1];
        }
        return guid;
    }

    /**
     * 注销
     */
    public void logout()
    {
        Retrofit retrofit = getRetrofit();
        if (null == retrofit)
        {
            return;
        }
        RequestManager requestManager = retrofit.create(RequestManager.class);
        Call call = requestManager.logOut(SystemConfig.getInstance().getGuid(),
                SystemConfig.getInstance().getCookie(),
                SystemConfig.getInstance().getUserName(),
                SystemConfig.getInstance().getVndID());

        if (null == call)
        {
            return;
        }

        call.enqueue(new Callback<ResultResponse>()
        {
            private BroadMsg broadMsg = new BroadMsg(NotifyMessage.AUTH_MSG_ON_LOGOUT);

            @Override
            public void onResponse(Call<ResultResponse> call, Response<ResultResponse> response)
            {
                ResultResponse model = response.body();
                if (null == model)
                {
                    //通知上层数据异常处理404或者转换失败
                    sendBroadcastWithError(broadMsg, NotifyMessage.RET_ERROR_RESPONSE);
                    return;
                }

                //200 model不为空 请求成功
                if (MobileCC.MESSAGE_OK.equals(model.getRetcode()))
                {
                    SystemConfig.getInstance().setLogin(false);
                    SystemConfig.getInstance().setUvid(-1);
                    SystemConfig.getInstance().setCalledNum("");
                    SystemConfig.getInstance().setServerIp("");
                    SystemConfig.getInstance().setServerPort(0);
                    SystemConfig.getInstance().clear();
                }
                else
                {
                    logInfo(TAG, "logout" + " " + "Logout fail ,retcode is："
                            + model.getRetcode() + ",Message:" + model.getMessage());
                }
                sendBroadcast(broadMsg, model.getRetcode());
            }

            @Override
            public void onFailure(Call<ResultResponse> call, Throwable t)
            {
                logError(TAG, "logout" + " " + "logout -> t = " + t.getMessage());
                sendBroadcastWithError(broadMsg, NotifyMessage.RET_ERROR_NETWORK);
            }
        });
    }

    /**
     * 轮询
     */
    public void beginLoop()
    {
        synchronized (LOCKOBJECT)
        {
            if (null == localRetrofit)
            {
                return;
            }
            RequestManager requestManager = localRetrofit.create(RequestManager.class);
            Call call = requestManager.loop(SystemConfig.getInstance().getGuid(),
                    SystemConfig.getInstance().getCookie(),
                    SystemConfig.getInstance().getUserName(),
                    SystemConfig.getInstance().getVndID());

            if (null == call)
            {
                return;
            }

            call.enqueue(new Callback<GetEventResponse>()
            {
                private BroadMsg broadMsg = new BroadMsg(NotifyMessage.CALL_MSG_ON_POLL);
                @Override
                public void onResponse(Call<GetEventResponse> call, Response<GetEventResponse> response)
                {
                    //网络恢复，计时器清零
                    num = 0;
                    GetEventResponse model = response.body();
                    if (null == model)
                    {
                        //通知上层数据异常处理404或者转换失败
                        sendBroadcastWithError(broadMsg, NotifyMessage.RET_ERROR_RESPONSE);
                    }
                    else
                    {
                        if (MobileCC.MESSAGE_OK.equals(model.getRetcode()) && null != model.getEvent())
                        {
                            String eventType = model.getEvent().getEventType();
                            logInfo(TAG, "Event： " + eventType);
                            if (NotifyMessage.WECC_WEBM_CALL_CONNECTED.equals(eventType))
                            {
                                parseWebmCallConnected(model);
                            }
                            else if (NotifyMessage.WECC_CHAT_RECEIVEDATA.equals(eventType))
                            {
                                parseChatReceiveData(model);
                            }
                            else if (NotifyMessage.WECC_CHAT_POSTDATA_SUCC.equals(eventType))
                            {
                                parseChatPostDataSucc(model);
                            }
                            else if (NotifyMessage.WECC_WEBM_CALL_DISCONNECTED.equals(eventType))
                            {
                                parseWebmCallDisconnected(model);
                            }
                            else if (NotifyMessage.WECC_WEBM_CALL_QUEUING.equals(eventType))
                            {
                                parseWebmCallQueuing();
                            }
                            else if (NotifyMessage.WECC_WEBM_QUEUE_TIMEOUT.equals(eventType))
                            {
                                parseWebmQueueTimeout();
                            }
                            else if (NotifyMessage.WECC_WEBM_CANCEL_QUEUE.equals(eventType))
                            {
                                parseWebmCancelQueueSuccess();
                            }
                            else if (NotifyMessage.WECC_WEBM_CALL_FAIL.equals(eventType))
                            {
                                parseWebmCallFail(model);
                            }
                            else if (NotifyMessage.WECC_MEETING_PREPARE_JOIN.equals(eventType))
                            {
                                parseMeetingPrepareJoin(model);
                            }
                        }
                    }

                    //登陆状态下继续轮询
                    if (SystemConfig.getInstance().isLogin())
                    {
                        beginTask();
                    }
                }
                @Override
                public void onFailure(Call<GetEventResponse> call, Throwable t)
                {
                    logError(TAG, "Event -> t = " + t.getMessage());
                    if (num < 120)
                    {
                        sendBroadcastWithError(broadMsg, NotifyMessage.RET_ERROR_NETWORK);
                    }
                    else
                    {
                        sendBroadcastWithError(broadMsg, NotifyMessage.RET_WILL_LOGOUT);
                    }

                    //网络异常，开启计时器
                    beginTimer();

                    //登陆状态下继续轮询
                    if (SystemConfig.getInstance().isLogin())
                    {
                        beginTask();
                    }
                }
            });
        }
    }

    /**
     * 呼叫成功
     *
     * @param model GetEventResponse
     */
    private void parseWebmCallConnected(GetEventResponse model)
    {
        BroadMsg broadMsg = new BroadMsg(NotifyMessage.CALL_MSG_ON_CONNECTED);
        logInfo(TAG, "==============WECC_WEBM_CALL_CONNECTED==============");
        int mediaType = model.getEvent().getContent().getMediaType();
        logInfo(TAG, "mediaType  === " + mediaType);
        SystemConfig.getInstance().setUvid(model.getEvent().getContent().getUvid());

        if (MEDIA_TYPE_TEXT == mediaType)
        {
            //mediaType：1 文字交谈能力
            logInfo(TAG, "mediaType ==1");
            SystemConfig.getInstance().setMeidaType(mediaType);
            SystemConfig.getInstance().setTextConnected(true);

            sendBroadcast(broadMsg, model.getRetcode(), mediaType + "");
            return;
        }

        if (MEDIA_TYPE_AUDIO != mediaType)
        {
            //mediaType：2 语音视频能力，不是就返回，是就继续执行
            return;
        }

        //audio or video connected
        String calledNum = model.getEvent().getContent().getClickToDial();
        SystemConfig.getInstance().setCalledNum(calledNum);
        SystemConfig.getInstance().setMeidaType(mediaType);
        logInfo(TAG, "get teller's num：" + calledNum);
        sendBroadcast(broadMsg, model.getRetcode(), mediaType + "");

        CallManager.getInstance().getVoipConfig().resetData("", "", SystemConfig
                .getInstance().getSIPIp(), SystemConfig.getInstance().getSIPPort());
        String toNum = SystemConfig.getInstance().getCalledNum();
        logInfo(TAG, "toNum-> " + toNum);


        //判断是语音呼叫还是视屏呼叫，对返回码进行错误处理
        int callId = -1;
        if (MobileCC.AUDIO_CALL.equals(SystemConfig.getInstance().getCallType()))
        {
            callId = CallManager.getInstance().makeAnonymousCall(toNum);
            SystemConfig.getInstance().setAudioConnected(true);
        }
        else if (MobileCC.VIDEO_CALL.equals(SystemConfig.getInstance().getCallType()))
        {
            callId = CallManager.getInstance().startAnonymousVideoCall(toNum);
            SystemConfig.getInstance().setVideoConnected(true);
        }

        if (Constant.ERROR != callId)
        {
            SystemConfig.getInstance().setCallId(callId + "");
            logInfo(TAG, "Call Success");
        }
        else
        {
            broadMsg = new BroadMsg(NotifyMessage.CALL_MSG_ON_FAIL);
            sendBroadcast(broadMsg);
        }
    }

    /**
     * 文字聊天中接受到对方消息
     *
     * @param model GetEventResponse
     */
    private void parseChatReceiveData(GetEventResponse model)
    {
        BroadMsg broadMsg = new BroadMsg(NotifyMessage.CHAT_MSG_ON_RECEIVE);
        String content = model.getEvent().getContent().getChatContent();
        if (StringUtils.isEmpty(content))
        {
            return;
        }

        logInfo(TAG, "Receive content: ***");
        sendBroadcast(broadMsg, model.getRetcode(), content);
    }

    /**
     * 文字聊天中消息发送成功
     *
     * @param model GetEventResponse
     */
    private void parseChatPostDataSucc(GetEventResponse model)
    {
        BroadMsg broadMsg = new BroadMsg(NotifyMessage.CHAT_MSG_ON_SEND);
        String content = model.getEvent().getContent().getChatContent();
        logInfo(TAG, "Content post success: ***");
        sendBroadcast(broadMsg, model.getRetcode(), content);
    }

    /**
     * 呼叫断开
     *
     * @param model GetEventResponse
     */
    private void parseWebmCallDisconnected(GetEventResponse model)
    {
        BroadMsg broadMsg = new BroadMsg(NotifyMessage.CALL_MSG_ON_DISCONNECTED);
        int mediaType = model.getEvent().getContent().getMediaType();
        SystemConfig.getInstance().setUvid(-1);
        logInfo(TAG, "disconnected  mediaType ==" + mediaType);
        //文字断开
        if (MEDIA_TYPE_TEXT == mediaType)
        {
            SystemConfig.getInstance().setTextConnected(false);
            SystemConfig.getInstance().setTextCallId("");
            logInfo(TAG, "ms text disconnected");
            sendBroadcast(broadMsg, model.getRetcode(), MEDIA_TYPE_TEXT + "");
        }
        //音视频断开
        else if (MEDIA_TYPE_AUDIO == mediaType)
        {
            SystemConfig.getInstance().setVideoConnected(false);
            SystemConfig.getInstance().setAudioConnected(false);
            SystemConfig.getInstance().setAudioCallId("");
            SystemConfig.getInstance().setCalledNum("");
            SystemConfig.getInstance().setCallType("");
            SystemConfig.getInstance().setCameraIndex(1);
            logInfo(TAG, "ms audio disconnected");
            sendBroadcast(broadMsg, model.getRetcode(), MEDIA_TYPE_AUDIO + "");
        }
    }

    /**
     * 正在排队
     */
    private void parseWebmCallQueuing()
    {
        SystemConfig.getInstance().setIsQueuing(true);
        BroadMsg broadMsg = new BroadMsg(NotifyMessage.CALL_MSG_ON_QUEUING);
        sendBroadcast(broadMsg);
    }

    /**
     * 排队超时
     */
    private void parseWebmQueueTimeout()
    {
        SystemConfig.getInstance().setIsQueuing(false);
        BroadMsg broadMsg = new BroadMsg(NotifyMessage.CALL_MSG_ON_QUEUE_TIMEOUT);
        sendBroadcast(broadMsg);
    }

    /**
     * 排队取消成功
     */
    private void parseWebmCancelQueueSuccess()
    {
        SystemConfig.getInstance().setIsQueuing(false);
        BroadMsg broadMsg = new BroadMsg(NotifyMessage.CALL_MSG_ON_CANCEL_QUEUE);
        sendBroadcast(broadMsg);
    }

    /**
     * 呼叫失败
     */
    private void parseWebmCallFail(GetEventResponse model)
    {
        if (SystemConfig.getInstance().isQueuing())
        {
            BroadMsg broadMsg = new BroadMsg(NotifyMessage.CALL_MSG_ON_CANCEL_QUEUE);
            sendBroadcast(broadMsg);
        }
        else
        {
            BroadMsg broadMsg = new BroadMsg(NotifyMessage.CALL_MSG_ON_FAIL);
            logInfo(TAG, "fail  retcode is ->" + model.getRetcode());
            sendBroadcast(broadMsg, model.getRetcode());
        }
    }

    /**
     * 加入会议
     */
    private void parseMeetingPrepareJoin(GetEventResponse model)
    {
        SystemConfig.getInstance().setIsMeeting(true);
        BroadMsg broadMsg = new BroadMsg(NotifyMessage.CALL_MSG_USER_START);
        sendBroadcast(broadMsg, "");

        logInfo(TAG, "get conf param");
        String confInfo = model.getEvent().getContent().getConfInfo();
        String a[] = confInfo.split("\\|");
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < a.length; i++)
        {
            String b[] = a[i].split("=");
            if (a[i].contains("MsNatMap"))
            {
                if (b.length == 1)
                {
                    isNat = false;
                    SystemConfig.getInstance().setIsNat(false);
                    continue;
                }
                else
                {
                    isNat = true;
                    SystemConfig.getInstance().setIsNat(true);
                }
            }
            map.put(b[0], b[1]);
        }
        String siteId = map.get("SiteID");
        String msServerIP = map.get("MSServerIP");
        String natIp = map.get("MsNatMap");
        String siteUrl = map.get("SiteUrl");
        SystemConfig.getInstance().setNatIp(natIp);
        int confId = Integer.parseInt(map.get("ConfID"));
        String confKey = map.get("ConfPrivilege");
        int userId = Integer.parseInt(map.get("UserID"));
        SystemConfig.getInstance().setUserId(userId + "");
        String userName = model.getEvent().getUserName();
        String hostKey = map.get("HostKey");
        logInfo(TAG, "siteID:" + "*" + ",confId:"
                + "*" + ",userId:" + userId
                + ",userName:" + "*" + ",hostKey:" + "*"
                + " ,serverIp:" + "*" + ", natIp" + "*");
        SystemConfig.getInstance().setConfId(confId + "");
        ConferenceInfo conferenceInfo = new ConferenceInfo();
        conferenceInfo.setConfId(confId);
        conferenceInfo.setConfKey(confKey);
        conferenceInfo.setHostKey(hostKey);
        if (isNat)
        {
            conferenceInfo.setServerIp(natIp);
        }
        else
        {
            conferenceInfo.setServerIp(msServerIP);
        }
        conferenceInfo.setSiteUrl(siteUrl);
        conferenceInfo.setUserId(userId);
        conferenceInfo.setUserName(userName);
        conferenceInfo.setSiteId(siteId);
        ConferenceMgr.getInstance().setConferenceInfo(conferenceInfo);

        ConferenceMgr.getInstance().initConf();
        ConferenceMgr.getInstance().joinConference();
    }

    /**
     * 获取文字能力
     *
     * @param accessCode accessCode
     * @param caller     caller
     * @param callData   callData
     * @param verifyCode verifyCode
     */
    public void textConnect(String accessCode, String caller, String callData, String verifyCode)
    {
        Retrofit retrofit = getRetrofit();
        if (null == retrofit)
        {
            return;
        }
        RequestManager requestManager = retrofit.create(RequestManager.class);

        Call call = requestManager.getTextConnect(SystemConfig.getInstance().getGuid(),
                SystemConfig.getInstance().getCookie(),
                caller,
                SystemConfig.getInstance().getVndID(),
                new GetTextConnectRequest(MEDIA_TYPE_TEXT, caller, accessCode, callData, SystemConfig.getInstance().getUvid(), verifyCode));

        if (null == call)
        {
            return;
        }
        call.enqueue(new Callback<ResultResponse>()
        {
            private BroadMsg broadMsg = new BroadMsg(NotifyMessage.CALL_MSG_ON_CONNECT);

            @Override
            public void onResponse(Call<ResultResponse> call, Response<ResultResponse> response)
            {
                ResultResponse model = response.body();
                if (null == model)
                {
                    //通知上层数据异常处理404或者转换失败
                    broadMsg.setType(MEDIA_TYPE_TEXT + "");
                    sendBroadcastWithError(broadMsg, NotifyMessage.RET_ERROR_RESPONSE);
                    return;
                }

                //200 model不为空 请求成功
                if (MobileCC.MESSAGE_OK.equals(model.getRetcode()))
                {
                    logInfo(TAG, "Retcode:" + model.getRetcode() + "\n Message:" + model.getMessage()
                            + "\n webChat Result:" + model.getResult());
                    String resultMsg = model.getResult();
                    SystemConfig.getInstance().setTextCallId(resultMsg);
                }
                else
                {
                    logInfo(TAG, "Text ability error ->Retcode:" + model.getRetcode() + "\n Message:"
                            + model.getMessage() + "\n webChat Result:" + model.getResult());
                }
                broadMsg.setType(MEDIA_TYPE_TEXT + "");
                sendBroadcast(broadMsg, model.getRetcode());
            }

            @Override
            public void onFailure(Call<ResultResponse> call, Throwable t)
            {
                logError(TAG, "Text ablitity ->  t = " + t.getMessage());
                broadMsg.setType(MEDIA_TYPE_TEXT + "");
                sendBroadcastWithError(broadMsg, NotifyMessage.RET_ERROR_NETWORK);
            }
        });

    }

    /**
     * 发送消息
     *
     * @param content content
     */
    public void sendMsg(String content)
    {
        Retrofit retrofit = getRetrofit();
        if (null == retrofit)
        {
            return;
        }
        RequestManager requestManager = retrofit.create(RequestManager.class);
        Call call = requestManager.sendMessage(SystemConfig.getInstance().getGuid(),
                SystemConfig.getInstance().getCookie(),
                SystemConfig.getInstance().getUserName(),
                SystemConfig.getInstance().getVndID(),
                new SendMsgRequest(SystemConfig.getInstance().getTextCallId(), content));
        if (null == call)
        {
            return;
        }
        call.enqueue(new Callback<SendMsgResponse>()
        {
            private BroadMsg broadMsg = new BroadMsg(NotifyMessage.CHAT_MSG_ON_SEND);

            @Override
            public void onResponse(Call<SendMsgResponse> call, Response<SendMsgResponse> response)
            {
                SendMsgResponse model = response.body();
                if (null == model)
                {
                    //通知上层数据异常处理404或者转换失败
                    sendBroadcastWithError(broadMsg, NotifyMessage.RET_ERROR_RESPONSE);
                    return;
                }

                if (MobileCC.MESSAGE_OK.equals(model.getRetcode()))
                {
                    logInfo(TAG, "content send success");
                }
                else
                {
                    logInfo(TAG, "content send fail");
                    sendBroadcast(broadMsg, model.getRetcode());
                }
            }

            @Override
            public void onFailure(Call<SendMsgResponse> call, Throwable t)
            {
                logError(TAG, "send msg-> t = " + t.getMessage());
                sendBroadcastWithError(broadMsg, NotifyMessage.RET_ERROR_NETWORK);
            }
        });
    }

    /**
     * 获取排队信息
     */
    public void getCallQueueInfo()
    {
        Retrofit retrofit = getRetrofit();
        if (null == retrofit)
        {
            return;
        }
        RequestManager requestManager = retrofit.create(RequestManager.class);
        Call call;
        if (MobileCC.AUDIO_CALL.equals(SystemConfig.getInstance().getCallType())
                || MobileCC.VIDEO_CALL.equals(SystemConfig.getInstance().getCallType()))
        {
            call = requestManager.getQueueInfo(SystemConfig.getInstance().getGuid(),
                    SystemConfig.getInstance().getCookie(),
                    SystemConfig.getInstance().getUserName(),
                    SystemConfig.getInstance().getVndID(),
                    SystemConfig.getInstance().getAudioCallId());
        }
        else
        {
            call = requestManager.getQueueInfo(SystemConfig.getInstance().getGuid(),
                    SystemConfig.getInstance().getCookie(),
                    SystemConfig.getInstance().getUserName(),
                    SystemConfig.getInstance().getVndID(),
                    SystemConfig.getInstance().getTextCallId());
        }

        if (null == call)
        {
            return;
        }
        call.enqueue(new Callback<QueueInfoResponse>()
        {

            private BroadMsg broadMsg = new BroadMsg(NotifyMessage.CALL_MSG_ON_QUEUE_INFO);

            @Override
            public void onResponse(Call<QueueInfoResponse> call, Response<QueueInfoResponse> response)
            {
                logInfo(TAG, response.headers() + "");
                QueueInfoResponse model = response.body();

                if (null == model)
                {
                    //通知上层数据异常处理404或者转换失败
                    sendBroadcastWithError(broadMsg, NotifyMessage.RET_ERROR_RESPONSE);
                    return;
                }

                //200 model不为空 请求成功
                if (MobileCC.MESSAGE_OK.equals(model.getRetcode()))
                {
                    //正在排队
                    long position = model.getResult().getPosition();
                    int onlineAgentNum = model.getResult().getOnlineAgentNum();
                    long longestWaitTime = model.getResult().getLongestWaitTime();

                    QueueInfo queueInfo = new QueueInfo();
                    queueInfo.setPosition(position);
                    queueInfo.setOnlineAgentNum(onlineAgentNum);
                    queueInfo.setLongestWaitTime(longestWaitTime);
                    broadMsg.setQueueInfo(queueInfo);
                }
                else
                {
                    //非排队状态
                    logInfo(TAG, "Not queuing");
                }
                sendBroadcast(broadMsg, model.getRetcode());
            }

            @Override
            public void onFailure(Call<QueueInfoResponse> call, Throwable t)
            {
                logError(TAG, "get queue info -> t = " + t.getMessage());
                sendBroadcastWithError(broadMsg, NotifyMessage.RET_ERROR_NETWORK);
            }
        });
    }

    /**
     * 会议连接
     */
    public void applyConf()
    {
        Retrofit retrofit = getRetrofit();
        if (null == retrofit)
        {
            return;
        }

        RequestManager requestManager = retrofit.create(RequestManager.class);
        Call call = requestManager.startConf(SystemConfig.getInstance().getGuid(),
                SystemConfig.getInstance().getCookie(),
                SystemConfig.getInstance().getUserName(),
                SystemConfig.getInstance().getVndID(),
                SystemConfig.getInstance().getAudioCallId());

        if (null == call)
        {
            return;
        }
        call.enqueue(new Callback<ResultResponse>()
        {
            private BroadMsg broadMsg = new BroadMsg(NotifyMessage.CALL_MSG_ON_APPLY_MEETING);

            @Override
            public void onResponse(Call<ResultResponse> call, Response<ResultResponse> response)
            {
                ResultResponse model = response.body();
                if (null == model)
                {
                    //通知上层数据异常处理404或者转换失败
                    sendBroadcastWithError(broadMsg, NotifyMessage.RET_ERROR_RESPONSE);
                    return;
                }

                //200 model不为空 请求成功
                if (MobileCC.MESSAGE_OK.equals(model.getRetcode()))
                {
                    logInfo(TAG, "apply Meeting sent");
                }
                else
                {
                    logInfo(TAG, "apply meeting fail");
                }
                sendBroadcast(broadMsg, model.getRetcode());
            }

            @Override
            public void onFailure(Call call, Throwable t)
            {
                logError(TAG, "t = " + t.getMessage());
                sendBroadcastWithError(broadMsg, NotifyMessage.RET_ERROR_NETWORK);
            }
        });
    }

    /**
     * 申请结束会议
     */
    public void stopConf()
    {
        Retrofit retrofit = getRetrofit();
        if (null == retrofit)
        {
            return;
        }

        RequestManager requestManager = retrofit.create(RequestManager.class);
        Call call = requestManager.stopConf(SystemConfig.getInstance().getGuid(),
                SystemConfig.getInstance().getCookie(),
                SystemConfig.getInstance().getUserName(),
                SystemConfig.getInstance().getVndID(),
                SystemConfig.getInstance().getConfId());
        if (null == call)
        {
            return;
        }
        call.enqueue(new Callback<StopConfResponse>()
        {
            private BroadMsg broadMsg = new BroadMsg(NotifyMessage.CALL_MSG_ON_STOP_MEETING);

            @Override
            public void onResponse(Call<StopConfResponse> call, Response<StopConfResponse> response)
            {
                StopConfResponse model = response.body();
                if (null == model)
                {
                    //通知上层数据异常处理404或者转换失败
                    sendBroadcastWithError(broadMsg, NotifyMessage.RET_ERROR_RESPONSE);
                    return;
                }

                //200 model不为空 请求成功
                if (MobileCC.MESSAGE_OK.equals(model.getRetcode()))
                {
                    logInfo(TAG, "stop Conf success");
                }
                else
                {
                    logInfo(TAG, "stop Conf fail");
                }
                sendBroadcast(broadMsg, model.getRetcode());
            }

            @Override
            public void onFailure(Call<StopConfResponse> call, Throwable t)
            {
                logError(TAG, "t = " + t.getMessage());
                sendBroadcastWithError(broadMsg, NotifyMessage.RET_ERROR_NETWORK);
            }
        });
    }


    /**
     * 音视频连接
     *
     * @param accessCode accessCode
     * @param callData callData
     * @param mediaAbility 0:audio, 1:video
     */
    public void msConnect(String accessCode, String callData, int mediaAbility)
    {
        Retrofit retrofit = getRetrofit();

        if (null == retrofit)
        {
            return;
        }
        RequestManager requestManager = retrofit.create(RequestManager.class);
        Call call = requestManager.getMSConnect(SystemConfig.getInstance().getGuid(),
                SystemConfig.getInstance().getCookie(),
                SystemConfig.getInstance().getUserName(),
                SystemConfig.getInstance().getVndID(),
                new GetConnectRequest(MEDIA_TYPE_AUDIO,
                        SystemConfig.getInstance().getUserName(),
                        accessCode,
                        callData,
                        SystemConfig.getInstance().getUvid(),
                        SystemConfig.getInstance().getVerifyCode(),
                        mediaAbility));
        logInfo(TAG, "Uvid ===== " + SystemConfig.getInstance().getUvid());

        if (null == call)
        {
            return;
        }
        call.enqueue(new Callback<ResultResponse>()
        {
            private BroadMsg broadMsg = new BroadMsg(NotifyMessage.CALL_MSG_ON_CONNECT);

            @Override
            public void onResponse(Call<ResultResponse> call, Response<ResultResponse> response)
            {
                ResultResponse model = response.body();
                if (null == model)
                {
                    //通知上层数据异常处理404或者转换失败
                    broadMsg.setType(MEDIA_TYPE_AUDIO + "");
                    sendBroadcastWithError(broadMsg, NotifyMessage.RET_ERROR_RESPONSE);
                    return;
                }

                //200 model不为空 请求成功
                if (MobileCC.MESSAGE_OK.equals(model.getRetcode()))
                {
                    logInfo(TAG, "Retcode:" + model.getRetcode() + "\n Message:"
                            + model.getMessage() + "\n Event:" + model.getResult());
                    String resultMsg = model.getResult();
                    SystemConfig.getInstance().setAudioCallId(resultMsg);
                }
                else
                {
                    logInfo(TAG, "Audio ability error ->Retcode:" + model.getRetcode()
                            + "\n Message:" + model.getMessage() + "\n  Result:" + model.getResult());
                }

                broadMsg.setType(MEDIA_TYPE_AUDIO + "");
                sendBroadcast(broadMsg, model.getRetcode());
            }

            @Override
            public void onFailure(Call<ResultResponse> call, Throwable t)
            {
                logError(TAG, "t = " + t.getMessage());
                broadMsg.setType(MEDIA_TYPE_AUDIO + "");
                sendBroadcastWithError(broadMsg, NotifyMessage.RET_ERROR_NETWORK);
            }
        });
    }

    /**
     * 释放呼叫
     */
    public void dropCall()
    {
        logInfo(TAG, "dropcall()");
        Retrofit retrofit = getRetrofit();
        if (null == retrofit)
        {
            logInfo(TAG, "dropcall  null == retrofit");
            return;
        }

        RequestManager requestManager = retrofit.create(RequestManager.class);
        String callId;

        //获取不通类型呼叫的callId进行释放呼叫，音视频用同一个id
        if (MobileCC.AUDIO_CALL.equals(SystemConfig.getInstance().getCallType())
                || MobileCC.VIDEO_CALL.equals(SystemConfig.getInstance().getCallType()))
        {
            callId = SystemConfig.getInstance().getAudioCallId();
        }
        else
        {
            callId = SystemConfig.getInstance().getTextCallId();
        }

        if (StringUtils.isEmpty(callId))
        {
            logError(TAG, "dropcall  callId id empty");
            return;
        }
        Call call = requestManager.dropCall(SystemConfig.getInstance().getGuid(),
                SystemConfig.getInstance().getCookie(),
                SystemConfig.getInstance().getUserName(),
                SystemConfig.getInstance().getVndID(),
                callId);

        if (null == call)
        {
            logInfo(TAG, "dropcall  null == call");
            return;
        }

        call.enqueue(new Callback<DropCallResponse>()
        {
            private BroadMsg broadMsg = new BroadMsg(NotifyMessage.CALL_MSG_ON_DROPCALL);

            @Override
            public void onResponse(Call<DropCallResponse> call, Response<DropCallResponse> response)
            {
                DropCallResponse model = response.body();
                if (null == model)
                {
                    //通知上层数据异常处理404或者转换失败
                    sendBroadcastWithError(broadMsg, NotifyMessage.RET_ERROR_RESPONSE);
                }
                else
                {
                    //200 model不为空 请求成功
                    logInfo(TAG, "dropcall retcode is :" + model.getRetcode());
                    sendBroadcast(broadMsg, model.getRetcode());
                }
            }

            @Override
            public void onFailure(Call<DropCallResponse> call, Throwable t)
            {
                logError(TAG, "drop call -> t = " + t.getMessage());
                sendBroadcastWithError(broadMsg, NotifyMessage.RET_ERROR_NETWORK);
            }
        });
    }

    /**
     * 取消排队
     */
    public void cancelQueue()
    {
        logInfo(TAG, "cancelQueue()");
        Retrofit retrofit = getRetrofit();
        if (null == retrofit)
        {
            logInfo(TAG, "cancelQueue  null == retrofit");
            return;
        }

        RequestManager requestManager = retrofit.create(RequestManager.class);
        String callId;

        //获取不通类型呼叫的callId，音视频用同一个id
        if (MobileCC.AUDIO_CALL.equals(SystemConfig.getInstance().getCallType())
                || MobileCC.VIDEO_CALL.equals(SystemConfig.getInstance().getCallType()))
        {
            callId = SystemConfig.getInstance().getAudioCallId();
        }
        else
        {
            callId = SystemConfig.getInstance().getTextCallId();
        }

        if (StringUtils.isEmpty(callId))
        {
            logError(TAG, "cancelQueue  callId id empty");
            return;
        }
        Call call = requestManager.cancelQueue(SystemConfig.getInstance().getGuid(),
                SystemConfig.getInstance().getCookie(),
                SystemConfig.getInstance().getUserName(),
                SystemConfig.getInstance().getVndID(),
                callId);

        if (null == call)
        {
            logInfo(TAG, "cancelQueue  null == call");
            return;
        }


        call.enqueue(new Callback<DropCallResponse>()
        {
            private BroadMsg broadMsg = new BroadMsg(NotifyMessage.CALL_MSG_ON_CANCEL_QUEUE);

            @Override
            public void onResponse(Call<DropCallResponse> call, Response<DropCallResponse> response)
            {
                DropCallResponse model = response.body();
                if (null == model)
                {
                    //通知上层数据异常处理404或者转换失败
                    sendBroadcastWithError(broadMsg, NotifyMessage.RET_ERROR_RESPONSE);
                }
                else
                {
                    //200 model不为空 请求成功
                    logInfo(TAG, "cancelQueue retcode is :" + model.getRetcode());
                }
            }

            @Override
            public void onFailure(Call<DropCallResponse> call, Throwable t)
            {
                logError(TAG, "cancelQueue -> t = " + t.getMessage());
                sendBroadcastWithError(broadMsg, NotifyMessage.RET_ERROR_NETWORK);
            }
        });
    }

    /**
     * 释放文字连接
     */
    public void dropTextCall()
    {
        Retrofit retrofit = getRetrofit();
        if (null == retrofit)
        {
            return;
        }
        RequestManager requestManager = retrofit.create(RequestManager.class);
        Call call = requestManager.dropCall(SystemConfig.getInstance().getGuid(),
                SystemConfig.getInstance().getCookie(),
                SystemConfig.getInstance().getUserName(),
                SystemConfig.getInstance().getVndID(),
                SystemConfig.getInstance().getTextCallId());
        if (null == call)
        {
            return;
        }
        call.enqueue(new Callback<DropCallResponse>()
        {
            private BroadMsg broadMsg = new BroadMsg(NotifyMessage.CALL_MSG_ON_DROPCALL);

            @Override
            public void onResponse(Call<DropCallResponse> call, Response<DropCallResponse> response)
            {
                DropCallResponse model = response.body();
                if (null == model)
                {
                    //通知上层数据异常处理404或者转换失败
                    sendBroadcastWithError(broadMsg, NotifyMessage.RET_ERROR_RESPONSE);
                }
                else
                {
                    //200 model不为空 请求成功
                    logInfo(TAG, "dropTextCall() retcode is :" + model.getRetcode());
                    sendBroadcast(broadMsg, model.getRetcode());
                }
            }

            @Override
            public void onFailure(Call<DropCallResponse> call, Throwable t)
            {
                logError(TAG, "dropTextCall()  -> t = " + t.getMessage());
                sendBroadcastWithError(broadMsg, NotifyMessage.RET_ERROR_NETWORK);
            }
        });
    }

    /**
     * 获取 Retrofit 实例
     *
     * @return Retrofit
     */
    private Retrofit getRetrofit()
    {
        client = createOkhttp();
        localRetrofit = new Retrofit.Builder()
                .baseUrl(SystemConfig.getInstance().getTransSecurity() + SystemConfig.getInstance().getServerIp()
                        + ":" + SystemConfig.getInstance().getServerPort())
                .addConverterFactory(GsonConverterFactory.create()).client(client)
                .build();
        logInfo(TAG, "path " + SystemConfig.getInstance().getTransSecurity() + SystemConfig.getInstance().getServerIp()
                + ":" + SystemConfig.getInstance().getServerPort());
        return localRetrofit;
    }

    /**
     * 获取OkHttpClient
     *
     * @return OkHttpClient
     */
    private OkHttpClient createOkhttp()
    {
        try
        {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {new MyTrustManager()};

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = new Tls12SocketFactory(sslContext.getSocketFactory());

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
            builder.readTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
            builder.writeTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
            builder.sslSocketFactory(sslSocketFactory);
            builder.hostnameVerifier(new MyHostnameVerifier());

            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        }
        catch (NoSuchAlgorithmException e)
        {
            logError(TAG, "createOkhttp failed due to NoSuchAlgorithmException.");
            return null;
        }
        catch (KeyManagementException e)
        {
            logError(TAG, "createOkhttp failed due to KeyManagementException.");
            return null;
        }
        catch (RuntimeException exception)
        {
            throw exception;
        }
    }

    private static class MyTrustManager implements X509TrustManager
    {
        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException
        {
        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException
        {
        }

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers()
        {
            return new java.security.cert.X509Certificate[]{};
        }
    }

    private static class MyHostnameVerifier implements HostnameVerifier
    {
        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    }

    /**
     * 获取验证码
     */
    public void getVerifyCode()
    {
        Retrofit retrofit = getRetrofit();

        if (null == retrofit)
        {
            return;
        }
        RequestManager requestManager = retrofit.create(RequestManager.class);
        Call call = requestManager.getVerifycode(SystemConfig.getInstance().getGuid(),
                SystemConfig.getInstance().getCookie(),
                SystemConfig.getInstance().getVndID(),
                SystemConfig.getInstance().getUserName());
        if (null == call)
        {
            return;
        }

        call.enqueue(new Callback<ResultResponse>()
        {
            private BroadMsg broadMsg = new BroadMsg(NotifyMessage.CALL_MSG_ON_VERIFYCODE);

            @Override
            public void onResponse(Call<ResultResponse> call, Response<ResultResponse> response)
            {
                ResultResponse model = response.body();

                if (null == model)
                {
                    //通知上层数据异常处理404或者转换失败
                    sendBroadcastWithError(broadMsg, NotifyMessage.RET_ERROR_RESPONSE);
                    return;
                }

                //200 model不为空 请求成功
                if (MobileCC.MESSAGE_OK.equals(model.getRetcode()))
                {
                    //获取验证码数据成功
                    logInfo(TAG, "get verifycode success");
                }
                else
                {
                    //获取验证码失败
                    logInfo(TAG, "getVerifyCode fail" + model.getRetcode());
                }
                sendBroadcast(broadMsg, model.getRetcode(), model.getResult());
            }

            @Override
            public void onFailure(Call<ResultResponse> call, Throwable t)
            {
                logError(TAG, "get verifycode fail -> t = " + t.getMessage());
                sendBroadcastWithError(broadMsg, NotifyMessage.RET_ERROR_NETWORK);
            }
        });
    }

    /**
     * 记录日志信息
     *
     * @param tagName 日志标签
     * @param content 日志内容
     */
    private void logInfo(String tagName, String content)
    {
        LogUtil.d(tagName, " " + content);
    }

    /**
     * 记录错误信息
     *
     * @param tagName 日志标签
     * @param content 日志内容
     */
    private void logError(String tagName, String content)
    {
        LogUtil.e(tagName, " " + content);
    }

    /**
     * 发送广播
     *
     * @param broadMsg BroadMsg
     * @param errorCode int
     */
    private void sendBroadcastWithError(BroadMsg broadMsg, int errorCode)
    {
        RequestCode requestCode = new RequestCode();
        requestCode.setErrorCode(errorCode);
        broadMsg.setRequestCode(requestCode);
        CCAPP.getInstance().sendBroadcast(broadMsg);
    }

    /**
     * 发送广播
     *
     * @param broadMsg BroadMsg
     */
    private void sendBroadcast(BroadMsg broadMsg)
    {
        sendBroadcast(broadMsg, "");
    }

    /**
     * 发送广播
     *
     * @param broadMsg BroadMsg
     * @param retCode String
     */
    private void sendBroadcast(BroadMsg broadMsg, String retCode)
    {
        sendBroadcast(broadMsg, retCode, "");
    }

    /**
     * 发送广播
     *
     * @param broadMsg BroadMsg
     * @param retCode String
     * @param msg String
     */
    private void sendBroadcast(BroadMsg broadMsg, String retCode, String msg)
    {
        RequestCode requestCode = new RequestCode();
        requestCode.setRCode(retCode);
        broadMsg.setRequestCode(requestCode);

        if (!StringUtils.isEmpty(msg))
        {
            RequestInfo requestInfo = new RequestInfo();
            requestInfo.setMsg(msg);
            broadMsg.setRequestInfo(requestInfo);
        }
        CCAPP.getInstance().sendBroadcast(broadMsg);
    }

    /**
     * 开启任务管理器
     */
    public void beginTimer()
    {
        if (null == timer)
        {
            timer = new Timer();
            timer.schedule(task, 1000, 1000);
        }
    }

    /**
     * 断网任务管理器
     */
    private TimerTask task = new TimerTask()
    {
        @Override
        public void run()
        {
            if (num == 120)
            {
                BroadMsg broadMsg = new BroadMsg(NotifyMessage.FORCE_LOGOUT);
                sendBroadcast(broadMsg);
                //清理状态
                SystemConfig.getInstance().clearStatus();
                //释放音视频资源
                MobileCC.getInstance().releaseCall();
                //释放会议资源
                ConferenceMgr.getInstance().unInitConf();
            }
            else
            {
                num++;
            }
        }
    };
}
