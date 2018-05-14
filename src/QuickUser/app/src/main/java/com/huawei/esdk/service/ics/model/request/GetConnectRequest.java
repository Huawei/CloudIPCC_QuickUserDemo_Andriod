package com.huawei.esdk.service.ics.model.request;


/**
 * Created on 2016/3/14.
 */
public class GetConnectRequest
{
    private final int mediaType;
    private final String caller;
    private final String accessCode;
    private final String callData;
    private final long uvid;
    private final String verifyCode;
    private final int mediaAblitiy; //do not fix this typo, or will make error

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
    public GetConnectRequest(int mediaType, String caller, String accessCode, String callData, long uvid, String verifyCode, int mediaAblitiy)
    {
        this.mediaType = mediaType;
        this.caller = caller;
        this.accessCode = accessCode;
        this.callData = callData;
        this.uvid = uvid;
        this.verifyCode = verifyCode;
        this.mediaAblitiy = mediaAblitiy;
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

    public int getMediaAbility()
    {
        return mediaAblitiy;
    }
}
