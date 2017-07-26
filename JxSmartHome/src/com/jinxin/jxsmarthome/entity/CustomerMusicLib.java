package com.jinxin.jxsmarthome.entity;

import java.io.Serializable;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;

/**
 * 用户收藏歌曲信息
 * @company 金鑫智慧
 */
@Table(name = "customer_music_lib")
public class CustomerMusicLib extends Base implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	private int id;
	@Column(name = "userId")
	private String userId;
	@Column(name = "musicId")
	private int musicId;
	@Column(name = "singer")
	private String singer;
	@Column(name = "musicName")
	private String musicName;
	@Column(name = "storeTime")
	private String storeTime;
	@Column(name = "enjoy")
	private int enjoy;//喜欢 1=是，0=否

	public CustomerMusicLib() {
	}

	public CustomerMusicLib(int id, String userId, int musicId,
			String singer, String musicName, String storeTime, int enjoy) {
		super();
		this.id = id;
		this.userId = userId;
		this.musicId = musicId;
		this.singer = singer;
		this.musicName = musicName;
		this.storeTime = storeTime;
		this.enjoy = enjoy;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getMusicId() {
		return musicId;
	}

	public void setMusicId(int musicId) {
		this.musicId = musicId;
	}

	public String getSinger() {
		return singer;
	}

	public void setSinger(String singer) {
		this.singer = singer;
	}

	public String getMusicName() {
		return musicName;
	}

	public void setMusicName(String musicName) {
		this.musicName = musicName;
	}

	public String getStoreTime() {
		return storeTime;
	}

	public void setStoreTime(String storeTime) {
		this.storeTime = storeTime;
	}

	public int getEnjoy() {
		return enjoy;
	}

	public void setEnjoy(int enjoy) {
		this.enjoy = enjoy;
	}
}
