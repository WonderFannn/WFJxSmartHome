package com.jinxin.jxsmarthome.entity;

/**
 * 自定义遥控功能点
 * @author TangLong
 * @company 金鑫智慧
 */
public class CustomControlFun {
	private String funName;
	
	@Override
	public String toString() {
		return "CustomControlFun [funName=" + funName + "]";
	}

	public String getFunName() {
		return funName;
	}

	public void setFunName(String funName) {
		this.funName = funName;
	}
}
