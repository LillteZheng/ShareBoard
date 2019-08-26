package com.zhengsr.drawlib.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * created by zhengshaorui on 2019/8/2
 * Describe: 图片压缩等操作
 */
public class BitmapUtils {

    public static Bitmap getScaleBitmap(String path,int width,int height){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path,options);
        options.inSampleSize = sampleSize(options,width,height);
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(path,options);
        return bitmap;
    }

    private static int sampleSize(BitmapFactory.Options option, int reWidth, int reHeight){
        int width = option.outWidth;
        int height = option.outHeight;
        int inSampleSize = 1;
        if (width > reWidth && height > reHeight) {
            int radioWidth = Math.round(width*1.0f/reWidth);
            int radioHeight = Math.round(height*1.0f/reHeight);
            inSampleSize = Math.max(radioWidth, radioHeight);
        }
        return inSampleSize;
    }
}
