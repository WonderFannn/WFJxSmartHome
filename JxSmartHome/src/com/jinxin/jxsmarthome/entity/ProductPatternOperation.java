package com.jinxin.jxsmarthome.entity;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;
/**
 * 产品模式操作列表单元
 * @author JackeyZhang
 * @company 金鑫智慧
 */
@Table(name = "product_pattern_operation")
public class ProductPatternOperation extends Base{
	@Id
	@Column(name = "id")
	private int id;
	@Column(name = "funId")
	private int funId;
	@Column(name = "whId")
	private String whId;
	@Column(name = "patternId")
	private int patternId;
	@Column(name = "operation")
	private String operation;
	@Column(name = "paraDesc")
	private String paraDesc;
	@Column(name = "updateTime")
	private String updateTime;
	public ProductPatternOperation(){
		
	}
	
	public ProductPatternOperation(int id, int funId, String whId,
			int patternId, String operation, String paraDesc, String updateTime) {
		super();
		this.id = id;
		this.funId = funId;
		this.whId = whId;
		this.patternId = patternId;
		this.operation = operation;
		this.paraDesc = paraDesc;
		this.updateTime = updateTime;
	}
	
	
	
	@Override
	public String toString() {
		return "ProductPatternOperation [id=" + id + ", funId=" + funId + ", whId=" + whId + ", patternId=" + patternId
				+ ", operation=" + operation + ", paraDesc=" + paraDesc + ", updateTime=" + updateTime + "]";
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getFunId() {
		return funId;
	}
	public void setFunId(int funId) {
		this.funId = funId;
	}
	public String getWhId() {
		return whId;
	}
	public void setWhId(String whId) {
		this.whId = whId;
	}
	public int getPatternId() {
		return patternId;
	}
	public void setPatternId(int patternId) {
		this.patternId = patternId;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getParaDesc() {
		return paraDesc;
	}
	public void setParaDesc(String paraDesc) {
		this.paraDesc = paraDesc;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	
	
}
