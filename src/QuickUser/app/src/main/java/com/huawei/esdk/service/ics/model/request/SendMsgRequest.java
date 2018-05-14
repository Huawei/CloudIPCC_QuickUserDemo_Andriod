package com.huawei.esdk.service.ics.model.request;

/**
 * Created on 2016/3/15.
 */
public class SendMsgRequest
{
    private final String callId;
    private final String content;

    /**
     * SendMsgRequest
     *
     * @param callId  callId
     * @param content content
     */
    public SendMsgRequest(String callId, String content)
    {
        this.callId = callId;
        this.content = content;
    }

    public String getCallId()
    {
        return callId;
    }

    public String getContent()
    {
        return content;
    }
}
