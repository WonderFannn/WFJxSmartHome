package com.jinxin.datan.net.command;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.protocol.AddSuggestFeedback;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.internet.NetRequest;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.Header;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.StringUtils;
import com.jinxin.record.SharedDB;


/**
 * 新增意见反馈信息
 * @author BinhuaHuang
 * @company 金鑫智慧
 */
public class AddSuggestFeedbackTask extends InternetTask {
	/**
	 * 请求任务
	 */
	private NetRequest request = null;
	private String _suggest = null;
	private String _contact = null;
	/**
	 * 
	 * @param context
	 */
	public AddSuggestFeedbackTask(Context context, String suggest,String contact) {
		this._suggest = suggest;
		this._contact = contact;
		this.init();
		super.setNetRequest(context, this.request, null);
	}

	private void init() {
		byte[] requestBytes = null;
		try {
			Header header = new Header(ControlDefine.BS_ADD_SUGGEST_FEEDBACK,
					ControlDefine.TRD_ADD_CUSTOMER_PATTERN, ControlDefine.ACTION_REQUEST,
					StringUtils.getSystemCurrentTime(), ControlDefine.TEST_TRUE);
//			
			requestBytes = getRequestByte(header, createServiceContent());
//			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.request = new NetRequest(DatanAgentConnectResource.HTTP_ACCESSPATH, new AddSuggestFeedback(this,
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
		serviceContent.put("account", _account);
		serviceContent.put("secretKey", _secretKey);
		serviceContent.put("content", _suggest);
		serviceContent.put("contactInfo", _contact);
//		serviceContent.put("updateTime", _updateTime);
//		Logger.error("Add", serviceContent.toString());
		return serviceContent;
	}
}
