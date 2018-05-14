package com.huawei.esdk.service.ics.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created on 2016/3/15.
 */
public class GetEventDataResponse
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
    private GetEventContentResponse content;

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

    public GetEventContentResponse getContent()
    {
        return content;
    }

    public void setContent(GetEventContentResponse content)
    {
        this.content = content;
    }

}
