package com.zhengsr.drawlib.action.imp;

import android.graphics.Canvas;
import android.view.MotionEvent;

import com.zhengsr.commonlib.LggUtils;
import com.zhengsr.drawlib.action.GestureAction;
import com.zhengsr.drawlib.bean.GestureBean;
import com.zhengsr.drawlib.data.Command;
import com.zhengsr.drawlib.view.GestureImageView;
import com.zhengsr.drawlib.view.WhiteBoardView;

/**
 * @auther by zhengshaorui on 2019/7/22
 * describe: 手势处理类
 */
public class GestureImp extends BaseImp {
    private GestureImageView mCurrentImageView;
    private WhiteBoardView mWhiteBoardView;
    public GestureImp(){

    }

    public void setWhiteBoradView(WhiteBoardView whiteBoradView) {
        mWhiteBoardView = whiteBoradView;
        GestureImageView lastestView = mWhiteBoardView.getLastestView();
        if (lastestView != null){
            GestureAction action = new GestureAction();
            action.setGestureView(lastestView);
            GestureBean bean = new GestureBean();
            bean.isFristAdd = true;
            bean.index =  mWhiteBoardView.getBackView().getChildCount();;
            action.addBean(bean);
            Command.getInstance().add(action);
            if (mDrawListener != null){
                mDrawListener.onAction(null,true);
            }
        }
    }

    @Override
    public void setCanvas(Canvas canvas) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mCurrentImageView = mWhiteBoardView.clickOnView(event);
        }
        if (mCurrentImageView != null) {
            mCurrentImageView.setOnTouchEvent(event);
        }
        if (event.getAction() == MotionEvent.ACTION_UP){
            if (mCurrentImageView != null && mCurrentImageView.isMove()) {
                GestureAction action = new GestureAction();
                action.setGestureView(mCurrentImageView);
                action.up(-1, -1, -1);
                GestureBean bean = new GestureBean();
                bean.index = mCurrentImageView.getImgBean().index;
                action.addBean(bean);
                Command.getInstance().add(action);
                if (mDrawListener != null) {
                    mDrawListener.onAction(Command.getInstance().getMultiAction(), true);
                }

                mCurrentImageView = null;
            }
        }

        return true;
    }
}
