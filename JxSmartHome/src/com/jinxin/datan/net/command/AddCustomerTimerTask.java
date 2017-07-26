package com.jinxin.datan.net.command;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.protocol.AddCustomerTimer;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.internet.NetRequest;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.Header;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.StringUtils;
import com.jinxin.record.SharedDB;


/**
 * 添加定时任务请求
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class AddCustomerTimerTask extends InternetTask {
	/**
	 * 请求任务
	 */
	private NetRequest request = null;
	private String cornExpression = null;
	private int status = -1;
	private String taskName = null;
	private String period = "";
	/**
	 * 
	 * @param context
	 */
	public AddCustomerTimerTask(Context context, String taskName,String period,String cornExpression,int status) {
		this.taskName = taskName;
		this.period = period;
		this.cornExpression = cornExpression;
		this.status = status;
		this.init();
		super.setNetRequest(context, this.request, null);
	}

	private void init() {
		byte[] requestBytes = null;
		try {
			Header header = new Header(ControlDefine.BS_CUSTOMER_TIMER,
					ControlDefine.TRD_ADD_CUSTOMER_PATTERN, ControlDefine.ACTION_REQUEST,
					StringUtils.getSystemCurrentTime(), ControlDefine.TEST_TRUE);
//			
			requestBytes = getRequestByte(header, createServiceContent());
//			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.request = new NetRequest(DatanAgentConnectResource.HTTP_ACCESSPATH, new AddCustomerTimer(this,
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
		serviceContent.put("taskName", taskName);
		serviceContent.put("period", period);
		serviceContent.put("cornExpression", cornExpression);
		serviceContent.put("status", status);
//		serviceContent.put("updateTime", _updateTime);
//		Logger.error("Add", serviceContent.toString());
		return serviceContent;
	}
}
