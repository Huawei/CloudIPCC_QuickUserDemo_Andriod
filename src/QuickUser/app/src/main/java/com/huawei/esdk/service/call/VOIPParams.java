package com.huawei.esdk.service.call;


/**
 * VOIP注册相关的配置参数
 */
public class VOIPParams
{
    private static final int DEFAULT_REG_EXPIRES = 300; // 默认的刷新注册超时时间为  300 s
    private static final int DEFAULT_SESSION_EXPIRES = 1800; // 默认的刷新订阅超时时间为  1800 s
    /**
     * 当前用户的VOIP号码
     */
    private String voipNumber;

    /**
     * Voip登录密码
     */
    private String voipPass;
    /**
     * 服务器地址
     */
    private String serverIp;
    /**
     * 服务器端口
     */
    private String serverPort;

    /**
     * 终端号码
     */
    private String bindNo = "";
    /**
     * audio 媒体 DSCP
     */
    private int audioDSCP;
    /**
     * Video 媒体 DSCP
     */
    private int videoDSCP;
    /**
     * 注册刷新可配时间
     */
    private int regExpires = DEFAULT_REG_EXPIRES;
    /**
     * 订阅刷新可配的时间
     */
    private int sessionExpires = DEFAULT_SESSION_EXPIRES;
    /**
     * opus采样率
     */
    private int opusSamplingFreq;
    /**
     * 构造
     */
    public VOIPParams()
    {
    }

    /**
     * @return the serverIp
     */
    public String getServerIp()
    {
        return serverIp;
    }

    /**
     * @return the serverPort
     */
    public String getServerPort()
    {
        return serverPort;
    }

    public int getAudioDSCP()
    {
        return audioDSCP;
    }

    public int getVideoDSCP()
    {
        return videoDSCP;
    }

    /**
     * @param phoneNo       phoneNo
     * @param pass          pass
     * @param sipServerIp   sipServerIp
     * @param sipServerPort sipServerPort
     */
    public void resetData(String phoneNo, String pass, String sipServerIp, String sipServerPort)
    {
        //初始化VOIP数据
        this.voipNumber = phoneNo;
        this.bindNo = phoneNo;
        this.voipPass = pass;
        this.serverIp = sipServerIp;
        this.serverPort = sipServerPort;
    }

    public int getRegExpires()
    {
        return regExpires;
    }

    public int getSessionExpires()
    {
        return sessionExpires;
    }

    public int getOpusSamplingFreq()
    {
        return opusSamplingFreq;
    }

    public String getVoipNumber() {
        return voipNumber;
    }

    public String getVoipPass() {
        return voipPass;
    }

    public String getBindNo() {
        return bindNo;
    }

    public void setAudioDSCP(int audioDSCP) {
        this.audioDSCP = audioDSCP;
    }

    public void setVideoDSCP(int videoDSCP) {
        this.videoDSCP = videoDSCP;
    }

    public void setOpusSamplingFreq(int opusSamplingFreq) {
        this.opusSamplingFreq = opusSamplingFreq;
    }
}
