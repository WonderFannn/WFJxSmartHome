package com.jinxin.jxsmarthome.cmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.db.impl.CustomerDaoImpl;
import com.jinxin.db.impl.CustomerProductDaoImpl;
import com.jinxin.db.impl.FunDetailConfigDaoImpl;
import com.jinxin.jxsmarthome.cmd.entity.Command;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.entity.Customer;
import com.jinxin.jxsmarthome.entity.CustomerProduct;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.FunDetailConfig;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductPatternOperation;
import com.jinxin.jxsmarthome.entity.ProductRegister;
import com.jinxin.jxsmarthome.util.AppUtil;
import com.jinxin.jxsmarthome.util.Logger;

/**
 * 在线命令生成器
 * @author  TangLong
 * @company 金鑫智慧
 */
public class OnlineCmdGenerator extends CmdGenerator {

	@Override
	public List<byte[]> generateCmd(Context context, ProductFun productFun,
			FunDetail funDetail, Map<String, Object> params, String type) {
		return generateCmdWithLenght(generateOriginalCmd(context, productFun, funDetail, params, type));
	}
	
	@Override
	public List<byte[]> generateCmd(Context context, ProductFun productFun,
			FunDetail funDetail, Map<String, Object> params) {
		return generateCmdWithLenght(generateOriginalCmd(context, productFun, funDetail, params));
	}
	
	// 封装zigbee指令
	public List<byte[]> generateCmd2(Context context, ProductFun productFun,
			FunDetail funDetail, Map<String, Object> params) {
		return generateCmd2(context, productFun, funDetail, params, null);
	}
	// 封装zigbee巡检指令
	public List<byte[]> generateCmdForCheckDevice(Context context, ProductFun productFun,
			FunDetail funDetail, Map<String, Object> params,String type) {
		return generateCmd2(context, productFun, funDetail, params, type);
	}
	
	public List<byte[]> generateCmd2(Context context, ProductFun productFun,
			FunDetail funDetail, Map<String, Object> params, String type) {
		List<String> originalCmdList = generateOriginalCmd(context, productFun, funDetail, params, type);
		List<byte[]> cmdList = new ArrayList<byte[]>();
		String zegbingWhId = AppUtil.getGetwayWhIdByProductWhId(context, productFun.getWhId());
		byte[] cmdBytes = generateCmdArrayToOneWithLength(zegbingWhId, ",", originalCmdList);
		if(cmdBytes != null) {
			cmdList.add(cmdBytes);
		}
		return cmdList;
	}
	
	@Deprecated
	@Override
	public List<byte[]> generateProductPatternOperationCmd(Context context,
			ProductFun productFun, FunDetail funDetail,
			Map<String, Object> params) {
		if (productFun == null || funDetail == null || context == null) {
			return null;
		}

		String customerId = null;
		String addr485 = "";
		String funType = "";

		// 获取用户ID
		CustomerDaoImpl cDaoImpl = new CustomerDaoImpl(context);
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
		CustomerProductDaoImpl cpDaoImpl = new CustomerProductDaoImpl(context);
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
				customerId, addr485, null, null, funType, params);
		
		return CommonMethod.createCmdOffline(originalCmd);
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
		Logger.debug(null, "begin generate pattern operation cmd:" + ppoList.size());
		List<Command> commandList = new ArrayList<Command>();
		
		// 将模式中的设备按网关类型（无线网关-zegbing网关/有线网关-普通网关)分组
		Map<String, List<ProductPatternOperation>> cmdMap = new HashMap<String, List<ProductPatternOperation>>();
		for(int i = 0; i < ppoList.size(); i++) {
			ProductRegister pr = getGatwayByWhId(context, ppoList.get(i).getWhId());
			if(pr != null) {
				String sn = pr.getGatewayWhId();
				if(!cmdMap.containsKey(sn)) {
					cmdMap.put(sn, new ArrayList<ProductPatternOperation>());
				}
				cmdMap.get(sn).add(ppoList.get(i));
			}else {
				Logger.error(null, "can not found register information for: " + ppoList.get(i).toString());
			}
		}
		
		// 分组生成命令 
		List<String> zegbingWhIds = getZegbingGatewayWhid(context);
		Logger.debug(null, "zegbingWhId:" + zegbingWhIds.size());
		Set<String> keys = cmdMap.keySet();
		
		Iterator<String> iterator = keys.iterator();
		List<Command> zigCommands = new ArrayList<Command>();
		List<Command> onlineCommands = new ArrayList<Command>();
		while(iterator.hasNext()) {
			String key = iterator.next();
			// Zigbee网关下的设备
			if(zegbingWhIds.contains(key)) {
				List<byte[]> cmdList = generateZigbeeDeviceCmdInPattern(context, cmdMap.get(key));
				Command command = new Command(key, cmdList);
				command.setGatewayRemoteIp(DatanAgentConnectResource.HOST_ZEGBING);
//				commandList.add(command);
				zigCommands.add(command);
				command.setZigbee(true);
			// 普通网关下的设备
			}else {
				List<byte[]> cmdList = CommonMethod.productPatternOperationToCMD_V1(context, cmdMap.get(key), null);
				Command command = new Command(key, cmdList);
				command.setGatewayRemoteIp(DatanAgentConnectResource.HOST);
//				commandList.add(command);
				onlineCommands.add(command);
				command.setZigbee(false);
			}
		}
		commandList.addAll(zigCommands);
		commandList.addAll(onlineCommands);

		Logger.debug(null, "end generate pattern operation cmd:" + commandList.size());
		return commandList;
	}
	
	/**
	 * 获取无线网关的whid
	 */
	public static List<String> getZegbingGatewayWhid(Context context) {
		// 获取无线网关的whid
		List<String> whIds = new ArrayList<String>();
		FunDetailConfigDaoImpl fdcDao = new FunDetailConfigDaoImpl(context);
		List<FunDetailConfig> fdcList = fdcDao.find(null, "funType=?",
				new String[] { "G102" }, null, null, null, null);
		if (fdcList.size() > 0) {
			for(int i = 0; i < fdcList.size(); i++) {
				whIds.add(fdcList.get(i).getWhId());
			}
		}
		return whIds;
	}
}
