package com.jinxin.infrared.model;

import java.util.HashMap;
import java.util.Map;
import com.jinxin.jxsmarthome.util.ClassMemberUtil;

public class InfraredCodeCMDUtil {
	private int deviceType;
	private String cmdType;
	private byte[] code;

	public InfraredCodeCMDUtil(int deviceType, String cmdType, byte[] code) {
		this.deviceType = deviceType;
		this.cmdType = cmdType;
		this.code = code;
	}

	public byte[] getCMD() {
		byte[] cmd = null;
		if (deviceType != InfraredCodeLibraryConstant.DeviceType.AirCondition) {
			byte[] head = { 0x30, 0x00 };
			byte fcode = code[0];
			byte[] keycode = getKeyCode(deviceType, cmdType, code);
			byte[] comcode = { code[code.length - 4], code[code.length - 3],
					code[code.length - 2], code[code.length - 1] };
			cmd = byteMerge(head, byteMerge(fcode, byteMerge(keycode, comcode)));
			byte check = calculateCheckCode(cmd);
			cmd = byteMerge(cmd, check);
			// 特殊情况处理
			if (cmd[2] == 0x04) {
				cmd[5] ^= 0x20;
			} else if (cmd[2] == 0x0a) {
				cmd[5] ^= 0x08;
			}
		} else {
			// 空调命令拼写
			cmd = code;
			int serial = ((cmd[0]<<8) | cmd[1]);
			if (serial > 510) {
				serial -= 510;
			}
			cmd[0] = (byte) ((serial>>8) & 0xff);
			cmd[1] = (byte) (serial & 0xff);
			
			Map<String, Integer> map = new HashMap<>();
			map = ClassMemberUtil.getObjMap(new InfraredCodeLibraryConstant.ARCKeyBoadMap());
			byte opCmd = (byte) map.get(cmdType).intValue();
			code[9] = opCmd;
			switch (opCmd) {
			case InfraredCodeLibraryConstant.ARCKeyBoadMap.ARC_Power:
				cmd[8] = (byte) ((cmd[8]+1)%2);
				break;
			case InfraredCodeLibraryConstant.ARCKeyBoadMap.ARC_Mode:
				cmd[10] = (byte) (cmd[10]%5+1);
				break;
			default:
				break;
			}
		}
		return cmd;
	}

	private byte[] byteMerge(byte[] a, byte[] b) {
		byte[] c = new byte[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}

	private byte[] byteMerge(byte[] a, byte b) {
		byte[] c = new byte[a.length + 1];
		System.arraycopy(a, 0, c, 0, a.length);
		c[a.length] = b;
		return c;
	}

	private byte[] byteMerge(byte a, byte[] b) {
		byte[] c = new byte[1 + b.length];
		c[0] = a;
		System.arraycopy(b, 0, c, 1, b.length);
		return c;
	}

	private byte[] byteMerge(byte a, byte b) {
		byte[] c = new byte[2];
		c[0] = a;
		c[1] = b;
		return c;
	}

	private byte calculateCheckCode(byte[] cmd) {
		byte a = 0;
		for (byte b : cmd) {
			a += b;
		}
		return (byte) (a & (byte) 0xff);
	}

	private byte[] getKeyCode(int deviceType, String cmdType, byte[] code) {
		byte[] keyCode = new byte[2];
		Map<String, Integer> map = new HashMap<>();
		switch (deviceType) {
		case InfraredCodeLibraryConstant.DeviceType.AirCleaner:
			map = ClassMemberUtil
					.getObjMap(new InfraredCodeLibraryConstant.AirKeyBoadMap());
			break;
		case InfraredCodeLibraryConstant.DeviceType.AirCondition:
			break;
		case InfraredCodeLibraryConstant.DeviceType.Projector:
			map = ClassMemberUtil
					.getObjMap(new InfraredCodeLibraryConstant.PJTKeyBoadMap());
			break;
		case InfraredCodeLibraryConstant.DeviceType.Fan:
			map = ClassMemberUtil
					.getObjMap(new InfraredCodeLibraryConstant.FanKeyBoadMap());
			break;
		case InfraredCodeLibraryConstant.DeviceType.TvBox:
			map = ClassMemberUtil
					.getObjMap(new InfraredCodeLibraryConstant.TVBoxKeyBoadMap());
			break;
		case InfraredCodeLibraryConstant.DeviceType.Tv:
			map = ClassMemberUtil
					.getObjMap(new InfraredCodeLibraryConstant.TVKeyBoadMap());
			break;
		case InfraredCodeLibraryConstant.DeviceType.InternetTv:
			map = ClassMemberUtil
					.getObjMap(new InfraredCodeLibraryConstant.IPTVKeyBoadMap());
			break;
		case InfraredCodeLibraryConstant.DeviceType.Dvd:
			map = ClassMemberUtil
					.getObjMap(new InfraredCodeLibraryConstant.DVDKeyBoadMap());
			break;
		case InfraredCodeLibraryConstant.DeviceType.Calorifier:
			map = ClassMemberUtil
					.getObjMap(new InfraredCodeLibraryConstant.WheaterKeyBoadMap());
			break;
		case InfraredCodeLibraryConstant.DeviceType.Camera:
			map = ClassMemberUtil
					.getObjMap(new InfraredCodeLibraryConstant.SLRKeyBoadMap());
			break;
		default:
			break;
		}

		int index = map.get(cmdType).intValue();
		keyCode[0] = code[index];
		keyCode[1] = code[index + 1];
		return keyCode;
	}

	// 均以接收数据为准
	private int compareMatch(byte[] rev, byte[] loc) {
		if (rev.length != 230 || loc.length != 230) {
			// printf("数据长度不对");
			return 0;
		}
		//第一位表示第一个0xff位置，第二位表示截止的0xf的位置
		int[] rfIndex = new int[2];
		int[] lfIndex = new int[2];

		if (!locateCompareFlag(rev, rfIndex)) {
			// printf("收到数据定位flag错误");
			return 0;
		}

		if (!locateCompareFlag(loc, lfIndex)) {
			// printf("本地数据定位flag错误");
			return 0;
		}
		if (rfIndex[0] != lfIndex[0]) {
			// printf("FF头定位不同");
			return 0;
		}
		if (rfIndex[1] != lfIndex[1]) {
			// printf("Fx/xF头定位不同");
			return 0;
		}

		// 1、首先前4字节必须全部相同
		for (int i = 0; i < 4; i++) {
			if (rev[i] != loc[i]) {
				// printf("前4字节不相同");
				return 0;
			}
		}

		int sameSum = 0;
		// 2、第5字节到rffhead之前 相差20%以内算相同
		for (int i = 4; i < rfIndex[0]; i++) {
			int revValue = rev[i];
			int locValue = loc[i];
			if (revValue == 0x0) {
				if (locValue == 0x0) {
					sameSum++;
				}
			} else {
				float k = Math.abs(revValue - locValue) / (float) revValue;
				if (k < 0.2) {
					sameSum++;
				}
			}
		}

		// 3、首个0xff（包括）到0x?f／0xf? 之间完全相同才算相同
		int stride = rfIndex[1] - rfIndex[0];

		for (int i = 0; i < stride; i++) {
			if (rev[rfIndex[0] + i] == loc[rfIndex[0] + i]) {
				sameSum++;
			}
		}
		return sameSum;
	}

	private boolean locateCompareFlag(byte[] dat, int[] rfHead) {
		for (int i = 4; i < dat.length; i++) {
			if (dat[i] == 0xff) {
				for (int j = i; j < dat.length; j++) {
					if (dat[j] != 0xff) {
						if (j > i && j > 0 && i > 0) {
							for (int k = j; k < dat.length; k++) {
								if ((dat[k] & 0x0f) == 0x0f
										|| (dat[k] & 0xf0) == 0xf0) {
									if (k >= j) {
										rfHead[0] = i;
										rfHead[1] = k;
										return true;
									}
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

}
