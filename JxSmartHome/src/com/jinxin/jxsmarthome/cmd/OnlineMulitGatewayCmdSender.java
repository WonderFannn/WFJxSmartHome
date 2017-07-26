package com.jinxin.jxsmarthome.cmd;

import java.util.List;

import android.content.Context;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.TaskListener;
import com.jinxin.jxsmarthome.cmd.entity.Command;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.util.DateUtil;
import com.jinxin.jxsmarthome.util.Logger;

/**
 * 多网关在线发送命令
 * 
 * @author TangLong
 * @company 金鑫智慧
 */
public class OnlineMulitGatewayCmdSender extends CmdSender<Command> {

	private boolean isAllCmdSuccessed = true;

	public OnlineMulitGatewayCmdSender(Context context, List<Command> cmdList) {
		super(context, cmdList);
	}

	@Override
	public void send() {
		Logger.warn(null, "command size:" + cmdStack.size());
		if (cmdStack == null)
			return;
		if (!cmdStack.isEmpty()) {
			Command command = cmdStack.pop();
			if (command == null)
				return;

			// debug
			Logger.warn(null, command.toString());
			List<byte[]> cmds = command.getCmdList();
			for (byte[] b : cmds) {
				Logger.debug(null, "OnlineMulitGatewayCmdSender--> "+new String(b));
			}

			// send cmd
			OnlineCmdSenderLong cmdSender = new OnlineCmdSenderLong(context, command.getGatewayRemoteIp(), command.isZigbee(), command.getCmdList(), true, 0,
					true);
			cmdSender.addListener(new TaskListener<ITask>() {
				
				@Override
				public void onStarted(ITask task, Object arg) {
					Logger.debug(null, "OnlineMulitGatewayCmdSender onStarted"+"----"+DateUtil.convertLongToStr1(System.currentTimeMillis()));
					if(listener != null)
						listener.onStarted(task, arg);
				}

				@Override
				public void onFinish() {
					Logger.debug(null, "OnlineMulitGatewayCmdSender onFinish"+"----"+DateUtil.convertLongToStr1(System.currentTimeMillis()));
					// send();
				}

				@Override
				public void onAllSuccess(ITask task, Object[] arg) {
					Logger.debug(null, "OnlineMulitGatewayCmdSender onAllSuccess"+"----"+DateUtil.convertLongToStr1(System.currentTimeMillis()));
					super.onAllSuccess(task, arg);
					if (listener != null) {
						listener.onSuccess(task, arg);
						if (cmdStack.isEmpty() && isAllCmdSuccessed)
							listener.onAllSuccess(task, arg);
					}
					send();
				}

				/* 全部命令发送完成但有任何一条命令发送失败时的回调 */
				@Override
				public void onAnyFail(ITask task, Object[] arg) {
					Logger.debug(null, "OnlineMulitGatewayCmdSender onAnyFail"+"----"+DateUtil.convertLongToStr1(System.currentTimeMillis()));
					super.onAnyFail(task, arg);
					isAllCmdSuccessed = false;
					if (listener != null) {
						listener.onFail(task, arg);
						if (task instanceof InternetTask
								&& ((InternetTask) task).getRequest().getResponseJson() != null
								&& ((InternetTask) task).getRequest().getResponseJson().getResultInfo() != null
								&& ((InternetTask) task).getRequest().getResponseJson().getResultInfo().validResultCode.trim().equalsIgnoreCase(
										DatanAgentConnectResource.SERVER_ERROR_MSG_01)) {//处理网关离线问题，不提示部分设备控制
						} else {
							if (cmdStack.isEmpty() && !isAllCmdSuccessed)
								listener.onAnyFail(task, arg);
						}
					}
					send();
				}
			});
			cmdSender.send();
		} else {
			// 释放按钮权限
			JxshApp.instance.isClinkable = false;
		}
	}

}
