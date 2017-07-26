package com.jinxin.jxsmarthome.entity;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;

@Table(name = "product_conntection")
public class ProductConnection extends Base {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id")
	private int id;					// 记录主键
	@Column(name = "funId")
	private int funId;				// 关联设备id
	@Column(name = "whId")
	private String whId;			// 关联设备whId
	@Column(name = "status")	
	private String status;			// 1—开启  0—关闭
	@Column(name = "bindType")
	private String bindType;	
	@Column(name = "bindFunId")    //被关联设备funId
	private int bindFunId;	
	@Column(name = "bindStatus")
	private String bindStatus;	
	@Column(name = "operation")
	private String operation;	
	@Column(name = "paraDesc")
	private String paraDesc;	
	@Column(name = "updateTime")
	private String updateTime;	
	@Column(name = "bindWhId")
	private String bindWhId;	
	@Column(name = "timeInterval")
	private String timeInterval;	
	@Column(name = "isvalid")
	private String isvalid;	//启用|禁用
	
	private String funName;
	private String bindFunName;
	private String icon;
	private String bindIcon;
	private String funType;
	private String bindFunType;
	
	public ProductConnection() {
		
	}

	public ProductConnection(int id, int funId, String whId, String status, String bindType,
			int bindFunId, String bindStatus, String operation,
			String paraDesc, String updateTime, String bindWhId,
			String timeInterval, String isvalid) {
		super();
		this.id = id;
		this.funId = funId;
		this.whId = whId;
		this.status = status;
		this.bindType = bindType;
		this.bindFunId = bindFunId;
		this.bindStatus = bindStatus;
		this.operation = operation;
		this.paraDesc = paraDesc;
		this.updateTime = updateTime;
		this.bindWhId = bindWhId;
		this.timeInterval = timeInterval;
		this.isvalid = isvalid;
	}

	@Override
	public String toString() {
		return "ProductConnection [id=" + id + ", funId=" + funId + ", whId=" + whId +", status="
				+ status + ", bindType=" + bindType + ", bindFunId="
				+ bindFunId + ", bindStatus=" + bindStatus + ", operation="
				+ operation + ", paraDesc=" + paraDesc + ", updateTime="
				+ updateTime + ", bindWhId=" + bindWhId + ", timeInterval="
				+ timeInterval + ", isvalid=" + isvalid + ", funName="
				+ funName + ", bindFunName=" + bindFunName + ", icon=" + icon
				+ ", bindIcon=" + bindIcon + ", funType=" + funType
				+ ", bindFunType=" + bindFunType + "]";
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBindType() {
		return bindType;
	}

	public void setBindType(String bindType) {
		this.bindType = bindType;
	}

	public int getBindFunId() {
		return bindFunId;
	}

	public void setBindFunId(int bindFunId) {
		this.bindFunId = bindFunId;
	}

	public String getBindStatus() {
		return bindStatus;
	}

	public void setBindStatus(String bindStatus) {
		this.bindStatus = bindStatus;
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

	public String getBindWhId() {
		return bindWhId;
	}

	public void setBindWhId(String bindWhId) {
		this.bindWhId = bindWhId;
	}

	public String getTimeInterval() {
		return timeInterval;
	}

	public void setTimeInterval(String timeInterval) {
		this.timeInterval = timeInterval;
	}

	public String getIsvalid() {
		return isvalid;
	}

	public void setIsvalid(String isvalid) {
		this.isvalid = isvalid;
	}

	public String getFunName() {
		return funName;
	}

	public void setFunName(String funName) {
		this.funName = funName;
	}

	public String getBindFunName() {
		return bindFunName;
	}

	public void setBindFunName(String bindFunName) {
		this.bindFunName = bindFunName;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getBindIcon() {
		return bindIcon;
	}

	public void setBindIcon(String bindIcon) {
		this.bindIcon = bindIcon;
	}

	public String getFunType() {
		return funType;
	}

	public void setFunType(String funType) {
		this.funType = funType;
	}

	public String getBindFunType() {
		return bindFunType;
	}

	public void setBindFunType(String bindFunType) {
		this.bindFunType = bindFunType;
	}	
	
}
