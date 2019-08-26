package com.zhengsr.drawlib.page;

import com.zhengsr.drawlib.action.BaseAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * created by zhengshaorui on 2019/7/11
 * Describe:存储绘画笔记
 */
public class PageDataManager {
   private HashMap<Integer,List<BaseAction>> mPageData = new LinkedHashMap<>();

   private PageDataManager(){}
   private static class Holder{
       static PageDataManager HODLER = new PageDataManager();
   }


   public static PageDataManager getInstance(){
       return Holder.HODLER;
   }


   public void addNote(int pageId,List<BaseAction> command){
       mPageData.remove(pageId);
       mPageData.put(pageId,new ArrayList<BaseAction>(command));
   }

   public List<BaseAction> getNote(int pageId){
       return mPageData.get(pageId);
   }

   public void delete(int pageId){
       mPageData.remove(pageId);
   }

   public void clear(){
       mPageData.clear();
   }

   public void clear(int pageId){
       mPageData.put(pageId,null);
   }

    public HashMap<Integer, List<BaseAction>> getPageData() {
        return mPageData;
    }
}
