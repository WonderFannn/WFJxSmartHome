package com.jinxin.jxsmarthome.test;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.test.AndroidTestCase;

import com.jinxin.jxsmarthome.cmd.OnlineMulitGatewayCmdSender;
import com.jinxin.jxsmarthome.cmd.entity.Command;
import com.jinxin.jxsmarthome.util.Logger;

public class OnlineMulitGatewayCmdSenderTest extends AndroidTestCase {
	public void test() {
		List<Command> commandList = new ArrayList<Command>();
		
		String remoteHost = "192.168.60.226:8890";
		List<byte[]> cmdList = new ArrayList<byte[]>();
		cmdList.add(cmd("C280010002|user20open004:c00000001"));
		cmdList.add(cmd("C280010002|user20open004:c00000010"));
		cmdList.add(cmd("C280010002|user20open004:c00000100"));
		cmdList.add(cmd("C280010002|user20open004:c00001000"));
		Command command = new Command();
		command.setGatewayRemoteIp(remoteHost);
		command.setCmdList(cmdList);
		commandList.add(command);
		
		List<byte[]> cmdList1 = new ArrayList<byte[]>();
		cmdList1.add(cmd("C280010002|user20close004:c00000001"));
		cmdList1.add(cmd("C280010002|user20close004:c00000010"));
		cmdList1.add(cmd("C280010002|user20close004:c00000100"));
		cmdList1.add(cmd("C280010002|user20close004:c00001000"));
		Command command1 = new Command();
		command1.setGatewayRemoteIp(remoteHost);
		command1.setCmdList(cmdList);
		commandList.add(command1);
		
		OnlineMulitGatewayCmdSender sender = new OnlineMulitGatewayCmdSender(getContext(), commandList);
		sender.send();
		
		Logger.debug(null, "send end.");
		
		try {
			TimeUnit.SECONDS.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	private byte[] cmd(String _cmd) {
		ByteBuffer bbf = null;
		try {
			int len = _cmd.getBytes("utf-8").length;

			bbf = ByteBuffer.allocate(len + 8);
			bbf.putInt(1); 
			bbf.putInt(len);
			bbf.put(_cmd.getBytes("utf-8"));
			bbf.flip();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return bbf.array();
	}
}
