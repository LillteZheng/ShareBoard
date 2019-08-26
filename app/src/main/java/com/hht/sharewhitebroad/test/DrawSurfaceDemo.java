package com.hht.sharewhitebroad.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.zhengsr.drawlib.utils.BgUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DrawSurfaceDemo extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "DrawSurface";
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Bitmap mBg;
    private boolean isDrawing = true;
    private Paint mPaint;
    private Path mPath;
    private HashMap<Integer,ItemPath> mPathItems = new LinkedHashMap<>();
    private Paint mClearPaint;

    public DrawSurfaceDemo(Context context) {
        this(context,null);
    }

    public DrawSurfaceDemo(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DrawSurfaceDemo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        getHolder().addCallback(this);
        setClickable(true);
        setFocusable(true);
        setFocusableInTouchMode(true);

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        mCanvas = new Canvas(mBitmap);
        initTools();


    }

    private void initTools() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(5);

        mClearPaint = new Paint();
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isDrawing = true;
        Canvas canvas = holder.lockCanvas();
        if (canvas != null){
            mCanvas.drawBitmap(mBg,0,0,null);
            canvas.drawBitmap(mBg,0,0,null);
            canvas.drawBitmap(mBitmap,0,0,null);
            holder.unlockCanvasAndPost(canvas);
        }
    }



    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isDrawing = false;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int actionIndex = event.getActionIndex();
        int pointId = event.getPointerId(actionIndex);
        int count = event.getPointerCount();

        switch (event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                setDown(event, pointId, actionIndex);
                onAction(false,mPathItems);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                setDown(event, pointId, actionIndex);
                onAction(false,mPathItems);
                break;
            case MotionEvent.ACTION_MOVE:
                for (int i = 0; i < count; i++) {
                    int index = event.getPointerId(i);
                    if (index != -1){
                        float x = event.getX(i);
                        float y = event.getY(i);
                        ItemPath itemPath = mPathItems.get(index);
                        if (itemPath != null) {
                            itemPath.move(x,y,i);
                        }
                    }
                }
                onAction(false,mPathItems);

                break;
            case MotionEvent.ACTION_POINTER_UP:
                setUp(event, pointId);
                break;
            case MotionEvent.ACTION_UP:
                setUp(event, pointId);
                onAction(true,mPathItems);
                mPathItems.clear();
                break;
            default:break;
        }


        return super.onTouchEvent(event);
    }



    private void setUp(MotionEvent event, int pointId) {
        float x = event.getX(event.getActionIndex());
        float y = event.getY(event.getActionIndex());
        ItemPath itemPath = mPathItems.get(pointId);
        if (itemPath != null){
            itemPath.up(x,y,pointId);
            //保存到画布上
            itemPath.draw(mCanvas);
            itemPath.path.reset();

        }

    }

    private void setDown(MotionEvent event, int pointId, int actionIndex) {
        float x = event.getX(actionIndex);
        float y = event.getY(actionIndex);
        mPathItems.put(pointId,new ItemPath(x,y,pointId));

    }


    public void onAction(final boolean isup, final HashMap<Integer,ItemPath> itempaths) {
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {

            }
        });
                postDraw(false, itempaths);

    }


    ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
    private synchronized void postDraw(boolean isRedraw,HashMap<Integer,ItemPath> itempaths){

        Canvas canvas = null;
        try {
            canvas = getHolder().lockCanvas();
            if (canvas != null){
                //先清空
                canvas.drawPaint(mClearPaint);
                //绘制以前的
                canvas.drawBitmap(mBitmap,0,0,null);
                //绘制现在的path
                if (itempaths != null && itempaths.size() > 0) {
                    for (int i = 0; i < itempaths.size(); i++) {
                        ItemPath itemPath = itempaths.get(i);
                        if (itemPath != null) {
                            itemPath.draw(canvas);
                        }
                    }
                }
            }
            getHolder().unlockCanvasAndPost(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class ItemPath {
        int pointId;
        Path path;
        boolean isClear = false;

        public ItemPath(float x, float y, int id) {
            path = new Path();
            path.reset();
            path.moveTo(x,y);
            path.lineTo(x,y);
            this.pointId = id;
        }
        public void move(float x,float y,int id){
            if (pointId == id){
            }
                path.lineTo(x,y);
        }
        public void up(float x,float y,int id){
            if (pointId == id){
            }
                path.lineTo(x,y);
        }
        public void draw(Canvas canvas){
            canvas.drawPath(path, mPaint);
        }
    }

}
