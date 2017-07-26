package com.jinxin.datan.local.serviceimpl;

import com.jinxin.datan.local.service.ILocalEncoder;
import com.jinxin.datan.local.util.StringUtil;
import com.jinxin.jxsmarthome.cmd.Constants;
import com.jinxin.jxsmarthome.cmd.entity.PackageMessage;

/**
 * 对发送的本地消息编码和解码
 * 使用方式：
 * 			1.属性都采用默认值
	  			PackageMessage msg2 = new PackageMessage("jxsmarthome");
				SHCPVersion10 p2 = new SHCPVersion10(msg2);
				System.out.println("-->" + p2.encode());
			2.属性自定义
				PackageMessage msg = new PackageMessage();
				msg.setVersion(Constants.LOCAL_VERSION_1);
				msg.setCmd(Constants.LOCAL_CMD_RESPONSE);
				msg.setContent("jxsmarthome");
				SHCPVersion10 p = new SHCPVersion10(msg);
				System.out.println("-->" + p.encode());
 * @author  TangLong
 * @company 金鑫智慧
 */
public class LocalEncoderVersion10 implements ILocalEncoder<PackageMessage> {
//	private static final String TAG = "SHCPVersion10";
	
	private PackageMessage pacageMessage;
	
	public LocalEncoderVersion10() {
		
	}
	
	public LocalEncoderVersion10(PackageMessage pacageMessage) {
		this.pacageMessage = pacageMessage;
	}
	
	@Override
	public String encode() {
		StringBuilder message = new StringBuilder();
		
		message.append(pacageMessage.getVersion());
		message.append(pacageMessage.getCmd());
		message.append(pacageMessage.getContenLength());
		message.append(pacageMessage.getContent());
		message.append(getRandomSerialStr());
		
		return message.toString();
	}
	
	@Override
	public String decode() {
		return null;
	}

	private String getRandomSerialStr() {
		return StringUtil.getRandomSerialString(Constants.LOCAL_SERIAL_LENGTH) + "A";
	}
}
