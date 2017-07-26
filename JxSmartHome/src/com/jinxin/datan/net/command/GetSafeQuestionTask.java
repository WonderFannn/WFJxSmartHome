package com.jinxin.datan.net.command;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.protocol.GetSafeQuestionList;
import com.jinxin.datan.net.protocol.LoginRj;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.internet.NetRequest;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.Header;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.StringUtils;
import com.jinxin.record.SharedDB;

/**获取密保问题的请求
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class GetSafeQuestionTask extends InternetTask {

	/**
	 * 请求任务
	 */
	private NetRequest request = null;
	private String _account = null;
	/**
	 * 
	 * @param context
	 */
	public GetSafeQuestionTask(Context context,String account) {
		this._account = account;
		this.init();
		super.setNetRequest(context, this.request, null);
	}

	private void init() {
		byte[] requestBytes = null;
		try {
			Header header = new Header(ControlDefine.BS_ACCOUNT_MANAGER,
					ControlDefine.TRD_SECRET_SECURITY, ControlDefine.ACTION_REQUEST,
					StringUtils.getSystemCurrentTime(), ControlDefine.TEST_TRUE);
//			
			requestBytes = getRequestByte(header, createServiceContent());
//			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.request = new NetRequest(DatanAgentConnectResource.HTTP_ACCESSPATH, new GetSafeQuestionList(this,
				requestBytes));
	}

	/**
	 * 创建serviceContent JSON数据体
	 * 
	 * @return
	 * @throws JSONException
	 */
	private JSONObject createServiceContent() throws JSONException {
		if (TextUtils.isEmpty(_account)) {
//			_account = CommUtil.getCurrentLoginAccount();
			_account = CommUtil.getMainAccount();
		}
		JSONObject serviceContent = new JSONObject();
		serviceContent.put("account", _account);
		return serviceContent;
	}

}
