package com.jinxin.datan.net.command;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.protocol.AddCustomerRemoteBrandRj;
import com.jinxin.datan.net.protocol.AddInfraredRemoteControllerRj;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.internet.NetRequest;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.Header;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.StringUtils;
import com.jinxin.record.SharedDB;


/**
 * 添加自定义遥控模板请求
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class AddInfraredRemoteControllerTask extends InternetTask {
	private String mCode;
	private String whId;
	private String nickName;
	private int deviceId;
	private int brandsId;
	private int type;
	/**
	 * 请求任务
	 */
	private NetRequest request = null;
	/**
	 * 
	 * @param context
	 */
	public AddInfraredRemoteControllerTask(Context context, String mCode, String whId, int deviceId, String nickName, int brandsId, int type) {
		this.mCode = mCode;
		this.whId = whId;
		this.nickName = nickName;
		this.deviceId = deviceId;
		this.brandsId = brandsId;
		this.type = type;
		this.init();
		super.setNetRequest(context, this.request, null);
	}

	private void init() {
		byte[] requestBytes = null;
		try {
			Header header = new Header(ControlDefine.BS_INFRARED_REMOTE_DEVICE_BIPCODE,
					ControlDefine.TRD_ADD_CUSTOMER_PATTERN, ControlDefine.ACTION_REQUEST,
					StringUtils.getSystemCurrentTime(), ControlDefine.TEST_TRUE);
//			
			requestBytes = getRequestByte(header, createServiceContent());
//			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.request = new NetRequest(DatanAgentConnectResource.HTTP_ACCESSPATH, new AddInfraredRemoteControllerRj(this,
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
		serviceContent.put("mCode", mCode);
		serviceContent.put("whId", whId);
		serviceContent.put("nickName", nickName);
		serviceContent.put("deviceId", deviceId);
		serviceContent.put("brandsId", brandsId);
		serviceContent.put("type", type);
		return serviceContent;
	}
}
