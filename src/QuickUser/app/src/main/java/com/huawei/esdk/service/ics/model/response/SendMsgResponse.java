package com.huawei.esdk.service.ics.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created on 2016/3/15.
 */
public class SendMsgResponse
{

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("retcode")
    @Expose
    private String retcode;

    @SerializedName("event")
    @Expose
    private SendMsgResContentResponse event;

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

    public SendMsgResContentResponse getEvent()
    {
        return event;
    }

    public void setEvent(SendMsgResContentResponse event)
    {
        this.event = event;
    }

    @Override
    public String toString()
    {
        return "SendMsgResponse{" + "message='" + message + '\''
                + ", retcode='" + retcode + '\'' + ", event=" + event + '}';
    }
}

