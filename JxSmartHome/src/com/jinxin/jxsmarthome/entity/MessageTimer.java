package com.jinxin.jxsmarthome.entity;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;

/**
 * 消息定时接收
 * @author YangJiJun
 * @company 金鑫智慧
 */
@Table(name = "message_timer")
public class MessageTimer extends Base {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id")
	private int id;
	@Column(name = "timeRange")
	private String timeRange;
	
	public MessageTimer() {
	}
	
	public MessageTimer(int id, String timeRange) {
		super();
		this.id = id;
		this.timeRange = timeRange;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTimeRange() {
		return timeRange;
	}
	public void setTimeRange(String timeRange) {
		this.timeRange = timeRange;
	}
	
}
