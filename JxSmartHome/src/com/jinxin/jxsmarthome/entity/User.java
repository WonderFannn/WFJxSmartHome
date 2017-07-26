package com.jinxin.jxsmarthome.entity;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;
/**
 * 用户信息单元
 * @author JackeyZhang
 * @company 金鑫智慧
 */
@Table(name = "user")
public class User extends Base {

	@Id
	@Column(name = "ui_id")
	private int ui_id;
	@Column(name = "ssc_id")
	private int ssc_id;
	@Column(name = "sys_uid")
	private int sys_uid;
	@Column(name = "avatar")
	private String avatar;
	@Column(name = "login_name")
	private String login_name;
	@Column(name = "nick_name")
	private String nick_name;
	@Column(name = "imei_no")
	private String imei_no;
	@Column(name = "phone_name")
	private String phone_name;
	@Column(name = "last_logintime")
	private String last_logintime;
	@Column(name = "province")
	private int province;
	@Column(name = "city")
	private int city;
	@Column(name = "county")
	private int county;
	@Column(name = "phone_no")
	private String phone_no;
	@Column(name = "qq")
	private String qq;
	@Column(name = "email")
	private String email;
	@Column(name = "address")
	private String address;
	@Column(name = "name")
	private String name;
	@Column(name = "full_zjm")
	private String full_zjm;
	@Column(name = "zjm")
	private String zjm;
	@Column(name = "commpany")
	private String commpany;
	@Column(name = "score")
	private Double score;
	@Column(name = "gold")
	private Double gold;
	@Column(name = "balance")
	private Double balance;
	@Column(name = "level")
	private Double level;
	@Column(name = "create_time")
	private String create_time;
	@Column(name = "update_time")
	private String update_time;
	@Column(name = "update_uid")
	private String update_uid;
	@Column(name = "memo")
	private String memo;
	@Column(name = "status")
	private int status;
	public int getUi_id() {
		return ui_id;
	}
	public void setUi_id(int ui_id) {
		this.ui_id = ui_id;
	}
	public int getSsc_id() {
		return ssc_id;
	}
	public void setSsc_id(int ssc_id) {
		this.ssc_id = ssc_id;
	}
	public int getSys_uid() {
		return sys_uid;
	}
	public void setSys_uid(int sys_uid) {
		this.sys_uid = sys_uid;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getLogin_name() {
		return login_name;
	}
	public void setLogin_name(String login_name) {
		this.login_name = login_name;
	}
	public String getNick_name() {
		return nick_name;
	}
	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}
	public String getImei_no() {
		return imei_no;
	}
	public void setImei_no(String imei_no) {
		this.imei_no = imei_no;
	}
	public String getPhone_name() {
		return phone_name;
	}
	public void setPhone_name(String phone_name) {
		this.phone_name = phone_name;
	}
	public String getLast_logintime() {
		return last_logintime;
	}
	public void setLast_logintime(String last_logintime) {
		this.last_logintime = last_logintime;
	}
	public int getProvince() {
		return province;
	}
	public void setProvince(int province) {
		this.province = province;
	}
	public int getCity() {
		return city;
	}
	public void setCity(int city) {
		this.city = city;
	}
	public int getCounty() {
		return county;
	}
	public void setCounty(int county) {
		this.county = county;
	}
	public String getPhone_no() {
		return phone_no;
	}
	public void setPhone_no(String phone_no) {
		this.phone_no = phone_no;
	}
	public String getQq() {
		return qq;
	}
	public void setQq(String qq) {
		this.qq = qq;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFull_zjm() {
		return full_zjm;
	}
	public void setFull_zjm(String full_zjm) {
		this.full_zjm = full_zjm;
	}
	public String getZjm() {
		return zjm;
	}
	public void setZjm(String zjm) {
		this.zjm = zjm;
	}
	public String getCommpany() {
		return commpany;
	}
	public void setCommpany(String commpany) {
		this.commpany = commpany;
	}
	public Double getScore() {
		return score;
	}
	public void setScore(Double score) {
		this.score = score;
	}
	public Double getGold() {
		return gold;
	}
	public void setGold(Double gold) {
		this.gold = gold;
	}
	public Double getBalance() {
		return balance;
	}
	public void setBalance(Double balance) {
		this.balance = balance;
	}
	public Double getLevel() {
		return level;
	}
	public void setLevel(Double level) {
		this.level = level;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public String getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}
	public String getUpdate_uid() {
		return update_uid;
	}
	public void setUpdate_uid(String update_uid) {
		this.update_uid = update_uid;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	
	
}
