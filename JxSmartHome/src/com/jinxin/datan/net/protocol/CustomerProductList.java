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
import com.jinxin.jxsmarthome.entity.CustomerProduct;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.record.SharedDB;

/**
 * 用户设备信息列表
 * 
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class CustomerProductList extends ResponseJson {
	private Task task = null;
	private byte[] requestBytes;

	public CustomerProductList(Task task, byte[] requestBytes) {
		this.task = task;
		this.requestBytes = requestBytes;
	}

	@Override
	public void response(InputStream in) throws Exception {
		if (in != null) {
			boolean isSuccess = false;
			RemoteJsonResultInfo _resultInfo = null;

			List<CustomerProduct> customerProductList = null;
			try {
				JSONObject jsonObject = this.getJsonObjectFromIn(in);

				_resultInfo = this.readResultInfo(jsonObject);
				if (_resultInfo.validResultCode.equals(ControlDefine.CONNECTION_SUCCESS)) {// 成功获取数据
					// 具体的解析工作在这里实现
					// 提示：任务中途检测任务是否被中止，同时可以直接返回任务取消数据（如有必要）
					if (this.task.ismTryCancel())
						return;
					// ////具体解析区////////////////////////
					// 取更新时间
					String _processTime = this.getJsonString(jsonObject,
							"processTime");
					String _account = CommUtil.getCurrentLoginAccount();
					
					// 取列表数据
					JSONArray jsonArray = jsonObject
							.getJSONArray("serviceContent");
					customerProductList = new ArrayList<CustomerProduct>();
					for (int i = 0; i < jsonArray.length(); i++) {
						if (this.task.ismTryCancel())
							return;
						JSONObject _jo = (JSONObject) jsonArray.get(i);
						customerProductList.add(new CustomerProduct(
								getJsonString(_jo, "whId"), getJsonString(_jo,
										"code"),
								getJsonString(_jo, "proTime"), getJsonString(
										_jo, "typeNo"),
								getJsonInt(_jo, "batch"), getJsonString(_jo,
										"version"), getJsonString(_jo,
										"qcReport"), getJsonString(_jo,
										"checker"), getJsonString(_jo,
										"checkTime"), getJsonString(_jo,
										"eqDesc"), getJsonString(_jo,
										"producer"), getJsonString(_jo,
										"recordTime"), getJsonString(_jo,
										"recorder"), getJsonString(_jo,
										"comments"), getJsonString(_jo, "mac"),
								getJsonString(_jo, "updateUser"),
								getJsonString(_jo, "updateTime"),
								getJsonString(_jo, "icon"),getJsonString(_jo, "typeName"),getJsonString(_jo, "address485")));
					}

					// /////////////////////////////////////
					isSuccess = true;
					SharedDB.saveStrDB(_account, ControlDefine.
							KEY_CUSTOMER_PRODUCT_LIST, _processTime);
				}
			} catch (JSONException e) {
				isSuccess = false;
			} catch (Exception e) {
				isSuccess = false;
			} finally {
				if (isSuccess) {
					this.task.callback(customerProductList);
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
