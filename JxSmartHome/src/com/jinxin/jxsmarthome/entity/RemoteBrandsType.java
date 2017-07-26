package com.jinxin.jxsmarthome.entity;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;


/**
 * 红外遥控设备类型
 * @company 金鑫智慧
 */
@Table(name = "remote_brands_type")
public class RemoteBrandsType extends Base{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "id")
	private int id;
	/**客户ID**/
	@Column(name = "modelQ")
	private Integer modelQ ;
	@Column(name = "brandName")
	private String brandName ;
	
	@Column(name = "ebrandName")
	private String ebrandName ;
	
	/**
	 * 1=空调  2=电视顶盒 4= DVD/VCD	5=电风扇 6=空气净化器
	 */
	@Column(name = "deviceId")
	private Integer deviceId ;
	
	@Column(name = "modelList")
	private String modelList ;
	
	@Column(name = "updateTime")
	private String updateTime ;
	
	public RemoteBrandsType() {
	
	}

	public RemoteBrandsType(int id, Integer modelQ, String brandName,String ebrandName,
			Integer deviceId, String modelList, String updateTime) {
		super();
		this.id = id;
		this.modelQ = modelQ;
		this.brandName = brandName;
		this.ebrandName = ebrandName;
		this.deviceId = deviceId;
		this.modelList = modelList;
		this.updateTime = updateTime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getModelQ() {
		return modelQ;
	}

	public void setModelQ(Integer modelQ) {
		this.modelQ = modelQ;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getEbrandName() {
		return ebrandName;
	}

	public void setEbrandName(String ebrandName) {
		this.ebrandName = ebrandName;
	}

	public Integer getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}

	public String getModelList() {
		return modelList;
	}

	public void setModelList(String modelList) {
		this.modelList = modelList;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	
}
