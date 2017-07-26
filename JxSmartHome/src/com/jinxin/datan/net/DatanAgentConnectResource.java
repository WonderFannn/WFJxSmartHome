package com.jinxin.datan.net;
/**
 * 联网资源或地址
 * @author JackeyZhang
 *
 */
public class DatanAgentConnectResource {

	/**
	 * 访问路径
	 */
	public static final String HTTP_ACCESSPATH = "http://www.beonehome.com:6100/smarthome/mobile/service";	// 云平台
	public static final String HTTP_ICON_PATH = "http://www.beonehome.com:6100/smarthome/";
//	public static final String HTTP_ACCESSPATH = "http://192.168.60.105:8080/smarthome1/mobile/service";
//	public static final String HTTP_ACCESSPATH = "http://192.168.60.149:8080/smarthomeOld/mobile/service";
//	public static final String HTTP_ACCESSPATH = "http://192.168.60.149:8080/smarthomeOld/index";
//	public static final String HTTP_ACCESSPATH = "http://192.168.60.113:9088/smarthome/mobile/service"; 	// 连调
//	public static final String HTTP_ICON_PATH = "http://192.168.60.113:9088/smarthome/";
//	public static final String HTTP_ACCESSPATH = "http://192.168.60.179:9080/smarthome/mobile/service"; 	// 测试
//	public static final String HTTP_ICON_PATH = "http://192.168.60.179:9080/smarthome/";
	public static final String HTTP_SEARCHPATH_ALL = HTTP_ICON_PATH+"m-m/music2/";//搜索所有歌曲地址
	public static final String HTTP_SEARCHPATH = HTTP_ICON_PATH +"m-m/music/";//搜索所有歌曲地址
	public static final String HTTP_FEEDBACK_ADD = HTTP_ICON_PATH +"system/feedback/insert";//新增意见反馈地址
	
	public static final String FTP_SERVER = "192.168.60.226";
	public static final int FTP_PORT = 21;
	public static final String FTP_USERNAME = "test";
	public static final String FTP_PASSWORD = "12345678";
	
	public static final String GATEWAY_PATH = HTTP_ICON_PATH +"zigbeeGatewayService/findStatus/";
	// ///////////协议命令//////////////////////////////

	// /服务器返回错误信息编号////////////////////////////
	/**
	 * 界面过期需刷新
	 * 访问权限认证失败
	 */
	public static final String SERVER_ERROR_MSG_0101 = "0101";
	//用户已登录
	public static final String SERVER_ERROR_MSG_0115 = "0115";
	//	00	操作成功
	//	01	网关处于离线
	//	02	处理超时
	//	03	服务端业务处理失败
	//	04	未知异常
	public static final String SERVER_ERROR_MSG_00 = "00";
	public static final String SERVER_ERROR_MSG_01 = "01";
	public static final String SERVER_ERROR_MSG_02 = "02";
	public static final String SERVER_ERROR_MSG_03 = "03";
	public static final String SERVER_ERROR_MSG_04 = "04";
	public static final String SERVER_ERROR_MSG_0404 = "0404";

	// ///////////联网参数/////////////////////////////
	public static final String appURL = "tcp://192.168.60.211:3333";// 联网地址

	public static String wapGateway = "10.0.0.200:80";// 电信端口号
	public final static String HOST = "www.beonehome.com:6300";// （云平台）数据分析服务器地址
	public final static String HOST_ZEGBING = "www.beonehome.com:12307";// （云平台）数据分析服务器地址
	public final static String HOST_TEST = "192.168.60.149:8080/smarthomeOld";// （云平台）数据分析服务器地址
//	public final static String HOST = "192.168.60.7:1000";// （测试）数据分析服务器地址
//	public final static String HOST_ZEGBING = "192.168.60.128:12307";// （测试）数据分析服务器地址
	public static String rootURL = "";// 固定URL部分
	public static boolean isCmWap = false;

	public static final int DEFAULT_CONNECT_TIME_OUT = 10000;
	public static final int DEFAULT_READ_TIME_OUT = 10000;

	public static final String DOWNLOAD_CAMERA_APP = "http://www.beonehome.com:6100/JxSmartMonitor.apk";
	public static final String DOWNLOAD_DOORBELL_APP = "http://www.beonehome.com:6100/JxSmartDoorbell_V2.1.5.apk";
	public static final String DOWNLOAD_KIT_APP = "http://www.beonehome.com:6100/JxSmartKit_V1.0.apk";
}
