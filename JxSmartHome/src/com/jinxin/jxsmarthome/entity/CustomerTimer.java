package com.jinxin.jxsmarthome.entity;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;
/**
 * 客户定时模式列表单元
 * @author YangJiJun
 * @company 金鑫智慧
 */
@Table(name = "customer_timer_task")
public class CustomerTimer extends Base{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 定时任务ID
	 */
	@Id
	@Column(name = "taskId")
	private int taskId;
	
	/**
	 * 任务名称
	 */
	@Column(name="taskName")
	private String taskName;
	/**
	 * 用户ID
	 */
	@Column(name = "customerId")
	private String customerId;
	/**
	 * 任务周期
	 */
	@Column(name = "period")
	private String period;
	/**
	 * 任务表达式
	 */
	@Column(name = "cornExpression")
	private String cornExpression;
	/**
	 * 任务状态 	0=已删除，1=有效，2=禁用
	 */
	@Column(name = "status")
	private int status;
	/**
	 * 备注
	 */
	@Column(name = "remark")
	private String remark;
	
	@Column(name = "updateTime")
	private String updateTime;
	
	public CustomerTimer(){
		
	}

	public CustomerTimer(int taskId, String customerId, String taskName,String period,
			String cornExpression, int status,String updateTime) {
		super();
		this.taskId = taskId;
		this.customerId = customerId;
		this.taskName = taskName;
		this.period = period;
		this.cornExpression = cornExpression;
		this.status = status;
		this.updateTime = updateTime;
	}

	@Override
	public String toString() {
		return "CustomerTimer [taskId=" + taskId + ", taskName=" + taskName
				+ ", customerId=" + customerId + ", period=" + period
				+ ", cornExpression=" + cornExpression + ", status=" + status
				+ ", remark=" + remark + ", updateTime=" + updateTime + "]";
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCornExpression() {
		return cornExpression;
	}

	public void setCornExpression(String cornExpression) {
		this.cornExpression = cornExpression;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
}
