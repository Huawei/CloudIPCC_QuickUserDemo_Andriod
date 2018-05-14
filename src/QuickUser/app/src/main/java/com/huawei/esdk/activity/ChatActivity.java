package com.huawei.esdk.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.huawei.esdk.MyFragmentPagerAdapter;
import com.huawei.esdk.NoScrollViewPager;
import com.huawei.esdk.NotifyMessage;
import com.huawei.esdk.R;
import com.huawei.esdk.fragment.HomeFragment;
import com.huawei.esdk.fragment.ShareFragment;
import com.huawei.esdk.fragment.VideoFragment;
import com.huawei.esdk.service.ics.SystemConfig;
import com.huawei.esdk.service.ics.model.BroadMsg;
import com.huawei.esdk.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2017/12/28.
 */
public class ChatActivity extends AppCompatActivity
{
    private NoScrollViewPager viewPager;
    private List<Fragment> fragmentList = new ArrayList<>();

    private HomeFragment homeFragment;
    private ShareFragment shareFragment;
    private VideoFragment videoFragment;
    private TabLayout tabLayout;
    private LocalBroadcastManager localBroadcastManager;
    private IntentFilter filter;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();
    }

    /**
     * 初始化
     */
    private void init()
    {
        //初始化控件
        viewPager = (NoScrollViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        homeFragment = HomeFragment.newInstance(0, false, getString(R.string.home_page));
        videoFragment = VideoFragment.newInstance(1, true, getString(R.string.video_page));
        shareFragment = ShareFragment.newInstance(2, true, getString(R.string.conf_page));
        fragmentList.add(homeFragment);
        fragmentList.add(videoFragment);
        fragmentList.add(shareFragment);

        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentPagerAdapter myFragmentPagerAdapter = new MyFragmentPagerAdapter(supportFragmentManager, fragmentList);
        viewPager.setAdapter(myFragmentPagerAdapter);

        //允许后台最大有2个界面不被销毁
        viewPager.setOffscreenPageLimit(2);
        //默认第一个界面为初始界面
        viewPager.setCurrentItem(0);
        tabLayout.setupWithViewPager(viewPager);

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        filter = new IntentFilter();

        //注册拦截事件
        filter.addAction(NotifyMessage.CALL_MSG_USER_START);
        filter.addAction(NotifyMessage.CALL_MSG_ON_POLL);
        filter.addAction(NotifyMessage.FORCE_LOGOUT);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        localBroadcastManager.registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        localBroadcastManager.unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onBackPressed()
    {
        if (0 == viewPager.getCurrentItem())
        {
            //主页面点击返回键
            ToastUtil.showToast(getApplicationContext(), getString(R.string.click_to_back), Toast.LENGTH_LONG);
        }
        else
        {
            //非主页面点击返回键
            viewPager.setCurrentItem(0);
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            BroadMsg broadMsg = (BroadMsg) intent.getSerializableExtra(NotifyMessage.CC_MSG_CONTENT);

            if (null != broadMsg)
            {
                if (NotifyMessage.CALL_MSG_USER_START.equals(action))
                {
                    //移动到会议页面
                    viewPager.setCurrentItem(2);
                }
                else if ((NotifyMessage.CALL_MSG_ON_POLL.equals(action)))
                {
                    if (null == broadMsg.getRequestCode())
                    {
                        return;
                    }

                    if (NotifyMessage.RET_ERROR_NETWORK == broadMsg.getRequestCode().getErrorCode())
                    {
                        //提示网络错误
                        ToastUtil.showToast(getApplicationContext(), getString(R.string.net_error_retry), Toast.LENGTH_SHORT);
                    }
                    else if (NotifyMessage.RET_WILL_LOGOUT == broadMsg.getRequestCode().getErrorCode())
                    {
                        //提示重新连接失败，返回到登录界面
                        ToastUtil.showToast(getApplicationContext(), getString(R.string.connet_fail_return), Toast.LENGTH_LONG);
                    }
                }
                else if (NotifyMessage.FORCE_LOGOUT.equals(action))
                {
                    SystemConfig.getInstance().clearStatus();
                    handler.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            finish();
                        }
                    }, 5000);
                }
            }
        }
    };

    /**
     * 跳转到视频页面，并发起视屏呼叫
     */
    public void startVideo()
    {
        viewPager.setCurrentItem(1);
        videoFragment.startVideo();
    }

    /**
     * 返回到主页面
     */
    public void returnHome()
    {
        viewPager.setCurrentItem(0);
    }
}
