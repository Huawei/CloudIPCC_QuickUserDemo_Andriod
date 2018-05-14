package com.huawei.esdk.service.ics.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created on 2016/3/15.
 */
public class GetEventContentResponse
{


    @SerializedName("caller")
    @Expose
    private String caller;

    @SerializedName("called")
    @Expose
    private String called;

    @SerializedName("uvid")
    @Expose
    private long uvid;

    @SerializedName("agentId")
    @Expose
    private String agentId;

    @SerializedName("clickToDial")
    @Expose
    private String clickToDial;

    @SerializedName("callId")
    @Expose
    private String callId;

    @SerializedName("mediaType")
    @Expose
    private int mediaType;

    @SerializedName("chatContent")
    @Expose
    private String chatContent;


    @SerializedName("confId")
    @Expose
    private String confId;
    @SerializedName("confInfo")
    @Expose
    private String confInfo;
    @SerializedName("vcConfInfo")
    @Expose
    private ConfInfoResponse vcConfInfo;

    public String getConfId()
    {
        return confId;
    }

    public void setConfId(String confId)
    {
        this.confId = confId;
    }

    public String getConfInfo()
    {
        return confInfo;
    }

    public void setConfInfo(String confInfo)
    {
        this.confInfo = confInfo;
    }

    public String getCaller()
    {
        return caller;
    }

    public void setCaller(String caller)
    {
        this.caller = caller;
    }

    public String getCalled()
    {
        return called;
    }

    public void setCalled(String called)
    {
        this.called = called;
    }

    public long getUvid()
    {
        return uvid;
    }

    public void setUvid(int uvid)
    {
        this.uvid = uvid;
    }

    public String getAgentId()
    {
        return agentId;
    }

    public void setAgentId(String agentId)
    {
        this.agentId = agentId;
    }

    public String getClickToDial()
    {
        return clickToDial;
    }

    public void setClickToDial(String clickToDial)
    {
        this.clickToDial = clickToDial;
    }

    public String getCallId()
    {
        return callId;
    }

    public void setCallId(String callId)
    {
        this.callId = callId;
    }

    public int getMediaType()
    {
        return mediaType;
    }

    public void setMediaType(int mediaType)
    {
        this.mediaType = mediaType;
    }

    public String getChatContent()
    {
        return chatContent;
    }

    public void setChatContent(String chatContent)
    {
        this.chatContent = chatContent;
    }

    public ConfInfoResponse getVcConfInfo()
    {
        return vcConfInfo;
    }

    public void setVcConfInfo(ConfInfoResponse vcConfInfo)
    {
        this.vcConfInfo = vcConfInfo;
    }
}
