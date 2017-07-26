package com.jinxin.datan.net.command;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.protocol.ProductRegisterParser;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.internet.NetRequest;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.Header;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.StringUtils;
import com.jinxin.record.SharedDB;

/**
 * 网关注册信息更新
 * 
 * @author TangLong
 * @company 金鑫智慧
 */
public class UpdateProductRegisterTask extends InternetTask {
	private NetRequest request = null;

	public UpdateProductRegisterTask(Context context) {
		this.init();
		super.setNetRequest(context, this.request, null);
	}

	private void init() {
		byte[] requestBytes = null;

		try {
			Header header = new Header(
					ControlDefine.BS_UPDATE_PRODUCT_REGISTER,
					ControlDefine.TRD_UPDATE_PRODUCT_REGISTER,
					ControlDefine.ACTION_REQUEST,
					StringUtils.getSystemCurrentTime(), ControlDefine.TEST_TRUE);
			requestBytes = getRequestByte(header, createServiceContent());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		this.request = new NetRequest(
				DatanAgentConnectResource.HTTP_ACCESSPATH,
				new ProductRegisterParser(this, requestBytes));
	}

	/**
	 * 创建serviceContent JSON数据体
	 * 
	 * @return
	 * @throws JSONException
	 */
	private JSONObject createServiceContent() throws JSONException {
		String _secretKey = SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS,
				ControlDefine.KEY_SECRETKEY, "");
		String _account = CommUtil.getCurrentLoginAccount();
		String _updateTime = SharedDB.loadStrFromDB(_account,
				ControlDefine.KEY_PRODUCT_REGISTER,
				ControlDefine.DEFAULT_UPDATE_TIME);

		JSONObject serviceContent = new JSONObject();
		serviceContent.put("secretKey", _secretKey);
		serviceContent.put("account", _account);
		serviceContent.put("registerTime", _updateTime);
		return serviceContent;
	}
}
