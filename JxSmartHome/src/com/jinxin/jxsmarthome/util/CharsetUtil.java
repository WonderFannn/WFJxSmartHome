package com.jinxin.jxsmarthome.util;

import java.io.UnsupportedEncodingException;

public class CharsetUtil {

	
	public static String changeCharset(String str, String newCharset) throws UnsupportedEncodingException {
		if (str != null) {
			byte[] bs = str.getBytes();
			return new String(bs, newCharset);
		}
		return null;
	}

}
