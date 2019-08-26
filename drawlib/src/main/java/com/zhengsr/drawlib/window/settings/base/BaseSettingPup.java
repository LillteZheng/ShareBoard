package com.zhengsr.drawlib.window.settings.base;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.zhengsr.commonlib.window.CusPopup;
import com.zhengsr.drawlib.DrawConfig;
import com.zhengsr.drawlib.R;
import com.zhengsr.drawlib.bean.MenuBean;
import com.zhengsr.drawlib.callback.DrawSettingMenuListener;

/**
 * created by zhengshaorui on 2019/7/31
 * Describe: 白板设置基类基类，用于实现view栈和其他资源调度
 */
public abstract class BaseSettingPup {

    protected final CusPopup mCusPopup;
    protected Context mContext;
    protected View mView;
    protected DrawSettingMenuListener mListener;
    protected MenuBean mBean;
    public BaseSettingPup(MenuBean bean) {
        mBean = bean;
        int WIDTH = bean.context.getResources().getDimensionPixelSize(R.dimen.draw_settings_pup_width);
        final int HEIGHT = bean.context.getResources().getDimensionPixelSize(R.dimen.draw_settings_pup_height);
        mContext = bean.context;
        mView = bean.view;
        mListener = bean.listener;
        mCusPopup = new CusPopup.Builder()
                .setContext(mContext)
                .setLayoutId(R.layout.base_pup_layout)
                .setWidth(getWidth() != -1 ? getWidth():WIDTH)
                .setHeight(getHeight() != -1 ? getHeight():HEIGHT)
                .setOutSideDimiss(true)
                .setAnimationStyle(R.style.PupAnim)
                .builder()
                .showAsLaction(mView, Gravity.CENTER,-mView.getWidth(),10);

        FrameLayout frameLayout = mCusPopup.getViewbyId(R.id.draw_base_pup_layout);
        View contentView = LayoutInflater.from(mContext).inflate(getLayoutId(),frameLayout,false);
        frameLayout.removeAllViews();
        frameLayout.addView(contentView);
        init();
        initView(contentView);
    }

    /**
     * 一些初始化需要放在这
     */
    public void init(){

    }
    public abstract int getLayoutId();
    public abstract void initView(View view);


    public int getWidth(){
        return -1;
    }
    public int getHeight(){
        return -1;
    }

    public int getDimen(int dimenId){
        return mContext.getResources().getDimensionPixelSize(dimenId);
    }

}
