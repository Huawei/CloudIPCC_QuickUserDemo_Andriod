/**
 * Copyright 2015 Huawei Technologies Co., Ltd. All rights reserved.
 * eSDK is licensed under the Apache License, Version 2.0 ^(the "License"^);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.huawei.esdk;

import android.content.Context;
import android.os.Environment;
import android.view.ViewGroup;

import com.huawei.esdk.conference.ConferenceMgr;
import com.huawei.esdk.service.ics.ICSService;
import com.huawei.esdk.service.ics.SystemConfig;
import com.huawei.esdk.service.ics.model.BroadMsg;
import com.huawei.esdk.service.ics.model.RequestCode;
import com.huawei.esdk.service.call.CallManager;
import com.huawei.esdk.utils.LogUtil;
import com.huawei.esdk.utils.StringUtils;
import com.huawei.esdk.service.video.VideoControl;

import java.io.File;
import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CC接口调用类
 */
public final class MobileCC
{
    /**
     * 视屏模式 流畅优先
     */
    public static final int VIDEOMODE_FLUENT = 1;

    /**
     * 视屏模式 画质优先
     */
    public static final int VIDEOMODE_QUALITY = 0;

    /**
     * 视屏控制-开启
     */
    public static final int START = 0x04;

    /**
     * 视屏控制-停止
     */
    public static final int STOP = 0x08;

    /**
     * 呼叫类型-视频呼叫
     */
    public static final String VIDEO_CALL = "0";

    /**
     * 呼叫类型-语音呼叫
     */
    public static final String AUDIO_CALL = "3";

    /**
     * 消息类型-文字
     */
    public static final String MESSAGE_TYPE_TEXT = "1";

    /**
     * 消息类型-语音
     */
    public static final String MESSAGE_TYPE_AUDIO = "2";

    /**
     * 返回码为"0",表示成功
     */
    public static final String MESSAGE_OK = "0";

    /**
     * 音频路由-扬声器
     */
    public static final int AUDIO_ROUTE_SPEAKER = 1;

    /**
     * 音频路由-听筒
     */
    public static final int AUDIO_ROUTE_RECEIVER = 0;

    /**
     * 文字最大长度
     *
     * 参考接口文档
     */
    public static final int TEXT_LENGTH_MAX = 6144;

    private static final Object LOCKOBJECT = new Object();
    private static final String TAG = "MobileCC";
    private static MobileCC instance;

    private MobileCC()
    {
    }

    /**
     * 实例获取
     *
     * @return MobileCC
     */
    public static MobileCC getInstance()
    {
        synchronized (LOCKOBJECT)
        {
            if (null == instance)
            {
                instance = new MobileCC();
            }
            return instance;
        }
    }

    /**
     * 初始化Tup
     */
    public void initTup()
    {
        CallManager.getInstance().tupInit();
        CallManager.getInstance().loadMeeting();
        logInfo(TAG, "initTup", "Application init");
    }

    /**
     * 停止Tup
     */
    public void unInitTup()
    {
        CallManager.getInstance().tupUninit();
        logInfo(TAG, "unInitTup", "Tup unInit");
    }

    /**
     * 接入网关地址设置
     *
     * @param ipStr         ipStr
     * @param portStr       portStr
     * @return int
     */
    public int setHostAddress(final String ipStr, String portStr, String host)
    {
        if (StringUtils.isEmpty(portStr)
                || !isNumber(portStr)
                || StringUtils.isEmpty(host))
        {
            logError(TAG, "setHostAddress", "IPStr=" + "***" + ", portStr="
                    + portStr + ",host=" + host + ", transSecurity=" + "transSecurity");
            return NotifyMessage.RET_ERROR_PARAM;
        }

        int port = 0;
        try
        {
            port = Integer.parseInt(portStr);
        } catch (NumberFormatException e)
        {
            logError(TAG, "setHostAddress", "NumberException!");
        }
        if (port <= 0 || port >= 65535)
        {
            logError(TAG, "setHostAddress", "ipStr=" + "***" + ", portStr="
                    + portStr + ",host=" + host);
            return NotifyMessage.RET_ERROR_PARAM;
        }

        SystemConfig.getInstance().setServerPort(port);
        SystemConfig.getInstance().setHost(host);

        if (StringUtils.isEmpty(ipStr) || !isIp(ipStr))
        {
            return NotifyMessage.RET_ERROR_PARAM;   //ip为空或者不匹配，返回上层尝试域名解析
        }

        SystemConfig.getInstance().setServerIp(ipStr);

        logInfo(TAG, "setHostAddress", "ipStr=" + "***" + ", portStr=" + portStr + ",host=" + host);
        return NotifyMessage.RET_OK;
    }

    /**
     * 解析域名
     *
     * @param host  host
     * */
    public void parseHost(final String host)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                getInetAddress(host);
            }
        }).start();
    }

    /**
     * 登录
     *
     * @param vdnId    vdnId
     * @param userName userName
     * @return int
     */
    public int login(String vdnId, String userName)
    {
        if (StringUtils.isEmpty(vdnId)
                || StringUtils.isEmpty(userName)
                || !isName(userName)
                || userName.length() > 20
                || !isNumber(vdnId)
                || vdnId.length() > 4)
        {
            logError(TAG, "login", "vdnId=" + vdnId + ", userName = " + userName);
            return NotifyMessage.RET_ERROR_PARAM;
        }

        SystemConfig.getInstance().setUserName(userName);
        SystemConfig.getInstance().setVndID(vdnId);
        ICSService.getInstance().login(userName);
        logInfo(TAG, "login", "vdnId=" + vdnId + ", userName = " + userName);
        return NotifyMessage.RET_OK;
    }

    /**
     * 注销
     */
    public int logout()
    {
        //会议状态
        if (SystemConfig.getInstance().isMeeting())
        {
            return Constant.IS_MEETING;
        }

        //文字通话状态
        if (SystemConfig.getInstance().isTextConnected())
        {
            return Constant.IS_TEXT_CALLING;
        }

        //音视频通话状态
        if (SystemConfig.getInstance().isAudioConnected() || SystemConfig.getInstance().isVideoConnected())
        {
            return Constant.IS_CALLING;
        }

        if (SystemConfig.getInstance().isQueuing())
        {
            cancelQueue();
        }

        ConferenceMgr.getInstance().releaseConf();
        ICSService.getInstance().logout();
        logInfo(TAG, "logout", "");
        return Constant.OK;
    }

    /**
     * 建立文字连接
     *
     * @param accessCode accessCode
     * @param callData   callData
     * @param verifyCode verifyCode
     * @return int
     */
    public int webChatCall(String accessCode, String callData, String verifyCode)
    {
        //检查参数是否规范
        if (StringUtils.isEmpty(accessCode)
                || null == callData
                || !isNumber(accessCode)
                || accessCode.length() > 24
                || callData.length() > 1024)
        {
            logError(TAG, "webChatCall", "accessCode = " + accessCode + ",callData= ***");
            return NotifyMessage.RET_ERROR_PARAM;
        }

        SystemConfig.getInstance().setVerifyCode(verifyCode);
        ICSService.getInstance().textConnect(accessCode, SystemConfig.getInstance().getUserName(),
                callData, SystemConfig.getInstance().getVerifyCode());
        logInfo(TAG, "webChatCall", "accessCode = " + accessCode + ",callData= ***");
        return NotifyMessage.RET_OK;
    }

    /**
     * 发起呼叫
     *
     * @param accessCode accessCode
     * @param callType   callType
     * @param callData   callData
     * @param verifyCode verifyCode
     * @return int
     */
    public int makeCall(String accessCode, String callType, String callData, String verifyCode, int mediaAbility)
    {
        //check params
        if (StringUtils.isEmpty(accessCode)
                || null == callData
                || StringUtils.isEmpty(callType)
                || !isNumber(accessCode)
                || accessCode.length() > 24
                || callData.length() > 1024
                || verifyCode.length() > 10)
        {
            logError(TAG, "makeCall", "accessCode = " + accessCode + ",callType="
                    + callType + ",callData= ***" + "verifyCode" + verifyCode);
            return NotifyMessage.RET_ERROR_PARAM;
        }

        SystemConfig.getInstance().setCallType(callType);
        SystemConfig.getInstance().setVerifyCode(verifyCode);
        SystemConfig.getInstance().setCurrentMediaAbility(mediaAbility);

        //make request to get audio or video ability
        ICSService.getInstance().msConnect(accessCode, callData, mediaAbility);
        logInfo(TAG, "makeCall", "accessCode = " + accessCode + ",callType="
                + callType + ",callData= ***" + "verifyCode" + verifyCode);
        return NotifyMessage.RET_OK;
    }

    /**
     * 释放文字呼叫
     */
    public void releaseText()
    {
        //先取消排队
        if (SystemConfig.getInstance().isQueuing())
        {
            cancelQueue();
        }

        //释放文字
        if (SystemConfig.getInstance().isTextConnected())
        {
            ICSService.getInstance().dropTextCall();
            logInfo(TAG, "releaseText", "releaseTextCall");
        }
    }

    /**
     * 停止音视频呼叫
     */
    public void stopCall()
    {
        //先取消排队
        if (SystemConfig.getInstance().isQueuing())
        {
            cancelQueue();
        }

        //停止视频
        if (SystemConfig.getInstance().isVideoConnected())
        {
            ICSService.getInstance().dropCall();
            logInfo(TAG, "stopCall", "stopVideo");
        }

        //停止语音
        if (SystemConfig.getInstance().isAudioConnected())
        {
            ICSService.getInstance().dropCall();
            logInfo(TAG, "stopCall", "stopAudio");
        }
    }

    /**
     * 释放音视频呼叫
     */
    public void releaseCall()
    {
        //释放呼叫
        CallManager.getInstance().releaseCall();
        VideoControl.getIns().clearSurfaceView();
        logInfo(TAG, "releaseCall", "");
    }

    /**
     * 发送消息
     *
     * @param content content
     * @return int
     */
    public int sendMsg(String content)
    {
        //判断文字是否超过最大长度
        if (content.length() > TEXT_LENGTH_MAX)
        {
            logError(TAG, "sendMsg", "content's length is" + content.length());
            return NotifyMessage.RET_ERROR_PARAM;
        }

        ICSService.getInstance().sendMsg(content);
        logInfo(TAG, "sendMsg", "content=" + content);
        return NotifyMessage.RET_OK;
    }

    /**
     * 发起会议请求
     */
    public void startConf()
    {
        if (!isConfAvaliable())
        {
            return;
        }
        ICSService.getInstance().applyConf();
        logInfo(TAG, "startConf", "");
    }

    /**
     * 终止会议请求
     */
    public void stopConf()
    {
        ICSService.getInstance().stopConf();
        logInfo(TAG, "stopConf", "");
    }

    /**
     * 释放会议
     */
    public void releaseConf(boolean needUnRegisterConf)
    {
        if (!SystemConfig.getInstance().isMeeting())
        {
            return;
        }
        logInfo(TAG, "releaseConf", "");

        SystemConfig.getInstance().setIsMeeting(false);
        if (needUnRegisterConf)
        {
            ConferenceMgr.getInstance().terminateConf();
        }
        ConferenceMgr.getInstance().unInitConf();
        stopConf();
    }

    /**
     * 设置视频容器
     *
     * @param context    context
     * @param localView  localView
     * @param remoteView remoteView
     */
    public void setVideoContainer(Context context, ViewGroup localView,
                                  ViewGroup remoteView)
    {
        VideoControl.getIns().setVideoContainer(context, localView,
                remoteView);

        logInfo(TAG, "setVideoContainer", "context=" + context + ",localView="
                + localView + ",remoteView=" + remoteView);
    }

    /**
     * 设置视频模式
     *
     * 0: 画质优先（默认）
     * 1: 流畅优先
     */
    public void setVideoMode(int videoMode)
    {
        logInfo(TAG, "setVideoMode", "videoMode=" + videoMode);
        CallManager.getInstance().setVideoMode(videoMode);
    }

    /**
     * 获取通话的分辨率带宽延迟等信息
     *
     * @return boolean
     */
    public boolean getChannelInfo()
    {
        //具体信息通过广播CALL_MSG_GET_VIDEO_INFO返回
        logInfo(TAG, "getChannelInfo", "");
        return CallManager.getInstance().getChannelInfo();
    }

    /**
     * 取消排队
     */
    public void cancelQueue()
    {
        if (!SystemConfig.getInstance().isQueuing())
        {
            return;
        }
        ICSService.getInstance().cancelQueue();
        logInfo(TAG, "cancelQueue", "");
    }

    /**
     * 获取版本信息
     *
     * @return string
     */
    public String getVersion()
    {
        logInfo(TAG, "getVersion", "");
        return "1.0.1";
    }

    private boolean isName(String name)
    {
        String regEx = "^[A-Za-z0-9]*$";
        return Pattern.compile(regEx).matcher(name).find();
    }

    private boolean isNumber(String port)
    {
        String regEx = "^[0-9]*$";
        return Pattern.compile(regEx).matcher(port).find();
    }

    private boolean isIp(String ipAddress)
    {
        String ip = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pattern = Pattern.compile(ip);
        Matcher matcher = pattern.matcher(ipAddress);
        return matcher.matches();
    }


    /**
     * SIP服务器地址设置
     *
     * @param ipStr   ipStr
     * @param portStr portStr
     * @return int
     */
    public int setSIPServerAddress(String ipStr, String portStr)
    {
        if (StringUtils.isEmpty(ipStr)
                || StringUtils.isEmpty(portStr)
                || !isIp(ipStr)
                || !isNumber(portStr))
        {
            logError(TAG, "setSIPServerAddress", "IPStr=" + ipStr + ",portStr=" + portStr);
            return NotifyMessage.RET_ERROR_PARAM;
        }
        SystemConfig.getInstance().initSIPServerAddr(ipStr, portStr);
        logInfo(TAG, "setSIPServerAddress", "ipStr=" + ipStr + ",portStr=" + portStr);
        return NotifyMessage.RET_OK;
    }

    /**
     * 改变音频路由
     *
     * @param route route
     * @return boolean
     */
    public boolean changeAudioRoute(int route)
    {
        int nRet = 1;

        //1:扬声器  0：听筒
        if (AUDIO_ROUTE_SPEAKER == route || AUDIO_ROUTE_RECEIVER == route)
        {
            nRet = CallManager.getInstance().setSpeakerOn(route);
            logInfo(TAG, "changeAudioRoute", "device=" + route);
        }
        else
        {
            logError(TAG, "changeAudioRoute", "device=" + route);
        }

        return NotifyMessage.RET_OK == nRet;
    }

    /**
     * 切换摄像头
     */
    public void switchCamera()
    {
        if (!SystemConfig.getInstance().isVideoConnected())
        {
            return;
        }
        VideoControl.getIns().switchCamera();
        logInfo(TAG, "switchCamera", "");
    }

    /**
     * 设置传输加密
     *
     * @param enableTLS  enableTLS
     * @param enableSRTP enableSRTP
     */
    public void setTransportSecurity(boolean enableTLS, boolean enableSRTP)
    {
        SystemConfig.getInstance().setTLSEncoded(enableTLS);
        SystemConfig.getInstance().setSRTPEncoded(enableSRTP);
        logInfo(TAG, "setTransportSecurity", "enableTLS=" + enableTLS + ",enableSRTP=" + enableSRTP);
    }

    private void logInfo(String tagName, String methodName, String content)
    {
        LogUtil.d(tagName, methodName + " " + content);
    }

    private void logError(String tagName, String methodName, String content)
    {
        LogUtil.e(tagName, methodName + " " + content);
    }

    /**
     * 视频后台控制
     *
     * @param operation operation
     */
    public void videoOperate(int operation)
    {
        CallManager.getInstance().videoControl(operation);
        logInfo(TAG, "videoOperate", "operation=" + operation);
    }

    /**
     * 设置视频角度
     *
     * @param cameraIndex cameraIndex
     * @param rotate rotate
     * @return boolean
     */
    public boolean setVideoRotate(int cameraIndex, int rotate)
    {
        if (rotate >= 360)
        {
            rotate  = rotate % 360;
        }
        logInfo(TAG, "setVideoRotate", "rotate=" + rotate);

        //将旋转角度rotate转换成{0,1,2,3}中的数字，setRotation()方法仅支持0,1,2,3这几个数字作为参数
        int result = rotate / 90;
        int nRet = CallManager.getInstance().setRotation(cameraIndex, result);
        return 0 == nRet;
    }

    /**
     * 获取验证码
     */
    public void getVerifyCode()
    {
        ICSService.getInstance().getVerifyCode();
    }

    /**
     * 设置匿名卡号
     * @param anonymousCard anonymousCard
     */
    public void setAnonymousCard(String anonymousCard)
    {
        SystemConfig.getInstance().setAnonymousCard(anonymousCard);
    }

    /**
     * 获取ip地址
     * @param host
     */
    private void getInetAddress(String  host)
    {
        String ipAddress = "";
        InetAddress result;
        BroadMsg broadMsg = new BroadMsg(NotifyMessage.PARSE_DOMAIN);
        RequestCode requestCode = new RequestCode();

        try
        {
            result = java.net.InetAddress.getByName(host);
            if (null != result)
            {
                ipAddress = result.getHostAddress();
            }
            else
            {
                requestCode.setErrorCode(NotifyMessage.RET_ERROR_RESPONSE);
            }
        }
        catch (Exception e)
        {
            requestCode.setErrorCode(NotifyMessage.RET_ERROR_RESPONSE);
            logError("getInetAddress", "", "exception");
        }
        if (!StringUtils.isEmpty(ipAddress))
        {
            logInfo("getInetAddress", "", ipAddress);
            SystemConfig.getInstance().setServerIp(ipAddress);
            requestCode.setRCode(NotifyMessage.RET_OK + "");
        }
        else
        {
            requestCode.setErrorCode(NotifyMessage.RET_ERROR_RESPONSE);
            logError("getInetAddress", "", "IPAddress parse error");
        }
        broadMsg.setRequestCode(requestCode);
        CCAPP.getInstance().sendBroadcast(broadMsg);
    }

    /**
     * 检查是否可以发起文字呼叫
     */
    public boolean isTextCallAvailable()
    {
        return !(SystemConfig.getInstance().isTextConnected());
    }

    /**
     * 是否可以发起音视频呼叫
     *
     * @return boolean
     */
    public boolean isCallAvaiable()
    {
        return !(SystemConfig.getInstance().isAudioConnected() || SystemConfig.getInstance().isVideoConnected());
    }

    /**
     * 是否可以发起会议
     *
     * @return boolean
     */
    public boolean isConfAvaliable()
    {
        return (SystemConfig.getInstance().isAudioConnected() || SystemConfig.getInstance().isVideoConnected()) && !SystemConfig.getInstance().isMeeting();
    }

    /**
     * 设置共享容器
     * @param context
     * @param sharedView
     * @param shareType
     */
    public void setShareContainer(Context context, ViewGroup sharedView, int shareType)
    {
        ConferenceMgr.getInstance().setSharedViewContainer(context, sharedView, shareType);
        logInfo(TAG, "setShareContainer", "context=" + context + ",sharedView=" + sharedView + ",shareType" + shareType);
    }
}
