package com.jinxin.jxsmarthome.entity;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;

/**
 * 遥控配置功能关系
 * @author TangLong
 * @company 金鑫智慧
 */
@Table(name = "product_remote_config_fun")
public class ProductRemoteConfigFun {
	@Id
	@Column(name = "id")
	private int id; // 主键
	@Column(name = "customerId")
	private String customerId; // 用户
	@Column(name = "funId")
	private String funId; // 功能点
	@Column(name = "type")
	private String type; // 类型：1:电视,2:空调,3:机顶盒
	@Column(name = "configId")
	private String configId; // 遥控配置ID
	@Column(name = "areaId")
	private String areaId; // 遥控配置ID
	@Column(name = "updateTime")
	private String updateTime; // 更新时间
	
	public ProductRemoteConfigFun() {
		super();
	}
	
	@Override
	public String toString() {
		return "ProductRemoteConfigFun [id=" + id + ", customerId="
				+ customerId + ", funId=" + funId + ", type=" + type
				+ ", configId=" + configId + ", areaId=" + areaId
				+ ", updateTime=" + updateTime + "]";
	}

	public ProductRemoteConfigFun(int id, String customerId, String funId,
			String type, String configId, String areaId, String updateTime) {
		super();
		this.id = id;
		this.customerId = customerId;
		this.funId = funId;
		this.type = type;
		this.configId = configId;
		this.areaId = areaId;
		this.updateTime = updateTime;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getFunId() {
		return funId;
	}
	public void setFunId(String funId) {
		this.funId = funId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getConfigId() {
		return configId;
	}
	public void setConfigId(String configId) {
		this.configId = configId;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public String getAreaId() {
		return areaId;
	}
	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}
}
