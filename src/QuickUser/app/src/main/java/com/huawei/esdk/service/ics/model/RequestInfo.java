package com.huawei.esdk.service.ics.model;

import com.huawei.esdk.service.video.StreamInfo;

import java.io.Serializable;

/**
 * Created on 2017/1/11.
 */
public class RequestInfo implements Serializable
{
    private static final long serialVersionUID = -1115208195996730353L;
    //消息
    private String msg = "";
    //视频流信息
    private StreamInfo streamInfo = null;

    /**
     * 构造
     */
    public RequestInfo()
    {

    }

    public StreamInfo getStreamInfo()
    {
        return streamInfo;
    }

    public void setStreamInfo(StreamInfo streamInfo)
    {
        this.streamInfo = streamInfo;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }
}
