package com.jinxin.jxsmarthome.entity;

import java.io.Serializable;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;

/**
 * 意见反馈
 * 
 * @company 金鑫智慧
 */
@Table(name = "feedback")
public class Feedback extends Base implements Serializable{
	private static final long serialVersionUID = 3436916971429018068L;
	@Id
	@Column(name = "messageId")
	private int messageId; // 意见ID 主键
	@Column(name = "content")
	private String content; // 内容
	@Column(name = "reply")
	private String reply; // 回复人
	@Column(name = "creTime")
	private long creTime; // 新增时间
	@Column(name = "replyTime")
	private String replyTime; // 回复时间
	@Column(name = "result")
	private String result; // 处理结果
	@Column(name = "messageType")
	private int messageType; // 类型
	@Column(name = "imageUrl1")
	private String imageUrl1; // 图片1
	@Column(name = "imageUrl12")
	private String imageUrl2; // 图片2
	@Column(name = "imageUrl3")
	private String imageUrl3; // 图片3
	@Column(name = "imageUrl4")
	private String imageUrl4; // 图片4

	public Feedback() {
		super();
	}

	public Feedback(int messageId, String content, String reply, String replyTime, String result, int messageType, String imageUrl1, String imageUrl2,
			String imageUrl3, String imageUrl4) {
		super();
		this.messageId = messageId;
		this.content = content;
		this.reply = reply;
		this.replyTime = replyTime;
		this.result = result;
		this.messageType = messageType;
		this.imageUrl1 = imageUrl1;
		this.imageUrl2 = imageUrl2;
		this.imageUrl3 = imageUrl3;
		this.imageUrl4 = imageUrl4;
	}

	@Override
	public String toString() {
		return "Feedback [messageId=" + messageId + ", content=" + content + ", reply=" + reply + ", creTime=" + creTime + ", replyTime=" + replyTime
				+ ", result=" + result + ", messageType=" + messageType + ", imageUrl1=" + imageUrl1 + ", imageUrl2=" + imageUrl2 + ", imageUrl3=" + imageUrl3
				+ ", imageUrl4=" + imageUrl4 + "]";
	}


	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	public String getReplyTime() {
		return replyTime;
	}

	public void setReplyTime(String replyTime) {
		this.replyTime = replyTime;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public int getMessageType() {
		return messageType;
	}

	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}

	public String getImageUrl1() {
		return imageUrl1;
	}

	public void setImageUrl1(String imageUrl1) {
		this.imageUrl1 = imageUrl1;
	}

	public String getImageUrl2() {
		return imageUrl2;
	}

	public void setImageUrl2(String imageUrl2) {
		this.imageUrl2 = imageUrl2;
	}

	public String getImageUrl3() {
		return imageUrl3;
	}

	public void setImageUrl3(String imageUrl3) {
		this.imageUrl3 = imageUrl3;
	}

	public String getImageUrl4() {
		return imageUrl4;
	}

	public void setImageUrl4(String imageUrl4) {
		this.imageUrl4 = imageUrl4;
	}

	public long getCreTime() {
		return creTime;
	}

	public void setCreTime(long creTime) {
		this.creTime = creTime;
	}



	

}
