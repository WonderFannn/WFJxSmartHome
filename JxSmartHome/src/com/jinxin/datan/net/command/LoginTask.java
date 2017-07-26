package com.jinxin.datan.net.command;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.protocol.LoginRj;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.internet.NetRequest;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.Header;
import com.jinxin.jxsmarthome.util.StringUtils;


/**
 * 登录
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class LoginTask extends InternetTask {
	/**
	 * 请求任务
	 */
	private NetRequest request = null;
	private String m_account = null;
	private String m_password = null;
	/**
	 * 
	 * @param context
	 */
	public LoginTask(Context context, String _account, String _password) {
		this.m_account = _account;
		this.m_password = _password;
		this.init();
		super.setNetRequest(context, this.request, null);
	}

	private void init() {
		byte[] requestBytes = null;
		try {
			Header header = new Header(ControlDefine.BS_ACCOUNT_MANAGER,
					ControlDefine.TRD_ACCOUNT_LOGIN, ControlDefine.ACTION_REQUEST,
					StringUtils.getSystemCurrentTime(), ControlDefine.TEST_TRUE);
//			
			requestBytes = getRequestByte(header, createServiceContent());
//			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.request = new NetRequest(DatanAgentConnectResource.HTTP_ACCESSPATH, new LoginRj(this,
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
		serviceContent.put("account", this.m_account);
		serviceContent.put("password", this.m_password);
		return serviceContent;
	}
}
