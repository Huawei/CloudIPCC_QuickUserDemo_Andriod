package com.huawei.esdk.service.video;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Handler;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.huawei.esdk.MobileCC;
import com.huawei.esdk.CCAPP;
import com.huawei.esdk.service.call.CallManager;
import com.huawei.esdk.service.ics.SystemConfig;
import com.huawei.esdk.utils.LogUtil;
import com.huawei.videoengine.ViERenderer;

import common.VideoWndType;

/**
 * VideoControl
 */
public final class VideoControl
{
    /**
     * 后置摄像头
     */
    public static final int BACK_CAMERA = 0;
    /**
     * 前置摄像头
     */
    public static final int FRONT_CAMERA = 1;
    private static VideoControl ins;
    /**
     * 摄像头数量
     */
    private static int numberOfCameras = Camera.getNumberOfCameras();
    /**
     * 本地隐藏视频视图，用一个像素布局
     */
    private SurfaceView localVideo;

    /**
     * 远端视频视图
     */
    private SurfaceView remoteVideo;
    /**
     * 协商后的视频方向
     */
    private VideoConfig videoConfig;
    /**
     * 显示本地视频的 SurfaceView
     **/
    private SurfaceView localSurfaceView;

    /**
     * 用于装载本地视频的 ViewGroup
     **/
    private ViewGroup mLocalContainer;
    /**
     * 用于装载远端视频的 ViewGroup
     **/
    private ViewGroup mRemoteContainer;

    private VideoControl()
    {
        videoConfig = new VideoConfig();
        configVideoCaps();
    }

    /**
     * getIns
     *
     * @return VideoControl
     */
    public static VideoControl getIns()
    {
        if (null == ins)
        {
            ins = new VideoControl();
        }
        return ins;
    }

    /**
     * 切换前后摄像头时,角度也会发生变化.
     */
    public void switchCamera()
    {
        if (!canSwitchCamera())
        {
            return;
        }
        int cameraIndex = videoConfig.getCameraIndex();
        int newIndex = (cameraIndex + 1) % 2;
        LogUtil.d("VideoControl", "cameraIndex" + cameraIndex + " newIndex" + newIndex);
        SystemConfig.getInstance().setCameraIndex(newIndex);
        videoConfig.setCameraIndex(newIndex);
        videoConfig.setOrientPortrait(getVideoChangeOrientation(0, isFrontCamera()) / 90);
        videoConfig.setOrientLandscape(getVideoChangeOrientation(90, isFrontCamera()) / 90);
        videoConfig.setOrientSeascape(getVideoChangeOrientation(270, isFrontCamera()) / 90);

        deployGlobalVideoCaps();
    }

    /**
     * setCallId
     *
     * @param callId callId
     */
    public void setCallId(String callId)
    {
        videoConfig.setSessionId(callId);
    }

    private void configVideoCaps()
    {
        // 设置默认采用的摄像头
        setDefaultCamera();

        // 设置为竖屏
        videoConfig.setOrient(Configuration.ORIENTATION_PORTRAIT);

        videoConfig.setOrientPortrait(getVideoChangeOrientation(0, isFrontCamera()) / 90);
        videoConfig.setOrientLandscape(getVideoChangeOrientation(90, isFrontCamera()) / 90);
        videoConfig.setOrientSeascape(getVideoChangeOrientation(270, isFrontCamera()) / 90);
    }

    /**
     * 根据Activity取回的界面旋转度数来计算需要转动的度数.
     *
     * @param degree    degree
     * @return
     */
    private int getVideoChangeOrientation(int degree, boolean isfront)
    {
        int resultDegree = 0;
        if (isfront)
        {
            // 注意: 魅族手机与寻常手机不一致.需要分别做判断.正常手机采用下面方法即可
            if (degree == 0)
            {
                resultDegree = 270;
            }
            else if (degree == 90)
            {
                resultDegree = 0;
            }
            else if (degree == 270)
            {
                resultDegree = 180;
            }
        }
        else
        {
            resultDegree = (90 - degree + 360) % 360;
        }
        return resultDegree;
    }

    /**
     * 如果有前置摄像头,就设置前置摄像头;否则设置后置摄像头.
     */
    private void setDefaultCamera()
    {
        if (canSwitchCamera())
        {
            videoConfig.setCameraIndex(FRONT_CAMERA);
        }
        else
        {
            videoConfig.setCameraIndex(BACK_CAMERA);
        }
    }

    private boolean isFrontCamera()
    {
        return videoConfig.getCameraIndex() == FRONT_CAMERA;
    }

    /**
     * 是否能切换摄像头
     *
     * @return boolean
     */
    public boolean canSwitchCamera()
    {
        LogUtil.d("VideoControl", "canSwitchCamera : " + (numberOfCameras > 1));
        return numberOfCameras > 1;
    }

    /**
     * deploySessionVideoCaps
     */
    public void deploySessionVideoCaps()
    {
        Context context = CCAPP.getInstance();
        if (null == localVideo)
        {
            localVideo = ViERenderer.createLocalRenderer(context);
            localVideo.setZOrderMediaOverlay(true);
        }
        //本地视屏回放
        videoConfig.setPlaybackLocal(ViERenderer.getIndexOfSurface(localVideo));
        if (null == remoteVideo)
        {
            remoteVideo = ViERenderer.createRenderer(context, false);
            remoteVideo.setZOrderOnTop(false);
        }
        //远端视频回放
        videoConfig.setPlaybackRemote(ViERenderer.getIndexOfSurface(remoteVideo));

        CallManager tempCallMan = CallManager.getInstance();
        String sessionId = videoConfig.getSessionId();

        configVideoCaps();

        if (null != tempCallMan)
        {
            tempCallMan.setVideoIndex(videoConfig.getCameraIndex());

            //通过videoWindowAction方法去updateVideoWindow
            // 设置视频窗口与呼叫绑定
            tempCallMan.videoWindowAction(VideoWndType.remote, videoConfig.getPlaybackRemote(), sessionId, 0);
        }

        deployGlobalVideoCaps();
    }

    /**
     * deployGlobalVideoCaps
     */
    public void deployGlobalVideoCaps()
    {
        CallManager manager = CallManager.getInstance();
        manager.setOrientParams(videoConfig);
        Configuration configuration = CCAPP.getInstance().getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT
                && videoConfig.getCameraIndex() == BACK_CAMERA)
        {
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    LogUtil.d("VideoControl", " 90");
                    MobileCC.getInstance().setVideoRotate(videoConfig.getCameraIndex(), 90);
                }
            }, 600);
        }
    }

    /**
     * 初始化SurfaceView (localView)
     *
     * @return SurfaceView
     */
    public SurfaceView getLocalVideoView()
    {
        if (null != localVideo)
        {
            Configuration configuration = CCAPP.getInstance().getResources().getConfiguration();
            if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT
                    && videoConfig.getCameraIndex() == FRONT_CAMERA)
            {
                MobileCC.getInstance().setVideoRotate(videoConfig.getCameraIndex(), 270);
            }
            return localVideo;
        }
        return null;
    }

    public SurfaceView getLocalVideo() {
        return localVideo;
    }

    /**
     * 初始化SurfaceView (remoteView)
     *
     * @return SurfaceView
     */
    public SurfaceView getRemoteVideoView()
    {
        if (null != remoteVideo)
        {
            return remoteVideo;
        }
        return null;
    }

    public SurfaceView getLocalSurfaceView() {
        return localSurfaceView;
    }

    /**
     * 设置摄像头默认状态，释放占用资源
     */
    public void clearSurfaceView()
    {
        setDefaultCamera();
        videoConfig.setPlaybackLocal(-1);
        videoConfig.setPlaybackRemote(-1);
        videoConfig.setSessionId(null);

        // 摄像头前后切换后，缓存的信息需要进行复位操作(3,0,2),默认为竖屏，
        videoConfig.setOrientPortrait(getVideoChangeOrientation(0, isFrontCamera()) / 90);
        videoConfig.setOrientLandscape(getVideoChangeOrientation(90, isFrontCamera()) / 90);
        videoConfig.setOrientSeascape(getVideoChangeOrientation(270, isFrontCamera()) / 90);

        if (null != localVideo && null != mLocalContainer)
        {
            ViERenderer.setSurfaceNull(localVideo);
            mLocalContainer.removeAllViews();
            localVideo = null;
            mLocalContainer = null;
        }

        if (null != remoteVideo && null != mRemoteContainer)
        {
            ViERenderer.setSurfaceNull(remoteVideo);
            mRemoteContainer.removeAllViews();
            remoteVideo = null;
            mRemoteContainer = null;
        }
    }

    /**
     * 设置视频视图SurfaceView的装载容器ViewGroup
     *
     * @param context    上下文
     * @param localView  显示本地视频的ViewGroup
     * @param remoteView 显示远端视频的ViewGroup
     */
    public void setVideoContainer(Context context, ViewGroup localView, ViewGroup remoteView)
    {
        if (null == mLocalContainer)
        {
            mLocalContainer = localView;
        }
        localSurfaceView = getLocalVideoView();


        if (null == mRemoteContainer)
        {
            mRemoteContainer = remoteView;
        }

        if (null != localVideo  && null != mLocalContainer)
        {
            //add surfaceView to container, need to remove before adding
            mLocalContainer.removeView(localVideo);
            mLocalContainer.addView(localVideo);
        }

        if (null != remoteVideo && null != mRemoteContainer)
        {
            //add surfaceView to container, need to remove before adding
            mRemoteContainer.removeView(remoteVideo);
            mRemoteContainer.addView(remoteVideo);
        }
    }

}
