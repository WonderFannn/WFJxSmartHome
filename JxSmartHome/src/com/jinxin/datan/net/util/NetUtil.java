package com.jinxin.datan.net.util;

/**
 * 联网工具类
 * @author JackeyZhang
 *
 */
public class NetUtil {
    /**
     * 添加URL地址字段
     * @param sbufUrl url地址头(http://223.4.244.64:8088/interface.aspx?service_id=1133)
     * @param name
     * @param value
     */
    public static void addURLNumericField(StringBuffer sbufUrl,String name, String value) {
    	if(value==null)
    		return;
        sbufUrl.append('&');
        DataChange.encodeURL(sbufUrl,name, value);
     }
    /**
     * 添加URL地址字段
     * @param sbufUrl url地址头(http://223.4.244.64:8088/interface.aspx?service_id=1133)
     * @param name
     * @param value
     */
    public static void addURLNumericField(StringBuffer sbufUrl,String name, int value) {
        sbufUrl.append('&');
        DataChange.encodeURL(sbufUrl,name, Integer.toString(value));
     }
    
    /**
     * 添加URL地址字段
     * @param sbufUrl url地址头(http://223.4.244.64:8088/interface.aspx?service_id=1133)
     * @param name 
     * @param value
     */
    public static void addURLNumericField(StringBuffer sbufUrl,String name, boolean value) {
    	addURLNumericField(sbufUrl, name, value?1:0);
     }
}
