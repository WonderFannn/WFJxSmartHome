package com.jinxin.datan.net.command;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.protocol.DefaultResponseJosn;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.internet.NetRequest;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.Header;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.StringUtils;
import com.jinxin.record.SharedDB;

/**
 * 为语音合成设置输入源
 * @author 	TangLong
 * @company 金鑫智慧
 */
public class SaveInput4VoiceTask extends InternetTask {
	private NetRequest request = null;
	private String source;
	private String gatewayWhId;
	private String address485;
	
	public SaveInput4VoiceTask(Context context, String source, String gatewayWhId, String address485) {
		this.source = source;
		this.gatewayWhId = gatewayWhId;
		this.address485 = address485;
		this.init();
		super.setNetRequest(context, this.request, null);
	}
	
	private void init() {
		byte[] requestBytes = null;
		
		try {
			Header header = new Header(ControlDefine.BS_SAVE_INPUT_VOICE,
						ControlDefine.TRD_SAVE_INPUT_VOICE, ControlDefine.ACTION_REQUEST,
						StringUtils.getSystemCurrentTime(), ControlDefine.TEST_TRUE);
			requestBytes = getRequestByte(header, createServiceContent());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		this.request = new NetRequest(DatanAgentConnectResource.HTTP_ACCESSPATH, new DefaultResponseJosn(this, requestBytes));
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
		serviceContent.put("source", source);
		serviceContent.put("gatewayWhId", gatewayWhId);
		serviceContent.put("address485", address485);
		return serviceContent;
	}
} 
