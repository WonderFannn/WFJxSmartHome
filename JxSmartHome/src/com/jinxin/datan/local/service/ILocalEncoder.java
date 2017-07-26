package com.jinxin.datan.local.service;

/**
 * 本地通信接口
 * @author  TangLong
 * @company 金鑫智慧
 * @param <T>
 */
public interface ILocalEncoder<T> {
	
	/**
	 * 信息编码
	 * @return 编码后的信息
	 */
	public String encode();
	
	/**
	 * 信息解码
	 * @return	解码后的信息
	 */	
	public String decode();

}
