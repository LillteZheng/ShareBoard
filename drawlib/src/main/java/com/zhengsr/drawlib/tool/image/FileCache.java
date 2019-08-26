package com.zhengsr.drawlib.tool.image;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.util.LruCache;


import com.jakewharton.disklrucache.DiskLruCache;
import com.zhengsr.commonlib.LggUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * created by zhengsr on 2019/8/4
 * Describe: disklrucache 的类
 */
public class FileCache {

    private DiskLruCache mDiskLruCache;
    private ByteBuffer mByteBuffer;

    public FileCache(Context context){
        try {
            File file = getCacheFile(context,context.getPackageName());
            //不存在则先创建
            if (!file.exists()){
                file.mkdir();
            }
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),0);
            //创建disklrucache，大小为10m
            mDiskLruCache = DiskLruCache.open(file,info.versionCode,1,10*1024*1024);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 拿到缓存地址
     * @param context
     * @param name
     * @return
     */
    private File getCacheFile(Context context,String name){
        String path;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            path = context.getExternalCacheDir().getAbsolutePath();
        }else{
            path = context.getCacheDir().getAbsolutePath();
        }
        return new File(path,name);
    }

    /**
     * 可能传入的字符存在特殊字符，这里全部转换成md5
     * @param key
     * @return
     */
    public String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(key.getBytes());
            cacheKey = bytesToHexString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    /**
     * 将byte数组转换成十六进制字符串
     * @param bytes
     * @return
     */
    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * 存路径到文件夹
     * @param path
     */
    public void addFileToFile(String path,Bitmap bitmap){
        OutputStream os = null;
        BufferedOutputStream bos = null;
        DiskLruCache.Editor editor =null;
        try {
            String key = hashKeyForDisk(path);
            editor = mDiskLruCache.edit(key);
            if (editor != null){
                os = editor.newOutputStream(0);
                bos = new BufferedOutputStream(os);

                byte[] bitmapByte = getBitmapByte(bitmap);
                bos.write(bitmapByte);
                bos.flush();
                editor.commit();
            }
        } catch (IOException e) {
            if (editor != null) {
                try {
                    editor.abort();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            e.printStackTrace();

        }finally {
            if (os != null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bos != null){
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * bitmap转换成 byte数组
     * @param bmp
     * @return
     */
    private byte[] getBitmapByte(Bitmap bmp){
       mByteBuffer = ByteBuffer.allocate(bmp.getByteCount());
       bmp.copyPixelsFromBuffer(mByteBuffer);
       return mByteBuffer.array();

    }

    /**
     * 拿到文件中的图片
     * @param path
     * @return
     */
    public Bitmap getBitmapFromFile(String path){
        try {
            String key = hashKeyForDisk(path);
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
            if (snapshot != null) {
                InputStream is = snapshot.getInputStream(0);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                return bitmap;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 返回缓冲大小，byte单位
     * @return
     */
    public long getFileCacheSize(){
        return mDiskLruCache.size();
    }

    /**
     * 删除某个文件
     * @param key
     */
    public void removeFileByKey(String key){
        try {
            String cacheKey = hashKeyForDisk(key);
            mDiskLruCache.remove(cacheKey);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除缓冲区的所有文件
     */
    public void deleteFileCache(){
        try {
            mDiskLruCache.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将内存中的记录，同步到日志中，不要频繁调用
     */
    public void flushFileCache(){
        try {
            mDiskLruCache.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

