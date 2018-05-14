package com.huawei.esdk.service.ics.model.request;


/**
 * Created on 2016/3/14.
 */
public class GetTextConnectRequest
{
    private final int mediaType;
    private final String caller;
    private final String accessCode;
    private final String callData;
    private final long uvid;
    private final String verifyCode;

    /**
     * 构造
     *
     * @param mediaType  mediaType
     * @param caller     caller
     * @param accessCode accessCode
     * @param callData   callData
     * @param uvid       uvid
     * @param verifyCode  verifyCode
     */
    public GetTextConnectRequest(int mediaType, String caller, String accessCode, String callData, long uvid, String verifyCode)
    {
        this.mediaType = mediaType;
        this.caller = caller;
        this.accessCode = accessCode;
        this.callData = callData;
        this.uvid = uvid;
        this.verifyCode = verifyCode;
    }

    public int getMediaType()
    {
        return mediaType;
    }

    public String getCaller()
    {
        return caller;
    }

    public String getAccessCode()
    {
        return accessCode;
    }

    public String getCallData()
    {
        return callData;
    }

    public long getUvid()
    {
        return uvid;
    }

    public String getVerifyCode()
    {
        return verifyCode;
    }
}
