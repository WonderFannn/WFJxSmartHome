package com.jinxin.jxsmarthome.cmd;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.jinxin.db.impl.CustomerDaoImpl;
import com.jinxin.db.impl.CustomerProductDaoImpl;
import com.jinxin.db.impl.ProductFunDaoImpl;
import com.jinxin.db.impl.ProductRegisterDaoImpl;
import com.jinxin.jxsmarthome.cmd.entity.Command;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.entity.Customer;
import com.jinxin.jxsmarthome.entity.CustomerProduct;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductPatternOperation;
import com.jinxin.jxsmarthome.entity.ProductRegister;
import com.jinxin.jxsmarthome.util.AppUtil;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.jxsmarthome.util.StringUtils;

public abstract class CmdGenerator implements ICmdGenerator {

	@Override
	public List<byte[]> generateCmd(Context context, ProductFun productFun,
			FunDetail funDetail, Map<String, Object> params) {
		return null;
	}
	
	@Override
	public List<byte[]> generateCmd(Context context, ProductFun productFun,
			FunDetail funDetail, Map<String, Object> params, String type) {
		return null;
	}

	@Override
	public List<byte[]> generateProductPatternOperationCmd(Context context,
			ProductFun productFun, FunDetail funDetail,
			Map<String, Object> params) {
		return null;
	}
	
	@Override
	public List<byte[]> generateProductPatternOperationCmd(Context context,
			List<ProductPatternOperation> ppoList, Map<String, Object> params) {
		return null;
	}
	
	@Override
	public List<Command> generateProductPatternOperationCommand(
			Context context, List<ProductPatternOperation> ppoList,
			Map<String, Object> params) {
		return null;
	}
	
	/**
	 * 根据设备的whid获取注册信息
	 */
	protected ProductRegister getGatwayByWhId(Context context, String whid) {
		ProductRegisterDaoImpl dao = new ProductRegisterDaoImpl(context);
		List<ProductRegister> prList = dao.find(null, "whId=?", new String[]{whid}, null, null, null, null);
		if(prList != null && prList.size() > 0) {
			return prList.get(0);
		}
		return null;
	}
	
	/**
	 * 为原始命令增加长度和头信息
	 */
	protected byte[] generateCmdWithPrefixAndLength(String prefix, String splitor, String cmd) {
		StringBuffer sb = new StringBuffer(prefix);
		sb.append(splitor);
		sb.append(cmd);
		try {
			int len = sb.toString().getBytes("utf-8").length;
			ByteBuffer bbf = ByteBuffer.allocate(len + 8);
			bbf.putInt(1); 
			bbf.putInt(len);
			bbf.put(sb.toString().getBytes("utf-8"));
			bbf.flip();
			return bbf.array();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new byte[0];
	}
	
	/**
	 * 为原始命令数组合并为一条并增加长度和头信息
	 */
	protected byte[] generateCmdArrayToOneWithLength(String prefix, String splitor, List<String> cmdArr) {
		if(cmdArr == null || cmdArr.isEmpty()) return null;
		StringBuffer sb = new StringBuffer(prefix);
		sb.append(splitor);
		for(String cmd : cmdArr) {
			sb.append(cmd);
			sb.append(splitor);
		}
		// 组拼完成后，删除最后一个字符(",")
		sb.deleteCharAt(sb.length() - 1);
		Logger.debug("Huang",sb.toString());
		try {
			int len = sb.toString().getBytes("utf-8").length;
			ByteBuffer bbf = ByteBuffer.allocate(len + 8);
			bbf.putInt(1); 
			bbf.putInt(len);
			bbf.put(sb.toString().getBytes("utf-8"));
			bbf.flip();
			return bbf.array();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 为原始命令增加长度和头信息
	 */
	protected List<byte[]> generateCmdWithPrefixAndLength(String prefix, String splitor, List<String> originalCmdList) {
		List<byte[]> cmdList = new ArrayList<byte[]>();
		for(String cmd : originalCmdList) {
			cmdList.add(generateCmdWithPrefixAndLength(prefix, splitor, cmd));
		}
		return cmdList;
	}
	
	/**
	 * 为原始命令增加长度
	 */
	protected List<byte[]> generateCmdWithLenght(List<String> originalCmdList) {
		List<byte[]> cmdList = new ArrayList<byte[]>();
		for (int i = 0; i < originalCmdList.size(); i++) {
			try {
				int len;
				String _cmd = originalCmdList.get(i).toString();

				len = _cmd.getBytes("utf-8").length;

				ByteBuffer bbf = ByteBuffer.allocate(len + 8);
				bbf.putInt(1); 
				bbf.putInt(len);
				bbf.put(_cmd.getBytes("utf-8"));
				bbf.flip();
				cmdList.add(bbf.array());
				
				System.out.println("send cmd-->>>" + new String(bbf.array()));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return cmdList;
	}
	
	/**
	 * 生成原始命令(无操作类型)
	 */
	protected List<String> generateOriginalCmd(Context context, ProductFun productFun,
			FunDetail funDetail, Map<String, Object> params) {
		return generateOriginalCmd(context, productFun, funDetail, params, null);
	}
	
	/**
	 * 生成原始命令(有操作类型)
	 */
	protected List<String> generateOriginalCmd(Context context, ProductFun productFun,
			FunDetail funDetail, Map<String, Object> params, String type) {
		if (productFun == null || funDetail == null || context == null) {
			Logger.error(null, "parameter error: productFun or funDetail is null");
			return Collections.emptyList();
		}

		String customerId = null;
		String addr485 = "";
		String funType = "";

		// 获取用户ID
		CustomerDaoImpl cDaoImpl = new CustomerDaoImpl(context);//Huang Table:customer
		List<Customer> cList = cDaoImpl.find();
		if (cList != null && cList.size() > 0) {
			Customer c = cList.get(0);
			if (c != null) {
				customerId = c.getCustomerId();
			}
		}

		// 获取funType
		funType = productFun.getFunType();

		// 获取addr485
		CustomerProductDaoImpl cpDaoImpl = new CustomerProductDaoImpl(context); //Huang Table:customer_product
		List<CustomerProduct> cpList = cpDaoImpl.find(null, "whId=?",
				new String[] { productFun.getWhId() }, null, null, null, null);
		if (cpList != null && cpList.size() > 0) {
			CustomerProduct cp = cpList.get(0);
			if (cp != null) {
				addr485 = cp.getAddress485();
			}
		}

		// 生成原始命令
		List<String> originalCmd = CommonMethod.createCmdWithNotAccount(
				customerId, addr485, type, null, funType, params);
		return originalCmd;
	}
	/**
	 * 生成原始命令为离线zg(模式)
	 */
	protected List<byte[]> generateZigbeeDeviceCmdInPatternForOffline(Context context,
			List<ProductPatternOperation> ppoList) {
		List<byte[]> cmdList = new ArrayList<byte[]>();
		// 传入参数校验
		if (ppoList == null || ppoList.size() < 1 || context == null) {
			Logger.error(null, "ppoList or context cannot be null");
			return cmdList;
		}
		
		for (ProductPatternOperation ppo : ppoList) {
			// 获取whid
			String whId = ppo.getWhId();
			if (CommUtil.isEmpty(whId)) {
				Logger.error(null, "whId cannot be null");
				return cmdList;
			}
			String zegbingWhId = AppUtil.getGetwayWhIdByProductWhId(context, whId);
			
			// 获取address485地址
			CustomerProductDaoImpl dao = new CustomerProductDaoImpl(context);
			List<CustomerProduct> customerProducts = dao.find(null, "whId=?",
					new String[] { whId }, null, null, null, null);
			if (customerProducts == null || customerProducts.size() < 1) {
				Logger.error(null, "customerproduct with whid=" + whId
						+ "cannot be found");
				return cmdList;
			}
			
			String funType = getCodeByProductWhId(context, whId);
			String address485 = customerProducts.get(0).getAddress485();
			if (CommUtil.isEmpty(address485) || CommUtil.isEmpty(funType)) {
				Logger.error(null, "address485 or type cannot be null");
				return cmdList;
			}
			
			// 获取customerid
			String customerId = CommUtil.getMainAccount();
			if (CommUtil.isEmpty(customerId)) {
				Logger.error(null, "customerId cannot be null");
				return cmdList;
			}
			
			// 生成原始命令
			List<String> originalCmd = new ArrayList<String>();
			
			//双向插座需要生成2次原始指令
			if (ProductConstants.FUN_TYPE_DOUBLE_SWITCH.equals(funType)||
					ProductConstants.FUN_TYPE_MULITIPLE_SWITCH.equals(funType)||
					ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FIVE.equals(funType)||
					ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FOUR.equals(funType)||
					ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_THREE.equals(funType)||
					ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_TWO.equals(funType)||
					ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_ONE.equals(funType)) {
				JSONObject jb;
				try {
					jb = new JSONObject(ppo.getParaDesc());
					Iterator keyIter = jb.keys();
					String key;
					String value;
					String leftType = "";
					String rightType = "";
					while (keyIter.hasNext()) {
						key = (String) keyIter.next();
						value = (String) jb.get(key);
						if ("left".equals(key)) {
							leftType = value;
						}else if("right".equals(key)){
							rightType = value;
						}else if(StaticConstant.TYPE_SOCKET_KEY1.equals(key)){
							leftType = value;
						}else if(StaticConstant.TYPE_SOCKET_KEY2.equals(key)){
							rightType = value;
						}
					}
					Map<String, Object> params = new HashMap<String, Object>();
					if (!TextUtils.isEmpty(leftType)) {//生成左开关指令
						params.put("src", "0x01");
						params.put("dst", "0x02");
						List<String> leftCmd = CommonMethod.createCmdWithNotAccount(
								customerId, address485, leftType, null, funType, params);
						originalCmd.addAll(leftCmd);
					}
					if (!TextUtils.isEmpty(rightType)) {//生成右开关指令
						params.put("src", "0x01");
						params.put("dst", "0x01");
						List<String> rightCmd = CommonMethod.createCmdWithNotAccount(
								customerId, address485, rightType, null, funType,params);
						originalCmd.addAll(rightCmd);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}else if (ProductConstants.FUN_TYPE_FIVE_SWITCH.equals(funType)){
				JSONObject jb;
				try {
					jb = new JSONObject(ppo.getParaDesc());
					Iterator keyIter = jb.keys();
					String key;
					String value;
					String[] switchStatusStr = new String[StaticConstant.FIVE_KEY_MAP.length];
					while (keyIter.hasNext()) {
						key = (String) keyIter.next();
						value = (String) jb.get(key);
						for (int i = 0; i < StaticConstant.FIVE_KEY_MAP.length; i++) {
							if (StaticConstant.FIVE_KEY_MAP[i].equals(key)) {
								switchStatusStr[i] = value;
							}
						}
					}
					Map<String, Object> params = new HashMap<String, Object>();
					for (int i = 0; i < switchStatusStr.length; i++) {
						if(!TextUtils.isEmpty(switchStatusStr[i])){
							params.put("src", "0x01");
							params.put("dst", StaticConstant.FIVE_DST_MAP[i]);
							List<String> fiveCmd = CommonMethod
									.createCmdWithNotAccount(customerId,
											address485, switchStatusStr[i], null, funType,
											params);
							originalCmd.addAll(fiveCmd);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}else if (ProductConstants.FUN_TYPE_THREE_SWITCH_THREE.equals(funType)||
					ProductConstants.FUN_TYPE_THREE_SWITCH_FOUR.equals(funType)||
					ProductConstants.FUN_TYPE_THREE_SWITCH_FIVE.equals(funType)||
					ProductConstants.FUN_TYPE_THREE_SWITCH_SIX.equals(funType)){
				JSONObject jb;
				try {
					jb = new JSONObject(ppo.getParaDesc());
					Iterator keyIter = jb.keys();
					String key;
					String value;
					String[] switchStatusStr = null;
					for (int i = 0; i < ProductConstants.FUN_TYPE_THREE_SWITCHES.length; i++) {
						if (funType.equals(ProductConstants.FUN_TYPE_THREE_SWITCHES[i]))
							switchStatusStr = new String[i + 3];
					}
					while (keyIter.hasNext()) {
						key = (String) keyIter.next();
						value = (String) jb.get(key);
						for (int i = 0; i < StaticConstant.TYPE_SOCKET_KEYS.length; i++) {
							if (StaticConstant.TYPE_SOCKET_KEYS[i].equals(key)) {
								switchStatusStr[i] = value;
							}
						}
					}
					Map<String, Object> params = new HashMap<String, Object>();
					for (int i = 0; i < switchStatusStr.length; i++) {
						if(!TextUtils.isEmpty(switchStatusStr[i])){
							params.put("src", "0x01");
							params.put("dst", StaticConstant.TYPE_DST_MAP[i]);
							List<String> threeCmd = CommonMethod
									.createCmdWithNotAccount(customerId,
											address485, switchStatusStr[i], null, funType,
											params);
							originalCmd.addAll(threeCmd);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (ProductConstants.FUN_TYPE_WIRELESS_INFRARED_TRANDPOND.equals(funType)) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("src", "0x01");
				params.put("dst", "0x01");
				params.put("type", "00 32");
				params.put("op", "0C");
				params.put(StaticConstant.PARAM_TEXT, ppo.getParaDesc());
				List<String> infrCmd = CommonMethod.createCmdWithNotAccount(customerId, address485, "commonCmd",
						null, funType, params);
				originalCmd.addAll(infrCmd);
			} else{// 其他设备生成原始命令
				if ("autoMode".equals(ppo.getOperation())) {//炫彩灯光模式指令生成
					List<String> openCmd = CommonMethod.createCmdWithNotAccount(customerId,
							address485, "mvToLevel", null, funType, openColorLight());
					originalCmd.addAll(openCmd);
					
					List<String> autoCmd = CommonMethod.createCmdWithNotAccount(
							customerId, address485, "autoMode", null, funType, 
							parseZigbeeDeviceParams4AutoMode(funType, ppo.getParaDesc()));
					originalCmd.addAll(autoCmd);
				}else{
					originalCmd = CommonMethod.createCmdWithNotAccount(
							customerId, address485, ppo.getOperation(), null, funType, 
							parseZigbeeDeviceParams(context, funType, ppo));
				}
			}
			
//			List<String> originalCmd = CommonMethod.createCmdWithNotAccount(
//					customerId, address485, ppo.getOperation(), null, funType, 
//					parseZigbeeDeviceParams(funType, ppo.getParaDesc()));
			
			cmdList.addAll(CommonMethod.createCmdOffline2(originalCmd));
		}
		return cmdList;
	}
//	/**
//	 * 生成原始命令(模式)
//	 */
//	protected List<byte[]> generateZigbeeDeviceCmdInPattern(Context context,
//			List<ProductPatternOperation> ppoList, boolean isOffline) {
//		List<byte[]> cmdList = new ArrayList<byte[]>();
//		// 传入参数校验
//		if (ppoList == null || ppoList.size() < 1 || context == null) {
//			Logger.error(null, "ppoList or context cannot be null");
//			return cmdList;
//		}
//		
//		for (ProductPatternOperation ppo : ppoList) {
//			// 获取whid
//			String whId = ppo.getWhId();
//			if (CommUtil.isEmpty(whId)) {
//				Logger.error(null, "whId cannot be null");
//				return cmdList;
//			}
//			String zegbingWhId = AppUtil.getGetwayWhIdByProductWhId(context, whId);
//			
//			// 获取address485地址
//			CustomerProductDaoImpl dao = new CustomerProductDaoImpl(context);
//			List<CustomerProduct> customerProducts = dao.find(null, "whId=?",
//					new String[] { whId }, null, null, null, null);
//			if (customerProducts == null || customerProducts.size() < 1) {
//				Logger.error(null, "customerproduct with whid=" + whId
//						+ "cannot be found");
//				return cmdList;
//			}
//			String type = customerProducts.get(0).getCode();
//			String address485 = customerProducts.get(0).getAddress485();
//			if (CommUtil.isEmpty(address485) || CommUtil.isEmpty(type)) {
//				Logger.error(null, "address485 or type cannot be null");
//				return cmdList;
//			}
//			
//			// 获取customerid
//			String customerId = CommUtil.getMainAccount();
//			if (CommUtil.isEmpty(customerId)) {
//				Logger.error(null, "customerId cannot be null");
//				return cmdList;
//			}
//			
//			// 生成原始命令
//			List<String> originalCmd = CommonMethod.createCmdWithNotAccount(
//					customerId, address485, ppo.getOperation(), null, type, 
//					parseZigbeeDeviceParams(type, ppo.getParaDesc()));
//			if(isOffline) cmdList.addAll(CommonMethod.createCmdOffline2(originalCmd));
//			else cmdList.addAll(generateCmdWithPrefixAndLength(zegbingWhId, ",", originalCmd));
//		}
//		return cmdList;
//	}
	/**
	 * 将一个zg设备的指令合并成一条（在线使用）
	 * @param context
	 * @param ppoList 一个zg设备的多条operation
	 * @return
	 */
	protected String generateZigbeeOneGroudOperationToCMD(Context context,
			List<ProductPatternOperation> ppoList) {
		// 传入参数校验
		if (ppoList == null || ppoList.size() < 1 || context == null) {
			Logger.error(null, "ppoList or context cannot be null");
			return null;
		}
		// // 获取whid
		// String whId = ppoList.get(0).getWhId();
		//
		// if (CommUtil.isEmpty(whId)) {
		// Logger.error(null, "whId cannot be null");
		// return null;
		// }
		StringBuilder _sb = new StringBuilder();
		for (ProductPatternOperation ppo : ppoList) {
			// 获取whid
			String whId = ppo.getWhId();
			if (CommUtil.isEmpty(whId)) {
				Logger.error(null, "whId cannot be null");
				return null;
			}
			String zegbingWhId = AppUtil.getGetwayWhIdByProductWhId(context,
					whId);

			// 获取address485地址
			CustomerProductDaoImpl dao = new CustomerProductDaoImpl(context);
			List<CustomerProduct> customerProducts = dao.find(null, "whId=?",
					new String[] { whId }, null, null, null, null);
			if (customerProducts == null || customerProducts.size() < 1) {
				Logger.error(null, "customerproduct with whid=" + whId
						+ "cannot be found");
				return null;
			}
			String funType = getCodeByProductWhId(context, whId);
			Logger.debug(null, "funType:" + funType);
			String address485 = customerProducts.get(0).getAddress485();
			if (CommUtil.isEmpty(address485) || CommUtil.isEmpty(funType)) {
				Logger.error(null, "address485 or type cannot be null");
				return null;
			}

			// 获取customerid
			String customerId = CommUtil.getMainAccount();
			if (CommUtil.isEmpty(customerId)) {
				Logger.error(null, "customerId cannot be null");
				return null;
			}

			List<String> originalCmd = new ArrayList<String>();
			// 双向插座需要生成2次原始指令
			if (ProductConstants.FUN_TYPE_DOUBLE_SWITCH.equals(funType)||
					ProductConstants.FUN_TYPE_MULITIPLE_SWITCH.equals(funType)||
					ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FIVE.equals(funType)||
					ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FOUR.equals(funType)||
					ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_THREE.equals(funType)||
					ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_TWO.equals(funType)||
					ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_ONE.equals(funType)) {
				JSONObject jb;
				try {
					jb = new JSONObject(ppo.getParaDesc());
					Iterator keyIter = jb.keys();
					String key;
					String value;
					String leftType = "";
					String rightType = "";
					while (keyIter.hasNext()) {
						key = (String) keyIter.next();
						value = (String) jb.get(key);
						if ("left".equals(key)) {
							leftType = value;
						}else if("right".equals(key)){
							rightType = value;
						}else if(StaticConstant.TYPE_SOCKET_KEY1.equals(key)){
							leftType = value;
						}else if(StaticConstant.TYPE_SOCKET_KEY2.equals(key)){
							rightType = value;
						}
					}
					Map<String, Object> params = new HashMap<String, Object>();
					if (!TextUtils.isEmpty(leftType)) {// 生成左开关指令
						params.put("src", "0x01");
						params.put("dst", "0x01");
						List<String> leftCmd = CommonMethod
								.createCmdWithNotAccount(customerId,
										address485, leftType, null, funType,
										params);
						originalCmd.addAll(leftCmd);
					}
					if (!TextUtils.isEmpty(rightType)) {// 生成右开关指令
						params.put("src", "0x01");
						params.put("dst", "0x02");
						List<String> rightCmd = CommonMethod
								.createCmdWithNotAccount(customerId,
										address485, rightType, null, funType,
										params);
						originalCmd.addAll(rightCmd);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (ProductConstants.FUN_TYPE_FIVE_SWITCH.equals(funType)){
				JSONObject jb;
				try {
					jb = new JSONObject(ppo.getParaDesc());
					Iterator keyIter = jb.keys();
					String key;
					String value;
					String[] switchStatusStr = new String[StaticConstant.FIVE_KEY_MAP.length];
					while (keyIter.hasNext()) {
						key = (String) keyIter.next();
						value = (String) jb.get(key);
						for (int i = 0; i < StaticConstant.FIVE_KEY_MAP.length; i++) {
							if (StaticConstant.FIVE_KEY_MAP[i].equals(key)) {
								switchStatusStr[i] = value;
							}
						}
					}
					Map<String, Object> params = new HashMap<String, Object>();
					for (int i = 0; i < switchStatusStr.length; i++) {
						if(!TextUtils.isEmpty(switchStatusStr[i])){
							params.put("src", "0x01");
							params.put("dst", StaticConstant.FIVE_DST_MAP[i]);
							List<String> fiveCmd = CommonMethod
									.createCmdWithNotAccount(customerId,
											address485, switchStatusStr[i], null, funType,
											params);
							originalCmd.addAll(fiveCmd);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}else if (ProductConstants.FUN_TYPE_THREE_SWITCH_THREE.equals(funType)||
					ProductConstants.FUN_TYPE_THREE_SWITCH_FOUR.equals(funType)||
					ProductConstants.FUN_TYPE_THREE_SWITCH_FIVE.equals(funType)||
					ProductConstants.FUN_TYPE_THREE_SWITCH_SIX.equals(funType)){
				JSONObject jb;
				try {
					jb = new JSONObject(ppo.getParaDesc());
					Iterator keyIter = jb.keys();
					String key;
					String value;
					String[] switchStatusStr = null;
					for (int i = 0; i < ProductConstants.FUN_TYPE_THREE_SWITCHES.length; i++) {
						if (funType.equals(ProductConstants.FUN_TYPE_THREE_SWITCHES[i]))
							switchStatusStr = new String[i + 3];
					}
					while (keyIter.hasNext()) {
						key = (String) keyIter.next();
						value = (String) jb.get(key);
						for (int i = 0; i < StaticConstant.TYPE_SOCKET_KEYS.length; i++) {
							if (StaticConstant.TYPE_SOCKET_KEYS[i].equals(key)) {
								switchStatusStr[i] = value;
							}
						}
					}
					Map<String, Object> params = new HashMap<String, Object>();
					for (int i = 0; i < switchStatusStr.length; i++) {
						if(!TextUtils.isEmpty(switchStatusStr[i])){
							params.put("src", "0x01");
							params.put("dst", StaticConstant.TYPE_DST_MAP[i]);
							List<String> threeCmd = CommonMethod
									.createCmdWithNotAccount(customerId,
											address485, switchStatusStr[i], null, funType,
											params);
							originalCmd.addAll(threeCmd);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}else if (ProductConstants.FUN_TYPE_WIRELESS_INFRARED_TRANDPOND.equals(funType)) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("src", "0x01");
				params.put("dst", "0x01");
				params.put("type", "00 32");
				params.put("op", "0C");
				String code = ppo.getParaDesc();
				StringBuffer buffer=new StringBuffer(code);
				int count=0;
				for (int i = 2; i < code.length();i+=2) {
					buffer.insert(i+count, " ");
					count+=1;
				}
				params.put(StaticConstant.PARAM_TEXT, buffer.toString()+" ");
				List<String> infrCmd = CommonMethod.createCmdWithNotAccount(customerId, address485, "commonCmd",
						null, funType, params);
				originalCmd.addAll(infrCmd);
			} else {// 其他设备生成原始命令
				if ("autoMode".equals(ppo.getOperation())
						|| "automode".equals(ppo.getOperation())) {// 炫彩灯光模式指令生成

					List<String> openCmd = CommonMethod
							.createCmdWithNotAccount(customerId, address485,
									"mvToLevel", null, funType,
									openColorLight());
					originalCmd.addAll(openCmd);

					List<String> autoCmd = CommonMethod
							.createCmdWithNotAccount(
									customerId,
									address485,
									"autoMode",
									null,
									funType,
									parseZigbeeDeviceParams4AutoMode(funType,
											ppo.getParaDesc()));
					originalCmd.addAll(autoCmd);
				} else {
					originalCmd = CommonMethod
							.createCmdWithNotAccount(
									customerId,
									address485,
									ppo.getOperation(),
									null,
									funType,
									parseZigbeeDeviceParams(context,funType,ppo));
				}
			}
			// 合并成一条
			if (originalCmd != null && originalCmd.size() > 0) {
				for (String _cmd : originalCmd)
					_sb.append("," + _cmd);
			}
		}
		String tempCmds = _sb.toString();
		if (tempCmds.length() > 1)
			tempCmds = tempCmds.substring(1);
		return tempCmds;
	}
	/**
	 * 生成原始命令为在线zg(模式)
	 */
	protected List<byte[]> generateZigbeeDeviceCmdInPattern(Context context,
			List<ProductPatternOperation> ppoList) {
		List<byte[]> cmdList = new ArrayList<byte[]>();
		// 传入参数校验
		if (ppoList == null || ppoList.size() < 1 || context == null) {
			Logger.error(null, "ppoList or context cannot be null");
			return cmdList;
		}
		
			// 对模式控制操作做对于控制单元的分组（根据whid设备）
			Map<String, List<ProductPatternOperation>> groupedOperation = new HashMap<String, List<ProductPatternOperation>>();

			for (ProductPatternOperation ppo : ppoList) {
				String key = ppo.getWhId();
//				Logger.debug(null, "tangl.whid "+key);
				if (groupedOperation.containsKey(key)) {
					groupedOperation.get(key).add(ppo);
				} else {
					groupedOperation.put(key,
							new ArrayList<ProductPatternOperation>());
					groupedOperation.get(key).add(ppo);
				}
			}

			// 对分组后的模式操作按控制单元生成命令
			List<byte[]> result = new ArrayList<byte[]>();
			Set<String> keySet = groupedOperation.keySet();
			if (keySet != null && keySet.size() > 0) {
				Iterator<String> keys = keySet.iterator();
				while (keys.hasNext()) {
					String key = keys.next();
					List<ProductPatternOperation> groupList = groupedOperation
							.get(key);
					String cmdOriginal = generateZigbeeOneGroudOperationToCMD(context,
							groupList);
					String zegbingWhId = AppUtil.getGetwayWhIdByProductWhId(context, groupList.get(0).getWhId());
					if (!TextUtils.isEmpty(cmdOriginal)) {
						cmdList.add(generateCmdWithPrefixAndLength(zegbingWhId, ",", cmdOriginal));
					}else{
						Logger.warn(null, "cmdOriginal cmd is null");
					}
				}
			}
			Logger.debug(null, "send to gateway size:"+cmdList.size());
			return cmdList;
		}

	
	/**
	 * 解析模式设置中Zigbee设备的参数
	 */
	protected Map<String, Object> parseZigbeeDeviceParams(Context context, String funType, ProductPatternOperation ppo) {
		Map<String, Object> params = new HashMap<String, Object>();
		if (ppo == null) return params;
		
		String paramsDesc = ppo.getParaDesc();
		if(ProductConstants.FUN_TYPE_POP_LIGHT.equals(funType) ||
				ProductConstants.FUN_TYPE_CRYSTAL_LIGHT.equals(funType) ||
				ProductConstants.FUN_TYPE_CEILING_LIGHT.equals(funType) ||
				ProductConstants.FUN_TYPE_LIGHT_BELT.equals(funType)) {
			if (ProductConstants.FUN_TYPE_LIGHT_BELT.equals(funType)) {
				Logger.error(null, "paramsDesc : "+paramsDesc);
			}
			if(TextUtils.isEmpty(paramsDesc)) return params;
			try {
				JSONObject jb = new JSONObject(paramsDesc);
				params.put("hue", StringUtils.integerToHexString(jb.getInt("hue")));
				params.put("sat", StringUtils.integerToHexString(jb.getInt("sat")));
				params.put("light", StringUtils.integerToHexString(jb.getInt("light")));
				params.put("time",  StringUtils.integerToHexString(1));
				params.put("src", "0x01");
				params.put("dst", "0x01");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else if(ProductConstants.FUN_TYPE_WIRELESS_SOCKET.equals(funType)||
				ProductConstants.FUN_TYPE_ONE_SWITCH.equals(funType)||
				ProductConstants.FUN_TYPE_WIRELESS_AIRCODITION_OUTLET.equals(funType)) {
			params.put("src", "0x01");
			params.put("dst", "0x01");
		}else if(ProductConstants.FUN_TYPE_WIRELESS_CURTAIN.equals(funType)) {
				params.put("src", "0x01");
				String units = getFunUnitsByFunId(context, ppo.getFunId());
				if(!TextUtils.isEmpty(units)) params.put("dst", units);
				else params.put("dst", "1"); 
		}else if(ProductConstants.FUN_TYPE_ZG_LOCK.equals(funType)){
			params.put("op", "C2");
			params.put("src", "0x01");
			params.put("dst", "0x01");
		}
		
		return params;
	}
	
	/**
	 * 炫彩灯光设备参数
	 * @param funType
	 * @param paramsDesc
	 * @return
	 */
	protected Map<String, Object> parseZigbeeDeviceParams4AutoMode(String funType, String paramsDesc){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("hue", StringUtils.integerToHexString(0));
		params.put("sat", StringUtils.integerToHexString(254));
		params.put("light", StringUtils.integerToHexString(1));
		params.put("step", StringUtils.integerToHexString(45));
		params.put("time", StringUtils.integerToHexString(5));
		params.put("src", "0x01");
		params.put("dst", "0x01");
		return params;
	}
	
	protected Map<String, Object> openColorLight(){
		/* 操作参数  */
		Map<String, Object> params = new HashMap<String, Object>();
		/* 亮度 */
		params.put("light",  StringUtils.integerToHexString(100));
		/* 操作时间  */
		params.put("time",  StringUtils.integerToHexString(1));
		/* 设备目标地址  */
		params.put("src", "0x01");
		/* 亮度关*/
		params.put("lightColor", StringUtils.integerToHexString(0));
		/* 关的时间 */
		params.put("timeColor", StringUtils.integerToHexString(1));
		/* 设备关的目标（白灯）*/
		params.put("dstColor", "0x02");
		
		params.put("dst", "0x01");
		
		return params;
	}
	
	private String getCodeByProductWhId(Context context, String whId){
		ProductRegisterDaoImpl prDaoImpl = new ProductRegisterDaoImpl(context);
		ArrayList<ProductRegister> list = (ArrayList<ProductRegister>) prDaoImpl.find(null,
				"whId = ?", new String[] {whId}, null, null, null, null);
		if(list != null && list.size() > 0) {
			ProductRegister pr = list.get(0);
				return pr.getCode();
		}else {
			Logger.error(null, "error to find product register information");
		}
		return "";
	}
	
	private String getFunUnitsByFunId(Context context, int funId){
		String units = "";
		ProductFunDaoImpl pfdImpl = new ProductFunDaoImpl(context);
		List<ProductFun> list = pfdImpl.find(null, "funId=?", new String[]{ String.valueOf(funId) }, null, null, null, null);
		if (list != null && list.size() > 0) {
			units = list.get(0).getFunUnit();
		}
		Logger.debug(null, "units:"+units); 
		return units;
	}

}
