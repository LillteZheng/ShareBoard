package com.zhengsr.drawlib.data;


import com.zhengsr.drawlib.action.BaseAction;
import com.zhengsr.drawlib.action.GestureAction;
import com.zhengsr.drawlib.bean.GestureBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Command {
    private static final String TAG = "Command";
    /**
     * 专门给多指用的,用线程安全的map
     */
    private Map<Integer,BaseAction> mMultiAction = new ConcurrentHashMap<>();
    private List<BaseAction> mUndoActions = new ArrayList<>();
    private List<BaseAction> mRedoActions = new ArrayList<>();

    /**
     * 专门给图片用的list
     */
    private List<BaseAction> mGestureUndoAction = new ArrayList<>();
    private List<BaseAction> mImageRedoActions = new ArrayList<>();
    private Command(){}

    private static class Holder{
        static Command HODLER = new Command();
    }

    public static Command getInstance(){
        return Holder.HODLER;
    }


    public void add(BaseAction action){
        mUndoActions.add(action);
    }

    public void putMultiAction(int pointId, BaseAction action){
        mMultiAction.put(pointId,action);
    }
    public BaseAction getMultiAction(int pointId){
        return mMultiAction.get(pointId);
    }
    public void removeMultiAction(int pointId){
        BaseAction action = mMultiAction.get(pointId);
        if (action != null){
            mMultiAction.remove(pointId);
        }
    }

    public void redo() {
        if (mRedoActions.size() > 0) {
            int index = mRedoActions.size() - 1;
            BaseAction action = mRedoActions.get(index);
            if (action instanceof GestureAction && !mImageRedoActions.isEmpty()){
                int imageIndex = mImageRedoActions.size() - 1;
                ((GestureAction) action).reDraw();
                mImageRedoActions.remove(imageIndex);

            }
            mUndoActions.add(action);
            mRedoActions.remove(index);
        }
    }
    public void undo(){
        if (mUndoActions.size() > 0) {
            int index = mUndoActions.size() - 1;
            BaseAction action = mUndoActions.get(index);
            if (action instanceof GestureAction){
                handleUndoGesture((GestureAction) action);
            }
            mRedoActions.add(action);
            mUndoActions.remove(index);
        }

    }



    public boolean isCanRedo(){
        return mRedoActions.size() > 0;
    }

    public boolean isCanUndo(){
        return mUndoActions.size() > 0;
    }
    public void clear(){
        mUndoActions.clear();
        mRedoActions.clear();
        mMultiAction.clear();
    }
    public void clearMultiAction(){
       mMultiAction.clear();
    }

    public List<BaseAction> getActions() {
        return mUndoActions;
    }


    public int  getMutilsActionSize(){
        return mMultiAction.size();
    }


    public Map<Integer, BaseAction> getMultiAction() {
        return mMultiAction;
    }


    /**
     * 处理 gesture的undo事件
     * @param action
     */
    private void handleUndoGesture(GestureAction action) {
        GestureBean bean = action.getBean();
        mGestureUndoAction.clear();
        mGestureUndoAction.addAll(getGestureByIndex(bean.index));
        int imageIndex = mGestureUndoAction.size() - 1;
        if (imageIndex > 0){
            GestureAction gestureAction = (GestureAction) mGestureUndoAction.get(imageIndex - 1);
            gestureAction.reDraw();
        }else{
            action.remove();
        }
        mImageRedoActions.add(action);
    }

    /**
     * 拿到对应下表的 gestureaction
     * @param index
     * @return
     */
    private List<BaseAction> getGestureByIndex(int index){
        List<BaseAction> lists = new ArrayList<>();
        if (index != -1) {
            for (BaseAction action : mUndoActions) {
                if (action instanceof GestureAction) {
                    GestureBean bean = ((GestureAction) action).getBean();
                    if (bean.index == index) {
                        lists.add(action);
                    }
                }
            }
        }
        return lists;
    }

}
