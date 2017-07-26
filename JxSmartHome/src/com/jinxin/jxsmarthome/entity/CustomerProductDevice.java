package com.jinxin.jxsmarthome.entity;

import java.util.List;


/**
 * 客户设备单路单元
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class CustomerProductDevice extends Base{

	private String code;
	private String cmdName;
	private String icon;
	private List<CustomerProductCMD> customerProductCMDList = null;
	private String whId = null;
		
	public CustomerProductDevice(){
		
	}

	public CustomerProductDevice(String code, String cmdName, String icon,
			List<CustomerProductCMD> customerProductCMDList, String whId) {
		super();
		this.code = code;
		this.cmdName = cmdName;
		this.icon = icon;
		this.customerProductCMDList = customerProductCMDList;
		this.whId = whId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCmdName() {
		return cmdName;
	}

	public void setCmdName(String cmdName) {
		this.cmdName = cmdName;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public List<CustomerProductCMD> getCustomerProductCMDList() {
		return customerProductCMDList;
	}

	public void setCustomerProductCMDList(
			List<CustomerProductCMD> customerProductCMDList) {
		this.customerProductCMDList = customerProductCMDList;
	}

	public String getWhId() {
		return whId;
	}

	public void setWhId(String whId) {
		this.whId = whId;
	}

	

	
}
