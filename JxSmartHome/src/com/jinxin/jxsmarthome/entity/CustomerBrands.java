package com.jinxin.jxsmarthome.entity;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;


/**
 * 红外遥控设备品牌、型号
 * @company 金鑫智慧
 */
@Table(name = "customer_brands")
public class CustomerBrands extends Base{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "id")
	private int id;

	@Column(name = "deviceId")
	private Integer deviceId ;

	/**代码编号**/
	@Column(name = "mCode")
	private String mCode ;

	@Column(name = "whId")
	private String whId ;
	
	/**品牌名称**/
	@Column(name = "customerId")
	private String customerId ;
	
	/**品牌名称**/
	@Column(name = "brandName")
	private String brandName ;
	
	@Column(name = "nickName")
	private String nickName ;
	
	/**品牌ID **/
	@Column(name = "brandsId")
	private Integer brandsId ;
	/**遥控板类型 1—码库  2—学习 **/
	@Column(name = "type")
	private Integer type ;
	

	public CustomerBrands() {
	
	}

	public CustomerBrands(int id, Integer deviceId, String mCode, String whId, String customerId,
			String brandName, String nickName, Integer brandsId,Integer type) {
		super();
		this.id = id;
		this.deviceId = deviceId;
		this.mCode = mCode;
		this.whId = whId;
		this.customerId = customerId;
		this.brandName = brandName;
		this.nickName = nickName;
		this.brandsId = brandsId;
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	public String getmCode() {
		return mCode;
	}

	public void setmCode(String mCode) {
		this.mCode = mCode;
	}

	public String getWhId() {
		return whId;
	}

	public void setWhId(String whId) {
		this.whId = whId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getBrandsId() {
		return brandsId;
	}

	public void setBrandsId(Integer brandsId) {
		this.brandsId = brandsId;
	}
}
