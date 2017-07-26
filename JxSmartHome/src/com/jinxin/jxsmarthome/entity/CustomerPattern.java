package com.jinxin.jxsmarthome.entity;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;
/**
 * 客户模式列表单元
 * @author JackeyZhang
 * @company 金鑫智慧
 */
@Table(name = "customer_pattern")
public class CustomerPattern extends Base{
	private static final long serialVersionUID = 334516112462008259L;
	@Id
	@Column(name = "patternId")
	private int patternId;
	@Column(name = "paternName")
	private String paternName;
	@Column(name = "paternType")
	private String paternType;
	@Column(name = "customerId")
	private String customerId;
	@Column(name = "createUser")
	private String createUser;
	@Column(name = "createTime")
	private String createTime;
	@Column(name = "status")
	private String status;
	@Column(name = "icon")
	private String icon;
	@Column(name = "ttsStart")
	private String ttsStart;
	@Column(name = "ttsEnd")
	private String ttsEnd;
	/**
	 * 点击次数
	 */
	@Column(name = "clickCount")
	private Integer clickCount;

	@Column(name = "patternGroupId")
	private Integer patternGroupId;
	/**
	 * 置顶
	 */
	@Column(name = "stayTop")
	private Integer stayTop;
	/**
	 * 备注
	 */
	@Column(name = "memo")
	private String memo;
	
	public CustomerPattern(){
		
	}

	public CustomerPattern(int patternId, String paternName, String paternType,
			String customerId, String createUser, String createTime,
			String status, String icon, String ttsStart, String ttsEnd,
			Integer patternGroupId, String memo) {
		super();
		this.patternId = patternId;
		this.paternName = paternName;
		this.paternType = paternType;
		this.customerId = customerId;
		this.createUser = createUser;
		this.createTime = createTime;
		this.status = status;
		this.icon = icon;
		this.ttsStart = ttsStart;
		this.ttsEnd = ttsEnd;
		this.patternGroupId = patternGroupId;
		this.memo = memo;
	}
	
	@Override
	public String toString() {
		return "CustomerPattern [patternId=" + patternId + ", paternName="
				+ paternName + ", paternType=" + paternType + ", customerId="
				+ customerId + ", createUser=" + createUser + ", createTime="
				+ createTime + ", status=" + status + ", icon=" + icon
				+ ", ttsStart=" + ttsStart + ", ttsEnd=" + ttsEnd
				+ ", clickCount=" + clickCount + ", patternGroupId="
				+ patternGroupId + ", stayTop=" + stayTop + ", memo=" + memo
				+ "]";
	}

	public int getPatternId() {
		return patternId;
	}

	public void setPatternId(int patternId) {
		this.patternId = patternId;
	}

	public String getPaternName() {
		return paternName;
	}

	public void setPaternName(String paternName) {
		this.paternName = paternName;
	}

	public String getPaternType() {
		return paternType;
	}

	public void setPaternType(String paternType) {
		this.paternType = paternType;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getTtsStart() {
		return ttsStart;
	}

	public void setTtsStart(String ttsStart) {
		this.ttsStart = ttsStart;
	}

	public String getTtsEnd() {
		return ttsEnd;
	}

	public void setTtsEnd(String ttsEnd) {
		this.ttsEnd = ttsEnd;
	}

	public Integer getClickCount() {
		return clickCount;
	}

	public void setClickCount(Integer clickCount) {
		this.clickCount = clickCount;
	}

	public Integer getPatternGroupId() {
		return patternGroupId;
	}

	public void setPatternGroupId(Integer patternGroupId) {
		this.patternGroupId = patternGroupId;
	}

	public Integer getStayTop() {
		return stayTop;
	}

	public void setStayTop(Integer stayTop) {
		this.stayTop = stayTop;
	}
	
	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}
	
}
