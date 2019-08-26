package com.zhengsr.drawlib;

/**
 * created by zhengshaorui on 2019/7/11
 * Describe:用来通知各种事件，比如页数已满，是否可以前进后退
 */
public enum DrawNotice {
    //页数已经最大
    PAGE_MAXED,
    //创建新的一页
    CREATE_NEW_PAGE,
    //有下一页
    HAS_NEXT_PAGE,
    //没有下一页
    NO_NEXT_PAGE,
    //有上一页
    HAS_PRE_PAGE,
    //没有上一页
    NO_PRE_PAGE,
    //能够返回
    CAN_UNDO,
    //不能够返回
    CAN_NOT_UNDO,
    //能够前进
    CAN_REDO,
    //能够前进
    CAN_NOT_REDO,
    //没有画布
    NO_BITMAP,
    //清屏操作，恢复一些状态
    CLEAR,
    //插入图片
    INSERT_IMAGE,
}
