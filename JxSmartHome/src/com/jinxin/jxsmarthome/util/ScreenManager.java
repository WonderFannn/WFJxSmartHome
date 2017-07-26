
package com.jinxin.jxsmarthome.util;

import java.util.Stack;
import android.app.Activity;
import com.jinxin.jxsmarthome.util.Logger;

/**
 * 界面记录器（保存浏览轨迹）
 * 
 * @author zj
 */
public class ScreenManager {
    /**
     * 默认值（无处理）
     */
    public static final int DEFAULT = -1;
    /**
     * 添加新界面
     */
    public static final int ADD_NEW_SCREEN = 0;
    /**
     * 清除所有界面
     */
    public static final int CLEAR_ALL_SCREEN = 1;
    /**
     * 更新历史到当前跳转后显示的界面（发现当前显示界面以存在，则弹掉其上的界面并且删除自己的记录）
     */
    public static final int CHECK_CURRENT_SCREEN = 2;
    /**
     * 更新历史到正在保存的界面（发现当前正在保存界面以存在，则弹掉其上的界面，并且判断正在保存的界面是否需要保存，若不保存也弹掉）
     */
    public static final int CHECK_SAVING_SCREEN = 3;
    
    private Stack<Class<?>> activityStack;

    private static ScreenManager instance;

    private ScreenManager() {

    }

    public static ScreenManager instance() {
        if (instance == null) {
            instance = new ScreenManager();
        }
        return instance;
    }
    
    /**
     * 获取界面数量
     * @return
     */
    public int getStackSize(){
        if(this.activityStack != null){
            return this.activityStack.size();
        }
        return 0;
    }
/**
 * 弹掉最上层的activity.class
 */
    public void popActivityClass() {
        activityStack.remove(this.getStackSize()-1);
    }

    /**
     * 弹掉指定activity.class
     * @param Class
     */
    public void popActivityClass(Class<?> cls) {
        if (cls != null) {
            activityStack.remove(cls);
        }
    }
    
 /**
  * 取当前保存的activity.class
  * @return
  */
    public Class<?> currentActivityClass() {
        if(activityStack == null || activityStack.size() <= 0)return null;
        Class<?> cls = activityStack.lastElement();
        return cls;
    }

    /**
     * 压入activity.class
     * @param class
     */
    public void pushActivityClass(Class<?> cls) {
        if (activityStack == null) {
            activityStack = new Stack<Class<?>>();
        }
        activityStack.add(cls);
    }
    /**
     * 检测Activity.class是否存在
     * @param cls
     */
    public boolean isExist(Class<?> cls) {
        if(activityStack == null || activityStack.size() <= 0)return false;
        for(int i = 0; i < activityStack.size();i++){
            Class<?> _cls = activityStack.get(i);
            if(_cls.equals(cls)){
                return true;
            }
        }
        return false;

    }
    /**
     * 弹掉指定Activity.class上面的所有Activity.class（不包括自己）
     * @param cls
     */
    public void popAllActivityExceptOne(Class<?> cls) {
        while (true) {
            Class<?> _cls = currentActivityClass();
            if (_cls == null) {
                break;
            }
            if (_cls.equals(cls)) {
                break;
            }
            popActivityClass(_cls);

        }

    }
    /**
     * 弹掉指定Activity.class上面的所有Activity.class(包括自己)
     * @param cls
     */
    public void popAllActivityIncludeOne(Class<?> cls) {
        while (true) {
            Class<?> _cls = currentActivityClass();
            if (_cls == null) {
                break;
            }
            if (_cls.equals(cls)) {
                popActivityClass(_cls);
                break;
            }
            popActivityClass(_cls);

        }

    }
    /**
     * 弹掉指定保留数量上面的所有Activity.class
     * @param num 保留的历史数量
     */
    public void popAllActivityExceptNum(int num) {
        while (true) {
            Class<?> _cls = currentActivityClass();
            if (_cls == null) {
                break;
            }
            if (this.getStackSize() <= num) {
                break;
            }
            popActivityClass(_cls);
        }

    }
    /**
     * 弹掉所有ActivityClass
     * @param cls
     */
    public void popAllActivityClass() {
        while (true) {
            Class<?> _cls = currentActivityClass();
            if (_cls == null) {
                break;
            }
            popActivityClass(_cls);
        }
    }
    
    /**
     * activity.class插入到指定位置
     * @param Class
     * @param location
     */
    public void insertActivityClass(Class<?> cls,int location){
        if (activityStack == null) {
            activityStack = new Stack<Class<?>>();
        }
            activityStack.insertElementAt(cls, location);
    }
    

}
