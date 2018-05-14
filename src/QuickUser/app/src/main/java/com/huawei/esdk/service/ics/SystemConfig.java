package com.huawei.esdk.service.ics;

/**
 * 设置类
 */
public final class SystemConfig
{
    private static final Object LOCKOBJECT = new Object();
    private static SystemConfig ins;
    public static final int MEDIA_AUDIA = 0;
    public static final int MEDIA_VIDEO = 1;

    private String guid = "";
    private String cookie = "";
    private String callId = "";
    private String audioCallId = "";
    private String textCallId = "";
    private String calledNum = "";
    private long uvid = -1;
    private String callType = "";
    /**
     * 摄像头索引
     **/
    private int cameraIndex = 1;
    /**
     * 记录是否是排队状态
     **/
    private boolean isQueuing = false;
    /**
     * 媒体类型
     * 1 : MEDIA_TYPE_TEXT
     * 2 : MEDIA_TYPE_AUDIO
     **/
    private int meidaType = 0;

    /**
     * 服务器地址
     **/
    private String serverIp = "127.0.0.1";

    /**
     * 端口号
     **/
    private int serverPort = 0;

    /**
     * SIP服务器地址
     **/
    private String sipIp = "";

    /**
     * SIP端口号
     **/
    private String sipPort = "";
    private String transSecurity = "https://";
    private String vndID = "";
    private String userName = "";

    private boolean isTLSEncoded = true;
    private boolean isSRTPEncoded = true;

    private boolean isAudioConnected = false;
    private boolean isTextConnected = false;
    private boolean isVideoConnected = false;

    private boolean isLogin = false;

    private String logPath = "";

    private String userId = "";

    private String verifyCode = "";
    private String textAccessCode = "";
    private String audioAccessCode = "";
    private String anonymousCard = "";
    private String host = "";

    private String natIp;
    private boolean isNat;
    private String confId = "";
    private boolean isMeeting;

    /**
     * 当前媒体能力
     *
     * 0为语音
     * 1为视频
     **/
    private int currentMediaAbility = 0;


    private SystemConfig()
    {
    }

    /**
     * 构造
     *
     * @return SystemConfig
     */
    public static SystemConfig getInstance()
    {
        synchronized (LOCKOBJECT)
        {
            if (null == ins)
            {
                ins = new SystemConfig();
            }
            return ins;
        }
    }

    public String getLogPath()
    {
        return logPath;
    }

    public void setLogPath(String logPath)
    {
        this.logPath = logPath;
    }

    public boolean isAudioConnected()
    {
        return isAudioConnected;
    }

    public void setAudioConnected(boolean audioConnected) {
        isAudioConnected = audioConnected;
    }

    public boolean isTextConnected() {
        return isTextConnected;
    }

    public void setTextConnected(boolean textConnected) {
        isTextConnected = textConnected;
    }

    public boolean isVideoConnected() {
        return isVideoConnected;
    }

    public void setVideoConnected(boolean videoConnected) {
        isVideoConnected = videoConnected;
    }

    public boolean isSRTPEncoded()
    {
        return isSRTPEncoded;
    }

    public void setSRTPEncoded(boolean isSRTPEncoded)
    {
        this.isSRTPEncoded = isSRTPEncoded;
    }

    public boolean isTLSEncoded()
    {
        return isTLSEncoded;
    }

    public void setTLSEncoded(boolean isEncoded)
    {
        this.isTLSEncoded = isEncoded;
    }

    /**
     * 初始化
     *
     * @param sipIp   sipIp
     * @param sipPort sipPort
     */
    public void initSIPServerAddr(String sipIp, String sipPort)
    {
        setSIPIp(sipIp);
        setSIPPort(sipPort);
    }

    public String getServerIp()
    {
        return serverIp;
    }

    public void setServerIp(String serverIp)
    {
        this.serverIp = serverIp;
    }

    public int getServerPort()
    {
        return serverPort;
    }

    public void setServerPort(int serverPort)
    {
        this.serverPort = serverPort;
    }

    public String getSIPIp()
    {
        return sipIp;
    }

    private void setSIPIp(String sipIp)
    {
        this.sipIp = sipIp;
    }

    public String getSIPPort()
    {
        return sipPort;
    }

    private void setSIPPort(String sipPort)
    {
        this.sipPort = sipPort;
    }


    public String getTransSecurity()
    {
        return transSecurity;
    }

    public void setTransSecurity(String transSecurity)
    {
        this.transSecurity = transSecurity;
    }

    public String getVndID()
    {
        return vndID;
    }

    public void setVndID(String vndID)
    {
        this.vndID = vndID;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    /**
     * 获取用户id
     *
     * @return String
     */
    public String getUserId()
    {
        return userId;
    }

    /**
     * 设置用户id
     *
     * @param userID userID
     */
    public void setUserId(String userID)
    {
        this.userId = userID;
    }

    public String getVerifyCode()
    {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode)
    {
        this.verifyCode = verifyCode;
    }

    public String getAudioAccessCode() {
        return audioAccessCode;
    }

    public void setAudioAccessCode(String audioAccessCode) {
        this.audioAccessCode = audioAccessCode;
    }

    public String getTextAccessCode() {
        return textAccessCode;
    }

    public void setTextAccessCode(String textAccessCode) {
        this.textAccessCode = textAccessCode;
    }

    public String getAnonymousCard()
    {
        return anonymousCard;
    }

    public void setAnonymousCard(String anonymousCard)
    {
        this.anonymousCard = anonymousCard;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public String getHost()
    {
        return host;
    }

    public int getCurrentMediaAbility()
    {
        return currentMediaAbility;
    }

    public void setCurrentMediaAbility(int currentMediaAbility)
    {
        this.currentMediaAbility = currentMediaAbility;
    }

    public String getGuid()
    {
        return guid;
    }

    public void setGuid(String guid)
    {
        this.guid = guid;
    }

    public String getCookie()
    {
        return cookie;
    }

    public void setCookie(String cookie)
    {
        this.cookie = cookie;
    }

    public String getCallId()
    {
        return callId;
    }

    public void setCallId(String callId)
    {
        this.callId = callId;
    }

    public String getAudioCallId()
    {
        return audioCallId;
    }

    public void setAudioCallId(String audioCallId)
    {
        this.audioCallId = audioCallId;
    }

    public String getTextCallId()
    {
        return textCallId;
    }

    public void setTextCallId(String textCallId)
    {
        this.textCallId = textCallId;
    }

    public int getMeidaType()
    {
        return meidaType;
    }

    public void setMeidaType(int meidaType)
    {
        this.meidaType = meidaType;
    }

    public String getCalledNum()
    {
        return calledNum;
    }

    public void setCalledNum(String calledNum)
    {
        this.calledNum = calledNum;
    }

    public long getUvid()
    {
        return uvid;
    }

    public void setUvid(long uvid)
    {
        this.uvid = uvid;
    }

    public String getCallType()
    {
        return callType;
    }

    public void setCallType(String callType)
    {
        this.callType = callType;
    }

    public boolean isQueuing()
    {
        return isQueuing;
    }

    public void setIsQueuing(boolean isQueuing)
    {
        this.isQueuing = isQueuing;
    }

    public int getCameraIndex()
    {
        return cameraIndex;
    }

    public void setCameraIndex(int cameraIndex)
    {
        this.cameraIndex = cameraIndex;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }


    public boolean isNat() {
        return isNat;
    }

    public void setIsNat(boolean nat) {
        isNat = nat;
    }

    public String getNatIp() {
        return natIp;
    }

    public void setNatIp(String natIp) {
        this.natIp = natIp;
    }

    public String getConfId() {
        return confId;
    }

    public void setConfId(String confId) {
        this.confId = confId;
    }

    public boolean isMeeting() {
        return isMeeting;
    }

    public void setIsMeeting(boolean meeting) {
        isMeeting = meeting;
    }

    private static void releaseIns()
    {
        synchronized (LOCKOBJECT)
        {
            ins = null;
        }
    }

    /**
     * 清除信息
     */
    public void clear()
    {
        releaseIns();
    }

    public void clearStatus()
    {
        isLogin = false;
        isQueuing = false;
        isTextConnected = false;
        isVideoConnected = false;
        isAudioConnected = false;
        isMeeting = false;
    }

}
