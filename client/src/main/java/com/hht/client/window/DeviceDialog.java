package com.hht.client.window;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.hht.client.R;
import com.hht.client.adapter.LGAdapter;
import com.hht.client.adapter.LGViewHolder;
import com.hht.client.fragment.MainFragment;
import com.hht.sharelib.TransServiceManager;
import com.hht.sharelib.bean.DeviceInfo;
import com.hht.sharelib.transtype.socket.udp.UdpManager;
import com.hht.sharelib.transtype.socket.udp.client.UdpSearcher;
import com.hht.sharelib.utils.PingUtils;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.zhengsr.commonlib.window.CusDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * created by @author zhengshaorui on 2019/8/23
 * Describe:
 */
public class DeviceDialog implements View.OnClickListener, UdpSearcher.DeviceListener, AdapterView.OnItemClickListener {
    private Activity mContext;
    private MainFragment mFragment;
    private ProgressBar mBar;
    private ListView mListView;

    private List<DeviceInfo> mDevices = new ArrayList<>();
    private DeviceAdapter mDeviceAdapter;
    private View mEmptyview;
    private InfoListener mInfoListener;
    private final CusDialog mCusDialog;

    public DeviceDialog(MainFragment fragment,Activity context,InfoListener listener,DeviceInfo info){
        mInfoListener = listener;
        mFragment = fragment;
        mContext = context;
        mCusDialog = new CusDialog.Builder()
                .setContext(context)
                .setLayoutId(R.layout.dialog_choose_device)
                .setWidth(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setAnimation(R.style.window_alpha)
                .setOutSideDimiss(false)
                .showAlphaBg(true)
                .builder();

        initView(mCusDialog,info);


    }

    private void initView(CusDialog cusDialog,DeviceInfo info) {

        mEmptyview = cusDialog.getViewbyId(R.id.dialog_empty_view);
        mListView = cusDialog.getViewbyId(R.id.dialog_list);
        mBar = cusDialog.getViewbyId(R.id.progress);
        if (info != null) {
            mDevices.clear();
            mDevices.add(info);
            mEmptyview.setVisibility(View.GONE);
        }
        mDeviceAdapter = new DeviceAdapter(mDevices,R.layout.item_device_info);
        mListView.setAdapter(mDeviceAdapter);
        mListView.setOnItemClickListener(this);
        cusDialog.setDismissByid(R.id.dialog_cancel);


        //点击事件

        cusDialog.getViewbyId(R.id.dialog_scan).setOnClickListener(this);
        cusDialog.getViewbyId(R.id.dialog_search).setOnClickListener(this);
        cusDialog.getViewbyId(R.id.dialog_net).setOnClickListener(this);




    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dialog_scan:
                PermissionsUtil.requestPermission(mContext, new PermissionListener() {
                    @Override
                    public void permissionGranted(@NonNull String[] permission) {
                        mCusDialog.dismiss();
                        Intent intent = new Intent(mContext, CaptureActivity.class);
                        mFragment.startActivityForResult(intent, 1);
                    }

                    @Override
                    public void permissionDenied(@NonNull String[] permission) {
                        Toast.makeText(mContext, R.string.open_camerafail, Toast.LENGTH_SHORT).show();
                    }
                }, Manifest.permission.CAMERA);

                break;
            case R.id.dialog_search:
                mBar.setVisibility(View.VISIBLE);
                mEmptyview.setVisibility(View.GONE);
                UdpManager.searchDevice(2000,this);
                break;
            case R.id.dialog_net:
                mContext.startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
                break;
            default:break;
        }
    }



    @Override
    public void findDevice(List<DeviceInfo> devices) {
        mBar.setVisibility(View.GONE);
        if (devices.isEmpty()){
            mEmptyview.setVisibility(View.VISIBLE);
        }
        mDevices.clear();
        mDevices.addAll(devices);
        mDeviceAdapter.notifyDataSetChanged();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mBar.setVisibility(View.VISIBLE);
        final DeviceInfo info = mDevices.get(position);
        TransServiceManager.isCanPing(info.ip, new PingUtils.PingListener() {
            @Override
            public void isPingOk(boolean isCanPing) {
               if (mInfoListener != null){
                   mInfoListener.getInfo(info,isCanPing);
               }
               mBar.setVisibility(View.GONE);
                mCusDialog.dismiss();
            }
        });

    }

    class DeviceAdapter extends LGAdapter<DeviceInfo> {

        public DeviceAdapter(List<DeviceInfo> datas, int layoutid) {
            super(datas, layoutid);
        }

        @Override
        public void convert(LGViewHolder viewHolder, DeviceInfo data, int position) {
            viewHolder.setText(R.id.item_text,mContext.getString(R.string.device_describe,data.info,data.ip));
        }
    }

    public interface InfoListener {
        void getInfo(DeviceInfo info, boolean isCanPing);
    }
}
