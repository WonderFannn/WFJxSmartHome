package com.jinxin.jxsmarthome.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.test.AndroidTestCase;

import com.jinxin.cd.smarthome.product.service.newcmd.CmdEntry;
import com.jinxin.cd.smarthome.product.service.newcmd.CmdPolicy;
import com.jinxin.cd.smarthome.product.service.newcmd.GenGateway;
import com.jinxin.datan.local.util.StringUtil;
import com.jinxin.jxsmarthome.cmd.Constants;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.util.StringUtils;

public class StringUtilTest extends AndroidTestCase {
	public void testColor() {
		float[] hbs = StringUtils.rgb2hsb(0, 0, 191);
		System.out.println(hbs[0] + "," + hbs[1] + "," + hbs[2] + ",");
	}
	
	public void testGetRandomSerialStr() {
		String test = StringUtil.getRandomSerialString(Constants.LOCAL_CONTENLENGTH);
		System.out.println("-->" + test);
		System.out.println("-->" + StringUtil.getZeroStr(Constants.LOCAL_SERIAL_LENGTH));
		System.out.println("-->" + StringUtil.getFixedLengthStrFromInt(100, 5));
		System.out.println("-->" + StringUtil.getFixedLengthStrFromInt(100000000, 5));
	}
	
	public void test() {
		CmdEntry cmdEntry = new CmdPolicy();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(StaticConstant.OPERATE_INPUT_SET, "input3");
		List<String> list = cmdEntry.getCmd(GenGateway.class, "setInput", "12345", params);
		for(String s : list) {
			System.out.println("-->" + s);
		}
	}
}
