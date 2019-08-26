package com.hht.sharewhitebroad.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * created by zhengshaorui on 2019/7/15
 * Describe:
 */
public class TestView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mSurfaceHolder;
    private Canvas mCanvas;
    private Bitmap mBg;
    private Paint mBitmapPaint;
    private Paint mPaint;
    private SparseArray<Path> mMapObject = new SparseArray<Path>();
    private Path path = new Path();
    private float mStartX = 0;
    private float mStartY = 0;
    private float mCurveEndX;
    private float mCurveEndY;

    public TestView(Context context) {
        this(context,null);
    }

    public TestView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClickable(true);
        getHolder().addCallback(this);

        mSurfaceHolder = getHolder();
        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
        mSurfaceHolder.addCallback(this);
        mBg = Bitmap.createBitmap(1920, 1080, Bitmap.Config.ARGB_8888);
        mBitmapPaint = new Paint();
        mBitmapPaint.setColor(Color.WHITE);
        mBitmapPaint.setStrokeWidth(1920);
        mBitmapPaint.setStyle(Paint.Style.STROKE);
        mBitmapPaint.setStrokeJoin(Paint.Join.ROUND);
        mBitmapPaint.setStrokeCap(Paint.Cap.ROUND);
        mBitmapPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mCanvas = new Canvas(mBg);
        Rect rect = new Rect(0, 0, 1920, 1080);
        mCanvas.drawRect(rect, mBitmapPaint);


        Bitmap bitmap = Bitmap.createBitmap(1920, 1080, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        mCanvas.drawBitmap(bitmap, 0, 0, null);

        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(10);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);//结束的笔画为圆心
        mPaint.setStrokeJoin(Paint.Join.ROUND);//连接处元
        mPaint.setAlpha(0xFF);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeMiter(1.0f);


    }
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    public void undo(){

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int pointerCount = event.getPointerCount();
        int pointerIndex = event.getActionIndex();
        int pointerId = event.getPointerId(pointerIndex);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:

            case MotionEvent.ACTION_POINTER_DOWN:
                Path path = new Path();
                mStartX = event.getX(pointerIndex);
                mStartY = event.getY(pointerIndex);
                path.moveTo(mStartX,mStartY);
                if (mMapObject != null) {
                    mMapObject.put(pointerId, path);
                }

                doDraw(false);
                break;
            case MotionEvent.ACTION_MOVE:
                for (int i = 0; i < pointerCount; i++) {
                    pointerId = event.getPointerId(i);
                    float x = event.getX(i);
                    float y = event.getY(i);
                    Path path1 = mMapObject.get(pointerId);
                    if (path1 != null) {
                        path1.lineTo(x,y);
                    }
                    doDraw(false);
                }
                break;
            case MotionEvent.ACTION_UP:
                doDraw(true);
                break;
                default:break;
        }
        return super.onTouchEvent(event);
    }

    public void doDraw(final boolean isUp) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Canvas canvas = mSurfaceHolder.lockCanvas();
                canvas.drawBitmap(mBg, 0, 0, mBitmapPaint);
                canvas.drawBitmap(mBg, 0, 0, null);
                for (int i = 0; i < mMapObject.size(); i++) {
                    Path path = mMapObject.get(i);
                    if (isUp) {
                        mCanvas.drawPath(path, mPaint);
                    }
                    canvas.drawPath(path, mPaint);
                }
                mSurfaceHolder.unlockCanvasAndPost(canvas);
                if (isUp) {
                    mMapObject.clear();
                }
            }
        });

    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Canvas canvas = holder.lockCanvas();
        canvas.drawBitmap(mBg,0,0,null);
        holder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
