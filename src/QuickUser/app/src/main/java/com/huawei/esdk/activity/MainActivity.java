package com.huawei.esdk.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.huawei.esdk.MobileCC;
import com.huawei.esdk.R;
import com.huawei.esdk.service.ics.SystemConfig;
import com.huawei.esdk.service.ics.model.BroadMsg;
import com.huawei.esdk.NotifyMessage;
import com.huawei.esdk.utils.LogUtil;
import com.huawei.esdk.utils.StringUtils;
import com.huawei.esdk.utils.ToastUtil;

import java.io.Serializable;

/**
 * Created on 2017/12/28.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = "MainActivity";
    private EditText edtIP;
    private EditText edtPort;
    private EditText edtSIPIP;
    private EditText edtSIPPort;
    private EditText edtTextAccessCode;
    private EditText edtAudioAccessCode;
    private CheckBox cbTLS;
    private EditText edtName;
    private EditText edtAnonymousCard;
    private Button btnLogin;
    private IntentFilter filter;
    private EditText edtHost;
    private EditText edtVdnId;

    private LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //hide soft keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        initView();
    }

    /**
     * 初始化View,filter
     */
    private void initView()
    {
        //初始化控件
        edtIP = (EditText) findViewById(R.id.edt_ip);
        edtPort = (EditText) findViewById(R.id.edt_port);
        edtSIPIP = (EditText) findViewById(R.id.edt_sip_ip);
        edtSIPPort = (EditText) findViewById(R.id.edt_sip_port);
        edtTextAccessCode = (EditText) findViewById(R.id.edt_access_code_text);
        edtAudioAccessCode = (EditText) findViewById(R.id.edt_access_code_audio);
        edtName = (EditText) findViewById(R.id.edt_name);
        edtAnonymousCard = (EditText) findViewById(R.id.edt_anonymous_id);
        btnLogin = (Button) findViewById(R.id.btn_login);
        cbTLS = (CheckBox) findViewById(R.id.cb_tls_srtp);
        //set cbTLS unchecked when init
        cbTLS.setChecked(false);
        edtHost = (EditText) findViewById(R.id.edt_domain);
        edtVdnId = (EditText) findViewById(R.id.edt_vdn);

        //设置点击事件
        btnLogin.setOnClickListener(this);

        //注册广播拦截事件
        filter = new IntentFilter();
        filter.addAction(NotifyMessage.AUTH_MSG_ON_LOGIN);
        filter.addAction(NotifyMessage.PARSE_DOMAIN);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        //注册广播
        localBroadcastManager.registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        //注销广播
        localBroadcastManager.unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_login:
                login();
                break;

            default:
                break;
        }
    }

    private void login()
    {
        hideKeyboard();

        if (!isPreparedLogin())
        {
            //参数不合法时停止登录
            return;
        }

        if (NotifyMessage.RET_OK != setHostAddress())
        {
            //ip不正确时尝试进行域名解析，以异步的方式处理，结果查看NotifyMessage.PARSE_DOMAIN的消息处理
            if (StringUtils.isEmpty(edtHost.getText().toString()))
            {
                ToastUtil.showToast(getApplicationContext(), getString(R.string.net_settings_error), Toast.LENGTH_SHORT);
                return;
            }

            MobileCC.getInstance().parseHost(edtHost.getText().toString());
            return;
        }

        if (NotifyMessage.RET_OK != MobileCC.getInstance().login(edtVdnId.getText().toString(), edtName.getText().toString().trim()))
        {
            ToastUtil.showToast(getApplicationContext(), getString(R.string.retype_name_error), Toast.LENGTH_SHORT);
        }
    }

    /**
     * 隐藏软键盘
     */
    private void hideKeyboard()
    {
        //隐藏软键盘
        View view = MainActivity.this.getCurrentFocus();
        IBinder binder;
        if (null == view)
        {
            return;
        }
        else
        {
            binder = view.getWindowToken();
        }

        if (null == binder)
        {
            return;
        }
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(binder, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 校验输入信息
     *
     * return boolean
     */
    private boolean isPreparedLogin()
    {
        //设置传输加密
        MobileCC.getInstance().setTransportSecurity(cbTLS.isChecked(), cbTLS.isChecked());

        //检查IP
        if (StringUtils.isEmpty(edtIP.getText().toString())
                || StringUtils.isEmpty(edtHost.getText().toString()))
        {
            ToastUtil.showToast(getApplicationContext(), getString(R.string.net_settings_error), Toast.LENGTH_SHORT);
            return false;
        }

        //检查AnonymousCard
        if (StringUtils.isEmpty(edtAnonymousCard.getText().toString()))
        {
            ToastUtil.showToast(getApplicationContext(), getString(R.string.anonymous_card_error), Toast.LENGTH_SHORT);
            return false;
        }
        MobileCC.getInstance().setAnonymousCard(edtAnonymousCard.getText().toString());

        //检查SIP信息
        if (NotifyMessage.RET_OK != MobileCC.getInstance().setSIPServerAddress(edtSIPIP.getText().toString(), edtSIPPort.getText().toString()))
        {
            ToastUtil.showToast(getApplicationContext(), getString(R.string.set_sip_info), Toast.LENGTH_SHORT);
            return false;
        }

        //检查接入码
        if (StringUtils.isEmpty(edtTextAccessCode.getText().toString())
                || StringUtils.isEmpty(edtAudioAccessCode.getText().toString()))
        {
            ToastUtil.showToast(getApplicationContext(), getString(R.string.set_access_code), Toast.LENGTH_SHORT);
            return false;
        }
        SystemConfig.getInstance().setTextAccessCode(edtTextAccessCode.getText().toString());
        SystemConfig.getInstance().setAudioAccessCode(edtAudioAccessCode.getText().toString());

        //检查vdnId
        if (StringUtils.isEmpty(edtVdnId.getText().toString()))
        {
            ToastUtil.showToast(getApplicationContext(), getString(R.string.vdn_error), Toast.LENGTH_SHORT);
            return false;
        }

        return true;
    }

    /**
     * 设置ip、port信息
     *
     * return int
     */
    private int setHostAddress()
    {
        String ipStr = edtIP.getText().toString();
        String portStr = edtPort.getText().toString();
        String host = edtHost.getText().toString();
        return MobileCC.getInstance().setHostAddress(ipStr, portStr, host);
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
            //登陆事件
            if (NotifyMessage.AUTH_MSG_ON_LOGIN.equals(action))
            {
                //retCode为空, 显示errorCode
                if (StringUtils.isEmpty(broadMsg.getRequestCode().getRCode()))
                {
                    ToastUtil.showToast(getApplicationContext(), getString(R.string.login_fail) + broadMsg.getRequestCode().getErrorCode(), Toast.LENGTH_SHORT);
                    return;
                }

                if (!MobileCC.MESSAGE_OK.equals(broadMsg.getRequestCode().getRCode()))
                {
                    //retCode 不为空, 显示后台返回的错误信息
                    ToastUtil.showToast(getApplicationContext(), getString(R.string.login_fail) + broadMsg.getRequestCode().getRCode(), Toast.LENGTH_SHORT);
                    return;
                }

                //登录成功，跳转activity
                startChatActivity();
            }
            //解析域名
            else if (NotifyMessage.PARSE_DOMAIN.equals(action))
            {
                if (StringUtils.isEmpty(broadMsg.getRequestCode().getRCode())
                        || !(MobileCC.MESSAGE_OK.equals(broadMsg.getRequestCode().getRCode())))
                {
                    ToastUtil.showToast(getApplicationContext(), getString(R.string.domain_parse_fail), Toast.LENGTH_SHORT);
                    return;
                }

                //域名解析正常，进行登录
                if (NotifyMessage.RET_OK != MobileCC.getInstance().login(edtVdnId.getText().toString(), edtName.getText().toString().trim()))
                {
                    ToastUtil.showToast(getApplicationContext(), getString(R.string.retype_name_error), Toast.LENGTH_SHORT);
                }
            }
        }
    };

    private void startChatActivity()
    {
        Intent intent2 = new Intent(MainActivity.this, ChatActivity.class);
        intent2.putExtra("textAccessCode", edtTextAccessCode.getText().toString());
        intent2.putExtra("audioAccessCode", edtAudioAccessCode.getText().toString());
        intent2.putExtra("userName", edtName.getText().toString());
        startActivity(intent2);
    }
}
