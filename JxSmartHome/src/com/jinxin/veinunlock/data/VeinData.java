package com.jinxin.veinunlock.data;


/**
 * 常量
 */
public class VeinData {

	public enum UnlockStatus{
		INIT,
		SCREEN_ON,
		SCREEN_OFF,
		UNLOCK_CALL,
		LOCK_CALL,
		CALL_RING,
		CALL_IDLE
	}
	public enum BatteryStatus{
		INIT,
		CHARGING,
		DISCHARGING,
		NO_CHARGE,
		STATUS1,
		STATUS2,
		STATUS3,
		STATUS4,
		STATUS5,
		STATUS_FULL
	}
	public static UnlockStatus UNLOCK_STATUS = UnlockStatus.INIT;
	public static UnlockStatus CALL_STATUS = UnlockStatus.CALL_IDLE;
	public static BatteryStatus BATTERY_STATUS = BatteryStatus.INIT;
	
	public static final String SETTING_INFOS = "BLUETOOTH_SETTING_INFOS";
	public static final String DOOR_MAC = "DOOR_MAC";
	public static final String DOOR_CHECK = "DOOR_CHECK";
	public static final String DOOR_LEVEL = "DOOR_LEVEL";
	public static final String DOOR_MODE = "DOOR_MODE";
	public static final String DOOR_TIMEOUT = "DOOR_TIMEOUT";
	public static final String SAFE_MAC = "SAFE_MAC";
	public static final String SAFE_CHECK = "SAFE_CHECK";
	public static final String SAFE_LEVEL = "SAFE_LEVEL";
	public static final String SAFE_MODE = "SAFE_MODE";
	public static final String SAFE_TIMEOUT = "SAFE_TIMEOUT";
	public static final String CAR_MAC = "CAR_MAC";
	public static final String CAR_CHECK = "CAR_CHECK";
	public static final String CAR_LEVEL = "CAR_LEVEL";
	public static final String CAR_MODE = "CAR_MODE";
	public static final String CAR_TIMEOUT = "CAR_TIMEOUT";
	public static final String PC_MAC = "PC_MAC";
	public static final String PC_CHECK = "PC_CHECK";
	public static final String PC_LEVEL = "PC_LEVEL";
	public static final String PC_MODE = "PC_MODE";
	public static final String PC_TIMEOUT = "PC_TIMEOUT";
	public static final String PAD_MAC = "PAD_MAC";
	public static final String PAD_CHECK = "PAD_CHECK";
	public static final String PAD_LEVEL = "PAD_LEVEL";
	public static final String PAD_MODE = "PAD_MODE";
	public static final String PAD_TIMEOUT = "PAD_TIMEOUT";
	public static final String UNLOCK_CHECK = "UNLOCK_CHECK";
	public static final String UNLOCK_LEVEL = "UNLOCK_LEVEL";
	public static final String UNLOCK_TIMEOUT = "UNLOCK_TIMEOUT";
	public static final String UNLOCK_MAC = "UNLOCK_MAC";
	public static final String UNLOCK_MODE = "PAD_MODE";
	public static final String CONTROL_SELECT = "CONTROL_SELECT";
	public static final int SYNCHRO_CONNECT = 0;///同步
	public static boolean  SERVICE_ENABLE = false;
	public static final int ASYNCHRO_CONNECT = 1;///异步
	public static boolean isBT_ENABLE;
	public static int CONNECT_MODE = SYNCHRO_CONNECT;
	public static String[] MAC_KEY = {UNLOCK_MAC,DOOR_MAC,CAR_MAC,SAFE_MAC,PC_MAC,PAD_MAC};
	public static String[] DEFAULT_MAC = {"NULL",
		                                  "00:18:11:72:30:2E",
		                                  //"00:18:11:72:30:2F",
		                                 // "90:21:55:1E:A7:79",
		                                 // "CC:8C:E3:85:B4:65",
		                                  //"CC:8C:E3:85:B4:65"
		                                  "00:00:00:00:00:00",
		                                  "00:00:00:00:00:00",
		                                  "00:00:00:00:00:00",
		                                  "00:00:00:00:00:00"};
	public static String[] CHECK_KEY = {UNLOCK_CHECK,DOOR_CHECK,CAR_CHECK,SAFE_CHECK,PC_CHECK,PAD_CHECK};
	public static boolean[] CHECK_VALUE = new boolean[CHECK_KEY.length+1];
	public static String[] LEVEL_KEY = {UNLOCK_LEVEL,DOOR_LEVEL,CAR_LEVEL,SAFE_LEVEL,PC_LEVEL,PAD_LEVEL};
	public static String[] MODE_KEY = {UNLOCK_MODE,DOOR_MODE,CAR_MODE,SAFE_MODE,PC_MODE,PAD_MODE};
	public static String[] TIMEOUT_KEY = {UNLOCK_TIMEOUT,DOOR_TIMEOUT,CAR_TIMEOUT,SAFE_TIMEOUT,PC_TIMEOUT,PAD_TIMEOUT};
	
	public static int m_iDeviceNum = 6;
	public static int m_iSelectDeviceNum = 0;
	public static DeviceInfo[] m_DeviceInfo = new DeviceInfo[m_iDeviceNum];
	
	public static int m_iLevel = 1;
	/**
	 * 常量
	 */
	public VeinData() {
		if(m_DeviceInfo == null){
			m_DeviceInfo = new DeviceInfo[m_iDeviceNum];
		}else{
			for(int i = 0; i < m_DeviceInfo.length; i++){
				m_DeviceInfo[i] = new DeviceInfo();
			}
		}
	}
}
