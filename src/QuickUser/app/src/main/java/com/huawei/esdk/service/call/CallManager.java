package com.huawei.esdk.service.call;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import com.huawei.esdk.MobileCC;
import com.huawei.esdk.CCAPP;
import com.huawei.esdk.service.ics.model.BroadMsg;
import com.huawei.esdk.NotifyMessage;
import com.huawei.esdk.service.ics.model.RequestCode;
import com.huawei.esdk.service.ics.model.RequestInfo;
import com.huawei.esdk.utils.LogUtil;
import com.huawei.esdk.utils.StringUtils;
import com.huawei.esdk.service.ics.SystemConfig;
import com.huawei.esdk.service.video.StreamInfo;
import com.huawei.esdk.service.video.VideoConfig;
import com.huawei.esdk.service.video.VideoControl;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import common.HdacceleType;
import common.TupBool;
import common.TupCallParam;
import common.VideoWndType;
import object.DecodeSuccessInfo;
import object.HdacceleRate;
import object.TupAudioQuality;
import object.TupCallCfgAudioVideo;
import object.TupCallCfgMedia;
import object.TupCallCfgSIP;
import object.VideoRenderInfo;
import object.VideoWndInfo;
import tupsdk.TupCall;
import tupsdk.TupCallManager;

/**
 * 呼叫控制类
 */
public final class CallManager extends CallManagerImpl
{
    private static final Object LOCKOBJECT = new Object();
    private static final String TAG = "CallManager";
    private static final int AUDIO_MIN = 10500;
    private static final int AUDIO_MAX = 10519;
    private static final int VIDEO_MIN = 10580;
    private static final int VIDEO_MAX = 10599;
    private static final String LIB_CONF = "TupConf";
    private static CallManager instance;
    private boolean initFlag = false;
    private TupCallManager tupManager;
    private TupCallCfgAudioVideo tupCallCfgAudioVideo = null;
    private Map<Integer, CallSession> calls = null;
    private VOIPParams voipConfig = new VOIPParams();
    private CallSession currentCallSession = null;
    private int callId;
    /**
     * 默认的带宽值
     */
    private int datarate = 512;
    /**
     * 视频模式
     * 默认流畅优先
     */
    private int vMode = 1;

    private CallManager()
    {
        calls = new ConcurrentHashMap();
        tupManager = new TupCallManager(this, CCAPP.getInstance());
        //加载需要的库
        tupManager.loadLibForUC();
        tupManager.setAndroidObjects();

        String logFile = "";
        if ("".equals(SystemConfig.getInstance().getLogPath()))
        {
            logFile = Environment.getExternalStorageDirectory().toString() + File.separator + NotifyMessage.QUICK_USER_LOG
                    + File.separator + "TUP";
        }
        else
        {
            logFile = SystemConfig.getInstance().getLogPath() + File.separator + "TUP";
        }

        File dirFile = new File(logFile);
        if (!(dirFile.exists()) && !(dirFile.isDirectory()))
        {
            if (dirFile.mkdirs())
            {
                logInfo(TAG, "CallManager", "mkdir " + dirFile.getPath());
            }
        }
        tupManager.logStart(NotifyMessage.LOG_LEVEL, 5 * 1000, 1, logFile);

        //音频配置和视频配置
        tupCallCfgAudioVideo = new TupCallCfgAudioVideo();
    }

    /**
     * 获取实例
     *
     * @return CallManager
     */
    public static CallManager getInstance()
    {
        synchronized (LOCKOBJECT)
        {
            if (null == instance)
            {
                instance = new CallManager();
            }
            return instance;
        }
    }

    /**
     * 获取带宽
     *
     * @return datarate
     */
    public int getDatarate()
    {
        return datarate;
    }

    /**
     * 设置带宽
     *
     * @param datarate datarate
     */
    public void setDatarate(int datarate)
    {
        this.datarate = datarate;
    }

    /**
     * 获取视频模式
     *
     * @return int
     */
    public int getvMode()
    {
        return vMode;
    }

    /**
     * 设置视屏模式
     *
     * @param vMode vMode
     */
    public void setvMode(int vMode)
    {
        this.vMode = vMode;
    }

    /**
     * 加载会议组件
     *
     * @return boolean
     */
    public boolean loadMeeting()
    {
        System.loadLibrary(LIB_CONF);
        return true;
    }

    /**
     * 获取voip配置
     *
     * @return VOIPParams
     */
    public VOIPParams getVoipConfig()
    {
        return voipConfig;
    }

    /**
     * Tup配置
     */
    private void tupConfig()
    {
        configMedia();
        configAudioAndVideo();
        configSip();
    }

    /**
     * 配置sip参数
     */
    private void configSip()
    {
        TupCallCfgSIP tupCallCfgSIP = new TupCallCfgSIP();

        tupCallCfgSIP.setSubAuthType(common.TupBool.TUP_TRUE);
        tupCallCfgSIP.setSipSupport100rel(common.TupBool.TUP_TRUE);

        //主服务器
        tupCallCfgSIP.setServerRegPrimary(this.getVoipConfig().getServerIp(),
                StringUtils.stringToInt(this.getVoipConfig().getServerPort()));

        //代理主服务器
        tupCallCfgSIP.setServerProxyPrimary(this.getVoipConfig().getServerIp(),
                StringUtils.stringToInt(this.getVoipConfig().getServerPort()));

        //本地网络地址
        String svnIp = StringUtils.getIpAddress();
        tupCallCfgSIP.setNetAddress(svnIp);

        //设置TLS
        if (SystemConfig.getInstance().isTLSEncoded())
        {
            tupCallCfgSIP.setSipTransMode(1);
        }
        else
        {
            tupCallCfgSIP.setSipTransMode(0);
        }

        // 刷新注册的时间
        tupCallCfgSIP.setSipRegistTimeout(getVoipConfig().getRegExpires());
        // 刷新订阅的时间
        tupCallCfgSIP.setSipSubscribeTimeout(getVoipConfig()
                .getSessionExpires());
        // 会话
        tupCallCfgSIP.setSipSessionTimerEnable(TupBool.TUP_TRUE);
        // 会话超时
        tupCallCfgSIP.setSipSessionTime(90);

        // 设置 DSCP
        tupCallCfgSIP.setDscpEnable(TupBool.TUP_TRUE);
        // 设置 tup 再注册的时间间隔， 注册失败后 间隔再注册
        tupCallCfgSIP.setSipReregisterTimeout(10);

        tupCallCfgSIP.setCheckCSeq(TupBool.TUP_FALSE);

        //设置匿名卡号
        String anonymousNum = SystemConfig.getInstance().getAnonymousCard() + "@" + SystemConfig.getInstance().getHost();
        logInfo(TAG, "configSip", "anonymousNum = " + anonymousNum);
        tupCallCfgSIP.setAnonymousNum(anonymousNum);

        tupManager.setCfgSIP(tupCallCfgSIP);
    }

    /**
     * 设置全局媒体配置
     */
    private void configMedia()
    {
        TupCallCfgMedia c = new TupCallCfgMedia();

        if (SystemConfig.getInstance().isSRTPEncoded())
        {
            c.setMediaSrtpMode(2); //设置SRTP  0不加密 1自动模式  2强制加密
        }
        else
        {
            c.setMediaSrtpMode(0); //设置SRTP  0不加密 1自动模式  2强制加密
        }

        VideoRenderInfo videoRenderInfo = new VideoRenderInfo();
        // (0:拉伸模式 1:(不拉伸)黑边模式 2:(不拉伸)裁剪模式)
        videoRenderInfo.setUlDisplaytype(0);
        videoRenderInfo.setRederType(VideoWndType.remote);
        c.setVideoRenderInfo(videoRenderInfo);

        //设置SDP带宽
        c.setCt(getDatarate());
        c.setMediaIframeMethod(TupBool.TUP_TRUE);
        c.setMediaFluidControl(TupBool.TUP_TRUE);
        tupManager.setCfgMedia(c);
    }

    /**
     * TUP初始化
     *
     * @return boolean
     */
    public boolean tupInit()
    {
        if (!initFlag)
        {
            logInfo(TAG, "tupInit", "TupCallManager_call_Init enter");
            tupManager.callInit();
            initFlag = true;
            logInfo(TAG, "tupInit", "TupCallManager_Init end");
            return true;
        }
        return false;
    }

    /**
     * 挂断
     */
    public void releaseCall()
    {
        if (null != currentCallSession)
        {
            currentCallSession.hangUp();
            currentCallSession = null;
        }
    }

    /**
     * 发起匿名呼叫
     *
     * @param number number
     * @return int
     */
    public int makeAnonymousCall(String number)
    {
        tupConfig();
        int callId = this.tupManager.startAnonmousCall(number);
        if (callId != -1)
        {
            TupCall tupCall = new TupCall(callId, 0);
            tupCall.setCaller(true);
            tupCall.setNormalCall(true);
            tupCall.setToNumber(number);
            CallSession iCSCallSession = new CallSession(this);

            iCSCallSession.setTupCall(tupCall);
            calls.put(callId, iCSCallSession);

            currentCallSession = iCSCallSession;
        }

        return callId;
    }

    /**
     * 视频控制
     *
     * @param operation operation
     */
    public void videoControl(int operation)
    {
        tupManager.vedioControl(callId, operation, 0x03);
    }

    /**
     * 发起匿名视屏
     *
     * @param number number
     * @return int
     */
    public int startAnonymousVideoCall(String number)
    {
        tupConfig();
        TupCall call = tupManager.StartAnonymousVideoCall(number);
        if (null == call)
        {
            return -1;
        }
        currentCallSession = new CallSession(this);
        currentCallSession.setTupCall(call);
        callId = call.getCallId();

        VideoControl.getIns().setCallId(callId + "");

        // 设置视频，必须放到ui线程来执行.
        new Handler(Looper.getMainLooper()).post(new MyRunnable());

        return callId;
    }

    private static class MyRunnable implements Runnable
    {
        @Override
        public void run()
        {
            VideoControl.getIns().deploySessionVideoCaps();
        }
    }

    /**
     * 视频摄像头采集方向
     *
     * @param cameraIndex cameraIndex
     * @param rotation rotation
     * @return int
     */
    public int setRotation(int cameraIndex, int rotation)
    {
        return currentCallSession.getTupCall().setCaptureRotation(cameraIndex, rotation);
    }

    /**
     * 音频配置和视频配置
     */
    private void configAudioAndVideo()
    {
        tupCallCfgAudioVideo.setAudioPortRange(AUDIO_MIN, AUDIO_MAX);
        tupCallCfgAudioVideo.setVideoPortRange(VIDEO_MIN, VIDEO_MAX);
        // audioCode ， 区分 3G 和WIFI
        tupCallCfgAudioVideo.setAudioCodec("0,8,18");

        //设置降噪处理
        tupCallCfgAudioVideo.setAudioAnr(1);

        //抗丢包设置
        tupCallCfgAudioVideo.setVideoErrorcorrecting(TupBool.TUP_TRUE);

        tupCallCfgAudioVideo.setAudioAec(1);
        // Dscp
        tupCallCfgAudioVideo.setDscpAudio(getVoipConfig().getAudioDSCP());
        tupCallCfgAudioVideo.setDscpVideo(getVoipConfig().getVideoDSCP());
        // net level
        tupCallCfgAudioVideo.setAudioNetatelevel(3);
        // opus 采样率
        tupCallCfgAudioVideo.setAudioClockrate(getVoipConfig().getOpusSamplingFreq());
        tupCallCfgAudioVideo.setForceIdrInfo(1);

        // 摄像头旋转
        // 可选，设置视频捕获（逆时针旋转）的角度。
        // 仅Android/iOS平台有效。
        // 0：0度；1：90度；2：180度；3：270度；
        // {0,1,2,3}
        tupCallCfgAudioVideo.setVideoCaptureRotation(0);

        tupCallCfgAudioVideo.setVideoCoderQuality(15);
        tupCallCfgAudioVideo.setVideoKeyframeinterval(10);
        tupCallCfgAudioVideo
                .setAudioDtmfMode(TupCallParam.CALL_E_DTMF_MODE.CALL_E_DTMF_MODE_CONST2833);

        //设置视频分辨率，参数分别为编码分辨率，最小编码分辨率，解码最大分辨率
        tupCallCfgAudioVideo.setVideoFramesize(8, 1, 11);
        HdacceleRate videoHdacceleRate = new HdacceleRate();
        videoHdacceleRate.setEncode(HdacceleType.Other);
        videoHdacceleRate.setDecode(HdacceleType.Other);
        tupCallCfgAudioVideo.setVideoHdaccelerate(videoHdacceleRate);

        int dataval = getDatarate();
        int maxDw;
        if (dataval <= 128)
        {
            maxDw = 600;
        }
        else
        {
            maxDw = 2000;
        }
        tupCallCfgAudioVideo.setVideoDatarate(dataval, dataval, maxDw, dataval);
        //视频模式
        tupCallCfgAudioVideo.setVideoTactic(vMode);

        tupCallCfgAudioVideo.setVideoClarityFluencyEnable(TupBool.TUP_TRUE);

        tupManager.setCfgAudioAndVideo(tupCallCfgAudioVideo);
        // 先只配置默认值
        tupManager.setMboileVideoOrient(0, 1, 1, 0, 0, 0);
    }

    /**
     * 设置视屏参数
     *
     * @param caps caps
     * @return int
     */
    public int setOrientParams(VideoConfig caps)
    {
        if (null == caps)
        {
            return -1;
        }
        VideoConfig.OrientParams params = caps.getOrientParams();
        int callId = StringUtils.stringToInt(caps.getSessionId(), -1);
        return tupManager.setMboileVideoOrient(callId, params.getCameraIndex(), params.getOrient(), params
                .getOrientPortrait(), params.getOrientLandscape(), params.getOrientSeascape());
    }

    /**
     * 封装的视频窗口操作
     *
     * @param videoWndType 窗口类型
     * @param index 视频index
     * @param callId 会话号
     * @param uiDisplayType 窗口拉伸模式(0:拉伸模式 1:(不拉伸)黑边模式 2:(不拉伸)裁剪模式)
     */
    public void videoWindowAction(VideoWndType videoWndType, int index, String callId, int uiDisplayType)
    {
        // 如果有callid调 update方法，没有callid调create方法
        if (StringUtils.isEmpty(callId))
        {
            createVideoWindow(videoWndType, index, uiDisplayType);
        }
        else
        {
            updateVideoWindow(videoWndType, index, callId, uiDisplayType);
        }
    }

    /**
     * @param videoWndType 窗口类型
     * @param index 视频index
     * @param uiDisplayType 窗口拉伸模式(0:拉伸模式 1:(不拉伸)黑边模式 2:(不拉伸)裁剪模式)
     */
    private void createVideoWindow(VideoWndType videoWndType, int index, int uiDisplayType)
    {
        tupManager.createVideoWindow(videoWndType.getIndex(), index, uiDisplayType);
    }

    /**
     * 有callId时需要调update接口
     *
     * @param videoWndType 窗口类型
     * @param index     index
     * @param callIdStr callIdStr
     * @param uiDisplayType 窗口拉伸模式(0:拉伸模式 1:(不拉伸)黑边模式 2:(不拉伸)裁剪模式)
     */
    private void updateVideoWindow(VideoWndType videoWndType, int index, String callIdStr, int uiDisplayType)
    {
        int callId = StringUtils.stringToInt(callIdStr);
        VideoWndInfo videoWndInfo = new VideoWndInfo();
        videoWndInfo.setVideowndType(videoWndType);
        videoWndInfo.setUlRender(index);
        videoWndInfo.setUlDisplayType(uiDisplayType);
        tupManager.updateVideoWindow(videoWndInfo, callId);
    }

    /**
     * 设置远端index
     *
     * @param index index
     */
    public void setVideoIndex(int index)
    {
        tupManager.mediaSetVideoIndex(index);
    }

    /**
     * 注销TUP
     */
    public void tupUninit()
    {
        if (initFlag)
        {
            tupManager.callUninit();
            logInfo(TAG, "tupUninit", "call_unInit end");
        }
    }

    @Override
    public void onCallComing(TupCall call)
    {
        if (tupManager.getRegState() != TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_REGISTERED)
        {
            call.endCall();
            return;
        }
        CallSession callSession = new CallSession(this);
        callSession.setTupCall(call);
        calls.put(callSession.getTupCall().getCallId(), callSession);
    }

    @Override
    public void onCallGoing(TupCall tupCall)
    {
        super.onCallGoing(tupCall);
    }

    @Override
    public void onCallConnected(TupCall call)
    {
        logInfo(TAG, "onCallConnected", "onCallConnected");

        BroadMsg broadMsg = new BroadMsg(NotifyMessage.CALL_MSG_ON_SUCCESS);
        CCAPP.getInstance().sendBroadcast(broadMsg);
    }

    @Override
    public void onCallEnded(TupCall call)
    {
        logInfo(TAG, "onCallEnded", "onCallEnded");
        BroadMsg broadMsg = new BroadMsg(NotifyMessage.CALL_MSG_ON_CALL_END);
        CCAPP.getInstance().sendBroadcast(broadMsg);
    }

    @Override
    public void onCallDestroy(TupCall call)
    {
        logInfo(TAG, "onCallDestroy", "onCallDestroy");
    }

    @Override
    public void onCallRTPCreated(TupCall call)
    {
        MobileCC.getInstance().changeAudioRoute(MobileCC.AUDIO_ROUTE_SPEAKER);
    }

    @Override
    public void onCallAddVideo(TupCall call)
    {
    }

    @Override
    public void onCallRefreshView(TupCall call)
    {
        BroadMsg broadMsg = new BroadMsg(NotifyMessage.CALL_MSG_REFRESH_LOCALVIEW);
        CCAPP.getInstance().sendBroadcast(broadMsg);
    }

    @Override
    public void onNetQualityChange(TupAudioQuality audioQuality)
    {
        logInfo(TAG, "onNetQualityChange", "NetQualityChange -> " + audioQuality.getAudioNetLevel());

        BroadMsg broadMsg = new BroadMsg(NotifyMessage.CALL_MSG_ON_NET_QUALITY_LEVEL);
        RequestCode requestCode = new RequestCode();
        requestCode.setNetLevel(audioQuality.getAudioNetLevel());
        broadMsg.setRequestCode(requestCode);

        CCAPP.getInstance().sendBroadcast(broadMsg);
    }

    @Override
    public void onDecodeSuccess(DecodeSuccessInfo decodeSuccessInfo)
    {
        BroadMsg broadMsg = new BroadMsg(NotifyMessage.CALL_MSG_REFRESH_REMOTEVIEW);
        CCAPP.getInstance().sendBroadcast(broadMsg);
    }

    /**
     * 设置视频模式
     *
     * 0: 画质优先（默认）
     * 1: 流畅优先
     * @param value value
     */
    public void setVideoMode(int value)
    {
        setvMode(value);
    }

    /**
     * 获取通话的分辨率带宽延迟等信息
     *
     * @return boolean
     */
    public boolean getChannelInfo()
    {
        if (null != currentCallSession)
        {
            currentCallSession.getTupCall().getChannelInfo();
            if (null != currentCallSession.getTupCall().getChannelInfo())
            {
                BroadMsg broadMsg = new BroadMsg(NotifyMessage.CALL_MSG_GET_VIDEO_INFO);
                //这里建一个StreamInfo对象进行赋值
                StreamInfo streamInfo = new StreamInfo();
                streamInfo.setEncoderSize(currentCallSession.getTupCall()
                        .getChannelInfo().getVideoStreamInfo().getEncoderSize());
                streamInfo.setSendFrameRate(currentCallSession.getTupCall()
                        .getChannelInfo().getVideoStreamInfo().getSendFrameRate());
                streamInfo.setVideoSendBitRate(currentCallSession.getTupCall()
                        .getChannelInfo().getVideoStreamInfo().getVideoSendBitRate());
                streamInfo.setVideoSendDelay(currentCallSession.getTupCall()
                        .getChannelInfo().getVideoStreamInfo().getVideoSendDelay());
                streamInfo.setVideoSendJitter(currentCallSession.getTupCall()
                        .getChannelInfo().getVideoStreamInfo().getVideoSendJitter());
                streamInfo.setVideoSendLossFraction(currentCallSession.getTupCall()
                        .getChannelInfo().getVideoStreamInfo().getVideoSendLossFraction());

                streamInfo.setDecoderSize(currentCallSession.getTupCall().getChannelInfo()
                        .getVideoStreamInfo().getDecoderSize());
                streamInfo.setRecvFrameRate(currentCallSession.getTupCall().getChannelInfo()
                        .getVideoStreamInfo().getRecvFrameRate());
                streamInfo.setVideoRecvBitRate(currentCallSession.getTupCall().getChannelInfo()
                        .getVideoStreamInfo().getVideoRecvBitRate());
                streamInfo.setVideoRecvDelay(currentCallSession.getTupCall().getChannelInfo()
                        .getVideoStreamInfo().getVideoRecvDelay());
                streamInfo.setVideoRecvJitter(currentCallSession.getTupCall().getChannelInfo()
                        .getVideoStreamInfo().getVideoRecvJitter());
                streamInfo.setVideoRecvLossFraction(currentCallSession.getTupCall().getChannelInfo()
                        .getVideoStreamInfo().getVideoRecvLossFraction());

                RequestInfo requestInfo = new RequestInfo();
                requestInfo.setStreamInfo(streamInfo);
                broadMsg.setRequestInfo(requestInfo);
                CCAPP.getInstance().sendBroadcast(broadMsg);
                return true;
            }
        }
        return false;
    }

    /**
     * 设置音频路由
     *
     * @param device 1:扬声器  0：听筒
     */
    public int setSpeakerOn(int device)
    {
        return tupManager.setMobileAudioRoute(device);
    }

    private void logInfo(String tagName, String methodName, String content)
    {
        LogUtil.d(tagName, methodName + " " + content);
    }
}
