package com.jinxin.jxsmarthome.entity;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;
/**
 * 设备分组的VO类
 * @author TangLong
 * @company 金鑫智慧
 */
@Table(name = "customer_product_area")
public class CustomerProductArea extends Base {
	private static final long serialVersionUID = -1531720104130041841L;
	@Id
	@Column(name = "id")
	private int id;					// 记录主键
	@Column(name = "customerId")
	private String customerId;		// 用户名
	@Column(name = "areaName")
	private String areaName;		// 区域名称（分组名称）
	@Column(name = "areaId")
	private int areaId;				// 区域ID
	@Column(name = "whId")
	private String whId;			// 家居设备序列号
	@Column(name = "funId")
	private int funId;				// 功能FUN_ID
	@Column(name = "createTime")
	private String createTime;		// 创建时间
	@Column(name = "updateTime")
	private String updateTime;		// 更新时间
	@Column(name = "status")
	private int status;				// 状态(0:disable 1:enable)
	
	public CustomerProductArea() {}
	
	public CustomerProductArea(int id, String customerId, String areaName, int areaId,
			String whId, int funId) {
		this.id = id;
		this.customerId = customerId;
		this.areaName = areaName;
		this.areaId = areaId;
		this.whId = whId;
		this.funId = funId;
	}
	
	public CustomerProductArea(int id, String customerId, String areaName, int areaId,
			String whId, int funId, String createTime, String updateTime, int status) {
		this.id = id;
		this.customerId = customerId;
		this.areaName = areaName;
		this.areaId = areaId;
		this.whId = whId;
		this.funId = funId;
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.status = status;
	}
	
	@Override
	public String toString() {
		return "CustomerProductArea [id=" + id + ", customerId=" + customerId
				+ ", areaName=" + areaName + ", areaId=" + areaId + ", whId="
				+ whId + ", funId=" + funId + ", createTime=" + createTime
				+ ", updateTime=" + updateTime + ", status=" + status + "]";
	}

	///////////////////////////////////////////////
	// getters and setters
	///////////////////////////////////////////////
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
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	public int getAreaId() {
		return areaId;
	}
	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}
	public String getWhId() {
		return whId;
	}
	public void setWhId(String whId) {
		this.whId = whId;
	}
	public int getFunId() {
		return funId;
	}
	public void setFunId(int funId) {
		this.funId = funId;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
}
