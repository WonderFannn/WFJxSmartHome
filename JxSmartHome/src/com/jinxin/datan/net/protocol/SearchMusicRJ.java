package com.jinxin.datan.net.protocol;

import java.io.InputStream;

import org.json.JSONObject;


import android.text.TextUtils;

import com.jinxin.datan.net.module.RemoteJsonResultInfo;
import com.jinxin.datan.toolkit.task.Task;
import com.jinxin.jxsmarthome.entity.BaiduMusic;

/**修改密码返回数据解析
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class SearchMusicRJ extends ResponseJson {
	
	private Task task = null;
	private byte[] requestBytes;
	
	public SearchMusicRJ(Task task, byte[] requestBytes) {
		this.task = task;
		this.requestBytes = requestBytes;
	}

	@Override
	public void response(InputStream in) throws Exception {

		if (in != null) {
			boolean isSuccess = false;
			BaiduMusic music = null;
			RemoteJsonResultInfo _resultInfo = null;
			try {
				
				JSONObject jsonObject = this.getJsonObjectFromIn(in);
				/********************添加Json结果**********************/
				JSONObject result = new JSONObject();
				JSONObject responseJo = new JSONObject();
				if ("0".equals(getJsonString(jsonObject, "count"))) {//歌曲地址为空 返回失败
					result.put("rspCode", "0404");
					result.put("rspDesc", "歌曲未找到");
				}else{
					result.put("rspCode", "0000");
					result.put("rspDesc", "获取歌曲地址成功");
					music = new BaiduMusic(getJsonInt(jsonObject, "count"),
							getJsonString(jsonObject, "urlEncode"), 
							getJsonString(jsonObject, "urlDecode"),
							getJsonString(jsonObject, "lrcid"),
							getJsonString(jsonObject, "durlEncode"),
							getJsonString(jsonObject, "durlDecode"),
							getJsonString(jsonObject, "p2pUrl"));
					
					isSuccess = true;
				}
				responseJo.put("response", result);
				_resultInfo = this
						.readResultInfo(responseJo);
			} catch (Exception e) {
				isSuccess = false;
			}
			finally {
				 if(isSuccess){
			            this.task.callback(music);
			        }else{
			        	this.task.onError("歌曲未找到");
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
