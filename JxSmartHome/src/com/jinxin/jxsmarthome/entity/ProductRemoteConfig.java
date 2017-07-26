package com.jinxin.jxsmarthome.entity;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;

/**
 * 遥控配置信息
 * @author TangLong
 * @company 金鑫智慧
 */
@Table(name = "product_remote_config")
public class ProductRemoteConfig {
	@Id
	@Column(name = "id")
	private int id; // 主键
	@Column(name = "name")
	private String name; // 名称
	@Column(name = "code")
	private String code; // 码值
	@Column(name = "type")
	private String type; // 类型：1:电视,2:空调,3:机顶盒
	@Column(name = "updateTime")
	private String updateTime; // 类型,1:电视,2:空调,3:机顶盒
	
	public ProductRemoteConfig() {
	}

	public ProductRemoteConfig(int id, String name, String code, String type,
			String updateTime) {
		super();
		this.id = id;
		this.name = name;
		this.code = code;
		this.type = type;
		this.updateTime = updateTime;
	}

	@Override
	public String toString() {
		return "ProductRemoteConfig [id=" + id + ", name=" + name + ", code="
				+ code + ", type=" + type + ", updateTime=" + updateTime + "]";
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
}
