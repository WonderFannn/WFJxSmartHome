package com.jinxin.jxsmarthome.entity;

public class SpeakerParam {
	private String selectRood;
	private String usbSd;
	
	public SpeakerParam(String selectRood, String usbSd) {
		super();
		this.selectRood = selectRood;
		this.usbSd = usbSd;
	}
	
	public String getSelectRood() {
		return selectRood;
	}
	public void setSelectRood(String selectRood) {
		this.selectRood = selectRood;
	}
	public String getUsbSd() {
		return usbSd;
	}
	public void setUsbSd(String usbSd) {
		this.usbSd = usbSd;
	}
	
	
}
