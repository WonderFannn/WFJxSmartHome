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
import com.jinxin.jxsmarthome.entity.CustomerBrands;
import com.jinxin.jxsmarthome.entity.RemoteBrandsType;
import com.jinxin.jxsmarthome.util.CommUtil;

/**
 * 定时任务列表解析
 * 
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class CustomerMatchCodeRj extends ResponseJson {
	private Task task = null;
	private byte[] requestBytes;

	public CustomerMatchCodeRj(Task task, byte[] requestBytes) {
		this.task = task;
		this.requestBytes = requestBytes;
	}

	@Override
	public void response(InputStream in) throws Exception {
		if (in != null) {
			boolean isSuccess = false;
			RemoteJsonResultInfo _resultInfo = null;
			List<RemoteBrandsType> barndsTypeList = null;
			try {
				JSONObject jsonObject = this.getJsonObjectFromIn(in);

				_resultInfo = this.readResultInfo(jsonObject);
				if (_resultInfo.validResultCode.equals(ControlDefine.CONNECTION_SUCCESS)) {// 成功获取数据
					// 具体的解析工作在这里实现
					// 提示：任务中途检测任务是否被中止，同时可以直接返回任务取消数据（如有必要）
					if (this.task.ismTryCancel())
						return;
					JSONArray jsonArray = jsonObject
							.getJSONArray("serviceContent");
					barndsTypeList = new ArrayList<RemoteBrandsType>();
					for (int i = 0; i < jsonArray.length(); i++) {
						if (this.task.ismTryCancel())
							return;
						JSONObject _jo = (JSONObject) jsonArray.get(i);
						barndsTypeList.add(new RemoteBrandsType(getJsonInt(_jo, "id"), 
								getJsonInt(_jo, "modelQ"), 
								getJsonString(_jo,"brandName"),
								getJsonString(_jo,"ebrandName"),
								getJsonInt(_jo, "deviceId"), 
								getJsonString(_jo,"modelList"),
								getJsonString(_jo,"updateTime")));
					}
					
					isSuccess = true;
				}
			} catch (JSONException e) {
				isSuccess = false;
			} catch (Exception e) {
				isSuccess = false;
			} finally {
				if (isSuccess) {
					this.task.callback(barndsTypeList);
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
