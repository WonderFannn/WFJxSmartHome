package com.jinxin.datan.net.command;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.protocol.ChangeSafeQuestionRJ;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.internet.NetRequest;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.Header;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.StringUtils;
import com.jinxin.record.SharedDB;

/**设置密保问题和答案请求任务
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class ChangeSafeQuestionTask extends InternetTask {

	/**
	 * 请求任务
	 */
	private NetRequest request = null;
	private String _question1 = null;
	private String _question2 = null;
	private String _answer1 = null;
	private String _answer2 = null;
	/**
	 * 
	 * @param context
	 */
	public ChangeSafeQuestionTask(Context context,String question1,String question2,
			String answer1, String answer2) {
		this._question1 = question1;
		this._question2 = question2;
		this._answer1 = answer1;
		this._answer2 = answer2;
		this.init();
		super.setNetRequest(context, this.request, null);
	}

	private void init() {
		byte[] requestBytes = null;
		try {
			Header header = new Header(ControlDefine.BS_ACCOUNT_MANAGER,
					ControlDefine.TRD_USER_SAFE_QUESITON, ControlDefine.ACTION_REQUEST,
					StringUtils.getSystemCurrentTime(), ControlDefine.TEST_TRUE);
//			
			requestBytes = getRequestByte(header, createServiceContent());
//			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.request = new NetRequest(DatanAgentConnectResource.HTTP_ACCESSPATH, new ChangeSafeQuestionRJ(this,
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
		serviceContent.put("question1", this._question1);
		serviceContent.put("question2", this._question2);
		serviceContent.put("answer1", this._answer1);
		serviceContent.put("answer2", this._answer2);
		return serviceContent;
	}

}
