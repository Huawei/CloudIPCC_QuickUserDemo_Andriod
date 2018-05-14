package com.huawei.esdk.service.call;


import tupsdk.TupCall;

/**
 * 会话管理
 */
public class CallSession
{
    private CallManager callManager = null;

    private TupCall tupCall;

    /**
     * 构造
     *
     * @param callManager callManager
     */
    public CallSession(CallManager callManager)
    {
        this.callManager = callManager;
    }

    public TupCall getTupCall()
    {
        return tupCall;
    }

    public void setTupCall(TupCall tupCall)
    {
        this.tupCall = tupCall;
    }

    /**
     * 挂断
     */
    public void hangUp()
    {
        this.getTupCall().endCall();
    }

    public CallManager getCallManager() {
        return callManager;
    }
}
