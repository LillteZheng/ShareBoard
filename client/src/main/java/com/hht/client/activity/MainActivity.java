package com.hht.client.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;


import com.hht.client.BaseActivity;
import com.hht.client.R;
import com.hht.client.fragment.MainFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.zhengsr.commonlib.LggUtils;

import me.jessyan.autosize.AutoSizeCompat;
import me.jessyan.autosize.AutoSizeConfig;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /**
         *  6.0 以上，改变字体和状态栏
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            getWindow().setStatusBarColor(Color.WHITE);
            //设置状态栏字体颜色
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        }

        if (findFragment(MainFragment.class) == null){
            loadRootFragment(R.id.content, MainFragment.newInstance());
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
