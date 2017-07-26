package com.jinxin.jxsmarthome.ui.widget.vfad;

import java.util.List;

public class ADManager {
	private static ADManager instance = null;
	private List<Advertising> advertisingList = null;

	public ADManager (){
	}
	/**
	 * 初始化
	 * @param context
	 * @return
	 */
	public static ADManager instance() {
		if (instance == null) {
			instance = new ADManager();
		}
		return instance;
	}
	public List<Advertising> getAdvertisingList() {
		return advertisingList;
	}
	public void setAdvertisingList(List<Advertising> advertisingList) {
		this.advertisingList = advertisingList;
	}
	
}
