package com.jinxin.datan.local.util;

import java.util.Random;

import com.jinxin.jxsmarthome.cmd.Constants;

/**
 * 本地命令常用的字符串操作
 * @author  TangLong
 * @company 金鑫智慧
 */
public class StringUtil {
	/**
	 * 得到随机的序列码
	 * @param len	随机码的长度
	 * @return		生成的随机码
	 */
	public static String getRandomSerialString(int len) {
		int serialNum = getRandomSerialNumber();
		String serialStr = String.valueOf(serialNum);
		
		while(serialStr.length() < len) {
			serialStr = "0" + serialStr;
		}
		
		return serialStr;
	}
	
	/**
	 * 将int转换为固定长度的字符串
	 * @param i		int值
	 * @param len	字符串长度
	 * @return		转换后的字符串
	 */
	public static String getFixedLengthStrFromInt(int i, int len) {
		String str = Integer.toString(i);
		if(str.length() > len) {
			return str.substring(0, len);
		}
		while(str.length() < len) {
			str = "0" + str;
		}
		return str;
	}
	
	/**
	 * 得到随机的序列码
	 */
	public static int getRandomSerialNumber() {
		return new Random().nextInt(Constants.LOCAL_SERIAL_MAXNUM);
	}
	
	/**
	 * 得到字符全部为0的字符串
	 */
	public static String getZeroStr(int len) {
		StringBuilder sb = new StringBuilder();
		while(sb.length() < len) {
			sb.append("0");
		}
		return sb.toString();
	}
}
