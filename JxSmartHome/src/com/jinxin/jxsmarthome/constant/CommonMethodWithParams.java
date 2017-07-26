package com.jinxin.jxsmarthome.constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.jinxin.db.impl.CustomerDaoImpl;
import com.jinxin.db.impl.CustomerProductDaoImpl;
import com.jinxin.jxsmarthome.entity.Customer;
import com.jinxin.jxsmarthome.entity.CustomerProduct;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.util.AppUtil;
import com.jinxin.jxsmarthome.util.CommUtil;

public class CommonMethodWithParams extends CommonMethod {
	
//	public static List<byte[]> productFunToCMD(Context con, String cmdType4Device, String address485, 
//			Map<String, Object> params) {
//		String customerId = AppUtil.getCustomerId(con);
//		return CommonMethod.createCmdWithLength(customerId,
//				address485, cmdType4Device, null, cmdType4Device, params);
//	}
	
	public static List<byte[]> productFunToCMD(Context con,ProductFun productFun, 
			FunDetail funDetail, Map<String, Object> params) {
		if (productFun == null || funDetail == null)
			return null;

		String type = getType(con, productFun, funDetail);
		List<String> lightNumbers = new ArrayList<String>();
		lightNumbers.add(productFun.getFunUnit());
		CustomerProductDaoImpl cpDaoImpl = new CustomerProductDaoImpl(con);
		List<CustomerProduct> cpList = cpDaoImpl.find(null, "whId=?",
				new String[] { productFun.getWhId() }, null, null, null, null);
		String addr485 = "";
		if (cpList != null && cpList.size() > 0) {
			CustomerProduct cp = cpList.get(0);
			if (cp != null) {
				addr485 = cp.getAddress485();
			}
		}

//		String customerId = CommUtil.getMainAccount();
		/********2016-01-26 修改多网关指令*********/
		String whId485 = AppUtil.getGetwayWhIdByProductWhId(con, productFun.getWhId());
		/*************END**********************/
		List<byte[]> cmd = CommonMethod.createCmdWithLength(whId485,
				addr485, type, lightNumbers, productFun.getFunType(), params);
		return cmd;
	}
	
	public static List<byte[]> productFunToCMD(Context con,ProductFun productFun, Map<String, Object> params, String type) {
		if (productFun == null)
			return null;

		List<String> lightNumbers = new ArrayList<String>();
		lightNumbers.add(productFun.getFunUnit());
		CustomerProductDaoImpl cpDaoImpl = new CustomerProductDaoImpl(con);
		List<CustomerProduct> cpList = cpDaoImpl.find(null, "whId=?",
				new String[] { productFun.getWhId() }, null, null, null, null);
		String addr485 = "";
		if (cpList != null && cpList.size() > 0) {
			CustomerProduct cp = cpList.get(0);
			if (cp != null) {
				addr485 = cp.getAddress485();
			}
		}

//		String customerId = CommUtil.getMainAccount();
		/********2016-01-26 修改多网关指令*********/
		String whId485 = AppUtil.getGetwayWhIdByProductWhId(con, productFun.getWhId());
		/*************END**********************/
		List<byte[]> cmd = CommonMethod.createCmdWithLength(whId485,
				addr485, type, lightNumbers, productFun.getFunType(), params);
		return cmd;
	}
	
	public static List<byte[]> productFunToCMD(Context con,ProductFun productFun, 
			FunDetail funDetail, Map<String, Object> params, String type) {
		if (productFun == null || funDetail == null)
			return null;

		List<String> lightNumbers = new ArrayList<String>();
		lightNumbers.add(productFun.getFunUnit());
		CustomerProductDaoImpl cpDaoImpl = new CustomerProductDaoImpl(con);
		List<CustomerProduct> cpList = cpDaoImpl.find(null, "whId=?",
				new String[] { productFun.getWhId() }, null, null, null, null);
		String addr485 = "";
		if (cpList != null && cpList.size() > 0) {
			CustomerProduct cp = cpList.get(0);
			if (cp != null) {
				addr485 = cp.getAddress485();
			}
		}

//		String customerId = CommUtil.getMainAccount();
		/********2016-01-26 修改多网关指令*********/
		String whId485 = AppUtil.getGetwayWhIdByProductWhId(con, productFun.getWhId());
		/*************END**********************/
		List<byte[]> cmd = CommonMethod.createCmdWithLength(whId485,
				addr485, type, lightNumbers, productFun.getFunType(), params);
		return cmd;
	}
}
