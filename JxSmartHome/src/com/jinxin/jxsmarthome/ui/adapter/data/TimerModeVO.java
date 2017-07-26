package com.jinxin.jxsmarthome.ui.adapter.data;

import java.io.Serializable;

import com.jinxin.jxsmarthome.entity.CustomerPattern;

public class TimerModeVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean isSelected = false;
	private CustomerPattern cPattern = null;
	
	public TimerModeVO(CustomerPattern cPattern) {
		this(false,cPattern);
	}
	public TimerModeVO(boolean isSelected,CustomerPattern cPattern) {
		this.isSelected = isSelected;
		this.cPattern = cPattern;
	}
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	public CustomerPattern getcPattern() {
		return cPattern;
	}
	public void setcPattern(CustomerPattern cPattern) {
		this.cPattern = cPattern;
	}
	
}
