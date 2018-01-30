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
}
