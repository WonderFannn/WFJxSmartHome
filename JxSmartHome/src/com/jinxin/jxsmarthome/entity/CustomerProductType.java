package com.jinxin.jxsmarthome.entity;

import java.util.List;


/**
 * 客户设备类型单元
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class CustomerProductType extends Base{

	private String code;
	private String icon;
	private String typeName;
	private List<CustomerProductDevice> customerProductDeviceList = null;
		
	public CustomerProductType(){
		
	}

	public CustomerProductType(String code, String icon, String typeName) {
		super();
		this.code = code;
		this.icon = icon;
		this.typeName = typeName;
	}
	public CustomerProductType(String code, String icon, String typeName,
			List<CustomerProductDevice> customerProductDeviceList) {
		super();
		this.code = code;
		this.icon = icon;
		this.typeName = typeName;
		this.customerProductDeviceList = customerProductDeviceList;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public List<CustomerProductDevice> getCustomerProductDeviceList() {
		return customerProductDeviceList;
	}

	public void setCustomerProductDeviceList(
			List<CustomerProductDevice> customerProductDeviceList) {
		this.customerProductDeviceList = customerProductDeviceList;
	}

	
	
}
