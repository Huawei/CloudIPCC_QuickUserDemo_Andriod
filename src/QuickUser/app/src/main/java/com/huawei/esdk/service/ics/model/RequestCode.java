package com.huawei.esdk.service.ics.model;

import java.io.Serializable;

/**
 * Created on 2017/1/11.
 */
public class RequestCode implements Serializable
{
    private static final long serialVersionUID = -878810063662915787L;
    //网络质量
    private int netLevel = 0;

    //返回码
    private String rCode;
    private int errorCode;

    /**
     * 构造
     */
    public RequestCode()
    {

    }

    public int getNetLevel()
    {
        return netLevel;
    }

    public void setNetLevel(int netLevel)
    {
        this.netLevel = netLevel;
    }

    public String getRCode()
    {
        return rCode;
    }

    public void setRCode(String rCode)
    {
        this.rCode = rCode;
    }

    public int getErrorCode()
    {
        return errorCode;
    }

    public void setErrorCode(int errorCode)
    {
        this.errorCode = errorCode;
    }
}
