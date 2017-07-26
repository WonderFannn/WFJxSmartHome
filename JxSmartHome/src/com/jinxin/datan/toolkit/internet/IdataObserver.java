package com.jinxin.datan.toolkit.internet;

/**
 * 数据返回接口
 * 
 * @author fuheng
 * 
 */
public interface IdataObserver {
	/**
	 * 联网成功
	 * @param obj 返回的数据组合
	 */
	void callback(Object... obj);
	/**
	 * 联网失败
	 * @param obj 返回的数据组合
	 */
	void onError(Object... obj);
	/**
	 * 联网中止
	 * @param obj 返回的数据组合
	 */
	void onCancel(Object... obj);
	/**
	 * 联网过程变化
	 * @param obj 返回的数据组合
	 */
	void onProcess(Object... obj);

}
