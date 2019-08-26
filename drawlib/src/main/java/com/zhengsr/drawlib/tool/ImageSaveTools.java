package com.zhengsr.drawlib.tool;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.FrameLayout;

import com.zhengsr.drawlib.DrawConfig;
import com.zhengsr.drawlib.R;
import com.zhengsr.drawlib.action.BaseAction;
import com.zhengsr.drawlib.action.GestureAction;
import com.zhengsr.drawlib.bean.GesImgBean;
import com.zhengsr.drawlib.page.PageDataManager;
import com.zhengsr.drawlib.view.GestureImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageSaveTools {
    private Context mContext;
    private String mPath;
    private String mName ;
    private List<GesImgBean> mGesImgBeans = new ArrayList<>();
    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
    static Handler mHandler = new Handler(Looper.getMainLooper());
    public ImageSaveTools(){

    }
    public ImageSaveTools context(Context context){
        mContext = context;
        return this;
    }

    public ImageSaveTools gesImgBeans(List<GesImgBean> gesImgBeans){
        mGesImgBeans.clear();
        mGesImgBeans.addAll(gesImgBeans);
        return this;
    }

    public ImageSaveTools path(String path){
        if (TextUtils.isEmpty(path)){
            throw new RuntimeException("path cannot be null");
        }
        //先创建文件夹
        File file = new File(path, mContext.getString(R.string.app_name));
        if (!file.exists()){
            file.mkdir();
        }
        mPath = file.getAbsolutePath();
        return this;
    }
    public ImageSaveTools name(String name){
        mName = name;
        return this;
    }

    public ImageSaveTools save(){
        if (TextUtils.isEmpty(mName)){
            mName = mContext.getString(R.string.draw_meet_record);
        }
        new saveBitmapAsync(mContext,mPath,mName).executeOnExecutor(mExecutorService);
        return this;
    }

    class saveBitmapAsync extends AsyncTask<Void,Integer,Boolean> {

        Context context;
        ProgressDialog dialog;
        String path;
        String name;

        public saveBitmapAsync(Context context, String path, String name) {
            this.context = context;
            this.path = path;
            this.name = name;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = ProgressDialog.show(context,null,null);

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            HashMap<Integer, List<BaseAction>> data = PageDataManager.getInstance().getPageData();
            if (data != null && data.size() > 0){
                int width = context.getResources().getDisplayMetrics().widthPixels;
                int height = context.getResources().getDisplayMetrics().heightPixels;
                Bitmap bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                int count = 0;
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
                    count +=1;
                    publishProgress(count,data.size());
                    StringBuilder sb = new StringBuilder();
                    sb.append(name)
                            .append("page")
                            .append(count)
                            .append(new SimpleDateFormat("yyMMddHHmmss").format(new Date()))
                            .append(".png");
                    saveBitmap(context,bitmap,path,sb.toString());
                }
                return true;
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            String msg = context.getString(R.string.draw_save_file,values[0],values[1]);
            dialog.setMessage(msg);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                dialog.setMessage(context.getString(R.string.draw_save_bg_success));
            }else {
                dialog.setMessage(context.getString(R.string.draw_save_bg_fail));
            }
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            },1000);

        }
    }

    static void saveBitmap(Context context,Bitmap bitmap,String path,String name) {
        try {
            File file = new File(path,name);
            if (!file.exists()){
                file.delete();
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bitmap.compress(Bitmap.CompressFormat.PNG,80,bos);
            bos.flush();
            bos.close();
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
