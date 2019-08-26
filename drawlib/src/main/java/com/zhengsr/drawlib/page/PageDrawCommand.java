package com.zhengsr.drawlib.page;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;

import com.alibaba.fastjson.JSON;
import com.zhengsr.drawlib.DrawConfig;
import com.zhengsr.drawlib.DrawNotice;
import com.zhengsr.drawlib.action.BaseAction;
import com.zhengsr.drawlib.action.imp.BaseImp;
import com.zhengsr.drawlib.action.imp.EraseImp;
import com.zhengsr.drawlib.action.imp.GestureImp;
import com.zhengsr.drawlib.action.imp.PenMultisImp;
import com.zhengsr.drawlib.callback.StatusListener;
import com.zhengsr.drawlib.callback.TransListener;
import com.zhengsr.drawlib.data.Command;
import com.zhengsr.drawlib.bean.TransBean;
import com.zhengsr.drawlib.tool.ImageSaveBackground;
import com.zhengsr.drawlib.trans.TransManager;
import com.zhengsr.drawlib.type.DrawActionType;
import com.zhengsr.drawlib.type.SizeType;
import com.zhengsr.drawlib.type.PenType;
import com.zhengsr.drawlib.view.DrawSurface;
import com.zhengsr.drawlib.view.WhiteBoardView;

import java.util.Map;

/**
 * created by zhengshaorui on 2019/7/11
 * Describe:
 */
public class PageDrawCommand implements BaseImp.DrawListener {

    private DrawSurface mView;
    private WhiteBoardView mWhiteBoradView;
    private BaseImp mBaseImp;
    private final PageControl mPageControl;
    private StatusListener mListener;
    private TransListener mTransListener;
    private Activity mActivity;
    public PageDrawCommand(Activity activity, WhiteBoardView view, DrawActionType drawType, TransListener listener) {
        mActivity = activity;
        mWhiteBoradView = view;
        this.mView = view.getDrawSurface();
        mView.setDrawState(this);
        mTransListener = listener;
        setDrawType(drawType);
        mPageControl = new PageControl(0, mView);
        TransManager.getInstance().addTransListener(listener).config(this,mView,mPageControl);

    }
    public void addStatusListener(StatusListener listener){
        mListener = listener;
        mPageControl.addStatusListener(listener);
    }



    public boolean onTouchEvent(MotionEvent event){
        return mBaseImp.onTouchEvent(event);
    }

    public void setDrawType(DrawActionType type){
        mWhiteBoradView.useGesture(type);
        switch (type){
            case PEN:
                mBaseImp = new PenMultisImp();
                mBaseImp.addDrawListener(this);
                break;
            case ERASE:
                mBaseImp = new EraseImp();
                mBaseImp.setBgBitmap(mView.getBg());
                mBaseImp.addDrawListener(this);
                break;
            case GESTURE:
                GestureImp gestureImp = new GestureImp();
                mBaseImp = gestureImp;
                mBaseImp.addDrawListener(this);
                gestureImp.setWhiteBoradView(mWhiteBoradView);
                break;
            default:break;
        }
        mBaseImp.setContext(mView.getContext());
        mBaseImp.setCanvas(mView.getCanvas());


    }


    public void redo(boolean trans) {
        Command.getInstance().redo();
        mView.reDraw(true);
        noticeStatus();
        if (trans) {
            sendSimpleTransCommand(DrawActionType.REDO);
        }
    }


    public void eraseSize(int size) {
        mBaseImp.setEraseSize(size);
    }


    public void clear(boolean trans) {
        //重置为画笔
        Command.getInstance().clear();
        setDrawType(DrawActionType.PEN);
        mView.clear();
        if (mListener != null){
            mListener.stateNotice(DrawNotice.CLEAR);
        }
        if (trans) {
            sendSimpleTransCommand(DrawActionType.CLEAR);
        }
    }

    public void undo(boolean trans){
        Command.getInstance().undo();
        mView.reDraw(true);
        noticeStatus();
        if (trans) {
            sendSimpleTransCommand(DrawActionType.UNDO);
        }
    }



    public BaseImp getBaseImp() {
        return mBaseImp;
    }

    public void penSize(int size){
        int penSize ;
        if (size == SizeType.SMALL.ordinal()){
            penSize = DrawConfig.PEN_SMALL_SIZE;
        }else if (size == SizeType.MIDDLE.ordinal()){
            penSize = DrawConfig.PEN_MID_SIZE;
        }else{
            penSize = DrawConfig.PEN_LARGE_SIZE;
        }
        mBaseImp.setPenSize(penSize);
    }

    public void penColor(int color){
        mBaseImp.setPenColor(color);
    }


    public void createNewPage(boolean sendTrans){
        mPageControl.createNewPage();
        //创建新的一页，需要重置为画笔
        setDrawType(DrawActionType.PEN);
        if (mListener != null) {
            mListener.stateNotice(DrawNotice.CREATE_NEW_PAGE);
        }
        if (sendTrans) {
            sendSimpleTransCommand(DrawActionType.NEW_PAGE);
        }
        noticeStatus();

        ImageSaveBackground.getInstance().saveBackground();
    }




    public void gotoNextPage(boolean sendTrans){
        //需要重置为画笔
        setDrawType(DrawActionType.PEN);
        mPageControl.goNextPage();
        noticeStatus();
        if (sendTrans) {
            sendSimpleTransCommand(DrawActionType.NEXT_PAGE);
        }

        mWhiteBoradView.getBackView().setBackground(new BitmapDrawable(DrawConfig.getParams().getBg()));
        ImageSaveBackground.getInstance().saveBackground();
    }

    public void gotoPrePage(boolean sendTrans){
        //需要重置为画笔
        setDrawType(DrawActionType.PEN);
        mPageControl.goPrePage();
        noticeStatus();
        if (sendTrans) {
            sendSimpleTransCommand(DrawActionType.PRE_PAGE);
        }
        mWhiteBoradView.getBackView().setBackground(new BitmapDrawable(DrawConfig.getParams().getBg()));
        ImageSaveBackground.getInstance().saveBackground();
    }


    private void sendSimpleTransCommand(DrawActionType type){
        TransBean bean = new TransBean();
        bean.drawaAtionType = type.ordinal();
        String msg = JSON.toJSONString(bean);
        mTransListener.sendTransData(msg);
    }


    public int getCurrentPage(){
        return mPageControl.getCurrentPage()+1;
    }

    public int getRealPageId(){
        return mPageControl.getCurrentPage();
    }



    public int getAllPage(){
        return mPageControl.getAllPage();
    }

    public int getCurrentColor() {
        return mBaseImp.getPenColor();
    }
    public int getCurrentPenSize(){
        return mBaseImp.getPenSize();
    }

    public int getCurrentEraseSize(){
        return mBaseImp.getEraseSize();
    }

    public PenType getCurrentPenType() {
        return mBaseImp.getPenType();
    }
    public void setPenType(int type){
        mBaseImp.setPenType(PenType.values()[type]);
    }


    public void gesture() {
        setDrawType(DrawActionType.GESTURE);
    }

    public void saveBitmap() {
        mView.saveBitmap();
    }


    public void reCovery() {
       /* Command.getInstance().getActions().addAll();
        mView.postDraw(true);
        ResourceDataManager.getInstance().clear();
        noticeStatus();*/
    }

    @Override
    public void onAction(Map<Integer, BaseAction> actions, boolean isRedraw) {
        if (isRedraw) {
            mView.postDraw(null,true);
            //每次都保存一下数据,方便分页
            PageDataManager.getInstance().addNote(mPageControl.getCurrentPage(),Command.getInstance().getActions());
            noticeStatus();
        } else {
            mView.postDraw(actions, true);
        }
    }



    @Override
    public void sendTrans(final TransBean bean) {
        String msg = JSON.toJSONString(bean);
        if (mTransListener != null){
            mTransListener.sendTransData(msg);
        }
    }

    public void noticeStatus() {
        if (mListener != null){
            if (!Command.getInstance().isCanRedo()){
                mListener.stateNotice(DrawNotice.CAN_NOT_REDO);
            }else{
                mListener.stateNotice(DrawNotice.CAN_REDO);
            }

            if (!Command.getInstance().isCanUndo()){
                mListener.stateNotice(DrawNotice.CAN_NOT_UNDO);
            }else{
                mListener.stateNotice(DrawNotice.CAN_UNDO);
            }

            if (mPageControl.isHasNextPage()){
                mListener.stateNotice(DrawNotice.HAS_NEXT_PAGE);
            }else{
                mListener.stateNotice(DrawNotice.NO_NEXT_PAGE);
            }
            if (mPageControl.isHasPrePage()){
                mListener.stateNotice(DrawNotice.HAS_PRE_PAGE);
            }else{
                mListener.stateNotice(DrawNotice.NO_PRE_PAGE);
            }

            DrawConfig.getParams().curPageId(mPageControl.getCurrentPage());
            mWhiteBoradView.changeBackground(DrawConfig.getParams().getBg());


        }
    }

    public void changeBackground(int index) {
        mWhiteBoradView.changeBackground(index);
        noticeStatus();
    }

    public void changeBackground(String path) {
        mWhiteBoradView.changeBackground(path);
        noticeStatus();
    }


    public void insertImage(String path) {
        mWhiteBoradView.insertImage(path);
        noticeStatus();
        mListener.stateNotice(DrawNotice.INSERT_IMAGE);
        setDrawType(DrawActionType.GESTURE);
    }

    /**
     * 退出
     */
    public void exit(boolean sendTrans) {
        if (sendTrans) {
            sendSimpleTransCommand(DrawActionType.EXIT);
        }else{
            mActivity.finish();
        }
    }
}
