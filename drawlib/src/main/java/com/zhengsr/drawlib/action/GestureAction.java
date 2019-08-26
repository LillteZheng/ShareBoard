package com.zhengsr.drawlib.action;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Looper;
import android.view.View;

import com.zhengsr.commonlib.LggUtils;
import com.zhengsr.drawlib.bean.GestureBean;
import com.zhengsr.drawlib.view.GestureImageView;

/**
 * created by zhengshaorui on 2019/8/2
 * Describe:
 */
public class GestureAction extends BaseAction {
    private Matrix mMatrix;
    private GestureImageView mGestureImageView;
    private boolean isFirstadd = false;
    private GestureBean mBean;
    boolean isHide;
    @Override
    public void down(float x, float y, int pointId) {

    }

    @Override
    public void move(float x, float y, int pointId) {

    }

    @Override
    public void up(float x, float y, int pointId) {
        if (mGestureImageView != null) {
            mMatrix = new Matrix(mGestureImageView.getCurrentMatrix());
        }
    }

    public void addBean(GestureBean bean){
        mBean = bean;
        if (mGestureImageView != null) {
            isFirstadd = true;
            mMatrix = new Matrix(mGestureImageView.getCurrentMatrix());
        }
    }

    @Override
    public void draw(Canvas canvas) {

    }

    public void reDraw(){
        if (mGestureImageView != null ) {
            if (mBean.isDelete){
                mGestureImageView.setVisibility(View.GONE);
            }else {
                mGestureImageView.setVisibility(View.VISIBLE);
            }
            if (mMatrix != null) {
                mGestureImageView.setMatrix(mMatrix);
                mGestureImageView.setImageMatrix(mMatrix);
            }
            mGestureImageView.reCaculateDeleteRect();
            mGestureImageView.invalidate();
        }
    }

    public void remove(){
        mGestureImageView.setVisibility(View.GONE);
    }

    public void setGestureView(GestureImageView imageView){
        mGestureImageView = imageView;
    }

    public void setHide(boolean isHide){
        /*if (mGestureImageView != null){
            mGestureImageView.setHide(isHide);
        }*/
        this.isHide = isHide;
    }


    public boolean isHide(){
        return isHide;
    }



    public GestureBean getBean() {
        return mBean;
    }

    public Matrix getMatrix() {
        return mMatrix;
    }
}
