package com.zhengsr.drawlib.window.settings.save;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.zhengsr.commonlib.LggUtils;
import com.zhengsr.drawlib.DrawConfig;
import com.zhengsr.drawlib.R;
import com.zhengsr.drawlib.bean.MenuBean;
import com.zhengsr.drawlib.callback.DrawSettingMenuListener;
import com.zhengsr.drawlib.tool.ImageSaveTools;
import com.zhengsr.drawlib.window.settings.SettingMenuManager;
import com.zhengsr.drawlib.window.settings.SettingsMenuPup;
import com.zhengsr.drawlib.window.settings.base.BaseSettingPup;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * created by zhengshaorui on 2019/8/6
 * Describe: 保存文件，用来保存源文件，图片，pdf等格式
 */
public class SaveChoosePup extends BaseSettingPup implements View.OnClickListener {


    private EditText mEditText;
    private RadioButton mSourceRadio;
    private RadioButton mBitmapRadio;

    public SaveChoosePup(MenuBean bean) {
        super(bean);
    }


    @Override
    public int getHeight() {
        return DrawConfig.px2dp(180);
    }

    @Override
    public int getLayoutId() {
        return R.layout.pup_save_choose_layout;
    }

    @Override
    public void initView(final View view) {

        view.findViewById(R.id.menu_bg_back).setOnClickListener(this);
        view.findViewById(R.id.menu_btn_cancel).setOnClickListener(this);
        view.findViewById(R.id.menu_btn_comfire).setOnClickListener(this);

        SimpleDateFormat simpleFormatter = new SimpleDateFormat("yyMMddHHmmss");
        String time = simpleFormatter.format(new Date());
        mEditText = view.findViewById(R.id.pup_save_edit);
        mEditText.setText(mContext.getString(R.string.save_default_name,time));
        mEditText.setSelection(mEditText.getText().toString().length());


        mSourceRadio = view.findViewById(R.id.pup_save_radio_source);
        mBitmapRadio = view.findViewById(R.id.pup_save_radio_bitmap);

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.menu_bg_back == id || R.id.menu_btn_cancel == id){
            mCusPopup.dismiss();
            new SettingsMenuPup(mBean);
        }else{
            Bitmap bg = DrawConfig.getParams().getBg();
            if (bg != null) {
                if (mBitmapRadio.isChecked()) {
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                    mCusPopup.dismiss();

                    new ImageSaveTools()
                            .context(mContext)
                            .path(path)
                            .name(mEditText.getText().toString())
                            .save();
                } else {
                    // ResourceDataManager.getInstance().loadResource(mView.getBg());
                }
            }else{
                Toast.makeText(mContext, "没有背景", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
