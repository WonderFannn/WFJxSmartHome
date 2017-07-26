package com.jinxin.jxsmarthome.ui.widget.vfad;

/**
 *  广告
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class Advertising {

	String name = null;

	String imageUrl = null;

	String url = null;
	
	String path = null;//本地存储图片位置
	
	boolean isLoading = false;//是否加载中

	public boolean isLoading() {
		return isLoading;
	}

	public void setLoading(boolean isLoading) {
		this.isLoading = isLoading;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
