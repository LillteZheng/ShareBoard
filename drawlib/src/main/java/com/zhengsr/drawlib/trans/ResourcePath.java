package com.zhengsr.drawlib.trans;

import com.zhengsr.drawlib.DrawConfig;
import com.zhengsr.drawlib.type.DrawActionType;
import com.zhengsr.drawlib.type.PenType;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * created by zhengshaorui on 2019/7/18
 * Describe:序列化 path,由于要夸进程且持久化，使用 serializebale 序列化
 */
public class ResourcePath implements Serializable{
    public static final long serialVersionUID = 7525131310849580639L;
    public int penColor = DrawConfig.PEN_COLOR;
    public int penSize = DrawConfig.PEN_DEFAULT_SIZE;
    public int penType = PenType.PEN.ordinal();
    public int drawType = DrawActionType.PEN.ordinal();
    public int pointId ;
    public ArrayList<ResourcePoint> points = new ArrayList<>();
}
