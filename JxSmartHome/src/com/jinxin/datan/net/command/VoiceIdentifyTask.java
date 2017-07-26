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
public class VoiceIdentifyTask extends InternetTask {
	private NetRequest request = null;
	private String type;
	private String whId;
	
	public VoiceIdentifyTask(Context context, String type, String whId) {
		this.type = type;
		this.whId = whId;
		this.init();
		super.setNetRequest(context, this.request, null);
	}
	
	private void init() {
		byte[] requestBytes = null;
		
		try {
			Header header = new Header(ControlDefine.BS_SET_VOICE_IDENTIFY,
						ControlDefine.TRD_SET_VOICE_IDENTIFY, ControlDefine.ACTION_REQUEST,
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
		serviceContent.put("type", type);
		serviceContent.put("gatewayWhId", whId);
		return serviceContent;
	}
} 
