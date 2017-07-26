package com.jinxin.jxsmarthome.entity;

import android.view.View.OnClickListener;

/**
 * 通用button控件单元
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class CommBtnVO {
	private int type = -1;
	private String name = null;
	private int IconId = -1;
	private OnClickListener listener = null;
	public CommBtnVO(int type, String name, int iconId, OnClickListener listener) {
		super();
		this.type = type;
		this.name = name;
		IconId = iconId;
		this.listener = listener;
	}
	public CommBtnVO(int type, String name, int iconId) {
		this(type, name,iconId,null);
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getIconId() {
		return IconId;
	}
	public void setIconId(int iconId) {
		IconId = iconId;
	}
	public OnClickListener getListener() {
		return listener;
	}
	public void setListener(OnClickListener listener) {
		this.listener = listener;
	}
}
