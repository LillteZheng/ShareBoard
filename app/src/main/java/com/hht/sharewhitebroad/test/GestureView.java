package com.hht.sharewhitebroad.test;

import android.content.Context;

import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.hht.sharewhitebroad.MainActivity;

import javax.xml.validation.Validator;

/**
 * @auther by zhengshaorui on 2019/7/22
 * describe:
 */
public class GestureView extends View {
    private static final String TAG = "GestureView";
    private GestureDetector mDetector;

    public GestureView(Context context) {
        this(context,null);
    }
    
    public GestureView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    
    public GestureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClickable(true);
        setFocusable(true);
       //
     //   mDetector = new GestureDetector(new testListener());
        //mDetector.setIsLongpressEnabled(true);

       // mDetector = new GestureDetector(new GestureListenerDemo());
       // mDetector.setOnDoubleTapListener(new DoubleListenerDemo());
        mDetector = new GestureDetector(context,new SimpleDemo());

    }




    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDetector.onTouchEvent(event);

    }
    
    class DoubleListenerDemo implements GestureDetector.OnDoubleTapListener{

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            /**
             * 点击事件,不像onSingleTapUp，当双击时 onSingleTapConfirmed 不会被调用
             */
            Log.d(TAG, "zsr onSingleTapConfirmed: ");
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            /**
             * 双击，当第二下点击，down 事件触发
             */
            Log.d(TAG, "zsr onDoubleTap: ");
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            /**
             * 双击时触发，且 MotionEvent 会继续触发，可以监听 up 事件，
             * 在up事件处理逻辑
             */
            Log.d(TAG, "zsr onDoubleTapEvent: "+e.getAction());
            return false;
        }
    }


    class GestureListenerDemo implements GestureDetector.OnGestureListener{

        @Override
        public boolean onDown(MotionEvent e) {
            /**
             * 按下屏幕就会出发，需要返回true来消费这个事件，通过GestureDetector 源码就知道，
             * 返回true后,mDetector.onTouchEvent(event) 也会返回突然， 这样 onTouchEvent 的事件才不会继续传递
             */
            Log.d(TAG, "zsr onDown: ");
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            /**
             * 当按下超过180ms时，就会触发，一般我们用这个来实现点击背景之类的
             */
            Log.d(TAG, "zsr onShowPress: ");
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            /**
             * 相当于 up 事件
             */
            Log.d(TAG, "zsr onSingleTapUp: ");
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            /**
             * 监听屏幕滑动，其中 e2 第二个触摸事件，distanceX 和 distanceY
             * 则为滑动距离
             */
            Log.d(TAG, "zsr onScroll: ");
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            /**
             * 当手指按住屏幕不放，则会触发,此时 onSingleTapUp 不再触发
             */
            Log.d(TAG, "zsr onLongPress: ");
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            /**
             * 当手指快速滑动屏幕，则会触发 velocityX，velocityY 表示每秒滑动的距离(像素)
             */
            Log.d(TAG, "zsr onFling: ");
            return false;
        }
    }



    class  SimpleDemo extends GestureDetector.SimpleOnGestureListener{

        @Override
        public boolean onDown(MotionEvent e) {
            Log.d(TAG, "zsr onDown: 按下");
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            Log.d(TAG, "zsr onShowPress: ");
            super.onShowPress(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d(TAG, "zsr onSingleTapUp: ");
            return false;
        }



        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
           // Log.d(TAG, "zsr onScroll:在触摸屏上滑动: "+(e1.getX() - e2.getX())+" "+distanceX);
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.d(TAG, "zsr onLongPress:长按并且没有松开");
            super.onLongPress(e);
        }


        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            //实现左滑和右滑
            float distanceX = e2.getX() - e1.getX();
            Log.d(TAG, "zsr onFling: "+distanceX+" "+(distanceX < -100)+" "+velocityX);
            if (distanceX > 100 && velocityX > 200){
                Log.d(TAG, "zsr onFling: 左滑退出");
            }
            if (distanceX < -100 && velocityX < -200){
                Log.d(TAG, "zsr onFling: 右滑进入其他activity");
            }
          //  Log.d(TAG, "onFling:迅速滑动，并松开");
            return false;
        }


        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.d(TAG, "zsr 单击");
            return super.onSingleTapConfirmed(e);
        }



        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d(TAG, "zsr 双击，第二次down的时候就出发");
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            Log.d(TAG, "zsr onDoubleTapEvent: "+e.getAction());
            return super.onDoubleTapEvent(e);
        }
    }
}
