package com.zhengsr.drawlib.utils;

import android.content.Context;
import android.os.Build;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;

import com.zhengsr.drawlib.R;
import com.zhengsr.drawlib.bean.UsbBean;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * created by zhengshaorui on 2019/8/1
 * Describe: 用来获取常用设备，比如内部存储，U盘等信息
 */
public class DiskUtils {
    private static final String TAG = "DiskUtils";
    private DiskUtils(){}
    private static class Holder{
        static DiskUtils HODLER = new DiskUtils();
    }


    public static DiskUtils getInstance(){
        return Holder.HODLER;
    }

    public UsbBean getAllStorageInfo(Context context){
        UsbBean usbBean = new UsbBean();
        usbBean.pathlist = new ArrayList<>();
        usbBean.namelist = new ArrayList<>();

        StorageManager storageManager = (StorageManager) context.getApplicationContext().
                getSystemService(Context.STORAGE_SERVICE);

        boolean isKjadd = false;
        try {
            Method method = storageManager.getClass().getDeclaredMethod("getVolumeList");
            method.setAccessible(true);
            StorageVolume[] volumes = (StorageVolume[]) method.invoke(storageManager);

            //usbBean = new UsbBean();

            usbBean.count = volumes.length ;
            for (StorageVolume volume : volumes) {
                Class<?> volumeClass = Class.forName("android.os.storage.StorageVolume");
                Method getpath = volumeClass.getDeclaredMethod("getPath");
                String path = (String) getpath.invoke(volume);



                String label = null;
                if (path.contains("storage/emulated/0")){
                    label = "storage/emulated/0";
                    usbBean.pathlist.add(path);
                }else {

                    if (Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT < 23) {
                        String[] paths = path.split("/");
                        label = paths[paths.length - 1];
                    } else {
                        Method getuserlabel = volumeClass.getDeclaredMethod("getUserLabel");
                        label = (String) getuserlabel.invoke(volume);
                        // String[] names = label.split("/");
                        String[] names = label.split(" ");
                        label = names[0];
                    }
                    usbBean.pathlist.add(path);

                }
                usbBean.namelist.add(label);
            }
            List<String> names = checkUsbName(usbBean.namelist);
            usbBean.namelist.clear();
            usbBean.namelist.addAll(names);

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "zsr getAllStorageInfo: ");
        }
        return usbBean;
    }

    /**
     * 多个U盘检查一下
     * @param names
     * @return
     */
    private List<String> checkUsbName(List<String> names){
        List<String> lists = new ArrayList<>();
        lists.addAll(names);
        if (lists.size() > 1){
            for (int i = 0; i < lists.size(); i++) {
                String name = lists.get(i);
                for (int j =i+1 ;j < lists.size(); j++) {
                    String nextname = lists.get(j);
                    if (name.equals(nextname)){
                        lists.remove(i);
                        lists.add(i,name+"("+i+")");
                    }
                }
            }
        }
        return lists;
    }

    /**
     * 判断是否为图片
     * @param path
     * @return
     */
    public boolean isPictureFile(String path) {
        try {
            String ext = path.substring(path.lastIndexOf(".") + 1);
            if (ext.equalsIgnoreCase("png") || ext.equalsIgnoreCase("jpeg")
                    || ext.equalsIgnoreCase("jpg")
                    || ext.equalsIgnoreCase("gif")
                    || ext.equalsIgnoreCase("bmp")
                    || ext.equalsIgnoreCase("jfif")
                    || ext.equalsIgnoreCase("tiff")) {
                return true;
            }
        } catch (IndexOutOfBoundsException e) {
            return false;
        }

        return false;
    }
}
