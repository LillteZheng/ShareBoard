package com.hht.client.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hht.client.R;
import com.zhengsr.commonlib.LggUtils;

import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

/**
 * created by @author zhengshaorui on 2019/8/23
 * Describe:
 */
public class SettingFragment extends BaseFragment implements View.OnClickListener {
    public static SettingFragment newInstance() {

        Bundle args = new Bundle();

        SettingFragment fragment = new SettingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultHorizontalAnimator();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting,container,false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        view.findViewById(R.id.setting_back).setOnClickListener(this);
        view.findViewById(R.id.check_version).setOnClickListener(this);
        view.findViewById(R.id.device_name).setOnClickListener(this);
        // 设备型号
        String deviceName = Build.MODEL;
        TextView textView = view.findViewById(R.id.setting_device_name);
        textView.setText(deviceName);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.setting_back:
                pop();
                break;
            case R.id.check_version:
                Toast.makeText(_mActivity, R.string.lastest_version, Toast.LENGTH_SHORT).show();
                break;
            case R.id.device_name:
                break;
            default:break;
        }
    }
}
