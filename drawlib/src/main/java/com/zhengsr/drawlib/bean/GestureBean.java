package com.zhengsr.drawlib.bean;

import android.opengl.Matrix;

/**
 * @auther by zhengshaorui on 2019/8/22
 * describe:
 */
public class GestureBean {
    public boolean isFristAdd;
    public int index = -1;
    public boolean isCallUndo;
    public Matrix mLastMatrix;
    public boolean isDelete;


    public GestureBean() {

    }

    public GestureBean(boolean isFristAdd) {
        this.isFristAdd = isFristAdd;
    }


    @Override
    public String toString() {
        return "GestureBean{" +
                "isFristAdd=" + isFristAdd +
                ", isCallUndo=" + isCallUndo +
                '}';
    }
}
