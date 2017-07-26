package com.jinxin.jxsmarthome.entity;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;

import android.provider.ContactsContract.Data;
/**
 * 设备功能明细列表单元
 * @author JackeyZhang
 * @company 金鑫智慧
 */
@Table(name = "whproductfun_uninfrared")
public class WHproductUnfrared extends Base{
	/* 主键 */
	@Id
	@Column(name = "id")
	private int id;
	/* 智能家居序列号 */
	@Column(name = "whId")
	private String whId;
	/* 关联的fundId */
	@Column(name = "fundId")
	private int fundId;
	/* 记录名称 */
	@Column(name = "recordName")
	private String recordName;
	/* 红外码 */
	@Column(name = "infraRedCode")
	private String infraRedCode;
	/* 创建时间  */
	@Column(name = "createTime")
	private int createTime;
	/* 更新时间  */
	@Column(name = "updateTime")
	private int updateTime;
	/* 序列编码  */
	@Column(name = "serCode")
	private Integer serCode;
	/* 是否启用  */
	@Column(name = "enable")
	private Integer enable;
	
	/**
	 * plutoSoundBoxControl(productFun,funDetail,"0A");
	 * productFun
	 */
	
	public WHproductUnfrared() {
		super();
	}
	public WHproductUnfrared(int id, String whId, int fundId, String recordName, String infraRedCode,
			int createTime, int updateTime, Integer serCode, Integer enable) {
		super();
		this.id = id;
		this.whId = whId;
		this.fundId = fundId;
		this.recordName = recordName;
		this.infraRedCode = infraRedCode;
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.serCode = serCode;
		this.enable = enable;
	}
	@Override
	public String toString() {
		return "WHproductUnfrared [id=" + id + ", whId=" + whId + ", fundId=" + fundId + ", recoorName=" + recordName
				+ ", infraRedCode=" + infraRedCode + ", createTime=" + createTime + ", updateTime=" + updateTime
				+ ", serCode=" + serCode + ", enable=" + enable + "]";
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getWhId() {
		return whId;
	}
	public void setWhId(String whId) {
		this.whId = whId;
	}
	public int getFundId() {
		return fundId;
	}
	public void setFundId(int fundId) {
		this.fundId = fundId;
	}
	public String getRecoorName() {
		return recordName;
	}
	public void setRecoorName(String recordName) {
		this.recordName = recordName;
	}
	public String getInfraRedCode() {
		return infraRedCode;
	}
	public void setInfraRedCode(String infraRedCode) {
		this.infraRedCode = infraRedCode;
	}
	public int getCreateTime() {
		return createTime;
	}
	public void setCreateTime(int createTime) {
		this.createTime = createTime;
	}
	public int getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(int updateTime) {
		this.updateTime = updateTime;
	}
	public Integer getSerCode() {
		return serCode;
	}
	public void setSerCode(Integer serCode) {
		this.serCode = serCode;
	}
	public Integer getEnable() {
		return enable;
	}
	public void setEnable(Integer enable) {
		this.enable = enable;
	}
	
	
	
	
	
}
