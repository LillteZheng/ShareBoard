package com.zhengsr.drawlib.tool;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.provider.MediaStore;
import android.widget.FrameLayout;

import com.zhengsr.commonlib.LggUtils;
import com.zhengsr.drawlib.DrawConfig;
import com.zhengsr.drawlib.action.BaseAction;
import com.zhengsr.drawlib.action.GestureAction;
import com.zhengsr.drawlib.bean.GesImgBean;
import com.zhengsr.drawlib.page.PageDataManager;
import com.zhengsr.drawlib.view.GestureImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @auther by zhengshaorui on 2019/8/22
 * describe: 用于后台保存数据
 */
public class ImageSaveBackground {

    private ImageSaveBackground(){
    }
    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
    private volatile List<Bitmap> mBitmaps = new ArrayList<>();
    private static class Holder{
        static ImageSaveBackground HODLER = new ImageSaveBackground();
    }

    public static ImageSaveBackground getInstance(){
        return Holder.HODLER;
    }

    /**
     * 开线程去保存图片
     */
    public void saveBackground(){
        final int width = 1920;
        final int height = 1080;
        mBitmaps.clear();
       /* mExecutorService.execute(new Runnable() {
            @Override
            public void run() {

            }
        });*/
        HashMap<Integer, List<BaseAction>> data = PageDataManager.getInstance().getPageData();
        if (data != null && data.size() > 0){
            Bitmap bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            for (Map.Entry<Integer, List<BaseAction>> entry : data.entrySet()) {
                List<BaseAction> baseActions = entry.getValue();
                canvas.drawBitmap(DrawConfig.getParams().getBg(entry.getKey()),0,0,null);
                /**
                 * 保存素材
                 */
                FrameLayout backview = DrawConfig.getParams().getBackview();
                int childCount = backview.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    GestureImageView view = (GestureImageView) backview.getChildAt(i);
                    GesImgBean bean = view.getImgBean();
                    if (bean.pageId == entry.getKey()){
                        canvas.drawBitmap(bean.bitmap,bean.matrix,null);
                    }
                }
                /**
                 * 保存画笔
                 */
                for (BaseAction action : baseActions) {
                    if (!(action instanceof GestureAction)) {
                        action.draw(canvas);
                    }
                }

                mBitmaps.add(bitmap);

            }
        }


    }

    public List<Bitmap> getBitmaps() {
        return mBitmaps;
    }
}
