package com.jinxin.jxsmarthome.entity;


import com.jinxin.jxsmarthome.ui.widget.MyGalleryLayout;

/**
 * 主菜单项
 * 
 * @author JackeyZhang
 * 
 */
public class MainMenuItem {
	private String id = null;// 菜单项ID
	private int iconId = 0;// 图标ID
	private String text = "";//文字内容
	private MyGalleryLayout.MyGalleryItemOnClickListener clickListener = null;// 点击事件

	private boolean isSelected = false;// 是否选中

	public MainMenuItem(String id, int iconId,String text, MyGalleryLayout.MyGalleryItemOnClickListener listener, boolean isSelected) {
		this.id = id;
		this.iconId = iconId;
		this.text = text;
		this.clickListener = listener;
		this.isSelected = isSelected;
	}

	public MainMenuItem(String id, int iconId,String text, MyGalleryLayout.MyGalleryItemOnClickListener listener) {
		this(id, iconId,text, listener, false);
	}

	public MainMenuItem(String id, int iconId,String text) {
		this(id, iconId,text, null, false);
	}
	public MainMenuItem(String id, int iconId) {
		this(id, iconId,"", null, false);
	}
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	public void addOnClickListener(MyGalleryLayout.MyGalleryItemOnClickListener listener) {
		this.clickListener = listener;
	}

	public MyGalleryLayout.MyGalleryItemOnClickListener getOnClickListener() {
		return this.clickListener;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getIconId() {
		return iconId;
	}

	public void setIconId(int iconId) {
		this.iconId = iconId;
	}
	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
}
