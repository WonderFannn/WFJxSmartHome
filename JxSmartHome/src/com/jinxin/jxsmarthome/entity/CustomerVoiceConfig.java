package com.jinxin.jxsmarthome.entity;

import java.io.Serializable;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;

/**
 * 自定义定时语音任务
 * @author YangJiJun
 * @company 金鑫智慧
 */
@Table(name = "costomer_voice_config")
public class CustomerVoiceConfig extends Base implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "id")
	private int id; 	//任务ID 记录主键
	@Column(name = "voiceId") //对应ProductVoiceConfig中的ID
	private int voiceId;
	@Column(name = "type") //语音分类 ID
	private int type;
	@Column(name = "name")
	private String name;
	@Column(name = "content")
	private String content;
	@Column(name = "title")
	private String title;
	@Column(name = "summary")
	private String summary;  //摘要
	@Column(name = "isOPen")
	private int isOPen;
	@Column(name = "period")
	private String period;
	/**
	 * 任务表达式
	 */
	@Column(name = "cornExpression")
	private String cornExpression; //JSON格式 {"time":"","type":"","period":""}
	
	public CustomerVoiceConfig() {
	}

	

	public CustomerVoiceConfig(int voiceId, String name, int type, String content,
			String title, String summary, int isOPen, String period, String cornExpression) {
		super();
		this.voiceId = voiceId;
		this.name = name;
		this.type = type;
		this.content = content;
		this.title = title;
		this.summary = summary;
		this.isOPen = isOPen;
		this.period = period;
		this.cornExpression = cornExpression;
	}

	@Override
	public String toString() {
		return "CustomerVoiceConfig [id=" + id + ", voiceId=" + voiceId
				+ ", type=" + type + ", name=" + name + ", content="
				+ content + ", title=" + title + ", summary=" + summary
				+ ", isOPen=" + isOPen + ", period=" + period
				+ ", cornExpression=" + cornExpression + "]";
	}



	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getVoiceId() {
		return voiceId;
	}

	public void setVoiceId(int voiceId) {
		this.voiceId = voiceId;
	}

	public int getType() {
		return type;
	}

	public void setType(int typeId) {
		this.type = typeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public int isOPen() {
		return isOPen;
	}

	public void setOPen(int isOPen) {
		this.isOPen = isOPen;
	}
	
	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getCornExpression() {
		return cornExpression;
	}
	
	public void setCornExpression(String cornExpression) {
		this.cornExpression = cornExpression;
	}
	
}
