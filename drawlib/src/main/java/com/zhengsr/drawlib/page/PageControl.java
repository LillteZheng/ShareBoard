package com.zhengsr.drawlib.page;

import com.zhengsr.drawlib.DrawConfig;
import com.zhengsr.drawlib.DrawNotice;
import com.zhengsr.drawlib.action.BaseAction;
import com.zhengsr.drawlib.callback.StatusListener;
import com.zhengsr.drawlib.data.Command;
import com.zhengsr.drawlib.view.DrawSurface;

import java.util.ArrayList;
import java.util.List;

/**
 * created by zhengshaorui on 2019/7/11
 * Describe:专门处理新建页问题
 */
public class PageControl {

    private int mCurrentPage;
    private DrawSurface mView;
    private StatusListener mListener;
    private List<Integer> mPage = new ArrayList<>();

    public PageControl(int page, DrawSurface view) {

        this.mCurrentPage = page;
        mView = view;
        mPage.add(mCurrentPage);
    }

    public void addStatusListener(StatusListener listener) {
        mListener = listener;
    }
    /**
     * 用来新建或者切换页
     * @param page
     */
    public void addOrChangePage(int lastPage,int page) {
        if (page != mCurrentPage) {
            //保存上一次数据
           // PageDataManager.getInstance().addNote(mCurrentPage, Command.getInstance().getActions());
            //拿到现在的数据
            List<BaseAction> note = PageDataManager.getInstance().getNote(page);
            Command.getInstance().clear();
            if (note != null && note.size() > 0) {
                for (BaseAction action : note) {
                    Command.getInstance().add(action);
                }
            }
            mView.hideAndRecoveryImage(lastPage,page);
            mView.reDraw();
            mCurrentPage = page;
        }
    }


    public void deletePage(int page){
        mPage.remove(page);
        PageDataManager.getInstance().delete(page);
    }

    public void createNewPage(){
        //先保存一下背景
        if (getCurrentPage() < DrawConfig.MAX_PAGE) {
            addOrChangePage(getCurrentPage(),mPage.size());
            mPage.add(mPage.size());
        }else{
            mListener.stateNotice(DrawNotice.PAGE_MAXED);
        }
        DrawConfig.getParams().curPageId(mPage.size() - 1);
        DrawConfig.getParams().setBg(DrawConfig.getParams().getBg());
    }

    public boolean goNextPage(){
        if (isHasNextPage()) {
            addOrChangePage(mCurrentPage,mCurrentPage+1);
            return true;
        }

        return false;
    }

    public boolean goPrePage(){
        if (isHasPrePage()) {
            addOrChangePage(mCurrentPage,mCurrentPage-1);
            return true;
        }
        mListener.stateNotice(DrawNotice.NO_PRE_PAGE);
        return false;
    }

    public boolean isHasNextPage(){
        return mCurrentPage < mPage.size() - 1;
    }

    public boolean isHasPrePage(){
        return mCurrentPage > 0;
    }

    public int getCurrentPage() {
        return mCurrentPage;
    }

    public int getAllPage() {
        return mPage.size();
    }


}
