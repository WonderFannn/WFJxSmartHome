package com.jinxin.datan.net.command;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.protocol.CodeLibraryRj;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.internet.NetRequest;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.Header;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.StringUtils;
import com.jinxin.record.SharedDB;


/**
 * 遥控设备指令发送请求
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class CodeLibraryTask extends InternetTask {
	private String mCode;
	private String whId;
	private String keyName;
	private int deviceId;
	private int brandsId;
	private String address485;
	private int type;
	private String keyStr;
	private int customerBrands;
	/**
	 * 请求任务
	 */
	private NetRequest request = null;
	/**
	 * 
	 * @param context
	 */
	public CodeLibraryTask(Context context,String mCode, String whId, String keyName,
			int deviceId, int brandsId, String address485, int type, String keyStr, int customerBrands) {
		this.mCode = mCode;
		this.whId = whId;
		this.keyName = keyName;
		this.deviceId = deviceId;
		this.brandsId = brandsId;
		this.address485 = address485;
		this.type = type;
		this.keyStr = keyStr;
		this.customerBrands = customerBrands;
		this.init();
		super.setNetRequest(context, this.request, null);
	}

	private void init() {
		byte[] requestBytes = null;
		try {
			Header header = new Header(ControlDefine.BS_DEVICE_TYPE,
					ControlDefine.TRD_USER_INFO, ControlDefine.ACTION_REQUEST,
					StringUtils.getSystemCurrentTime(), ControlDefine.TEST_TRUE);
//			
			requestBytes = getRequestByte(header, createServiceContent());
//			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.request = new NetRequest(DatanAgentConnectResource.HTTP_ACCESSPATH, new CodeLibraryRj(this,
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
//		String _updateTime = SharedDB.loadStrFromDB(_account, ControlDefine.KEY_CUSTOMER_TIMER_LIST,ControlDefine.DEFAULT_UPDATE_TIME);
		JSONObject serviceContent = new JSONObject();
		serviceContent.put("secretKey", _secretKey);
		serviceContent.put("account", _account);
		serviceContent.put("mCode", mCode);
		serviceContent.put("whId", whId);
		serviceContent.put("keyName", keyName);
		serviceContent.put("deviceId", deviceId);
		serviceContent.put("brandsId", brandsId);
		serviceContent.put("address485", address485);
		serviceContent.put("type", type);
		serviceContent.put("keyStr", keyStr);
		serviceContent.put("customerBrands", customerBrands);
//		serviceContent.put("updateTime", _updateTime);
		return serviceContent;
	}
}
