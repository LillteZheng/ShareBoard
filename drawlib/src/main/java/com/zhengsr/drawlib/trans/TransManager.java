package com.zhengsr.drawlib.trans;

import android.os.Handler;
import android.os.Looper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhengsr.commonlib.LggUtils;
import com.zhengsr.drawlib.action.BaseAction;
import com.zhengsr.drawlib.action.EraseAction;
import com.zhengsr.drawlib.action.PenAction;
import com.zhengsr.drawlib.bean.TransBean;
import com.zhengsr.drawlib.callback.TransListener;
import com.zhengsr.drawlib.data.Command;
import com.zhengsr.drawlib.page.PageControl;
import com.zhengsr.drawlib.page.PageDrawCommand;
import com.zhengsr.drawlib.page.PageDataManager;
import com.zhengsr.drawlib.type.DrawActionType;
import com.zhengsr.drawlib.type.EventType;
import com.zhengsr.drawlib.type.PenType;
import com.zhengsr.drawlib.view.DrawSurface;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * created by zhengshaorui on 2019/7/19
 * Describe: 传输管理类
 */
public class TransManager {
    private TransListener mTransListener;
    private DrawSurface mView;
    private PageDrawCommand mPageDrawStutas;
    static Handler mHandler = new Handler(Looper.getMainLooper());
    private PageControl mPageControl;
    private boolean isResponsing = false;
    private TransManager(){}
    private static class Holder{
        static TransManager HODLER = new TransManager();
    }

    public static TransManager getInstance(){
        return Holder.HODLER;
    }

    public void config(PageDrawCommand drawStutas, DrawSurface view, PageControl pageControl){
        mPageDrawStutas = drawStutas;
        mPageControl = pageControl;
        mView = view;

    }
    public TransManager addTransListener(TransListener listener){
        mTransListener = listener;
        return this;
    }



    /**
     * 传输响应
     * @param response
     */
    public void onTransResponse(final String response){
        parseTransBean(response);
    }

    private void actionDown(BaseAction action,TransBean bean){

        action.down(bean.x, bean.y, bean.pointId);
        mMultiAction.put(bean.pointId, action);
        mView.postDraw(mMultiAction, false);
        Command.getInstance().add(action);
    }

    private HashMap<Integer,BaseAction> mMultiAction = new LinkedHashMap<>();
    private void parseTransBean(String response){
        if (checkSimpleCommand(response)){
            return;
        }
        TransBean bean = JSON.parseObject(response,TransBean.class);

        LggUtils.d("bean: "+bean.toString());
        DrawActionType tranType = DrawActionType.values()[bean.drawaAtionType];
        EventType eventype = EventType.values()[bean.eventType];
        BaseAction penAction;
        switch (eventype){
            case DOWN:
                isResponsing = true;
                if (DrawActionType.PEN == tranType){
                    PenType penType = PenType.values()[bean.penType];
                    if (penType == PenType.PEN) {
                        BaseAction action = new PenAction();
                        action.setPenSize(bean.paintSize);
                        action.setPenColor(bean.penColor);
                        actionDown(action,bean);
                    }else if(penType == PenType.LIGHT_PEN){
                        BaseAction action = new PenAction();
                        action.setPenSize(bean.paintSize);
                        action.setPenColor(bean.penColor);
                        actionDown(action,bean);
                    }
                }
                if (DrawActionType.ERASE == tranType){
                    EraseAction eraseAction = new EraseAction();
                    eraseAction.setbgBitmap(mView.getBg());
                    eraseAction.setEraseSize(bean.paintSize);
                    actionDown(eraseAction,bean);
                }

                break;
            case MOVE:
                isResponsing = true;
                penAction = mMultiAction.get(bean.pointId);
                if (penAction != null) {
                    penAction.move(bean.x, bean.y, bean.pointId);
                }
                //使用线程去画
                mView.postDraw(mMultiAction, true);
                break;
            case POINT_UP:
                penAction = mMultiAction.get(bean.pointId);
                if (penAction != null) {
                    penAction.up(bean.x, bean.y, bean.pointId);
                    penAction.draw(mView.getCanvas());
                }
                break;
            case UP:
                isResponsing = false;
                penAction = mMultiAction.get(bean.pointId);
                if (penAction != null) {
                    penAction.up(bean.x, bean.y, bean.pointId);
                    penAction.draw(mView.getCanvas());
                }
                mView.postDraw(null, true);
                //每次都保存一下数据,方便分页
                PageDataManager.getInstance().addNote(mPageControl.getCurrentPage(), Command.getInstance().getActions());
                mMultiAction.clear();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mPageDrawStutas.noticeStatus();
                    }
                });
                break;
            default:break;

        }
    }

    private boolean checkSimpleCommand(final String response){
        JSONObject jsonObject = JSON.parseObject(response);
        int typedValue = jsonObject.getIntValue("drawaAtionType");
        final DrawActionType type = DrawActionType.values()[typedValue];
        if (DrawActionType.PEN == type || DrawActionType.ERASE == type){
            return false;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                switch (type){
                    case CLEAR:
                        mPageDrawStutas.clear(false);
                        break;
                    case REDO:
                        mPageDrawStutas.redo(false);
                        break;
                    case UNDO:
                        mPageDrawStutas.undo(false);
                        break;
                    case NEW_PAGE:
                        mPageDrawStutas.createNewPage(false);
                        break;
                    case PRE_PAGE:
                        mPageDrawStutas.gotoPrePage(false);
                        break;
                    case NEXT_PAGE:
                        mPageDrawStutas.gotoNextPage(false);
                        break;
                    case EXIT :
                        mPageDrawStutas.exit(false);
                        break;
                    default:
                        break;
                }
            }
        });
        return true;

    }


    public boolean isResponsing(){
        return isResponsing;
    }

    /**
     * 传输冲突
     */
    public void sendResponseConflict(){
        if (mTransListener != null){
            mTransListener.sendResponseConflict();
        }
    }



}
