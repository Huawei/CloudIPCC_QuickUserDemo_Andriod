package com.huawei.esdk;

import java.io.Serializable;

/**
 * Created on 2015/12/29.
 */
public final class NotifyMessage implements Serializable
{
    private static final long serialVersionUID = 769506604661731005L;
    /**
     * 登录通知
     **/
    public static final String AUTH_MSG_ON_LOGIN = "AUTH_MSG_ON_LOGIN";

    /**
     * 注销通知
     **/
    public static final String AUTH_MSG_ON_LOGOUT = "AUTH_MSG_ON_LOGOUT";

    /**
     * 强制注销
     **/
    public static final String FORCE_LOGOUT = "FORCE_LOGOUT";

    /**
     * 与坐席连接成功
     **/
    public static final String CALL_MSG_ON_CONNECTED = "CALL_MSG_ON_CONNECTED";

    /**
     * 与坐席断开连接
     **/
    public static final String CALL_MSG_ON_DISCONNECTED = "CALL_MSG_ON_DISCONNECTED";

    /**
     * 排队信息
     **/
    public static final String CALL_MSG_ON_QUEUE_INFO = "CALL_MSG_ON_QUEUE_INFO";

    /**
     * 排队超时
     **/
    public static final String CALL_MSG_ON_QUEUE_TIMEOUT = "CALL_MSG_ON_QUEUE_TIMEOUT";

    /**
     * 正在排队
     **/
    public static final String CALL_MSG_ON_QUEUING = "CALL_MSG_ON_QUEUING";

    /**
     * 取消排队
     **/
    public static final String CALL_MSG_ON_CANCEL_QUEUE = "CALL_MSG_ON_CANCEL_QUEUE";

    /**
     * 发送消息
     **/
    public static final String CHAT_MSG_ON_SEND = "CHAT_MSG_ON_SEND";


    /**
     * 接收消息
     **/
    public static final String CHAT_MSG_ON_RECEIVE = "CHAT_MSG_ON_RECEIVE";


    /**
     * 显示本地视频
     **/
    public static final String CALL_MSG_REFRESH_LOCALVIEW = "CALL_MSG_REFRESH_LOCALVIEW";

    /**
     * 显示对端视频
     **/
    public static final String CALL_MSG_REFRESH_REMOTEVIEW = "CALL_MSG_REFRESH_REMOTEVIEW";
    /**
     * 释放呼叫
     **/
    public static final String CALL_MSG_ON_DROPCALL = "CALL_MSG_ON_DROPCALL";

    /**
     * 呼叫失败
     */
    public static final String CALL_MSG_ON_FAIL = "CALL_MSG_ON_FAIL";

    /**
     * 呼叫失败
     */
    public static final String CALL_MSG_ON_CALL_END = "CALL_MSG_ON_CALL_END";

    /**
     * 获取验证码
     */
    public static final String CALL_MSG_ON_VERIFYCODE = "CALL_MSG_ON_VERIFYCODE";
    /**
     * 语音接通
     **/
    public static final String CALL_MSG_ON_SUCCESS = "CALL_MSG_ON_SUCCESS";

    /**
     * 获取文字能力
     */

    public static final String CALL_MSG_ON_CONNECT = "CALL_MSG_ON_CONNECT";


    /**
     * 加入会议通知
     **/
    public static final String CALL_MSG_USER_JOIN = "CALL_MSG_USER_JOIN";

    /**
     * 会议终止通知
     **/
    public static final String CALL_MSG_USER_END = "CALL_MSG_USER_END";
    /**
     * 会议创建通知
     **/
    public static final String CALL_MSG_USER_START = "CALL_MSG_USER_START";

    /**
     * 获取到视屏流信息
     */
    public static final String CALL_MSG_GET_VIDEO_INFO = "CALL_MSG_GET_VIDEO_INFO";

    /**
     * 会议断线通知
     **/
    public static final String CALL_MSG_USER_NETWORK_ERROR = "CALL_MSG_USER_NETWORK_ERROR";

    /**
     * 接收到远端共享数据
     */
    public static final String CALL_MSG_USER_RECEIVE_SHARED_DATA = "CALL_MSG_USER_RECEIVE_SHARED_DATA";

    /**
     * w网络状态
     */
    public static final String CALL_MSG_ON_NET_QUALITY_LEVEL = "CALL_MSG_ON_NET_QUALITY_LEVEL";

    /**
     * 退出会议
     */
    public static final String CALL_MSG_ON_STOP_MEETING = "CALL_MSG_ON_STOP_MEETING";

    /**
     * 申请会议时网络异常
     */
    public static final String CALL_MSG_ON_APPLY_MEETING = "CALL_MSG_ON_APPLY_MEETING";

    /**
     * 轮询时网络异常
     */
    public static final String CALL_MSG_ON_POLL = "CALL_MSG_ON_POLL";

    /**
     * 广播相关
     */
    public static final String CC_MSG_CONTENT = "CC_MSG_CONTENT";

    /**
     * 与坐席建立连接
     */
    public static final String WECC_WEBM_CALL_CONNECTED = "WECC_WEBM_CALL_CONNECTED";

    /**
     * TLS
     */
    public static final String TLS = "TLS";

    /**
     * 收到信息
     */
    public static final String WECC_CHAT_RECEIVEDATA = "WECC_CHAT_RECEIVEDATA";

    /**
     * 发送消息成功
     */
    public static final String WECC_CHAT_POSTDATA_SUCC = "WECC_CHAT_POSTDATA_SUCC";

    /**
     * 断开连接
     */
    public static final String WECC_WEBM_CALL_DISCONNECTED = "WECC_WEBM_CALL_DISCONNECTED";

    /**
     * 正在排队
     */
    public static final String WECC_WEBM_CALL_QUEUING = "WECC_WEBM_CALL_QUEUING";

    /**
     * 排队超时
     */
    public static final String WECC_WEBM_QUEUE_TIMEOUT = "WECC_WEBM_QUEUE_TIMEOUT";

    /**
     * 排队超时
     */
    public static final String WECC_WEBM_CANCEL_QUEUE = "WECC_WEBM_CANCEL_QUEUE";

    /**
     * 呼叫失败
     */
    public static final String WECC_WEBM_CALL_FAIL = "WECC_WEBM_CALL_FAIL";

    /**
     * 会议加入
     */
    public static final String WECC_MEETING_PREPARE_JOIN = "WECC_MEETING_PREPARE_JOIN";

    /**
     * 编码格式-GBK
     */
    public static final String CC_CHARSET_GBK = "GBK";

    /**
     * 编码格式-UTF-8
     */
    public static final String CHARSET_UTF_8 = "UTF-8";

    /**
     * 本地日志文件
     */
    public static final String QUICK_USER_LOG_FILE_NAME = "QuickUser-API.log";

    /**
     * 日志路径
     */
    public static final String QUICK_USER_LOG = "QuickUserLOG";

    /**
     * 日志级别
     */
    public static final int LOG_LEVEL = 2;

    /**
     * 域名解析
     */
    public static final String PARSE_DOMAIN = "PARSEDOMAIN";

    /**
     * 错误码-正常
     */
    public static final int RET_OK = 0;

    /**
     * 参数校验错误
     */
    public static final int RET_ERROR_PARAM = -1;

    /**
     * 语音未连接
     */
    public static final int RET_ERROR_AUDIO_NOT_CONNECTED = -3;

    /**
     * 数据解析出错
     */
    public static final int RET_ERROR_RESPONSE = -4;

    /**
     * 网络出错
     */
    public static final int RET_ERROR_NETWORK = -5;

    /**
     * 提示强制登出
     */
    public static final int RET_WILL_LOGOUT = -6;

    /**
     * 会议重连成功通知
     **/
    public static final String CONF_RECONNECTED = "conf_reconnect";

    /**
     * 用户加入会议通知
     **/
    public static final String CONF_USER_ENTER_EVENT = "conf_user_enter";

    /**
     * 用户离开会议通知
     **/
    public static final String CONF_USER_LEAVE_EVENT = "conf_user_leave";

    /**
     * 视频状态变化通知
     **/
    public static final String COMPT_VIDEO_SWITCH_EVENT = "conf_compt_video_switch";

    /**
     * 本地视频截图返回图片byte数组通知
     */
    public static final String CC_SNAP_SHOT_DATA = "conf_snap_shot_data";

    /**
     * 在通话建立过程中，
     * VTA发送该消息给VTM，
     * 告知VTA侧的会话信息，
     * 并请求VTM告知VTM侧的会话信息
     **/
    public static final int CONF_NEGOTIATE_MSG = 25;

    /**
     * 质检员加入
     **/
    public static final int CONF_MONITOR_JOIN_MSG = 26;

    /**
     * 在VTM收到VTA的25类型的消息后，
     * 所需应答的消息，
     * 告知VTA本侧的会话相关消息
     **/
    public static final int CONF_NEGOTIATE_RESPOND_MSG = 27;

    /**
     * 质检员加入响应消息
     **/
    public static final int CONF_MONITOR_JOIN_RESPOND = 38;

    /**
     * 私有化构造
     */
    private NotifyMessage()
    {

    }
}
