//#define USE_PHONE_DIR
//only for blackberry




//
//use system property to get music directory

//#define USE_SYS_TIME_DISPLAY_WORDS




//#define ONLINE_PLAY_WAIT_SOME_TIME



//#define CHECK_TIME_OUT_WHEN_CONNECT

//#define DEBUG_MEDIA_LENGTH_DIFF //medio size property different with media bytes


//#define DEBUG_DIFF_PLAY_INDEX

//#define SAFE_REPAINT




package com.jinxin.datan.net.util;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class Base64 {

	private static final byte encodingTable[] = { 65, 66, 67, 68, 69, 70, 71,
			72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88,
			89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108,
			109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121,
			122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47 };

	public Base64() {
	}

	// ->encode 编译
	public static String encode(byte data[]) {

		int modulus = data.length % 3;

		byte bytes[];

		if (modulus == 0) {
			bytes = new byte[(4 * data.length) / 3];
		} else {
			bytes = new byte[4 * (data.length / 3 + 1)];
		}
		int dataLength = data.length - modulus;
		int i = 0;

		for (int j = 0; i < dataLength; j += 4) {

			int a1 = data[i] & 0xff;
			int a2 = data[i + 1] & 0xff;
			int a3 = data[i + 2] & 0xff;
			bytes[j] = encodingTable[a1 >>> 2 & 0x3f];
			bytes[j + 1] = encodingTable[(a1 << 4 | a2 >>> 4) & 0x3f];
			bytes[j + 2] = encodingTable[(a2 << 2 | a3 >>> 6) & 0x3f];
			bytes[j + 3] = encodingTable[a3 & 0x3f];
			i += 3;
		}

		switch (modulus) {

		case 1: {// '\001'
			int d1 = data[data.length - 1] & 0xff;
			int b1 = d1 >>> 2 & 0x3f;
			int b2 = d1 << 4 & 0x3f;
			bytes[bytes.length - 4] = encodingTable[b1];
			bytes[bytes.length - 3] = encodingTable[b2];
			bytes[bytes.length - 2] = 61;
			bytes[bytes.length - 1] = 61;
			break;
		}
		case 2: {// '\002'
			int d1 = data[data.length - 2] & 0xff;
			int d2 = data[data.length - 1] & 0xff;
			int b1 = d1 >>> 2 & 0x3f;
			int b2 = (d1 << 4 | d2 >>> 4) & 0x3f;
			int b3 = d2 << 2 & 0x3f;
			bytes[bytes.length - 4] = encodingTable[b1];
			bytes[bytes.length - 3] = encodingTable[b2];
			bytes[bytes.length - 2] = encodingTable[b3];
			bytes[bytes.length - 1] = 61;
			break;
		}
		}

		return new String(bytes);
	}

	
}
