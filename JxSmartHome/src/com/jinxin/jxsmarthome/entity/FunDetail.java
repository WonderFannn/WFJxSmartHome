package com.jinxin.jxsmarthome.entity;

import android.R.integer;

import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;

/**
 * 设备功能明细列表单元
 * 
 * @author JackeyZhang
 * @company 金鑫智慧
 */
@Table(name = "fun_detail")
public class FunDetail extends Base {
	private static final long serialVersionUID = 6983165498125932815L;
	@Id
	@Column(name = "id")
	private int id;
	@Column(name = "funType")
	private String funType;
	@Column(name = "funName")
	private String funName;
	@Column(name = "customerId")
	private String customerId;
	@Column(name = "code")
	private String code;
	@Column(name = "icon")
	private String icon;
	@Column(name = "updateTime")
	private String updateTime;
	@Column(name = "shortcuts")
	private String shortcuts;
	@Column(name = "shortCutOperation")
	private String shortCutOperation;

	private String shortcutOpen;
	private String shortcutClose;
	@Column(name = "joinPattern")
	private Integer joinPattern;

	public FunDetail() {

	}

	public FunDetail(int id, String funType, String funName, String customerId,
			String code, String icon, String updateTime, String shortcuts,
			String shortCutOperation,Integer joinPattern) {
		super();
		this.id = id;
		this.funType = funType;
		this.funName = funName;
		this.customerId = customerId;
		this.code = code;
		this.icon = icon;
		this.updateTime = updateTime;
		this.shortcuts = shortcuts;
		this.shortCutOperation = shortCutOperation;
		this.joinPattern = joinPattern;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFunType() {
		return funType;
	}

	public void setFunType(String funType) {
		this.funType = funType;
	}

	public String getFunName() {
		return funName;
	}

	public void setFunName(String funName) {
		this.funName = funName;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getShortcuts() {
		return shortcuts;
	}

	public void setShortcuts(String shortcuts) {
		this.shortcuts = shortcuts;
	}

	public String getShortcutOpen() {
		// 增加校验
		if(CommUtil.isEmpty(shortCutOperation) || getSubStringCountAtString(shortCutOperation, "\"") < 2) {
			return null;
		}

		shortcutOpen = shortCutOperation.substring(
				(shortCutOperation.indexOf("\"", 0) + 1), 
				shortCutOperation.indexOf("\"", shortCutOperation.indexOf("\"") + 1));
		
		return shortcutOpen;
	}

	public void setShortcutOpen(String shortcutOpen) {
		this.shortcutOpen = shortcutOpen;
	}

	public String getShortcutClose() {
		shortcutClose = shortCutOperation.substring((shortCutOperation.indexOf(
				"\"", shortCutOperation.indexOf(",")) + 1), shortCutOperation
				.indexOf("\"", shortCutOperation.lastIndexOf("\"")));
		return shortcutClose;
	}

	public void setShortcutClose(String shortcutClose) {
		this.shortcutClose = shortcutClose;
	}

	public String getShortCutOperation() {
		return shortCutOperation;
	}

	public void setShortCutOperation(String shortCutOperation) {
		this.shortCutOperation = shortCutOperation;
	}
	
	public Integer getJoinPattern() {
		return joinPattern;
	}

	public void setJoinPattern(Integer joinPattern) {
		this.joinPattern = joinPattern;
	}

	private int getSubStringCountAtString(String str, String sub) {
		if(str == null || sub == null) {
			throw new IllegalArgumentException("paramters str or ch cannot be null");
		}
		int count = 0;
		
		for(int i = 0; i < str.length(); i++) {
			if(str.indexOf(sub) != -1) {
				count++;
			}
		}
		
		return count;
	}

	@Override
	public String toString() {
		return "FunDetail [id=" + id + ", funType=" + funType + ", funName=" + funName + ", customerId=" + customerId
				+ ", code=" + code + ", icon=" + icon + ", updateTime=" + updateTime + ", shortcuts=" + shortcuts
				+ ", shortCutOperation=" + shortCutOperation + ", shortcutOpen=" + shortcutOpen + ", shortcutClose="
				+ shortcutClose + ", joinPattern=" + joinPattern + "]";
	}

	
	
	

}
