package com.jinxin.infrared.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.net.nntp.NewGroupsOrNewsQuery;

import u.aly.cm;

import android.R.integer;

import com.jinxin.jxsmarthome.util.ClassMemberUtil;

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

	private byte[] getKeyCode(int deviceType, String cmdType, byte[] code) {
		byte[] keyCode = new byte[2];
		Map<String, Integer> map = new HashMap<>();
		switch (deviceType) {
		case InfraredCodeLibraryConstant.DeviceType.AirCleaner:
			map = ClassMemberUtil.getObjMap(new InfraredCodeLibraryConstant.AirKeyBoadMap());
			break;
		case InfraredCodeLibraryConstant.DeviceType.AirCondition:
			break;
		case InfraredCodeLibraryConstant.DeviceType.Projector:
			map = ClassMemberUtil.getObjMap(new InfraredCodeLibraryConstant.PJTKeyBoadMap());
			break;
		case InfraredCodeLibraryConstant.DeviceType.Fan:
			map = ClassMemberUtil.getObjMap(new InfraredCodeLibraryConstant.FanKeyBoadMap());
			break;
		case InfraredCodeLibraryConstant.DeviceType.TvBox:
			map = ClassMemberUtil.getObjMap(new InfraredCodeLibraryConstant.TVBoxKeyBoadMap());
			break;
		case InfraredCodeLibraryConstant.DeviceType.Tv:
			map = ClassMemberUtil.getObjMap(new InfraredCodeLibraryConstant.TVKeyBoadMap());
			break;
		case InfraredCodeLibraryConstant.DeviceType.InternetTv:
			map = ClassMemberUtil.getObjMap(new InfraredCodeLibraryConstant.IPTVKeyBoadMap());
			break;
		case InfraredCodeLibraryConstant.DeviceType.Dvd:
			map = ClassMemberUtil.getObjMap(new InfraredCodeLibraryConstant.DVDKeyBoadMap());
			break;
		case InfraredCodeLibraryConstant.DeviceType.Calorifier:
			map = ClassMemberUtil.getObjMap(new InfraredCodeLibraryConstant.WheaterKeyBoadMap());
			break;
		case InfraredCodeLibraryConstant.DeviceType.Camera:
			map = ClassMemberUtil.getObjMap(new InfraredCodeLibraryConstant.SLRKeyBoadMap());
			break;
		default:
			break;
		}
		
		int index = map.get(cmdType).intValue();
		keyCode[0] = code[index];
		keyCode[1] = code[index+1];
		return keyCode;
	}

}