package com.jinxin.jxsmarthome.entity;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;

/**
 * 红外遥控器
 * @company 金鑫智慧
 * @author wangfan
 */
public class InfraredRemoteController extends Base {
	
	
	@Id
	@Column(name = "id")
	private int id;
	
	private String deviceName = "";
	
	private int deviceType;
	private String brand;
	private byte[] mCode;

	private FunDetail funDetail;
	private ProductFun productFun;
	public InfraredRemoteController(int id,String deviceName,int deviceType,String brand,byte[] mCode,FunDetail funDetail,ProductFun productFun) {
		setId(id);
		setDeviceName(deviceName);
		setDeviceType(deviceType);
		setBrand(brand);
		setmCode(mCode);
		setFunDetail(funDetail);
		setProductFun(productFun);
	}
	public InfraredRemoteController() {
		// TODO Auto-generated constructor stub
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
	public int getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public byte[] getmCode() {
		return mCode;
	}
	public void setmCode(byte[] mCode) {
		this.mCode = mCode;
	}
	public FunDetail getFunDetail() {
		return funDetail;
	}
	public void setFunDetail(FunDetail funDetail) {
		this.funDetail = funDetail;
	}
	public ProductFun getProductFun() {
		return productFun;
	}
	public void setProductFun(ProductFun productFun) {
		this.productFun = productFun;
	}
}
