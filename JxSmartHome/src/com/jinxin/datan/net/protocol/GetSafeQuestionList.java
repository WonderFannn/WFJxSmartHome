package com.jinxin.datan.net.protocol;

import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.jinxin.datan.net.module.RemoteJsonResultInfo;
import com.jinxin.datan.toolkit.task.Task;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.SysUser;

public class GetSafeQuestionList extends ResponseJson{

	private Task task = null;
	private byte[] requestBytes;
	
	public GetSafeQuestionList(Task task, byte[] requestBytes) {
		this.task = task;
		this.requestBytes = requestBytes;
	}
	
	@Override
	public void response(InputStream in) throws Exception {
		if (in != null) {
			boolean isSuccess = false;
			RemoteJsonResultInfo _resultInfo = null;
			SysUser sysUser = null;
			try {
				JSONObject jsonObject = this.getJsonObjectFromIn(in);
								
				_resultInfo = this
						.readResultInfo(jsonObject);
				if (_resultInfo.validResultCode.equals(ControlDefine.CONNECTION_SUCCESS)) {// 成功获取数据
					// 具体的解析工作在这里实现
					// 提示：任务中途检测任务是否被中止，同时可以直接返回任务取消数据（如有必要）
					if (this.task.ismTryCancel()) return;
						//具体解析区
						JSONObject _obj = jsonObject.getJSONObject("serviceContent");
						sysUser = new SysUser();
						sysUser.setQuestion1(_obj.getString("question1"));
						sysUser.setQuestion2(_obj.getString("question2"));

						isSuccess = true;
				}
			} catch(JSONException e){
				isSuccess = false;
			} catch (Exception e) {
				isSuccess = false;
			}
			finally {
				 if(isSuccess){
					 if (!TextUtils.isEmpty(sysUser.getQuestion1()) && !TextUtils.isEmpty(sysUser.getQuestion2())) {
						 this.task.callback(sysUser);
					}
		        }else{
		        	this.task.onError(_resultInfo.validResultCode);
		        }
				this.closeInputStream(in);//必须关闭流
			}
		}
	}

	@Override
	public byte[] toOutputBytes() {
		return this.requestBytes;
	}


}
