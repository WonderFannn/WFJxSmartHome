package com.jinxin.jxsmarthome.cmd;

import java.util.List;
import java.util.Map;

import android.content.Context;

import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductPatternOperation;
import com.jinxin.jxsmarthome.util.Logger;

/**
 * 离线命令管理器
 * @author  TangLong
 * @company 金鑫智慧
 */
public class OfflineCmdManager {
	/**
	 * 生成并发送离线命令
	 * @param context		Context对象
	 * @param productFun	ProductFun对象
	 * @param funDetail		FunDetail对象
	 * @param params		生成命令需要的参数
	 */
	public static void generateCmdAndSend(Context context, ProductFun productFun, FunDetail funDetail, 
			Map<String, Object> params) {
		// 生成离线命令
		ICmdGenerator offlineCmdGenerator = new OfflineCmdGenerator();
		List<byte[]> cmdList = offlineCmdGenerator.generateCmd(context,
			productFun, funDetail, params);
		
		if(cmdList != null && cmdList.size() > 0) {
			Logger.debug("offline", "generate cmd size:" + cmdList.size());
			Logger.debug("offline", "generate cmd size:" + cmdList.get(0));
			
			ICmdSender offlineCmdSender = new OfflineCmdSenderLong(context, cmdList, true);
			offlineCmdSender.send();
		}
	}
	
	/**
	 * 生成并发送离线命令
	 * @param context		Context对象
	 * @param ppoList		模式列表
	 * @param params		生成命令需要的参数
	 */
	public static void generateCmdAndSend(Context context,
			List<ProductPatternOperation> ppoList, Map<String, Object> params) {
		// 生成离线命令
		ICmdGenerator cmdGenerator = new OfflineCmdGenerator();
		List<byte[]> listCmd = cmdGenerator.generateProductPatternOperationCmd(context, ppoList, null);
		
		Logger.debug("offline", "generate cmd size:" + ppoList.size());
		
		if(listCmd != null) {
			// 发送离线命令
			ICmdSender cmdSender = new OfflineCmdSenderLong(context, listCmd);
			cmdSender.send();
		}
	}
}
