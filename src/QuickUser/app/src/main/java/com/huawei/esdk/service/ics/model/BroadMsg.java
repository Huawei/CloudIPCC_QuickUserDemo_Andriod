package com.huawei.esdk.service.ics.model;

import java.io.Serializable;


/**
 * Created on 2015/12/29.
 */
public class BroadMsg implements Serializable
{
    private static final long serialVersionUID = 7038243005014607042L;
    //排队相关消息
    private QueueInfo queueInfo;

    //返回码信息
    private RequestCode requestCode;

    //返回信息
    private RequestInfo requestInfo;

    //标记
    private String action;

    //事件类型
    private String type;

    /**
     * 构造
     */
    public BroadMsg()
    {
    }

    /**
     * 构造
     *
     * @param action action
     */
    public BroadMsg(String action)
    {
        this.action = action;
    }

    public String getAction()
    {
        return action;
    }


    public void setAction(String action)
    {
        this.action = action;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public QueueInfo getQueueInfo()
    {
        return queueInfo;
    }

    public void setQueueInfo(QueueInfo queueInfo)
    {
        this.queueInfo = queueInfo;
    }

    public RequestCode getRequestCode()
    {
        return requestCode;
    }

    public void setRequestCode(RequestCode requestCode)
    {
        this.requestCode = requestCode;
    }

    public RequestInfo getRequestInfo()
    {
        return requestInfo;
    }

    public void setRequestInfo(RequestInfo requestInfo)
    {
        this.requestInfo = requestInfo;
    }
}
