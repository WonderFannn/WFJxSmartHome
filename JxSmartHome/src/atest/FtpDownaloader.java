package atest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.test.AndroidTestCase;

import com.jinxin.db.impl.FeedbackDaoImpl;
import com.jinxin.jxsmarthome.entity.Feedback;
import com.jinxin.record.FileManager;
import com.zhy.utils.ZipUtils;

public class FtpDownaloader extends AndroidTestCase {

	public FtpDownaloader() {
		
	}
	
	public void testCachePath() {
		System.out.println(FileManager.instance().getCacheImagePath(0));
	}
	
	public void testZipFile() {
		ArrayList<File> list = new ArrayList<File>();
		list.add(new File("/sdcard/logs_360"));
		list.add(new File("/sdcard/shuame.log"));
		
		File file = new File("/sdcard/"+String.valueOf(System.currentTimeMillis())+".zip");
		try {
			ZipUtils.zipFiles(list, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void testQueryFeedback() {
		FeedbackDaoImpl daoImpl = new FeedbackDaoImpl(getContext());
		List<Feedback> list = daoImpl.find(null,"messageType = ?",new String[]{String.valueOf(1)},null,null,null,null);
		
	}
	
	public void testTmpZipFile() {
		ArrayList<File> list = new ArrayList<File>();
		list.add(new File("/sdcard/logs_360"));
		list.add(new File("/sdcard/shuame.log"));
		
		try {
			FileManager _fm = new FileManager();
			String filePath =_fm.getSDPath() + FileManager.PROJECT_NAME +FileManager.CACHE;
			File zipFile = new File(filePath, String.valueOf(System.currentTimeMillis())+".zip");
			ZipUtils.zipFiles(list, zipFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
