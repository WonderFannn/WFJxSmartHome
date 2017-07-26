package com.jinxin.datan.net.command;

import android.content.Context;

import com.jinxin.datan.net.protocol.CommonDeviceControlByServer;
import com.jinxin.datan.net.protocol.CommonDeviceControlByServer2;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.internet.NetRequest;
import com.jinxin.jxsmarthome.util.Logger;

/**
 * 通用设备控制（通过服务器）
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class CommonDeviceControlByServerTask2 extends InternetTask {
	/**
	 * 请求任务
	 */
	private NetRequest request = null;
	private String host;
	
	byte[] cmd = null;
	private int requestType = 0;

	/**
	 * 构造方法
	 * 		现请求类型主要有两种（操作和查询）， 默认为操作，但需要对请求类型做显示声明时调用此方法（如：请求为查询时requestType为1）,
	 * 		其他情况下建议调用
	 * 		CommonDeviceControlByServerTask(Context context,byte[] cmd,boolean isSocketLong)
	 * @param context			context对象
	 * @param cmd				命令
	 * @param isSocketLong		是否是长连接
	 * @param requestType		请求类型	0：操作    1：查询
	 */
	public CommonDeviceControlByServerTask2(Context context, String host, byte[] cmd, boolean isSocketLong, int requestType) {
		this.requestType = requestType;
		this.cmd = cmd;
		this.host = host;
		this.init();
		super.setNetRequest(context, this.request, null, 1); 
		super.setSocketOrHttp(isSocketLong ?(byte)6 : (byte)5);
	}
	

	private void init() {
		this.request = new NetRequest(host, new CommonDeviceControlByServer2(this,
				cmd, requestType));
	}

	/**
	 * 设置请求服务器地址
	 * @param host 服务器地址
	 */
	public void setRequestHost(String host) {
		Logger.debug(null, "-->" + host);
		this.request = new NetRequest(host, new CommonDeviceControlByServer(this,
				cmd, requestType));
		setNetRequest(_context, this.request, null, 1); 
		setSocketOrHttp((byte)6);
	}

}
