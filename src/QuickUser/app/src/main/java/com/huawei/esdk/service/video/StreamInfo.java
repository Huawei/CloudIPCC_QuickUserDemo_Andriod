package com.huawei.esdk.service.video;

import java.io.Serializable;

/**
 * Created on 2016/7/13.
 */
public class StreamInfo implements Serializable
{
    private static final long serialVersionUID = -4112204559019515497L;
    private String encoderSize;
    private String decoderSize;
    private int sendFrameRate;
    private int recvFrameRate;
    private int videoSendBitRate;
    private int videoRecvBitRate;
    private float videoSendLossFraction;
    private float videoRecvLossFraction;
    private float videoSendJitter;
    private float videoRecvJitter;
    private float videoSendDelay;
    private float videoRecvDelay;

    public String getEncoderSize()
    {
        return encoderSize;
    }

    public void setEncoderSize(String encoderSize)
    {
        this.encoderSize = encoderSize;
    }

    public String getDecoderSize()
    {
        return decoderSize;
    }

    public void setDecoderSize(String decoderSize)
    {
        this.decoderSize = decoderSize;
    }

    public int getSendFrameRate()
    {
        return sendFrameRate;
    }

    public void setSendFrameRate(int sendFrameRate)
    {
        this.sendFrameRate = sendFrameRate;
    }

    public int getRecvFrameRate()
    {
        return recvFrameRate;
    }

    public void setRecvFrameRate(int recvFrameRate)
    {
        this.recvFrameRate = recvFrameRate;
    }

    public int getVideoSendBitRate()
    {
        return videoSendBitRate;
    }

    public void setVideoSendBitRate(int videoSendBitRate)
    {
        this.videoSendBitRate = videoSendBitRate;
    }

    public int getVideoRecvBitRate()
    {
        return videoRecvBitRate;
    }

    public void setVideoRecvBitRate(int videoRecvBitRate)
    {
        this.videoRecvBitRate = videoRecvBitRate;
    }

    public float getVideoSendLossFraction()
    {
        return videoSendLossFraction;
    }

    public void setVideoSendLossFraction(float videoSendLossFraction)
    {
        this.videoSendLossFraction = videoSendLossFraction;
    }

    public float getVideoRecvLossFraction()
    {
        return videoRecvLossFraction;
    }

    public void setVideoRecvLossFraction(float videoRecvLossFraction)
    {
        this.videoRecvLossFraction = videoRecvLossFraction;
    }

    public float getVideoSendJitter()
    {
        return videoSendJitter;
    }

    public void setVideoSendJitter(float videoSendJitter)
    {
        this.videoSendJitter = videoSendJitter;
    }

    public float getVideoRecvJitter()
    {
        return videoRecvJitter;
    }

    public void setVideoRecvJitter(float videoRecvJitter)
    {
        this.videoRecvJitter = videoRecvJitter;
    }

    public float getVideoSendDelay()
    {
        return videoSendDelay;
    }

    public void setVideoSendDelay(float videoSendDelay)
    {
        this.videoSendDelay = videoSendDelay;
    }

    public float getVideoRecvDelay()
    {
        return videoRecvDelay;
    }

    public void setVideoRecvDelay(float videoRecvDelay)
    {
        this.videoRecvDelay = videoRecvDelay;
    }
}
