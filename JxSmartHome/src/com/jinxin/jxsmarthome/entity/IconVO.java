package com.jinxin.jxsmarthome.entity;

public class IconVO extends Base {

	public IconVO(String iconPath, boolean isSelected) {
		super();
		this.iconPath = iconPath;
		this.isSelected = isSelected;
	}
	public String getIconPath() {
		return iconPath;
	}
	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	private String iconPath;
	private boolean isSelected;
	
}
