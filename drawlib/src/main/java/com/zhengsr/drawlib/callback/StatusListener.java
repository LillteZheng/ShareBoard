package com.zhengsr.drawlib.callback;

import com.zhengsr.drawlib.DrawNotice;

/**
 * created by zhengshaorui on 2019/7/11
 * Describe:用来通知 drawstatuas 的状态
 */
public interface StatusListener {
    void stateNotice(DrawNotice state);
}
