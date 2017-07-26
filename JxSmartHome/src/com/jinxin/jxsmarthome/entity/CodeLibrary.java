package com.jinxin.jxsmarthome.entity;


public class CodeLibrary extends Base {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String account ;
	private int secretKey;
	/**客户ID**/
	private String mCode ;
	private String deviceId ;
	private String brandsId ;
	private String address485 ;
	
	public CodeLibrary() {
		
	}
	
	public CodeLibrary(String account, int secretKey, String mCode,
			String deviceId, String brandsId, String address485) {
		super();
		this.account = account;
		this.secretKey = secretKey;
		this.mCode = mCode;
		this.deviceId = deviceId;
		this.brandsId = brandsId;
		this.address485 = address485;
	}

	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public int getSecretKey() {
		return secretKey;
	}
	public void setSecretKey(int secretKey) {
		this.secretKey = secretKey;
	}
	public String getmCode() {
		return mCode;
	}
	public void setmCode(String mCode) {
		this.mCode = mCode;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getBrandsId() {
		return brandsId;
	}
	public void setBrandsId(String brandsId) {
		this.brandsId = brandsId;
	}
	public String getAddress485() {
		return address485;
	}
	public void setAddress485(String address485) {
		this.address485 = address485;
	}
}
