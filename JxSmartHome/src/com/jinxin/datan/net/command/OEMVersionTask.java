package com.jinxin.datan.net.command;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.protocol.OEMVersionRJ;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.internet.NetRequest;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.Header;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.StringUtils;
import com.jinxin.record.SharedDB;


/**
 * 根据客户所属的代理商不同动态变化版权信息
 * @author huang
 * @company 金鑫智慧
 */
public class OEMVersionTask extends InternetTask {
	/**
	 * 请求任务
	 */
	private NetRequest request = null;
	/**
	 * @param context
	 */
	public OEMVersionTask(Context context) {
		this.init();
		super.setNetRequest(context, this.request, null);
	}
	
	private void init() {
		byte[] requestBytes = null;
		try {
			Header header = new Header(ControlDefine.BS_OEM_VERSION,
					ControlDefine.TRD_PRODUCT_REMOTE_CONFIG_FUN_OPERATION, ControlDefine.ACTION_REQUEST,
					StringUtils.getSystemCurrentTime(), ControlDefine.TEST_TRUE);
			requestBytes = getRequestByte(header, createServiceContent());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.request = new NetRequest(DatanAgentConnectResource.HTTP_ACCESSPATH, new OEMVersionRJ(this,
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
		serviceContent.put("secretKey", _secretKey);
		serviceContent.put("account", _account);
		return serviceContent;
	}
}
