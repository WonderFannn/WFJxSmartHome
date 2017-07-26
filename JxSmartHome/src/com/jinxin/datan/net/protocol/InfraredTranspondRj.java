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
import com.jinxin.jxsmarthome.entity.RemoteBrandsType;
import com.jinxin.jxsmarthome.entity.WHproductUnfrared;

import android.provider.ContactsContract.Data;
/**
 * 无线红外发送
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class InfraredTranspondRj extends ResponseJson {
	private Task task = null;
	private byte[] requestBytes;
	
	public InfraredTranspondRj(Task task, byte[] requestBytes) {
		this.task = task;
		this.requestBytes = requestBytes;
	}
	
	@Override
	public void response(InputStream in) throws Exception {
		if (in != null) {
			boolean isSuccess = false;
			RemoteJsonResultInfo _resultInfo = null;
			List<WHproductUnfrared> wHproductUnfrareds = null;
			try {
				JSONObject jsonObject = this.getJsonObjectFromIn(in);
				_resultInfo = this.readResultInfo(jsonObject);
				WHproductUnfrared unfrared=new WHproductUnfrared();
				wHproductUnfrareds = new ArrayList<WHproductUnfrared>();
				if (_resultInfo.validResultCode.equals(ControlDefine.CONNECTION_SUCCESS)) {// 成功获取数据
					isSuccess = true;
					
					JSONArray jsonArray = jsonObject
							.getJSONArray("serviceContent");
					System.out.println("===_resultInfo========>>>>"+jsonArray.toString());
					
					for (int i = 0; i < jsonArray.length(); i++) {
						if (this.task.ismTryCancel())
							return;
						JSONObject _jo = (JSONObject) jsonArray.get(i);
						wHproductUnfrareds.add(new WHproductUnfrared(
								getJsonInt(_jo, "id"), getJsonString(_jo, "whId"),
								getJsonInt(_jo, "fundId"),getJsonString(_jo, "recordName"),
								getJsonString(_jo, "infraRedCode"),getJsonInt(_jo, "createTime"),
								getJsonInt(_jo, "updateTime"),getJsonInt(_jo, "serCode"),
								getJsonInt(_jo, "enable")
								));
					}
					
				}
				
			} catch(JSONException e){
				isSuccess = false;
			} catch (Exception e) {
				isSuccess = false;
			}
			finally {
				 if(isSuccess){
			            this.task.callback(wHproductUnfrareds);
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
