package com.zhengsr.drawlib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.TextUtils;
import android.widget.FrameLayout;

import com.zhengsr.commonlib.LggUtils;
import com.zhengsr.drawlib.bean.GesImgBean;
import com.zhengsr.drawlib.utils.BgUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * created by zhengshaorui on 2019/7/11
 * Describe: 配置画板基本属性
 */
public class DrawConfig {
    //最大10页
    public static int MAX_PAGE = 10;
    //画笔默认红河
    public static int PEN_COLOR = Color.WHITE;
    //画笔默认大小
    public static int PEN_SMALL_SIZE ;
    public static int PEN_MID_SIZE ;
    public static int PEN_LARGE_SIZE ;

    //橡皮擦默认大小
    public static int ERASE_SMALL;
    public static int ERASE_MIDDLE;
    public static int ERASE_LARGE;

    public static int PEN_DEFAULT_SIZE;

    //荧光笔默认透明度
    public static int LIGHT_ALPHA = 150;

    private static Context mContext;

    /**
     * 需要传入的是 applicationcontext
     * @param context
     */
    public static void init(Context context){
        mContext = context;
        /**
         * 画笔大小
         */
        PEN_SMALL_SIZE = px2dp(2);
        PEN_MID_SIZE= px2dp(4);
        PEN_LARGE_SIZE = px2dp(6.67f);
        PEN_DEFAULT_SIZE = PEN_SMALL_SIZE;

        /**
         * 橡皮擦大小
         */

        ERASE_SMALL = px2dp(33.33f);
        ERASE_MIDDLE = px2dp(50);
        ERASE_LARGE = px2dp(66.67f);

    }

    public static Context context(){
        return mContext;
    }

    public static  int px2dp(float px){
        return (int) (mContext.getResources().getDimensionPixelSize(R.dimen.base_1dp) * px);
    }


    private static Builder mBuilder;



    public static Builder getParams() {
        if (mBuilder == null){
            mBuilder = new Builder();
        }
        return mBuilder;
    }

    public static class Builder{
        private int eraseSize;
        private int penSize;
        private int penColor;
        private int penType;
        private FrameLayout backview;
        private int curPageId = 0;
        private LinkedHashMap<Integer,Bitmap> bgMap = new LinkedHashMap<>();

        public Builder setBg(Bitmap bitmap) {
            bgMap.put(curPageId,bitmap);
            return this;
        }
        public Builder setBg(String path) {
            bgMap.put(curPageId,BitmapFactory.decodeFile(path));
            return this;
        }

        public Builder setBg(int position) {
            bgMap.put(curPageId,BgUtils.getChooseBitmap(mContext,position,1920,1080));
            return this;
        }

        public Builder curPageId(int curPageId) {
            this.curPageId = curPageId;
            return this;
        }

        public Builder eraseSize(int eraseSize) {
            this.eraseSize = eraseSize;
            return this;
        }

        public Builder penSize(int penSize) {
            this.penSize = penSize;
            return this;
        }

        public Builder penColor(int penColor) {
            this.penColor = penColor;
            return this;
        }

        public Builder penType(int penType) {
            this.penType = penType;
            return this;
        }




        public Builder backview(FrameLayout backview) {
            this.backview = backview;
            return this;
        }


        public Bitmap getBg() {
            Bitmap bitmap = bgMap.get(curPageId);
            if (bitmap != null){
                return bitmap;
            }
            return bgMap.get(0);
        }
        public Bitmap getBg(int pageid) {
            return bgMap.get(pageid);
        }

        /**
         * context
         */


        public int getEraseSize() {
            return eraseSize;
        }

        public int getPenSize() {
            return penSize;
        }

        public int getPenColor() {
            return penColor;
        }

        public int getPenType() {
            return penType;
        }

        public int getCurPageId() {
            return curPageId;
        }

        public FrameLayout getBackview() {
            return backview;
        }
    }



}
