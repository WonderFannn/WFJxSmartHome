package com.jinxin.jxsmarthome.util;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.jinxin.db.impl.CustomerDaoImpl;
import com.jinxin.db.impl.CustomerProductDaoImpl;
import com.jinxin.db.impl.FunDetailConfigDaoImpl;
import com.jinxin.db.impl.FunDetailDaoImpl;
import com.jinxin.db.impl.ProductFunDaoImpl;
import com.jinxin.db.impl.ProductRegisterDaoImpl;
import com.jinxin.db.impl.ProductStateDaoImpl;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.entity.Customer;
import com.jinxin.jxsmarthome.entity.CustomerProduct;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.FunDetailConfig;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductRegister;
import com.jinxin.jxsmarthome.entity.ProductState;
import com.jinxin.jxsmarthome.main.JxshApp;

/**
 * app常用操作工具类
 * @author TangLong
 * @company 金鑫智慧
 */
public class AppUtil {
	// 空数组
	public static final int[] EMPTY_INT_ARRAY = new int[0];
	public static final String[] EMPTY_STRING_ARRAY = new String[0];
	
	/**
	 * 根据funType得到FunDetail
	 */
	public static FunDetail getFunDetailByFunType(Context context, String funType) {
		final FunDetailDaoImpl fdDao = new FunDetailDaoImpl(context);
		List<FunDetail> fdList = fdDao.find(null, "funType=?", new String[]{funType}, null, null, null, null);
		if(fdList != null && fdList.size() > 0) {
			return fdList.get(0);
		}
		return null;
	}
	
	/**
	 * 获取设备的ProductFun
	 */
	public static List<ProductFun> getProductFunByFunType(Context context, String type) {
		ProductFunDaoImpl proDaoImpl = new ProductFunDaoImpl(context);
		ArrayList<ProductFun> resultList = new ArrayList<ProductFun>();
		ArrayList<ProductFun> list = (ArrayList<ProductFun>) proDaoImpl.find(null,
				"funType = ?", new String[] {type}, null, null, null, null);
		resultList.addAll(list);
		return resultList;
	}
	
	/**
	 * 获取设备的FunDetailConfig
	 */
	public static FunDetailConfig getFunDetailConfigByWhId(Context context, String whId) {
		FunDetailConfigDaoImpl fdcDaoImpl = new FunDetailConfigDaoImpl(context);
		ArrayList<FunDetailConfig> list = (ArrayList<FunDetailConfig>) fdcDaoImpl.find(null,
				"whId = ?", new String[] {whId}, null, null, null, null);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * 获取设备的ProductFun
	 */
	public static ProductFun getSingleProductFunByFunType(Context context, String type) {
		ProductFunDaoImpl proDaoImpl = new ProductFunDaoImpl(context);
		ArrayList<ProductFun> list = (ArrayList<ProductFun>) proDaoImpl.find(null,
				"funType = ?", new String[] {type}, null, null, null, null);
		if(list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	/**
	 * 通过whId获取设备的ProductFun
	 */
	public static ProductFun getSingleProductFunByWhId(Context context, String whId) {
		ProductFunDaoImpl proDaoImpl = new ProductFunDaoImpl(context);
		ArrayList<ProductFun> list = (ArrayList<ProductFun>) proDaoImpl.find(null,
				"whId = ?", new String[] {whId}, null, null, null, null);
		if(list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * 获取设备注册信息
	 */
	public static ProductRegister getProductRegisterByWhId(Context context, String whId) {
		ProductRegisterDaoImpl prDaoImpl = new ProductRegisterDaoImpl(context);
		ArrayList<ProductRegister> list = (ArrayList<ProductRegister>) prDaoImpl.find(null,
				"whId = ?", new String[] {whId}, null, null, null, null);
		if(list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * 获取设备对应的网关注册信息
	 */
	public static ProductRegister getGetwayByWhId(Context context, String whId) {
		ProductRegisterDaoImpl prDaoImpl = new ProductRegisterDaoImpl(context);
		ArrayList<ProductRegister> list = (ArrayList<ProductRegister>) prDaoImpl.find(null,
				"whId = ?", new String[] {whId}, null, null, null, null);
		if(list != null && list.size() > 0) {
			ProductRegister pr = list.get(0);
			Logger.debug(null, pr.toString());
			ArrayList<ProductRegister> list1 = (ArrayList<ProductRegister>) prDaoImpl
					.find(null, "whId=?", new String[]{pr.getGatewayWhId()}, null, null, null, null);
			if(list1.size() > 0) return list1.get(0);
		}
		return null;
	}
	
	/**
	 * 获取设备对应的网关注册信息
	 */
	public static String getGetwayWhIdByProductWhId(Context context, String whId) {
		ProductRegisterDaoImpl prDaoImpl = new ProductRegisterDaoImpl(context);
		ArrayList<ProductRegister> list = (ArrayList<ProductRegister>) prDaoImpl.find(null,
				"whId = ?", new String[] {whId}, null, null, null, null);
		if(list != null && list.size() > 0) {
			ProductRegister pr = list.get(0);
//			Logger.debug(null, "AppUtil-->"+pr.toString());
			ArrayList<ProductRegister> list1 = (ArrayList<ProductRegister>) prDaoImpl
					.find(null, "whId=?", new String[]{pr.getGatewayWhId()}, null, null, null, null);
			if(list1.size() > 0) {
				return list1.get(0).getGatewayWhId();
			}
		}else {
			Logger.error(null, "error to find product register information");
		}
		return "";
	}
	
	/**
	 * 通过设备whId获取设备对应的网关MAC地址
	 */
	public static String getGetwayMACByProductWhId(Context context, String whId) {
		ProductRegisterDaoImpl prDaoImpl = new ProductRegisterDaoImpl(context);
		//先通过设备whId查找网关whId
		ArrayList<ProductRegister> list = (ArrayList<ProductRegister>) prDaoImpl.find(null,
				"whId=?", new String[] {whId}, null, null, null, null);
		if(list != null && list.size() > 0) {
			ProductRegister pr = list.get(0);
			Logger.debug(null, pr.toString());
			//再通过网关whId查找该网关下所有的设备，包括网关
			ArrayList<ProductRegister> list1 = (ArrayList<ProductRegister>) prDaoImpl
					.find(null, "whId=?", new String[]{pr.getGatewayWhId()}, null, null, null, null);
			if(list1 != null && list1.size() > 0) {
//				return list1.get(0).getGatewayWhId();
				//最后找到网关的MAC地址取匹配无线网关的本地IP
				for (ProductRegister _pr : list1) {
					if (_pr.getCode().equals(ProductConstants.FUN_TYPE_WIRELESS_GATEWAY)) {
						return list1.get(0).getMac();
					}
				}
				
			}
		}else {
			Logger.error(null, "error to find product register information");
		}
		return "";
	}
	
	public static ProductRegister getGetway(Context context) {
		ProductRegisterDaoImpl prDaoImpl = new ProductRegisterDaoImpl(context);
		ArrayList<ProductRegister> list = (ArrayList<ProductRegister>) prDaoImpl.find(null,
				"code = ?", new String[] {"007"}, null, null, null, null);
		if(list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	} 
	
	/**
	 * 获取CustomerProduct
	 */
	public static CustomerProduct getCustomerProductByWhId(Context context, String whId) {
		CustomerProductDaoImpl dao = new CustomerProductDaoImpl(context);
		List<CustomerProduct> list = dao.find(null, "whId=?",
				new String[] { whId }, null, null, null, null);
		if(list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * 获取设备关联的状态
	 */
	public static ProductState getProductStateByFunId(Context context, int funId) {
		ProductStateDaoImpl psDaoImpl = new ProductStateDaoImpl(context);
		ArrayList<ProductState> list = (ArrayList<ProductState>) psDaoImpl.find(null,
				"funId = ?", new String[] {String.valueOf(funId)}, null, null, null, null);
		if(list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * 获取设备的ProductFun
	 */
	public static ProductFun getSingleProductFunByFunId(Context context, String id) {
		ProductFunDaoImpl proDaoImpl = new ProductFunDaoImpl(context);
		ArrayList<ProductFun> list = (ArrayList<ProductFun>) proDaoImpl.find(null,
				"funId = ?", new String[] {id}, null, null, null, null);
		if(list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 得到用户id
	 * @param context
	 * @return
	 */
	public static String getCustomerId(Context context) {
		String customerId = null;
		CustomerDaoImpl cDaoImpl = new CustomerDaoImpl(context);
		List<Customer> cList = cDaoImpl.find();
		if (cList != null && cList.size() > 0) {
			Customer c = cList.get(0);
			if (c != null) {
				customerId = (c.getCustomerId() == null ? "" : c.getCustomerId());
			}
		}
		return customerId;
	}
	

	/**
	 * 获取设备信息
	 * @param context
	 * @return
	 */
	public static String getDeviceInfo(Context context) {
	    try{
	      org.json.JSONObject json = new org.json.JSONObject();
	      android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
	          .getSystemService(Context.TELEPHONY_SERVICE);
	
	      String device_id = tm.getDeviceId();
	
	      android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	
	      String mac = wifi.getConnectionInfo().getMacAddress();
	      json.put("mac", mac);
	
	      if( TextUtils.isEmpty(device_id) ){
	        device_id = mac;
	      }
	
	      if( TextUtils.isEmpty(device_id) ){
	        device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);
	      }
	
	      json.put("device_id", device_id);
	      Logger.debug(null, json.toString());
	      return json.toString();
	    }catch(Exception e){
	      e.printStackTrace();
	    }
	  return null;
	}
	
	/**
	 * 启动电子猫眼app并启用第三方登录
	 */
	public static void startKit() {
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setComponent(new ComponentName("com.jinxin.jxsmartkit", "com.jinxin.jxsmartkit.MainActivity"));
		JxshApp.getContext().startActivity(intent);
//		String account = CommUtil.getCurrentLoginAccount();
//		JxshApp.instance.loginKit(account, JxshApp.getPassword(account), "A0000010", "n5wGPB6iItutTbB");
	}
                  
}
