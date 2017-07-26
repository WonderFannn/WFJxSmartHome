package com.jinxin.datan.toolkit.task;
/**
 * 通信事件监听接口
 * @author zj
 *
 * @param <T>
 */
public interface ITaskListener<T extends ITask> {

    public void onStarted(T task, Object arg);

    public void onCanceled(T task, Object arg);

    public void onFail(T task, Object[] arg);

    public void onSuccess(T task, Object[] arg);
    
    public void onProcess(T task, Object[] arg);
    
}
