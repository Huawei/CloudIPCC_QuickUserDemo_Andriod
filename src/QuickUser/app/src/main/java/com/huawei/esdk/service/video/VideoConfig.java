package com.huawei.esdk.service.video;

/**
 * VideoConfig
 */
public class VideoConfig
{
    private static final int INVALID_VALUE = -1;

    /**
     * 可选,会话id。
     * 针对指定会话时需要这个参数
     * 公共参数则不需要这个参数。
     */
    private String sessionId = null;


    /**
     * 可选,本地视频回放是否启动.
     * <p/>
     * 0: 不启动;
     * 1: 启动. 默认1
     */
    private int playbackLocal = INVALID_VALUE;


    /**
     * 可选,远端视频回放是否启动.
     * <p/>
     * 0: 不启动;
     * 1: 启动. 默认1
     */
    private int playbackRemote = INVALID_VALUE;


    /**
     * 可选,视频编解码名称。
     */
    private String name = "H264";

    private OrientParams orientParams = null;

    /**
     * 构造
     */
    public VideoConfig()
    {
        orientParams = new OrientParams();
    }

    public String getSessionId()
    {
        return sessionId;
    }

    public void setSessionId(String sessionId)
    {
        this.sessionId = sessionId;
    }

    public int getCameraIndex()
    {
        return orientParams.cameraIndex;
    }

    public void setCameraIndex(int cameraIndex)
    {
        orientParams.cameraIndex = cameraIndex;
    }

    public void setPlaybackLocal(int playbackLocal)
    {
        this.playbackLocal = playbackLocal;
    }

    public int getPlaybackLocal() {
        return playbackLocal;
    }

    public int getPlaybackRemote()
    {
        return playbackRemote;
    }

    public void setPlaybackRemote(int playbackRemote)
    {
        this.playbackRemote = playbackRemote;
    }


    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setOrient(int orient)
    {
        orientParams.orient = orient;
    }

    public void setOrientPortrait(int orientPortrait)
    {
        orientParams.orientPortrait = orientPortrait;
    }

    public void setOrientLandscape(int orientLandscape)
    {
        orientParams.orientLandscape = orientLandscape;
    }

    public void setOrientSeascape(int orientSeascape)
    {
        orientParams.orientSeascape = orientSeascape;
    }

    public OrientParams getOrientParams()
    {
        return orientParams;
    }

    /**
     * OrientParams
     */
    public static class OrientParams
    {
        /**
         * 可选,摄像头索引值,对手机客户端只存在 {0, 1}
         */
        private int cameraIndex = 1;

        /**
         * 可选，设置视频捕获（逆时针旋转）的角度。
         * 仅Android/iOS平台有效。
         * 0：0度；1：90度；2：180度；3：270度；
         * {0,1,2,3}
         */

        /**
         * 可选，视频横竖屏情况，仅对对移动平台有效{1,2,3}
         * <p/>
         * 1：竖屏；2：横屏；3：反向横屏
         */
        private int orient = INVALID_VALUE;

        /**
         * 可选，竖屏视频捕获（逆时针旋转）角度。
         * 仅对移动平台有效。
         * <p/>
         * 0：0度；1：90度；2：180度；3：270度；
         */
        private int orientPortrait = 0;

        /**
         * 可选，横屏视频捕获（逆时针旋转）角度。
         * 仅对移动平台有效。
         * <p/>
         * 0：0度；1：90度；2：180度；3：270度；
         */
        private int orientLandscape = 0;

        /**
         * 可选，反向横屏视频捕获（逆时针旋转）角度。
         * 仅对移动平台有效。
         * <p/>
         * 0：0度；1：90度；2：180度；3：270度；
         */
        private int orientSeascape = 0;

        public int getCameraIndex()
        {
            return cameraIndex;
        }

        public void setCameraIndex(int cameraIndex)
        {
            this.cameraIndex = cameraIndex;
        }

        public int getOrient()
        {
            return orient;
        }

        public void setOrient(int orient)
        {
            this.orient = orient;
        }

        public int getOrientPortrait()
        {
            return orientPortrait;
        }

        public void setOrientPortrait(int orientPortrait)
        {
            this.orientPortrait = orientPortrait;
        }

        public int getOrientLandscape()
        {
            return orientLandscape;
        }

        public void setOrientLandscape(int orientLandscape)
        {
            this.orientLandscape = orientLandscape;
        }

        public int getOrientSeascape()
        {
            return orientSeascape;
        }

        public void setOrientSeascape(int orientSeascape)
        {
            this.orientSeascape = orientSeascape;
        }
    }
}
