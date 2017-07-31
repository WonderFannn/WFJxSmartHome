package com.jinxin.infrared.model;

public class InfraredCodeCMDUtil {
	private int deviceType;
	private String cmdType;
	private byte[] code;
	
	public InfraredCodeCMDUtil(int deviceType,String cmdType,byte[] code) {
		this.deviceType = deviceType;
		this.cmdType = cmdType;
		this.code = code;
	}
	
	public byte[] getCMD() {
		//TODO
		byte[] cmd = null;
		if (deviceType != InfraredCodeLibraryConstant.DeviceType.AirCondition) {
			byte[] head = {0x30,0x00};
			byte fcode = code[0];
			byte[] keycode = getKeyCode( deviceType, cmdType, code);
			byte[] comcode = {code[code.length-4],code[code.length-3],code[code.length-2],code[code.length-1]};
			byte check = calculateCheckCode(cmd);
		}
		
		
		
		
		
		return cmd;
		
	}

	private byte calculateCheckCode(byte[] cmd) {
		byte a = 0;
		for (byte b : cmd) {
			a += b; 
		}
		return (byte) (a & (byte)0xff);
	}

	private byte[] getKeyCode(int deviceType2, String cmdType2, byte[] code2) {
		switch (deviceType2) {
		case InfraredCodeLibraryConstant.DeviceType.AirCleaner:
			
			break;

		default:
			break;
		}
		
		return null;
	}

	

}
