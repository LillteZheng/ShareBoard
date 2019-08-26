package com.zhengsr.drawlib.window.settings.bg;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.zhengsr.commonlib.adapter.LGAdapter;
import com.zhengsr.commonlib.adapter.LGViewHolder;
import com.zhengsr.drawlib.DrawConfig;
import com.zhengsr.drawlib.R;
import com.zhengsr.drawlib.bean.MenuBean;
import com.zhengsr.drawlib.callback.DrawSettingMenuListener;
import com.zhengsr.drawlib.type.SettingMenuType;
import com.zhengsr.drawlib.utils.BgUtils;
import com.zhengsr.drawlib.window.settings.base.BaseSettingPup;
import com.zhengsr.drawlib.window.settings.SettingMenuManager;
import com.zhengsr.drawlib.window.settings.SettingsMenuPup;
import com.zhengsr.drawlib.window.settings.image.ImageShowPup;

import java.util.ArrayList;
import java.util.List;

/**
 * created by zhengshaorui on 2019/7/31
 * Describe: 背景设置
 */
public class MenuBgPup extends BaseSettingPup implements AdapterView.OnItemClickListener {

    private final int COLOR_WIDTH = 100;
    private final int COLOR_HEIGHT = 60;

    private  int[] COLORS;
    private List<ImageBean> mDatas;

    public MenuBgPup(MenuBean bean) {
        super(bean);
    }


    @Override
    public int getLayoutId() {
        return R.layout.pup_bg_layout;
    }



    @Override
    public void init() {
        COLORS = new int[]{
            R.color.draw_default,R.color.draw_blue_color,R.color.draw_white_color
        };
    }


    @Override
    public void initView(View view) {
        view.findViewById(R.id.menu_bg_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCusPopup.dismiss();
                new SettingsMenuPup(mBean);
            }
        });
        mDatas = new ArrayList<>();
        initData();
        GridView gridView = view.findViewById(R.id.menu_bg_grid);
        gridView.setAdapter(new ImageAdapter(mDatas,R.layout.item_bg_layout));
        gridView.setOnItemClickListener(this);
    }

    private void initData() {

        for (int i = 0; i < BgUtils.getBgLength(); i++) {
            ImageBean bean = new ImageBean();
            bean.bitmap = BgUtils.getChooseBitmap(mContext,i, COLOR_WIDTH,COLOR_HEIGHT);
            mDatas.add(bean);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position < mDatas.size() - 1) {
            DrawConfig.getParams().setBg(position);
            mListener.settingMenu(SettingMenuType.BACKGROUND, position);
        }else{
            mCusPopup.dismiss();
            new ImageShowPup(mBean).useBackground();
        }
    }


    class ImageBean{
        public int resId = -1;
        public Drawable drawable = null;
        public Bitmap bitmap = null;

        @Override
        public String toString() {
            return "ImageBean{" +
                    "resId=" + resId +
                    '}';
        }
    }

    class ImageAdapter extends LGAdapter<ImageBean>{

        public ImageAdapter(List<ImageBean> datas, int layoutid) {
            super(datas, layoutid);
        }

        @Override
        public void convert(LGViewHolder viewHolder, ImageBean data, int position) {
            ImageView imageView = viewHolder.getView(R.id.item_bg_iv);
            if (data.bitmap != null){
                imageView.setImageBitmap(data.bitmap);
            }

        }
    }
}
