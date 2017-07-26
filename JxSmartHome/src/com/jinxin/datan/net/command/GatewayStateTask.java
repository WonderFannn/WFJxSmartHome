package com.jinxin.datan.net.command;

import android.content.Context;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.protocol.GatewayStateRJ;
import com.jinxin.datan.net.protocol.SearchMusicRJ;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.internet.NetRequest;

/**搜索百度音乐请求任务
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class GatewayStateTask extends InternetTask {
	
	private String whId ="";
	
	/**
	 * 请求任务
	 */
	private NetRequest request = null;
	
	public GatewayStateTask(Context context,String whId) {
		this.whId = whId;
		init();
		setHttpType((byte)1);//设置get请求
		setNetRequest(context, this.request, null);
	}
	
	private void init() {
		byte[] requestBytes = null;
		String url = null;
		try {
			StringBuffer _buff = new StringBuffer();
			_buff.append(DatanAgentConnectResource.GATEWAY_PATH).append(
					whId);
			
			url = _buff.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.request = new NetRequest(url,new GatewayStateRJ(this, requestBytes));

	}
	
}
