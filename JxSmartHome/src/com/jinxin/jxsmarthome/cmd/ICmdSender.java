package com.jinxin.jxsmarthome.cmd;

/**
 * 命令发送器接口
 * @author TangLong
 * @company 金鑫智慧
 */
public interface ICmdSender {
	/**
	 * 发送命令
	 */
	public void send();
	
	/**
	 * 给指定的服务器发送命令
	 * host 服务器地址
	 */
	public void send(String host);
}
