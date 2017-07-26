package com.jinxin.datan.net.command;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.protocol.AddCustomerPattern;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.internet.NetRequest;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.Header;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.StringUtils;
import com.jinxin.record.SharedDB;

/**
 * 添加客户模式
 * 
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class AddCustomerPatternTask extends InternetTask {
	/**
	 * 请求任务
	 */
	private NetRequest request = null;
	private String paternName = null;
	private String customerId = null;
	private String status = null;
	private String icon = null;
	private String patternGroupId = null;
	private String memo = null;

	/**
	 * 
	 * @param context
	 */
	public AddCustomerPatternTask(Context context, String paternName,
			String customerId, String status,
			String icon, String patternGroupId, String memo) {
		this.paternName = paternName;
		this.customerId = customerId;
		this.status = status;
		this.icon = icon;
		this.patternGroupId = patternGroupId;
		this.memo = memo;
		this.init();
		super.setNetRequest(context, this.request, null);
	}

	private void init() {
		byte[] requestBytes = null;
		try {
			Header header = new Header(ControlDefine.BS_USER_PATTERN,
					ControlDefine.TRD_ADD_CUSTOMER_PATTERN,
					ControlDefine.ACTION_REQUEST,
					StringUtils.getSystemCurrentTime(), ControlDefine.TEST_TRUE);
			//
			requestBytes = getRequestByte(header, createServiceContent());
			//
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.request = new NetRequest(
				DatanAgentConnectResource.HTTP_ACCESSPATH,
				new AddCustomerPattern(this, requestBytes));
	}

	/**
	 * 创建serviceContent JSON数据体
	 * 
	 * @return
	 * @throws JSONException
	 */
	private JSONObject createServiceContent() throws JSONException {
		String _secretKey = SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS,
				ControlDefine.KEY_SECRETKEY, "");
		String _account = CommUtil.getCurrentLoginAccount();
		JSONObject serviceContent = new JSONObject();
		serviceContent.put("secretKey", _secretKey);
		serviceContent.put("account", _account);
		serviceContent.put("paternName", paternName);
		serviceContent.put("paternType", "2");
		serviceContent.put("customerId", customerId);
		serviceContent.put("createUser", customerId);
		serviceContent.put("status", status);
		serviceContent.put("icon", icon);
		serviceContent.put("patternGroupId", patternGroupId);
		serviceContent.put("memo", memo);
		// serviceContent.put("updateTime", _updateTime);
		return serviceContent;
	}
}
