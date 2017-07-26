package com.jinxin.jxsmarthome.entity;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;
/**
 * 产品功能列表单元
 * @author JackeyZhang
 * @company 金鑫智慧
 */
@Table(name = "product_fun")
public class ProductFun extends Base implements Cloneable{
	private static final long serialVersionUID = -3425262334098839688L;
	
	@Id
	@Column(name = "funId")
	private int funId;
	@Column(name = "whId")
	private String whId;
	@Column(name = "funUnit")
	private String funUnit;
	@Column(name = "code")
	private String code;
	@Column(name = "funName")
	private String funName;
	@Column(name = "comments")
	private String comments;
	@Column(name = "funType")
	private String funType;
	@Column(name = "lightColor")
	private String lightColor;
	@Column(name = "brightness")
	private int brightness;
	@Column(name = "intColor")
	private int intColor;
	@Column(name = "beHomepage")
	private long beHomepage;
	@Column(name = "enable")
	private int enable;
	@Column(name = "funParams")
	private String funParams ;
	private String state;
	
	private String tempFunName;
	private boolean isOpen = false;
	
	public ProductFun(){
		
	}
	public ProductFun(int funId, String whId, String funUnit, String code,
			String funName, String comments, String funType,
			String lightColor, int brightness, int intColor, int enable, String funParams) {
		super();
		this.funId = funId;
		this.whId = whId;
		this.funUnit = funUnit;
		this.code = code;
		this.funName = funName;
		this.comments = comments;
		this.funType = funType;
		this.lightColor = lightColor;
		this.brightness = brightness;
		this.intColor = intColor;
		this.enable = enable;
		this.funParams = funParams;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		ProductFun pf = null;
		try {
			pf = (ProductFun) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return pf;
	}
	
	@Override
	public String toString() {
		return "ProductFun [funId=" + funId + ", whId=" + whId + ", funUnit="
				+ funUnit + ", code=" + code + ", funName=" + funName
				+ ", comments=" + comments + ", funType=" + funType
				+ ", lightColor=" + lightColor + ", brightness=" + brightness
				+ ", intColor=" + intColor + ", beHomepage=" + beHomepage
				+ ", enable=" + enable + ", tempFunName=" + tempFunName
				+ ", isOpen=" + isOpen + "]";
	}
	public int getFunId() {
		return funId;
	}
	public void setFunId(int funId) {
		this.funId = funId;
	}
	public String getWhId() {
		return whId;
	}
	public void setWhId(String whId) {
		this.whId = whId;
	}
	public String getFunUnit() {
		return funUnit;
	}
	public void setFunUnit(String funUnit) {
		this.funUnit = funUnit;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getFunName() {
		return funName;
	}
	public void setFunName(String funName) {
		this.funName = funName;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getFunType() {
		return funType;
	}
	public void setFunType(String funType) {
		this.funType = funType;
	}
	public String getTempFunName() {
		return tempFunName;
	}
	public void setTempFunName(String tempFunName) {
		this.tempFunName = tempFunName;
	}
	public boolean isOpen() {
		return isOpen;
	}
	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}
	public String getLightColor() {
		return lightColor;
	}
	public void setLightColor(String lightColor) {
		this.lightColor = lightColor;
	}
	public int getBrightness() {
		return brightness;
	}
	public void setBrightness(int brightness) {
		this.brightness = brightness;
	}
	public int getIntColor() {
		return intColor;
	}
	public void setIntColor(int intColor) {
		this.intColor = intColor;
	}
	public long getBeHomepage() {
		return beHomepage;
	}
	public void setBeHomepage(long beHomepage) {
		this.beHomepage = beHomepage;
	}
	public int getEnable() {
		return enable;
	}
	public void setEnable(int enable) {
		this.enable = enable;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getFunParams() {
		return funParams;
	}
	public void setFunParam(String funParam) {
		this.funParams = funParam;
	}
	
}
