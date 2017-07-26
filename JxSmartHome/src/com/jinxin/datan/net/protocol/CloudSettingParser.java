package com.jinxin.datan.net.protocol;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jinxin.jxsmarthome.util.Logger;

import com.jinxin.datan.net.module.RemoteJsonResultInfo;
import com.jinxin.datan.toolkit.task.Task;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.CloudSetting;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.record.SharedDB;

/**
 * 云设置数据解析
 * 
 * @author  TangLong
 * @company 金鑫智慧
 */
public class CloudSettingParser extends ResponseJson {
	private Task task = null;
	private byte[] requestBytes;

	public CloudSettingParser(Task task, byte[] requestBytes) {
		this.task = task;
		this.requestBytes = requestBytes;
	}

	@Override
	public void response(InputStream in) throws Exception {
		if (in != null) {
			boolean isSuccess = false;
			RemoteJsonResultInfo _resultInfo = null;

			List<CloudSetting> csList = null;
			try {
				JSONObject jsonObject = this.getJsonObjectFromIn(in);

				_resultInfo = this.readResultInfo(jsonObject);
				if (_resultInfo.validResultCode.equals(ControlDefine.CONNECTION_SUCCESS)) {
					// 具体的解析工作在这里实现
					// 提示：任务中途检测任务是否被中止，同时可以直接返回任务取消数据（如有必要）
					if (this.task.ismTryCancel()) {
						return;
					}
					
					String _processTime = this.getJsonString(jsonObject,
							"updateTime");
					String _account = CommUtil.getCurrentLoginAccount();
					
					//////////////////////具体解析区////////////////////////
					JSONArray jsonArray = jsonObject.getJSONArray("serviceContent");
					csList = new ArrayList<CloudSetting>();
					
					Logger.error(null, jsonArray.toString());
					
					for (int i = 0; i < jsonArray.length(); i++) {
						if (this.task.ismTryCancel()) {
							return;
						}
						JSONObject _jo = (JSONObject) jsonArray.get(i);
						
						CloudSetting cs = new CloudSetting(0,
								getJsonString(_jo, "id"), 
								getJsonString(_jo, "customerId"),
								getJsonString(_jo, "category"),
								getJsonString(_jo, "items"), 
								getJsonString(_jo, "params"), 
								getJsonString(_jo, "updateTime"), 
								null);
						
						
						Logger.error("tangl", cs.toString());
						csList.add(cs);
					}
					//////////////////////具体解析完成////////////////////////
					
					isSuccess = true;
					SharedDB.saveStrDB(_account, ControlDefine.
							KEY_CLOUD_SETTING, _processTime);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				isSuccess = false;
			} catch (Exception e) {
				e.printStackTrace();
				isSuccess = false;
			} finally {
				if (isSuccess) {
					this.task.callback(csList);
				} else {
					this.task.onError(_resultInfo.validResultInfo);
				}
				this.closeInputStream(in);
			}
		}
	}

	@Override
	public byte[] toOutputBytes() {
		return this.requestBytes;
	}

}
