package com.jinxin.datan.net.command;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import com.jinxin.jxsmarthome.util.Logger;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.protocol.UpdateCustomerTimerOperate;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.internet.NetRequest;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.Header;
import com.jinxin.jxsmarthome.entity.ProductPatternOperation;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.StringUtils;
import com.jinxin.record.SharedDB;


/**
 * 修改定时任务详细操作请求接口
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class UpdateCustomerTimerOperateTask extends InternetTask {
	/**
	 * 请求任务
	 */
	private NetRequest request = null;
	
	private List<ProductPatternOperation> timerTaskOperationList = null;
	private int taskId = -1;
	private int patternId = -1;
	private int operationType = -1;
	private Map<Integer, Integer> map = null;
	
	/**
	 * 
	 * @param context
	 */
	public UpdateCustomerTimerOperateTask(Context context,int taskId, Map<Integer, Integer> map,
			List<ProductPatternOperation> list) {
		this.timerTaskOperationList = list;
		this.taskId  = taskId;
		this.map = map;
		this.init();
		super.setNetRequest(context, this.request, null);
	}

	private void init() {
		byte[] requestBytes = null;
		try {
			Header header = new Header(ControlDefine.BS_CUSTOMER_TIMER_OPERATION,
					ControlDefine.TRD_USER_DEVICE, ControlDefine.ACTION_REQUEST,
					StringUtils.getSystemCurrentTime(), ControlDefine.TEST_TRUE);
//			
			requestBytes = getRequestByte(header, createServiceContent());
//			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.request = new NetRequest(DatanAgentConnectResource.HTTP_ACCESSPATH, new UpdateCustomerTimerOperate(this,
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
		serviceContent.put("taskId", taskId);
//		serviceContent.put("updateTime", _updateTime);
		JSONArray jsonArray = new JSONArray();
		if(this.timerTaskOperationList != null){
			operationType = 2;
			for(ProductPatternOperation timerTaskOperation : this.timerTaskOperationList){
				if(timerTaskOperation != null){
					JSONObject _jo = new JSONObject();
					_jo.put("taskId", taskId);
					_jo.put("funId", timerTaskOperation.getFunId());
					_jo.put("whId", timerTaskOperation.getWhId());
					_jo.put("patternId", "");
					_jo.put("operationType", operationType);
					_jo.put("operation", timerTaskOperation.getOperation());
					_jo.put("paraDesc", timerTaskOperation.getParaDesc());
					jsonArray.put(_jo);
					
//					Logger.debug("Yang", _jo.toString());
				}
			}
			if (map != null) {
				operationType = 1;
				for (Integer key : map.keySet()) {
					JSONObject _jo = new JSONObject();
					patternId = map.get(key);
					_jo.put("taskId", taskId);
					_jo.put("funId", "");
					_jo.put("whId", "");
					_jo.put("patternId", patternId);
					_jo.put("operationType", operationType);
					_jo.put("operation", "");
					_jo.put("paraDesc", "");
					jsonArray.put(_jo);
//					Logger.debug("Yang", _jo.toString());
				}
			}
		}
		serviceContent.put("patternList", jsonArray);
		return serviceContent;
	}
}
