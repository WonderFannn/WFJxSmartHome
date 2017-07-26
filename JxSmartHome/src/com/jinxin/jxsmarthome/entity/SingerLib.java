package com.jinxin.jxsmarthome.entity;

import java.io.Serializable;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;

/**
 * 歌手信息
 * @company 金鑫智慧
 */
@Table(name = "singer_lib")
public class SingerLib extends Base implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id")
	private int id;
	@Column(name = "singer")
	private String singer;
	@Column(name = "sex")
	private int sex;
	@Column(name = "hot")
	private int hot;
	@Column(name = "area")
	private String area;
	@Column(name = "icon")
	private String icon;
	@Column(name = "memo")
	private String memo;
	@Column(name = "updateUser")
	private String updateUser;
	@Column(name = "updateTime")
	private String updateTime;
	@Column(name = "isvalid")
	private int isvalid;
	
	public SingerLib() {
	}

	public SingerLib(int id, String singer, int sex, int hot, String area,
			String icon, String memo, String updateUser, String updateTime,
			int isvalid) {
		super();
		this.id = id;
		this.singer = singer;
		this.sex = sex;
		this.hot = hot;
		this.area = area;
		this.icon = icon;
		this.memo = memo;
		this.updateUser = updateUser;
		this.updateTime = updateTime;
		this.isvalid = isvalid;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSinger() {
		return singer;
	}

	public void setSinger(String singer) {
		this.singer = singer;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public int getHot() {
		return hot;
	}

	public void setHot(int hot) {
		this.hot = hot;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public int getIsvalid() {
		return isvalid;
	}

	public void setIsvalid(int isvalid) {
		this.isvalid = isvalid;
	}
	
}
