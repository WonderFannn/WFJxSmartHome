package xgzx.VeinUnlock;

public class VeinCore {
	static {
		System.loadLibrary("VeinApi");
	}
	public enum DisplayStatus{
		STATUS_INIT,
		STATUS_CHECKING,
		STATUS_OK_BTN,
		STATUS_OK_IMG,
		STATUS_AUTH_TIMEOUT,
		STATUS_AUTH_OK,
		STATUS_AUTH_FALIURE,
		STATUS_REG_OK,
		STATUS_REG_FAILURE,
		STATUS_REG_DIFF,
	}
	
	public static final int MODE_REG = 0;
	public static final int MODE_AUTH = 1;
	public static final int MODE_AUTO_AUTH = 2;
	public static final int MODE_AUTO_REG = 3;
	
	public static final int MSG_AUTH_OK = 0;
	public static final int MSG_AUTH_FAILURE = 1;
	public static final int MSG_REG_OK = 2;
	public static final int MSG_REG_FAILURE = 3;
	public static final int MSG_TIME_OUT = 4;
	public static final int MSG_CAMERA_ERROR = 5;
	public static final int MSG_FINISH_EXIT = 6;
	public static final int MSG_BT_CONNECTED = 7;

	public static final int m_iMaxUser = 3;
	public static int m_iMaxRegNum = 8; //最多8个
	public static final int m_iMaxAuthNum = 20;
	public static final int m_iCheckOkNum = 5;
	
	public static final int m_iImgWidth = 320;
	public static final int m_iImgHeight = 240;
	public static final int m_iWristWidth = 240;
	public static final int m_iWristHeight = 210;
	
	public static final int PART_WRIST = 1;
	public static final int PART_BACKHAND_L = 2;
	public static final int PART_PALM_L = 3;
	public static final int PART_BACKHAND_R = 4;
	public static final int PART_PALM_R = 5;
	
	public int m_iUser = 1;
	public int m_CheckEnable = 2;
	
	public String m_strIP = null;
	public String m_strPhoneNumber = null;
	public String m_strIMSI = null;
	public String m_strIMEI = null;
	public String m_strMAC = null;

	/**
	 * 获取软件的版本号
	 * @return 软件的版本信息
	 */
	public native int GetVeinLibVer(byte[] Ver);
	
	/**
	 * 初始化软件所需要的信息
	 * 
	 * @param Model 手机型号
	 * * @param DataDir 数据保存目录
	 * @return
	 */
	public native int CreateVein(String Model, String DataDir);

	/**
     * 设置阈值，分为四个等级：低、中、高、最高
     * @param Peak 为设置的阈值的指
     * @return
     */
    public native int VeinSetLevel(int Level);

    /**
     * 关闭静脉库
     * @return
     */
    public native int CloseVein();
    
    /**
     * 静脉部位检测
     * @param buf 图像数据
     * @param Pos 获取到的位置信息 
     * @return
     */
    public native int CheckHand(byte[] buf, byte[] Pos);

    /**
     * 静脉注册
     * @param bTh 
     * @param cTh
     * @param bCheck
     * @param cCheck
     * @return
     */
    public native int SetCheckParam(int bTh, int cTh, int bCheck, int cCheck);
    
    /**
     * 静脉注册
     * @param buf 图像数据
     * @param left 
     * @param right
     * @param top
     * @param bottom
     * @return
     */
    public native int CheckSkin(byte[] buf, int left, int right, int top, int bottom);

    /**
     * 静脉注册
     * @param buf 图像数据
     * @param user 用户号
     * @param part 注册部位
     * @param left 
     * @param right
     * @param top
     * @param bottom
     * @return
     */
    public native int VeinEnroll(byte[] buf, int user, int part, int left, int right, int top, int bottom);

    /**
     * 静脉识别
     * @param buf 图像数据
     * @param user 用户号
     * @param part 识别部位
     * @param left
     * @param right
     * @param top
     * @param bottom
     * @return
     */
    public native int VeinVerify(byte[] buf, int user, int part, int left, int right, int top, int bottom);

    /**
     * 静脉识别
     * @param buf 图像数据，返回的特征数据
     * @param part 识别部位
     * @param left
     * @param right
     * @param top
     * @param bottom
     * @return
     */
    public native int GetVeinChara(byte[] buf, int part, int left, int right, int top, int bottom);

    /**
     * 保存注册信息 
     * @param user 用户号
     * @return
     */
    public native int VeinSaveEnroll(int user);
    
    /**
     * 删除注册信息
     * @param user 用户号
     * @return
     */
    public native int VeinDelEnroll(int user);
    
    /**
     * 根据输入的用户号获得该用户的注册数
     * @param user 为需要获取注册数的用户号
     * @return 该用户的注册数
     */
    public native int VeinGetTempNum(int user);

    public int GetRegUserNum()
    {
    	int UserNum = 0;
    	for(int i = 0; i < m_iMaxUser; i++)
    	{
    		int num = VeinGetTempNum(i + 1);
    		if(num > 0) UserNum++;
    	}
    	return UserNum;
    };
    
    public int GetVerifyUserId()
    {
    	//优先顺序为手背，手腕和手掌
    	if(VeinGetTempNum(2) > 0) return 2;
    	if(VeinGetTempNum(1) > 0) return 1;
    	if(VeinGetTempNum(3) > 0) return 3;
    	return 0;
    };
    
    public native int setLED(int val);
    
    private String toVeinUser = null;

	public String getUser() {
		return toVeinUser;
	}

	public void setUser(String user) {
		this.toVeinUser = user;
	}
    
}