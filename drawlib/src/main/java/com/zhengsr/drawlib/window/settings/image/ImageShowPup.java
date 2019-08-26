package com.zhengsr.drawlib.window.settings.image;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zhengsr.commonlib.LggUtils;
import com.zhengsr.drawlib.DrawConfig;
import com.zhengsr.drawlib.R;
import com.zhengsr.drawlib.adapter.RBaseAdapter;
import com.zhengsr.drawlib.adapter.RBaseViewholder;
import com.zhengsr.drawlib.bean.MenuBean;
import com.zhengsr.drawlib.callback.DrawSettingMenuListener;
import com.zhengsr.drawlib.tool.image.ImageBean;
import com.zhengsr.drawlib.tool.image.ImageLoadercache;
import com.zhengsr.drawlib.type.SettingMenuType;
import com.zhengsr.drawlib.utils.BitmapUtils;
import com.zhengsr.drawlib.utils.RxUtils;
import com.zhengsr.drawlib.window.settings.base.BaseSavePup;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * created by zhengshaorui on 2019/8/1
 * Describe:
 */
public class ImageShowPup extends BaseSavePup implements RBaseAdapter.onItemClickListener {
    private List<ImageBean> mImageBeans;
    private ImageAdapter mAdapter;
    private boolean useBackground = false;
    private ExecutorService mExecutorService;
    private RecyclerView mRecyclerView;

    public ImageShowPup(MenuBean bean) {
        super(bean);
    }


    @Override
    public void init() {
        super.init();
        mExecutorService = Executors.newSingleThreadExecutor();
        mImageBeans = new ArrayList<>();
        mRecyclerView = mCusPopup.getViewbyId(R.id.storage_content_rv);
        final GridLayoutManager manager = new GridLayoutManager(mContext,3);
        mRecyclerView.setLayoutManager(manager);
        mAdapter = new ImageAdapter(R.layout.item_image_save, mImageBeans);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setonItemClickListener(this);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE ||
                        newState == RecyclerView.SCROLL_STATE_DRAGGING){
                    mAdapter.setScrolling(false);
                    //通知adapter恢复getview的图片加载
                    if (Glide.with(mContext).isPaused()){
                        Glide.with(mContext).resumeRequests();
                    //    mAdapter.notifyDataSetChanged();
                    }
                }else{
                    mAdapter.setScrolling(true);
                }
                //通知一下
                super.onScrollStateChanged(recyclerView, newState);
            }


        });
    }

    @SuppressLint("CheckResult")
    @Override
    public void initView(BaseSavePup.DeviceInfo info) {
        mImageBeans.clear();
        /**
         * 使用rxjava遍历
         */
        showImage(info);


    }

    class MediaInfo{
        public int id;
        public String path;
        public String size;
        public String name;
        public String title;
        public String date;
        public String dateModify;
        public String mimetype;
        public String width;
        public String height;
        public String thumbPath;

        @Override
        public String toString() {
            return "MediaInfo{" +
                    "id=" + id +
                    ", path='" + path + '\'' +
                    ", size='" + size + '\'' +
                    ", name='" + name + '\'' +
                    ", title='" + title + '\'' +
                    ", date='" + date + '\'' +
                    ", dateModify='" + dateModify + '\'' +
                    ", mimetype='" + mimetype + '\'' +
                    ", width='" + width + '\'' +
                    ", height='" + height + '\'' +
                    ", thumbPath='" + thumbPath + '\'' +
                    '}';
        }
    }

    private void showImage(DeviceInfo info) {
        if (info.path.contains(INNER_PATH)){
            scanImgByThread();
        }else {
            Observable.just(new File(info.path))
                    .flatMap(new Function<File, ObservableSource<File>>() {
                        @Override
                        public ObservableSource<File> apply(File file) throws Exception {
                            return RxUtils.listFile(file);
                        }
                    })
                    .map(new Function<File, ImageBean>() {
                        @Override
                        public ImageBean apply(File file) throws Exception {
                            ImageBean imageBean = new ImageBean();
                            imageBean.imgPath = file.getAbsolutePath();
                            imageBean.bitmap = BitmapUtils.getScaleBitmap(imageBean.imgPath, getDimen(R.dimen.draw_item_image_width),
                                    getDimen(R.dimen.draw_item_image_height));

                            return imageBean;
                        }
                    })
                    .compose(RxUtils.<ImageBean>rxScheduers())
                    .subscribe(new Observer<ImageBean>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(ImageBean imageBean) {
                            mImageBeans.add(imageBean);
                            int size = mImageBeans.size();
                            if (size % 10 == 0) {
                                mAdapter.notifyItemRangeChanged(mImageBeans.size() - 1, 10);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onComplete() {
                            LggUtils.d("size: " + mImageBeans.size());
                            mAdapter.notifyDataSetChanged();
                        }
                    });
        }
    }

    public void useBackground() {
        useBackground = true;
    }

    class ImageBean{
        //当前图片路径
        public String imgPath;
        //文件夹中的首张图片路径，用于显示
        public String firstImgPath;
        //文件夹路径
        public String dirPath;
        //文件夹中的多少张图片
        public long picSize ;
        public Bitmap bitmap;

        @Override
        public String toString() {
            return "ImageBean{" +
                    "imgPath='" + imgPath + '\'' +
                    ", firstImgPath='" + firstImgPath + '\'' +
                    ", dirPath='" + dirPath + '\'' +
                    ", picSize=" + picSize +
                    '}';
        }
    }

    class ImageAdapter extends RBaseAdapter<ImageBean>{

        public ImageAdapter(int layoutid, List<ImageBean> list) {
            super(layoutid, list);
        }

        @Override
        public void getConver(RBaseViewholder holder, ImageBean data) {
            ImageView imageView = holder.getItemView(R.id.item_image_iv);
            //imageView.setImageResource(R.drawable.pictures_no);
           // ImageLoader.getInstance().loadImage(data.imgPath,imageView);
            //ImageLoadercache.getInstance().loadImage(data.imgPath,imageView);
            Glide.with(imageView)
                    .load(data.imgPath)
                    .placeholder(R.drawable.pictures_no)
                    .into(imageView);

        }
    }

    @Override
    public void onItemClick(View view, int position) {
        mCusPopup.dismiss();
        ImageBean imageBean = mImageBeans.get(position);
        if (imageBean != null) {
            if (mListener != null){
                if (!useBackground) {
                    mListener.settingMenu(SettingMenuType.IMAGE_PATH, imageBean.imgPath);
                }else{
                    mListener.settingMenu(SettingMenuType.BACKGROUND_IMAGE,imageBean.imgPath);
                    //DrawConfig.getParams().bgPath(imageBean.imgPath);
                    DrawConfig.getParams().setBg(imageBean.imgPath);
                }
            }
        }
    }

    static Handler handler = new Handler(Looper.getMainLooper());


    private void scanImgByThread(){
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                ContentResolver contentResolver = mContext.getContentResolver();
                Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                Cursor cursor =  contentResolver.query(uri,null,MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[] { "image/jpeg", "image/png" },
                        MediaStore.Images.Media.DATE_MODIFIED);
                long last = System.currentTimeMillis();
                if (cursor != null && cursor.moveToFirst()){
                    Cursor thumbCursor = null;
                    Set<String> dirList = new HashSet<String>();
                    String firstimgPath = null;
                    long picSize = 0;
                    do {
                        MediaInfo mediaInfo = new MediaInfo();
                        //  mediaInfo.id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                        mediaInfo.path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        mediaInfo.size = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
                        mediaInfo.name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                        mediaInfo.title = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE));
                        mediaInfo.date = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
                        mediaInfo.dateModify = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));
                        mediaInfo.mimetype = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));
                        mediaInfo.width = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.WIDTH));
                        mediaInfo.height = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT));

                        if (firstimgPath == null){
                            firstimgPath = mediaInfo.path;
                        }

                        File parentFile = new File(mediaInfo.path).getParentFile();
                        if (parentFile == null){
                            continue;
                        }
                        String dirPath = parentFile.getAbsolutePath();
                        //防止多次扫描同个文件夹
                        ImageBean bean = new ImageBean();

                        if (!dirList.contains(dirPath)){
                            dirList.add(dirPath);
                            if (parentFile.list() != null) {
                                picSize = parentFile.list(new FilenameFilter() {
                                    @Override
                                    public boolean accept(File dir, String name) {
                                        if (name.endsWith(".jpeg") ||
                                                name.endsWith(".png") ||
                                                name.endsWith(".jpg")
                                                ) {
                                            return true;
                                        }
                                        return false;
                                    }
                                }).length;
                            }
                        }
                        bean.picSize = picSize;
                        bean.dirPath = dirPath;
                        bean.imgPath = mediaInfo.path;
                        bean.firstImgPath = firstimgPath;

                        mImageBeans.add(bean);

                    }while (cursor.moveToNext());
                    cursor.close();
                }
                long time = System.currentTimeMillis() - last;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                        mAdapter.notifyDataSetChanged();
                    }
                });

            }
        });
    }
}
