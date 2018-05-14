package com.huawei.esdk.service.ics.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created on 2016/3/15.
 */
public class SendMsgResContentResponse
{

    @SerializedName("eventType")
    @Expose
    private String eventType;

    @SerializedName("userName")
    @Expose
    private String userName;

    @SerializedName("vdnId")
    @Expose
    private int vdnId;

    @SerializedName("content")
    @Expose
    private SendMsgChatContentResponse content;

    public String getEventType()
    {
        return eventType;
    }

    public void setEventType(String eventType)
    {
        this.eventType = eventType;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public int getVdnId()
    {
        return vdnId;
    }

    public void setVdnId(int vdnId)
    {
        this.vdnId = vdnId;
    }

    public SendMsgChatContentResponse getContent()
    {
        return content;
    }

    public void setContent(SendMsgChatContentResponse content)
    {
        this.content = content;
    }

    @Override
    public String toString()
    {
        return "SendMsgResContentResponse{" + "eventType='" + eventType + '\''
                + ", userName='" + userName + '\'' + ", vdnId=" + vdnId + ", content=" + content + '}';
    }
}
