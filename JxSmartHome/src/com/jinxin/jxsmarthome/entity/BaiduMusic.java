package com.jinxin.jxsmarthome.entity;

import java.io.Serializable;

/**
 * 百度音乐、歌词地址信息
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class BaiduMusic extends Base implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer count;
	private String urlEncode;
	private String urlDecode;
	private String lrcid;
	private String durlEncode;
	private String durlDecode;
	private String p2pUrl;
	
	public BaiduMusic(){}

	public BaiduMusic ( Integer count,String urlEncode , String urlDecode , String lrcid , String durlEncode , String durlDecode , String p2pUrl ) {
		this.count = count;
		this.urlEncode = urlEncode;
		this.urlDecode = urlDecode;
		this.lrcid = lrcid;
		this.durlEncode = durlEncode;
		this.durlDecode = durlDecode;
		this.p2pUrl = p2pUrl;
	}
	
	@Override
	public String toString() {
		return "BaiduMusic [count=" + count + ", urlEncode=" + urlEncode
				+ ", urlDecode=" + urlDecode + ", lrcid=" + lrcid
				+ ", durlEncode=" + durlEncode + ", durlDecode=" + durlDecode
				+ ", p2pUrl=" + p2pUrl + "]";
	}

	public Integer getCount ( ) {
		return count;
	}

	public void setCount ( Integer count ) {
		this.count = count;
	}

	public String getUrlEncode ( ) {
		return urlEncode;
	}

	
	public void setUrlEncode ( String urlEncode ) {
		this.urlEncode = urlEncode;
	}

	
	public String getUrlDecode ( ) {
		return urlDecode;
	}

	
	public void setUrlDecode ( String urlDecode ) {
		this.urlDecode = urlDecode;
	}

	
	public String getLrcid ( ) {
		return lrcid;
	}

	
	public void setLrcid ( String lrcid ) {
		this.lrcid = lrcid;
	}

	
	public String getDurlEncode ( ) {
		return durlEncode;
	}

	
	public void setDurlEncode ( String durlEncode ) {
		this.durlEncode = durlEncode;
	}

	
	public String getDurlDecode ( ) {
		return durlDecode;
	}

	
	public void setDurlDecode ( String durlDecode ) {
		this.durlDecode = durlDecode;
	}

	
	public String getP2pUrl ( ) {
		return p2pUrl;
	}

	
	public void setP2pUrl ( String p2pUrl ) {
		this.p2pUrl = p2pUrl;
	}
	
	
}
