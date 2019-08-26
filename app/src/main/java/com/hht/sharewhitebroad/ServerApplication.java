package com.hht.sharewhitebroad;

import android.app.Application;
import android.os.Environment;

import com.zhengsr.drawlib.DrawConfig;

/**
 * created by zhengshaorui on 2019/7/18
 * Describe:
 */
public class ServerApplication extends Application {
    public static int PORT = 9090;
    public static int BROADCAST_PORT = 8989;
    public static int TCP_PORT = 50000;
    public static String CLOSE_MSG = "CLOSED";
    public static String BROADCAST_IP = "255.255.255.255";
    @Override
    public void onCreate() {
        super.onCreate();
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        String name = "cachedata.txt";

        DrawConfig.init(this);

    }
}
