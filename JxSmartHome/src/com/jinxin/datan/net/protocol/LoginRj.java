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
import com.jinxin.jxsmarthome.constant.MusicSetting;
import com.jinxin.jxsmarthome.entity.SysUser;
import com.jinxin.record.SharedDB;

public class LoginRj extends ResponseJson {
	private Task task = null;
	private byte[] requestBytes;
	
	public LoginRj(Task task, byte[] requestBytes) {
		this.task = task;
		this.requestBytes = requestBytes;
	}
	
	@Override
	public void response(InputStream in) throws Exception {
		if (in != null) {
			boolean isSuccess = false;
			RemoteJsonResultInfo _resultInfo = null;
//			List<LoginEntry> loginEntry = null;
			SysUser sysUser = null;
			String secretKey = null;
			try {
				JSONObject jsonObject = this.getJsonObjectFromIn(in);
								
				_resultInfo = this
						.readResultInfo(jsonObject);
				if (_resultInfo.validResultCode.equals(ControlDefine.CONNECTION_SUCCESS)) {// 成功获取数据
					// 具体的解析工作在这里实现
					// 提示：任务中途检测任务是否被中止，同时可以直接返回任务取消数据（如有必要）
					if (this.task.ismTryCancel()) return;
					//////具体解析区////////////////////////
					//取列表数据
//					JSONArray jsonArray = jsonObject.getJSONArray("serviceContent");
//					loginEntry = new ArrayList<LoginEntry>();
//					for(int i = 0; i < jsonArray.length(); i++){
//						if (this.task.ismTryCancel()) return;
//						JSONObject _jo = (JSONObject)jsonArray.get(i);
//						JSONObject _jo = jsonObject.getJSONObject("serviceContent");
//						loginEntry.add(new LoginEntry( getJsonString(_jo, "secretKey") ));
					
						JSONObject _obj = jsonObject.getJSONObject("serviceContent");
						sysUser = new SysUser();
						sysUser.setAccount(_obj.getString("account"));
						sysUser.setPassword(_obj.getString("secretKey"));
						sysUser.setNickyName(_obj.getString("nickyName"));
						sysUser.setSubAccunt(_obj.getString("subAccunt"));
						if (_obj.has("gfversion")) {
							SharedDB.saveStrDB(MusicSetting.SP_NAME, MusicSetting.NEWEST_VERSION, _obj.getString("gfversion"));
						}
						String location = getJsonString(_obj, "location");
						JSONObject locationObj = new JSONObject(location);
						String province = locationObj.getString("province");
						String city = locationObj.getString("city");
						SharedDB.saveStrDB(SharedDB.ORDINARY_CONSTANTS, ControlDefine.CURR_CITY, city);
						
//					}
					
					///////////////////////////////////////
					isSuccess = true;
				}
			} catch(JSONException e){
				isSuccess = false;
			} catch (Exception e) {
				isSuccess = false;
			}
			finally {
				 if(isSuccess){
			            this.task.callback(sysUser);
//			            this.task.callback(secretKey);
			        }else{
			        	this.task.onError(_resultInfo.validResultInfo);
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
