package com.huawei.esdk.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.huawei.esdk.Constant;
import com.huawei.esdk.MobileCC;
import com.huawei.esdk.NotifyMessage;
import com.huawei.esdk.R;
import com.huawei.esdk.activity.ChatActivity;
import com.huawei.esdk.service.ics.SystemConfig;
import com.huawei.esdk.service.ics.model.BroadMsg;
import com.huawei.esdk.utils.LogUtil;
import com.huawei.esdk.utils.StringUtils;
import com.huawei.esdk.utils.ToastUtil;

import java.io.Serializable;

/**
 * Created on 2017/12/28.
 */
public class ShareFragment extends LazyFragment implements View.OnClickListener
{
    private static final String TAG = "ShareFragment";
    public static final String INTENT_INT_INDEX="index";

    private Context context;
    private RelativeLayout container;
    private RelativeLayout docContainer;
    private LinearLayout controlBar;
    private Button btnEnd;
    private LocalBroadcastManager localBroadcastManager;

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
            LogUtil.d(TAG, "action = " + action);
            if (NotifyMessage.CALL_MSG_USER_JOIN.equals(action))
            {
                //加入会议
                LogUtil.d(TAG, "join conf.");
                ToastUtil.showToast(getApplicationContext(), getString(R.string.conf_join), Toast.LENGTH_SHORT);
            }
            else if (NotifyMessage.CALL_MSG_USER_NETWORK_ERROR.equals(action))
            {
                //网络错误
                LogUtil.d(TAG, "network error.");
            }
            else if (NotifyMessage.CONF_USER_LEAVE_EVENT.equals(action))
            {
            }
            else if (NotifyMessage.CALL_MSG_USER_RECEIVE_SHARED_DATA.equals(action))
            {
                //共享事件
                Serializable serializable = intent.getSerializableExtra(NotifyMessage.CC_MSG_CONTENT);
                if (null == serializable)
                {
                    LogUtil.d(TAG, "getSerializableExtra() return null.");
                    return;
                }
                BroadMsg notifyMsg = (BroadMsg) serializable;
                String type = notifyMsg.getRequestCode().getRCode();

                if (null == notifyMsg.getRequestInfo())
                {
                    LogUtil.d(TAG, "getRequestInfo() return  null.");
                    return;
                }
                String state = notifyMsg.getRequestInfo().getMsg();

                String shareType = "";
                String shareState = "";
                if (String.valueOf(Constant.SHARE_DESKTOP).equals(type))
                {
                    shareType = "shareDeskTop";
                }
                else if (String.valueOf(Constant.SHARE_DOC).equals(type))
                {
                    shareType = "shareDoc";
                }

                if ((String.valueOf(Constant.SHARE_START)).equals(state))
                {
                    shareState = "start !";
                    setShareContainer(type);
                }
                else if ((String.valueOf(Constant.SHARE_STOP)).equals(state))
                {
                    shareState = "stop !";
                }
                LogUtil.d(TAG, shareType + " - " + shareState);
            }
            else if (NotifyMessage.CALL_MSG_ON_STOP_MEETING.equals(action))
            {
                //结束会议成功
                ToastUtil.showToast(getApplicationContext(), getString(R.string.conf_end), Toast.LENGTH_SHORT);
                returnHomePage();
            }
        }
    };

    public static ShareFragment newInstance(int tabIndex, boolean isLazyLoad, String pagerTitle)
    {
        Bundle args = new Bundle();
        args.putInt(INTENT_INT_INDEX, tabIndex);
        args.putBoolean(LazyFragment.INTENT_BOOLEAN_LAZYLOAD, isLazyLoad);
        ShareFragment fragment = new ShareFragment();
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
        setContentView(R.layout.share_layout);
        if (null == getActivity())
        {
            return;
        }
        context = getActivity();
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
        if (SystemConfig.getInstance().isMeeting())
        {
            controlBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPauseLazy()
    {
    }

    @Override
    public void onDestroyViewLazy()
    {
        localBroadcastManager.unregisterReceiver(receiver);
    }

    @Override
    public void onClick(View view) {
        if (R.id.btn_end == view.getId())
        {
            //结束会议
            MobileCC.getInstance().releaseConf(true);
        }
    }

    /**
     * 设置共享容器
     * @param type
     */
    private void setShareContainer(String type)
    {
        //桌面共享
        if (String.valueOf(Constant.SHARE_DESKTOP).equals(type))
        {
            MobileCC.getInstance().setShareContainer(context, container, Constant.SHARE_DESKTOP);
        }
        //文档共享
        else if (String.valueOf(Constant.SHARE_DOC).equals(type))
        {
            MobileCC.getInstance().setShareContainer(context, docContainer, Constant.SHARE_DOC);
        }
    }

    /**
     * 初始化
     */
    private void init()
    {
        container = (RelativeLayout) findViewById(R.id.layout_container);
        docContainer = (RelativeLayout) findViewById(R.id.layout_doc_container);
        controlBar = (LinearLayout) findViewById(R.id.layout_control_bar);
        btnEnd = (Button) findViewById(R.id.btn_end);
        localBroadcastManager = LocalBroadcastManager.getInstance(context);

        //注册按钮点击事件
        btnEnd.setOnClickListener(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(NotifyMessage.CALL_MSG_USER_RECEIVE_SHARED_DATA);
        filter.addAction(NotifyMessage.CALL_MSG_ON_STOP_MEETING);
        filter.addAction(NotifyMessage.CALL_MSG_USER_JOIN);
        filter.addAction(NotifyMessage.CALL_MSG_USER_NETWORK_ERROR);
        filter.addAction(NotifyMessage.CONF_USER_LEAVE_EVENT);
        localBroadcastManager.registerReceiver(receiver, filter);
    }

    /**
     * 返回主页面
     */
    private void returnHomePage()
    {
        if (null != getActivity())
        {
            ((ChatActivity)getActivity()).returnHome();
        }
    }
}
