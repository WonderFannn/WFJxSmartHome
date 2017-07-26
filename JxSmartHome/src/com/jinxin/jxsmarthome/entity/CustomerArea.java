package com.jinxin.jxsmarthome.entity;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;

/**
 * 用户模式分组的vo类
 * @author TangLong
 * @company 金鑫智慧
 */
@Table(name = "customer_area")
public class CustomerArea extends Base {
	private static final long serialVersionUID = -3382110516022389863L;
	@Id
	@Column(name = "id")
	private int id;					// 记录主键
	@Column(name = "customerId")
	private String customerId;		// 用户名
	@Column(name = "areaName")
	private String areaName;		// 区域名称（分组名称）
	@Column(name = "areaOrder")
	private int areaOrder;			// 区域顺序
	@Column(name = "createTime")
	private String createTime;		// 创建时间
	@Column(name = "updateTime")
	private String updateTime;		// 更新时间
	@Column(name = "status")
	private int status;				// 状态(0:disable 1:enable)
	
	public CustomerArea() {}
	
	public CustomerArea(int id, String customerId, String areaName, int areaOrder,
			String createTime, String updateTime, int status) {
		this.id = id;
		this.customerId = customerId;
		this.areaName = areaName;
		this.areaOrder = areaOrder;
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.status = status;
	}
	
	@Override
	public String toString() {
		return "id:" + id + ", customerId:" + customerId 
				+ ", areaName:" + areaName + ", areaOrder:" + areaOrder + ", createTime:"
				+ createTime + ", updateTime:" + updateTime + ", status:" + status;
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

	public int getAreaOrder() {
		return areaOrder;
	}

	public void setAreaOrder(int areaOrder) {
		this.areaOrder = areaOrder;
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
