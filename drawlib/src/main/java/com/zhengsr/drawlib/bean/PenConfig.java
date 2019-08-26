package com.zhengsr.drawlib.bean;

/**
 * created by zhengshaorui on 2019/7/12
 * Describe: 画笔设置类
 */
public class PenConfig {
    public int pensize = -1;
    // -1 白色，不能用-1来表示
    public int penColor = -2;
    public int penType = -1;

    @Override
    public String toString() {
        return "PenConfig{" +
                "pensize=" + pensize +
                ", penColor=" + penColor +
                ", penType=" + penType +
                '}';
    }
}
