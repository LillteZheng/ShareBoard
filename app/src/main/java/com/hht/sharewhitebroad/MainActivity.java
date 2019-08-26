package com.hht.sharewhitebroad;

import android.Manifest;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.hht.sharelib.TransServiceManager;
import com.hht.sharelib.bean.DeviceInfo;
import com.hht.sharelib.callback.TcpServerListener;
import com.hht.sharewhitebroad.view.BottomViewImp;
import com.zhengsr.commonlib.LggUtils;
import com.zhengsr.commonlib.window.ToastUtils;
import com.zhengsr.drawlib.callback.TransListener;
import com.zhengsr.drawlib.trans.TransManager;
import com.zhengsr.drawlib.type.DrawActionType;
import com.zhengsr.drawlib.page.PageDrawCommand;
import com.zhengsr.drawlib.view.WhiteBoardView;

import me.jessyan.autosize.AutoSizeCompat;
import me.jessyan.autosize.AutoSizeConfig;


public class MainActivity extends AppCompatActivity implements TcpServerListener, TransListener {

    private TextView mTextView;
    private ViewGroup mBottomLy;
    private PageDrawCommand mDrawState;
    private WhiteBoardView mWhiteBoradView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置横屏尺寸
        AutoSizeConfig.getInstance().setDesignWidthInDp(640);
        setContentView(R.layout.activity_main);
        initDraw();
        initTrans();
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);



    }

    @Override
    public Resources getResources() {
        AutoSizeCompat.autoConvertDensityOfGlobal((super.getResources()));//如果没有自定义需求用这个方法
        return super.getResources();
    }

    private void initTrans() {
        /**
         * 初始传输
         */

         TransServiceManager.startProvider();
         TransServiceManager.get()
                .nio()
                .server()
                .listener(this)
                .start();

        TransManager.getInstance().addTransListener(this);

        mTextView = findViewById(R.id.info_text);
    }

    private void initDraw() {
        mWhiteBoradView = findViewById(R.id.whiteboradview);

        PageDrawCommand pageCommand = new PageDrawCommand(this,mWhiteBoradView, DrawActionType.PEN,this);


        mBottomLy = findViewById(R.id.bottom_ly);
        new BottomViewImp(this,pageCommand,mBottomLy);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    long time;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            long curTime = System.currentTimeMillis();
            if (curTime - time > 2000){
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                time = curTime;
                return false;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TransServiceManager.stopProvider();
        TransServiceManager.stop();
    }




    @Override
    public void onResponse(String msg) {
        TransManager.getInstance().onTransResponse(msg);
    }

    @Override
    public void sendTransData(String drawMsg) {
        TransServiceManager.sendBroServerMsg(drawMsg);
    }

    @Override
    public void sendResponseConflict() {
        ToastUtils.makeText(this, "正在传输，不能同时画线");
    }




    @Override
    public void onClientCount(int count) {
        String msg = "共有 "+count+" 个客户端连接";
        mTextView.setText(msg);
    }

    @Override
    public void onClientConnected(DeviceInfo info) {
        LggUtils.d("新客户端连接: " + info.toString());
    }

    @Override
    public void onClientDisconnect(DeviceInfo info) {
        LggUtils.d("客户端: " + info.ip + "断开连接");
    }



}
