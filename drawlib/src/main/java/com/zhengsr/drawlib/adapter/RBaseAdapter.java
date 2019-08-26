package com.zhengsr.drawlib.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;


import java.util.List;

/**
 * Created by zhengshaorui on 2018/4/13.
 */
public abstract class RBaseAdapter<T> extends RecyclerView.Adapter<RBaseViewholder> {
    private int mLayoutId;
    private List<T> mDatas; //取泛型是因为不知道bean是啥类型的。
    private RBaseViewholder mViewholder;

    private boolean isScroll = false;


    public RBaseAdapter(int layoutid,List<T> list) {
        mLayoutId = layoutid;
        mDatas = list;

    }
    public interface onItemClickListener{
        void onItemClick(View view, int position);
    }
    private onItemClickListener mOnItemClickListener;
    public void setonItemClickListener(onItemClickListener listener){
        mOnItemClickListener = listener;
    }


    @Override
    public RBaseViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        mViewholder = RBaseViewholder.getViewHolder(parent.getContext(),
                mLayoutId,parent);
        return mViewholder;
    }

    @Override
    public void onBindViewHolder(RBaseViewholder holder, int position) {
        if (mOnItemClickListener != null){
            final int pos = position;
            holder.getConserView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v,pos);
                }
            });

        }
        getConver(holder,mDatas.get(position)); //提供 viewholder 出去，数据由用户处理
    }

    public abstract void getConver(RBaseViewholder holder,T data);


    @Override
    public int getItemCount() {
        return mDatas == null ? 0 :mDatas.size();
    }

    /**
     * 用来区分是否在滚动，需要在 recyclerview.addonScrollListener去判断设置
     * @param isScroll
     */
    public void setScrolling(boolean isScroll){
        this.isScroll = isScroll;
    }

    public boolean isScroll() {
        return isScroll;
    }
}