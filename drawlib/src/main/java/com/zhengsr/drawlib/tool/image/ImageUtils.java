package com.zhengsr.drawlib.tool.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.widget.ImageView;

/**
 * created by zhengshaorui on 2019/8/5
 * Describe:
 */
public class ImageUtils {
    public static ImageSize getImageSize(ImageView imageView) {
        ImageSize  size = new ImageSize();
        DisplayMetrics metrics = imageView.getResources().getDisplayMetrics();
        int width = imageView.getWidth();
        if (width <= 0){
            width = imageView.getLayoutParams().width;
        }
        if (width <= 0){
            width = imageView.getMaxWidth();
        }
        if (width <= 0){
            width = metrics.widthPixels;
        }

        int height = imageView.getHeight();
        if (height <= 0){
            height = imageView.getLayoutParams().height;
        }
        if (height <= 0){
            height = imageView.getMaxHeight();
        }
        if (height <= 0){
            height = metrics.heightPixels;
        }

        size.width = width;
        size.height = height;
        return size;
    }
    public static  class ImageSize{
        public int width;
        public int height;
    }

    /**
     * 拿到缩放后的图片
     * @param path
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getScaleBitmap(String path, int width, int height){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path,options);
        options.inSampleSize = sampleSize(options,width,height);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path,options);
        return bitmap;
    }

    /**
     * 计算缩放比例
     * @param option
     * @param reWidth
     * @param reHeight
     * @return
     */
    private  static int sampleSize(BitmapFactory.Options option, int reWidth, int reHeight){
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
