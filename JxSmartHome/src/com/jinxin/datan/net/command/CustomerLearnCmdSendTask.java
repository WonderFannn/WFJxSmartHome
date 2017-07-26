package com.jinxin.datan.net.command;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.protocol.CustomerLearnCmdSendRj;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.internet.NetRequest;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.Header;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.StringUtils;
import com.jinxin.record.SharedDB;


/**
 * 学习方式的遥控指令发送请求
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class CustomerLearnCmdSendTask extends InternetTask {
	/**
	 * 请求任务
	 */
	private NetRequest request = null;
	private String whId = "";
	private String address485 = "";
	private int funId;
	/**
	 * 
	 * @param context
	 */
	public CustomerLearnCmdSendTask(Context context, String whId, String address485, int funId) {
		this.whId = whId;
		this.address485 = address485;
		this.funId = funId;
		this.init();
		super.setNetRequest(context, this.request, null);
	}

	private void init() {
		byte[] requestBytes = null;
		try {
			Header header = new Header(ControlDefine.BS_DEVICE_TYPE,
					ControlDefine.TRD_USER_DEVICE_COMMAND, ControlDefine.ACTION_REQUEST,
					StringUtils.getSystemCurrentTime(), ControlDefine.TEST_TRUE);
//			
			requestBytes = getRequestByte(header, createServiceContent());
//			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.request = new NetRequest(DatanAgentConnectResource.HTTP_ACCESSPATH, new CustomerLearnCmdSendRj(this,
				requestBytes));
	}

	/**
	 * 创建serviceContent JSON数据体
	 * 
	 * @return
	 * @throws JSONException
	 */
	private JSONObject createServiceContent() throws JSONException {
		String _secretKey = SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS, ControlDefine.KEY_SECRETKEY,"");
		String _account = CommUtil.getCurrentLoginAccount();
		JSONObject serviceContent = new JSONObject();
		serviceContent.put("secretKey", _secretKey);
		serviceContent.put("account", _account);
		serviceContent.put("whId", whId);
		serviceContent.put("address485", address485);
		serviceContent.put("funId", funId);
		return serviceContent;
	}
}
