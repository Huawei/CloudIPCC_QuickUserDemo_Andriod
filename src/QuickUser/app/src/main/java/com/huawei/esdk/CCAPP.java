package com.huawei.esdk;

import android.app.Application;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.huawei.esdk.service.ics.model.BroadMsg;


/**
 * Created on 2017/11/1.
 */
public class CCAPP extends Application
{
    private static CCAPP ccApp;
    private LocalBroadcastManager localBroadcastManager;

    public CCAPP () {}

    public CCAPP (LocalBroadcastManager localBroadcastManager)
    {
        this.localBroadcastManager = localBroadcastManager;
    }

    public static CCAPP getInstance()
    {
        return ccApp;
    }

    private void setCCAPP (CCAPP ccApp)
    {
        setCCAPPValue(ccApp);
    }

    private static void setCCAPPValue(CCAPP ccApp)
    {
        CCAPP.ccApp = ccApp;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        setCCAPP(this);

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        MobileCC.getInstance().initTup();
    }

    @Override
    public void onTerminate()
    {
        super.onTerminate();
        //停止Tup服务
        MobileCC.getInstance().unInitTup();
    }

    /**
     * 发送广播
     *
     * @param broadMsg broadMsg
     */
    public void sendBroadcast(BroadMsg broadMsg)
    {
        Intent intent = new Intent(broadMsg.getAction());
        intent.putExtra(NotifyMessage.CC_MSG_CONTENT, broadMsg);
        localBroadcastManager.sendBroadcast(intent);
    }
}