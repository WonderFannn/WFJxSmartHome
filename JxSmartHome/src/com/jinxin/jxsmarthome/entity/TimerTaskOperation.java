package com.jinxin.jxsmarthome.entity;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;
/**
 * 定时任务操作列表单元
 * @author YangJiJun
 * @company 金鑫智慧
 */
@Table(name = "timer_task_operation")
public class TimerTaskOperation extends Base{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "id")
	private int id;
	/**
	 * 任务ID
	 */
	@Column(name = "taskId")
	private int taskId;
	
	/**
	 * 模式ID
	 */
	@Column(name = "patternId")
	private int patternId;
	/**
	 * 操作类型	1=模式操作，2=设备，3=模式设备组合
	 */
	@Column(name = "operationType")
	private int operationType;
	@Column(name = "funId")
	private int funId;
	@Column(name = "whId")
	private String whId;
	/**
	 * 操作
	 */
	@Column(name = "operation")
	private String operation;
	@Column(name = "paraDesc")
	private String paraDesc;
	@Column(name = "updateTime")
	private String updateTime;
	public TimerTaskOperation(){
		
	}
	public TimerTaskOperation(int id, int taskId, int patternId,
			int operationType, int funId, String whId, String operation,
			String paraDesc, String updateTime) {
		super();
		this.id = id;
		this.taskId = taskId;
		this.patternId = patternId;
		this.operationType = operationType;
		this.funId = funId;
		this.whId = whId;
		this.operation = operation;
		this.paraDesc = paraDesc;
		this.updateTime = updateTime;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getTaskId() {
		return taskId;
	}
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
	public int getPatternId() {
		return patternId;
	}
	public void setPatternId(int patternId) {
		this.patternId = patternId;
	}
	public int getOperationType() {
		return operationType;
	}
	public void setOperationType(int operationType) {
		this.operationType = operationType;
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
