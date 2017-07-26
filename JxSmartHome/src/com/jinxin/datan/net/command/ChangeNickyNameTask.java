package com.jinxin.datan.net.command;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.protocol.ChangeNickyNameRJ;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.internet.NetRequest;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.Header;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.StringUtils;
import com.jinxin.record.SharedDB;

/**修改昵称请求任务
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class ChangeNickyNameTask extends InternetTask {

	/**
	 * 请求任务
	 */
	private NetRequest request = null;
	private String nickyName = null;
	/**
	 * 
	 * @param context
	 */
	public ChangeNickyNameTask(Context context,String nickyName) {
		this.nickyName = nickyName;
		this.init();
		super.setNetRequest(context, this.request, null);
	}

	private void init() {
		byte[] requestBytes = null;
		try {
			Header header = new Header(ControlDefine.BS_ACCOUNT_MANAGER,
					ControlDefine.TRD_USER_NICKY_NAME, ControlDefine.ACTION_REQUEST,
					StringUtils.getSystemCurrentTime(), ControlDefine.TEST_TRUE);
//			
			requestBytes = getRequestByte(header, createServiceContent());
//			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.request = new NetRequest(DatanAgentConnectResource.HTTP_ACCESSPATH, new ChangeNickyNameRJ(this,
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
		serviceContent.put("nickyName", this.nickyName);
		return serviceContent;
	}

}
