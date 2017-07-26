package com.jinxin.jxsmarthome.entity;

/**
 * 版本信息
 * 
 * @author BinhuaHuang
 * @company 金鑫智慧
 */
public class VersionVO extends Base {

	public String getAppVersion() {
		return appVersion;
	}
	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}
	public String getPublishTime() {
		return publishTime;
	}
	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getAppPath() {
		return appPath;
	}
	public void setAppPath(String appPath) {
		this.appPath = appPath;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public VersionVO(String appVersion, String publishTime, String comments, int status, String appPath, int size) {
		super();
		this.appVersion = appVersion;
		this.publishTime = publishTime;
		this.comments = comments;
		this.status = status;
		this.appPath = appPath;
		this.size = size;
	}
	private String appVersion;
	private String publishTime;
	private String comments;
	private int status;
	private String appPath;
	private int size;

}
