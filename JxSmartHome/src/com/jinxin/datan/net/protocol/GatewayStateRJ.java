package com.jinxin.datan.net.protocol;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONObject;

import android.text.TextUtils;

import com.jinxin.datan.net.module.RemoteJsonResultInfo;
import com.jinxin.datan.toolkit.task.Task;

/**修改密码返回数据解析
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class GatewayStateRJ extends ResponseJson {
	
	private Task task = null;
	private byte[] requestBytes;
	
	public GatewayStateRJ(Task task, byte[] requestBytes) {
		this.task = task;
		this.requestBytes = requestBytes;
	}

	@Override
	public void response(InputStream in) throws Exception {
		if (in != null) {//返回结果为数组 正常返回：[{"customerId":"","whId":"","ip":""}] 错误返回：[]

			boolean isSuccess = false;
			RemoteJsonResultInfo _resultInfo = null;
			StringBuffer strBuf = new StringBuffer();
			BufferedReader buffer = null;
			try {
				String line;
		        buffer = new BufferedReader(new InputStreamReader(   
		                in));   
		        while ((line = buffer.readLine()) != null) {   
		            strBuf.append(line);
		        }
		        String sResult = strBuf.toString().substring(1, strBuf.toString().length()-1);
		        JSONObject result = new JSONObject();
	        	JSONObject responseJo = new JSONObject();
		        if (!TextUtils.isEmpty(sResult)) {
		        	JSONObject jsonObject = new JSONObject(sResult);
		        	//读取返回结果数据
		        	/******************** 添加Json结果 **********************/
		        	result.put("rspCode", "0000");
		        	result.put("rspDesc", "网关在线");
		        	isSuccess = true;
		        	
				}else{
					isSuccess = false;
					result.put("rspCode", "0004");
					result.put("rspDesc", "无法获取网关状态");
				}
		        responseJo.put("response", result);
	        	_resultInfo = this.readResultInfo(responseJo);
			} catch (Exception e) {
				isSuccess = false;
			} finally {
				if (isSuccess) {
					this.task.callback("网关在线");
				} else {
					this.task.onError("无法获取网关状态");
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
