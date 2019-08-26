package com.zhengsr.drawlib.trans;



import java.io.Serializable;

/**
 * created by zhengshaorui on 2019/7/18
 * Describe: 序列化点,由于要夸进程且持久化，使用 serializebale 序列化
 */
public class ResourcePoint implements Serializable{
    private static final long serialVersionUID = -3724012946176499636L;
    public float x;
    public float y;

    public ResourcePoint(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
