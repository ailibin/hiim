package com.aiitec.openapi.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络状态监测工具类
 * @author Anthony
 * @version 1.0
 * createTime 2017/10/12.
 */
public class NetUtils {

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }


    /**
     * 判断WIFI网络是否可用
     * @param context 上下文
     * @return Wifi网络是否可用
     */
    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }


    /**
     * 判断移动网络是否可用
     * @param context 上下文
     * @return 移动网络是否可用
     */
    public static boolean isMobileConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mMobileNetworkInfo != null) {
                return mMobileNetworkInfo.isAvailable();
            }
        }
        return false;
    }



    /**
     * 判断网络类型
     * @param context
     * @return -1：没有网络  1：WIFI网络2：wap网络3：net网络
     */
    public static int getNetype(Context context)
    {
        int netType = -1;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo==null)
        {
            return netType;
        }
        int nType = networkInfo.getType();
        if(nType==ConnectivityManager.TYPE_MOBILE)
        {
            if("cmnet".equals(networkInfo.getExtraInfo().toLowerCase()))
            {
                netType = 3;
            }
            else
            {
                netType = 2;
            }
        }
        else if(nType==ConnectivityManager.TYPE_WIFI)
        {
            netType = 1;
        }
        return netType;
    }
}
