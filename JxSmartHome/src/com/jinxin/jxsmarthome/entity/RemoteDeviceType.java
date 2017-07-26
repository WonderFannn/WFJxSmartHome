package com.jinxin.jxsmarthome.entity;



/**
 * 红外遥控设备类型
 * @company 金鑫智慧
 */
public class RemoteDeviceType extends Base{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	/**客户ID**/
	private String deviceName ;
	private String icon ;
	
	public RemoteDeviceType() {
	
	}

	public RemoteDeviceType(int id, String deviceName,String icon) {
		super();
		this.id = id;
		this.deviceName = deviceName;
		this.icon = icon;
	}

	@Override
	public String toString() {
		return "RemoteDeviceType [id=" + id + ", deviceName=" + deviceName
				+ "]";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
	
}
