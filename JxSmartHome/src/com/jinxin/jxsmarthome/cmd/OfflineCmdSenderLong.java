package com.jinxin.jxsmarthome.cmd;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.content.Context;

import com.jinxin.datan.net.command.CommonDeviceControlByServerTask;
import com.jinxin.datan.net.command.CommonDeviceControlByServerTask2;
import com.jinxin.datan.net.module.RemoteJsonResultInfo;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.datan.toolkit.task.Task;
import com.jinxin.datan.toolkit.task.TaskListener;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.util.Logger;

/**
 * 通用的离线命令发送（不处理返回结果）
 * @author  TangLong
 * @company 金鑫智慧
 */
public class OfflineCmdSenderLong extends CmdSender<byte[]> {
	private String host;
	private Task cdcbsTask;
	private int requestType = 0;
	private boolean isMulit = false;
	
	private List<byte[]> cmdList = null;

	/**
	 * 默认的命令发送顺序
	 * @param context
	 * @param cmdList
	 */
	public OfflineCmdSenderLong(Context context, List<byte[]> cmdList) {
		this(context, cmdList,false);
	}
	
	/**
	 * 指定命令放松顺序(在某些模块的命令发送中，如功放的命令发送，对命令的顺序有要求)
	 * @param context
	 * @param cmdList
	 * @param fromTop
	 */
	public OfflineCmdSenderLong(Context context, List<byte[]> cmdList, boolean fromTop) {
		this(context,null, cmdList, fromTop,false);
	}
	
	/**
	 * 指定命令发送的目的服务器和发送顺序
	 * @param context
	 * @param host
	 * @param cmdList
	 * @param fromTop
	 */
	public OfflineCmdSenderLong(Context context, String host, List<byte[]> cmdList, boolean fromTop,boolean isMulit) {
		super(context, cmdList, fromTop);
		this.host = host;
		this.isMulit = isMulit;
		this.cmdList = cmdList;
	}
	
	/**
	 * 发送命令
	 */
	@Override
	public void send() {
		Logger.debug(null, "offline cmd size:" + cmdStack.size());
		
		/*----------合并所有指令一次发送----------------- */
		if (cmdList != null && cmdList.size() > 0) {
			byte[] allCmd = null;
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				for (int i = 0; i < cmdList.size(); i++) {
					bos.write(cmdList.get(i));
				}
				allCmd = bos.toByteArray();
				Logger.debug(null, "send:" + new String(allCmd));
			} catch (IOException e) {
				e.printStackTrace();
			}
				
			if (allCmd != null) {
				cdcbsTask =  new CommonDeviceControlByServerTask2(context, host, allCmd, true, requestType);
				
				cdcbsTask.addListener(new ITaskListener<ITask>() {

					@Override
					public void onStarted(ITask task, Object arg) {
						Logger.debug(null, "OfflineCmdSenderLong onStarted");
						if(listener != null)
							listener.onStarted(task, arg);
					}

					@Override
					public void onCanceled(ITask task, Object arg) {
						Logger.debug(null, "OfflineCmdSenderLong onCanceled");
						if(listener != null)
							listener.onCanceled(task, arg);
						doIfFinish();
					}

					@Override
					public void onFail(ITask task, Object[] arg) {
						Logger.debug(null, "OfflineCmdSenderLong onFail");
						if(listener != null) {
							listener.onFail(task, arg);
							listener.onAnyFail(task, arg);
						}
						doIfFinish();
					}

					@Override
					public void onSuccess(ITask task, Object[] arg) {
						Logger.debug(null, "OfflineCmdSenderLong onSuccess");
						doIfFinish();
						if(listener != null) {
							listener.onSuccess(task, arg);
							listener.onAllSuccess(task, arg); 
						}
					}

					@Override
					public void onProcess(ITask task, Object[] arg) {
						
					}
				});
				JxshApp.getDatanAgentHandler().postDelayed(cdcbsTask, 50);
			}
		}else{
			doIfFinish();
		}
		/*---------------END------------------------- */
		
//		byte[] _ppo = (byte[]) cmdStack.pop();
//
//		if (_ppo == null) {
//			JxshApp.closeLoading();
//			doIfFinish();
//			Logger.debug(null, "onfail...");
//			if(listener != null) {
//				listener.onFail(null, null);
//				if(cmdStack.isEmpty()) listener.onAnyFail(null, null);
//			}
//			if(!cmdStack.isEmpty()){
//				send();
//			}
//		}else {
//			byte[] cmd = _ppo;
//
//			cdcbsTask =  new CommonDeviceControlByServerTask2(context, host, cmd, true, requestType);
//			Logger.debug(null, "cmd:" + new String(cmd));
//			Logger.debug(null, "host:" + host);
//			
//			cdcbsTask.addListener(new ITaskListener<ITask>() {
//
//				public void onStarted(ITask task, Object arg) {
//					Logger.debug(null, "onstart...");
//					if(listener != null)
//						listener.onStarted(task, arg);
//				}
//
//				@Override
//				public void onCanceled(ITask task, Object arg) {
//					Logger.debug(null, "oncancel...");
//					JxshApp.closeLoading();
//					if(listener != null)
//						listener.onCanceled(task, arg);
//				}
//
//				@Override
//				public void onFail(ITask task, Object[] arg) {
//					JxshApp.closeLoading();
//					doIfFinish();
//					Logger.debug(null, "onfail...");
//					if(arg != null && arg.length > 0 && arg[0] instanceof RemoteJsonResultInfo) {
//						RemoteJsonResultInfo result = (RemoteJsonResultInfo)arg[0];
//						Logger.debug(null, result.validResultCode + "," + result.validResultInfo);
//					}
//					if(listener != null) {
//						listener.onFail(task, arg);
//						if(cmdStack.isEmpty()) listener.onAnyFail(task, arg);
//					}
//					if(!cmdStack.isEmpty()){
//						send();
//					}
//				}
//
//				@Override
//				public void onSuccess(ITask task, Object[] arg) {
//					Logger.debug(null, "onSuccess...");
//					doIfFinish();
//					if(listener != null) {
//						listener.onSuccess(task, arg);
//						if(cmdStack.isEmpty()) listener.onAllSuccess(task, arg);
//					}
//					if(!cmdStack.isEmpty()){
//						send();
//					}
//				}
//
//				@Override
//				public void onProcess(ITask task, Object[] arg) {
//
//				}
//			});
//			JxshApp.getDatanAgentHandler().postDelayed(cdcbsTask, 50);
//		}
	}

	private void doIfFinish() {
//		if (cmdStack == null || cmdStack.empty()) {
			JxshApp.closeLoading();
			if (cdcbsTask != null) {
				try {
					TimeUnit.MILLISECONDS.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
//				cdcbsTask.setcloseSocketLongAfterTaskFinish(true);
				cdcbsTask.clearListeners();
				((InternetTask) cdcbsTask).clearSocketConnector();
			}
			//释放按钮权限
			JxshApp.instance.isClinkable = false;
			if(listener instanceof TaskListener) ((TaskListener<?>)listener).onFinish();
//		}
	}
	
	public void setRequestType(int type) {
		this.requestType = type;
	}
}
