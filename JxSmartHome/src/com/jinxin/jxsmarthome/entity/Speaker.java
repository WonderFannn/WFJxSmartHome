package com.jinxin.jxsmarthome.entity;

/**
 * 扬声器
 * @author  TangLong
 * @company 金鑫智慧
 */
public class Speaker {
	private String name;
	private boolean checked;
	
	public Speaker() {
		
	}
	
	public Speaker(String name) {
		this(name, false);
	}
	
	public Speaker(String name, boolean checked) {
		super();
		this.name = name;
		this.checked = checked;
	}
	
	@Override
	public String toString() {
		return name + ":" + checked;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
	
}
