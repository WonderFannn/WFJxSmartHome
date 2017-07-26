package com.jinxin.jxsmarthome.entity;

/**
 * 同步删除的数据的VO类
 * @author TangLong
 * @company 金鑫智慧
 */
public class CustomerDataSync extends Base {
	private static final long serialVersionUID = -3736414517460686556L;

	private Integer id;					// ID
	private String customerId;			// 用户id
	private String actionId;			// 执行删除的记录的id
	private String actionFieldName;		// 执行的数据库名称
	private String actionFieldType;		// 1=Integer，2=String
	private Integer action;				// 1=插入、0=删除、2=更新
	private String model;				// 需要操作的表名
	private String updateTime;			// 更新时间
	
	
	public CustomerDataSync() {}
	
	public CustomerDataSync(Integer id, String customerId, String actionId, String actionFieldName,
			String actionFieldType, Integer action, String model, String updateTime) {
		this.id = id;
		this.customerId = customerId;
		this.actionId = actionId;
		this.actionFieldName = actionFieldName;
		this.actionFieldType = actionFieldType;
		this.action = action;
		this.model = model;
		this.updateTime = updateTime;
	}
	
	@Override
	public String toString() {
		return "id:" + id + ",actionId:" + actionId + ",actionFieldName:" + actionFieldName
				+ ",actionFieldType" + actionFieldType + "action:" + action + ",model:" + model
				+ ",updateTime:" + updateTime;
	}
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getActionId() {
		return actionId;
	}
	public void setActionId(String actionId) {
		this.actionId = actionId;
	}
	public String getActionFieldName() {
		return actionFieldName;
	}
	public void setActionFieldName(String actionFieldName) {
		this.actionFieldName = actionFieldName;
	}
	public String getActionFieldType() {
		return actionFieldType;
	}
	public void setActionFieldType(String actionFieldType) {
		this.actionFieldType = actionFieldType;
	}
	public Integer getAction() {
		return action;
	}
	public void setAction(Integer action) {
		this.action = action;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
}
