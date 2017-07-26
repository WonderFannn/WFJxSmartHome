package com.jinxin.jxsmarthome.cmd.entity;

import com.jinxin.datan.local.util.StringUtil;
import com.jinxin.jxsmarthome.cmd.Constants;

/**
 * Zigbee报文
 * 		格式：protocol + version + length + content + <ENTER>
 * 		例子：ZG100005AAAAA\r\n
 * @author TangLong
 * @company 金鑫智慧
 */
public class PackageMessage2 {
	private String protocol;
	private String version;
	private String length;
	private String content;
	
	public PackageMessage2(String content) {
		this.content = content;
		this.protocol = Constants.ZIGBEE_PROTOCOL;
		this.version = Constants.ZIGBEE_VERSION;
		this.setLength();
	}
	
	public PackageMessage2(String protocol, String version, String content) {
		super();
		this.protocol = protocol;
		this.version = version;
		this.content = content;
		this.setLength();
	}
	
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getLength() {
		return length;
	}
	public void setLength() {
		int length = content.length();
		String str = StringUtil.getFixedLengthStrFromInt(length, Constants.ZIGBEE_CONTENLENGTH);
		this.length = str;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
