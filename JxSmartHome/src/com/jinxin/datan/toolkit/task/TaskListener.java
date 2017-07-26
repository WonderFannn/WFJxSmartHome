package com.jinxin.datan.toolkit.task;
/**
 * 监听的抽象类
 * @author TangLong
 * @param <T>
 */
public class TaskListener<T extends ITask> implements ITaskListener<T>{

    public void onStarted(T task, Object arg) {}

    public void onCanceled(T task, Object arg) {}

    public void onFail(T task, Object[] arg) {}

    public void onSuccess(T task, Object[] arg) {}
    
    public void onProcess(T task, Object[] arg) {}
    
    /* 全部命令发送完成时的的回调 */
    public void onFinish() {}
    
    /* 全部命令发送完成并且所有命令成功时的回调  */
    public void onAllSuccess(T task, Object[] arg) {}
    
    /* 全部命令发送完成但有任何一条命令发送失败时的回调  */
    public void onAnyFail(T task, Object[] arg) {}
}
