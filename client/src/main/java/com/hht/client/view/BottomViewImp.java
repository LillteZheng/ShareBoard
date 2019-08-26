package com.hht.client.view;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hht.client.R;
import com.zhengsr.drawlib.DrawConfig;
import com.zhengsr.drawlib.DrawNotice;
import com.zhengsr.drawlib.bean.PenConfig;
import com.zhengsr.drawlib.callback.DrawSettingMenuListener;
import com.zhengsr.drawlib.callback.PenConfigListener;
import com.zhengsr.drawlib.callback.StatusListener;
import com.zhengsr.drawlib.page.PageDrawCommand;
import com.zhengsr.drawlib.type.DrawActionType;
import com.zhengsr.drawlib.type.SettingMenuType;
import com.zhengsr.drawlib.utils.IpUtils;
import com.zhengsr.drawlib.view.BottomItem;
import com.zhengsr.drawlib.window.ErasePup;
import com.zhengsr.drawlib.window.PagePreviewDialog;
import com.zhengsr.drawlib.window.PenPupwindow;
import com.zhengsr.drawlib.window.settings.SettingMenuManager;

/**
 * created by zhengshaorui on 2019/7/11
 * Describe:底部工具类方法实现类
 */
public class BottomViewImp implements View.OnClickListener, StatusListener, DrawSettingMenuListener {
    PageDrawCommand mDrawState;
    ViewGroup mBottomView;
    private View mPenView;
    private BottomItem mPageNumTv;
    private Activity mActivity;
    private BottomItem mUndoView,mRedoView,mNextPageView,mPrePageView,mClearView,mGestureView;
    private View mCurrentBottomView;
    public BottomViewImp(Activity context, PageDrawCommand drawState, ViewGroup bottomView) {
        mActivity = context;
        this.mDrawState = drawState;
        this.mBottomView = bottomView;
        mDrawState.addStatusListener(this);
        initView(bottomView);
        mPageNumTv.updateTopText(mActivity.getString(R.string.page_t_num,mDrawState.getCurrentPage(),mDrawState.getAllPage()));
    }

    private void initView(ViewGroup bottomView) {
        int childCount = bottomView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ViewGroup viewGroup = (ViewGroup) bottomView.getChildAt(i);
            int count = viewGroup.getChildCount();
            for (int j = 0; j < count; j++) {
                View view = viewGroup.getChildAt(j);
                int viewId = view.getId();
                switch (viewId){
                    case R.id.bottom_pen:
                        view.setSelected(true);
                        mPenView = view;
                        break;
                    case R.id.bottom_undo:
                        mUndoView = (BottomItem) view;
                        break;
                    case R.id.bottom_redo:
                        mRedoView = (BottomItem) view;
                        break;
                    case R.id.bottom_page_num:
                        mPageNumTv = (BottomItem) view;
                        break;
                    case R.id.bottom_next_page:
                        mNextPageView = (BottomItem) view;
                        break;
                    case R.id.bottom_pre_page:
                        mPrePageView = (BottomItem) view;
                        break;
                    case R.id.bottom_clear:
                        mClearView = (BottomItem) view;
                        break;
                    case R.id.bottom_gesture:
                        mGestureView = (BottomItem) view;
                        break;
                    default:break;
                }


                view.setOnClickListener(this);
            }

        }
    }




    @Override
    public void onClick(View v) {
        mCurrentBottomView = v;
        resetSelect();
        switch (v.getId()){
            case R.id.bottom_pen:
                mPenView = v;
                if (v.isSelected()){
                    showPenConfigPup(v);
                }else {
                    v.setSelected(true);
                    mDrawState.setDrawType(DrawActionType.PEN);
                }
                break;
            case R.id.bottom_erase:
                if (v.isSelected()){
                    showErasePup(v);
                }
                if (!v.isSelected()) {
                    v.setSelected(true);
                    mDrawState.setDrawType(DrawActionType.ERASE);
                }
                break;
            case R.id.bottom_undo:
                mDrawState.undo(false);
                break;
            case R.id.bottom_redo:
                mDrawState.redo(false);
                break;
            case R.id.bottom_gesture:
                v.setSelected(true);
                mDrawState.gesture();
                break;
            case R.id.bottom_clear:
                if (mPenView != null){
                    mPenView.setSelected(true);
                }
                mDrawState.clear(false);
                break;
            case R.id.bottom_draw_settings:
                showSettingsMenuPup(v);
                break;
            case R.id.bottom_page_more:
                if (mPenView != null){
                    mPenView.setSelected(true);
                }
                mDrawState.createNewPage(false);

                break;

            case R.id.bottom_next_page:
                if (mPenView != null){
                    mPenView.setSelected(true);
                }
                mDrawState.gotoNextPage(false);
                break;
            case R.id.bottom_page_num:
                new PagePreviewDialog(mActivity);
                break;
            case R.id.bottom_pre_page:
                if (mPenView != null){
                    mPenView.setSelected(true);
                }
                mDrawState.gotoPrePage(false);
                break;
            case R.id.bottom_exit:
                mActivity.finish();
                break;
            default:break;
        }
    }

    /**
     * 显示画笔菜单
     * @param v
     */
    private void showErasePup(View v) {
        new ErasePup(mActivity,v)
                .eraseSize(mDrawState.getCurrentEraseSize())
                .listener(new ErasePup.ErasePupListener() {
                    @Override
                    public void onSize(int size) {
                        mDrawState.eraseSize(size);
                    }
                });
    }

    /**
     * 显示设置菜单
     * @param v
     */
    private void showSettingsMenuPup(View v) {
        SettingMenuManager.create()
                .context(mActivity)
                .view(v)
                .ip(IpUtils.getIp(mActivity))
                .mainLayoutId(R.layout.activity_main)
                .addListener(this)
                .showMenu();

    }

    /**
     * 恢复选中状态
     */
    private void resetSelect(){
        for (int i = 0; i < mBottomView.getChildCount(); i++) {
            ViewGroup viewGroup = (ViewGroup) mBottomView.getChildAt(i);
            for (int j = 0; j < viewGroup.getChildCount(); j++) {
                View view = viewGroup.getChildAt(j);
                if (view != mCurrentBottomView) {
                    view.setSelected(false);
                }
            }

        }
    }


    /**
     * 显示画笔设置，context 和 view 是必备的
     * @param view
     */
    private void showPenConfigPup(View view){
        PenPupwindow.create()
                .conntext(mActivity)
                .view(view)
                .color(mDrawState.getCurrentColor())
                .penSize(mDrawState.getCurrentPenSize())
                .penType(mDrawState.getCurrentPenType())
                .listener(new PenConfigListener() {
                    @Override
                    public void penConfig(PenConfig penConfig) {
                        if (penConfig.penColor != -2){
                            mDrawState.penColor(penConfig.penColor);
                        }
                        if (penConfig.pensize != -1){
                            mDrawState.penSize(penConfig.pensize);
                        }
                        if (penConfig.penType != -1){
                            mDrawState.setPenType(penConfig.penType);
                        }
                    }
                });
    }

    @Override
    public void stateNotice(DrawNotice state) {
        switch (state){
            case PAGE_MAXED:
                Toast.makeText(mActivity, mActivity.getString(R.string.page_can_noly, DrawConfig.MAX_PAGE), Toast.LENGTH_SHORT).show();
                break;
            case CLEAR:
                mUndoView.setGray(true);
                mRedoView.setGray(true);
                break;
            case CAN_REDO:
                mRedoView.setGray(false);

                break;
            case CAN_NOT_REDO:
                mRedoView.setGray(true);
                break;
            case CAN_UNDO:
                mUndoView.setGray(false);
                mClearView.setGray(false);
                break;
            case CAN_NOT_UNDO:
                mUndoView.setGray(true);
                break;
            case CREATE_NEW_PAGE:
                mPrePageView.setGray(false);
                mNextPageView.setGray(false);
                mPageNumTv.updateTopText(mActivity.getString(R.string.page_t_num,mDrawState.getCurrentPage(),mDrawState.getAllPage()));
                break;
            case NO_NEXT_PAGE:
                mNextPageView.setGray(true);
                break;
            case HAS_NEXT_PAGE:
                mNextPageView.setGray(false);
                mPageNumTv.updateTopText(mActivity.getString(R.string.page_t_num,mDrawState.getCurrentPage(),mDrawState.getAllPage()));
                break;
            case NO_PRE_PAGE:
                mPrePageView.setGray(true);
                break;
            case HAS_PRE_PAGE:
                mPrePageView.setGray(false);
                mPageNumTv.updateTopText(mActivity.getString(R.string.page_t_num,mDrawState.getCurrentPage(),mDrawState.getAllPage()));
                break;
            case INSERT_IMAGE:
                mGestureView.setGray(false);
                resetSelect();
                mGestureView.setSelected(true);
                break;
            default:break;
        }
    }

    @Override
    public void settingMenu(SettingMenuType menuType, Object data) {
        switch (menuType){
            case ERASE:
                int size = (int) data;
                mDrawState.eraseSize(size);
                break;
            case BACKGROUND:
                int index = (int) data;
                mDrawState.changeBackground(index);
                break;
            case IMAGE_PATH:
                String path = (String) data;
                mDrawState.insertImage(path);
                break;
            case BACKGROUND_IMAGE:
                String bgPath = (String) data;
                mDrawState.changeBackground(bgPath);
                break;
            default:break;
        }
    }
}
