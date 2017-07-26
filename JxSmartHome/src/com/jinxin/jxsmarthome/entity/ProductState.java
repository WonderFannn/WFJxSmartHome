package com.jinxin.jxsmarthome.entity;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;

/**
 * 设备状态获取
 * @author YangJiJun
 * @company 金鑫智慧
 */

@Table(name = "product_state")
public class ProductState {
	@Id
	@Column(name = "funId")
	private int funId;
//	@Column(name = "funType")
//	private String funType;
	@Column(name = "state")
	private String state;
	
	public ProductState() {
	}

	public ProductState(int funId, String state) {
		super();
		this.funId = funId;
//		this.funType = funType;
		this.state = state;
	}
	
	@Override
	public String toString() {
		return "{" + "funId" + funId + ",state:" + state + "}";
	}

	public int getFunId() {
		return funId;
	}
	public void setFunId(int funId) {
		this.funId = funId;
	}

	public String getState() {
		return state == null ? "" : state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
}
