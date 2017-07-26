package com.jinxin.jxsmarthome.cmd;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.core.Arrays;
import android.content.Context;
import android.text.TextUtils;

import com.jinxin.datan.net.command.CommonDeviceControlByServerTask;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.datan.toolkit.task.TaskListener;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.util.Logger;

/**
 * 在线发送命令
 * @author TangLong
 * @company 金鑫智慧
 */
public class OnlineCmdSenderLong extends CmdSender<byte[]> {
	
	private int doCMDResultToast = -1;
	private int resultCount = 0;
	private InternetTask cdcbsTask;
	private String host;
	private boolean isZigbee;
	private int type;
	private boolean isAllCmdSuccessed = true;
	private List<byte[]> cmdList = new ArrayList<byte[]>();
	/**是否多网关*/
	private boolean isMulit = false;
	
	/**
	 * 发送一条命令
	 * @param context
	 * @param cmd	命令集合	
	 */
	public OnlineCmdSenderLong(Context context, byte[] cmd) {
		this(context, cmd,false);
	}
	/**
	 * 发送一条命令
	 * @param context
	 * @param cmd	命令集合	
	 */
	public OnlineCmdSenderLong(Context context, byte[] cmd,boolean isMulit) {
		super(context, cmd);
		this.isMulit = isMulit;
		this.cmdList.add(cmd);
	}
	/**
	 * 默认的命令发送顺序
	 * @param context
	 * @param cmdList	命令集合
	 */
	public OnlineCmdSenderLong(Context context, List<byte[]> cmdList) {
		this(context, cmdList,false);
	}
	

	
	/**
	 * 指定命令放松顺序(在某些模块的命令发送中，如功放的命令发送，对命令的顺序有要求)
	 * @param context
	 * @param cmdList	命令集合
	 * @param fromTop	是否按顺序发送命令
	 */
	public OnlineCmdSenderLong(Context context, List<byte[]> cmdList, boolean fromTop) {
		this(context,null, cmdList, fromTop);
	}
	
	/**
	 * 指定命令发送的目的服务器和发送顺序
	 * @param context
	 * @param host		地址
	 * @param cmdList	命令集合
	 * @param fromTop	是否按顺序发送命令
	 */
	public OnlineCmdSenderLong(Context context, String host, List<byte[]> cmdList, boolean fromTop) {
		this(context,host,false, cmdList, fromTop,0,false);
//		this.host = host;
	}
	
	/**
	 * 指定命令发送的目的服务器和发送顺序以及命令类型
	 * @param context
	 * @param host		地址
	 * @param cmdList	命令集合
	 * @param fromTop	是否按顺序发送命令
	 * @param type	控制或查询 请求类型	0：操作    1：查询
	 * @param isMulit	是否多网关
	 */
	public OnlineCmdSenderLong(Context context, String host, boolean isZigbee, List<byte[]> cmdList, boolean fromTop, int type,boolean isMulit) {
		super(context, cmdList, fromTop);
		this.isZigbee = isZigbee;
		this.host = host;
		this.type = type;
		this.isMulit = isMulit;
		this.cmdList = cmdList;
	}
	
	/**
	 * @deprecated
	 * @param context
	 * @param cmdList
	 * @param fromTop
	 * @param doCMDResultToast
	 */
	public OnlineCmdSenderLong(Context context, List<byte[]> cmdList,
			boolean fromTop, int doCMDResultToast) {
		super(context, cmdList, fromTop);
		this.doCMDResultToast = doCMDResultToast;
	}

	@Override
	public void send() {
		Logger.debug(null, "OnlineCmdSenderLong send...");
		Logger.debug(null, "cmd size:" + cmdStack.size());
//		if(showLoading) JxshApp.showLoading(context, null);
		/*----------合并所有指令一次发送----------------- */
		if (cmdList != null && cmdList.size() > 0) {
			byte[] allCmd = null;
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				for (int i = 0; i < cmdList.size(); i++) {
					if (cmdList.get(i) != null) {
						bos.write(cmdList.get(i));
					}
				}
				allCmd = bos.toByteArray();
				Logger.debug(null, "send:" + new String(allCmd));
			} catch (IOException e) {
				e.printStackTrace();
			}
				
			if (allCmd != null) {
				cdcbsTask =  new CommonDeviceControlByServerTask(context, allCmd, true, type);
				if(host != null) {
					if(isZigbee) {
						Logger.debug(null, "is zigbee device");
						((CommonDeviceControlByServerTask)cdcbsTask).setRequestHost(host, 1);
					}else {
						Logger.debug(null, "not zigbee device");
						((CommonDeviceControlByServerTask)cdcbsTask).setRequestHost(host, 0);
					}
				}
				cdcbsTask.addListener(new ITaskListener<ITask>() {

					@Override
					public void onStarted(ITask task, Object arg) {
						Logger.debug(null, "OnlineCmdSenderLong onStarted");
						if(listener != null)
							listener.onStarted(task, arg);
					}

					@Override
					public void onCanceled(ITask task, Object arg) {
						Logger.debug(null, "OnlineCmdSenderLong onCanceled");
						doIfFinish();
						if(listener != null)
							listener.onCanceled(task, arg);
					}

					@Override
					public void onFail(ITask task, Object[] arg) {
						Logger.debug(null, "OnlineCmdSenderLong onFail");
						isAllCmdSuccessed = false;
						doIfFinish();
						if(listener != null) {
							listener.onFail(task, arg);
							listener.onAnyFail(task, arg);
						}
					}

					@Override
					public void onSuccess(ITask task, Object[] arg) {
						Logger.debug(null, "OnlineCmdSenderLong onSuccess");
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
				cdcbsTask.start();
			}
		}else{
			doIfFinish();
		}
		/*---------------END------------------------- */
		
//		if(cmdStack.isEmpty()){
//			doIfFinish();
//			return;
//		}
//		
//		showLoading = false;
//		byte[] _ppo = cmdStack.pop();
//		
//		if (_ppo == null) {
//			isAllCmdSuccessed = false;
//			Logger.debug(null, "OnlineCmdSenderLong onFail...");
//			doIfFinish();
//			if(listener != null) {
//				listener.onFail(null, null);
//				if(cmdStack.isEmpty() && !isAllCmdSuccessed) listener.onAnyFail(null, null);
//			}
//			if(!cmdStack.isEmpty()){
//				send();
//			}
//			JxshApp.closeLoading();
//			resultCount++;
//			if (resultCount == doCMDResultToast) {
//				JxshApp.showToast(context,CommDefines.
//						getSkinManager().string(R.string.mode_contorl_fail));
//			}
//		}else {
//			byte[] cmd = _ppo;
//			
//			Logger.debug(null, "send:" + new String(cmd));
//			Logger.error(null, "cmd = "+Arrays.toString(cmd));
//
//			cdcbsTask =  new CommonDeviceControlByServerTask(context, cmd, true, type);
//			if(host != null) {
//				if(isZigbee) {
//					Logger.debug(null, "is zigbee device");
//					((CommonDeviceControlByServerTask)cdcbsTask).setRequestHost(host, 1);
//				}else {
//					Logger.debug(null, "not zigbee device");
//					((CommonDeviceControlByServerTask)cdcbsTask).setRequestHost(host, 0);
//				}
//			}
//			
//			cdcbsTask.addListener(new ITaskListener<ITask>() {
//
//				public void onStarted(ITask task, Object arg) {
//					Logger.debug(null, "OnlineCmdSenderLong onstart...");
//					if(listener != null)
//						listener.onStarted(task, arg);
//					resultCount++;
//				}
//
//				@Override
//				public void onCanceled(ITask task, Object arg) {
//					Logger.debug(null, "OnlineCmdSenderLong onCanceled...");
//					if(listener != null)
//						listener.onCanceled(task, arg);
//				}
//
//				@Override
//				public void onFail(ITask task, Object[] arg) {
//					isAllCmdSuccessed = false;
//					Logger.debug(null, "OnlineCmdSenderLong onFail...");
//					doIfFinish();
//					if(listener != null) {
//						listener.onFail(task, arg);
//						if(cmdStack.isEmpty() && !isAllCmdSuccessed) listener.onAnyFail(task, arg);
//					}
//					if(!cmdStack.isEmpty()){
//						send();
//					}
//					JxshApp.closeLoading();
//					resultCount++;
//					if (resultCount == doCMDResultToast) {
//						if (arg != null && arg.length > 0) {
//							RemoteJsonResultInfo resultInfo = (RemoteJsonResultInfo) arg[0];
//							JxshApp.showToast(context,resultInfo.validResultInfo);
//						}else{
//							JxshApp.showToast(context,CommDefines.
//									getSkinManager().string(R.string.mode_contorl_fail));
//						}
//					}
//				}
//
//				@Override
//				public void onSuccess(ITask task, Object[] arg) {
//					Logger.debug(null, "OnlineCmdSenderLong onSuccess...");
//					resultCount++;
//					doIfFinish();
//					if(listener != null) {
//						listener.onSuccess(task, arg);
//						Logger.debug(null, "OnlineCmdSenderLong cmdStack："+cmdStack.size()+"|"+isAllCmdSuccessed);
//						if(cmdStack.isEmpty() && isAllCmdSuccessed) 
//						{
//							listener.onAllSuccess(task, arg);
//						}
//					}
//					if(!cmdStack.isEmpty()){
//						send();
//					}
//					if (resultCount == doCMDResultToast) {
//						JxshApp.showToast(context, CommDefines
//								.getSkinManager()
//								.string(R.string.mode_contorl_success));
//					}
//				}
//
//				@Override
//				public void onProcess(ITask task, Object[] arg) {
//
//				}
//			});
//			cdcbsTask.start();
//		}
	}
	/**
	 * 完成队列任务时的处理
	 * 关闭socket，回调onFinish,释放按钮
	 */
	private void doIfFinish() {
//		if (cmdStack == null || cmdStack.empty()) {
			JxshApp.closeLoading();
			//释放按钮权限
			if (cdcbsTask != null) {
//				if(listener != null)
//					listener.onFinish();
				Logger.debug(null, "OnlineCmdSenderLong -----关闭socket连接-------");
//				cdcbsTask.setcloseSocketLongAfterTaskFinish(true);
				cdcbsTask.clearSocketConnector();
			}
			JxshApp.instance.isClinkable = false;
			Logger.debug(null, "OnlineCmdSenderLong onclick is on");
			if(listener instanceof TaskListener) ((TaskListener<?>)listener).onFinish();
//		}
	}
	
}
