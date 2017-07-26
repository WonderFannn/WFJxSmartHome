package com.jinxin.jxsmarthome.test;

import java.util.concurrent.TimeUnit;

import android.os.Environment;
import android.test.AndroidTestCase;

import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.util.Downloader;
import com.jinxin.jxsmarthome.util.Downloader.DownloadListener;
import com.jinxin.jxsmarthome.util.Logger;

public class LoggerTest extends AndroidTestCase {
	public void testLogger() {
		Logger.error(null, "haha");
		Logger.warn("tangl", "hehe");
		Logger.debug("tangl", "enen");
		Logger.info("tangl", "hoho");
	}
	
	public void testString() {
//		String shortCutOperation = "[\"open\",\"close\"]";
		String shortCutOperation = "[\"\"]";
		String shortcutOpen = shortCutOperation.substring(
				(shortCutOperation.indexOf("\"", 0) + 1), 
				shortCutOperation.indexOf("\"", shortCutOperation.indexOf("\"") + 1));
		System.out.println("-->" + shortcutOpen);
	}
	
	public void testProductPatternOperationToCMD_V1() {
		String operationType = ProductConstants.FUN_TYPE_MASTER_CONTROLLER_LIGHT;
//		CommonMethod.productPatternOperationToCMD_V1(getContext(), operationType, ppoList, params);
	}
	
	public void testGetSpName() {
		Logger.warn("tangl", "1:" + getContext().getFilesDir().getAbsolutePath());
		Logger.warn("tangl", "2:" + Environment.getExternalStorageDirectory().getAbsolutePath());
	}
	
	public void testDownloader() {
		Downloader downloader = new Downloader(getContext(), "http://www.easyn.cn/download/P2PCam264.apk",
				null, null);
		new Thread(downloader).start();
		
		downloader.addDownloadListener(new DownloadListener() {
			@Override
			public void onStart() {
				
			}
			
			@Override
			public void onProcess(int downloadSize, int totleSize) {
				Logger.warn("tangl", downloadSize + "");
				Logger.warn("tangl", totleSize + "");
			}
			
			@Override
			public void onFinish(String savePath) {
				Logger.warn("tangl", savePath);
			}

			@Override
			public void onFail() {
				// TODO Auto-generated method stub
				
			}
		});
		
		try {
			TimeUnit.SECONDS.sleep(60);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
