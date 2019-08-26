package com.hht.client;

import android.app.Application;

import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import com.zhengsr.drawlib.DrawConfig;

/**
 * created by zhengshaorui on 2019/7/18
 * Describe:
 */
public class ClientApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();


        ZXingLibrary.initDisplayOpinion(this);
        DrawConfig.init(this);
    }
}
