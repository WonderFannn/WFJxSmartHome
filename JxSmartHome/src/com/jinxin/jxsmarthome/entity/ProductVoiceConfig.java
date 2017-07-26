package com.jinxin.jxsmarthome.entity;

import java.io.Serializable;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;

/**
 * 语音配置
 * @author YangJiJun
 * @company 金鑫智慧
 */
@Table(name = "product_voice_config")
public class ProductVoiceConfig extends Base implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "id")
	private int id; 	// 记录主键
	@Column(name = "typeId")
	private int typeId;
	@Column(name = "content")
	private String content;
	@Column(name = "updateTime")
	private String updateTime;	
	@Column(name = "title")
	private String title;
	@Column(name = "summary")
	private String summary;  //摘要

	public ProductVoiceConfig() {
	}

	public ProductVoiceConfig(int id, int typeId, String content,
			String updateTime, String title, String summary) {
		super();
		this.id = id;
		this.typeId = typeId;
		this.content = content;
		this.updateTime = updateTime;
		this.title = title;
		this.summary = summary;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
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
	
}
