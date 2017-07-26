package com.jinxin.jxsmarthome.entity;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;

/**
 * 离线  网关搜索成功返回信息存放类
 * 
 * @author YangJiJun
 * @company 金鑫智慧
 */
@Table(name = "off_line_message")
public class OffLineContent extends Base {
	@Override
	public String toString() {
		return "OffLineContent [id=" + id + ", version=" + version + ", ip="
				+ ip + ", sn=" + sn + "]";
	}

	private static final long serialVersionUID = 6983165498125932815L;
	/**
	 * 网关硬件版本
	 */
	@Id
	@Column(name = "id")
	private int id;


	@Column(name = "version")
	private String version;
	/**
	 * 网关IP
	 */
	@Column(name = "ip")
	private String ip;
	/**
	 * 网关的注册号(序列号)
	 */
	@Column(name = "sn")
	private String sn;

	public OffLineContent() {

	}

	public OffLineContent(String version, String ip, String sn) {
		super();
		this.version = version;
		this.ip = ip;
		this.sn = sn;
	}
	


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

}
