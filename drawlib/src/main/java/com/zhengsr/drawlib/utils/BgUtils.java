package com.zhengsr.drawlib.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;

import com.zhengsr.drawlib.R;

/**
 * created by zhengshaorui
 * time on 2019/7/11 
 */
public class BgUtils {

    /**
     * 方格
     * @param width
     * @param height
     * @return
     */
    private static Bitmap drawGrayLineBg(int width, int height,int color) {
        Bitmap localBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(localBitmap);
        canvas.drawColor(color);
        Paint localPaint = new Paint();
        localPaint.setAntiAlias(true);
        localPaint.setStyle(Paint.Style.STROKE);
        localPaint.setStrokeWidth(2.0F);
        localPaint.setColor(Color.BLACK);
        localPaint.setAlpha(50);
        //缩略图20
        int space = 80;
        if(height < 600){
            space = 10;
        }
        for (int i = 0; i < width / space; i++) {
            canvas.drawLine(i * space, 0, i * space, height, localPaint);
        }
        for (int j = 0; j < height / space + 1; j++) {
            canvas.drawLine(0, j * space, width, j * space, localPaint);
        }
        return localBitmap;
    }


    /**
     * 1.横线背景
     */
    private static Bitmap drawHorizontalLine(int width, int height, int bgColor, int lineColor) {
        Bitmap localBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas localCanvas = new Canvas(localBitmap);
        Paint localPaint = new Paint();
        localPaint.setAntiAlias(true);
        localPaint.setStyle(Paint.Style.STROKE);
        localPaint.setStrokeWidth(2.0F);
        localPaint.setColor(lineColor);
        localCanvas.drawColor(bgColor);
        localPaint.setAlpha(100);
        //space 缩略图间隔20 背景图间隔80
        int space = 80;
        if (height < 600) {
            space = 8;
        }
        for (int j = 0; j < height / space; j++) {
            localCanvas.drawLine(0, j * space, width, j * space, localPaint);
        }
        return localBitmap;
    }

    /**
     * 2.五线谱拼音格背景
     */
    private static Bitmap drawEnglishGrid(int width, int height, int bgColor,int redColor,int lineColor) {
        Bitmap localBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas localCanvas = new Canvas(localBitmap);
        Paint localPaint = new Paint();
        localPaint.setAntiAlias(true);
        localPaint.setStyle(Paint.Style.STROKE);
        localPaint.setStrokeWidth(1.0F);
        localCanvas.drawColor(bgColor);
        //背景图scale = 1；
        int s160 = 160;
        int s26 = 26;
        int s53 = 53;
        int s80 = 80;
        int s90 = 105;
        if (height < 600) {
            s160 = 160 / 8;
            s26 = 26 / 4;
            s53 = 53 / 4;
            s80 = 80 / 4;
            s90 = s90 / 4;
        }

        for (int i = 0; ; i++) {
            if (i >= 1 + height / s160) {
                return localBitmap;
            }
            //localPaint.setAlpha(100);
            localCanvas.drawLine(0.0F, i * s160, width, i * s160, localPaint);
            //localPaint.setAlpha(100);
            localPaint.setColor(redColor);
            localCanvas.drawLine(0.0F, s26 + i * s160, width, s26 + i * s160, localPaint);

            //localPaint.setAlpha(100);
            localCanvas.drawLine(0.0F, s53 + i * s160, width, s53 + i * s160, localPaint);

            //localPaint.setAlpha(100);
            localPaint.setColor(lineColor);
            localCanvas.drawLine(0.0F, s80 + i * s160, width, s80 + i * s160, localPaint);
            localCanvas.drawLine(0.0F, s90 + i * s160, width, s90 + i * s160, localPaint);
        }
    }

    /**
     * 4.竖线背景
     */
    private static Bitmap drawVerticalLine(int width, int height,int bgColor,int lineColor) {
        Bitmap localBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas localCanvas = new Canvas(localBitmap);
        Paint localPaint = new Paint();
        localPaint.setAntiAlias(true);
        localPaint.setStyle(Paint.Style.STROKE);
        localPaint.setStrokeWidth(2.0F);
        localPaint.setColor(lineColor);
        localCanvas.drawColor(bgColor);
        localPaint.setAlpha(100);
        //缩略图20 背景图80
        int space = 80;
        if (height < 600) {
            space = 12;
        }
        for (int i = 0; i < width / space; i++) {
            localCanvas.drawLine(i * space, 0, i * space, height, localPaint);
        }
        return localBitmap;
    }

    /**
     * 5.大方格
     */
    private static Bitmap drawBigGrid(int width, int height,int bgColor,int lineColor) {
        Bitmap localBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas localCanvas = new Canvas(localBitmap);
        Paint localPaint = new Paint();
        localPaint.setAntiAlias(true);
        localPaint.setStyle(Paint.Style.STROKE);
        localPaint.setStrokeWidth(2.0F);
        localPaint.setColor(lineColor);
        localCanvas.drawColor(bgColor);
        localPaint.setAlpha(100);
        //缩略图30 背景图120
        int space = 120;
        if (height < 600) {
            space = 16;
        }
        for (int i = 0; i < width / space; i++) {
            localCanvas.drawLine(i * space, 0, i * space, height, localPaint);
        }
        for (int j = 0; j < height / space; j++) {
            localCanvas.drawLine(0, j * space, width, j * space, localPaint);
        }
        return localBitmap;
    }

    /**
     * 默认颜色
     * @param width
     * @param height
     * @return
     */
    private static Bitmap drawDefualtBg(int width, int height,int color) {
        Bitmap localBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(localBitmap);
        canvas.drawColor(color);
        return localBitmap;
    }


    /**
     * 通过颜色，设置一个drawable
     * @param color
     * @param roundsize
     * @return
     */
    public static GradientDrawable drawRoundBg(int color, int roundsize) {
        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(roundsize);
        gd.setColor(color);
        return gd;
    }


    /**
     * 拿到color的bitmap
     * @param width
     * @param height
     * @return
     */
    public static Bitmap drawColorBitmap(int width, int height,int color) {
        Bitmap localBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(localBitmap);
        canvas.drawColor(color);
        return localBitmap;
    }


    /**
     * 画更多
     * @param width
     * @param height
     * @return
     */
    private static Bitmap drawMoreBitmap(int width, int height,int bgcolor) {
        Bitmap localBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(localBitmap);
        canvas.drawColor(bgcolor);
        int radius = 5;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(width/4,height/2,radius,paint);
        canvas.drawCircle(width/2,height/2,radius,paint);
        canvas.drawCircle(width*3/4,height/2,radius,paint);
        return localBitmap;
    }

    public static Bitmap getChooseBitmap(Context context,int position, int width, int height){
        Bitmap bitmap = null;
        switch (position){
            case 0:
                bitmap = drawDefualtBg(width,height,getColor(context,R.color.draw_default));
                break;
            case 1:
                bitmap = drawColorBitmap(width,height,getColor(context,R.color.draw_blue_color));
                break;
            case 2:
                bitmap = drawColorBitmap(width,height,getColor(context,R.color.draw_white_color));
                break;
            case 3:
                bitmap = drawGrayLineBg(width,height,getColor(context,R.color.draw_deep_gray));
                break;
            case 4:
                bitmap = drawHorizontalLine(width,height,
                        getColor(context,R.color.draw_line_bg_color),Color.RED);
                break;
            case 5:
                bitmap = drawEnglishGrid(width,height,
                        getColor(context,R.color.draw_line_bg_color),Color.RED,R.color.draw_line_color);
                break;
            case 6:
                bitmap = drawVerticalLine(width,height,
                        getColor(context,R.color.draw_line_bg_color),R.color.draw_line_color);
                break;
            case 7:
                bitmap = drawBigGrid(width,height,
                        getColor(context,R.color.draw_line_bg_color),R.color.draw_line_color);
                break;
            case 8:
                bitmap = drawMoreBitmap(width,height,getColor(context,R.color.draw_default));
                break;
            default:
                bitmap = drawDefualtBg(width,height,context.getResources().getColor(R.color.draw_default));
                break;

        }
        return bitmap;
    }

    public static int getBgLength(){
        return 9;
    }

    private static int getColor(Context context,int color){
        return context.getResources().getColor(color);
    }

}