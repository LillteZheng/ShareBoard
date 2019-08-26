package com.zhengsr.drawlib.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.zhengsr.commonlib.LggUtils;
import com.zhengsr.drawlib.DrawConfig;
import com.zhengsr.drawlib.R;
import com.zhengsr.drawlib.action.GestureAction;
import com.zhengsr.drawlib.bean.GestureBean;
import com.zhengsr.drawlib.data.Command;
import com.zhengsr.drawlib.type.DrawActionType;
import com.zhengsr.drawlib.utils.BgUtils;

/**
 * created by zhengshaorui on 2019/7/30
 * Describe:
 */
public class WhiteBoardView extends FrameLayout {

    private DrawSurface mDrawSurface;
    private FrameLayout mFrameLayout;
    private boolean mUseGesture;

    public WhiteBoardView(Context context) {
        this(context,null);
    }
    
    public WhiteBoardView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    
    public WhiteBoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClickable(true);
        View view = LayoutInflater.from(context).inflate(R.layout.content_layout,this,false);
        removeAllViews();
        addView(view);

        mFrameLayout = view.findViewById(R.id.draw_content);
        mDrawSurface = view.findViewById(R.id.drawsurface);
        int widthPixels = getResources().getDisplayMetrics().widthPixels;
        int heightPixels = getResources().getDisplayMetrics().heightPixels;

        Bitmap bitmap = BgUtils.getChooseBitmap(context,0,widthPixels,heightPixels);
        mFrameLayout.setBackground(new BitmapDrawable(bitmap));
        mDrawSurface.setBackfroundView(mFrameLayout);
        DrawConfig.getParams().setBg(bitmap).backview(mFrameLayout);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        mDrawSurface.setOnTouchEvent(event);
        return true;
    }



    public DrawSurface getDrawSurface() {
        return mDrawSurface;
    }

    public void changeBackground(int index) {
        Bitmap bitmap = BgUtils.getChooseBitmap(getContext(),index,1920,1080);
        mFrameLayout.setBackground(new BitmapDrawable(getResources(),bitmap));
    }

    public void changeBackground(Bitmap bitmap) {
        mFrameLayout.setBackground(new BitmapDrawable(getResources(),bitmap));
    }

    public void changeBackground(String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        mFrameLayout.setBackground(new BitmapDrawable(getResources(),bitmap));
    }

    public void insertImage(String path) {
        int childCount = mFrameLayout.getChildCount();
        GestureImageView imageview = new GestureImageView(getContext());
        imageview.configBitmap(
                path,
                getDimen(R.dimen.insert_image_width),
                getDimen(R.dimen.insert_image_height),
                DrawConfig.getParams().getCurPageId(),
                200 + childCount * 10,
                200 + childCount * 10,
                mFrameLayout.getChildCount()+1
                );
        mFrameLayout.addView(imageview);
        //更新一下
        DrawConfig.getParams().backview(mFrameLayout);
    }

    private int getDimen(int dimenId){
        return getContext().getResources().getDimensionPixelSize(dimenId);
    }


    /**
     * 是否使用手势
     * @param type
     */
    public void useGesture(DrawActionType type){
        if (DrawActionType.GESTURE == type){
            mUseGesture = true;
        }else{
            mUseGesture = false;
            resetStatus();
        }
    }

    public GestureImageView clickOnView(MotionEvent event){
        int childCount = mFrameLayout.getChildCount();
        resetStatus();
        for (int i = childCount - 1; i > -1; i--) {
            GestureImageView view = (GestureImageView) mFrameLayout.getChildAt(i);
            if (view.isCanDelete(event)){
                GestureAction action = new GestureAction();
                action.setHide(true);
                view.setVisibility(GONE);
                action.setGestureView(view);
                GestureBean bean = new GestureBean();
                bean.isDelete = true;
                bean.index = view.getImgBean().index;
                action.addBean(bean);
                Command.getInstance().add(action);
                return null;
            }
            if (view.isCanClick(event)){
                return view;
            }
        }
        return null;
    }

    /**
     * 拿到最新的background image
     * @return
     */
    public GestureImageView getLastestView(){
        int childCount = mFrameLayout.getChildCount();
        if (childCount > 0){
            return (GestureImageView) mFrameLayout.getChildAt(childCount - 1);
        }

        return null;
    }

    /**
     * 重新恢复状态
     */
    private void resetStatus(){
        int childCount = mFrameLayout.getChildCount();
        if (childCount > 0) {
            for (int i = childCount - 1; i > -1; i--) {
                GestureImageView view = (GestureImageView) mFrameLayout.getChildAt(i);
                view.resetStatus();
            }
        }
    }

    public FrameLayout getBackView(){
        return mFrameLayout;
    }
}
