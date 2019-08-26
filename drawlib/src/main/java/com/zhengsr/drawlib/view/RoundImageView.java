package com.zhengsr.drawlib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.zhengsr.drawlib.R;

public class RoundImageView extends ImageView {
    private static final String TAG = "zsr";
    private Paint mPaint;
    private Path mPath;
    private Bitmap mBitmap;
    private int mWidth,mHeight;
    /**
     * attrs
     */
    private int mRoundSize;
    public RoundImageView(Context context) {
        this(context,null);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView);
        mRoundSize = ta.getDimensionPixelSize(R.styleable.RoundImageView_round_sise,10);

        ta.recycle();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setFilterBitmap(true);

        mPath = new Path();




    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmap != null){
            Shader shader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP,
                    Shader.TileMode.CLAMP);
            mPaint.setShader(shader);
            canvas.drawPath(mPath,mPaint);
        }



    }

    @Override
    public void setImageResource(int resId) {
        mBitmap = BitmapFactory.decodeResource(getResources(),resId);
        invalidate();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        BitmapDrawable bd = (BitmapDrawable) drawable;
        if (bd != null){
            mBitmap = bd.getBitmap();
            invalidate();
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        mBitmap = bm;
        invalidate();
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        mPath.moveTo(mRoundSize,0);
        mPath.lineTo(width - mRoundSize,0);
        mPath.quadTo(width,0,width,mRoundSize);
        mPath.lineTo(width,height - mRoundSize);
        mPath.quadTo(width,height,width - mRoundSize,height);
        mPath.lineTo(mRoundSize,height);
        mPath.quadTo(0,height,0,height - mRoundSize);
        mPath.lineTo(0,mRoundSize);
        mPath.quadTo(0,0,mRoundSize,0);


        setMeasuredDimension(width, height);

    }

    //设置高的大小
    private int measureHeight(int heightMeasureSpec) {
        // TODO Auto-generated method stub
        int result = 0;
        //获取模式和大小
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(heightMeasureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }else{
            result = 100; //如果是wrap_content ,给个初始值
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }
    //设置宽的大小
    private int measureWidth(int widthMeasureSpec) {
        // TODO Auto-generated method stub
        int result = 0;
        //获取模式和大小
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }else{
            result = 100; //如果是wrap_content ,给个初始值
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }
    
}