package com.huawei.esdk.service.ics.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created on 2016/3/15.
 */
public class QueueInfoResponse
{
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("retcode")
    @Expose
    private String retcode;

    @SerializedName("result")
    @Expose
    private QueueInfoResultResponse result;

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

    public QueueInfoResultResponse getResult()
    {
        return result;
    }

    public void setResult(QueueInfoResultResponse result)
    {
        this.result = result;
    }

    @Override
    public String toString()
    {
        return "QueueInfoResponse{"
                + "message='" + message + '\''
                + ", retcode='" + retcode + '\''
                + ", result=" + result + '}';
    }
}
