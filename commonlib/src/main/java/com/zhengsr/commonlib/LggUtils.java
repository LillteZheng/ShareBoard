package com.zhengsr.commonlib;

import android.util.Log;

public class LggUtils {
    private static String TAG = "ShareWhiteBroad";
    private static boolean ENABLE = true;

    public static void d(String content){
        Log.d(TAG, "zsr "+content);
    }
}
