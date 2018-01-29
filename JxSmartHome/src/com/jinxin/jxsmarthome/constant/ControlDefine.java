package com.jinxin.jxsmarthome.constant;

/**
 * 控制常量定义类
 * 
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class ControlDefine {
	/**
	 * 设备操作开
	 */
	public final static String CONNECTION_SUCCESS = "0000";
	// 获取数据失败
	public final static String CONNECTION_ERROR = "0001";
	/**
	 * 设备操作开
	 */
	// public final static String CONTROL_OPERATION_OPEN = "open";
	/**
	 * 设备操作关
	 */
	// public final static String CONTROL_OPERATION_CLOSE = "close";
	/**
	 * 默认更新时间
	 */
	public final static String DEFAULT_UPDATE_TIME = "20130808122323";
	/**
	 * 定时空返回更新时间
	 */
	public final static String DEFAULT_UPDATE_TIME_1990 = "19900808122323";
	/**
	 * 默认九宫格锁屏延时
	 */
	public static final String DEFAULT_NINE_DELAY = "30 秒";
	/**
	 * DOMAIN编码
	 */
	public final static String DM_HOME = "P000";
	// 其它Android系统终端设备
	public final static String DM_ANDROID_OTHER = "A000";
	// Android系统手机
	public final static String DM_ANDROID_PHONE = "A001";
	// Android系统平板电脑
	public final static String DM_ANDROID_PAD = "A002";

	/** 业务编码 ***************************/
	public final static String BS_VER = "1.0";
	public static final String BS_ACCOUNT_MANAGER = "B000";
	public static final String BS_USER_MANAGER = "B001";
	public static final String BS_USER_PATTERN = "B002";
	public static final String BS_USER_PATTERN_CMD = "B003";
	public static final String BS_USER_PRODUCT_MANAGER = "B004";
	public static final String BS_USER_PATTERN_MANAGER = "B005";
	public static final String BS_FUN_MANAGER = "B006";
	public static final String BS_UPGRADE_MANAGER = "B007";
	public static final String BS_FUN_CONFIG = "B008";
	public static final String BS_PRODUCT_STATE = "B010";// 设备状态列表
	public static final String BS_CUSTOMER_TIMER = "B012";// 定时任务列表
	public static final String BS_CUSTOMER_TIMER_OPERATION = "B013";// 定时任务操作列表
	public static final String BS_CUSTOMER_AREA_OPERATION = "B015";// 获取用户区域分组
	public static final String BS_CUSTOMER_PRODUCT_AREA_OPERATION = "B016";// 获取设备区域分组
	public static final String BS_CUSTOMER_DATA_SYNC_OPERATION = "B014";// 获取需要删除的数据
	public static final String BS_PRODUCT_REMOTE_CONFIG_OPERATION = "B019";// 获取设备区域分组
	public static final String BS_PRODUCT_REMOTE_CONFIG_FUN_OPERATION = "B020";// 获取需要删除的数据
	public static final String BS_ADD_SUGGEST_FEEDBACK = "B021";// 新增意见反馈信息
	public static final String BS_CLOUD_MUSIC_SINGER = "B024";// 音乐查询
	public static final String BS_CLOUD_MUSIC_STORE = "B025";// 音乐查询
	public static final String BS_DOOR_CONTACT_STATUS = "B027";// 门磁状态
	public static final String BS_DEVICE_TYPE = "B029";// 遥控设备类型
	public static final String BS_DEVICE_BRANDS = "B030";// 遥控设备品牌
	public static final String BS_OEM_VERSION = "B032";// OEM
	public static final String BS_ADD_CONNECTION_OPERATION = "B031";// 新增设备联动
	public static final String BS_ADD_GEO_LOCATION = "B033";// 新增设备联动
	public static final String INFRARED_TRANSPORT_TEST_BIPCODE = "B037";
	// 更新音乐播放列表
	public static final String BS_UPDATE_MUSIC_LIST = "B009";
	// 同步音乐播放列表
	public static final String BS_SYNC_MUSIC_LIST = "B009";
	// 云设置同步和更新
	public static final String BS_SYNC_CLOUD_SETTING = "B011";
	// 为语音合成保存输入源
	public static final String BS_SAVE_INPUT_VOICE = "B009";
	// 语音合成识别
	public static final String BS_SET_VOICE_IDENTIFY = "B017";
	// 网关注册信息更新
	public static final String BS_UPDATE_PRODUCT_REGISTER = "B018";
	//语音合成类型
	public static final String BS_UPDATE_PRODUCT_VOICE = "B022";
	//新增语音配置
	public static final String BS_UPDATE_PRODUCT_VOICE_CONFIGER = "B023";
	//语音转文字
	public static final String BS_UPDATE_VOICE_TO_TEXT = "B026";
	//唤醒词查看
	public static final String BS_WAKE_WORD_BROWSE = "V019";
	

	/** 交易编码 ***************************/
	// 账号登陆
	
	public static final String TRD_ACCOUNT_LOGIN = "T000";
	// 注销账户
	public static final String TRD_CANCELLATION_ACCOUNT = "T001";
	// 获取密保信息
	public static final String TRD_SECRET_SECURITY = "T201";
	// 客户模式列表
	public static final String TRD_CUSTOMER_PATTERN_LIST = "T203";
	// 模式新增
	public static final String TRD_ADD_CUSTOMER_PATTERN = "T101";
	// 密码修改
	public static final String TRD_CHANGE_PASSWORD = "T102";
	// 模式删除
	public static final String TRD_CUSTOMER_PATTERN_DELETE = "T103";
	// 用户详情
	public static final String TRD_USER_DETAIL = "T204";
	// 用户设备列表
	public static final String TRD_USER_DEVICE = "T901";
	// 获取客户设备操作命令
	public static final String TRD_USER_DEVICE_COMMAND = "T902";
	// 更新音乐播放列表
	public static final String TRD_UPDATE_MUSIC_LIST = "T901";
	// 同步音乐播放列表
	public static final String TRD_SYNC_MUSIC_LIST = "T301";
	// 修改密保问题
	public static final String TRD_USER_SAFE_QUESITON = "T904";
	// 用户详情
	public static final String TRD_USER_INFO = "T903";
	// 修改用户昵称
	public static final String TRD_USER_NICKY_NAME = "T905";
	// 更新（获取）云同步设置
	public static final String TRD_GET_USER_CLOUND_SETTING = "T203";
	// 同步（设置）云同步设置
	public static final String TRD_SYNC_USER_CLOUND_SETTING = "T901";
	// 获取用户区域分组
	public static final String TRD_SYNC_CUSTOMER_AREA = "T901";
	// 获取设备区域分组
	public static final String TRD_SYNC_CUSTOMER_PRODUCT_AREA = "T203";
	// 获取需要删除的数据
	public static final String TRD_CUSTOMER_DATA_SYNC = "T203";
	// 为语音合成保存输入源
	public static final String TRD_SAVE_INPUT_VOICE = "T902";
	// 语音合成识别
	public static final String TRD_SET_VOICE_IDENTIFY = "T901";
	// 网关注册信息更新
	public static final String TRD_UPDATE_PRODUCT_REGISTER = "T901";
	public static final String TRD_PRODUCT_REMOTE_CONFIG_OPERATION = "T901";
	public static final String TRD_PRODUCT_REMOTE_CONFIG_FUN_OPERATION = "T901";
	//云音乐列表
	public static final String TRD_CLOUD_MUSIC = "T202";
	//添加云音乐收藏
	public static final String TRD_CLOUD_MUSIC_ADD_FAV = "T101";
	public static final String TRD_CLOUD_MUSIC_DELETE_FAV = "T103";
	//唤醒词查看
	public static final String TRD_WAKE_WORD_BROWSE = "T203";

	/** APP类型 *********************************/
	public static final Integer APP_TYPE_ANDROID_PHONE = 1;
	public static final Integer APP_TYPE_ANDROID_PAD = 2;
	public static final Integer APP_TYPE_IPHONE = 3;
	public static final Integer APP_TYPE_IPAD = 4;

	/**
	 * 测试交易
	 */
	public final static String TEST_TRUE = "1";
	public final static String TEST_FALSE = "0";

	/**
	 * 测试交易
	 */
	public final static String ACTION_REQUEST = "0";
	public final static String ACTION_RESPONSE = "1";

	/**
	 * 应答代码
	 */
	public final static String RC_SUCCESS = "0000";

	/** 存储配置表key *********************/

	public final static String KEY_ACCOUNT = "curr_ur";
	public final static String KEY_SECRETKEY = "secretKey";
	public final static String KEY_NICKNAME = "nickyName";
	public final static String KEY_SUNACCOUNT = "sunAccount";
	public final static String KEY_LAST_ACCOUNT = "lastAccount";
	public final static String KEY_ISLOADING = "isLoading";// 是否登录过应用
	public final static String KEY_PASSWORD = "share_key";
	public final static String KEY_OFF_LINE_MODE = "offLineMode";// 是否离线模式
	public final static String KEY_ENABLE_OFFLINE_MODE = "offlineSwitch";// 打开离线切换
	public final static String KEY_WAKE_MODE = "keyWakeMode";// 打开自定义唤醒
	public final static String KEY_VEIN_ONLY = "veinOnly";// 只进行静脉纹登录
	public final static String KEY_VOICE_SEND_SWITCH = "sendSwitch";// 语音自动发送 or 点击发送
	public final static String KEY_APPLY_SUB_SWITCH = "applyall";// 应用到子账号
	public final static String KEY_NOT_FIRST_LOGING = "notFirstLogin";// 是否第一次登录
	public final static String KEY_ACCOUNT_HISTORY = "accountList";// 账户登录历史
	public final static String KEY_WARN_SHAKE = "warnShake";// 报警震动
	public final static String KEY_DOORBELL_LIST = "doorbellList";// 门铃序列号
	public final static String KEY_MONITOR_LIST = "monitorList";// 监控序列号
	public final static String KEY_LOGIN = "login";// 登录账号
	public final static String KEY_NOTICE_ON_OFF = "noticeOnOrOff";// 分时段报警开关
	public final static String KEY_DB_NUM = "dbNum";// 数据库版本号
	public final static String KEY_FIRST_INSTALL = "firstInstall";// 是否首次安装
	public final static String KEY_VOICE_LOCK = "voice_lock";// 声纹锁开关
	public final static String KEY_CURR_MUSIC_VERSION = "curr_music_version";// 当前功放版本
 
	public final static String CURR_CITY = "curr_city";// 当前功放版本
 
	public final static String KEY_MOST_USE_PATTERN = "most_use_pattern";// 常用模式ID
 
	
	public final static String KEY_GATEWAYIP="gatewayIP"; //更改的大网关ip
	public final static String KEY_GATEWAYPORT="gatewayPort"; //更改的大网关端口
	/**
	 * 用户设备更新key
	 */
	public final static String KEY_CUSTOMER_PRODUCT_LIST = "key_customer_product_list";
	/**
	 * 用户设备指令更新key
	 */
	public final static String KEY_CUSTOMER_PRODUCT_CMD_LIST = "key_customer_product_cmd_list";
	/**
	 * 客户详情更新key
	 */
	public final static String KEY_CUSTOMER_DETAIL_LIST = "key_customer_detail_list";
	/**
	 * 用户详情更新key
	 */
	public final static String KEY_USER_DETAIL_LIST = "key_user_detail_list";
	/**
	 * 客户模式列表更新key
	 */
	public final static String KEY_CUSTOMER_PATTERN_LIST = "key_customer_pattern_list";
	/**
	 * 客户模式指令列表更新key
	 */
	public final static String KEY_CUSTOMER_PATTERN_CMD_LIST = "KEY_CUSTOMER_PATTERN_CMD_LIST";
	/**
	 * 产品功能列表更新key
	 */
	public final static String KEY_PRODUCT_FUN_LIST = "key_product_fun_list";
	/**
	 * 产品功能详情列表更新key
	 */
	public final static String KEY_FUN_DETAIL_LIST = "key_fun_detail_list";
	/**
	 * 产品功能明显配置列表更新key
	 */
	public final static String KEY_PRODUCT_FUN_DETAIL_LIST = "key_product_fun_detail_list";
	/**
	 * 设备状态表更新key
	 */
	public final static String KEY_PRODUCT_STATE_LIST = "key_product_stae_list";
	/**
	 * 产品模式列表更新key
	 */
	public final static String KEY_PRODUCT_PATTERN_OPERATION_LIST = "key_product_pattern_operation_list";
	/**
	 * 灯光颜色key
	 */
	public final static String KEY_LIGHT_COLOR = "key_light_color";
	/**
	 * 灯光颜色 命令字符串
	 */
	public final static String KEY_LIGHT_COLOR_STRING = "key_light_color_string";
	/**
	 * 灯光亮度进度值key
	 */
	public final static String KEY_LIGHT_BRIGHT = "key_light_bright";
	/**
	 * 子账号列表key
	 */
	public final static String KEY_SUB_ACCOUNT = "key_sub_account";
	/**
	 * 灯光亮度A
	 */
	public final static String KEY_LIGHT_A = "key_light_a";
	/**
	 * 灯光亮度R
	 */
	public final static String KEY_LIGHT_R = "key_light_r";
	/**
	 * 灯光亮度G
	 */
	public final static String KEY_LIGHT_G = "key_light_g";
	/**
	 * 灯光亮度B
	 */
	public final static String KEY_LIGHT_B = "key_light_b";
	/**
	 * 彩灯ID KEY
	 */
	public final static String KEY_COLOR_LIGHT_ID = "key_color_light_id";
	/**
	 * 彩灯List
	 */
	public final static String KEY_COLOR_LIGHT_LIST = "key_color_light_list";

	public final static String KEY_CLOUD_SETTING = "key_cloud_setting";
	/**
	 * 定时任务列表
	 */
	public final static String KEY_CUSTOMER_TIMER_LIST = "key_customer_timer_list";
	/**
	 * 意见反馈列表
	 */
	public final static String KEY_FEEDBACK_LIST = "key_feedback_list";
	/**
	 * 定时任务详细操作列表
	 */
	public final static String KEY_TIMER_TASK_OPERATION_LIST = "key_timer_task_operation_list";

	/**
	 * 用户区域分组
	 */
	public final static String KEY_CUSTOMER_AREA = "key_customer_area";

	/**
	 * 设备区域分组
	 */
	public final static String KEY_CUSTOMER_PRODUCT_AREA = "key_customer_product_area";

	/**
	 * 九宫格密码
	 */
	public final static String KEY_PASSWORD_PATTERN = "key_password_pattern";

	/**
	 * 九宫格开关
	 */
	public final static String KEY_ENABLE_PASSWORD_PATTERN = "key_enable_password_pattern";

	/**
	 * 静脉纹开关
	 */
	public final static String KEY_ENABLE_PASSWORD_VEIN = "key_enable_password_vein";
	
	/**
	 * 删除数据
	 */
	public final static String KEY_CUSTOMER_DATA_SYNC = "key_customer_data_sync";

	/**
	 * 触屏检测周期
	 */
	public final static String KEY_TOUCH_DETECT_PEROID = "key_touch_detect_peroid";

	/**
	 * 最近触屏时间
	 */
	public final static String KEY_LATEST_TOUCH_TIME = "key_latest_touch_time";

	/**
	 * UI Background
	 */
	public final static String KEY_UI_BACKGROUND = "key_ui_background";
	/**
	 * 已选的DLNA设备名
	 */
	public final static String KEY_DLNA_NAME = "key_dlna_name";
	/**
	 * 云音乐列表更新时间
	 */
	public final static String KEY_CLOUD_MUSIC_LIST = "key_cloud_music_list";
	/**
	 * 云音乐歌手列表更新时间
	 */
	public final static String KEY_MUSIC_SINGER_LIST = "key_music_singer_list";
	/**
	 * 语音类型更新时间
	 */
	public final static String KEY_VOICE_TYPE = "key_voice_type";
	/**
	 * 新增语音配置更新时间
	 */
	public final static String KEY_VOICE_CONFIG_NEW = "key_voice_config_new";
	/**
	 * 历史语音配置更新时间
	 */
	public final static String KEY_VOICE_CONFIG_HISTORY = "key_voice_config_history";
	/**
	 * 查看当前用户唤醒词
	 */
	public final static String KEY_VOICE_WAKE_WORD = "key_voice_wake_word";
	/**
	 * 当前语音
	 */
	public final static String KEY_VOICE_LANGUAGE = "key_voice_language";
	/**
	 * 门铃设备更新时间
	 */
	public final static String KEY_DOORBELL_DEVICE = "key_doorbell_device";
	/**
	 * 意见反馈更新时间
	 */
	public final static String KEY_FEEDBACK = "key_feedback";
	/**
	 * 监控设备更新时间
	 */
	public final static String KEY_MONITOR_DEVICE = "key_monitor_device";
	
	/**
	 * 网关注册信息更新
	 */
	public final static String KEY_PRODUCT_REGISTER = "key_product_register";
	/**
	 * 红外遥控板更新
	 */
	public final static String KEY_REMOTE_BRANDS = "key_remote_brands";
	/**
	 * 设备关联更新
	 */
	public final static String KEY_PRODUCT_CONNECTION = "key_product_connection";
	
	public final static String KEY_PRODUCT_REMOTE_CONFIG = "key_product_remote_config";
	public final static String KEY_PRODUCT_REMOTE_CONFIG_FUN = "key_product_remote_config_fun";
	public final static String KEY_GEO_LOCATION = "key_geo_location";
	
	// 背景颜色随机数
	public static final int NUMFIRST = 1;
	public static final int NUMSECONd = 2;
	public static final int NUMTHREE = 3;
	public static final int NUMFOURTH = 4;

}
