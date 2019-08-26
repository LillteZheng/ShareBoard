package com.hht.client.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.hht.client.Constans;
import com.hht.client.activity.DrawActivity;
import com.hht.client.R;
import com.hht.client.window.DeviceDialog;
import com.hht.sharelib.bean.DeviceInfo;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.zhengsr.commonlib.LggUtils;
import com.zhengsr.drawlib.utils.ViewUtils;

import me.jessyan.autosize.AutoSizeCompat;

/**
 * created by @author zhengshaorui on 2019/8/23
 * Describe:
 */
public class MainFragment extends BaseFragment implements View.OnClickListener, DeviceDialog.InfoListener {

    private FrameLayout mShareBoardLy;
    private TextView mTextView;
    private DeviceInfo mCurrentInfo;

    public static MainFragment newInstance() {

        Bundle args = new Bundle();

        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main,container,false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        view.findViewById(R.id.main_exit).setOnClickListener(this);
        view.findViewById(R.id.main_setting).setOnClickListener(this);
        view.findViewById(R.id.main_choose_ly).setOnClickListener(this);
        mTextView = view.findViewById(R.id.device_tv);

        mShareBoardLy = view.findViewById(R.id.share_board_ly);
        mShareBoardLy.setOnClickListener(this);
        ViewUtils.setGray(mShareBoardLy,true, Color.GRAY,Color.WHITE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.main_exit:
                _mActivity.finish();
                break;
            case R.id.main_setting:
                start(SettingFragment.newInstance());
                break;
            case R.id.main_choose_ly:
                //防止适配失败
                AutoSizeCompat.autoConvertDensity(getResources(),360,true);
                new DeviceDialog(this,_mActivity,this,mCurrentInfo);
                break;
            case R.id.share_board_ly:
                if (mCurrentInfo != null) {
                    Intent intent = new Intent(_mActivity, DrawActivity.class);
                    intent.putExtra(Constans.DEVICE, mCurrentInfo);
                    startActivity(intent);
                }
                break;
            default:break;
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null != data) {
            Bundle bundle = data.getExtras();
            if (bundle == null) {
                return;
            }
            if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                String result = bundle.getString(CodeUtils.RESULT_STRING);
                mCurrentInfo  = JSON.parseObject(result,DeviceInfo.class);
                if (mCurrentInfo != null) {
                   updateStatus(mCurrentInfo,true);
                }else{
                    updateStatus(mCurrentInfo,false);
                }
            } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                Toast.makeText(_mActivity, R.string.rqcode_fail, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void getInfo(DeviceInfo info, boolean isCanPing) {
        mCurrentInfo = info;
        updateStatus(info, isCanPing);
    }

    private void updateStatus(DeviceInfo info, boolean isCanPing) {
        if (isCanPing){
            ViewUtils.setGray(mShareBoardLy,false, Color.GRAY,Color.WHITE);
            mTextView.setText(getString(R.string.device_describe,info.info,info.ip));
        }else{
            ViewUtils.setGray(mShareBoardLy,true, Color.GRAY,Color.WHITE);
            Toast.makeText(_mActivity, R.string.cannot_connect_server, Toast.LENGTH_SHORT).show();
        }
    }
}
