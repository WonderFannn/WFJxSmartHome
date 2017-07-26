package com.jinxin.jxsmarthome.entity;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;
/**
 * 设备功能明细列表单元
 * @author JackeyZhang
 * @company 金鑫智慧
 */
@Table(name = "fun_detail_config")
public class FunDetailConfig extends Base{
	@Id
	
	@Column(name = "id")
	private int id;
	@Column(name = "whId")
	private String whId;
	@Column(name = "funType")
	private String funType;
	@Column(name = "params")
	private String params;
	@Column(name = "alias")
	private String alias;
	@Column(name = "updateTime")
	private String updateTime;
	
	public FunDetailConfig() {

	}
	
	@Override
	public String toString() {
		return "id:" + id + "whId:" + whId + ",funType:" + funType + ",params:" + params + ",updateTime:" + updateTime;
	}
	
	public FunDetailConfig(String funType, String whId, String params,
			String alias, String updateTime) {
		super();
		this.funType = funType;
		this.whId = whId;
		this.params = params;
		this.alias = alias;
		this.updateTime = updateTime;
	}

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFunType() {
		return funType;
	}

	public void setFunType(String funType) {
		this.funType = funType;
	}

	public String getWhId() {
		return whId;
	}

	public void setWhId(String whId) {
		this.whId = whId;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

}
