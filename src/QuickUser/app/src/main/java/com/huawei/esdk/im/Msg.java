package com.huawei.esdk.im;

/**
 * Created on 2016/1/5.
 */
public class Msg
{

    /*接收的消息标记*/
    public static final int TYPE_RECEIVED = 0;

    /*发出的消息标记*/
    public static final int TYPE_SENT = 1;

    /*消息内容*/
    private String content;

    /*消息类型：接收或者发出的*/
    private int type;

    public Msg(String content, int type)
    {
        this.content = content;
        this.type = type;
    }

    public String getContent()
    {
        return content;
    }

    public int getType()
    {
        return type;
    }

}
