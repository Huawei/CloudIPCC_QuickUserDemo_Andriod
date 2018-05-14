package com.huawei.esdk.service.ics.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created on 2016/3/15.
 */
public class QueueInfoResultResponse
{
    @SerializedName("skillId")
    @Expose
    private int skillId;

    @SerializedName("onlineAgentNum")
    @Expose
    private int onlineAgentNum;

    @SerializedName("position")
    @Expose
    private int position;

    @SerializedName("totalWaitTime")
    @Expose
    private int totalWaitTime;

    @SerializedName("currentDeviceWaitTime")
    @Expose
    private int currentDeviceWaitTime;

    @SerializedName("configMaxcWaitTime")
    @Expose
    private int configMaxcWaitTime;

    @SerializedName("longestWaitTime")
    @Expose
    private int longestWaitTime;

    public int getSkillId()
    {
        return skillId;
    }

    public void setSkillId(int skillId)
    {
        this.skillId = skillId;
    }

    public int getOnlineAgentNum()
    {
        return onlineAgentNum;
    }

    public void setOnlineAgentNum(int onlineAgentNum)
    {
        this.onlineAgentNum = onlineAgentNum;
    }

    public int getPosition()
    {
        return position;
    }

    public void setPosition(int position)
    {
        this.position = position;
    }

    public int getTotalWaitTime()
    {
        return totalWaitTime;
    }

    public void setTotalWaitTime(int totalWaitTime)
    {
        this.totalWaitTime = totalWaitTime;
    }

    public int getCurrentDeviceWaitTime()
    {
        return currentDeviceWaitTime;
    }

    public void setCurrentDeviceWaitTime(int currentDeviceWaitTime)
    {
        this.currentDeviceWaitTime = currentDeviceWaitTime;
    }

    public int getConfigMaxcWaitTime()
    {
        return configMaxcWaitTime;
    }

    public void setConfigMaxcWaitTime(int configMaxcWaitTime)
    {
        this.configMaxcWaitTime = configMaxcWaitTime;
    }

    public int getLongestWaitTime()
    {
        return longestWaitTime;
    }

    public void setLongestWaitTime(int longestWaitTime)
    {
        this.longestWaitTime = longestWaitTime;
    }

    @Override
    public String toString()
    {
        return "QueueInfoResultResponse{" + "skillId=" + skillId + ", onlineAgentNum=" + onlineAgentNum
                + ", position=" + position + ", totalWaitTime=" + totalWaitTime + ", currentDeviceWaitTime="
                + currentDeviceWaitTime + ", configMaxcWaitTime=" + configMaxcWaitTime + ", longestWaitTime="
                + longestWaitTime + '}';
    }
}
