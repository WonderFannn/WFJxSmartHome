package com.jinxin.datan.local.serviceimpl;

import com.jinxin.datan.local.service.ILocalEncoder;
import com.jinxin.jxsmarthome.cmd.Constants;
import com.jinxin.jxsmarthome.cmd.entity.PackageMessage2;

/**
 * Zigbee协议封装
 * @author TangLong
 * @company 金鑫智慧
 */
public class ZigbeeEncoderVersion10 implements ILocalEncoder<PackageMessage2> {
	private PackageMessage2 packageMessage;
	
	public ZigbeeEncoderVersion10(PackageMessage2 packageMessage) {
		super();
		this.packageMessage = packageMessage;
	}

	@Override
	public String encode() {
		StringBuilder message = new StringBuilder();
		
//		message.append(packageMessage.getProtocol());
//		message.append(packageMessage.getVersion());
//		message.append(packageMessage.getLength());
		message.append(packageMessage.getContent());
		message.append(Constants.ZIGBEE_SPLIT);
		
		return message.toString();
	}

	@Override
	public String decode() {
		return null;
	}

}
