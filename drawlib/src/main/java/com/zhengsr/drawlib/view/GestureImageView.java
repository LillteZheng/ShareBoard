package com.zhengsr.drawlib.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewConfiguration;

import com.zhengsr.commonlib.LggUtils;
import com.zhengsr.drawlib.DrawConfig;
import com.zhengsr.drawlib.R;
import com.zhengsr.drawlib.bean.GesImgBean;
import com.zhengsr.drawlib.utils.BitmapUtils;

import java.util.logging.Logger;

/**
 * created by zhengshaorui on 2019/7/29
 * Describe: 一个可以自动移动和放大的Imageview
 */
public class GestureImageView extends AppCompatImageView {
    private static final String TAG = "GestureBitmap";
    private static final int RECT_OFFSERT = 2;
    private Matrix mMatrix;
    private GestureDetector mGestureDetector;
    private Paint mPaint;
    private Bitmap mDeleteBg;
    private boolean isShowStatus = false;
    private RectF mDeleteBgRect;
    private ScaleGestureDetector mScaleGestureDetector;
    private float mDefualtSx;
    private float mDefualtSy;
    private boolean isScaleTouch = false;
    private GesImgBean mBean;
    private boolean isMove;
    private float mTouchSlop;

    public GestureImageView(Context context) {
        this(context,null);
    }

    public GestureImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public GestureImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setScaleType(ScaleType.MATRIX);
        mMatrix = new Matrix();
        mGestureDetector = new GestureDetector(context,new SimpleGesture());
        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleGesture());
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);

        mDeleteBg = BitmapFactory.decodeResource(context.getResources(), R.drawable.draw_delete);
        mDeleteBgRect = new RectF();
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

    }


    public void configBitmap(int resId,int width,int height){
        setImageResource(resId);
        Drawable dw = getDrawable();
        mDefualtSx = width * 1.0f / dw.getIntrinsicWidth();
        mDefualtSy = height * 1.0f / dw.getIntrinsicHeight();
       // mMatrix.preTranslate(200,200);
        mMatrix.preScale(mDefualtSx, mDefualtSy);
        setImageMatrix(mMatrix);
    }


    public void configBitmap(String path,int width,int height,int pageId,int offsetx,int offsety,int index){
        mBean = new GesImgBean();
        mBean.pageId = pageId;
        mBean.index = index;
        setImageBitmap(BitmapUtils.getScaleBitmap(path,width,height));

        Drawable drawable = getDrawable();
        float dw = drawable.getIntrinsicWidth();
        float dh = drawable.getIntrinsicHeight();
        float scale = 0;
        // mDefualtSx = width * 1.0f / dw;
        //  mDefualtSy = height * 1.0f / dh;


        //控件宽高都比图片大,即放大了多少倍，拿到宽高的最小放大比例
        if (width > dw && height > dh){
            scale = Math.min((width * 1.0f / dw),(height * 1.0f / dh));
        }
        //图片宽高比控件大,即缩小了多少倍，，拿到宽高的最大放小比例
        if (dw > width && dh > height){
            scale = Math.max((width * 1.0f / dw),(height * 1.0f / dh));
        }
        //图片高比控件的高大,即应该缩放多少
        if (dw < width && dh > height){
            scale =  height * 1.0f / dh;
        }
        //图片宽比控件的宽大,即应该缩放多少
        if (dw > width && dh < height){
            scale = width * 1.0f / dw;
        }
        mDefualtSx = mDefualtSy = scale;
        mMatrix.preTranslate(offsetx,offsety);
        mMatrix.preScale(mDefualtSx, mDefualtSy);
        setImageMatrix(mMatrix);

      /*  Drawable dw = getDrawable();
        mDefualtSx = width * 1.0f / dw.getIntrinsicWidth();
        mDefualtSy = height * 1.0f / dw.getIntrinsicHeight();
        mMatrix.preTranslate(200,200);
        mMatrix.preScale(mDefualtSx, mDefualtSy);
        setImageMatrix(mMatrix);*/
    }
    /**
     * 获取缩放比例
     * @param index
     * @return
     */
    private float getMatrixScale(int index){
        float[] floats = new float[9];
        mMatrix.getValues(floats);
        return floats[index];
    }

    public Matrix getCurrentMatrix() {
        return mMatrix;
    }

    public void setMatrix(Matrix matrix){
        float[] floats = new float[9];
        matrix.getValues(floats);
        mMatrix.setValues(floats);
    }

    /**
     * 设置触摸事件
     * @param event
     * @return
     */
    public boolean setOnTouchEvent(MotionEvent event){

        int count = event.getPointerCount();
        if (count > 1){
            isScaleTouch = true;
            mScaleGestureDetector.onTouchEvent(event);

        }else{
            if (!isScaleTouch) {
                mGestureDetector.onTouchEvent(event);
            }
        }
        if (event.getAction() == MotionEvent.ACTION_UP){
            isShowStatus = true;
            isScaleTouch = false;
            reCaculateDeleteRect();
          //  mPaint.setPathEffect(new DashPathEffect(new float[]{10, 10, 10, 10}, 1));
            invalidate();
        }

        return true;
    }


    /**
     * 重新计算删除图片的rect
     */
    public void reCaculateDeleteRect() {
        RectF rect = getMatrixRect();
        float left = rect.right - mDeleteBg.getWidth();
        float right = left + mDeleteBg.getWidth();
        float top ;

        if ( (rect.bottom + mDeleteBg.getHeight()) > getHeight()) {
            top = rect.top - mDeleteBg.getHeight() -4;
        }else{
            top = rect.bottom+4;

        }
        float bottom = top + mDeleteBg.getHeight();
        mDeleteBgRect.set(left,top,right,bottom);
    }

    /**
     * 手势监听
     */
    class SimpleGesture extends GestureDetector.SimpleOnGestureListener{
        private  boolean isCanMove = false;
        @Override
        public boolean onDown(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();
            RectF rectF = getMatrixRect();
            isMove = false;
            isCanMove = false;
            isShowStatus = false;
            mPaint.setPathEffect(null);
            if (x <= rectF.right && x >= rectF.left && y>= rectF.top && y <= rectF.bottom) {
                if (mPaint.getPathEffect() == null) {
                    mPaint.setPathEffect(new DashPathEffect(new float[]{10, 10, 10, 10}, 1));
                }
                isCanMove = true;
            }
            return true;
        }


        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            if (isCanMove) {
                //if (distanceX >= mTouchSlop || distanceY >= mTouchSlop) {
                    float sx = getMatrixScale(Matrix.MSCALE_X);
                    float sy = getMatrixScale(Matrix.MSCALE_Y);
                    mMatrix.preTranslate(-distanceX / sx, -distanceY / sy);
                    checkBoard();
                    setImageMatrix(mMatrix);
                    if (Math.abs(distanceX) >= 3 || distanceY >= 3) {
                        isMove = true;
                    }
                    invalidate();
              //  }
            }

            return false;
        }
    }

    /**
     * 放大监听
     */
    class ScaleGesture implements ScaleGestureDetector.OnScaleGestureListener{

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            float focusX = detector.getFocusX();
            float focusY = detector.getFocusY();
            float scale = getMatrixScale(Matrix.MSCALE_X);
            if (scaleFactor * scale < mDefualtSx){
                scaleFactor = mDefualtSx  / scale;
            }
            //只能放大两倍
            if (scaleFactor * scale > mDefualtSx * 2){
                scaleFactor =  mDefualtSx * 2 / scale;
            }
            //注意这里要采用右乘
            mMatrix.postScale(scaleFactor,scaleFactor,focusX,focusY);
            checkBoard();
            setImageMatrix(mMatrix);
           // invalidate();
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {

        }
    }

    /**
     * 获取放大后的矩阵大小
     * @return
     */
    private RectF getMatrixRect(){
        RectF rectF = new RectF();
        Drawable dw = getDrawable();
        if (dw != null){
            rectF.set(0,0,dw.getIntrinsicWidth(),dw.getIntrinsicHeight());
            mMatrix.mapRect(rectF);
        }
        return rectF;
    }

    /**
     * 获取外围虚线大小
     * @return
     */
    private RectF getPortLineRect(){
        RectF rectF = new RectF();
        RectF rect = getMatrixRect();
        rectF.set(rect.left - RECT_OFFSERT,rect.top - RECT_OFFSERT,rect.right+RECT_OFFSERT,rect.bottom+RECT_OFFSERT);
        return rectF;
    }

    /**
     * 是否能点击，即是否在图片局域内
     * @param e
     * @return
     */
    public boolean isCanClick(MotionEvent e){
        float x = e.getX();
        float y = e.getY();
        RectF rectF = getMatrixRect();

        if (x <= rectF.right && x >= rectF.left && y>= rectF.top && y <= rectF.bottom) {
            return true;
        }
        return false;
    }

    /**
     * 是否能删除
     * @param e
     * @return
     */
    public boolean isCanDelete(MotionEvent e){
        float x = e.getX();
        float y = e.getY();
        if (x <= mDeleteBgRect.right && x >= mDeleteBgRect.left && y>= mDeleteBgRect.top && y <= mDeleteBgRect.bottom) {
            return true;
        }
        return false;
    }

    public void setHide(boolean hide){
        mBean.isManual = hide;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        RectF rect = getPortLineRect();
        if (isShowStatus) {
            canvas.save();
            canvas.clipRect(mDeleteBgRect);
            canvas.drawBitmap(mDeleteBg,mDeleteBgRect.left,mDeleteBgRect.top, null);
            canvas.restore();
        }
        canvas.drawRect(rect,mPaint);
    }

    /**
     * 重置装填，比如删除图片和虚线
     */
    public void resetStatus(){
        isShowStatus = false;
        mPaint.setPathEffect(null);
        invalidate();
    }


    /**
     * 检查边沿，不让它超过界面
     */
    private void checkBoard(){
        RectF rectF = getMatrixRect();
        float dx = 0;
        float dy = 0;
        //检测边界，超过界面
        if (rectF.width() >= getWidth()) {
            if (rectF.right < getWidth()) {
                dx = getWidth() - rectF.right;
            }
            if (rectF.left > 0) {
                dx = -rectF.left;
            }
        }else {
            if (rectF.right > getWidth()){
                dx = getWidth() - rectF.right;
            }
            if (rectF.left < 0){
                dx = -rectF.left;
            }
        }
        //检测边界，超过界面
        if (rectF.height() >= getHeight()){
            if (rectF.top > 0){
                dy = -rectF.top;
            }
            if (rectF.bottom < getHeight()){
                dy = getHeight() - rectF.bottom;
            }
        }else{
            if (rectF.bottom > getHeight()){
                dy = getHeight() - rectF.bottom;
            }
            if (rectF.top < 0){
                dy = -rectF.top;
            }
        }

        mMatrix.preTranslate(dx,dy);

    }

    public GesImgBean getImgBean(){
        BitmapDrawable drawable = (BitmapDrawable) getDrawable();
        mBean.bitmap = drawable.getBitmap();
        mBean.matrix = mMatrix;
        mBean.visiable = getVisibility() == VISIBLE;
        return mBean;
    }

    public boolean isMove() {
        return isMove;
    }
}
