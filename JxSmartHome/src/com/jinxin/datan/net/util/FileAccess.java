package com.jinxin.datan.net.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import com.jinxin.jxsmarthome.util.Logger;

// System.getProperty("fileconn.dir.memorycard")
// 获得存储卡目录名称，如：file:///E:/
// file:///c:/代表internal memory, file:///E:/ 代表 external memory
// file:///root1/ 这是电脑模拟器上用这样

public class FileAccess {
	public boolean exists;
	public long fizeSize;
	// FileChannel fc = null;
	//FileOutputStream os = null;
	RandomAccessFile rac = null;

	public FileAccess() {
	}
	
	public void exists(String url) throws IOException {
		File file = new File(url);
		exists = file.exists();
		Tracer.debug("fc.exists()....." + exists);
		if (!exists) {
			Logger.error("exists", file.createNewFile() ? "TRUE" : "FALSE");
		}
		
		fizeSize = file.length();
	}
	/**
	 * 截取
	 * @param url
	 * @param nPos
	 * @throws IOException
	 */
	public void truncate(String url, long nPos) throws IOException {
		rac =  new RandomAccessFile(url,"rw");
		rac.seek(nPos);
// 
//
//		File file = new File(url);
//		exists = file.exists();
//		System.out.println("fc.exists()....." + exists);
//		if (!exists) {
//			System.out.println(file.createNewFile());
//		}
//		
//		FileOutputStream fos = new FileOutputStream(file);
//		FileChannel fc = fos.getChannel();
//		fc.truncate(nPos);
//	 
//		System.out.println("[truncate]fc.fileSize()....." + fc.size());
//		fc.close();
//		fos.close();
		

	}

	public void openOutputStream(String url) {
//		File file = new File(url);
//		try {
//			os = new FileOutputStream(file);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
	}

	public void close() throws Exception {
		if (rac != null) {
			rac.close();
		}
	}

	public synchronized int write(byte[] b, int nStart, int nLen) {
		int n = -1;
		try {
			rac.write(b, nStart, nLen);
			n = nLen;
		} catch (IOException e) {
			Logger.error("IOException", e.toString());
		}

		return n;
	}

}
