package com.jinxin.datan.net.command;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.protocol.AddCustomerPattern;
import com.jinxin.datan.net.protocol.AddProductConnectionListRj;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.internet.NetRequest;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.Header;
import com.jinxin.jxsmarthome.entity.ProductConnection;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.jxsmarthome.util.StringUtils;
import com.jinxin.record.SharedDB;

/**
 * 添加设备联动操作任务
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class AddConnectionOperationTask extends InternetTask {
	/**
	 * 请求任务
	 */
	private NetRequest request = null;
	private List<ProductConnection> productConntectionlist = null;

	/**
	 * 
	 * @param context
	 */
	public AddConnectionOperationTask(Context context, List<ProductConnection> productConntectionlist) {
		this.productConntectionlist = productConntectionlist;
		this.init();
		super.setNetRequest(context, this.request, null);
	}

	private void init() {
		byte[] requestBytes = null;
		try {
			Header header = new Header(ControlDefine.BS_ADD_CONNECTION_OPERATION,
					ControlDefine.TRD_ADD_CUSTOMER_PATTERN,ControlDefine.ACTION_REQUEST,
					StringUtils.getSystemCurrentTime(), ControlDefine.TEST_TRUE);
			requestBytes = getRequestByte(header, createServiceContent());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.request = new NetRequest(
				DatanAgentConnectResource.HTTP_ACCESSPATH,
				new AddProductConnectionListRj(this, requestBytes));
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
		JSONObject serviceContent = new JSONObject();
		serviceContent.put("account", _account);
		serviceContent.put("secretKey", _secretKey);
		JSONArray jsonArray = new JSONArray();
		if (this.productConntectionlist != null) {
			for (ProductConnection productConntection : productConntectionlist) {
				if(productConntection != null){
					JSONObject _jo = new JSONObject();
					_jo.put("funId", productConntection.getFunId());
					_jo.put("whId", productConntection.getWhId());
					_jo.put("status", productConntection.getStatus());
					_jo.put("bindType", productConntection.getBindType());
					_jo.put("bindFunId", productConntection.getBindFunId());
					_jo.put("operation", productConntection.getOperation());
					_jo.put("paraDesc", productConntection.getParaDesc());
					_jo.put("bindStatus", productConntection.getBindStatus());
					_jo.put("bindWhId", productConntection.getBindWhId());
					_jo.put("timeInterval", productConntection.getTimeInterval());
					_jo.put("isvalid", productConntection.getIsvalid());
//					_jo.put("patternId", "0");
					jsonArray.put(_jo);
					
					Logger.error(null, _jo.toString());
				}
			}
		}
		serviceContent.put("connectionList", jsonArray);
//		Logger.error(null, "-->"+serviceContent.toString());
		return serviceContent;
	}
}
