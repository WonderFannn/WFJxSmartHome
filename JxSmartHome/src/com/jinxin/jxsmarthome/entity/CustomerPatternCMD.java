package com.jinxin.jxsmarthome.entity;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;

/**
 * 客户模式指令信息单元
 * @deprecated
 * @author JackeyZhang
 * @company 金鑫智慧
 */
@Table(name = "customer_pattern_cmd")
public class CustomerPatternCMD extends Base {
	@Id
	@Column(name = "id")
	private int id;
	@Column(name = "patternId")
	private int patternId;
	@Column(name = "cmdId")
	private int cmdId;
	@Column(name = "exeOrder")
	private int exeOrder;

	public CustomerPatternCMD() {

	}

	public CustomerPatternCMD(int id, int patternId, int cmdId, int exeOrder) {
		super();
		this.id = id;
		this.patternId = patternId;
		this.cmdId = cmdId;
		this.exeOrder = exeOrder;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPatternId() {
		return patternId;
	}

	public void setPatternId(int patternId) {
		this.patternId = patternId;
	}

	public int getCmdId() {
		return cmdId;
	}

	public void setCmdId(int cmdId) {
		this.cmdId = cmdId;
	}

	public int getExeOrder() {
		return exeOrder;
	}

	public void setExeOrder(int exeOrder) {
		this.exeOrder = exeOrder;
	}

}
