package com.jinxin.jxsmarthome.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import com.jinxin.jxsmarthome.main.JxshApp;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.widget.Toast;

public class Downloader implements Runnable {
	private static final String TAG = "Downloader";
	private Context context;
	private String downloadUrl;
	private String savePath;
	private String fileName;
	private InputStream is;
	private FileOutputStream fos;
	private int downloadSize;
	private int size;
	private DownloadListener downloadListener;
	
	public Downloader(Context context,String downloadUrl, String savePath, String fileName) {
		this.context = context;
		this.downloadUrl = downloadUrl;
		this.savePath = savePath;
		this.fileName = fileName;
	}
	
	public void addDownloadListener(DownloadListener downloadListener) {
		this.downloadListener = downloadListener;
	}
	
	public void downloadFile(String downloadUrl, String savePath, String fileName) {
		/*if the filename is null, use the name in url*/
		if(CommUtil.isEmpty(fileName)) {
			fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);
		}
		Logger.warn(TAG, "download filename:" + fileName);
		
		/*if the sd card is exist, save the file on sdcard. otherwise save it in che cache directory of this app*/
		if(CommUtil.isEmpty(savePath)) {
			String dir = StorageUtil.getAvailStoragePath();
			if(dir == null || dir.equals("null")) {
				JxshApp.showToast(JxshApp.getContext(), "内存空间已满,请清理内存或使用外置SD卡", Toast.LENGTH_SHORT);
				downloadListener.onFail();
				return;
			}
			savePath = dir + "/" + fileName;
//			System.out.println("------>" + savePath);
		}
		Logger.warn(TAG, "download savePath:" + savePath);
		
		/*download the file*/
		try {
			downloadListener.onStart();
			
			URL url = new URL(downloadUrl);
			URLConnection conn = url.openConnection();
			conn.connect();
			
			is = conn.getInputStream();
			size = conn.getContentLength();
			
			if(is == null || size <= 0) {
				Logger.error(TAG, "download error, cannot get data");
				return;
			}
			
			File saveFile = new File(savePath);
			if(!saveFile.exists()) {
				saveFile.createNewFile();
			}
			
			fos = new FileOutputStream(savePath);
			byte[] buffer = new byte[1024];
			int length = 0;
			
			while((length = is.read(buffer)) != -1) {
				fos.write(buffer, 0, length);
				downloadSize += length;
				downloadListener.onProcess(downloadSize, size);
			}
			
			downloadListener.onFinish(savePath);
		} catch (Exception e) {
			e.printStackTrace();
			if (downloadListener != null) {
				downloadListener.onFail();
			}
		} finally {
			try {
				if(is != null) {
					is.close();
				}
				if(fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private float getAvailStorageSize(String path) {
		  StatFs sf = new StatFs(path);
		  long blockSize = sf.getBlockSize();
		  long availCount = sf.getAvailableBlocks();
		  
		  return blockSize*availCount/ (1024*1024);
	}
	
	@Override
	public void run() {
		Logger.warn(TAG, "start download...");
		downloadFile(downloadUrl, savePath, fileName);
	}
	
	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}
	
	/* Checks if external storage is available to read */
	public boolean isExternalStorageReadable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) ||
	        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}
	
	//////////////////////////////////////////////////////////////////////
	// interface for download state control
	//////////////////////////////////////////////////////////////////////
	public interface DownloadListener {
		public void onStart();
		public void onProcess(int downloadSize, int totleSize);
		public void onFinish(String savePath);
		public void onFail();
	}
	
	
	//////////////////////////////////////////////////////////////////////
	// getters and setters
	//////////////////////////////////////////////////////////////////////
	public int getDownloadSize() {
		return downloadSize;
	}

	public int getSize() {
		return size;
	}

}
