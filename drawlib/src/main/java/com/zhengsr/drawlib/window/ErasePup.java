package com.zhengsr.drawlib.window;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.zhengsr.commonlib.window.CusPopup;
import com.zhengsr.drawlib.DrawConfig;
import com.zhengsr.drawlib.R;
import com.zhengsr.drawlib.type.SizeType;

/**
 * @auther by zhengshaorui on 2019/8/24
 * describe: 橡皮擦大小设置
 */
public class ErasePup {

    private final ViewGroup mSizeLy;

    public ErasePup(Context context, View parentview){
        CusPopup popup = new CusPopup.Builder()
                .setContext(context)
                .setLayoutId(R.layout.pup_erase)
                .setWidth(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setHeight(context.getResources().getDimensionPixelSize(R.dimen.draw_erase_pup_height))
                .setOutSideDimiss(true)
                .setAnimationStyle(R.style.PupAnim)
                .builder()
                .showAsLaction(parentview, Gravity.CENTER,0,10);

        mSizeLy = popup.getViewbyId(R.id.erase_pup_ly);
        int childCount = mSizeLy.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = mSizeLy.getChildAt(i);
            final int index = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetAndChoose(index);
                    if (mListener != null){
                        int size;
                        if (index == 0){
                            size = DrawConfig.ERASE_SMALL;
                        }else if (index == 1){
                            size = DrawConfig.ERASE_MIDDLE;
                        }else{
                            size = DrawConfig.ERASE_LARGE;
                        }
                        mListener.onSize(size);
                    }
                }
            });
        }
    }

    public ErasePup eraseSize(int size ){
        if (size == DrawConfig.ERASE_SMALL){
            resetAndChoose(0);
        }else if (size == DrawConfig.ERASE_MIDDLE){
            resetAndChoose(1);
        }else{
            resetAndChoose(2);
        }

        return this;
    }
    private ErasePupListener mListener;
    public ErasePup listener(ErasePupListener listener){
        mListener = listener;
        return this;
    }

    private void resetAndChoose(int index){
        int childCount = mSizeLy.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = mSizeLy.getChildAt(i);
            view.setSelected(false);
        }
        mSizeLy.getChildAt(index).setSelected(true);
    }

    public interface ErasePupListener{
        void onSize(int size);
    }
}
