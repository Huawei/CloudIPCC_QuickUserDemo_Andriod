package com.huawei.esdk.conference;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;

import com.huawei.esdk.CCAPP;
import com.huawei.esdk.Constant;
import com.huawei.esdk.MobileCC;
import com.huawei.esdk.NotifyMessage;
import com.huawei.esdk.service.ics.SystemConfig;
import com.huawei.esdk.service.ics.model.BroadMsg;
import com.huawei.esdk.service.ics.model.RequestCode;
import com.huawei.esdk.service.ics.model.RequestInfo;
import com.huawei.esdk.utils.LogUtil;
import com.huawei.meeting.ConfDefines;
import com.huawei.meeting.ConfExtendMsg;
import com.huawei.meeting.ConfExtendUserInfoMsg;
import com.huawei.meeting.ConfGLView;
import com.huawei.meeting.ConfInfo;
import com.huawei.meeting.ConfInstance;
import com.huawei.meeting.ConfMsg;
import com.huawei.meeting.ConfOper;
import com.huawei.meeting.ConfPrew;
import com.huawei.meeting.Conference;
import com.huawei.meeting.IConferenceUI;
import com.huawei.meeting.serverip;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;


/**
 * Created on 2018/1/3.
 */

public final class ConferenceMgr implements IConferenceUI
{
    private static final Object LOCKOBJECT = new Object();
    private static final String TAG = "ConferenceMgr";
    private static ConferenceMgr instance;
    private ConferenceInfo conferenceInfo = null;
    /**
     * 会议组件开关
     **/
    private int componentVal = ConfDefines.IID_COMPONENT_BASE
            | ConfDefines.IID_COMPONENT_DS
            | ConfDefines.IID_COMPONENT_AS;

    private ConfInstance conf;
    private boolean captureFlag = false;
    private Timer mytimer;
    /** 控制心跳的Handler **/
    private Handler mheartBeatHandler;
    private ConferenceMgr.WorkThread confThread;
    private Semaphore confThreadStartSemaphore;
    /** 用于释放会议的Timer **/
    private Timer releaseConfTimer;
    private Handler mConfHandler;
    private long mMainThreadID;
    /** 会议句柄 **/
    private int confHandle = 0;
    private ConfGLView desktopSurfaceView;
    private ConfGLView docSurfaceView;
    private ViewGroup mDesktopViewContainer;
    private ViewGroup mDocViewContainer;
    private int dscurrentDocCount = 0;
    private int dscurrentDocID = 0;
    private int dscurrentPageID = 0;

    private ConferenceMgr()
    {
    }

    /**
     * @return ConferenceMgr
     */
    public static ConferenceMgr getInstance()
    {
        synchronized(LOCKOBJECT)
        {
            if(null == instance)
            {
                instance = new ConferenceMgr();
            }
            return instance;
        }
    }

    /**
     * 开启心跳
     */
    public void initConf()
    {
        this.mytimer = new Timer();
        this.mytimer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                Message m = new Message();
                m.what = 0;
                mheartBeatHandler.sendMessage(m);
            }
        }, 200L, 100L);
        mheartBeatHandler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                heartBeat();
            }
        };
        mMainThreadID = Looper.getMainLooper().getThread().getId();
        confThreadStartSemaphore = new Semaphore(0);
        confThread = new ConferenceMgr.WorkThread();
        confThread.start();
        confThreadStartSemaphore.acquireUninterruptibly();
        mConfHandler = confThread.getHandler();
    }

    /**
     * 设置共享容器
     *
     * @param context    context
     * @param sharedView sharedView
     * @param sharedType sharedType
     */
    public void setSharedViewContainer(Context context, ViewGroup sharedView, int sharedType)
    {
        if (Constant.SHARE_DESKTOP == sharedType)
        {
            mDesktopViewContainer = sharedView;
            mDesktopViewContainer.removeAllViews();
            desktopSurfaceView = new ConfGLView(context);
            desktopSurfaceView.setConf(conf);
            desktopSurfaceView.setViewType(sharedType);
            mDesktopViewContainer.addView(desktopSurfaceView);
            desktopSurfaceView.onResume();
            desktopSurfaceView.setVisibility(View.VISIBLE);
        }
        else if (Constant.SHARE_DOC == sharedType)
        {
            mDocViewContainer = sharedView;
            mDocViewContainer.removeAllViews();
            docSurfaceView = new ConfGLView(context);
            docSurfaceView.setConf(conf);
            docSurfaceView.setViewType(sharedType);
            mDocViewContainer.addView(docSurfaceView);
            docSurfaceView.onResume();
            docSurfaceView.setVisibility(View.VISIBLE);
        }
        else
        {
            logInfo(TAG, "", "setDesktopShareContainer | sharedType = " + sharedType + " not support type");
        }
    }

    /**
     * 释放共享容器
     */
    public void releaseShareView()
    {
        releaseDesktopShareView();
        releaseDocShareView();
    }

    /**
     * 释放桌面共享容器
     */
    public void releaseDesktopShareView()
    {
        if (null != desktopSurfaceView && null != mDesktopViewContainer)
        {
            desktopSurfaceView.onPause();
            mDesktopViewContainer.removeView(desktopSurfaceView);
            mDesktopViewContainer.removeAllViews();
            mDesktopViewContainer.invalidate();
            desktopSurfaceView = null;
            LogUtil.d(TAG, "releaseDesktopShareView");
        }
    }

    /**
     * 释放文档共享容器
     */
    public void releaseDocShareView()
    {
        if (null != docSurfaceView && null != mDocViewContainer)
        {
            docSurfaceView.onPause();
            mDocViewContainer.removeView(docSurfaceView);
            mDocViewContainer.removeAllViews();
            mDocViewContainer.invalidate();
            docSurfaceView = null;
            LogUtil.d(TAG, "releaseDocShareView");
        }

        dscurrentDocCount = 0;
        dscurrentDocID = 0;
        dscurrentPageID = 0;
    }

    /**
     * 释放会议资源
     */
    private void releaseConfResource()
    {
        mMainThreadID = 0;
        if (null != this.mytimer)
        {
            mytimer.cancel();
            mytimer = null;
        }

        if (null != confThreadStartSemaphore)
        {
            confThreadStartSemaphore.release();
            confThreadStartSemaphore = null;
        }
        if (null != confThread)
        {
            confThread.getHandler().getLooper().quit();
            confThread.interrupt();
            confThread = null;
        }

        if (null != releaseConfTimer)
        {
            releaseConfTimer.cancel();
            releaseConfTimer = null;
        }

        releaseShareView();
    }

    /**
     * 释放会议
     */
    public void releaseConf()
    {
        if (null != conf)
        {
            conf.confRelease();
        }
    }

    /**
     * 创建并加入会议
     *
     * @return boolean
     */
    public boolean joinConference()
    {
        newConf();

        try
        {
            Thread.sleep(50L, 0);
        }
        catch (InterruptedException var2)
        {
            logInfo(TAG, "", "Interrupted Exception");
        }

        if (SystemConfig.getInstance().isNat())
        {
            openIpMap();
        }

        return this.joinConf();
    }

    /**
     * 开启ipmap(MS重定向所需)
     */
    private void openIpMap()
    {
        String servIp = SystemConfig.getInstance().getServerIp();
        String[] a = servIp.split(":");
        serverip[] sip = new serverip[1];

        for(int i = 0; i < 1; ++i)
        {
            sip[i] = new serverip();
            sip[i].SetInterIp(a[0]);
            sip[i].SetOuterIp(SystemConfig.getInstance().getNatIp());
        }

        if (conf != null)
        {
            conf.setipmap(sip, 1);
        }
    }

    /**
     * 初始化
     */
    public void initConfSDK()
    {
        String logFile = Environment.getExternalStorageDirectory().toString() + File.separator + "CCLOG" + "/conf";
        File dirFile = new File(logFile);
        if (!dirFile.exists() && !dirFile.isDirectory() && dirFile.mkdir())
        {
            logInfo(TAG, "", "mkdir " + dirFile.getPath());
        }

        Conference.getInstance().setLogLevel(3, 3);
        Conference.getInstance().setPath(logFile, logFile);
        Conference.getInstance().initSDK(false, 4);
    }

    /**
     * 创建会议
     */
    public void newConf()
    {
        this.initConfSDK();
        this.conf = new ConfInstance();
        this.conf.setConfUI(this);
        ConfInfo cinfo = new ConfInfo();
        cinfo.setConfId(this.conferenceInfo.getConfId());
        cinfo.setConfKey(this.conferenceInfo.getConfKey());
        cinfo.setConfOption(1);
        cinfo.setHostKey(this.conferenceInfo.getHostKey());
        cinfo.setUserId((long)this.conferenceInfo.getUserId());
        cinfo.setUserName(this.conferenceInfo.getUserName());
        cinfo.setUserType(8);
        cinfo.setSiteId(this.conferenceInfo.getSiteId());
        cinfo.setSvrIp(this.conferenceInfo.getServerIp());
        cinfo.setSiteUrl(this.conferenceInfo.getSiteUrl());
        cinfo.setUserUri("");
        conf.confNew(cinfo);
    }

    /**
     * 加入会议
     *
     * @return boolean
     */
    public boolean joinConf()
    {
        if (isMainThread())
        {
            Message msg = new Message();
            msg.what = ConfOper.CONF_OPER_JOIN;
            mConfHandler.sendMessage(msg);
            return true;
        }

        int nRet = this.conf.confJoin();
        logInfo(TAG, "", "JoinConf |  nRet = " + nRet);
        return nRet == 0;
    }

    public void unInitConf()
    {
        // 定义Handler
        final Handler tmpHandler = new Handler(Looper.getMainLooper())
        {
            public void handleMessage(Message msg)
            {
                if (captureFlag)
                {
                    int closeCapRes = ConfPrew.getInstance()
                            .videoWizCloseCapture(1);
                    LogUtil.d(TAG, "ConfPrew videoWizCloseCapture result:" + closeCapRes);
                    if (closeCapRes == 0)
                    {
                        captureFlag = false;
                    }
                }
                confHandle = 0;
                releaseConfResource();
                Conference.getInstance().exitSDK();
            }
        };

        // 定义计时器
        releaseConfTimer = new Timer();
        MyTimerTask task = new MyTimerTask();
        task.setHandler(tmpHandler);
        releaseConfTimer.schedule(task, 1500);
    }

    private static class MyTimerTask extends TimerTask
    {
        private Handler handler;

        public void setHandler(Handler handler)
        {
            this.handler = handler;
        }

        @Override
        public void run()
        {
            Message msg = new Message();
            handler.sendMessage(msg);
        }
    }

    /**
     * 加载组件
     *
     * @return boolean
     */
    public boolean loadComponent()
    {
        if (isMainThread())
        {
            Message msg = new Message();
            msg.what = ConfOper.CONF_OPER_LOAD_COMPONENT;
            mConfHandler.sendMessage(msg);
            return true;
        }
        int nRet = this.conf.confLoadComponent(this.componentVal);
        logInfo(TAG, "", "load componentVal:" + this.componentVal + "LoadComponent |  nRet = " + nRet + "check confHandle::::::" + this.confHandle + ",,,," + this.conf.getConfHandle());
        return nRet == 0;
    }

    /**
     * 心跳
     */
    public void heartBeat()
    {
        conf.confHeartBeat();
    }

    /**
     * 终止会议
     *
     * @return boolean
     */
    public boolean terminateConf()
    {
        if (isMainThread())
        {
            Message msg = new Message();
            msg.what = ConfOper.CONF_OPER_TERMINATE;
            mConfHandler.sendMessage(msg);
            return true;
        }

        int nRet = this.conf.confTerminate();
        this.logInfo(TAG, "", "TerminateConf | nRet = " + nRet);
        return nRet == 0;
    }

    /**
     * 设置视频编码端的最大宽和高。 Android对于不同的型号有不同的编码要求，
     * 如果需要请设置，不设置的话，采用默认值640*480
     *
     * @param xResolution xResolution
     * @param yResolution yResolution
     * @return boolean
     */
    public boolean setEncodeMaxResolution(int xResolution, int yResolution)
    {
        if (isMainThread())
        {
            Message msg = new Message();
            msg.what = ConfOper.VIDEO_OPER_SETENCODE_MAXRESOLUTION;
            msg.arg1 = xResolution;
            msg.arg2 = yResolution;
            mConfHandler.sendMessage(msg);
            return true;
        }
        int nRet = this.conf.videoSetEncodeMaxResolution(xResolution, yResolution);
        return nRet == 0;
    }

    /**
     * 文档共享 —— 设置当前页码
     *
     * @param nDocID  nDocID
     * @param nPageID nPageID
     * @return boolean
     */
    public boolean dsSetcurrentpage(int nDocID, int nPageID)
    {
        if (isMainThread())
        {
            Message msg = new Message();
            msg.what = ConfOper.DS_OPER_SET_CURRENTPAGE;
            msg.arg1 = nDocID;
            msg.arg2 = nPageID;
            mConfHandler.sendMessage(msg);
            return true;
        }
        int nRet = this.conf.dsSetCurrentPage((long)nDocID, (long)nPageID);
        return nRet == 0;
    }

    /**
     * 设置屏幕共享参数
     *
     * @param value 1:开启；0：关闭
     * @return boolean
     */
    public boolean asSetParam(int value)
    {
        if (this.isMainThread())
        {
            Message msg = new Message();
            msg.what = ConfOper.AS_OPER_SET_PARAM;
            msg.arg1 = value;
            mConfHandler.sendMessage(msg);
            return true;
        }
        int nRet = this.conf.asSetParam(20L, (long)value);
        logInfo(TAG, "", "as_set_param end | value = " + value + ", nRet = " + nRet);
        return nRet == 0;
    }

    /**
     * 加载DS组件的两个接口 应该都为会议线程
     *
     * @param compid compid
     * @return int
     */
    public int annotRegCustomerType(int compid)
    {
        return conf.annotRegCustomerType(compid);
    }

    /**
     * 会议回调消息通知
     *
     * @param msg       msg
     * @param extendMsg extendMsg
     */
    @Override
    public void confMsgNotify(ConfMsg msg, ConfExtendMsg extendMsg)
    {
        long nValue1 = (long)msg.getnValue1();
        long nValue2 = msg.getnValue2();
        int msgType = msg.getMsgType();
        logInfo(TAG, "", "msgType = " + msgType + " , nValue1 = " + nValue1 + " , nValue2 = " + nValue2);
        BroadMsg broadMsg = new BroadMsg();
        ConfExtendUserInfoMsg infoMsg;
        String fromId;
        RequestInfo requestInfo;
        switch (msgType)
        {
            case ConfMsg.CONF_MSG_ON_CONFERENCE_JOIN:
                broadMsg.setAction(NotifyMessage.CALL_MSG_USER_JOIN);
                RequestCode requestCode = new RequestCode();
                requestCode.setRCode(String.valueOf(nValue1));
                broadMsg.setRequestCode(requestCode);
                if(nValue1 == 0L)
                {
                    loadComponent();
                }
                CCAPP.getInstance().sendBroadcast(broadMsg);
                break;
            case ConfMsg.CONF_MSG_ON_CONFERENCE_TERMINATE:
                LogUtil.d(TAG, "=========结束会议=======");
                MobileCC.getInstance().releaseConf(false);
                break;
            case ConfMsg.CONF_MSG_ON_CONFERENCE_LEAVE:
                MobileCC.getInstance().releaseConf(true);
                break;
            case ConfMsg.CONF_MSG_ON_DISCONNECT:
                broadMsg.setAction(NotifyMessage.CALL_MSG_USER_NETWORK_ERROR);
                CCAPP.getInstance().sendBroadcast(broadMsg);
                break;
            case ConfMsg.CONF_MSG_ON_RECONNECT:
                broadMsg.setAction(NotifyMessage.CONF_RECONNECTED);
                CCAPP.getInstance().sendBroadcast(broadMsg);
                break;
            case ConfMsg.CONF_MSG_ON_COMPONENT_LOAD:
                switch ((int)nValue2)
                {
                    case 1:
                        this.annotRegCustomerType(1);
                        return;
                    case 2:
                        return;
                    case 8:
                        this.setEncodeMaxResolution(640, 480);
                        return;
                    default:
                        return;
                }
            case ConfMsg.CONF_MSG_USER_ON_ENTER_IND:
                infoMsg = (ConfExtendUserInfoMsg)extendMsg;
                if (infoMsg != null)
                {
                    fromId = infoMsg.getUserid() + "";
                    broadMsg.setAction(NotifyMessage.CONF_USER_ENTER_EVENT);
                    requestInfo = new RequestInfo();
                    requestInfo.setMsg(fromId);
                    broadMsg.setRequestInfo(requestInfo);
                    CCAPP.getInstance().sendBroadcast(broadMsg);
                }
                break;
            case ConfMsg.CONF_MSG_USER_ON_LEAVE_IND:
                infoMsg = (ConfExtendUserInfoMsg)extendMsg;
                if (null != infoMsg)
                {
                    MobileCC.getInstance().releaseConf(false);
                }
                break;
            case ConfMsg.CONF_MSG_USER_ON_MESSAGE_IND:
                break;
            case ConfMsg.COMPT_MSG_AS_ON_SCREEN_DATA:
            case ConfMsg.COMPT_MSG_AS_ON_SCREEN_SIZE:
            case ConfMsg.COMPT_MSG_AS_ON_SHARING_STATE:
            case ConfMsg.COMPT_MSG_AS_ON_SHARING_SESSION:
                confMsgNotifyAs(msg, extendMsg);
                break;
            case ConfMsg.COMPT_MSG_DS_ON_DOC_NEW:
            case ConfMsg.COMPT_MSG_DS_ON_DOC_DEL:
            case ConfMsg.COMPT_MSG_DS_ON_PAGE_NEW:
            case ConfMsg.COMPT_MSG_DS_ON_PAGE_DEL:
            case ConfMsg.COMPT_MSG_DS_ON_CURRENT_PAGE_IND:
            case ConfMsg.COMPT_MSG_DS_ON_CURRENT_PAGE:
            case ConfMsg.COMPT_MSG_DS_ON_DRAW_DATA_NOTIFY:
            case ConfMsg.COMPT_MSG_DS_PAGE_DATA_DOWNLOAD:
            case ConfMsg.COMPT_MSG_DS_ANDROID_DOC_COUNT:
                confMsgNotifyDs(msg, extendMsg);
                break;
            default:
                break;
        }
    }

    public void setConferenceInfo(ConferenceInfo conferenceInfo)
    {
        this.conferenceInfo = conferenceInfo;
    }

    /**
     * 桌面共享通知
     */
    private void confMsgNotifyAs(ConfMsg msg, ConfExtendMsg extendMsg)
    {
        int msgType = msg.getMsgType();
        int nValue1 = msg.getnValue1();
        int nValue2 = (int)msg.getnValue2();
        switch(msgType)
        {
            case ConfMsg.COMPT_MSG_AS_ON_SHARING_STATE:
                if (nValue2 == ConfDefines.AS_STATE_NULL)
                {
                    releaseDesktopShareView();

                    BroadMsg broadMsg = new BroadMsg(NotifyMessage.CALL_MSG_USER_RECEIVE_SHARED_DATA);

                    RequestCode requestCode1 = new RequestCode();
                    requestCode1.setRCode(String.valueOf(Constant.SHARE_DESKTOP));
                    broadMsg.setRequestCode(requestCode1);

                    RequestInfo requestInfo1 = new RequestInfo();
                    requestInfo1.setMsg(String.valueOf(Constant.SHARE_STOP));
                    broadMsg.setRequestInfo(requestInfo1);
                    CCAPP.getInstance().sendBroadcast(broadMsg);
                }
                updateDesktopSharedView();
                break;
            case ConfMsg.COMPT_MSG_AS_ON_SHARING_SESSION:
                if (nValue1 == 1 && (nValue2 == ConfDefines.AS_STATE_VIEW || nValue2 == ConfDefines.AS_STATE_START))
                {
                    BroadMsg broadMsg = new BroadMsg(NotifyMessage.CALL_MSG_USER_RECEIVE_SHARED_DATA);

                    RequestCode requestCode = new RequestCode();
                    requestCode.setRCode(String.valueOf(Constant.SHARE_DESKTOP));
                    broadMsg.setRequestCode(requestCode);

                    RequestInfo requestInfo = new RequestInfo();
                    requestInfo.setMsg(String.valueOf(Constant.SHARE_START));
                    broadMsg.setRequestInfo(requestInfo);
                    CCAPP.getInstance().sendBroadcast(broadMsg);
                }
                updateDesktopSharedView();
                break;
            default:
                if (msgType == ConfMsg.COMPT_MSG_AS_ON_SCREEN_SIZE || msgType == ConfMsg.COMPT_MSG_AS_ON_SCREEN_DATA)
                {
                    updateDesktopSharedView();
                }
                break;
        }
    }

    /**
     * 文档共享通知
     */
    private void confMsgNotifyDs(ConfMsg msg, ConfExtendMsg extendMsg)
    {
        int msgType = msg.getMsgType();
        int nValue1 = msg.getnValue1();
        int nValue2 = (int)msg.getnValue2();
        switch(msgType)
        {
            case ConfMsg.COMPT_MSG_DS_ON_DOC_NEW:
                // 新建一个文档共享时，当前共享文档数量为零，即文档共享开始
                if (this.dscurrentDocCount == 0)
                {
                    BroadMsg broadMsg = new BroadMsg(NotifyMessage.CALL_MSG_USER_RECEIVE_SHARED_DATA);

                    RequestCode requestCode = new RequestCode();
                    requestCode.setRCode(String.valueOf(Constant.SHARE_DOC));
                    broadMsg.setRequestCode(requestCode);

                    RequestInfo requestInfo = new RequestInfo();
                    requestInfo.setMsg(String.valueOf(Constant.SHARE_START));
                    broadMsg.setRequestInfo(requestInfo);
                    CCAPP.getInstance().sendBroadcast(broadMsg);
                }

                updateDocSharedView();
                break;
            case ConfMsg.COMPT_MSG_DS_ON_CURRENT_PAGE_IND:
                // 同步翻页预先通知
                if (nValue1 != 0)
                {
                    this.dscurrentDocID = nValue1;
                    this.dscurrentPageID = nValue2;
                    this.dsSetcurrentpage(dscurrentDocID, dscurrentPageID);
                }

                updateDocSharedView();
                break;
            case ConfMsg.COMPT_MSG_DS_PAGE_DATA_DOWNLOAD:
                // 文档页面数据已经下载通知
                if (nValue1 == this.dscurrentDocID && nValue2 == this.dscurrentPageID)
                {
                    updateDocSharedView();
                }
                break;
            case ConfMsg.COMPT_MSG_DS_ANDROID_DOC_COUNT:
                this.dscurrentDocCount = nValue1;
                // 共享文档数量为零，即文档共享停止
                if (nValue1 == 0)
                {
                    releaseDocShareView();

                    BroadMsg broadMsg = new BroadMsg(NotifyMessage.CALL_MSG_USER_RECEIVE_SHARED_DATA);
                    RequestCode requestCode = new RequestCode();
                    requestCode.setRCode(String.valueOf(Constant.SHARE_DOC));
                    broadMsg.setRequestCode(requestCode);
                    RequestInfo requestInfo = new RequestInfo();
                    requestInfo.setMsg(String.valueOf(Constant.SHARE_STOP));
                    broadMsg.setRequestInfo(requestInfo);
                    CCAPP.getInstance().sendBroadcast(broadMsg);
                }
                break;
            default:
                if (msgType == ConfMsg.COMPT_MSG_DS_ON_DOC_DEL
                        || msgType == ConfMsg.COMPT_MSG_DS_ON_PAGE_NEW
                        || msgType == ConfMsg.COMPT_MSG_DS_ON_CURRENT_PAGE
                        || msgType == ConfMsg.COMPT_MSG_DS_ON_DRAW_DATA_NOTIFY)
                {
                    updateDocSharedView();
                    if (msgType == ConfMsg.COMPT_MSG_DS_ON_DRAW_DATA_NOTIFY)
                    {
                        updateDesktopSharedView();
                    }
                }
                break;
        }
    }

    /**
     * 刷新共享屏幕页面
     */
    private void updateDesktopSharedView()
    {
        if (null != desktopSurfaceView)
        {
            desktopSurfaceView.update();
        }
    }

    /**
     * 刷新共享文档页面
     */
    private void updateDocSharedView()
    {
        if (null != docSurfaceView)
        {
            docSurfaceView.update();
        }
    }

    /**
     * 判断是否是主线程
     *
     * @return boolean
     */
    private boolean isMainThread()
    {
        return Thread.currentThread().getId() == this.mMainThreadID;
    }

    /**
     * Handle 消息处理
     *
     * @param msg msg
     */
    private void handleMsg(Message msg)
    {
        int yResolution;
        int value;
        switch (msg.what)
        {
            case ConfOper.CONF_OPER_JOIN:
                joinConf();
                break;
            case ConfOper.CONF_OPER_LEAVE:
            case ConfOper.CONF_OPER_TERMINATE:
                terminateConf();
                break;
            case ConfOper.CONF_OPER_LOCK:
                break;
            case ConfOper.CONF_OPER_UNLOCK:
                break;
            case ConfOper.CONF_OPER_KICKOUT:
                break;
            case ConfOper.CONF_OPER_SET_ROLE:
                break;
            case ConfOper.CONF_OPER_LOAD_COMPONENT:
                loadComponent();
                break;
            case ConfOper.VIDEO_OPER_SETENCODE_MAXRESOLUTION:
                value = msg.arg1;
                yResolution = msg.arg2;
                setEncodeMaxResolution(value, yResolution);
                break;
            case ConfOper.DS_OPER_SET_CURRENTPAGE:
                dsSetcurrentpage(msg.arg1, msg.arg2);
                break;
            case ConfOper.AS_OPER_SET_PARAM:
                value = msg.arg1;
                asSetParam(value);
                break;
            default:
                break;
        }
    }

    private void logInfo(String tagName, String methodName, String content)
    {
        LogUtil.d(tagName, methodName + " " + content);
    }

    /**
     * 该类主要是确保操作是在主线程中
     */
    private class WorkThread extends Thread
    {
        private Handler handler;

        private WorkThread()
        {
        }

        public void run()
        {
            Looper.prepare();
            this.handler = new Handler()
            {
                public void handleMessage(Message msg)
                {
                    ConferenceMgr.this.handleMsg(msg);
                }
            };
            ConferenceMgr.this.confThreadStartSemaphore.release();
            Looper.loop();
        }

        public Handler getHandler()
        {
            return this.handler;
        }
    }
}
