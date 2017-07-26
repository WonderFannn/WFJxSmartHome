package com.jinxin.jxsmarthome.constant;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.content.Context;
import com.jinxin.jxsmarthome.util.Logger;

import com.jinxin.db.impl.CustomerDaoImpl;
import com.jinxin.jxsmarthome.entity.Customer;
import com.jinxin.jxsmarthome.ui.adapter.data.Device;
import com.jinxin.jxsmarthome.util.AppUtil;
import com.jinxin.jxsmarthome.util.Logger;

public class CommonMethodForZGDetect {

	// 参照OnlineCmdGenerator generateCmd2
	public static List<byte[]> productDetectToZGCMD(Context context, Device device, Map<String, Object> params,
			String type) {
		List<String> originalCmdList = generateOriginalCmd(context, device, params, type);
		List<byte[]> cmdList = new ArrayList<byte[]>();
		String zegbingWhId = AppUtil.getGetwayWhIdByProductWhId(context, device.getGatewayId());
		byte[] cmdBytes = generateCmdArrayToOneWithLength(zegbingWhId, ",", originalCmdList);
		if (cmdBytes != null) {
			cmdList.add(cmdBytes);
		}
		return cmdList;
	}

	/**
	 * 为原始命令数组合并为一条并增加长度和头信息
	 */
	private static byte[] generateCmdArrayToOneWithLength(String prefix, String splitor, List<String> cmdArr) {
		if (cmdArr == null || cmdArr.isEmpty())
			return null;
		StringBuffer sb = new StringBuffer(prefix);
		sb.append(splitor);
		for (String cmd : cmdArr) {
			sb.append(cmd);
			sb.append(splitor);
		}
		// 组拼完成后，删除最后一个字符(",")
		sb.deleteCharAt(sb.length() - 1);
		Logger.debug("Huang", sb.toString());
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
	 * 生成原始命令(有操作类型)
	 */
	private static List<String> generateOriginalCmd(Context context, Device device, Map<String, Object> params,
			String type) {
		if (device == null || context == null) {
			Logger.error(null, "parameter error: productFun or funDetail is null");
			return Collections.emptyList();
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
		funType = device.getFunType();

		// 获取addr485
		/*CustomerProductDaoImpl cpDaoImpl = new CustomerProductDaoImpl(context);
		List<CustomerProduct> cpList = cpDaoImpl.find(null, "whId=?", new String[] { device.getWhId() }, null, null,
				null, null);
		if (cpList != null && cpList.size() > 0) {
			CustomerProduct cp = cpList.get(0);
			if (cp != null) {
				addr485 = cp.getAddress485();
			}
		}*/
		addr485 = device.getAddress485();

		// 生成原始命令
		List<String> originalCmd = CommonMethod.createCmdWithNotAccount(customerId, addr485, type, null, funType,
				params);
		return originalCmd;
	}

}
