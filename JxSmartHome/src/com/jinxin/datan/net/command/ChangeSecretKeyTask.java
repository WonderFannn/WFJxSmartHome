package com.jinxin.datan.net.command;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.protocol.ChangeSecretRJ;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.internet.NetRequest;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.Header;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.StringUtils;
import com.jinxin.record.SharedDB;

/**修改密码请求任务
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class ChangeSecretKeyTask extends InternetTask {

	/**
	 * 请求任务
	 */
	private NetRequest request = null;
	private String _oldPwd = null;
	private String _newPwd = null;
	private String _answer1 = null;
	private String _answer2 = null;
	/**
	 * 
	 * @param context
	 */
	public ChangeSecretKeyTask(Context context,String _oldPwd,String _newPwd,
			String _answer1, String _answer2) {
		this._oldPwd = _oldPwd;
		this._newPwd = _newPwd;
		this._answer1 = _answer1;
		this._answer2 = _answer2;
		this.init();
		super.setNetRequest(context, this.request, null);
	}

	private void init() {
		byte[] requestBytes = null;
		try {
			Header header = new Header(ControlDefine.BS_ACCOUNT_MANAGER,
					ControlDefine.TRD_CHANGE_PASSWORD, ControlDefine.ACTION_REQUEST,
					StringUtils.getSystemCurrentTime(), ControlDefine.TEST_TRUE);
//			
			requestBytes = getRequestByte(header, createServiceContent());
//			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.request = new NetRequest(DatanAgentConnectResource.HTTP_ACCESSPATH, new ChangeSecretRJ(this,
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
		serviceContent.put("account", _account);
		serviceContent.put("secretKey", _secretKey);
		serviceContent.put("orgPassword", this._oldPwd);
		serviceContent.put("newPassword", this._newPwd);
		serviceContent.put("answer1", this._answer1);
		serviceContent.put("answer2", this._answer2);
		return serviceContent;
	}

}
