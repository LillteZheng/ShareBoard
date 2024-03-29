package com.hht.sharelib.transtype.nio.entrance.server;

import com.hht.sharelib.utils.CloseUtils;
import com.hht.sharelib.bean.DeviceInfo;
import com.hht.sharelib.transtype.nio.core.Connector;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NioDataHandle extends Connector{
    private static final String TAG = "NioDataHandle";
    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
    private DataListener mListener;
    private  DeviceInfo mInfo;
    private SocketChannel mSocketChannel;
    public NioDataHandle(SocketChannel socket, DataListener listener) {
        mSocketChannel = socket;
        mListener = listener;
        try {
            setUp(socket);
            String ip = socket.socket().getInetAddress().getHostAddress();
            int port = socket.socket().getPort();
            mInfo = new DeviceInfo(ip, port, "client connect");
            mListener.onConnect(mInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void exitBySelt(){
        exit();
        if (mListener != null){
            mListener.onSelfClosed(this);
        }
    }

    public void exit() {
        CloseUtils.close(this);
        CloseUtils.close(mSocketChannel);
    }



    @Override
    public void onChannelClosed(SocketChannel channel) {
        super.onChannelClosed(channel);
        exitBySelt();

    }

    @Override
    protected void onReceiveNewMessage(String str) {
        super.onReceiveNewMessage(str);
        mListener.onResponse(this,str);
    }

    public DeviceInfo getInfo(){
        return mInfo;
    }

    public interface DataListener {
        void onResponse(NioDataHandle handle, String msg);
        void onSelfClosed(NioDataHandle handle);
        void onConnect(DeviceInfo info);
    }


}
