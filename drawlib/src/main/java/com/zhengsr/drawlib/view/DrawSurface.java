package com.zhengsr.drawlib.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.SurfaceTexture;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.TextureView;
import android.widget.FrameLayout;

import com.zhengsr.drawlib.action.BaseAction;
import com.zhengsr.drawlib.action.EraseAction;
import com.zhengsr.drawlib.bean.GesImgBean;
import com.zhengsr.drawlib.data.Command;
import com.zhengsr.drawlib.page.PageDataManager;
import com.zhengsr.drawlib.page.PageDrawCommand;
import com.zhengsr.drawlib.tool.ImageSaveTools;
import com.zhengsr.drawlib.trans.TransManager;
import com.zhengsr.drawlib.utils.BgUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @auther by zhengshaorui on 2019/7/22
 * describe: 画线surfaceview 类
 */
public class DrawSurface extends TextureView implements TextureView.SurfaceTextureListener {
    private static final String TAG = "DrawSurface";
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Bitmap mBg;
    private boolean isDrawing = true;
    private ExecutorService mSingleExecutor ;
    private Paint mPaint;
    private PageDrawCommand mDrawState;
    private Paint mClearPaint;
    private FrameLayout mBackgroundView;
    public DrawSurface(Context context) {
        this(context,null);
    }

    public DrawSurface(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DrawSurface(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {

       /* setClickable(true);
        setFocusable(true);
        setFocusableInTouchMode(true);*/

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setFilterBitmap(true);

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        mCanvas = new Canvas(mBitmap);
        mBg = BgUtils.getChooseBitmap(getContext(),1,width,height);
        //默认画笔
        setOpaque(false);
        setSurfaceTextureListener(this);

        mClearPaint = new Paint();
        mClearPaint.setColor(Color.WHITE);
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    public void setDrawState(PageDrawCommand state){
        mDrawState = state;
    }


    public boolean setOnTouchEvent(MotionEvent event) {
        if (TransManager.getInstance().isResponsing()){
            TransManager.getInstance().sendResponseConflict();
            return true;
        }
        return mDrawState == null ||  mDrawState.onTouchEvent(event);

    }

    public void clear(){
        PageDataManager.getInstance().clear(mDrawState.getRealPageId());
        mCanvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
        postDraw(null,false);
        removeOrHideBackImage(mDrawState.getRealPageId());
    }

    public void postDraw(final Map<Integer,BaseAction> actions, boolean useThread){
        if (useThread){
            mSingleExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    postDraw(actions);
                }
            });
        }else{
            postDraw(actions);
        }
    }

    private void postDraw(Map<Integer,BaseAction> actions){
        Canvas canvas = null;
        try {
            canvas = lockCanvas();
            if (canvas != null) {
                canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
                canvas.drawBitmap(mBitmap, 0, 0, null);
                if (actions != null) {
                    int count = actions.size();
                    for (int i = 0; i < count; i++) {
                        BaseAction action = actions.get(i);
                        if (action != null) {
                            action.draw(canvas);
                            if (action instanceof EraseAction) {
                                EraseAction eraseAction = (EraseAction) action;
                                eraseAction.drawErase(canvas);
                            }

                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (canvas != null) {
                unlockCanvasAndPost(canvas);
            }

        }
    }

    public void reDraw(boolean reDrawImg){
        List<BaseAction> baseActions = Command.getInstance().getActions();
        int size = baseActions.size();
        mCanvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
        for (int i = 0; i < size; i++) {
            BaseAction action = baseActions.get(i);
            action.draw(mCanvas);
        }
        postDraw(null,true);
    }

    public void reDraw(){

        reDraw(false);
    }

    public Canvas getCanvas() {
        return mCanvas;
    }

    public Bitmap getBg(){
        return mBg;
    }

    public void saveBitmap() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        new ImageSaveTools()
                .context(getContext())
                .path(path)
                .save();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        isDrawing = true;
        mSingleExecutor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        isDrawing = false;
        mSingleExecutor.shutdownNow();
        //先把引用关系去掉
        mCanvas = null;
        if (mBitmap != null && !mBitmap.isRecycled()){
            mBitmap.recycle();
        }
        if (mBg != null && !mBg.isRecycled()){
            mBg.recycle();
        }
        Command.getInstance().clear();
        PageDataManager.getInstance().clear();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    public void setBackfroundView(FrameLayout backfroundView) {
        mBackgroundView = backfroundView;
    }


    /**
     * 删除或隐藏所有的图片
     * @param page
     */
    private void removeOrHideBackImage(int  page){
        if (mBackgroundView != null){
            int childCount = mBackgroundView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                GestureImageView imageView = (GestureImageView) mBackgroundView.getChildAt(i);
                GesImgBean bean = imageView.getImgBean();
                if (bean.pageId == page){
                    mBackgroundView.removeViewAt(i);
                }
            }
        }
    }

    public void hideAndRecoveryImage(int lastPageId,int pageId){
        //把上一页的图片隐藏
        if (mBackgroundView != null) {
            int childCount = mBackgroundView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                GestureImageView imageView = (GestureImageView) mBackgroundView.getChildAt(i);
                GesImgBean bean = imageView.getImgBean();
                if (bean.pageId == lastPageId) {
                    imageView.setVisibility(GONE);
                } else if (bean.pageId == pageId) {
                    imageView.setVisibility(VISIBLE);
                }

            }
        }
    }

}
