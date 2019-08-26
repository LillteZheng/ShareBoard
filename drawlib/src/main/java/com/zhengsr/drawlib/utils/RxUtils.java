package com.zhengsr.drawlib.utils;


import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zhengshaorui
 * Time on 2018/9/5
 */

public class RxUtils {
    private static final String TAG = "RxUtils";


    /**
     * 递归所有图片
     * @param file
     * @return
     */
    public static Observable<File> listFile(final File file){
        if (file.isDirectory()){
            //如果是文件夹，则继续递归
            return Observable.fromArray(file.listFiles())
                    .flatMap(new Function<File, ObservableSource<File>>() {
                        @Override
                        public ObservableSource<File> apply(File file) throws Exception {
                            return listFile(file);
                        }
                    });

        }else{
            return Observable.just(file)
                    .filter(new Predicate<File>() {
                        @Override
                        public boolean test(File file) throws Exception {
                            String path = file.getAbsolutePath();
                            return file.length() > 0 && DiskUtils.getInstance().isPictureFile(path);
                        }
                    });
        }

    }



    /**
     * 封装线程调度
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<T,T> rxScheduers(){
        return new ObservableTransformer<T,T>(){

            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }






}
