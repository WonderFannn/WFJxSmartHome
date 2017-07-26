package com.jinxin.datan.toolkit.task;

import android.text.TextUtils;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.util.Logger;

import com.jinxin.datan.toolkit.internet.IdataObserver;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * 通信事件流程控制抽象类(启动，状态监听，处理)
 * 
 * @author zj
 * 
 */
public abstract class Task implements ITask, IdataObserver, Runnable {

	private boolean mTryCancel;//是否取消任务
	private boolean closeSocketLong;//是否关闭socketLong的情况
	private int mState;
	private Object[] receiverSuccessObject = null;// 联网成功的数据接收
	private Object[] receiverFailObject = null;// 联网失败的数据接收
	private Object[] receiverCanceledObject = null;// 联网中止的数据接收

	protected Task() {
		mTryCancel = false;
		closeSocketLong = false;
		mState = STATE_INIT;
	}

	@Override
	public void callback(Object... obj) {
		// TODO Auto-generated method stub
		this.receiverSuccessObject = obj;
	}

	@Override
	public void onError(Object... obj) {
		// TODO Auto-generated method stub
		this.receiverFailObject = obj;
	}
	
	@Override
	public void onCancel(Object... obj) {
		// TODO Auto-generated method stub
		this.receiverCanceledObject = obj;
	}
	@Override
	public void onProcess(Object... obj) {
		// TODO Auto-generated method stub
		this.onProcess(this, obj);
	}

	public boolean isEnd() {
		return mState != STATE_RUNNING && mState != STATE_INIT;
	}
	/**
	 * 任务是否取消状态
	 * @return
	 */
	public boolean ismTryCancel() {
		return mTryCancel;
	}
	/**
	 * 取消中途联网（联网结束调用无效）
	 */
	public void cancel() {
		closeSocketLong = true;
		mTryCancel = true;
	}
	/**
	 * 关闭socket长连接的状态
	 * @return
	 */
	public boolean isCloseSocketLong(){
		return closeSocketLong;
	}
	/**
	 * 设置是否在本次task联网结束之后关闭长连接（联网结束调用无效）
	 * 默认为false
	 */
	public void setcloseSocketLongAfterTaskFinish(boolean closeSocketLong){
		this.closeSocketLong = closeSocketLong;
	}
	public void run() {
		onStarted(this, null);
		// onStarted(this, null);
		mState = STATE_RUNNING;
		mState = excute();
		onEnd();
	}

	protected void onEnd() {
		if (mState == STATE_SUCCESS) {
			onSuccess(this, this.receiverSuccessObject);
		} else if (mState == STATE_FAIL) {//连接失败
//			onFail(this, this.receiverFailObject);
//			this.receiverFailObject = new Object[]{-1};
			
//			JxshApp.showToast(JxshApp.getContext(), CommDefines
//					.getSkinManager()
//					.string(R.string.mode_contorl_fail_net));
			onFail(this, this.receiverFailObject);
		}else if(mState == STATE_CANCELED){
			onFail(this, this.receiverCanceledObject);
		}
	}

	public int getState() {
		return mState;
	}

	/**
	 * 开始执行当前线程
	 */
	public void start() {
//		new Thread(this).start();
		Thread thread = new Thread(this);
		thread.start();
		
	}
	/**
	 * 开始执行当前线程（禁重）
	 * @param name 设置Thread名字（如果当前活动线程中存在同名，则不启动）
	 */
	public void start(String name) {
		if(TextUtils.isEmpty(name)){
			Thread thread = new Thread(this);
			thread.start();
		}else{
			Thread[]threads = findAllThreads();
			int len = threads.length;
			for(int i = 0;i < len;i++){
				if(name.equals(threads[i].getName())){
					Logger.error("Task","发现重复任务：禁止["+name+"]");
					return;
				}
			}
			
			Thread thread = new Thread(this);
			thread.setName(name);
			thread.start();
			
		}
		
	}
	/**
	 * 得到当前系统的活动线程
	 * @return
	 */
	public static Thread[] findAllThreads() {
		ThreadGroup group = 
		Thread.currentThread().getThreadGroup();
		ThreadGroup topGroup = group;

		        // 遍历线程组树，获取根线程组
		while ( group != null ) {
		topGroup = group;
		group = group.getParent();
		}
		        // 激活的线程数加倍
		int estimatedSize = topGroup.activeCount() * 2;
		Thread[] slackList = new Thread[estimatedSize];
		        //获取根线程组的所有线程
		int actualSize = topGroup.enumerate(slackList);
		// copy into a list that is the exact size
		Thread[] list = new Thread[actualSize];
		System.arraycopy(slackList, 0, list, 0, actualSize);
		return list;
		} 

	// ////////////////////////////

	private ArrayList<ITaskListener> listeners = new ArrayList<ITaskListener>();

	public void addListener(ITaskListener t) {
		listeners.add(t);
	}

	public void removeListener(ITaskListener t) {
		listeners.remove(t);
	}

	public void clearListeners() {
		listeners.clear();
	}

	// ///////////////////////////////

	protected void onStarted(Task task, Object arg) {
		for (ITaskListener tl : listeners) {
			tl.onStarted(task, arg);
		}
	}

	protected void onCanceled(Task task, Object arg) {
		for (ITaskListener tl : listeners) {
			tl.onCanceled(task, arg);
		}
	}

	protected void onFail(Task task, Object[] arg) {
		for (ITaskListener tl : listeners) {
			tl.onFail(task, arg);
		}
	}

	protected void onSuccess(Task task, Object[] arg) {
		for (ITaskListener tl : listeners) {
			tl.onSuccess(task, arg);
		}
	}
	protected void onProcess(Task task, Object[] arg) {
		for (ITaskListener tl : listeners) {
			tl.onProcess(task, arg);
		}
	}

	// ///////////////////////////////////////
	/**
	 * 字符串转utf-8数组
	 * 
	 * @param str
	 * @return
	 */
	protected byte[] turnStringToUtf8byte(String str) {
		if (str == null)
			return null;
		try {
			return str.getBytes("utf-8");
		} catch (UnsupportedEncodingException ex) {
			Logger.error("Task", ex.toString());
			return null;
		}
	}
}
