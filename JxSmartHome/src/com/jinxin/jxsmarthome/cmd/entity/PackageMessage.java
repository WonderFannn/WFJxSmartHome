package com.jinxin.jxsmarthome.cmd.entity;

import com.jinxin.datan.local.util.StringUtil;
import com.jinxin.jxsmarthome.cmd.Constants;

/**
 * 用于本地通信的信息包装对象
 * @author 	TangLOng
 * @company 金鑫智慧
 */
public class PackageMessage implements java.io.Serializable{
	private static final long serialVersionUID = 6597046168524393175L;

	//协议版本号
	private String version;
	
	//协议命令，0=请求(REQUEST)，1=应答(RESPONSE)，2=心跳(KEEPLIVED)，3=终端 状态上报(REPORT)
	private Integer cmd;
	
	//指令长度指令长度字符串转换成Integer类型
	private String contenLength;
	
	//指令内容
	private String content;
	
	public PackageMessage(){
		
	}
	
	public PackageMessage(String content) {
		this(Constants.LOCAL_VERSION_1, Constants.LOCAL_CMD_RESPONSE, content);
	}
	
	public PackageMessage (String version, Integer cmd, String content ) {
		this.version = version;
		this.cmd = cmd;
		this.content = content;
		this.setContenLength();
	}
	
	@Override
	public String toString ( ) {
		StringBuffer sb = new StringBuffer();
		sb.append( "version：" ).append( version ).append( ", " );
		sb.append( "cmd：" ).append( cmd ).append( ", " );
		sb.append( "contenLength：" ).append( contenLength );
		sb.append( "content：" ).append( content );
		return sb.toString();
	}
	
	////////////////////////////////////////////////////
	// getters and setters
	////////////////////////////////////////////////////
	public String getVersion ( ) {
		return version;
	}
	
	public void setVersion ( String version ) {
		this.version = version;
	}
	
	public Integer getCmd ( ) {
		return cmd;
	}
	
	public void setCmd ( Integer cmd ) {
		this.cmd = cmd;
	}
	
	public String getContenLength() {
		return contenLength;
	}
	public void setContenLength() {
		int length = Constants.LOCAL_SERIAL_LENGTH_A + content.length();
		String str = StringUtil.getFixedLengthStrFromInt(length, Constants.LOCAL_CONTENLENGTH);
		this.contenLength = str;
	}
	public String getContent ( ) {
		return content;
	}
	
	public void setContent ( String content ) {
		this.content = content;
		this.setContenLength();
	}
}
