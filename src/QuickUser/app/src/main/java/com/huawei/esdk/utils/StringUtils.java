package com.huawei.esdk.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.huawei.esdk.CCAPP;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Pattern;

/**
 * Created on 2015/12/29.
 */
public final class StringUtils
{
    private StringUtils()
    {

    }

    /**
     * @param source source
     * @return boolean
     */
    public static boolean isStringEmpty(String source)
    {
        return null == source || "".equals(source);
    }

    /**
     * @param str str
     * @return int
     */
    public static int stringToInt(String str)
    {
        return stringToInt(str, -1);
    }

    /**
     * @param str          str
     * @param defaultValue defaultValue
     * @return int
     */
    public static int stringToInt(String str, int defaultValue)
    {
        if (isStringEmpty(str))
        {
            return defaultValue;
        }
        try
        {
            return Integer.parseInt(str);
        } catch (NumberFormatException e)
        {
            return defaultValue;
        }
    }


    /**
     * Function: 获取当前的IP地址.
     *
     * @return String
     */
    public static String getIpAddress()
    {
        WifiManager wifiManager = (WifiManager) CCAPP.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (null == wifiManager)
        {
            return "";
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (null == wifiInfo)
        {
            return "";
        }
        int ipAddress = wifiInfo.getIpAddress();
        String ip = intToIp(ipAddress);
        if (0 != ipAddress)
        {
            return ip;
        }
        else
        {
            return "";
        }
    }


    /**
     * 获取本机IP
     *
     * @param i i
     * @return String
     */
    public static String intToIp(int i)
    {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
                + "." + ((i >> 24) & 0xFF);
    }

    /**
     * 判断是否是ipv4地址
     *
     * @param ipAddr ipAddr
     * @return boolean
     */
    public static boolean isIPV4Addr(String ipAddr)
    {
        Pattern p = Pattern
                .compile("^((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\.){3}"
                        + "(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])$");
        return p.matcher(ipAddr).matches();
    }


    /**
     * 判断string是否为空
     *
     * @param str
     * @return boolean
     * */
    public static boolean isEmpty(String str)
    {
        if (null == str || "" .equals(str) || "".equals(str.trim()))
        {
            return true;
        }
        return false;
    }

}
