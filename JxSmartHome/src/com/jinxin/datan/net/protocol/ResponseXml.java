package com.jinxin.datan.net.protocol;

import com.jinxin.jxsmarthome.util.Logger;

import com.jinxin.datan.net.module.RemoteErrorInfo;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

/**
 * XML解析器父类
 * @author JackeyZhang
 *
 */
public abstract class ResponseXml {
	private RemoteErrorInfo errorInfo = null;

	/**
	 * post方式请求时需实现加入请求字节数据
	 * 
	 * @return
	 */
	public abstract byte[] toOutputBytes();

	public abstract void response(InputStream in) throws Exception;

	public RemoteErrorInfo getErrorInfo() {
		return errorInfo;
	}

	public void setErrorInfo(RemoteErrorInfo errorInfo) {
		this.errorInfo = errorInfo;
	}

	public void closeInputStream(InputStream in) {
		if (in != null) {
			try {
				in.close();
				in = null;
			} catch (Exception ex) {
				Logger.error("ResponseXml", ex.toString());
			}
		}
	}

	/**
	 * 读错误信息
	 * 
	 * @param xmlParser
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public RemoteErrorInfo readErrorInfo(XmlPullParser xmlParser)
			throws IOException, XmlPullParserException {
		RemoteErrorInfo errorInfo = new RemoteErrorInfo();
		while (xmlParser.nextTag() != XmlPullParser.END_TAG) {
			xmlParser.require(XmlPullParser.START_TAG, null, null);
			String name = xmlParser.getName();
			String text = xmlParser.nextText();
			if (name != null && name.toLowerCase().equals("validerrorcode")) {
				errorInfo.validErrorCode = text;
			} else if (name != null
					&& name.toLowerCase().equals("validerrorinfo")) {
				errorInfo.validErrorInfo = text;
			}
			xmlParser.require(XmlPullParser.END_TAG, null, name);
		}

		return errorInfo;
	}

	public void readErrorInfo(String error) {
		RemoteErrorInfo errorInfo = new RemoteErrorInfo();
		errorInfo.validErrorCode = "0";
		errorInfo.validErrorInfo = error;
		this.errorInfo = errorInfo;
	}

}
