package com.jinxin.datan.net.protocol;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;
import com.jinxin.jxsmarthome.util.Logger;

import com.jinxin.datan.net.module.RemoteJsonResultInfo;
import com.jinxin.datan.toolkit.task.Task;
import com.jinxin.jxsmarthome.util.Logger;

/**
 * 通用设备控制解析（通过服务器）
 * 
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class CommonDeviceControlByServer2 extends ResponseJson {

	private Task task = null;
	private byte[] requestBytes;
	private int requestType = 0;
	private int cmdCount = 0;// 指令数目
	private List<CmdResult> resultList = new ArrayList<CmdResult>();

	public CommonDeviceControlByServer2(Task task, byte[] requestBytes, int requestType) {
		this.task = task;
		this.requestBytes = requestBytes;
		if (this.requestBytes != null) {
			String _cmd = new String(requestBytes);
			this.cmdCount = cCount(_cmd, '|');
		}
		this.requestType = requestType;
	}

	@Override
	public void response(InputStream in) throws Exception {
		/**** test *********/
		if (in != null) {
			boolean isSuccess = false;
			DataInputStream dis = null;
//			byte[] buf = null;
//			String tempStr = "";
			try {
				dis = new DataInputStream(in);
				System.out.println("cmdCount:" + cmdCount);
				for (int i = 0; i < this.cmdCount; i++) {//TODO:
					CmdResult _cmdr = new CmdResult();
					int cmd = dis.readInt(); // 响应类型0=web向平台发送执行命令请求/应答，1=手机向平台发送执行命令请求/应答,2= web向平台发送查询命令请求/应答，
											// 3=手机向平台发送查询命令请求/应答，4=平台主动向终端推送消息
					_cmdr.setCmd(cmd);
					int len = dis.readInt(); // 内容长度
					System.out.println("offline len:"+len);
					_cmdr.setLen(len);
					if (len != 0) {
						//长度过大不进行解析
						if (len < 1024*10) {
							byte[] buf = new byte[len];
							dis.read(buf);
							String tempStr = new String(buf);
							_cmdr.setContent(tempStr);
							System.out.println("cmdResult:" + tempStr);
						}else{
							_cmdr.setContent("");
						}
					} else {
						_cmdr.setContent("");
						System.out.println("cmdResult:");
					}
					this.resultList.add(_cmdr);
				}
				isSuccess = this.checkResult();
				System.out.println("isSuccess:"+isSuccess);
			} catch (Exception ex) {
				ex.printStackTrace();
				isSuccess = false;
			} finally {
//				Thread.currentThread().sleep(500);
				RemoteJsonResultInfo resultInfo = new RemoteJsonResultInfo();
				if (isSuccess) {
					if(requestType == 1){
						resultInfo.validResultCode = "0000";
						String _content = "";
						if(this.resultList.size() > 0){
							CmdResult cr = this.resultList.get(0);
							if(cr != null){
								_content = cr.getContent();
							}
						}
						resultInfo.validResultInfo = _content;
						this.setResultInfo(resultInfo);
						this.task.callback(resultInfo);
					}else{
						String _content = "";
						if(this.resultList.size() > 0){
							CmdResult cr = this.resultList.get(0);
							if(cr != null){
								_content = cr.getContent();
							}
						}
						resultInfo.validResultCode = "0000";
						resultInfo.validResultInfo = _content;
						this.setResultInfo(resultInfo);
						this.task.callback(resultInfo);
					}
				} else {
					if(requestType == 1){
						resultInfo.validResultCode = "0000";
						String _content = "";
						if(this.resultList.size() > 0){
							CmdResult cr = this.resultList.get(0);
							if(cr != null){
								_content = cr.getContent();
							}
						}
						resultInfo.validResultInfo = _content;
						this.setResultInfo(resultInfo);
						this.task.callback(resultInfo);
					}else{
						Logger.error("aaa", "操作失败");
					if (this.rusultStartWith("01")) {
							resultInfo.validResultCode = "01";
							resultInfo.validResultInfo = "网关处于离线";
							this.setResultInfo(resultInfo);
							this.task.onError(resultInfo);

					} else if (this.rusultStartWith("02")) {
							resultInfo.validResultCode = "02";
							resultInfo.validResultInfo = "处理超时，请检查设备与网关连接是否正常";
							this.setResultInfo(resultInfo);
							this.task.onError(resultInfo);

					} else if (this.rusultStartWith("03")) {
							resultInfo.validResultCode = "03";
							resultInfo.validResultInfo = "服务端业务处理失败";
							this.setResultInfo(resultInfo);
							this.task.onError(resultInfo);

					} else if (this.rusultStartWith("04")) {
							resultInfo.validResultCode = "04";
							resultInfo.validResultInfo = "未知异常";
							this.setResultInfo(resultInfo);
							this.task.onError(resultInfo);

					} else {
						resultInfo.validResultCode = "0000";
						resultInfo.validResultInfo = new String("操作成功");
						this.setResultInfo(resultInfo);
						this.task.callback(resultInfo);
					}
					// this.task.callback(resultInfo);
					}
				}
			}
		}
		/**** test *********/

		/*
		 * if (in != null) { boolean isSuccess = false; RemoteJsonResultInfo _resultInfo = null;
		 * 
		 * // List<LoginEntry> loginEntry = null; String secretKey = null; try { JSONObject jsonObject = this.getJsonObjectFromIn(in);
		 * 
		 * _resultInfo = this .readResultInfo(jsonObject); if (_resultInfo.validResultCode.equals(ControlDefine.CONNECTION_SUCCESS)) {//
		 * 成功获取数据 // 具体的解析工作在这里实现 // 提示：任务中途检测任务是否被中止，同时可以直接返回任务取消数据（如有必要） if (this.task.ismTryCancel()) return;
		 * //////具体解析区//////////////////////// //取列表数据 // JSONArray jsonArray = jsonObject.getJSONArray("serviceContent"); // loginEntry =
		 * new ArrayList<LoginEntry>(); // for(int i = 0; i < jsonArray.length(); i++){ if (this.task.ismTryCancel()) return; // JSONObject
		 * _jo = (JSONObject)jsonArray.get(i); // JSONObject _jo = jsonObject.getJSONObject("serviceContent"); // loginEntry.add(new
		 * LoginEntry( getJsonString(_jo, "secretKey") )); secretKey = getJsonString(jsonObject, "serviceContent"); // }
		 * 
		 * /////////////////////////////////////// isSuccess = true; } } catch(JSONException e){ isSuccess = false; } catch (Exception e) {
		 * isSuccess = false; } finally { if(isSuccess){ this.task.callback(secretKey); }else{
		 * this.task.onError(_resultInfo.validResultInfo); } this.closeInputStream(in);//必须关闭流 } }
		 */
	}

	@Override
	public byte[] toOutputBytes() {
		return this.requestBytes;
	}

	/**
	 * 统计字符方法
	 * 
	 * @param str
	 *            被检测的字符串
	 * @param c
	 *            被统计的字符
	 */
	public int cCount(String str, char c) {//TODO:
		if (str == null) return 0;
		int i = 0;
		// System.out.println("您输入的字符串为: "+str);
		// System.out.println("您要统计的字符为: "+c);
		char[] _charArray = str.toCharArray();
		for (char temp : _charArray) {
			if (temp == c) i++;
		}
//		if(i == 0) {
//			i++;
//		}
		System.out.println("cCount: "+i);
		return i;
	}
	/**
	 * 检测结果成功与否初级策略
	 * @return
	 */
	private boolean checkResult(){
		if(this.resultList.size() == 0)return false;
		int len = this.resultList.size();
		for(int i = 0;i < len;i++){
			CmdResult cr = this.resultList.get(i);
			if(cr != null){
				if(cr.len == 0 && "".equals(cr.content))return false;//策略1：出现无返回内容的指令结果视为失败
				if(cr.content.length() <= 2)return false;//策略2：出现内容为错误信息的指令结果视为失败
			}
		}
		return true;
	}
	/**
	 * 指令结果集中是否有用该code打头的返回信息
	 * @param code
	 * @return
	 */
	private boolean rusultStartWith(String code){
		if(this.resultList.size() == 0 || TextUtils.isEmpty(code))return false;
		int len = this.resultList.size();
		for(int i = 0;i < len;i++){
			CmdResult cr = this.resultList.get(i);
			if(cr != null){
				if(cr.getContent().startsWith(code)){
					return true;
				}
			}
		}
		return false;
	}
	private class CmdResult {

		int cmd = 0;
		int len = 0;
		String content = null;

		public int getCmd() {
			return cmd;
		}

		public void setCmd(int cmd) {
			this.cmd = cmd;
		}

		public int getLen() {
			return len;
		}

		public void setLen(int len) {
			this.len = len;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public CmdResult(int cmd, int len, String content) {
			this.cmd = cmd;
			this.len = len;
			this.content = content;
		}
		public CmdResult() {
			
		}
	}
}
