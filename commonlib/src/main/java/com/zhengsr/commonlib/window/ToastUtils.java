package com.zhengsr.commonlib.window;

import android.content.Context;
import android.widget.Toast;

/**
 * created by @author zhengshaorui on 2019/8/21
 * Describe:
 */
public class ToastUtils {
    private static Toast toast;

    public static void makeText(Context context, String content) {
        if (toast == null) {
            toast = Toast.makeText(context,
                    content,
                    Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }
        toast.show();
    }

}
