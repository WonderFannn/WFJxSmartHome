package com.jinxin.jxsmarthome.cmd;

import java.util.List;

import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.TaskListener;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.cmd.entity.Command;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.util.Logger;

import android.content.Context;

/**
 * 多网关在线发送命令
 * @author TangLong
 * @company 金鑫智慧
 */
public class OfflineMulitGatewayCmdSender extends CmdSender<Command> {

	private boolean isAllCmdSuccessed = true;
	
	public OfflineMulitGatewayCmdSender(Context context, List<Command> cmdList) {
		super(context, cmdList);
	}

	@Override
	public void send() {
		Logger.warn(null, "command size:" + cmdStack.size());
		if(cmdStack == null) return;
		if(!cmdStack.isEmpty()) {
			Command command = cmdStack.pop();
			if(command == null) return;
			
			// debug
			Logger.warn(null, command.toString());
			List<byte[]> cmds = command.getCmdList();
			for(byte[] b : cmds) {
				Logger.debug(null, new String(b));
			}
			
			// send cmd
			OfflineCmdSenderLong cmdSender = new OfflineCmdSenderLong(context, command.getGatewayLocalIp(), 
					command.getCmdList(), true,true);
			cmdSender.addListener(new TaskListener<ITask>() {
				@Override
				public void onFinish() {
//					send();
				}
				
				@Override
				public void onAllSuccess(ITask task, Object[] arg) {
					super.onAllSuccess(task, arg);
					if(listener != null) {
						listener.onSuccess(task, arg);
						if(cmdStack.isEmpty() && isAllCmdSuccessed) listener.onAllSuccess(task, arg);
					}
					send();
				}
				
				/* 全部命令发送完成但有任何一条命令发送失败时的回调  */
				@Override
				public void onAnyFail(ITask task, Object[] arg) {
					isAllCmdSuccessed = false;
					JxshApp.showToast(context, CommDefines
							.getSkinManager()
							.string(R.string.mode_contorl_fail_any));
					if(listener != null) {
						listener.onFail(task, arg);
						if(cmdStack.isEmpty() && !isAllCmdSuccessed) listener.onAnyFail(task, arg);
					}
					send();
				}
			});
			cmdSender.send();
		}else{
			Logger.debug(null, "OfflineMulitGatewayCmdSender:"+JxshApp.instance.isClinkable);
			//释放按钮权限
			JxshApp.instance.isClinkable = false;
		}
	}

}
