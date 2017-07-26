package com.jinxin.jxsmarthome.entity;

import java.io.Serializable;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;

/**
 * 云音乐信息
 * @company 金鑫智慧
 */
@Table(name = "music_lib")
public class MusicLib extends Base implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	private int id;// 主键ID
	@Column(name = "musicId")
	private int musicId;// 主键ID
	@Column(name = "name")
	private String name;
	@Column(name = "singer")
	private String singer;
	@Column(name = "format")
	private String format;//格式  1=MP3，2=WMA，3=WAV ，4=MOD，5=RA ，6=CD
	@Column(name = "ablum")
	private String ablum;//所属专辑
	@Column(name = "pubTime")
	private String pubTime;//发布时间
	@Column(name = "hot")
	private Integer hot;//是否热门歌曲 1= 是，0=否
	@Column(name = "netUrl")
	private String netUrl;//歌曲网络地址
	@Column(name = "lrcUrl")
	private String lrcUrl;//歌词网络地址
	
	/**
	 * 歌手地区 1=内地，2=港台，3=欧美，4=韩国，5=日本
	 */
	@Column(name = "area")
	private String area;
	/**
	 *  音乐流派 1=流行，2=摇滚，3=DJ慢摇，4=舞曲，
	 *  5=轻音乐，6=R=乡村，8=爵士，9=电子， 10=拉丁，11=古典，12=民谣
	 */
	@Column(name = "genre")
	private String genre;
	/**
	 * 1=综艺热歌，2=影视热歌，3=经典好歌，4=中国风，5=K歌金曲，
	 * 6=儿歌童谣，7=网络热歌， 8=小清新，9=情歌对唱，10=游戏音乐
	 */
	@Column(name = "category")
	private String category;
	/**
	 * 1=休闲，2=聚会，3=工作，4=开车，5=运动，6=校园，7=写作业，
	 * 8=睡觉，9=散步，10=约会， 11=旅行，12=做家务，13=下午茶，
	 * 14=夜店嗨歌15=公共交通，16=胎教，17=广场舞，18=减压
	 */
	@Column(name = "scence")
	private String scence;
	/**
	 * 1=伤感，2=思念，3=孤单，4=甜蜜，5=欢快，6=励志，7=叛逆，8=宣泄，9=期待，10=安静
	 */
	@Column(name = "feeling")
	private String feeling;
	@Column(name = "updateUser")
	private Integer updateUser; //更新人 默认值=1
	@Column(name = "updateTime")
	private Integer updateTime; //更新时间 默认值=10
	@Column(name = "isvalid")
	private Integer isvalid; //是否有效 1=有效，0=无效
	
	
	public MusicLib() {
	}

	public MusicLib(int id,String name, String singer, String format,
			String ablum, String pubTime, Integer hot,
			String area,String genre, String category, String scence, String feeling,
			Integer updateUser, Integer updateTime, Integer isvalid) {
		super();
		this.id = id;
		this.name = name;
		this.singer = singer;
		this.format = format;
		this.ablum = ablum;
		this.pubTime = pubTime;
		this.hot = hot;
		this.area = area;
		this.genre = genre;
		this.category = category;
		this.scence = scence;
		this.feeling = feeling;
		this.updateUser = updateUser;
		this.updateTime = updateTime;
		this.isvalid = isvalid;
	}


	public int getId() {
		return id;
	}

	public int getMusicId() {
		return musicId;
	}

	public void setMusicId(int musicId) {
		this.musicId = musicId;
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

	public String getSinger() {
		return singer;
	}

	public void setSinger(String singer) {
		this.singer = singer;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getAblum() {
		return ablum;
	}

	public void setAblum(String ablum) {
		this.ablum = ablum;
	}

	public String getPubTime() {
		return pubTime;
	}

	public void setPubTime(String pubTime) {
		this.pubTime = pubTime;
	}

	public Integer getHot() {
		return hot;
	}

	public void setHot(Integer hot) {
		this.hot = hot;
	}

	public String getNetUrl() {
		return netUrl;
	}

	public void setNetUrl(String netUrl) {
		this.netUrl = netUrl;
	}

	public String getLrcUrl() {
		return lrcUrl;
	}

	public void setLrcUrl(String lrcUrl) {
		this.lrcUrl = lrcUrl;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}


	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getScence() {
		return scence;
	}

	public void setScence(String scence) {
		this.scence = scence;
	}

	public String getFeeling() {
		return feeling;
	}

	public void setFeeling(String feeling) {
		this.feeling = feeling;
	}

	public Integer getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(Integer updateUser) {
		this.updateUser = updateUser;
	}

	public Integer getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Integer updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getIsvalid() {
		return isvalid;
	}

	public void setIsvalid(Integer isvalid) {
		this.isvalid = isvalid;
	}
	
}
