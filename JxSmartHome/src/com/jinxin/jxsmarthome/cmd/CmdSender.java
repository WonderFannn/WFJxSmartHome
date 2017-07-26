package com.jinxin.jxsmarthome.cmd;

import java.util.List;
import java.util.Stack;

import android.content.Context;

import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.datan.toolkit.task.TaskListener;

/**
 * 离线命令处理的抽象类
 * @author  TangLong
 * @company 金鑫智慧
 */
public abstract class CmdSender<T> implements ICmdSender {
	protected boolean showLoading;
	protected Context context;
	protected Stack<T> cmdStack;
	protected TaskListener<ITask> listener;
	
	/**
	 * 发送一条命令
	 * @param context
	 * @param cmd		
	 */
	public CmdSender(Context context, T cmd) {
		this.context = context;
		this.cmdStack = new Stack<T>();
		this.cmdStack.add(cmd);
		this.showLoading = true;
	}
	
	/**
	 * 默认的命令发送顺序
	 * @param context
	 * @param cmdList
	 */
	public CmdSender(Context context, List<T> cmdList) {
		this(context, cmdList, false);
	}
	
	/**
	 * 指定命令放松顺序(在某些模块的命令发送中，如功放的命令发送，对命令的顺序有要求)
	 * @param context
	 * @param cmdList
	 * @param fromTop
	 */
	public CmdSender(Context context, List<T> cmdList, boolean fromTop) {
		this.context = context;
		this.cmdStack = new Stack<T>();
		if(fromTop) {
			for(int i = cmdList.size() - 1; i >= 0; i--) {
				cmdStack.add(cmdList.get(i));
			}
		}else {
			for(int i = 0; i < cmdList.size(); i++) {
				cmdStack.add(cmdList.get(i));
			}
		}
	}
	
	@Override
	public void send(String host) {
		// do nothing, some subclass will implements it
	}
	
	public CmdSender<T> addListener(TaskListener<ITask> listener) {
		this.listener = listener;
		return this;
	}
}
