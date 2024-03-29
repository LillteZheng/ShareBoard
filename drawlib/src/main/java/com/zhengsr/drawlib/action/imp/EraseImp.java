package com.zhengsr.drawlib.action.imp;

import android.graphics.Canvas;
import android.view.MotionEvent;

import com.zhengsr.drawlib.bean.TransBean;
import com.zhengsr.drawlib.data.Command;
import com.zhengsr.drawlib.action.BaseAction;
import com.zhengsr.drawlib.action.EraseAction;
import com.zhengsr.drawlib.trans.ResourceDataManager;
import com.zhengsr.drawlib.trans.TransManager;
import com.zhengsr.drawlib.type.DrawActionType;
import com.zhengsr.drawlib.type.EventType;

/**
 * created by zhengshaorui on 2019/7/11
 * Describe: 橡皮擦类，只支持单指，也可以多指，注释部分打开即可
 */
public class EraseImp extends BaseImp {

    private Canvas mCanvas;
    private ResourceDataManager mTransPathManager;
    public EraseImp() {
        mTransPathManager = ResourceDataManager.getInstance();
    }

    @Override
    public void setCanvas(Canvas canvas) {
        mCanvas = canvas;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (TransManager.getInstance().isResponsing()){
            TransManager.getInstance().sendResponseConflict();
            return true;
        }
        switch (event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                setAction(event);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
               // setAction(event);
                break;
            case MotionEvent.ACTION_MOVE:
                int count = event.getPointerCount();
                for (int i = 0; i < count; i++) {
                    int pointerId = event.getPointerId(i);
                    float x = event.getX(i);
                    float y = event.getY(i);
                    BaseAction action = Command.getInstance().getMultiAction(pointerId);
                    if (action != null) {
                        action.move(x, y, pointerId);
                        mTransPathManager.lineMove(x,y,pointerId);

                        TransBean upbean = getTransBean(DrawActionType.ERASE);
                        upbean.pointId = pointerId;
                        upbean.x = x;
                        upbean.y = y;
                        upbean.eventType = EventType.MOVE.ordinal();
                        sendTransData(upbean);
                    }
                }
                sendDraw(false);
                break;

            case MotionEvent.ACTION_UP:
                int pointid = event.getPointerId(event.getActionIndex());
                BaseAction action = Command.getInstance().getMultiAction(pointid);
                if (action != null) {
                    float x = event.getX(event.getActionIndex());
                    float y = event.getY(event.getActionIndex());
                    action.up(x,y,pointid);
                    action.draw(mCanvas);
                    mTransPathManager.lineUp(x,y,pointid);

                    Command.getInstance().removeMultiAction(pointid);

                    TransBean upbean = getTransBean(DrawActionType.ERASE);
                    upbean.pointId = pointid;
                    upbean.x = x;
                    upbean.y = y;
                    upbean.eventType = EventType.UP.ordinal();
                    sendTransData(upbean);

                }
                sendDraw(true);
                Command.getInstance().clearMultiAction();
              //  sendDraw();
                break;
            default:break;
        }



        return true;
    }


    private void sendDraw(boolean isRedraw){
        if (mDrawListener != null){
            mDrawListener.onAction(Command.getInstance().getMultiAction(),isRedraw);
        }
    }

    /**
     * 每次down事件都是一个 action
     * @param event
     * @return
     */
    private BaseAction setAction(MotionEvent event) {
        EraseAction action = new EraseAction();
        int pointerId = event.getPointerId(event.getActionIndex());
        int actionIndex = event.getActionIndex();
        float x = event.getX(actionIndex);
        float y = event.getY(actionIndex);

       // int width = (int) event.getTouchMajor();
      //  width = width * 2 + getEraseSize();
        action.setbgBitmap(getBgBitmap());
        action.setEraseSize(getEraseSize());
        action.down(x,y,pointerId);
        Command.getInstance().putMultiAction(pointerId,action);
        Command.getInstance().add(action);
        mTransPathManager.lineStart(x,y,pointerId, DrawActionType.ERASE.ordinal(),-1,
                getPenColor(),getEraseSize());

        TransBean bean = getTransBean(DrawActionType.ERASE);
        bean.x = x;
        bean.y = y;
        bean.pointId = pointerId;
        bean.eventType  = EventType.DOWN.ordinal();
        sendTransData(bean);
        sendDraw(false);
        return action;
    }







}
