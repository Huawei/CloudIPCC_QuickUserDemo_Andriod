package com.huawei.esdk.conference;

/**
 * Created on 2016/3/9.
 */
public final class ConferenceInfo
{
    private String siteId;
    private String serverIp;
    private String confKey;
    private String hostKey;
    private String userName;
    private String siteUrl;
    private int confId;
    private int userId;

    public String getSiteId()
    {
        return siteId;
    }

    public void setSiteId(String siteId)
    {
        this.siteId = siteId;
    }

    public String getServerIp()
    {
        return serverIp;
    }

    public void setServerIp(String serverIp)
    {
        this.serverIp = serverIp;
    }

    public String getConfKey()
    {
        return confKey;
    }

    public void setConfKey(String confKey)
    {
        this.confKey = confKey;
    }

    public String getHostKey()
    {
        return hostKey;
    }

    public void setHostKey(String hostKey)
    {
        this.hostKey = hostKey;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getSiteUrl()
    {
        return siteUrl;
    }

    public void setSiteUrl(String siteUrl)
    {
        this.siteUrl = siteUrl;
    }

    public int getConfId()
    {
        return confId;
    }

    public void setConfId(int confId)
    {
        this.confId = confId;
    }

    public int getUserId()
    {
        return userId;
    }

    public void setUserId(int userId)
    {
        this.userId = userId;
    }

}
