package com.huawei.esdk.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.huawei.esdk.MobileCC;
import com.huawei.esdk.NotifyMessage;
import com.huawei.esdk.R;
import com.huawei.esdk.service.ics.SystemConfig;
import com.huawei.esdk.service.ics.model.BroadMsg;
import com.huawei.esdk.service.video.VideoControl;
import com.huawei.esdk.utils.ToastUtil;

/**
 * Created on 2017/12/28.
 */
public class VideoFragment extends LazyFragment  implements View.OnClickListener
{

    private IntentFilter filter;
    private FrameLayout remoteView;
    private FrameLayout localView;
    private Button btnSwitchCamera;
    private Button btnRotateCamera;

    private Handler handler = new Handler();
    private static final String TAG = "VideoFragment";
    public static final String INTENT_INT_INDEX="index";

    private Context context;
    private LocalBroadcastManager localBroadcastManager;
    private int count = 1;

    public static VideoFragment newInstance(int tabIndex, boolean isLazyLoad, String pagerTitle)
    {
        Bundle args = new Bundle();
        args.putInt(INTENT_INT_INDEX, tabIndex);
        args.putBoolean(LazyFragment.INTENT_BOOLEAN_LAZYLOAD, isLazyLoad);
        VideoFragment fragment = new VideoFragment();
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
        setContentView(R.layout.video_layout);
        Log.i(TAG, "onCreateViewLazy");

        context = getActivity();
        if (null == context)
        {
            return;
        }

        // 保持屏幕常亮
        getActivity().getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        + WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        + WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
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
        if (localBroadcastManager != null)
        {
            localBroadcastManager.registerReceiver(receiver, filter);
        }
        MobileCC.getInstance().videoOperate(MobileCC.START);
    }

    @Override
    public void onPauseLazy()
    {
        if (localBroadcastManager != null)
        {
            localBroadcastManager.unregisterReceiver(receiver);
        }
        MobileCC.getInstance().videoOperate(MobileCC.STOP);
    }

    @Override
    public void onDestroyViewLazy()
    {
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.btn_switch_camera)
        {
            onSwitchCameraClick();
        }
        else if (view.getId() == R.id.btn_rotate_camera)
        {
            onRotateCameraClick();
        }
    }

    private void onSwitchCameraClick()
    {
        if (!SystemConfig.getInstance().isVideoConnected())
        {
            return;
        }
        count = 1;
        MobileCC.getInstance().switchCamera();
        btnSwitchCamera.setClickable(false);

        Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                btnSwitchCamera.setClickable(true);
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    private void onRotateCameraClick()
    {
        if (!SystemConfig.getInstance().isVideoConnected())
        {
            return;
        }
        btnRotateCamera.setClickable(false);

        Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                btnRotateCamera.setClickable(true);
            }
        };
        handler.postDelayed(runnable, 1000);

        int cameraIndex = SystemConfig.getInstance().getCameraIndex();
        int angel = 90 * count;
        if (cameraIndex == VideoControl.BACK_CAMERA)
        {
            //90
            angel = angel + 90;
        }
        else if (cameraIndex == VideoControl.FRONT_CAMERA)
        {
            //270
            angel = angel + 270;
        }

        MobileCC.getInstance().setVideoRotate(cameraIndex, angel);
        count = count + 1;
    }

    public void startVideo()
    {
        //发起视频匿名呼叫, 注意参数mediaAbility
        int nRet = MobileCC.getInstance().makeCall(SystemConfig.getInstance().getAudioAccessCode(),
                MobileCC.VIDEO_CALL + "",
                "",
                SystemConfig.getInstance().getVerifyCode(),
                SystemConfig.MEDIA_VIDEO);
        if (NotifyMessage.RET_ERROR_PARAM == nRet)
        {
            ToastUtil.showToast(getApplicationContext(), getString(R.string.make_call_error), Toast.LENGTH_SHORT);
        }
    }

    //初始化
    private void init()
    {
        //初始化view
        remoteView = (FrameLayout) findViewById(R.id.view_remote);
        localView = (FrameLayout) findViewById(R.id.view_local);
        btnSwitchCamera = (Button) findViewById(R.id.btn_switch_camera);
        btnRotateCamera = (Button) findViewById(R.id.btn_rotate_camera);

        //注册按钮点击事件
        btnSwitchCamera.setOnClickListener(this);
        btnRotateCamera.setOnClickListener(this);

        //注册广播拦截事件
        filter = new IntentFilter();
        filter.addAction(NotifyMessage.CALL_MSG_REFRESH_LOCALVIEW);
        filter.addAction(NotifyMessage.CALL_MSG_REFRESH_REMOTEVIEW);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            BroadMsg broadMsg = (BroadMsg) intent
                    .getSerializableExtra(NotifyMessage.CC_MSG_CONTENT);

            if (null != broadMsg)
            {
                if (NotifyMessage.CALL_MSG_REFRESH_LOCALVIEW.equals(action))
                {
                    MobileCC.getInstance().setVideoContainer(context, localView, null);
                }
                else if (NotifyMessage.CALL_MSG_REFRESH_REMOTEVIEW.equals(action))
                {
                    MobileCC.getInstance().setVideoContainer(context, null, remoteView);
                }
            }
        }
    };
}
