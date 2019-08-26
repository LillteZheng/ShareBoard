package com.zhengsr.drawlib.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;


import java.util.ArrayList;
import java.util.List;

public class BackgroundUtils {

    private static int lineColor = Color.parseColor("#272727");
    private static int lineColor2 = Color.parseColor("#666666");
    private static int redColor = Color.parseColor("#FF0000");
    private static int bgColor = Color.parseColor("#D3D3D3");

    /**
     * 1.横线背景
     */
    private static Bitmap drawHorizontalLine(int width, int height) {
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
    private static Bitmap drawEnglishGrid(int width, int height, boolean paramBoolean) {
        Bitmap localBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas localCanvas = new Canvas(localBitmap);
        Paint localPaint = new Paint();
        localPaint.setAntiAlias(true);
        localPaint.setStyle(Paint.Style.STROKE);
        localPaint.setStrokeWidth(1.0F);
        localPaint.setColor(lineColor);
        if (paramBoolean) {
            localCanvas.drawColor(bgColor);
        }
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
     * 2.五线谱拼音格背景,同一种颜色
     */
    private static Bitmap drawEnglishSameColorGrid(int width, int height, boolean paramBoolean) {
        Bitmap localBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas localCanvas = new Canvas(localBitmap);
        Paint localPaint = new Paint();
        localPaint.setAntiAlias(true);
        localPaint.setStyle(Paint.Style.STROKE);
        localPaint.setStrokeWidth(1.0F);
        localPaint.setColor(lineColor);
        if (paramBoolean) {
            localCanvas.drawColor(bgColor);
        }
        //背景图scale = 1；
        int spaceWidth = 180;
        int offsert = 35;
        int s1 = 120;
        int s2 = (int) (s1 + 6.5f * offsert);
        int s3 = (int) (s2 + 6.5f * offsert);
        int s4 = (int) (s3 + 6.5f * offsert);
        if (height < 600) {
            spaceWidth = spaceWidth / 10;
            s1 = s1 / 5;
            s2 = s2 / 5;
            s3 = s3 / 5;
            s4 = s4 / 5;
        }

        for (int i = 0; i < 5; i++) {
            localCanvas.drawLine(0.0F, s1 + i * offsert, width, s1 + i * offsert, localPaint);
            localCanvas.drawLine(0.0F, s2 + i * offsert, width, s2 + i * offsert, localPaint);
            localCanvas.drawLine(0.0F, s3 + i * offsert, width, s3 + i * offsert, localPaint);
            localCanvas.drawLine(0.0F, s4 + i * offsert, width, s4 + i * offsert, localPaint);
        }

        return localBitmap;
    }

    /**
     * 3.小方格背景
     */
    private static Bitmap drawSmallGrid(int width, int height) {
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
        for (int j = 0; j < height / space + 1; j++) {
            localCanvas.drawLine(0, j * space, width, j * space, localPaint);
        }
        return localBitmap;
    }

    /**
     * 4.竖线背景
     */
    private static Bitmap drawVerticalLine(int width, int height) {
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
    private static Bitmap drawBigGrid(int width, int height) {
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

    /******************************* 画背景 ***************************************************/

    private static Bitmap drawBlueBg(int width, int height) {
        Bitmap localBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(localBitmap);
        canvas.drawColor(Color.parseColor("#2D3d7F"));
        return localBitmap;
    }

    private static Bitmap drawGreenBg(int width, int height) {
        Bitmap localBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(localBitmap);
        //绿色无网格
        canvas.drawColor(Color.parseColor("#416938"));
        return localBitmap;
    }

    private static Bitmap drawWhiteffBg(int width, int height) {
        Bitmap localBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(localBitmap);
        canvas.drawColor(Color.parseColor("#ADADAD"));
        return localBitmap;
    }

    private static Bitmap drawWhiteBg(int width, int height) {
        Bitmap localBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(localBitmap);
        canvas.drawColor(Color.parseColor("#FFFFFF"));
        return localBitmap;
    }


    private static Bitmap drawGreenGeBg(int width, int height) {
        Bitmap localBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(localBitmap);
        canvas.drawColor(Color.parseColor("#416938"));
        Paint localPaint = new Paint();
        localPaint.setAntiAlias(true);
        localPaint.setStyle(Paint.Style.STROKE);
        localPaint.setStrokeWidth(2.0F);
        localPaint.setColor(lineColor2);
        localPaint.setAlpha(50);
        //缩略图20 背景图80
        int space = 80;
        if (height < 600) {
            space = 10;
        }
        for (int i = 0; i < width / space; i++) {
            canvas.drawLine(i * space, 0, i * space, height, localPaint);
        }
        for (int j = 0; j < height / space; j++) {
            canvas.drawLine(0, j * space, width, j * space, localPaint);
        }
        return localBitmap;
    }

    /**
     * add by zsr 2017/12/21
     */
    private static Bitmap drawGrayLineBg(int width, int height) {
        Bitmap localBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(localBitmap);
        canvas.drawColor(Color.parseColor("#333b3e"));
        Paint localPaint = new Paint();
        localPaint.setAntiAlias(true);
        localPaint.setStyle(Paint.Style.STROKE);
        localPaint.setStrokeWidth(2.0F);
        localPaint.setColor(Color.BLACK);
        localPaint.setAlpha(50);
        //缩略图20
        int space = 80;
        if (height < 600) {
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

    private static Bitmap drawGrayBg(int width, int height) {
        Bitmap localBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(localBitmap);
        canvas.drawColor(Color.parseColor("#5C5C5C"));
        return localBitmap;
    }

    private static Bitmap drawBlackBg(int width, int height) {
        Bitmap localBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(localBitmap);
        canvas.drawColor(Color.BLACK);
        return localBitmap;
    }

    public static List<Bitmap> getBackgroundBitmaps(int width, int height) {
        List<Bitmap> bitmaps = new ArrayList<>();
        bitmaps.add(drawWhiteBg(width, height));
        bitmaps.add(drawWhiteffBg(width, height));
        //bitmaps.add(drawGrayBg(width,height));
        bitmaps.add(drawGrayLineBg(width, height));
        bitmaps.add(drawBlackBg(width, height));
        bitmaps.add(drawBlueBg(width, height));
        bitmaps.add(drawGreenBg(width, height));

        bitmaps.add(drawHorizontalLine(width, height));
        bitmaps.add(drawEnglishGrid(width, height, true));
        bitmaps.add(drawSmallGrid(width, height));
        bitmaps.add(drawVerticalLine(width, height));
        bitmaps.add(drawBigGrid(width, height));
        return bitmaps;
    }

    public static Bitmap getBigBitmap(int postion, int width, int height) {
        Bitmap bitmap = null;
        switch (postion) {
            case 0:
                bitmap = drawWhiteBg(width, height);
                break;
            case 1:
                bitmap = drawWhiteffBg(width, height);
                break;
            case 2:
                bitmap = drawGrayLineBg(width, height);
                break;
            case 3:
                bitmap = drawBlackBg(width, height);
                break;
            case 4:
                bitmap = drawBlueBg(width, height);
                break;
            case 5:
                bitmap = drawGreenBg(width, height);
                break;
            case 6:
                bitmap = drawHorizontalLine(width, height);
                break;
            case 7:
               /* if ("IST_1302".equals(InvokeManager.context("customer.prdID","IST_1101"))){
                    bitmap = drawEnglishSameColorGrid(width,height,true);
                }else{
                    bitmap = drawEnglishGrid(width, height, true);
                }*/

                break;
            case 8:
                bitmap = drawSmallGrid(width, height);
                break;
            case 9:
                bitmap = drawVerticalLine(width, height);
                break;
            case 10:
                bitmap = drawBigGrid(width, height);
                break;
        }
        return bitmap;
    }
}
