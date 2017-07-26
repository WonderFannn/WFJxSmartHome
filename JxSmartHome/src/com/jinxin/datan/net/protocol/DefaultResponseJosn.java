package com.jinxin.datan.net.protocol;

import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.jinxin.datan.net.module.RemoteJsonResultInfo;
import com.jinxin.datan.net.protocol.ResponseJson;
import com.jinxin.datan.toolkit.task.Task;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.record.SharedDB;

public class DefaultResponseJosn extends ResponseJson {

	private Task task = null;
	private byte[] requestBytes;

	public DefaultResponseJosn(Task task, byte[] requestBytes) {
		this.task = task;
		this.requestBytes = requestBytes;
	}

	@Override
	public void response(InputStream in) throws Exception {
		if (in != null) {
			boolean isSuccess = false;
			RemoteJsonResultInfo _resultInfo = null;

			String result = "";
			try {
				JSONObject jsonObject = this.getJsonObjectFromIn(in);

				_resultInfo = this.readResultInfo(jsonObject);
				if (_resultInfo.validResultCode.equals(ControlDefine.CONNECTION_SUCCESS)) {
					// 具体的解析工作在这里实现
					// 提示：任务中途检测任务是否被中止，同时可以直接返回任务取消数据（如有必要）
					if (this.task.ismTryCancel()) {
						return;
					}
					
					//////////////////////具体解析区////////////////////////
					result = jsonObject.getString("serviceContent");
					
					String _processTime = this.getJsonString(jsonObject,
							"processTime");
					String _account = CommUtil.getCurrentLoginAccount();
					
					//////////////////////具体解析完成////////////////////////
					
					isSuccess = true;
					SharedDB.saveStrDB(_account,
							ControlDefine.KEY_CUSTOMER_AREA, _processTime);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				isSuccess = false;
			} catch (Exception e) {
				e.printStackTrace();
				isSuccess = false;
			} finally {
				if (isSuccess) {
					this.task.callback(result);
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
