package com.huawei.esdk.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.esdk.Constant;
import com.huawei.esdk.MobileCC;
import com.huawei.esdk.NotifyMessage;
import com.huawei.esdk.R;
import com.huawei.esdk.activity.ChatActivity;
import com.huawei.esdk.im.Msg;
import com.huawei.esdk.im.MsgAdapter;
import com.huawei.esdk.service.ics.SystemConfig;
import com.huawei.esdk.service.ics.model.BroadMsg;
import com.huawei.esdk.utils.LogUtil;
import com.huawei.esdk.utils.StringUtils;
import com.huawei.esdk.utils.ToastUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.huawei.esdk.NotifyMessage.RET_OK;

/**
 * Created on 2017/12/28.
 */
public class HomeFragment extends LazyFragment implements View.OnClickListener
{
    private TextView txtStatus;
    private TextView txtName;
    private Button btnLogout;
    private Button btnCallText;
    private Button btnGetVerifyCode;
    private ImageView imgVerifyCode;
    private EditText edtVerifyCode;

    private ListView lvMsg;
    private EditText edtMsg;

    private Button btnSend;
    private Button btnCancelQueue;
    private Button btnRelease;
    private Button btnCallAudio;
    private Button btnConf;
    private Button btnReleaseText;

    //默认呼叫类型为语音呼叫
    private MsgAdapter adapter;
    private List<Msg> msgList = new ArrayList<>();
    private LocalBroadcastManager localBroadcastManager;
    private IntentFilter filter;
    private Context context;

    private static final String TAG = "HomeFragment";

    public static final String INTENT_INT_INDEX="index";

    public static HomeFragment newInstance(int tabIndex, boolean isLazyLoad, String pagerTitle)
    {
        Bundle args = new Bundle();
        args.putInt(INTENT_INT_INDEX, tabIndex);
        args.putBoolean(LazyFragment.INTENT_BOOLEAN_LAZYLOAD, isLazyLoad);
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        fragment.setPagerTitle(pagerTitle);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateViewLazy(Bundle savedInstanceState)
    {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.home_layout);

        context = getActivity();
        if (null == context)
        {
            return;
        }

        //hide soft keyboard
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        localBroadcastManager = LocalBroadcastManager.getInstance(context);

        init();
    }

    @Override
    protected void onFragmentStartLazy()
    {
    }

    @Override
    protected void onFragmentStopLazy()
    {
    }

    @Override
    public void onResumeLazy()
    {
        //注册广播
        if (localBroadcastManager != null)
        {
            localBroadcastManager.registerReceiver(receiver, filter);
        }
    }

    @Override
    public void onPauseLazy()
    {
        //注销广播
        if (localBroadcastManager != null)
        {
            localBroadcastManager.unregisterReceiver(receiver);
        }
    }

    @Override
    public void onDestroyViewLazy()
    {
    }

    /**
     * 初始化
     */
    private void init()
    {
        //初始化控件
        txtStatus = (TextView) findViewById(R.id.txt_status);
        txtName = (TextView) findViewById(R.id.txt_name);
        btnLogout = (Button) findViewById(R.id.btn_logout);
        btnCallText = (Button) findViewById(R.id.btn_call_text);

        btnGetVerifyCode = (Button) findViewById(R.id.btn_get_verifycode);
        imgVerifyCode = (ImageView) findViewById(R.id.img_verifycode);
        edtVerifyCode = (EditText) findViewById(R.id.edt_verifycode);

        btnCallAudio = (Button) findViewById(R.id.btn_call_audio);

        lvMsg = (ListView) findViewById(R.id.lv_msg);
        edtMsg = (EditText) findViewById(R.id.edt_msg);
        btnSend = (Button) findViewById(R.id.btn_send);

        adapter = new MsgAdapter(getActivity(), R.layout.msg_item, msgList);
        lvMsg.setAdapter(adapter);

        btnCancelQueue = (Button) findViewById(R.id.btn_cancel_queue);
        btnRelease = (Button) findViewById(R.id.btn_release);
        btnReleaseText = (Button) findViewById(R.id.btn_text_release);
        btnConf = (Button) findViewById(R.id.btn_conf);

        //初始化状态
        txtStatus.setText(getString(R.string.status_login));
        txtName.setText(SystemConfig.getInstance().getUserName());

        //注册点击事件
        btnLogout.setOnClickListener(this);
        btnCallText.setOnClickListener(this);
        btnGetVerifyCode.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        btnCallAudio.setOnClickListener(this);
        btnCancelQueue.setOnClickListener(this);
        btnRelease.setOnClickListener(this);
        btnReleaseText.setOnClickListener(this);
        btnConf.setOnClickListener(this);

        //注册广播拦截事件
        filter = new IntentFilter();
        filter.addAction(NotifyMessage.CALL_MSG_ON_CONNECTED);
        filter.addAction(NotifyMessage.CHAT_MSG_ON_RECEIVE);
        filter.addAction(NotifyMessage.CHAT_MSG_ON_SEND);
        filter.addAction(NotifyMessage.CALL_MSG_ON_DISCONNECTED);
        filter.addAction(NotifyMessage.CALL_MSG_ON_QUEUE_INFO);
        filter.addAction(NotifyMessage.CALL_MSG_ON_DROPCALL);
        filter.addAction(NotifyMessage.CALL_MSG_ON_SUCCESS);
        filter.addAction(NotifyMessage.CALL_MSG_ON_QUEUE_TIMEOUT);
        filter.addAction(NotifyMessage.CALL_MSG_ON_FAIL);
        filter.addAction(NotifyMessage.CALL_MSG_ON_CANCEL_QUEUE);
        filter.addAction(NotifyMessage.CALL_MSG_ON_NET_QUALITY_LEVEL);
        filter.addAction(NotifyMessage.CALL_MSG_ON_QUEUING);
        filter.addAction(NotifyMessage.CALL_MSG_ON_CONNECT);
        filter.addAction(NotifyMessage.CALL_MSG_ON_VERIFYCODE);
        filter.addAction(NotifyMessage.CALL_MSG_ON_CALL_END);
        filter.addAction(NotifyMessage.AUTH_MSG_ON_LOGOUT);
        filter.addAction(NotifyMessage.CALL_MSG_ON_APPLY_MEETING);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_logout:
                logout();
                break;
            case R.id.btn_call_text:
                makeMsgCall();
                break;
            case R.id.btn_get_verifycode:
                MobileCC.getInstance().getVerifyCode();
                break;
            case R.id.btn_call_audio:
                makeCall();
                break;
            case R.id.btn_send:
                sendMessage();
                break;
            case R.id.btn_cancel_queue:
                MobileCC.getInstance().cancelQueue();
                break;
            case R.id.btn_release:
                MobileCC.getInstance().stopCall();
                break;
            case R.id.btn_text_release:
                MobileCC.getInstance().releaseText();
                break;
            case R.id.btn_conf:
                MobileCC.getInstance().startConf();
                break;
            default:
                break;
        }
    }

    /**
     * 用户注销
     */
    private void logout()
    {
        int nRet = MobileCC.getInstance().logout();
        switch (nRet)
        {
            case Constant.IS_TEXT_CALLING:
                //当前正在文字通话
                ToastUtil.showToast(getApplicationContext(), getString(R.string.release_text_first), Toast.LENGTH_SHORT);
                break;
            case Constant.IS_CALLING:
                //当前正在音视频通话
                ToastUtil.showToast(getApplicationContext(), getString(R.string.release_call_first), Toast.LENGTH_SHORT);
                break;
            case Constant.IS_MEETING:
                //当前正在会议
                ToastUtil.showToast(getApplicationContext(), getString(R.string.release_conf_first), Toast.LENGTH_SHORT);
                break;
            default:
                break;
        }
    }

    /**
     * 发起文字呼叫
     */
    private void makeMsgCall()
    {
        hideKeyboard();

        //不符合文字呼叫的情况直接返回
        if (!MobileCC.getInstance().isTextCallAvailable())
        {
            return;
        }

        int nRet = MobileCC.getInstance().webChatCall(SystemConfig.getInstance().getTextAccessCode(), "", edtVerifyCode.getText().toString());
        if (NotifyMessage.RET_ERROR_PARAM == nRet)
        {
            LogUtil.d(TAG, "makecall()  fail");
            ToastUtil.showToast(getApplicationContext(), getString(R.string.call_fail_return), Toast.LENGTH_SHORT);
        }
        else if (RET_OK == nRet)
        {
            //成功发起呼叫，验证码置空
            imgVerifyCode.setImageBitmap(null);
        }
    }

    /**
     * 发起语音或视频呼叫
     */
    private void makeCall()
    {
        hideKeyboard();

        //不符合呼叫
        if (!MobileCC.getInstance().isCallAvaiable())
        {
            ToastUtil.showToast(getApplicationContext(), getString(R.string.release_call_first),Toast.LENGTH_SHORT);
            return;
        }

        //验证码置空
        imgVerifyCode.setImageBitmap(null);

        //选择呼叫类型
        showCallTypeDialog();
    }

    /**
     * 显示对话框选择通话方式
     */
    private void showCallTypeDialog()
    {
        //数组存放dialog展示的内容
        String[] items = {getString(R.string.audio), getString(R.string.video)};

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle(R.string.choose_call_type);
        dialogBuilder.setItems(items, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                //i为数组items的索引，0对应语音呼叫类型，1对应视屏呼叫类型
                if(0 == i)
                {
                    getAudioConnect();
                }
                //发起视频呼叫
                else if (1 == i)
                {
                    startVideo();
                }
            }
        });
        AlertDialog callTypeDialog = dialogBuilder.create();
        callTypeDialog.show();
    }

    /**
     * 发送信息
     */
    private void sendMessage()
    {
        //对输入的内容进行空判断
        if (StringUtils.isEmpty(edtMsg.getText().toString()))
        {
            ToastUtil.showToast(getApplicationContext(), getString(R.string.input_msg), Toast.LENGTH_SHORT);
            return;
        }
        //判断消息是否发送成功
        int nRet = MobileCC.getInstance().sendMsg(edtMsg.getText().toString());
        if (NotifyMessage.RET_ERROR_PARAM == nRet)
        {
            ToastUtil.showToast(getApplicationContext(), getString(R.string.text_too_long), Toast.LENGTH_SHORT);
        }
        edtMsg.setText("");
    }

    /**
     * 发起语音呼叫
     */
    private void getAudioConnect()
    {
        LogUtil.d(TAG, "-----------audio call-------------");
        int nRet = MobileCC.getInstance().makeCall(SystemConfig.getInstance().getAudioAccessCode(), MobileCC.AUDIO_CALL, "", edtVerifyCode.getText().toString(), SystemConfig.MEDIA_AUDIA);
        //呼叫错误
        if (NotifyMessage.RET_ERROR_PARAM == nRet)
        {
            ToastUtil.showToast(getApplicationContext(), getString(R.string.make_call_error), Toast.LENGTH_SHORT);
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (StringUtils.isEmpty(action))
            {
                LogUtil.d(TAG, "action is empty.");
                return;
            }

            Serializable serializable = intent.getSerializableExtra(NotifyMessage.CC_MSG_CONTENT);
            if (null == serializable)
            {
                LogUtil.d(TAG, "getSerializableExtra() return null.");
                return;
            }
            BroadMsg broadMsg = (BroadMsg) serializable;

            if (NotifyMessage.AUTH_MSG_ON_LOGOUT.equals(action))
            {
                handleEventLougout(broadMsg);
            }
            else if (NotifyMessage.CALL_MSG_ON_VERIFYCODE.equals(action))
            {
                handleEventVerifyCode(broadMsg);
            }
            else if (NotifyMessage.CHAT_MSG_ON_SEND.equals(action))
            {
                handleEventSendMessage(broadMsg);
            }
            else if (NotifyMessage.CHAT_MSG_ON_RECEIVE.equals(action))
            {
                handleEventReceiveMessage(broadMsg);
            }
            else if (NotifyMessage.CALL_MSG_ON_CONNECTED.equals(action))
            {
                handleEventConnected();
            }
            else if (NotifyMessage.CALL_MSG_ON_CONNECT.equals(action))
            {
                handleEventConnect(broadMsg);
            }
            else if (NotifyMessage.CALL_MSG_ON_DISCONNECTED.equals(action))
            {
                handleEventDisconnect(broadMsg);
            }
            else if (NotifyMessage.CALL_MSG_ON_QUEUING.equals(action))
            {
                handleEventQueuing();
            }
            else if (NotifyMessage.CALL_MSG_ON_QUEUE_INFO.equals(action))
            {
                handleEventQueueInfo(broadMsg);
            }
            else if (NotifyMessage.CALL_MSG_ON_CANCEL_QUEUE.equals(action))
            {
                handleEventCancelQueue(broadMsg);
            }
            else if (NotifyMessage.CALL_MSG_ON_QUEUE_TIMEOUT.equals(action))
            {
                handleEventQueueTimeOut();
            }
            else if (NotifyMessage.CALL_MSG_ON_NET_QUALITY_LEVEL.equals(action))
            {
                handleEventNetQualityChanged(broadMsg);
            }
            else if (NotifyMessage.CALL_MSG_ON_SUCCESS.equals(action)
                    && SystemConfig.MEDIA_AUDIA  == SystemConfig.getInstance().getCurrentMediaAbility())
            {
                //语音连接成功
                ToastUtil.showToast(getApplicationContext(), getString(R.string.call_success), Toast.LENGTH_SHORT);
            }
            else if (NotifyMessage.CALL_MSG_ON_FAIL.equals(action))
            {
                handleEventCallFail();
            }
            else if (NotifyMessage.CALL_MSG_ON_CALL_END.equals(action))
            {
                LogUtil.d(TAG, "call end ");
                ToastUtil.showToast(getApplicationContext(), getString(R.string.call_end), Toast.LENGTH_SHORT);
            }
            else if (NotifyMessage.CALL_MSG_ON_DROPCALL.equals(action))
            {
                handleEventDropCall(broadMsg);
            }
            else if (NotifyMessage.CALL_MSG_ON_APPLY_MEETING.equals(action))
            {
                handleEventApplyConf(broadMsg);
            }
        }
    };

    public static Bitmap base64ToBitmap(String base64Data)
    {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * 隐藏小键盘
     */
    private void hideKeyboard()
    {
        View view = getActivity().getCurrentFocus();
        IBinder binder;
        if (null == view)
        {
            return;
        }
        binder = view.getWindowToken();

        if (null == binder)
        {
            return;
        }
        ((InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(binder, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 处理注销事件
     */
    private void handleEventLougout(BroadMsg broadMsg)
    {
        if (StringUtils.isEmpty(broadMsg.getRequestCode().getRCode()))
        {
            ToastUtil.showToast(getApplicationContext(), getString(R.string.logout_fail) + broadMsg.getRequestCode().getErrorCode(), Toast.LENGTH_SHORT);
            return;
        }

        if (MobileCC.MESSAGE_OK.equals(broadMsg.getRequestCode().getRCode()))
        {
            //注销成功, 返回上一个界面
            getActivity().finish();
            return;
        }

        //注销不成功
        ToastUtil.showToast(getApplicationContext(), getString(R.string.logout_fail) + broadMsg.getRequestCode().getRCode(), Toast.LENGTH_SHORT);
    }

    /**
     * 处理获取验证码事件
     */
    private void handleEventVerifyCode(BroadMsg broadMsg)
    {
        if (StringUtils.isEmpty(broadMsg.getRequestCode().getRCode()))
        {
            ToastUtil.showToast(getApplicationContext(), getString(R.string.get_verifycode_fail) + broadMsg.getRequestCode().getErrorCode(), Toast.LENGTH_SHORT);
            return;
        }

        String retcode = broadMsg.getRequestCode().getRCode();

        if (!MobileCC.MESSAGE_OK.equals(retcode))
        {
            //返回码不正确
            ToastUtil.showToast(getApplicationContext(), getString(R.string.get_verifycode_fail) + retcode, Toast.LENGTH_SHORT);
            return;
        }

        //返回码正确，设置验证码
        String verifyCode = broadMsg.getRequestInfo().getMsg();
        Bitmap bitmap = base64ToBitmap(verifyCode);
        imgVerifyCode.setImageBitmap(bitmap);
    }

    /**
     * 处理发送消息事件
     */
    private void handleEventSendMessage(BroadMsg broadMsg)
    {
        if (StringUtils.isEmpty(broadMsg.getRequestCode().getRCode()))
        {
            //retcode为空时
            ToastUtil.showToast(getApplicationContext(), getString(R.string.text_send_fail) + broadMsg.getRequestCode().getErrorCode(), Toast.LENGTH_LONG);
            return;
        }

        //retcode不为为空时
        String retcode = broadMsg.getRequestCode().getRCode();
        if (!MobileCC.MESSAGE_OK.equals(retcode))
        {
            //消息发送未成功
            ToastUtil.showToast(getApplicationContext(), getString(R.string.text_send_fail) + retcode, Toast.LENGTH_LONG);
            return;
        }

        //消息发送成功
        String content = broadMsg.getRequestInfo().getMsg();
        if (!StringUtils.isEmpty(content))
        {
            Msg sendContent = new Msg(content, Msg.TYPE_SENT);
            msgList.add(sendContent);
            adapter.notifyDataSetChanged();
            lvMsg.setSelection(msgList.size());
        }
    }

    /**
     * 处理接受消息事件
     */
    private void handleEventReceiveMessage(BroadMsg broadMsg)
    {
        if (null == broadMsg.getRequestInfo() || StringUtils.isEmpty(broadMsg.getRequestInfo().getMsg()))
        {
            LogUtil.d(TAG, "receive msg： null");
            return;
        }
        //收到IM消息
        String receiveData = broadMsg.getRequestInfo().getMsg();
        LogUtil.d(TAG, "receive msg： " + receiveData);

        //显示消息
        Msg receiveContent = new Msg(receiveData, Msg.TYPE_RECEIVED);
        msgList.add(receiveContent);
        adapter.notifyDataSetChanged();
        lvMsg.setSelection(msgList.size());
    }

    /**
     * 处理连接成功事件
     */
    private void handleEventConnected()
    {
        updateStatus();
        btnCancelQueue.setVisibility(View.INVISIBLE);
        ToastUtil.showToast(getApplicationContext(), getString(R.string.connected), Toast.LENGTH_LONG);
    }

    /**
     * 处理连接失败事件
     */
    private void handleEventCallFail()
    {
        LogUtil.d(TAG, "call fail ");
        ToastUtil.showToast(getApplicationContext(), getString(R.string.call_fail_return), Toast.LENGTH_SHORT);
    }

    /**
     * 处理连接事件，此处只是连接请求发送成功，并不是连接成功
     */
    private void handleEventConnect(BroadMsg broadMsg)
    {
        if (StringUtils.isEmpty(broadMsg.getRequestCode().getRCode()))
        {
            //没有收到服务器的retcode，打印出错误码
            ToastUtil.showToast(getApplicationContext(), getString(R.string.connect_fail) + broadMsg.getRequestCode().getErrorCode(), Toast.LENGTH_LONG);
            return;
        }

        //收到服务器的retcode，打印出错误码
        if (!MobileCC.MESSAGE_OK.equals(broadMsg.getRequestCode().getRCode()))
        {
            ToastUtil.showToast(getApplicationContext(), getString(R.string.connect_fail) + broadMsg.getRequestCode().getRCode(), Toast.LENGTH_LONG);
            return;
        }

        //返回正常
        if (MobileCC.MESSAGE_TYPE_TEXT.equals(broadMsg.getType()))
        {
            LogUtil.d(TAG, "webChatCall --->get callId success");
        }
        else
        {
            LogUtil.d(TAG, "get audio ablity success");
        }
    }

    /**
     * 处理断开连接事件
     */
    private void handleEventDisconnect(BroadMsg broadMsg)
    {
        if (null == broadMsg.getRequestInfo() || StringUtils.isEmpty(broadMsg.getRequestInfo().getMsg()))
        {
            LogUtil.d(TAG, "disconnected fail");
            return;
        }
        //断开文字能力
        if (MobileCC.MESSAGE_TYPE_TEXT.equals(broadMsg.getRequestInfo().getMsg()))
        {
            ToastUtil.showToast(getApplicationContext(), getString(R.string.text_disconnect), Toast.LENGTH_SHORT);
        }
        //断开语音视频能力
        else if (MobileCC.MESSAGE_TYPE_AUDIO.equals(broadMsg.getRequestInfo().getMsg()))
        {
            MobileCC.getInstance().releaseCall();
        }

        //断开后更新状态
        updateStatus();
    }

    /**
     * 处理排队事件
     */
    private void handleEventQueuing()
    {
        btnCancelQueue.setVisibility(View.VISIBLE);
        ToastUtil.showToast(getApplicationContext(), getString(R.string.queuing), Toast.LENGTH_SHORT);
    }

    /**
     * 处理排队信息事件
     */
    private void handleEventQueueInfo(BroadMsg broadMsg)
    {
        String retCode = broadMsg.getRequestCode().getRCode();
        if (StringUtils.isEmpty(retCode))
        {
            ToastUtil.showToast(getApplicationContext(), getString(R.string.get_queue_info_error) + broadMsg.getRequestCode().getErrorCode(), Toast.LENGTH_SHORT);
            return;
        }


        if (!MobileCC.MESSAGE_OK.equals(retCode))
        {
            //不是排队状态
            ToastUtil.showToast(getApplicationContext(), getString(R.string.not_queue_status_error) + retCode, Toast.LENGTH_SHORT);
            return;
        }

        //正处于排队状态
        long position = broadMsg.getQueueInfo().getPosition();
        LogUtil.d(TAG, "queuing , position =" + position);

        btnCancelQueue.setVisibility(View.VISIBLE);
        ToastUtil.showToast(getApplicationContext(), getString(R.string.queuing), Toast.LENGTH_SHORT);
    }

    /**
     *处理取消排队事件
     */
    private void handleEventCancelQueue(BroadMsg broadMsg)
    {
        String retCode = broadMsg.getRequestCode().getRCode();
        if (StringUtils.isEmpty(retCode) || !MobileCC.MESSAGE_OK.equals(retCode))
        {
            ToastUtil.showToast(getApplicationContext(), getString(R.string.cancel_queue_fail), Toast.LENGTH_SHORT);
            return;
        }

        btnCancelQueue.setVisibility(View.INVISIBLE);
        LogUtil.d(TAG, "cancel queue success");
        ToastUtil.showToast(getApplicationContext(), getString(R.string.cancel_queue_success), Toast.LENGTH_SHORT);
    }

    /**
     * 处理排队超时事件
     */
    private void handleEventQueueTimeOut()
    {
        btnCancelQueue.setVisibility(View.INVISIBLE);
        MobileCC.getInstance().cancelQueue();
        ToastUtil.showToast(getApplicationContext(), getString(R.string.queue_timeout), Toast.LENGTH_SHORT);
    }

    /**
     * 处理网络等级变化
     */
    private void handleEventNetQualityChanged(BroadMsg broadMsg)
    {
        int netLevel = broadMsg.getRequestCode().getNetLevel();
        LogUtil.d(TAG, "net level is:" + netLevel);

        if (netLevel < 5)
        {
            ToastUtil.showToast(getApplicationContext(), getString(R.string.network_level) + netLevel, Toast.LENGTH_SHORT);
        }
    }

    /**
     * 处理挂断事件
     */
    private void handleEventDropCall(BroadMsg broadMsg)
    {
        String retCode = broadMsg.getRequestCode().getRCode();
        if (StringUtils.isEmpty(retCode))
        {
            ToastUtil.showToast(getApplicationContext(), getString(R.string.dropcall_fail) + broadMsg.getRequestCode().getErrorCode(), Toast.LENGTH_SHORT);
            return;
        }

        if (MobileCC.MESSAGE_OK.equals(retCode))
        {
            LogUtil.d(TAG, "drop call success");
            return;
        }

        ToastUtil.showToast(getApplicationContext(), getString(R.string.dropcall_fail) + broadMsg.getRequestCode().getRCode(), Toast.LENGTH_SHORT);
    }

    /**
     * 处理会议申请事件
     */
    private void handleEventApplyConf(BroadMsg broadMsg)
    {
        String retCode = broadMsg.getRequestCode().getRCode();
        //仅处理会议申请失败的情况
        if (StringUtils.isEmpty(retCode))
        {
            ToastUtil.showToast(getApplicationContext(), getString(R.string.apply_meeting_fail) + broadMsg.getRequestCode().getErrorCode(), Toast.LENGTH_SHORT);
        }
        else if (!MobileCC.MESSAGE_OK.equals(retCode))
        {
            ToastUtil.showToast(getApplicationContext(), getString(R.string.apply_meeting_fail) + retCode, Toast.LENGTH_SHORT);
        }
    }

    /**
     * 跳转到视频界面
     */
    private void startVideo()
    {
        imgVerifyCode.setImageBitmap(null);
        SystemConfig.getInstance().setVerifyCode(edtVerifyCode.getText().toString());
        ((ChatActivity)getActivity()).startVideo();
    }

    /**
     * 更新当前用户状态
     */
    public void updateStatus()
    {
        if (SystemConfig.getInstance().isAudioConnected()
                || SystemConfig.getInstance().isVideoConnected()
                || SystemConfig.getInstance().isTextConnected())
        {
            //已连接，显示通话中
            txtStatus.setText(getString(R.string.status_on_call));
        }
        else
        {
            //未连接，显示已登录
            txtStatus.setText(getString(R.string.status_login));
        }
    }
}
