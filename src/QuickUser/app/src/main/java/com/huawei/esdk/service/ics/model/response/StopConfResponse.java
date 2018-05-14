package com.huawei.esdk.service.ics.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created on 2016/5/24.
 */
public class StopConfResponse
{

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("retcode")
    @Expose
    private String retcode;

    @SerializedName("event")
    @Expose
    private String event;

    public String getEvent()
    {
        return event;
    }

    public void setEvent(String event)
    {
        this.event = event;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getRetcode()
    {
        return retcode;
    }

    public void setRetcode(String retcode)
    {
        this.retcode = retcode;
    }

}
