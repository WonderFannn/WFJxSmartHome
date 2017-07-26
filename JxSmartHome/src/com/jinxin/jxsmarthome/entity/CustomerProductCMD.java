package com.jinxin.jxsmarthome.entity;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;

/**
 * 客户设备指令信息
 * @deprecated
 * @author JackeyZhang
 * @company 金鑫智慧
 */
@Table(name = "customer_product_cmd")
public class CustomerProductCMD extends Base{
	@Id
	@Column(name = "cmdId")
	private int cmdId;
	@Column(name = "cmdName")
	private String cmdName;
	@Column(name = "customerId")
	private String customerId;
	@Column(name = "code")
	private String code;
	@Column(name = "cmd")
	private String cmd;
	@Column(name = "updateTime")
	private String updateTime;
	@Column(name = "whId")
	private String whId;
	
		
	public CustomerProductCMD(){
		
	}


	public CustomerProductCMD(int cmdId, String cmdName, String customerId,
			String code, String cmd, String updateTime, String whId) {
		super();
		this.cmdId = cmdId;
		this.cmdName = cmdName;
		this.customerId = customerId;
		this.code = code;
		this.cmd = cmd;
		this.updateTime = updateTime;
		this.whId = whId;
	}


	public int getCmdId() {
		return cmdId;
	}


	public void setCmdId(int cmdId) {
		this.cmdId = cmdId;
	}


	public String getCmdName() {
		return cmdName;
	}


	public void setCmdName(String cmdName) {
		this.cmdName = cmdName;
	}


	public String getCustomerId() {
		return customerId;
	}


	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}


	public String getCode() {
		return code;
	}


	public void setTypeId(String code) {
		this.code = code;
	}


	public String getCmd() {
		return cmd;
	}


	public void setCmd(String cmd) {
		this.cmd = cmd;
	}


	public String getUpdateTime() {
		return updateTime;
	}


	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}


	public String getWhId() {
		return whId;
	}


	public void setWhId(String whId) {
		this.whId = whId;
	}


	
	
	
	
}
