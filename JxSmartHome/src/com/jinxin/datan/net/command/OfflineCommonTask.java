package com.jinxin.datan.net.command;

import java.util.List;

import android.content.Context;

import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.datan.net.protocol.OfflineCommonParser;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.internet.NetRequest;
import com.jinxin.jxsmarthome.entity.OffLineContent;
import com.jinxin.jxsmarthome.util.Logger;

/**
 * 离线网络任务的处理(通用)
 * @author  TangLong
 * @company 金鑫智慧
 */
public class OfflineCommonTask extends InternetTask {
	private NetRequest request = null;		
	byte[] cmd = null;

	public OfflineCommonTask(Context context, byte[] cmd,boolean isSocketLong) {
		this.cmd = cmd;
		this.init(context);
		super.setNetRequest(context, this.request, null); 
		super.setSocketOrHttp(isSocketLong ?(byte)6 : (byte)5);
	} 

	private void init(Context context) {
		String getwayIp = "";
		List<OffLineContent> gatwayIps = NetworkModeSwitcher.getLocalGatwayIP(context);
		if(gatwayIps != null && gatwayIps.size() > 0) {
			getwayIp = gatwayIps.get(0).getIp()+":3333";
		}
		Logger.debug(null, "getwayIp:" + getwayIp);
		
		if (!getwayIp.equals("")) {
			this.request = new NetRequest(getwayIp, new OfflineCommonParser(this,
					cmd));
		}
	}

	/**
	 * 设置请求服务器地址
	 * @param host 服务器地址
	 */
	public void setRequestHost(String host) {
		Logger.debug(null, "-->" + host);
		this.request = new NetRequest(host, new OfflineCommonParser(this, cmd));
		setNetRequest(_context, this.request, null); 
		setSocketOrHttp((byte)6);
	}

}
