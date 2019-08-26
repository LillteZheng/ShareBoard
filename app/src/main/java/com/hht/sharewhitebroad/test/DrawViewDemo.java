package com.hht.sharewhitebroad.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.zhengsr.drawlib.trans.ResourceDataManager;
import com.zhengsr.drawlib.trans.ResourcePath;
import com.zhengsr.drawlib.utils.BgUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class DrawViewDemo extends View {
    private static final String TAG = "DrawFiltersdfsdfsdf";
    private Paint mPaint;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mClearPaint;
    private HashMap<Integer,ItemPath> mPathItems = new LinkedHashMap<>();
    private ResourceDataManager mTransManager;
    public DrawViewDemo(Context context) {
        this(context, null);
    }

    public DrawViewDemo(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawViewDemo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(5);
        mPaint.setStyle(Paint.Style.STROKE);

        mClearPaint = new Paint();
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        setClickable(true);
       // mBitmap = BgUtils.drawDefualtBg(960,1080);
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawColor(Color.WHITE);
        mCanvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        mTransManager = ResourceDataManager.getInstance();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int actionIndex = event.getActionIndex();
        int pointId = event.getPointerId(actionIndex);
        int count = event.getPointerCount();

        switch (event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                setDown(event, pointId, actionIndex);

                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                setDown(event, pointId, actionIndex);
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
                            mTransManager.lineMove(x,y,pointId);
                        }
                    }

                }

                invalidate();
                break;
            case MotionEvent.ACTION_POINTER_UP:
                setUp(event, pointId);
              //  invalidate();
                break;
            case MotionEvent.ACTION_UP:
                setUp(event, pointId);
                mPathItems.clear();
                invalidate();

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
          //  mPathItems.remove(pointId);

        }
    }

    public void clear(){
        mCanvas.drawColor(Color.WHITE);
        invalidate();
    }

    public void reDraw(){
        List<ResourcePath> paths = mTransManager.getTransPaths();
        for (int i = 0; i < paths.size(); i++) {
            ResourcePath transPath = paths.get(i);
            Paint paint = mTransManager.getPaint(transPath);
            Path path = mTransManager.getPath(transPath);
            mCanvas.drawPath(path,paint);
        }
    }

    private void setDown(MotionEvent event, int pointId, int actionIndex) {
        float x = event.getX(actionIndex);
        float y = event.getY(actionIndex);
        mPathItems.put(pointId,new ItemPath(x,y,pointId));
        mTransManager.lineStart(x,y,pointId,1,1,Color.RED,5);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //先清空
        canvas.drawBitmap(mBitmap,0,0,mClearPaint);
        //绘制以前的
        canvas.drawBitmap(mBitmap,0,0,null);
        //绘制现在的path
        if (mPathItems != null && mPathItems.size() > 0) {
            for (int i = 0; i < mPathItems.size(); i++) {
                ItemPath itemPath = mPathItems.get(i);
                if (itemPath != null) {
                    itemPath.draw(canvas);
                }
            }
        }
    }

    class ItemPath {
        int pointId;
        Path path;

        public ItemPath(float x, float y, int id) {
            path = new Path();
            path.reset();
            path.moveTo(x,y);
            path.lineTo(x,y);
            this.pointId = id;

            path.getFillType();
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
            canvas.drawPath(path,mPaint);
        }
    }
}
