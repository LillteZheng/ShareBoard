package com.zhengsr.drawlib.window.settings.base;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhengsr.commonlib.window.CusPopup;
import com.zhengsr.drawlib.DrawConfig;
import com.zhengsr.drawlib.R;
import com.zhengsr.drawlib.adapter.RBaseAdapter;
import com.zhengsr.drawlib.adapter.RBaseViewholder;
import com.zhengsr.drawlib.bean.MenuBean;
import com.zhengsr.drawlib.bean.UsbBean;
import com.zhengsr.drawlib.callback.DrawSettingMenuListener;
import com.zhengsr.drawlib.utils.DiskUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * created by zhengshaorui on 2019/8/1
 * Describe: 用于各种存储设备的获取与显示
 */
public abstract class BaseSavePup  {
    protected static final String INNER_PATH = "storage/emulated/0";
    protected Context mContext;
    private List<DeviceInfo> mDeviceInfos = new ArrayList<>();
    protected CusPopup mCusPopup;
    private TextView mTitleTv;
    private int mLastIndex;
    private DeviceAdapter mDeviceAdapter;
    protected DrawSettingMenuListener mListener;
    public BaseSavePup(MenuBean bean){
        mContext = bean.context;
        mListener = bean.listener;
        mCusPopup = new CusPopup.Builder()
                .setContext(mContext)
                .setLayoutId(R.layout.base_storage_layout)
                .setWidth(getDimen(R.dimen.draw_save_width))
                .setHeight(getDimen(R.dimen.draw_save_height))
                .setAnimationStyle(R.style.PupAnim)
                .builder()
                .showAtLocation(bean.mainlayoutId, Gravity.CENTER,0,0);

        init();

        mCusPopup.setOnClickListener(R.id.menu_top_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCusPopup.dismiss();
            }
        });
        mTitleTv = mCusPopup.getViewbyId(R.id.menu_top_title);


        initDeviceRecycler();
    }

    public void init(){

    }
    public abstract void initView(DeviceInfo info);

    private void initDeviceRecycler() {
        UsbBean usbBean = DiskUtils.getInstance().getAllStorageInfo(DrawConfig.context());
        List<String> namelist = usbBean.namelist;
        if (namelist != null){
            mDeviceInfos.clear();
            int size = namelist.size();
            for (int i = 0; i < size; i++) {
                DeviceInfo info = new DeviceInfo();
                info.name = namelist.get(i);
                info.path = usbBean.pathlist.get(i);
                if (INNER_PATH.equals(info.name)){
                    info.name = mContext.getString(R.string.inner_storage);
                    info.iconId = R.drawable.draw_storage;
                    info.isSelect = true;
                    mTitleTv.setText(info.name);
                    mLastIndex = i;
                    initView(info);
                }else{
                    info.iconId = R.drawable.draw_usb;
                }
                mDeviceInfos.add(info);
            }
        }

        RecyclerView recyclerView = mCusPopup.getViewbyId(R.id.storage_device_rv);
        LinearLayoutManager manager = new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(manager);
        mDeviceAdapter = new DeviceAdapter(R.layout.item_recy_storage,mDeviceInfos);
        recyclerView.setAdapter(mDeviceAdapter);
        mDeviceAdapter.setonItemClickListener(new RBaseAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mLastIndex != position) {
                    DeviceInfo info1 = mDeviceInfos.get(mLastIndex);
                    if (info1 != null) {
                        info1.isSelect = false;
                        mDeviceInfos.set(mLastIndex, info1);
                    }
                    DeviceInfo info = mDeviceInfos.get(position);
                    if (info != null) {
                        info.isSelect = true;
                        mTitleTv.setText(info.name);
                        mLastIndex = position;
                        mDeviceInfos.set(position, info);
                        initView(info);
                    }

                    mDeviceAdapter.notifyDataSetChanged();
                }
            }
        });

    }




    protected class DeviceInfo{
        public String name;
        public String path;
        public int iconId = -1;
        public boolean isSelect;
    }

    protected int getDimen(int dimenId){
        return DrawConfig.context().getResources().getDimensionPixelSize(dimenId);
    }

    class DeviceAdapter extends RBaseAdapter<DeviceInfo>{

        public DeviceAdapter(int layoutid, List<DeviceInfo> list) {
            super(layoutid, list);
        }



        @Override
        public void getConver(RBaseViewholder holder, DeviceInfo data) {
            ImageView imageView = (ImageView) holder.getItemView(R.id.item_storage_iv);
            if (data.name != null) {
                holder.setText(R.id.item_storage_tv, data.name);
            }
            if (data.iconId != -1) {
                imageView.setImageResource(data.iconId);
            }
            View view = holder.getItemView(R.id.item_storage_ly);
            if (data.isSelect){
                view.setSelected(true);
            }else {
                view.setSelected(false);
            }
        }
    }


}
