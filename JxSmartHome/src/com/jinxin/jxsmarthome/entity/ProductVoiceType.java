package com.jinxin.jxsmarthome.entity;

import java.io.Serializable;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;

/**
 * 语音类别
 * @author YangJiJun
 * @company 金鑫智慧
 */
@Table(name = "product_voice_type")
public class ProductVoiceType extends Base implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "id")
	private int id;		// 记录主键
	@Column(name = "name")
	private String name;
	@Column(name = "type")
	private int type;  //类型,1:模板,2:情景
	@Column(name = "describe")
	private String describe;
	@Column(name = "createUser")
	private String createUser;
	@Column(name = "createTime")
	private String createTime;
	@Column(name = "updateTime")
	private String updateTime;
	@Column(name = "ordinal")
	private int ordinal;
	@Column(name = "icon")
	private String icon;
	@Column(name = "status")
	private int status;     //状态：0 禁止，1 启用
	@Column(name = "category")
	private int category;  //类别：1 正文，2 正文+标题,3 正文+标题+摘要
	
	public ProductVoiceType() {
	}

	public ProductVoiceType(int id, String name, int type, String describe,
			String createUser, String createTime, String updateTime,
			int ordinal, String icon, int status, int category) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.describe = describe;
		this.createUser = createUser;
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.ordinal = ordinal;
		this.icon = icon;
		this.status = status;
		this.category = category;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public int getOrdinal() {
		return ordinal;
	}

	public void setOrdinal(int ordinal) {
		this.ordinal = ordinal;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}
}
