package com.jinxin.datan.net.protocol;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jinxin.datan.net.module.RemoteJsonResultInfo;
import com.jinxin.datan.toolkit.task.Task;

/**
 * 离线命令发送的结果解析(通用)
 * 
 * @author  TangLong
 * @company 金鑫智慧
 */
public class OfflineCommonParser extends ResponseJson {
	private Task task = null;
	private byte[] requestBytes;

	public OfflineCommonParser(Task task, byte[] requestBytes) {
		this.task = task;
		this.requestBytes = requestBytes;
	}

	@Override
	public byte[] toOutputBytes() {
		return this.requestBytes;
	}

	@Override
	public void response(InputStream in) throws Exception {
		RemoteJsonResultInfo resultInfo = new RemoteJsonResultInfo();

		if (in != null) {
			try {
				DataInputStream input = new DataInputStream(in);
				
				//////////////////////////// debug ///////////////////////////////
				byte[] b = new byte[1024];
				int readSize = 0;
				StringBuffer sb = new StringBuffer();
				
				while((readSize = in.read(b)) != -1) {
					sb.append(new String(b, 0, readSize));
				}
				
				String result = unicodeToString(sb.toString());
				
				System.out.println("server 返回:" + sb.length());
				System.out.println("server 返回:" + result.length());
				System.out.println("server 返回:" + result);
				///////////////////////////////////////////////////////////////////
				
				byte[] bufferVer = new byte[2];
				input.read(bufferVer);
				String ver = new String(bufferVer);
				System.out.println("server 返回协议版本信息：" + unicodeToString(ver));

				byte[] bufferCmdType = new byte[1];
				input.read(bufferCmdType);
				String cmdType = new String(bufferCmdType);
				System.out.println("server 返回请求类型信息：" + unicodeToString(cmdType));

				byte[] bufferLen = new byte[5];
				input.read(bufferLen);
				String len = new String(bufferLen);
				System.out.println("server 返回内容长度：" + unicodeToString(len));

				byte[] bufferCon = new byte[len.length()];
				input.read(bufferCon);
				String content = new String(bufferCon, "UTF-8");
				System.out.println("server 返回请求内容信息：" + unicodeToString(content));

				byte[] bufferCode = new byte[6];
				input.read(bufferCode);
				String code = new String(bufferCode);
				System.out.println("server 返回请求随机码：" + unicodeToString(code));

				resultInfo.validResultCode = "0000";
				resultInfo.validResultInfo = new String(content);
			} catch (Exception e) {
				e.printStackTrace();
				resultInfo.validResultCode = "04";
				resultInfo.validResultInfo = "解析结果失败";
			}
		} else {
			resultInfo.validResultCode = "04";
			resultInfo.validResultInfo = "未知异常";
		}
		this.setResultInfo(resultInfo);
		this.task.callback(resultInfo);
	}
	
	private static String unicodeToString( String str ) {
		Pattern pattern = Pattern.compile( "(\\\\u(\\p{XDigit}{4}))" );
		Matcher matcher = pattern.matcher( str );
		char ch;
		while ( matcher.find() ) {
			ch = (char) Integer.parseInt( matcher.group( 2 ) , 16 );
			str = str.replace( matcher.group( 1 ) , ch + "" );
		}
		return str;
	}
}
