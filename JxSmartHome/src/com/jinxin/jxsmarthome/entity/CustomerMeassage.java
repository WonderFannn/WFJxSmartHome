package com.jinxin.jxsmarthome.entity;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;


/**
 * 消息提醒
 * @author JackeyZhang
 * @company 金鑫智慧
 */
@Table(name = "customer_meassage")
public class CustomerMeassage extends Base {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id")
	private int id;
	@Column(name = "whId")
	private String whId;
	@Column(name = "message")//消息内容
	private String message;
	@Column(name = "time")
	private String time;
	@Column(name = "isReaded")
	private int isReaded;//已读标识 0:未读 ，1：已读
	
	public CustomerMeassage() {
	}

	public CustomerMeassage(int id, String whId, String message, String time, int isReaded) {
		super();
		this.id = id;
		this.whId = whId;
		this.message = message;
		this.time = time;
		this.isReaded = isReaded;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getWhId() {
		return whId;
	}

	public void setWhId(String whId) {
		this.whId = whId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getIsReaded() {
		return isReaded;
	}

	public void setIsReaded(int isReaded) {
		this.isReaded = isReaded;
	}

}
