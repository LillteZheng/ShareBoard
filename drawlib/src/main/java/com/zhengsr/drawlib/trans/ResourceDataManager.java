package com.zhengsr.drawlib.trans;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Path;

import com.zhengsr.drawlib.action.BaseAction;
import com.zhengsr.drawlib.action.EraseAction;
import com.zhengsr.drawlib.action.PenAction;
import com.zhengsr.drawlib.action.PenLightAction;
import com.zhengsr.drawlib.type.DrawActionType;
import com.zhengsr.drawlib.type.PenType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * created by zhengshaorui on 2019/7/18
 * Describe: 用来保存当前的path，以便于存储源文件或者用于传输
 */
public class ResourceDataManager {


    private ResourceDataManager(){}
    private static class Holder{
        static ResourceDataManager HODLER = new ResourceDataManager();
    }


    public static ResourceDataManager getInstance(){
        return Holder.HODLER;
    }

    private HashMap<Integer,ResourcePath> mTempPaths = new LinkedHashMap<>();
    private List<ResourcePath> mPaths = new ArrayList<>();

    public void lineStart(float x, float y, int pointId,int drawType,int penType,int penColor,int penSize){
        ResourcePath transPath = new ResourcePath();
        transPath.penSize = penSize;
        transPath.penColor = penColor;
        transPath.penType = penType;
        transPath.drawType = drawType;
        transPath.pointId = pointId;
        transPath.points.add(new ResourcePoint(x,y));
        mTempPaths.put(pointId,transPath);
    }

    public void lineMove(float x,float y,int pointId){
        ResourcePath transPath = mTempPaths.get(pointId);
        if (transPath == null){
            transPath = new ResourcePath();
        }
        transPath.points.add(new ResourcePoint(x,y));
    }

    public void lineUp(float x,float y,int pointId){
        ResourcePath transPath = mTempPaths.get(pointId);
        if (transPath == null){
            transPath = new ResourcePath();
        }
        transPath.points.add(new ResourcePoint(x,y));
        mPaths.add(transPath);
        mTempPaths.remove(pointId);
    }

    /**
     * 拿到梳篦
     * @param transPath
     * @return
     */
    public Paint getPaint(ResourcePath transPath){
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setFilterBitmap(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(transPath.penSize);
        paint.setColor(transPath.penColor);
        return paint;
    }

    /**
     * 拿到所有的path
     * @return
     */
    public List<ResourcePath> getTransPaths() {
        return mPaths;
    }

    /**
     * 获取转换之后的path
     * @param transPath
     * @return
     */
    public Path getPath(ResourcePath transPath){
        Path path = new Path();
        int size = transPath.points.size();

        ResourcePoint tp = transPath.points.get(0);
        path.moveTo(tp.x,tp.y);
        float tx = tp.x;
        float ty = tp.y;
        for (int i = 1; i < size - 1; i++) {
            ResourcePoint p = transPath.points.get(i);
            path.quadTo(tx, ty, (p.x + tx) / 2, (p.y + ty) / 2);
            tx = p.x;
            ty = p.y;
        }
        ResourcePoint upPoint = transPath.points.get(size - 1);
        path.lineTo(upPoint.x,upPoint.y);
        return path;
    }

    /**
     * 清除数据
     */
    public void clear(){
        mTempPaths.clear();
        mPaths.clear();
    }

    /**
     * 恢复源文件
     * @param bg
     * @return
     */
    public List<BaseAction> loadResource(List<ResourcePath> transPaths, Bitmap bg){
        if (transPaths != null) {
            int size = transPaths.size();
            List<BaseAction> actions = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                ResourcePath transPath = transPaths.get(i);
                BaseAction action = getAction(transPath, bg);
                if (action != null) {
                    actions.add(action);
                }
            }
            return actions;
        }
       return new ArrayList<>();
    }

    /**
     * 通过tranpath 把数据传输成 baseaction
     * @param tp
     * @param bg
     * @return
     */
    private BaseAction getAction(ResourcePath tp, Bitmap bg){
        DrawActionType drawType = DrawActionType.values()[tp.drawType];
        BaseAction action = null;
        switch (drawType){
            case PEN:
                PenType penType = PenType.values()[tp.penType];
                if (penType == PenType.PEN) {
                    PenAction penAction = new PenAction();
                    penAction.setPenColor(tp.penColor);
                    penAction.setPenSize(tp.penSize);
                    action = getActionPath(tp,penAction);
                }else if(penType == PenType.LIGHT_PEN){
                    PenLightAction lightAction = new PenLightAction();
                    lightAction.setPenColor(tp.penColor);
                    lightAction.setPenSize(tp.penSize);
                    action = getActionPath(tp,lightAction);
                }
                break;
            case ERASE:
                EraseAction eraseAction = new EraseAction();
                eraseAction.setEraseSize(tp.penSize);
                eraseAction.setbgBitmap(bg);
                action = getActionPath(tp,eraseAction);
                break;
            default:
                break;
        }
        return action;

    }


    /**
     * 把数据写到 baseaction里面
     * @param transPath
     * @param action
     * @return
     */
    private BaseAction getActionPath(ResourcePath transPath, BaseAction action){
        BaseAction baseAction = action;
        int size = transPath.points.size();
        ResourcePoint tp = transPath.points.get(0);
        int pointId = transPath.pointId;
        baseAction.down(tp.x,tp.y,pointId);

        for (int i = 1; i < size - 1; i++) {
            ResourcePoint p = transPath.points.get(i);
            baseAction.move(p.x,p.y,pointId);
        }
        ResourcePoint upPoint = transPath.points.get(size - 1);
        baseAction.up(upPoint.x,upPoint.y,pointId);
        return baseAction;
    }

    /**
     * 加载数据
     * @param path
     * @param name
     * @return
     */
    private List<ResourcePath> loadFile(String path, String name){
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(new File(path,name));
            ois = new ObjectInputStream(fis);
            return (List<ResourcePath>) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ois != null){
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }


    /**
     * 保存原始数据
     * @param path
     * @param name
     */
    private void saveToFile(String path,String name){
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            File file = new File(path,name);
            if (file.exists()){
                file.delete();
            }
            fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(mPaths);
            oos.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (oos != null){
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
