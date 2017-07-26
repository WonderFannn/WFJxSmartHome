package com.jinxin.datan.net.command;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.protocol.ChangeCustomerPattern;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.internet.NetRequest;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.Header;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.StringUtils;
import com.jinxin.record.SharedDB;


/**
 * 修改客户模式
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class ChangeCustomerPatternTask extends InternetTask {
	/**
	 * 请求任务
	 */
	private NetRequest request = null;
	private int patternId = -1;
	private String paternName = null;
	private String customerId = null;
	private String icon = null;
	private String status = null;
	private String patternGroupId = null;
	private String memo = null;
	/**
	 * 
	 * @param context
	 */
	public ChangeCustomerPatternTask(Context context, int patternId,String paternName,String customerId,
			String status,String icon, String patternGroupId, String memo) {
		this.patternId = patternId;
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
					ControlDefine.TRD_CHANGE_PASSWORD, ControlDefine.ACTION_REQUEST,
					StringUtils.getSystemCurrentTime(), ControlDefine.TEST_TRUE);
//			
			requestBytes = getRequestByte(header, createServiceContent());
//			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.request = new NetRequest(DatanAgentConnectResource.HTTP_ACCESSPATH, new ChangeCustomerPattern(this,
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
//		String _updateTime = SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS, ControlDefine.KEY_CUSTOMER_PATTERN_LIST,ControlDefine.DEFAULT_UPDATE_TIME);
		JSONObject serviceContent = new JSONObject();
		serviceContent.put("secretKey", _secretKey);
		serviceContent.put("account", _account);
		serviceContent.put("patternId", patternId);
		serviceContent.put("paternName", paternName);
		serviceContent.put("paternType", "2");
		serviceContent.put("customerId", customerId);
		serviceContent.put("createUser", customerId);
		serviceContent.put("status", status);
		serviceContent.put("icon", icon);
		serviceContent.put("memo", memo);
		serviceContent.put("patternGroupId", patternGroupId);
//		serviceContent.put("updateTime", _updateTime);
		return serviceContent;
	}
}
