package com.zhengsr.drawlib.tool.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.zhengsr.commonlib.LggUtils;

/**
 * created by zhengsr on 2019/8/4
 * Describe:
 */
public class ImageRequest {
    private ImageBean mImageBean;
    private MemoryCache mMemoryCache;
    private FileCache mFileCache;

    private ImageRequest(){}
    private static class Holder{
        static ImageRequest HODLER = new ImageRequest();
    }
    public static ImageRequest getInstance(){
        return Holder.HODLER;
    }

    public ImageRequest with(Context context){
        mImageBean = new ImageBean();
        if (mMemoryCache == null) {
            mMemoryCache = MemoryCache.create();
        }
        if (mFileCache == null) {
            mFileCache = new FileCache(context);
        }
        return this;
    }

    public ImageRequest path(String path){
        mImageBean.path = path;
        return this;
    }

    public ImageRequest width(int width){
        mImageBean.width = width;
        return this;
    }

    public ImageRequest height(int height){
        mImageBean.height = height;
        return this;
    }
    public ImageRequest into(ImageView imageView){
        Bitmap bitmap = mMemoryCache.getCacheBitmap(mImageBean.path);
        imageView.setTag(mImageBean.path);
        if (bitmap == null){
            ImageSize size = getImageSize(imageView);
            bitmap = getScaleBitmap(mImageBean.path,size.width, size.height);
            if (bitmap != null) {
                mMemoryCache.addBitmapToCache(mImageBean.path, bitmap);
            }
        }

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }
        return this;
    }

    private ImageSize getImageSize(ImageView imageView) {
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


    class ImageSize{
        public int width;
        public int height;

        @Override
        public String toString() {
            return "ImageSize{" +
                    "width=" + width +
                    ", height=" + height +
                    '}';
        }
    }

    /**
     * 拿到缩放后的图片
     * @param path
     * @param width
     * @param height
     * @return
     */
    public Bitmap getScaleBitmap(String path, int width, int height){
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
    private  int sampleSize(BitmapFactory.Options option, int reWidth, int reHeight){
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
