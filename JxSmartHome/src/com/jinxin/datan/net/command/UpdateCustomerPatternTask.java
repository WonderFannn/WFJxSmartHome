package com.jinxin.datan.net.command;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.protocol.UpdateCustomerPattern;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.internet.NetRequest;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.Header;
import com.jinxin.jxsmarthome.entity.ProductPatternOperation;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.jxsmarthome.util.StringUtils;
import com.jinxin.record.SharedDB;


/**
 * 修改产品模式操作接口
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class UpdateCustomerPatternTask extends InternetTask {
	/**
	 * 请求任务
	 */
	private NetRequest request = null;
	
	private List<ProductPatternOperation> productPatternOperationList = null;
	private int patternId = -1;
	
	/**
	 * 
	 * @param context
	 */
	public UpdateCustomerPatternTask(Context context,int patternId, List<ProductPatternOperation> productPatternOperationList) {
		this.productPatternOperationList = productPatternOperationList;
		this.patternId  = patternId;
		this.init();
		super.setNetRequest(context, this.request, null);
	}

	private void init() {
		byte[] requestBytes = null;
		try {
			Header header = new Header(ControlDefine.BS_USER_PATTERN_MANAGER,
					ControlDefine.TRD_CHANGE_PASSWORD, ControlDefine.ACTION_REQUEST,
					StringUtils.getSystemCurrentTime(), ControlDefine.TEST_TRUE);
//			
			requestBytes = getRequestByte(header, createServiceContent());
//			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.request = new NetRequest(DatanAgentConnectResource.HTTP_ACCESSPATH, new UpdateCustomerPattern(this,
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
		serviceContent.put("secretKey", _secretKey);
		serviceContent.put("account", _account);
		serviceContent.put("patternId", patternId);
//		serviceContent.put("updateTime", _updateTime);
		JSONArray jsonArray = new JSONArray();
		if(this.productPatternOperationList != null){
			for(ProductPatternOperation productPatternOperation : this.productPatternOperationList){
				if(productPatternOperation != null){
					JSONObject _jo = new JSONObject();
					_jo.put("funId", productPatternOperation.getFunId());
					_jo.put("whId", productPatternOperation.getWhId());
					_jo.put("patternId", productPatternOperation.getPatternId());
					_jo.put("operation", productPatternOperation.getOperation());
					_jo.put("paraDesc", productPatternOperation.getParaDesc());
					jsonArray.put(_jo);
//					Logger.error(null, _jo.toString());
				}
			}
		}
		serviceContent.put("patternList", jsonArray);
		return serviceContent;
	}
}
