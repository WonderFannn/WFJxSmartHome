package com.jinxin.datan.toolkit.task;
/**
 * 通信事件基础接口定义
 * @author zj
 *
 */
public interface ITask {

    final public int STATE_INIT = 0;
    final public int STATE_RUNNING = 1;
    final public int STATE_SUCCESS = 2;
    final public int STATE_FAIL = 3;
    final public int STATE_CANCELED = 4;

    /**
     * excute the task in current thread
     * 
     * @return the state of the excute result
     */
    public int excute();
/**
 * 取消任务
 */
    public void cancel();
/**
 * 任务状态
 * @return
 */
    public int getState();
}
