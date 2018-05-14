package com.huawei.esdk.service.ics.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created on 2016/3/15.
 */
public class SendMsgChatContentResponse
{


    @SerializedName("chatContent")
    @Expose
    private String chatContent;

    @SerializedName("callId")
    @Expose
    private String callId;

    public String getChatContent()
    {
        return chatContent;
    }

    public void setChatContent(String chatContent)
    {
        this.chatContent = chatContent;
    }

    public String getCallId()
    {
        return callId;
    }

    public void setCallId(String callId)
    {
        this.callId = callId;
    }

    @Override
    public String toString()
    {
        return "SendMsgChatContentResponse{" + "chatContent='" + chatContent + '\''
                + ", callId='" + callId + '\'' + '}';
    }
}
