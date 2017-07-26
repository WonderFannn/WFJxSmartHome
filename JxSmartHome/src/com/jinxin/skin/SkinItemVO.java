package com.jinxin.skin;

/**
 * 皮肤属性单元
 * @author JackeyZhang 2013-6-25 上午11:18:15
 */
public class SkinItemVO {
	private String packageName = null;
	private String name = null;
	private String icon = null;
	private String id = null;
	public SkinItemVO(String id){
		this(id,null,null,null);
	}
	public SkinItemVO(String id,String packageName,String name,String icon){
		this.id = id;
		this.packageName = packageName;
		this.name = name;
		this.icon = icon;
	}
	public String getID() {
		return id;
	}
	public void setID(String iD) {
		id = iD;
	}
	public String getPackageUrl() {
		return packageName;
	}
	public void setPackageUrl(String packageUrl) {
		this.packageName = packageUrl;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
}
