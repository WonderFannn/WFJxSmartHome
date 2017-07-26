package com.jinxin.datan.net.protocol;

import com.jinxin.jxsmarthome.util.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * 文件操作事件父类
 * @author zj
 *
 */
public abstract class CommandFile {    
	public abstract boolean read();
    public abstract boolean write();
    /**
     * 关闭stringWriter
     * @param stringWriter
     */
    public void closeStringWriter(StringWriter stringWriter){
    	if(stringWriter != null){
    		try {
				stringWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    /**
     * 关闭输入流
     * @param in
     */
	public void closeInputStream(InputStream in) {
		if (in != null) {
			try {
				in.close();
				in = null;
			} catch (Exception ex) {
				Logger.error("closeInputStream", ex.toString());
			}
		}
	}
}
