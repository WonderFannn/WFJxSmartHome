package com.jinxin.jxsmarthome.entity;

import java.io.Serializable;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;

public class WakeWord extends Base implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "id")
	private int id; 	// 记录主键
	@Column(name = "customerId")
	private String customerId;
	@Column(name = "wakeUpWord")
	private String wakeUpWord;
	@Column(name = "status")
	private String status;	
	@Column(name = "operateTime")
	private Double operateTime;
	
	public WakeWord() {
		super();
	}

	public WakeWord(int id, String customerId, String wakeUpWord, String status, Double operateTime) {
		super();
		this.id = id;
		this.customerId = customerId;
		this.wakeUpWord = wakeUpWord;
		this.status = status;
		this.operateTime = operateTime;
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

	public String getWakeUpWord() {
		return wakeUpWord;
	}

	public void setWakeUpWord(String wakeUpWord) {
		this.wakeUpWord = wakeUpWord;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Double getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(Double operateTime) {
		this.operateTime = operateTime;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	
}
