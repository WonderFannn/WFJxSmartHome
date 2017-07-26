package com.jinxin.jxsmarthome.cmd;

import java.util.List;

import com.jinxin.datan.local.util.NetworkModeSwitcher;

import android.content.Context;

public class AutoCmdSender {
	private Context context;
	private List<byte[]> cmdList;

	public AutoCmdSender(Context context, List<byte[]> cmdList) {
		this.context = context;
		this.cmdList = cmdList;
	}

	public void send() {
		// offline
		if (NetworkModeSwitcher.useOfflineMode(context)) {
			OfflineCmdSenderLong offlineSender = new OfflineCmdSenderLong(context, cmdList);
			offlineSender.send();

			// online
		} else {
			OnlineCmdSenderLong onlineCmdSender = new OnlineCmdSenderLong(context, cmdList);
			onlineCmdSender.send();
		}
	}

}
