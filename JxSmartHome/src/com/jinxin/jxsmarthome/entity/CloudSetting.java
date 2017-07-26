package com.jinxin.jxsmarthome.entity;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;

/**
 * 云设置的VO类
 * @author  TangLong
 * @company 金鑫智慧
 */
@Table(name="cloud_setting")
public class CloudSetting extends Base {
	private static final long serialVersionUID = -7864502132866990283L;
	
	@Id
	@Column(name = "id")				
	private int id;						// id，自动增长
	@Column(name = "server_id")
	private String server_id;			// 平台端的id
	@Column(name="customer_id")
	private String customerId;			// 用户id
	@Column(name="category")
	private String category;			// 设备类型
	@Column(name = "items")
	private String items;				// 设置项
	@Column(name = "params")
	private String params;				// 设置项的参数
	@Column(name = "update_time")
	private String update_time;			// 更新时间
	@Column(name = "create_time")
	private String create_time;			// 建立时间
	
	public CloudSetting() {
		
	}
	
	public CloudSetting(String server_id, String customerId, String category, String items, String params,
			String update_time, String create_time) {
		this(-1, server_id, customerId, category, items, params, update_time, create_time);
	}
	
	public CloudSetting(int id, String server_id, String customerId, String category, String items, String params,
			String update_time, String create_time) {
		super();
		this.id = id;
		this.server_id = server_id;
		this.customerId = customerId;
		this.category = category;
		this.items = items;
		this.params = params;
		this.update_time = update_time;
		this.create_time = create_time;
	}
	
	@Override
	public boolean equals(Object object) {
		if(this == object) {
			return true;
		}else if(object instanceof CloudSetting) {
			final CloudSetting other = (CloudSetting)object;
			return this.category.equals(other.category) && this.items.equals(other.category) && this.params.equals(other.params);
		}else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return "CloudSetting [id=" + id + ", server_id=" + server_id
				+ ", customerId=" + customerId + ", category=" + category
				+ ", items=" + items + ", params=" + params + ", update_time="
				+ update_time + ", create_time=" + create_time + "]";
	}

	//////////////////////////////////
	// getters and setters
	/////////////////////////////////
	public int getId() {
		return id;
	}
	public String getServer_id() {
		return server_id;
	}
	public void setServer_id(String server_id) {
		this.server_id = server_id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getItems() {
		return items;
	}
	public void setItems(String items) {
		this.items = items;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	public String getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

}
