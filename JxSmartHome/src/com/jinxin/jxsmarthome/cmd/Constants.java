package com.jinxin.jxsmarthome.cmd;

public class Constants {
	/** ---------- 普通网关相关常量  ----------- **/
	/* 协议版本  */
	public static final String LOCAL_VERSION_1 = "10";		// 1.0版本
	public static final String LOCAL_VERSION_2 = "20";		// 2.0版本
	
	/* 协议命令字  */
	public static final int LOCAL_CMD_REQUEST = 0;			// 请求
	public static final int LOCAL_CMD_RESPONSE = 1;			// 应答
	public static final int LOCAL_CMD_KEEP_ALIVE = 2;		// 心跳
	public static final int LOCAL_CMD_REPORT = 3;			// 设备状态上报
	
	/* 报文长度  */
	public static final int LOCAL_CONTENLENGTH = 5;			// 报文内容长度
	public static final int LOCAL_SERIAL_MAXNUM = 99999;	// 随机码最大值
	
	/* 随机序列码的长度  */
	public static final int LOCAL_SERIAL_LENGTH = 5;		// 随机码长度
	public static final int LOCAL_SERIAL_LENGTH_A = 6;		// 随机码长度加A
	
	/** ---------- Zigbee网关相关常量  ----------- **/
	public static final String ZIGBEE_PROTOCOL = "ZG";
	public static final String ZIGBEE_VERSION = "10";
	public static final String ZIGBEE_SPLIT = "\r\n";
	public static final int ZIGBEE_CONTENLENGTH = 4;
}
