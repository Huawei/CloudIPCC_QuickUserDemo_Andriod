package com.huawei.esdk.service.ics.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created on 2016/4/8.
 */
public class ConfInfoResponse
{

    @SerializedName("callId")
    @Expose
    private String callId;

    @SerializedName("serverIp")
    @Expose
    private String serverIp;

    @SerializedName("port")
    @Expose
    private String port;

    @SerializedName("isRecordEnabled")
    @Expose
    private String isRecordEnabled;

    @SerializedName("protocolType")
    @Expose
    private String protocolType;

    @SerializedName("accessNumber")
    @Expose
    private String accessNumber;

    @SerializedName("clustered")
    @Expose
    private String clustered;

    public String getCallId()
    {
        return callId;
    }

    public void setCallId(String callId)
    {
        this.callId = callId;
    }

    public String getServerIp()
    {
        return serverIp;
    }

    public void setServerIp(String serverIp)
    {
        this.serverIp = serverIp;
    }

    public String getPort()
    {
        return port;
    }

    public void setPort(String port)
    {
        this.port = port;
    }

    public String getIsRecordEnabled()
    {
        return isRecordEnabled;
    }

    public void setIsRecordEnabled(String isRecordEnabled)
    {
        this.isRecordEnabled = isRecordEnabled;
    }

    public String getProtocolType()
    {
        return protocolType;
    }

    public void setProtocolType(String protocolType)
    {
        this.protocolType = protocolType;
    }

    public String getAccessNumber()
    {
        return accessNumber;
    }

    public void setAccessNumber(String accessNumber)
    {
        this.accessNumber = accessNumber;
    }

    public String getClustered()
    {
        return clustered;
    }

    public void setClustered(String clustered)
    {
        this.clustered = clustered;
    }
}
