package com.jinxin.jxsmarthome.cmd.entity;

/**
 * 命令结果解析策略
 * @author TangLong
 * @company 金鑫智慧
 */
public enum CmdParserStrategy {
	ZIGBEE, /* zigbee与平台socket连接的返回结果的解析策略：偶数行解析  */
	ZIGBEE_DEVICE_CHECK 
}
