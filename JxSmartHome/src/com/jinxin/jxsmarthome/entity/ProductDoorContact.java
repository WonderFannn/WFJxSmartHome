package com.jinxin.jxsmarthome.entity;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;

/**
 * 报警设备状态、电量
 * @author YangJiJun
 * @company 金鑫智慧
 */
@Table(name = "product_door_contact")
public class ProductDoorContact extends Base {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "whId")
	private String whId;
	@Column(name = "status")
	private String status;// 00:关 ,01：开
	/**
	 * 电量
	 */
	@Column(name = "electric")
	private String electric;
	/**
	 * 是否开启警告
	 */
	@Column(name = "isWarn")
	private int isWarn = 1; //是否开启警报 0：关， 1： 开
	
	public ProductDoorContact() {
	}

	public ProductDoorContact(String whId, String status, String electric) {
		super();
		this.whId = whId;
		this.status = status;
		this.electric = electric;
		this.isWarn = 1;
	}

	public String getWhId() {
		return whId;
	}

	public void setWhId(String funId) {
		this.whId = funId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getElectric() {
		return electric;
	}

	public void setElectric(String electric) {
		this.electric = electric;
	}

	public int getIsWarn() {
		return isWarn;
	}

	public void setIsWarn(int isWarn) {
		this.isWarn = isWarn;
	}
}
