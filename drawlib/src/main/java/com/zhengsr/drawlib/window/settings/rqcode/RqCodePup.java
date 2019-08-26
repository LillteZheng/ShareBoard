package com.zhengsr.drawlib.window.settings.rqcode;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.hht.sharelib.bean.DeviceInfo;
import com.hht.sharelib.transtype.socket.TCPConstants;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.zhengsr.commonlib.window.CusPopup;
import com.zhengsr.drawlib.R;
import com.zhengsr.drawlib.bean.MenuBean;

/**
 * created by zhengshaorui on 2019/8/7
 * Describe:
 */
public class RqCodePup {

    public RqCodePup(MenuBean menuBean){
        final CusPopup cusPopup = new CusPopup.Builder()
                .setContext(menuBean.context)
                .setLayoutId(R.layout.pup_rqcode_layout)
                .setWidth(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setAnimationStyle(R.style.PupAnim)
                .setFouse(true)
                .setOutSideDimiss(true)
                .builder()
                .showAtLocation(menuBean.mainlayoutId, Gravity.CENTER,0,0);

        cusPopup.setOnClickListener(R.id.menu_bg_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cusPopup.dismiss();
            }
        });
        ImageView imageView = cusPopup.getViewbyId(R.id.pup_rq_iv);

        DeviceInfo bean = new DeviceInfo();
        bean.ip = menuBean.ip;
        bean.port = TCPConstants.PORT_SERVER;
        bean.info = "协同";
        String msg = JSON.toJSONString(bean);
        Bitmap bitmap  = CodeUtils.createImage(msg, 400, 400, null);
        imageView.setImageBitmap(bitmap);
    }


}
