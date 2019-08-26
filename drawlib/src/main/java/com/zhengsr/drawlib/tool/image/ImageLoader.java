package com.zhengsr.drawlib.tool.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;


import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * created by zhengsr on 2019/8/3
 * Describe: 图片加载等操作的类
 */
public class ImageLoader {
    private static final String TAG = "ImageLoader";

    private Handler mPoolThreadHandler;
    private final MemoryCache mMemoryCache;
    private ExecutorService mThreadPool;
    private Handler mUiHandler;
    /**
     * 线程队列
     */
    private LinkedList<Runnable> mTaskQueue;

    private Semaphore mSemaphorePoolThread;

    /**
     * 并发等待，同步关键类，构造方法传入的数字是多少，则同一个时刻，只运行多少个进程同时运行制定代码
     */
    private Semaphore mSemaphoreAddTask = new Semaphore(0);


    public static ImageRequest with(Context context){
        return ImageRequest.getInstance().with(context);
    }


    private static class Holder{
        static ImageLoader HODLER = new ImageLoader();
    }


    public static ImageLoader getInstance(){
        return Holder.HODLER;
    }
    private ImageLoader(){
        //开个线程，用来轮询线程事件
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                //创建一个后台handler
                mPoolThreadHandler =new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        mThreadPool.execute(getTaskFromEnque());

                        try {
                            mSemaphorePoolThread.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                mSemaphoreAddTask.release();
                //开启一个looper
                Looper.loop();

            }
        }).start();

        mMemoryCache = MemoryCache.create();

        //创建一个固定的线程池，用来解析Task
        mThreadPool = Executors.newFixedThreadPool(3);

        mTaskQueue = new LinkedList<>();
        //同时允许三个线程进行
        mSemaphorePoolThread = new Semaphore(3);

        mUiHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                ImageBean bean = (ImageBean) msg.obj;
                String imgpath = bean.path;
                Bitmap bitmap = bean.bitmap;
                ImageView iv = bean.imageView;
                if (iv.getTag().toString().equals(imgpath)){
                    iv.setImageBitmap(bitmap);
                }
            }
        };

    }


    public void loadImage(final String path, final ImageView imageView){
        //设置tag，防止异步加载错乱问题
        imageView.setTag(path);

        Bitmap bitmap = mMemoryCache.getCacheBitmap(path);
        if (bitmap != null){
            //使用UI线程更新imageview
            Message msg = new Message();
            ImageBean bean = new ImageBean();
            bean.bitmap = bitmap;
            bean.imageView = imageView;
            bean.path = path;
            msg.obj = bean;
            mUiHandler.sendMessage(msg);

        }else{
            addTask(new Runnable() {
                @Override
                public void run() {
                    ImageUtils.ImageSize imageSize = ImageUtils.getImageSize(imageView);
                    Bitmap bm = ImageUtils.getScaleBitmap(path,imageSize.width,imageSize.height);
                    if (bm != null) {
                        mMemoryCache.addBitmapToCache(path, bm);

                        //使用UI线程更新imageview
                        Message msg = new Message();
                        ImageBean bean = new ImageBean();
                        bean.bitmap = bm;
                        bean.imageView = imageView;
                        bean.path = path;
                        msg.obj = bean;
                        mUiHandler.sendMessage(msg);
                        mSemaphorePoolThread.release();
                    }
                }
            });

        }
    }


    public synchronized void addTask(Runnable runnable){
        mTaskQueue.add(runnable);
        if (mPoolThreadHandler == null) {
            try {
                mSemaphoreAddTask.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        mPoolThreadHandler.sendEmptyMessage(1);

    }

    /**
     * 获取栈队列,从最后一个开始获取，这样当用户停止时，能从当前控件开始加载
     * @return
     */
    private synchronized Runnable getTaskFromEnque() {
        return  mTaskQueue.removeLast();
    }



    class ImageBean{
        public ImageView imageView;
        public String path;
        public Bitmap bitmap;
    }




}
