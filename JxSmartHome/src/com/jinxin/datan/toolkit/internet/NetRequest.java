package com.jinxin.datan.toolkit.internet;

import com.jinxin.datan.net.protocol.CommandBase;
import com.jinxin.datan.net.protocol.CommandFile;
import com.jinxin.datan.net.protocol.ResponseJson;
import com.jinxin.datan.net.protocol.ResponseXml;
/**
 * 通信事件请求类（请求类型，数据结构选择，联网地址等）
 * @author zj
 *
 */
public class NetRequest implements IRequest {

    private byte mRequestId;
    private CommandBase mCommandBase;
    private CommandFile mCommandFile;
    
	private byte intdesFlag;// 加密条件（0不加1加密）
    private ResponseXml responseXml;//解析xml接口
    private ResponseJson responseJson;//解析json接口

    private String url;//http get方式联网地址
    private long createTime;//创建请求时间
    private long excuteTime;//执行请求时间
    private byte type;

    private int dirID;
    /**
     * http get解析为xml格式专用
     * @param url
     * @param response
     */
    public NetRequest(String url,ResponseXml response){
        this.url = url;
        this.responseXml = response;
        this.init();
    }
    /**
     * http get解析为json格式专用
     * @param url
     * @param response
     */
    public NetRequest(String url,ResponseJson response){
        this.url = url;
        this.responseJson = response;
        this.init();
    }
    /**
     * 文件操作模式
     * @param cf
     */
    public NetRequest(CommandFile cf){
    	this.mCommandFile = cf;
    }
    /**
     * http get解析数据流模式
     * @param url
     * @param cmd
     */
    public NetRequest(String url,CommandBase cmd){
        this((byte)0, cmd, (byte)0);
        this.url = url;
    }
    public NetRequest(byte requestId, CommandBase cmd, byte flag) {
        this(requestId, cmd, flag, 0);
    }

    public NetRequest(byte requestId, CommandBase cmd, byte flag, int dirID) {
        this(requestId, cmd, flag, dirID, 0);
    }

    public NetRequest(byte requestId, CommandBase cmd, byte flag, int dirID, int type) {
        mRequestId = requestId;
        this.mCommandBase = cmd;
        this.intdesFlag = flag;
        this.dirID = dirID;
        this.type = (byte) type;
        this.init();
    }
    private void init(){
        this.createTime = System.currentTimeMillis();
    }
    public byte getRequestId() {
        return mRequestId;
    }
    public CommandFile getmCommandFile() {
		return mCommandFile;
	}
	public void setmCommandFile(CommandFile mCommandFile) {
		this.mCommandFile = mCommandFile;
	}
    public CommandBase getCommandBase() {
        return mCommandBase;
    }

    public byte getIntdesFlag() {
        return this.intdesFlag;
    }

    public void setCommandBase(CommandBase cmd) {
        this.mCommandBase = cmd;
    }
    @Override
    public ResponseXml getResponseXml() {
        // TODO Auto-generated method stub
        return this.responseXml;
    }
    @Override
    public ResponseJson getResponseJson() {
        return responseJson;
    }
    public void setResponseJson(ResponseJson responseJson) {
        this.responseJson = responseJson;
    }
    @Override
    public String getUrl() {
        // TODO Auto-generated method stub
        return this.url;
    }
    @Override
    public void setUrl(String url) {
        // TODO Auto-generated method stub
        this.url = url;
    }
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (o instanceof NetRequest) {
            NetRequest req = (NetRequest) o;
            if (req.dirID == dirID && req.mRequestId == mRequestId && req.intdesFlag == intdesFlag && req.type == type) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        // Start with a non-zero constant.
        int result = 17;

        // Include a hash for each field.
        // result = 31 * result + (booleanField ? 1 : 0);

        result = 31 * result + dirID;
        result = 31 * result + mRequestId;
        result = 31 * result + intdesFlag;
        result = 31 * result + type;

        // result = 31 * result + (int) (longField ^ (longField >>> 32));
        //
        // result = 31 * result + Float.floatToIntBits(floatField);
        //
        // long doubleFieldBits = Double.doubleToLongBits(doubleField);
        // result = 31 * result + (int) (doubleFieldBits ^ (doubleFieldBits >>>
        // 32));

        // result = 31 * result + Arrays.hashCode(arrayField);

        // result = 31 * result + referenceField.hashCode();
        // result = 31 * result + (nullableReferenceField == null ? 0 :
        // nullableReferenceField.hashCode());

        return result;
    }
    public void setExcuteTime(long time){
        this.excuteTime = time;
    }
    public long getExcuteTime(){
        return this.excuteTime;
    }
    @Override
	public String toString() {
		return "NetRequest [mRequestId=" + mRequestId + ", mCommandBase=" + mCommandBase + ", mCommandFile="
				+ mCommandFile + ", intdesFlag=" + intdesFlag + ", responseXml=" + responseXml + ", responseJson="
				+ responseJson + ", url=" + url + ", createTime=" + createTime + ", excuteTime=" + excuteTime
				+ ", type=" + type + ", dirID=" + dirID + "]";
	}
	public long getCreateTime(){
        return this.createTime;
    }
    
    
}
