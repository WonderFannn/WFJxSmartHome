package com.jinxin.datan.net.command;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.protocol.UpdateCustomerPatternCMD;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.internet.NetRequest;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.CustomerPatternUpdataCMDVO;
import com.jinxin.jxsmarthome.entity.Header;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.StringUtils;
import com.jinxin.record.SharedDB;


/**
 * 更新客户模式指令
 * 
 * @author JackeyZhang
 * @company 金鑫智慧
 * @deprecated
 */
public class UpdateCustomerPatternCMDTask extends InternetTask {
	/**
	 * 请求任务
	 */
	private NetRequest request = null;
	
	private List<CustomerPatternUpdataCMDVO> customerPatternUpdataCMDList = null;
	
	/**
	 * 
	 * @param context
	 */
	public UpdateCustomerPatternCMDTask(Context context, List<CustomerPatternUpdataCMDVO> addCustomerPatternCMDList) {
		this.init();
		this.customerPatternUpdataCMDList = addCustomerPatternCMDList;
		super.setNetRequest(context, this.request, null);
	}

	private void init() {
		byte[] requestBytes = null;
		try {
			Header header = new Header(ControlDefine.BS_USER_PATTERN_CMD,
					ControlDefine.TRD_ADD_CUSTOMER_PATTERN, ControlDefine.ACTION_REQUEST,
					StringUtils.getSystemCurrentTime(), ControlDefine.TEST_TRUE);
//			
			requestBytes = getRequestByte(header, createServiceContent());
//			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.request = new NetRequest(DatanAgentConnectResource.HTTP_ACCESSPATH, new UpdateCustomerPatternCMD(this,
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
//		serviceContent.put("updateTime", _updateTime);
		JSONArray jsonArray = new JSONArray();
		if(this.customerPatternUpdataCMDList != null){
			for(CustomerPatternUpdataCMDVO customerPatternAddCMDVO : this.customerPatternUpdataCMDList){
				if(customerPatternAddCMDVO != null){
					JSONObject _jo = new JSONObject();
					_jo.put("patternId", customerPatternAddCMDVO.getPatternId());
					_jo.put("cmdId", customerPatternAddCMDVO.getCmdId());
					_jo.put("exeOrder", customerPatternAddCMDVO.getExeOrder());
					jsonArray.put(_jo);
				}
			}
		}
		serviceContent.put("patternList", jsonArray);
		return serviceContent;
	}
}
