package com.jinxin.jxsmarthome.util;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;  
import java.security.NoSuchAlgorithmException;  

import android.text.TextUtils;
  
public class MD5Util {  
  
    
	// 全局数组
    private final static String[] strDigits = { "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

    public MD5Util() {
    }
    
    // 返回形式为数字跟字符串
    private static String byteToArrayString(byte bByte) {
        int iRet = bByte;
        // System.out.println("iRet="+iRet);
        if (iRet < 0) {
            iRet += 256;
        }
        int iD1 = iRet / 16;
        int iD2 = iRet % 16;
        return strDigits[iD1] + strDigits[iD2];
    }
    
    // 返回形式只为数字
    private static String byteToNum(byte bByte) {
        int iRet = bByte;
        System.out.println("iRet1=" + iRet);
        if (iRet < 0) {
            iRet += 256;
        }
        return String.valueOf(iRet);
    }

    // 转换字节数组为16进制字串
    private static String byteToString(byte[] bByte) {
        StringBuffer sBuffer = new StringBuffer();
        for (int i = 0; i < bByte.length; i++) {
            sBuffer.append(byteToArrayString(bByte[i]));
        }
        return sBuffer.toString();
    }
    
    public static String GetMD5Code(String strObj) {
        String resultString = null;
        try {
            resultString = new String(strObj);
            MessageDigest md = MessageDigest.getInstance("MD5");
            // md.digest() 该函数返回值为存放哈希值结果的byte数组
            resultString = byteToString(md.digest(strObj.getBytes()));
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return resultString;
    }
    
    /** 
     * 对字符串进行URLDecoder.encode(strEncoding)编码 
     * @param String src 要进行编码的字符串 
     *  
     * @return  String 进行编码后的字符串 
     */  
    public static String getURLEncode(String src)  
    {  
     String requestValue="";  
     try{  
       
     requestValue = URLEncoder.encode(src);  
     }  
     catch(Exception e){  
       e.printStackTrace();  
     }  
       
     return requestValue;  
    } 
    
    /** 
     * 对字符串进行URLDecoder.decode(strEncoding)解码 
     * @param String src 要进行解码的字符串 
     *  
     * @return  String 进行解码后的字符串 
     */  
    public static String getURLDecoderdecode(String src)  
    {     
     String requestValue="";  
     try{  
       
     requestValue = URLDecoder.decode(src);  
     }  
     catch(Exception e){  
       e.printStackTrace();  
     }  
       
     return requestValue;  
    } 
    
	public static String Md5(String plainText) {
		String md5Str ="";
		if (TextUtils.isEmpty(plainText)) {
			return md5Str;
		}
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();

			int i;

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}

			System.out.println("result: " + buf.toString());// 32位的加密
//			System.out.println("result: " + buf.toString().substring(8, 24));// 16位的加密
			md5Str = buf.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return md5Str;
	} 
    
}
