package com.jinxin.datan.net.command;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.protocol.ForgetPasswordRJ;
import com.jinxin.datan.net.protocol.GetSafeQuestionList;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.internet.NetRequest;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.Header;
import com.jinxin.jxsmarthome.util.StringUtils;

/**忘记密码、修改的请求
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class ForgetPasswordTask extends InternetTask {

	/**
	 * 请求任务
	 */
	private NetRequest request = null;
	private String _account = null;
	private String _password = null;
	private String _answer1 = null;
	private String _answer2 = null;
	/**
	 * 
	 * @param context
	 */
	public ForgetPasswordTask(Context context,String account,
			String password,String answer1,String answer2) {
		this._account = account;
		this._password = password;
		this._answer1 = answer1;
		this._answer2 = answer2;
		this.init();
		super.setNetRequest(context, this.request, null);
	}

	private void init() {
		byte[] requestBytes = null;
		try {
			Header header = new Header(ControlDefine.BS_ACCOUNT_MANAGER,
					ControlDefine.TRD_UPDATE_MUSIC_LIST, ControlDefine.ACTION_REQUEST,
					StringUtils.getSystemCurrentTime(), ControlDefine.TEST_TRUE);
//			
			requestBytes = getRequestByte(header, createServiceContent());
//			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.request = new NetRequest(DatanAgentConnectResource.HTTP_ACCESSPATH, new ForgetPasswordRJ(this,
				requestBytes));
	}

	/**
	 * 创建serviceContent JSON数据体
	 * 
	 * @return
	 * @throws JSONException
	 */
	private JSONObject createServiceContent() throws JSONException {
		JSONObject serviceContent = new JSONObject();
		serviceContent.put("account", _account);
		serviceContent.put("newPassword", _password);
		serviceContent.put("answer1", _answer1);
		serviceContent.put("answer2", _answer2);
		return serviceContent;
	}

}
