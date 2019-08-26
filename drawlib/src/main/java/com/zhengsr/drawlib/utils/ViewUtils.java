package com.zhengsr.drawlib.utils;

import android.graphics.PorterDuff;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * created by @author zhengshaorui on 2019/8/26
 * Describe:
 */
public class ViewUtils {
    public static void setGray(ViewGroup viewGroup ,boolean gray,int grayColor,int normalColor){
        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = viewGroup.getChildAt(i);
            if (gray){
                viewGroup.setEnabled(false);
                if (view instanceof TextView){
                    ((TextView) view).setTextColor(grayColor);
                }
                if (view instanceof ImageView){
                    ((ImageView) view).setColorFilter(grayColor, PorterDuff.Mode.MULTIPLY);
                }
            }else{
                viewGroup.setEnabled(true);
                if (view instanceof TextView){
                    ((TextView) view).setTextColor(normalColor);
                }
                if (view instanceof ImageView){
                    ((ImageView) view).setColorFilter(normalColor, PorterDuff.Mode.MULTIPLY);
                }
            }
        }

    }
}
