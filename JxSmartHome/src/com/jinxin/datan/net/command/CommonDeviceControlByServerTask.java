package com.jinxin.datan.net.command;

import android.content.Context;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.protocol.CommonDeviceControlByServer;
import com.jinxin.datan.net.protocol.CommonWirelessDeviceControlByServer;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.internet.NetRequest;
import com.jinxin.jxsmarthome.cmd.entity.CmdParserStrategy;
import com.jinxin.jxsmarthome.util.Logger;

/**
 * 通用设备控制（通过服务器）
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class CommonDeviceControlByServerTask extends InternetTask {
	/**
	 * 请求任务
	 */
	private NetRequest request = null;
	
	byte[] cmd = null;
	private int requestType = 0;

	/**
	 * @param context
	 */
	public CommonDeviceControlByServerTask(Context context,byte[] cmd,boolean isSocketLong) {
		this(context, cmd, isSocketLong, 0);
	} 
	
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
	public CommonDeviceControlByServerTask(Context context, byte[] cmd, boolean isSocketLong, int requestType) {
		this.requestType = requestType;
		this.cmd = cmd;
		this.init();
		super.setNetRequest(context, this.request, null,1); 
		super.setSocketOrHttp(isSocketLong ?(byte)6 : (byte)5);
	}
	

	private void init() {
		this.request = new NetRequest(DatanAgentConnectResource.HOST, new CommonDeviceControlByServer(this,
				cmd, requestType));
	}

	/**
	 * 设置请求服务器地址
	 * 
	 * @param host 服务器地址
	 * @param type 请求类型：0-普通，1-zigbee偶数行解析，2-zg巡检, 3-ZG锁解析
	 * @param isLast 是否是最后一条,当设置为true时，只会对最后一条做解析
	 */
	public void setRequestHost(String host, int type) {
		Logger.debug(null, "-->" + host);
		if (type == 0) {
			Logger.debug(null, "parse type 0");
			this.request = new NetRequest(host, new CommonDeviceControlByServer(this, cmd, requestType));
		} else if (type == 1) {
			Logger.debug(null, "parse type 1");
			this.request = new NetRequest(host, new CommonWirelessDeviceControlByServer(this, cmd,
					CmdParserStrategy.ZIGBEE, 0));
		} else if (type == 2) {
			Logger.debug(null, "parse type 2");
			this.request = new NetRequest(host, new CommonWirelessDeviceControlByServer(this, cmd,
					CmdParserStrategy.ZIGBEE_DEVICE_CHECK, 0));
		} else if(type == 3){
			Logger.debug(null, "parse type 3");
			this.request = new NetRequest(host, new CommonWirelessDeviceControlByServer(this, cmd,
					CmdParserStrategy.ZIGBEE, 1));
		}
		setNetRequest(_context, this.request, null,1);
		setSocketOrHttp((byte)6);
	}

}
