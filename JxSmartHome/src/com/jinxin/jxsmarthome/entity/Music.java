package com.jinxin.jxsmarthome.entity;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;

/**
 * 歌曲信息
 * @author  Tanglong
 * @company 金鑫智慧
 */
@Table(name = "music")
public class Music extends Base {
	private static final long serialVersionUID = 903596812353354193L;
	
	@Id
	@Column(name = "id")
	private int id;					// ID
	@Column(name="title")
	private String title;			// 名称
	@Column(name="singer")
	private String singer;			// 歌手
	@Column(name="time")
	private String time;			// 时长
	@Column(name="source")
	private String source;  		// 来源
	@Column(name="no")
	private Integer no;					// 播放序号
	@Column(name="iyric")
	private String iyric;			// 歌词
	@Column(name="createTime")
	private String createTime;		// 创建时间
	
	public Music() {
		
	}
	
	public Music(String title, String singer, String time, String source, Integer no, String iyric, 
			String createTime) {
		this.title = title;
		this.singer = singer;
		this.time = time;
		this.source = source;
		this.no = no;
		this.iyric = iyric;
		this.createTime = createTime;
	}
	
	@Override
	public String toString() {
		return "{id:" + id + ", title:" + title + ", singer:" + singer + ", time:" + time + "}";
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSinger() {
		return singer;
	}
	public void setSinger(String singer) {
		this.singer = singer;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Integer getNo() {
		return no;
	}

	public void setNo(Integer no) {
		this.no = no;
	}

	public String getIyric() {
		return iyric;
	}

	public void setIyric(String iyric) {
		this.iyric = iyric;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
}
