package com.zhengsr.drawlib.window.settings;

import android.content.Context;
import android.view.Menu;
import android.view.View;

import com.zhengsr.drawlib.bean.MenuBean;
import com.zhengsr.drawlib.callback.DrawSettingMenuListener;
import com.zhengsr.drawlib.type.SettingMenuType;

/**
 * created by zhengshaorui on 2019/7/31
 * Describe: 管理menu setting和一些借口回调
 */
public class SettingMenuManager {
    private MenuBean mBean;
    private SettingMenuManager(){
        mBean = new MenuBean();
    }
    public static SettingMenuManager create(){
        return new SettingMenuManager();
    }


    public SettingMenuManager context(Context context){
        mBean.context = context;
        return this;
    }
    public SettingMenuManager view(View view){
        mBean.view = view;
        return this;
    }


    public SettingMenuManager ip(String ip){
        mBean.ip = ip;
        return this;
    }

    public SettingMenuManager addListener(DrawSettingMenuListener listner){
        mBean.listener = listner;
        return this;
    }

    public SettingMenuManager mainLayoutId(int layoutId){
        mBean.mainlayoutId = layoutId;
        return this;
    }

    public void showMenu(){
        new SettingsMenuPup(mBean);
    }


}
