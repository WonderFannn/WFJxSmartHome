package com.jinxin.jxsmarthome.entity;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;

/**
 * 客户设备信息
 * @author JackeyZhang
 * @company 金鑫智慧
 */
@Table(name = "customer_product")
public class CustomerProduct extends Base{
	@Id
	@Column(name = "id")
	private int id;
	@Column(name = "whId")
	private String whId;
	@Column(name = "code")
	private String code;
	@Column(name = "proTime")
	private String proTime;
	@Column(name = "typeNo")
	private String typeNo;
	@Column(name = "batch")
	private int batch;
	@Column(name = "version")
	private String version;
	@Column(name = "qcReport")
	private String qcReport;
	@Column(name = "checker")
	private String checker;
	@Column(name = "checkTime")
	private String checkTime;
	@Column(name = "eqDesc")
	private String eqDesc;
	@Column(name = "producer")
	private String producer;
	@Column(name = "recordTime")
	private String recordTime;
	@Column(name = "recorder")
	private String recorder;
	@Column(name = "comments")
	private String comments;
	@Column(name = "mac")
	private String mac;
	@Column(name = "updateUser")
	private String updateUser;
	@Column(name = "updateTime")
	private String updateTime;
	@Column(name = "icon")
	private String icon;
	@Column(name = "typeName")
	private String typeName;
	@Column(name = "address485")
	private String address485;
	@Column(name = "path")
	private String path = "";
	
	private boolean isLoading = false;//是否加载中
		
	public CustomerProduct(){
		
	}

	public CustomerProduct(String whId, String code, String proTime,
			String typeNo, int batch, String version, String qcReport,
			String checker, String checkTime, String eqDesc, String producer,
			String recordTime, String recorder, String comments, String mac,
			String updateUser, String updateTime, String icon, String typeName,
			String address485) {
		super();
		this.whId = whId;
		this.code = code;
		this.proTime = proTime;
		this.typeNo = typeNo;
		this.batch = batch;
		this.version = version;
		this.qcReport = qcReport;
		this.checker = checker;
		this.checkTime = checkTime;
		this.eqDesc = eqDesc;
		this.producer = producer;
		this.recordTime = recordTime;
		this.recorder = recorder;
		this.comments = comments;
		this.mac = mac;
		this.updateUser = updateUser;
		this.updateTime = updateTime;
		this.icon = icon;
		this.typeName = typeName;
		this.address485 = address485;
		this.path = "";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getWhId() {
		return whId;
	}

	public void setWhId(String whId) {
		this.whId = whId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getProTime() {
		return proTime;
	}

	public void setProTime(String proTime) {
		this.proTime = proTime;
	}

	public String getTypeNo() {
		return typeNo;
	}

	public void setTypeNo(String typeNo) {
		this.typeNo = typeNo;
	}

	public int getBatch() {
		return batch;
	}

	public void setBatch(int batch) {
		this.batch = batch;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getQcReport() {
		return qcReport;
	}

	public void setQcReport(String qcReport) {
		this.qcReport = qcReport;
	}

	public String getChecker() {
		return checker;
	}

	public void setChecker(String checker) {
		this.checker = checker;
	}

	public String getCheckTime() {
		return checkTime;
	}

	public void setCheckTime(String checkTime) {
		this.checkTime = checkTime;
	}

	public String getEqDesc() {
		return eqDesc;
	}

	public void setEqDesc(String eqDesc) {
		this.eqDesc = eqDesc;
	}

	public String getProducer() {
		return producer;
	}

	public void setProducer(String producer) {
		this.producer = producer;
	}

	public String getRecordTime() {
		return recordTime;
	}

	public void setRecordTime(String recordTime) {
		this.recordTime = recordTime;
	}

	public String getRecorder() {
		return recorder;
	}

	public void setRecorder(String recorder) {
		this.recorder = recorder;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getAddress485() {
		return address485;
	}

	public void setAddress485(String address485) {
		this.address485 = address485;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isLoading() {
		return isLoading;
	}

	public void setLoading(boolean isLoading) {
		this.isLoading = isLoading;
	}
	
	
}
