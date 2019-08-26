package com.zhengsr.drawlib.bean;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;

/**
 * created by zhengshaorui on 2019/8/6
 * Describe: 素材的一些参数，用来保存到bitmap中
 */
public class GesImgBean {
    public int index;
    public int pageId;
    public boolean visiable;
    public Bitmap bitmap;
    public Matrix matrix;
    public boolean isManual = false;

    @Override
    public String toString() {
        return "GesImgBean{" +
                "pageId=" + pageId +
                ", visiable=" + visiable +
                ", bitmap=" + bitmap +
                ", matrix=" + matrix +
                ", isManual=" + isManual +
                '}';
    }
}
