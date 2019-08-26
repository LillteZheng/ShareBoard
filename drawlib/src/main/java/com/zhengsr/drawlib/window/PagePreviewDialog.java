package com.zhengsr.drawlib.window;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zhengsr.commonlib.LggUtils;
import com.zhengsr.commonlib.window.CusDialog;
import com.zhengsr.commonlib.window.CusPopup;
import com.zhengsr.drawlib.DrawConfig;
import com.zhengsr.drawlib.R;
import com.zhengsr.drawlib.adapter.RBaseAdapter;
import com.zhengsr.drawlib.adapter.RBaseViewholder;
import com.zhengsr.drawlib.tool.ImageSaveBackground;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther by zhengshaorui on 2019/8/25
 * describe: 页面预览界面
 */
public class PagePreviewDialog {
    private Context mContext;
    public PagePreviewDialog(Context context){
        mContext = context;
        CusDialog dialog = new CusDialog.Builder()
                .setContext(context)
                .setLayoutId(R.layout.dialog_page_preview)
                .setWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                .setAnimation(R.style.PupAnim)
                .setOutSideDimiss(false)
                .showAlphaBg(true)
                .builder();

        initView(dialog);
    }

    private void initView(CusDialog dialog) {
        RecyclerView recyclerView = dialog.getViewbyId(R.id.page_preview_recy);
        List<Bitmap> bitmaps = ImageSaveBackground.getInstance().getBitmaps();

        List<PageBean> pageBeans = new ArrayList<>();
        for (Bitmap bitmap : bitmaps) {
            PageBean bean = new PageBean();
            bean.bitmap = bitmap;
            pageBeans.add(bean);
        }

        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
        PageAdapter adapter = new PageAdapter(R.layout.item_page_preview,pageBeans);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    class PageBean{
        public Bitmap bitmap;
        public boolean isChoosed;
    }

    class PageAdapter extends RBaseAdapter<PageBean>{

        public PageAdapter(int layoutid, List<PageBean> list) {
            super(layoutid, list);
        }

        @Override
        public void getConver(RBaseViewholder holder, PageBean data) {
            ImageView imageView = holder.getItemView(R.id.item_image_iv);
            Glide.with(mContext)
                    .load(data.bitmap)
                    .centerCrop()
                    .into(imageView);
        }
    }


}
