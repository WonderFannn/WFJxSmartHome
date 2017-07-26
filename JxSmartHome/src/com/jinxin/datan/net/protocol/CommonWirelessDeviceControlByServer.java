package com.jinxin.datan.net.protocol;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import android.text.TextUtils;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.module.RemoteJsonResultInfo;
import com.jinxin.datan.toolkit.task.Task;
import com.jinxin.jxsmarthome.cmd.entity.CmdParserStrategy;
import com.jinxin.jxsmarthome.util.Logger;

/**
 * 通用设备控制解析（通过服务器）
 * 
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class CommonWirelessDeviceControlByServer extends ResponseJson {

	private Task task = null;
	private byte[] requestBytes;
	private CmdParserStrategy parseStrategy;
	/**
	 * 1-ZG锁结果解析，0-其他无线设备解析
	 */
	private int requestType = 0;

	public CommonWirelessDeviceControlByServer(Task task, byte[] requestBytes, CmdParserStrategy parseStrategy, int requestType) {
		this.task = task;
		this.requestBytes = requestBytes;
		this.parseStrategy = parseStrategy;
		this.requestType = requestType;
	}

	@Override
	public void response(InputStream in) throws Exception {
		RemoteJsonResultInfo resultInfo = new RemoteJsonResultInfo();
		if (in != null) {
			Logger.warn(null, "parse response for wireless");
			DataInputStream dis = new DataInputStream(in);
			String tempStr = "-1";
			boolean isSuccess = true;
			/* Zigbee 解析，偶数行解析  */
			if(parseStrategy == CmdParserStrategy.ZIGBEE) {
				int cmdLength = getCmdArrayLength();
				Logger.debug("cmdLength", "cmdLength="+cmdLength);
				if(cmdLength > 0) {
					for(int i = 1; i <= cmdLength; i++) {
						try{
							// 当指令超过一条时，为奇数行命令时，抛出异常，使其不对结果做解析，但任然会有返回
							if(cmdLength > 1 && i%2 != 0){
//								isSuccess = true;
								throw new Exception("cannot parse exception.");
							}
							int cmd = dis.readInt(); // cmd，无用
							System.out.println("-->cmd:" + cmd);
							int len = dis.readInt(); // 内容长度
							System.out.println("-->len:" + len);
							if (len != 0) {
								//长度过大不进行解析
								if (len < 1024*10) {
									byte[] buf = new byte[len];
									
									dis.read(buf);
									tempStr = new String(buf);
									System.out.println("-->content:" + tempStr);
									if (tempStr.startsWith("01") || tempStr.startsWith("02")) {
										isSuccess = false;
										break;
									}else{
										//解析返回结果，最后两位“00”代表成功，其他代表失败
										if (!TextUtils.isEmpty(tempStr)) {
											//如果返回结果包含此字符串则为巡检指令，不做进一步判断
											if (tempStr.contains("cluster=0x0000")) {
												isSuccess = true;
											}else{
												//对结果进一步解析 以“00”结尾代表成功
												String payload = tempStr.substring(tempStr.indexOf("[")+1,
														tempStr.indexOf("]")).trim();
												System.out.println("payload:"+payload);
												if (!TextUtils.isEmpty(payload) ) {
													if (this.requestType == 1) {
														tempStr = payload;
														isSuccess = true;
													}else{
														if (payload.endsWith("00")) {
															isSuccess = true;
														}
													}
												}else{
													isSuccess = false;
													tempStr = "操作失败";
												}
											}
										}
										
									}
								}
							}
						}catch(EOFException e){
							
						}catch(IOException e) {
							// do nothing...
						}catch(Exception e) {
							// do nothing...
						}finally{
							if (isSuccess) {
								Logger.debug(null, "call onSuccess");
								resultInfo.validResultCode = "0000";
								resultInfo.validResultInfo = tempStr;
								Logger.debug(null, resultInfo.toString());
								this.setResultInfo(resultInfo);
								this.task.callback(resultInfo);
							}else{
//								if(i==1) {
								Logger.debug(null, "call onFail");
//								resultInfo.validResultCode = "0004";
								resultInfo.validResultCode = tempStr.substring(0, 2);
								System.out.println("--->" + resultInfo.validResultCode);
								if(DatanAgentConnectResource.SERVER_ERROR_MSG_01.equals(resultInfo.validResultCode)) {
									resultInfo.validResultInfo = "无线网关离线";
								} else if(DatanAgentConnectResource.SERVER_ERROR_MSG_02.equals(resultInfo.validResultCode)) {
									resultInfo.validResultInfo = "处理超时";
								} else {
									resultInfo.validResultInfo = tempStr;
								}
								Logger.debug(null, resultInfo.toString());
								this.setResultInfo(resultInfo);
//								this.task.callback(resultInfo);
								this.task.onError(resultInfo);
//								return;
//								}
							}
						}
					}
				}
			} else if(parseStrategy == CmdParserStrategy.ZIGBEE_DEVICE_CHECK) {//巡检
				try{
					int cmd = dis.readInt(); // cmd，无用
					System.out.println(parseStrategy + "-->cmd:" + cmd);
					int len = dis.readInt(); // 内容长度
					if (len != 0) {
						byte[] buf = new byte[len];
						System.out.println("-->len:" + len);
						dis.read(buf);
						tempStr = new String(buf);
						System.out.println("-->content:" + tempStr);
					}
					Logger.debug(null, "call onSuccess");
					resultInfo.validResultCode = "0000";
					resultInfo.validResultInfo = tempStr;
					Logger.debug(null, resultInfo.toString());
				}catch(Exception e) {
					Logger.debug(null, e.getMessage());
					resultInfo.validResultCode = "0001";
					resultInfo.validResultInfo = new String("解析失败");
				}finally{
					this.setResultInfo(resultInfo);
					this.task.callback(resultInfo);
				}
			}
			
		}else {
			Logger.debug(null, "call onFail");
			resultInfo.validResultCode = "0001";
			resultInfo.validResultInfo = new String("请求失败");
			this.setResultInfo(resultInfo);
			this.task.onError(resultInfo);
		}
	}

	@Override
	public byte[] toOutputBytes() {
		return this.requestBytes;
	}

	/**
	 * 获取命令的长度
	 */
	private int getCmdArrayLength() {
		if(requestBytes != null) {
			String cmdArrayStr = new String(requestBytes);
			return cmdArrayStr.split(",").length - 1;
		}
		return 0;
	}
	
}
