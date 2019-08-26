package com.zhengsr.drawlib.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * created by @author zhengshaorui on 2019/8/26
 * Describe: 获取ip
 */
public class IpUtils {
    /**
     * 获取本地ip
     * @param context
     * @return
     */
    public static String getIp(Context context){

        NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()){
            //当前使用2G/3G/4G网络
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                return getMobileIp();
            }else if (info.getType() == ConnectivityManager.TYPE_WIFI){
                return getLocalIp(true);
            }else{
                return getLocalIp(false);
            }

        }

        return null;
    }

    /**
     * 得到有限网关的IP地址
     *
     * @return
     */
    private static String getLocalIp(boolean isWifi) {

        try {
            // 获取本地设备的所有网络接口
            Enumeration<NetworkInterface> enumerationNi = NetworkInterface
                    .getNetworkInterfaces();
            while (enumerationNi.hasMoreElements()) {
                NetworkInterface networkInterface = enumerationNi.nextElement();
                String interfaceName = networkInterface.getDisplayName();
                Log.i("tag", "zsr 网络名字" + interfaceName);

                String ipType ;
                if (isWifi){
                    ipType = "wlan0";
                }else{
                    ipType = "eth0";
                }
                if (interfaceName.equals(ipType)) {
                    Enumeration<InetAddress> enumIpAddr = networkInterface
                            .getInetAddresses();

                    while (enumIpAddr.hasMoreElements()) {
                        // 返回枚举集合中的下一个IP地址信息
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        // 不是回环地址，并且是ipv4的地址
                        if (!inetAddress.isLoopbackAddress()
                                && inetAddress instanceof Inet4Address) {
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";

    }

    /**
     * 获取手机ip
     * @return
     */
    private static String getMobileIp(){
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

}
