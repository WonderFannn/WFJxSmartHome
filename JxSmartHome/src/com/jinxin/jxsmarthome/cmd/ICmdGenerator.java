package com.jinxin.jxsmarthome.cmd;

import java.util.List;
import java.util.Map;

import android.content.Context;

import com.jinxin.jxsmarthome.cmd.entity.Command;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductPatternOperation;

/**
 * 命令生成器
 * @author  TangLong
 * @company 金鑫智慧
 */
public interface ICmdGenerator {
	/**
	 * 根据传入的参数生成命令(无操作类型)
	 * @param context		Context对象
	 * @param productFun	ProductFun对象
	 * @param funDetail		FunDetail对象
	 * @param params		生成命令需要的参数
	 * @return				生成的命令
	 */
	public List<byte[]> generateCmd(Context context, ProductFun productFun, FunDetail funDetail, 
			Map<String, Object> params);
	
	/**
	 * 根据传入的参数生成命令(有操作类型)
	 * @param context		Context对象
	 * @param productFun	ProductFun对象
	 * @param funDetail		FunDetail对象
	 * @param params		生成命令需要的参数
	 * @param type			操作类型
	 * @return				生成的命令
	 */
	public List<byte[]> generateCmd(Context context, ProductFun productFun, FunDetail funDetail, 
			Map<String, Object> params, String type);
	
	/**
	 * 根据传入的参数生成模式控制的命令
	 * @param context		Context对象
	 * @param productFun	ProductFun对象
	 * @param funDetail		FunDetail对象
	 * @param params		生成命令需要的参数
	 * @return				生成的命令
	 */
	public List<byte[]> generateProductPatternOperationCmd(Context context, ProductFun productFun, 
			FunDetail funDetail, Map<String, Object> params);
	
	/**
	 * 根据传入的参数生成模式控制的命令
	 * @param context		Context对象
	 * @param ppoList		模式列表
	 * @param params		生成命令需要的参数
	 * @return				生成的命令
	 */
	public List<byte[]> generateProductPatternOperationCmd(Context context,
			List<ProductPatternOperation> ppoList, Map<String, Object> params);
	
	/**
	 * 根据传入的参数生成模式控制的命令（根据不同类型的网关【普通网关，zegbing网关】生成对应的命令）
	 * @param context		Context对象
	 * @param ppoList		模式列表
	 * @param params		生成命令需要的参数
	 * @return				生成的命令
	 */
	public List<Command> generateProductPatternOperationCommand(Context context,
			List<ProductPatternOperation> ppoList, Map<String, Object> params);
}
