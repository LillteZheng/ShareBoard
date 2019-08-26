package com.zhengsr.drawlib.window.settings;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.zhengsr.commonlib.window.ToastUtils;
import com.zhengsr.drawlib.R;
import com.zhengsr.drawlib.bean.MenuBean;
import com.zhengsr.drawlib.callback.DrawSettingMenuListener;
import com.zhengsr.drawlib.type.SettingMenuType;
import com.zhengsr.drawlib.window.settings.base.BaseSavePup;
import com.zhengsr.drawlib.window.settings.base.BaseSettingPup;
import com.zhengsr.drawlib.window.settings.bg.MenuBgPup;
import com.zhengsr.drawlib.window.settings.image.ImageShowPup;
import com.zhengsr.drawlib.window.settings.rqcode.RqCodePup;
import com.zhengsr.drawlib.window.settings.save.SaveChoosePup;

/**
 * created by zhengshaorui on 2019/7/31
 * Describe:
 */
public class SettingsMenuPup extends BaseSettingPup implements  View.OnClickListener {


    public SettingsMenuPup(MenuBean bean) {
        super(bean);
    }


    @Override
    public int getLayoutId() {
        return R.layout.settings_menu_layout;
    }

    @Override
    public void initView(View view) {


        ViewGroup ly1 = view.findViewById(R.id.menu_setting_ly1);
        ViewGroup ly2 = view.findViewById(R.id.menu_setting_ly2);

        int childCount = ly1.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childview = ly1.getChildAt(i);
            childview.setOnClickListener(this);
        }
        int childCount2 = ly2.getChildCount();
        for (int i = 0; i < childCount2; i++) {
            View childview = ly2.getChildAt(i);
            childview.setOnClickListener(this);
        }
    }





    @Override
    public void onClick(View v) {
        int id = v.getId();
        /**
         * android lib 中，id 不为常亮，故使用不了 switch，采用if-else
         */
        mCusPopup.dismiss();
        if (id == R.id.menu_setting_bg) {
            new MenuBgPup(mBean);
        } else if (id == R.id.menu_setting_image) {
           new ImageShowPup(mBean);
        } else if (id == R.id.menu_setting_rqcode) {
            new RqCodePup(mBean);
        } else if (id == R.id.menu_setting_email) {
        } else if (id == R.id.menu_setting_save) {
            new SaveChoosePup(mBean);
        } else if (id == R.id.menu_setting_more) {
        } else {
        }
    }
}
