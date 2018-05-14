/**
 * Copyright 2015 Huawei Technologies Co., Ltd. All rights reserved.
 * eSDK is licensed under the Apache License, Version 2.0 ^(the "License"^);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.huawei.esdk.service.video;

/**
 * VideoParams
 */
public class VideoParams
{
    /**
     * 视频x分辨率（宽）
     **/
    private int xRes;

    /**
     * 视频y分辨率（高）
     **/
    private int yRes;

    /**
     * 视频帧率
     **/
    private int nFrame;

    /**
     * 比特率
     **/
    private int nBitRate;

    /**
     * 视频格式
     **/
    private int nRawtype;

    /**
     * 带宽
     **/
    private int nBandwidth;

    /**
     * VideoParams
     */
    public VideoParams()
    {
    }

    /**
     * VideoParams
     *
     * @param xRes   xRes
     * @param yRes   yRes
     * @param nFrame nFrame
     */
    public VideoParams(int xRes, int yRes, int nFrame)
    {
        this.xRes = xRes;
        this.yRes = yRes;
        this.nFrame = nFrame;
    }

    /**
     * getxRes
     *
     * @return int
     */
    public int getxRes()
    {
        return xRes;
    }

    /**
     * setxRes
     *
     * @param xRes xRes
     */
    public void setxRes(int xRes)
    {
        this.xRes = xRes;
    }

    /**
     * @return int
     */
    public int getyRes()
    {
        return yRes;
    }

    /**
     * @param yRes yRes
     */
    public void setyRes(int yRes)
    {
        this.yRes = yRes;
    }

    /**
     * @return int
     */
    public int getnFrame()
    {
        return nFrame;
    }

    /**
     * @param nFrame nFrame
     */
    public void setnFrame(int nFrame)
    {
        this.nFrame = nFrame;
    }

    /**
     * @return int
     */
    public int getnBitRate()
    {
        return nBitRate;
    }

    /**
     * @param nBitRate nBitRate
     */
    public void setnBitRate(int nBitRate)
    {
        this.nBitRate = nBitRate;
    }

    /**
     * @return int
     */
    public int getnRawtype()
    {
        return nRawtype;
    }

    /**
     * @param nRawtype nRawtype
     */
    public void setnRawtype(int nRawtype)
    {
        this.nRawtype = nRawtype;
    }

    /**
     * @return int
     */
    public int getnBandwidth()
    {
        return nBandwidth;
    }

    /**
     * setnBandwidth
     *
     * @param nBandwidth nBandwidth
     */
    public void setnBandwidth(int nBandwidth)
    {
        this.nBandwidth = nBandwidth;
    }

    @Override
    public String toString()
    {
        return xRes + "*" + yRes;
    }

}
