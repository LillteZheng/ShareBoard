package com.zhengsr.drawlib.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.zhengsr.drawlib.action.BaseAction;
import com.zhengsr.drawlib.action.EraseAction;
import com.zhengsr.drawlib.data.Command;
import com.zhengsr.drawlib.page.PageDataManager;
import com.zhengsr.drawlib.page.PageDrawCommand;
import com.zhengsr.drawlib.tool.ImageSaveTools;
import com.zhengsr.drawlib.utils.BgUtils;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @auther by zhengshaorui on 2019/7/22
 * describe: 画线surfaceview 类
 */
public class DrawSurfaceview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "DrawSurface";
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Bitmap mBg;
    private boolean isDrawing = true;
    private ExecutorService mSingleExecutor ;
    private Paint mPaint;
    private PageDrawCommand mDrawState;
    private Paint mClearPaint;

    public DrawSurfaceview(Context context) {
        this(context,null);
    }

    public DrawSurfaceview(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DrawSurfaceview(Context context, AttributeSet attrs, int defStyleAttr) {
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
        mClearPaint = new Paint();
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        setZOrderOnTop(true);
      //  setZOrderMediaOverlay(true);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        getHolder().addCallback(this);
    }

    public void setDrawState(PageDrawCommand state){
        mDrawState = state;
    }




    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isDrawing = true;
        Canvas canvas = holder.lockCanvas();
        if (canvas != null){
            //mCanvas.drawBitmap(mBg,0,0,null);
            mCanvas.drawColor(Color.TRANSPARENT);
            holder.unlockCanvasAndPost(canvas);
        }
        mSingleExecutor = Executors.newSingleThreadExecutor();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {


    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
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
        holder.removeCallback(this);
        Command.getInstance().clear();
        PageDataManager.getInstance().clear();
    }

   /* @Override
    public boolean onTouchEvent(MotionEvent event) {

        return mDrawState == null ||  mDrawState.onTouchEvent(event);
    }*/

    public boolean setOnTouchEvent(MotionEvent event) {
        return mDrawState == null ||  mDrawState.onTouchEvent(event);

    }

    public void clear(){
        mCanvas.drawBitmap(mBg,0,0,null);
        postDraw(null,false);
    }


    public void postDraw(final HashMap<Integer,BaseAction> actions, final boolean isReDraw){
        mSingleExecutor.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (DrawSurfaceview.this) {
                    Canvas canvas = null;
                    try {
                        canvas = getHolder().lockCanvas();
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
                            getHolder().unlockCanvasAndPost(canvas);
                        }

                    }
                }
            }
        });




    }

    public void postDraw(final boolean isReDraw){
        List<BaseAction> baseActions = Command.getInstance().getActions();
        int size = baseActions.size();
       // mCanvas.drawBitmap(mBg,0,0,null);
        for (int i = 0; i < size; i++) {
            BaseAction action = baseActions.get(i);
            action.draw(mCanvas);
        }
        postDraw(null,isReDraw);

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
}
