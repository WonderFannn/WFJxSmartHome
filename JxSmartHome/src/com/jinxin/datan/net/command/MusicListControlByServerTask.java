package com.jinxin.datan.net.command;

import android.content.Context;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.protocol.MusicListControlByServer;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.internet.NetRequest;


/**
 * 通用设备控制（通过服务器）
 * @author TangLong
 * @company 金鑫智慧
 */
public class MusicListControlByServerTask extends InternetTask {
	private NetRequest request = null;
	
	byte[] cmd = null;
	
	public MusicListControlByServerTask(Context context,byte[] cmd,boolean isSocketLong) {
		this.cmd = cmd;
		this.init();
		super.setNetRequest(context, this.request, null); 
		super.setSocketOrHttp(isSocketLong ?(byte)6 : (byte)5);
	} 
	
	private void init() {
		this.request = new NetRequest(DatanAgentConnectResource.HOST, new MusicListControlByServer(this,
				cmd));
	}
}
