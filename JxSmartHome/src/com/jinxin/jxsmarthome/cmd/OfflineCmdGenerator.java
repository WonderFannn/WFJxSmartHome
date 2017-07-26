package com.jinxin.jxsmarthome.cmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.text.TextUtils;

import com.jinxin.db.impl.CustomerDaoImpl;
import com.jinxin.db.impl.CustomerProductDaoImpl;
import com.jinxin.db.impl.FunDetailConfigDaoImpl;
import com.jinxin.db.impl.OffLineConetenDaoImpl;
import com.jinxin.jxsmarthome.cmd.entity.Command;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.entity.Customer;
import com.jinxin.jxsmarthome.entity.CustomerProduct;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.FunDetailConfig;
import com.jinxin.jxsmarthome.entity.OffLineContent;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductPatternOperation;
import com.jinxin.jxsmarthome.entity.ProductRegister;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.Logger;

/**
 * 离线命令生成器
 * @author  TangLong
 * @company 金鑫智慧
 */
public class OfflineCmdGenerator extends CmdGenerator {

	@Override
	public List<byte[]> generateCmd(Context context, ProductFun productFun,
			FunDetail funDetail, Map<String, Object> params) {
		return CommonMethod.createCmdOffline((generateOriginalCmd(context, productFun, funDetail, params)));
	}
	
	@Override
	public List<byte[]> generateCmd(Context context, ProductFun productFun,
			FunDetail funDetail, Map<String, Object> params, String type) {
		return CommonMethod.createCmdOffline((generateOriginalCmd(context, productFun, funDetail, params, type)));
	}
	
	public List<byte[]> generateCmd2(Context context, ProductFun productFun,
			FunDetail funDetail, Map<String, Object> params) {
		return CommonMethod.createCmdOffline2((generateOriginalCmd(context, productFun, funDetail, params)));
	}
	
	public List<byte[]> generateCmd2(Context context, ProductFun productFun,
			FunDetail funDetail, Map<String, Object> params, String type) {
		return CommonMethod.createCmdOffline2((generateOriginalCmd(context, productFun, funDetail, params, type)));
	}
	
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
		
		// 传入参数校验
		if (ppoList == null || ppoList.size() < 1 || context == null) {
			Logger.error(null, "ppoList or context cannot be null");
			return null;
		}

		// 获取customerid
		String customerId = null;
		CustomerDaoImpl cDaoImpl = new CustomerDaoImpl(context);
		List<Customer> cList = cDaoImpl.find();
		if (cList != null && cList.size() > 0) {
			Customer c = cList.get(0);
			if (c != null) {
				customerId = c.getCustomerId();
			}
		}
		if (CommUtil.isEmpty(customerId)) {
			Logger.error(null, "customerId cannot be null");
			return null;
		}

		// 对模式控制操作做对于控制单元的分组（根据whid）
		Map<String, List<ProductPatternOperation>> groupedOperation = new HashMap<String, List<ProductPatternOperation>>();

		for (ProductPatternOperation ppo : ppoList) {
			String key = ppo.getWhId();
			if (groupedOperation.containsKey(key)) {
				groupedOperation.get(key).add(ppo);
			} else {
				groupedOperation.put(key,
						new ArrayList<ProductPatternOperation>());
				groupedOperation.get(key).add(ppo);
			}
		}

		// 对分组后的模式操作按控制单元生成命令
		StringBuilder sb = new StringBuilder();
		Set<String> keySet = groupedOperation.keySet(); 
		if (keySet != null && keySet.size() > 0) {
			Iterator<String> keys = keySet.iterator();
			while (keys.hasNext()) {
				String key = keys.next();
				List<ProductPatternOperation> groupList = groupedOperation
						.get(key);
				String cmdOriginal = CommonMethod.generateOneGroudOperationToCMD(context,
						groupList);
				sb.append(cmdOriginal);
			}
		}

		String cmdStr = sb.toString();
		
		List<String> cmds = new ArrayList<String>();
		if(!CommUtil.isEmpty(cmdStr)) {//zj讲解:离线指令不能够合并成一条，再次拆分
			String[] cmdStrArr = cmdStr.split("\\|");
			if(cmdStrArr != null && cmdStrArr.length > 0) {
				for(int i = 0; i < cmdStrArr.length; i++) {
					Logger.debug("offline", cmdStrArr[i]);
					cmds.add(cmdStrArr[i]);
				}
			}
		}
		
		return CommonMethod.createCmdOffline(cmds);
	}
	@Deprecated
	public List<byte[]> generateProductPatternOperationCmd2(Context context,
			List<ProductPatternOperation> ppoList, Map<String, Object> params) {
		
		// 传入参数校验
		if (ppoList == null || ppoList.size() < 1 || context == null) {
			Logger.error(null, "ppoList or context cannot be null");
			return null;
		}

		// 获取customerid
		String customerId = null;
		CustomerDaoImpl cDaoImpl = new CustomerDaoImpl(context);
		List<Customer> cList = cDaoImpl.find();
		if (cList != null && cList.size() > 0) {
			Customer c = cList.get(0);
			if (c != null) {
				customerId = c.getCustomerId();
			}
		}
		if (CommUtil.isEmpty(customerId)) {
			Logger.error(null, "customerId cannot be null");
			return null;
		}

		// 对模式控制操作做对于控制单元的分组（根据whid）
		Map<String, List<ProductPatternOperation>> groupedOperation = new HashMap<String, List<ProductPatternOperation>>();

		for (ProductPatternOperation ppo : ppoList) {
			String key = ppo.getWhId();
			Logger.debug("tangl.whid", key);
			if (groupedOperation.containsKey(key)) {
				groupedOperation.get(key).add(ppo);
			} else {
				groupedOperation.put(key,
						new ArrayList<ProductPatternOperation>());
				groupedOperation.get(key).add(ppo);
			}
		}

		// 对分组后的模式操作按控制单元生成命令
		StringBuilder sb = new StringBuilder();
		Set<String> keySet = groupedOperation.keySet(); 
		if (keySet != null && keySet.size() > 0) {
			Iterator<String> keys = keySet.iterator();
			while (keys.hasNext()) {
				String key = keys.next();
				List<ProductPatternOperation> groupList = groupedOperation
						.get(key);
				String cmdOriginal = CommonMethod.generateOneGroudOperationToCMD(context,
						groupList);
				sb.append(cmdOriginal);
			}
		}

		String cmdStr = sb.toString();
		
		List<String> cmds = new ArrayList<String>();
		if(!CommUtil.isEmpty(cmdStr)) {
			String[] cmdStrArr = cmdStr.split("\\|");
			if(cmdStrArr != null && cmdStrArr.length > 0) {
				for(int i = 0; i < cmdStrArr.length; i++) {
					Logger.debug("offline", cmdStrArr[i]);
					if(!TextUtils.isEmpty(cmdStrArr[i])) cmds.add(cmdStrArr[i]);
				}
			}
		}
		
		return CommonMethod.createCmdOffline2(cmds);
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
				List<byte[]> cmdList = generateZigbeeDeviceCmdInPatternForOffline(context, cmdMap.get(key));
				Command command = new Command(key, cmdList);
				//离线模式下，通过mac查找对应的网关IP
				ProductRegister pr = getGatwayByWhId(context, key);
				String mac = "";
				if (pr != null) {
					mac = pr.getMac();
				}
				String zegbingHost = getGatewayLocalIpBySn(context, mac);
				command.setGatewayLocalIp(zegbingHost + ":3333");
//				commandList.add(command);
				zigCommands.add(command);
				command.setZigbee(true);
			// 普通网关下的设备
			}else {
				List<byte[]> cmdList = generateProductPatternOperationCmd(context, cmdMap.get(key), null);
				Command command = new Command(key, cmdList);
				String zegbingHost = getGatewayLocalIpBySn(context, key);
				command.setGatewayLocalIp(zegbingHost + ":3333");
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
	 * 获取网关序列号对应的本地ip
	 */
	public static String getGatewayLocalIpBySn(Context context, String sn) {
		OffLineConetenDaoImpl dao = new OffLineConetenDaoImpl(context);
		List<OffLineContent> ocList = dao.find(null, "sn=?", new String[]{sn}, null, null, null, null);
		if(ocList != null && ocList.size() > 0) {
			OffLineContent oc = ocList.get(0);
			return oc == null ? "" : oc.getIp();
		}
		return "";
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
