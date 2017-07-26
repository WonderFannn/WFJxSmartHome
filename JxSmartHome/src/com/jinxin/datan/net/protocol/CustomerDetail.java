package com.jinxin.datan.net.protocol;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jinxin.datan.net.module.RemoteJsonResultInfo;
import com.jinxin.datan.toolkit.task.Task;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.Customer;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.record.SharedDB;

/**
 * 客户详情解析
 * 
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class CustomerDetail extends ResponseJson {
	private Task task = null;
	private byte[] requestBytes;

	public CustomerDetail(Task task, byte[] requestBytes) {
		this.task = task;
		this.requestBytes = requestBytes;
	}

	@Override
	public void response(InputStream in) throws Exception {
		if (in != null) {
			boolean isSuccess = false;
			RemoteJsonResultInfo _resultInfo = null;

			// List<LoginEntry> loginEntry = null;
			Customer customer = null;
			try {
				JSONObject jsonObject = this.getJsonObjectFromIn(in);

				_resultInfo = this.readResultInfo(jsonObject);
				if (_resultInfo.validResultCode.equals(ControlDefine.CONNECTION_SUCCESS)) {// 成功获取数据
					// 具体的解析工作在这里实现
					// 提示：任务中途检测任务是否被中止，同时可以直接返回任务取消数据（如有必要）
					if (this.task.ismTryCancel())
						return;
					// ////具体解析区////////////////////////
					// 取列表数据
					// JSONArray jsonArray =
					// jsonObject.getJSONArray("serviceContent");
					// loginEntry = new ArrayList<LoginEntry>();
					// for(int i = 0; i < jsonArray.length(); i++){
					// if (this.task.ismTryCancel()) return;
					// JSONObject _jo = (JSONObject)jsonArray.get(i);
					// JSONObject _jo =
					// jsonObject.getJSONObject("serviceContent");
					// loginEntry.add(new LoginEntry( getJsonString(_jo,
					// "secretKey") ));

					// }
					// 取更新时间
					String _processTime = this.getJsonString(jsonObject,
							"processTime");
					String _account = CommUtil.getCurrentLoginAccount();
					
					JSONObject _jo = jsonObject.getJSONObject("serviceContent");
					if(_jo.length() > 0)
					customer = new Customer(getJsonString(_jo, "customerId"),
							getJsonString(_jo, "customerName"), getJsonInt(_jo,
									"sex"), getJsonInt(_jo, "age"),
							getJsonString(_jo, "address"), getJsonString(_jo,
									"tel"), getJsonString(_jo, "mobile"),
							getJsonString(_jo, "provence"), getJsonString(_jo,
									"city"), getJsonString(_jo, "contry"),
							getJsonString(_jo, "agentId"), getJsonString(_jo,
									"createUser"), getJsonString(_jo,
									"createTime"), getJsonString(_jo,
									"updateUser"), getJsonString(_jo,
									"updateTime"), getJsonInt(_jo, "status"),
							getJsonInt(_jo, "accountType"), getJsonString(_jo,
									"question1"), getJsonString(_jo,
									"question2"),
							getJsonString(_jo, "comments"), getJsonString(_jo,
									"idCard"));
					// /////////////////////////////////////
					isSuccess = true;
					SharedDB.saveStrDB(_account,ControlDefine.
							KEY_CUSTOMER_DETAIL_LIST,_processTime);
				}
			} catch (JSONException e) {
				isSuccess = false;
			} catch (Exception e) {
				isSuccess = false;
			} finally {
				if (isSuccess) {
					this.task.callback(customer);
				} else {
					this.task.onError(_resultInfo.validResultInfo);
				}
				this.closeInputStream(in);// 必须关闭流
			}
		}
	}

	@Override
	public byte[] toOutputBytes() {
		return this.requestBytes;
	}

}
