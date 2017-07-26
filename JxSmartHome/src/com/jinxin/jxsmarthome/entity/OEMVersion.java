package com.jinxin.jxsmarthome.entity;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;

/**
 * 代理商版权信息
 * @author JackeyZhang
 * @company 金鑫智慧
 */
@Table(name = "oem_version")
public class OEMVersion extends Base {
	@Id
	@Column(name = "id")
	private int id;
	@Column(name = "appLog")
	private String appLog;
	@Column(name = "nightAppLog")
	private String nightAppLog;
	@Column(name = "copyright")
	private String copyright;
	@Column(name = "message")
	private String message;
	@Column(name = "account")
	private String account;

	public OEMVersion() {
	}
	
	@Override
	public String toString() {
		return "id:" + id + ",account:" + account + ",copyright:" + copyright;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAppLog() {
		return appLog;
	}

	public void setAppLog(String appLog) {
		this.appLog = appLog;
	}

	public String getNightAppLog() {
		return nightAppLog;
	}

	public void setNightAppLog(String nightAppLog) {
		this.nightAppLog = nightAppLog;
	}

	public String getCopyright() {
		return copyright;
	}

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

}
