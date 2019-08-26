package com.zhengsr.drawlib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhengsr.commonlib.LggUtils;
import com.zhengsr.drawlib.R;

/**
 * created by zhengshaorui
 * time on 2019/7/11 
 */
public class BottomItem extends FrameLayout  {


    private TextView mTopTextView;
    private ImageView mImageView;
    private TextView mBottextView;
    private View mView;
    private int mBottomColor;
    private int mTopColor;
    private int mGrayColor;
    public BottomItem(Context context) {
       this(context,null);
   }

   public BottomItem(Context context, AttributeSet attrs) {
       this(context, attrs,0);
   }

   public BottomItem(Context context, AttributeSet attrs, int defStyleAttr) {
       super(context, attrs, defStyleAttr);

       mView = LayoutInflater.from(getContext()).inflate(R.layout.bottom_item,this,false);
       addView(mView);

       mImageView = mView.findViewById(R.id.bottom_item_img);
       mBottextView = mView.findViewById(R.id.bottom_item_text);
       mTopTextView = mView.findViewById(R.id.bottom_item_t_text);
       TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.BottomItem);
       int imgWidth = ta.getDimensionPixelSize(R.styleable.BottomItem_bt_img_width, 40);
       int height = ta.getDimensionPixelSize(R.styleable.BottomItem_bt_img_height, 40);
       int imgId = ta.getResourceId(R.styleable.BottomItem_bt_img_src, -1);

       String text = ta.getString(R.styleable.BottomItem_bt_txt_text);
       int txtsize = ta.getDimensionPixelSize(R.styleable.BottomItem_bt_txt_size,20);
       mBottomColor = ta.getColor(R.styleable.BottomItem_bt_txt_color, Color.RED);

       String t_text = ta.getString(R.styleable.BottomItem_bt_txt_t_text);
       int t_txtsize = ta.getDimensionPixelSize(R.styleable.BottomItem_bt_txt_t_size,20);
       mTopColor = ta.getColor(R.styleable.BottomItem_bt_txt_t_color, Color.RED);
       mGrayColor = ta.getColor(R.styleable.BottomItem_bt_notenable_color,Color.GRAY);
       boolean isGray = ta.getBoolean(R.styleable.BottomItem_bt_showgray,false);

       if (imgId != -1) {
           ViewGroup.LayoutParams params = mImageView.getLayoutParams();
           params.width = imgWidth;
           params.height = height;
           mImageView.setLayoutParams(params);
           mImageView.setImageResource(imgId);
       }else{
           mImageView.setVisibility(GONE);
           mTopTextView.setVisibility(VISIBLE);
       }


       mBottextView.setText(text);
       mBottextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,txtsize);
       mBottextView.setTextColor(mBottomColor);

       mTopTextView.setText(t_text);
       mTopTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,t_txtsize);
       mTopTextView.setTextColor(mTopColor);

       setBackgroundResource(R.drawable.bottom_item_selector);

       ta.recycle();


       if (isGray){
           setGray(true);
       }else{
           setClickable(true);
           setFocusable(true);
       }

   }

   public void updateTopText(String msg){
       if (mTopTextView.getVisibility() == VISIBLE){
           mTopTextView.setText(msg);
       }
   }

   public void setGray(boolean gray){
       if (gray) {
           setEnabled(false);
           mBottextView.setTextColor(mGrayColor);
           mTopTextView.setTextColor(mGrayColor);
           //取两层叠加
           mImageView.setColorFilter(mGrayColor, PorterDuff.Mode.MULTIPLY);
       } else {
           setEnabled(true);
           mBottextView.setTextColor(mBottomColor);
           mTopTextView.setTextColor(mTopColor);
           //取两层叠加
           mImageView.clearColorFilter();
       }
   }


}
