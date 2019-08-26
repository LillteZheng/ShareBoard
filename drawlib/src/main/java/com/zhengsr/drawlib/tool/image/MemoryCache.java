package com.zhengsr.drawlib.tool.image;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * created by zhengsr on 2019/8/4
 * Describe: LruCache 类
 */
public class MemoryCache {
    private final LruCache<String,Bitmap> mLruCache;


    public static MemoryCache create(){
        return new MemoryCache();
    }

    private MemoryCache(){
        long maxMemory = Runtime.getRuntime().maxMemory();
        //设置缓存为系统内存的1/8
        int cache = (int) (maxMemory/8);
        mLruCache = new LruCache<String,Bitmap>(cache){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //默认返回时value 的个数
                //return super.sizeOf(key, value);
                //返回图片的大小
                return value.getByteCount();
            }
        };
    }

    /**
     * 将一张图片放入缓存中
     * @param key
     * @param bitmap
     */
    public void addBitmapToCache(String key,Bitmap bitmap){
        if (getCacheBitmap(key) == null){
            mLruCache.put(key,bitmap);
        }
    }

    /**
     * 缓存是否有图片
     * @param key
     * @return
     */
    public Bitmap getCacheBitmap(String key){
        return mLruCache.get(key);
    }

}
