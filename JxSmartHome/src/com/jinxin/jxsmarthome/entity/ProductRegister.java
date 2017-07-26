package com.jinxin.jxsmarthome.entity;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;

/**
 * 网关注册信息
 * @author TangLong
 * @company 金鑫智慧
 */
@Table(name = "product_register")
public class ProductRegister {
	@Id
	@Column(name = "whId")
	private String whId; // 智慧家居设备序列号
	@Column(name = "code")
	private String code; // 设备类型代码
	@Column(name = "userId")
	private String userId; // 用户ID
	@Column(name = "address485")
	private String address485; // 设备485地址
	@Column(name = "mac")
	private String mac;	// 设备mac地址
	@Column(name = "ip")
	private String ip; // 设备外部ip地址
	@Column(name = "registerTime")
	private String registerTime; // 注册时间
	@Column(name = "innerIp")
	private String innerIp; // 设备内部ip地址
	@Column(name = "gatewayWhId")
	private String gatewayWhId; // 智慧家居网关序列号
	
	public ProductRegister() {}
	
	public ProductRegister(String whId, String code, String userId,
			String address485, String mac, String ip, String registerTime,
			String innerIp, String gatewayWhId) {
		this.whId = whId;
		this.code = code;
		this.userId = userId;
		this.address485 = address485;
		this.mac = mac;
		this.ip = ip;
		this.registerTime = registerTime;
		this.innerIp = innerIp;
		this.gatewayWhId = gatewayWhId;
	}

	@Override
	public String toString() {
		return "ProductRegister [whId=" + whId + ", code=" + code + ", userId="
				+ userId + ", address485=" + address485 + ", mac=" + mac
				+ ", ip=" + ip + ", registerTime=" + registerTime
				+ ", innerIp=" + innerIp + ", gatewayWhId=" + gatewayWhId + "]";
	}

	public String getWhId() {
		return whId;
	}
	public void setWhId(String whId) {
		this.whId = whId;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getAddress485() {
		return address485;
	}
	public void setAddress485(String address485) {
		this.address485 = address485;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getRegisterTime() {
		return registerTime;
	}
	public void setRegisterTime(String registerTime) {
		this.registerTime = registerTime;
	}
	public String getInnerIp() {
		return innerIp;
	}
	public void setInnerIp(String innerIp) {
		this.innerIp = innerIp;
	}
	public String getGatewayWhId() {
		return gatewayWhId;
	}
	public void setGatewayWhId(String gatewayWhId) {
		this.gatewayWhId = gatewayWhId;
	}
}
