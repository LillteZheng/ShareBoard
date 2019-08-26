package com.zhengsr.drawlib.window;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.zhengsr.commonlib.adapter.LGAdapter;
import com.zhengsr.commonlib.adapter.LGViewHolder;
import com.zhengsr.commonlib.window.CusPopup;
import com.zhengsr.drawlib.DrawConfig;
import com.zhengsr.drawlib.R;
import com.zhengsr.drawlib.bean.PenConfig;
import com.zhengsr.drawlib.callback.PenConfigListener;
import com.zhengsr.drawlib.type.PenType;
import com.zhengsr.drawlib.utils.BgUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * created by zhengshaorui on 2019/7/11
 * Describe: 显示画笔设置属性
 */
public class PenPupwindow implements AdapterView.OnItemClickListener{
    public static final int ROUNDSIZE = 5;
    private int[] COLORS = new int[]{
            R.color.draw_red_color,R.color.draw_green_color,R.color.draw_white_color,
            R.color.draw_black_color,R.color.draw_blue_color,R.color.draw_yellow_color
    };
    private List<Pen> mPenColors = new ArrayList<>();;
    private Context mContext;
    private ViewGroup mPenSizeView;
    private CusPopup mCusPopup;
    private PenConfigListener mListener;

    public static PenPupwindow create(){
        return new PenPupwindow();
    }

    private PenPupwindow(){
    }

    public PenPupwindow conntext(Context context){
        mContext = context;
        return this;
    }
    public PenPupwindow view(View view){
        mCusPopup = new CusPopup.Builder()
                .setContext(mContext)
                .setLayoutId(R.layout.pup_pen_layout)
                // .setWidth(context.getResources().getDimensionPixelSize(R.dimen.pen_pup_width))
                .setWidth(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setHeight(mContext.getResources().getDimensionPixelSize(R.dimen.pen_pup_height))
                .setOutSideDimiss(true)
                .setAnimationStyle(R.style.PupAnim)
                .builder()
                .showAsLaction(view, Gravity.CENTER,0,10);
        return this;
    }

    public PenPupwindow color(int defaultColor){
        initColorGridview(defaultColor, mCusPopup);
        initCurrentColor(mCusPopup,defaultColor);
        return this;
    }

    public PenPupwindow penSize(int penSize){
        configPenSize(mCusPopup,penSize);
        return this;
    }
    public PenPupwindow penType(PenType type){
        configPenType(type);
        return this;
    }



    public PenPupwindow listener(PenConfigListener listener){
        mListener = listener;
        return this;
    }



    private void configPenType(PenType type) {
        final ViewGroup viewGroup =  mCusPopup.getViewbyId(R.id.pen_type_pup_ly);
        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = viewGroup.getChildAt(i);
            final int index = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetSelect(0,viewGroup);
                    v.setSelected(true);
                    if (mListener != null){
                        PenConfig config = new PenConfig();
                        config.penType = index;
                        mListener.penConfig(config);
                    }
                }
            });
        }
        if (type == PenType.PEN){
            viewGroup.getChildAt(0).setSelected(true);
        }else if (type == PenType.BRUSH){
            viewGroup.getChildAt(1).setSelected(true);
        }else if (type == PenType.LIGHT_PEN){
            viewGroup.getChildAt(2).setSelected(true);
        }
    }

    /**
     * 初始化画笔大小
     * @param cusPopup
     * @param penSize
     */
    private void configPenSize(CusPopup cusPopup, int penSize) {
        mPenSizeView = cusPopup.getViewbyId(R.id.pen_pup_pensize_ly);

        int count = mPenSizeView.getChildCount();
        for (int i = 1; i < count; i++) {
            View view = mPenSizeView.getChildAt(i);
            final int index = i-1;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetSelect(1,mPenSizeView);
                    v.setSelected(true);
                    if (mListener != null){
                        PenConfig config = new PenConfig();
                        config.pensize = index;
                        mListener.penConfig(config);
                    }
                }
            });
        }
        if (penSize == DrawConfig.PEN_SMALL_SIZE){
            mPenSizeView.getChildAt(1).setSelected(true);
        }else if (penSize == DrawConfig.PEN_MID_SIZE){
            mPenSizeView.getChildAt(2).setSelected(true);
        }else if (penSize == DrawConfig.PEN_LARGE_SIZE){
            mPenSizeView.getChildAt(3).setSelected(true);
        }

    }
    private void resetSelect(int index,ViewGroup viewGroup){
        int count = viewGroup.getChildCount();
        for (int i = index; i < count; i++) {
            View view = viewGroup.getChildAt(i);
            view.setSelected(false);
        }

    }

    /**
     * 初始化默认颜色
     * @param cusPopup
     * @param defaultColor
     */
    private void initCurrentColor(CusPopup cusPopup,int defaultColor) {
        ImageView imageView = cusPopup.getViewbyId(R.id.pen_pup_c_color);
        Drawable drawable = BgUtils.drawRoundBg(defaultColor,ROUNDSIZE);
        imageView.setBackground(drawable);
    }

    /**
     * 初始化默认颜色表
     * @param defaultColor
     * @param cusPopup
     */
    private void initColorGridview(int defaultColor, CusPopup cusPopup) {
        mPenColors.clear();
        for (int color : COLORS) {
            Pen pen = new Pen();
            pen.drawable = BgUtils.drawRoundBg(mContext.getResources().getColor(color),ROUNDSIZE);
            pen.resId = color;
            if (defaultColor == mContext.getResources().getColor(color)){
                pen.isSlected = true;
            }
            mPenColors.add(pen);
        }
        GridView colorGridView = cusPopup.getViewbyId(R.id.pen_color_pup_grid);
        colorGridView.setAdapter(new PenColorAdapter(mPenColors,R.layout.item_pen_colors));
        colorGridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Pen pen = mPenColors.get(position);
        int color = ContextCompat.getColor(mContext,pen.resId);
        initCurrentColor(mCusPopup,color);
        PenConfig config = new PenConfig();
        if (mListener != null){
            if (color == -1) {
                config.penColor = Color.WHITE;
            }else{
                config.penColor = color;
            }
            mListener.penConfig(config);
        }
    }


    class Pen{
        public int resId;
        public GradientDrawable drawable;
        public boolean isSlected = false;
    }



    class PenColorAdapter extends LGAdapter<Pen>{

        public PenColorAdapter(List<Pen> datas, int layoutid) {
            super(datas, layoutid);
        }

        @Override
        public void convert(LGViewHolder viewHolder, Pen data, int position) {
            ImageView imageView = viewHolder.getView(R.id.item_pen_color_img);
            imageView.setBackground(data.drawable);
        }
    }
}
