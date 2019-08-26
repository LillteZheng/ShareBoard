package com.hht.client.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.hht.client.BaseActivity;
import com.hht.client.Constans;
import com.hht.client.R;
import com.hht.client.view.BottomViewImp;
import com.hht.sharelib.TransServiceManager;
import com.hht.sharelib.bean.DeviceInfo;
import com.hht.sharelib.callback.TcpClientListener;
import com.zhengsr.commonlib.LggUtils;
import com.zhengsr.commonlib.window.ToastUtils;
import com.zhengsr.drawlib.callback.TransListener;
import com.zhengsr.drawlib.page.PageDrawCommand;
import com.zhengsr.drawlib.trans.TransManager;
import com.zhengsr.drawlib.type.DrawActionType;
import com.zhengsr.drawlib.view.WhiteBoardView;

import me.jessyan.autosize.internal.CustomAdapt;

public class DrawActivity extends BaseActivity implements TcpClientListener, TransListener,CustomAdapt {
    private ProgressDialog mDialog;
    private TextView mTextView;
    private PageDrawCommand mDrawStutas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        initView();
        Intent arguments = getIntent();
        String ip = null;
        if (arguments != null){
            DeviceInfo device = (DeviceInfo) arguments.getSerializableExtra(Constans.DEVICE);
            if (device != null) {
                ip = device.ip;
                mDialog = ProgressDialog.show(this,null,getString(R.string.connecting_server));
                mDialog.setCancelable(true);
                mDialog.show();
            }
        }
        if (!TextUtils.isEmpty(ip)){
            TransServiceManager.get()
                    .nio()
                    .client(ip)
                    .listener(this)
                    .start();
        }else{
            Toast.makeText(this, R.string.cannot_connect_server, Toast.LENGTH_SHORT).show();
        }
    }



    private void initView() {
        mTextView = findViewById(R.id.info_text);
        WhiteBoardView whiteBoradView = findViewById(R.id.whiteboradview);
        PageDrawCommand command = new PageDrawCommand(this,whiteBoradView, DrawActionType.PEN,this);
        ViewGroup viewGroup = findViewById(R.id.bottom_ly);
        new BottomViewImp(this, command,viewGroup);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TransServiceManager.stop();
    }

    @Override
    public void onResponse(String msg) {
        TransManager.getInstance().onTransResponse(msg);
    }





    @Override
    public void sendTransData(String drawMsg) {
        TransServiceManager.sendClientMsg(drawMsg);
    }
    @Override
    public void sendResponseConflict() {
        ToastUtils.makeText(this, "正在传输，不能同时画线");
    }

    @Override
    public void serverConnected(DeviceInfo info) {
        mTextView.setText("已连接服务器: "+info.ip);
        mDialog.dismiss();
    }

    @Override
    public void serverDisconnect(DeviceInfo info) {
        mTextView.setText("未连接服务器");

        Toast.makeText(this, "服务端已断开", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void serverConnectFail(String msg) {
        LggUtils.d("serverConnectFail: "+msg);
        mDialog.dismiss();
        Toast.makeText(this, R.string.connect_server_fail, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean isBaseOnWidth() {
        return true;
    }

    @Override
    public float getSizeInDp() {
        return 640;
    }
}
