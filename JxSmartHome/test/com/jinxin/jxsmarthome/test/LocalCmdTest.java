package com.jinxin.jxsmarthome.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.test.AndroidTestCase;

import com.jinxin.datan.local.serviceimpl.LocalEncoderVersion10;
import com.jinxin.jxsmarthome.cmd.Constants;
import com.jinxin.jxsmarthome.cmd.entity.PackageMessage;
import com.jinxin.jxsmarthome.constant.CommonMethod;

public class LocalCmdTest extends AndroidTestCase {
	public void test() {
//		PackageMessage msg = new PackageMessage();
//		msg.setVersion(Constants.LOCAL_VERSION_1);
//		msg.setCmd(Constants.LOCAL_CMD_RESPONSE);
//		msg.setContent("jxsmarthome");
//		LocalEncoderVersion10 p = new LocalEncoderVersion10(msg);
//		String d = p.encode();
//		System.out.println("-->" + d);
//		
//		
//		PackageMessage msg2 = new PackageMessage("jxsmarthome");
//		LocalEncoderVersion10 p2 = new LocalEncoderVersion10(msg2);
//		String d2 = p2.encode();
//		System.out.println("-->" + d2);
		
		System.out.println("-->" + CommonMethod.createCmdOfflineVersion10("jxsmarthome"));
		
	}
	
	
	public void test2() {
		try {
			// 发送信息到服务器
			Socket socketClient = new Socket("192.168.60.219", 3333);
			
			OutputStream out = socketClient.getOutputStream();
			
			String msg = "20100017jxsmarthome34284A";
			
			out.write(msg.getBytes());
			
			socketClient.shutdownOutput();
			
			// 接收服务器信息
			InputStream in = socketClient.getInputStream();
			
			byte[] b = new byte[1026];
			int readSize = 0;
			StringBuffer sb = new StringBuffer();
			
			while((readSize = in.read(b)) != -1) {
				sb.append(new String(b, 0, readSize));
			}
			
			System.out.println("The response from server: " + sb.toString());
			
			socketClient.shutdownInput();
			
			//关闭socket
			socketClient.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
