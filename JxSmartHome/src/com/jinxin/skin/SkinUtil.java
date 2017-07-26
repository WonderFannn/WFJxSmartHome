package com.jinxin.skin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 换肤工具类
 * 
 * @author JackeyZhang 2013-6-25 下午1:58:37
 */
public class SkinUtil {
	/**
	 * 本地安装APK
	 * 
	 * @param act
	 * @param apkPath
	 *            本地存放路径
	 */
	public static void InstallAPK(Activity act, String apkPath) {
		// TODO Auto-generated method stub
		// 代码安装
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// intent.setDataAndType(Uri.parse("file://"+fileName), "application/vnd.android.package-archive");
		intent.setDataAndType(Uri.fromFile(new File(apkPath)), "application/vnd.android.package-archive");
		act.startActivity(intent);
	}

	/**
	 * 卸载APK
	 * 
	 * @param act
	 * @param packageName
	 *            APK包名
	 */
	public static void uninstallAPK(Activity act, String packageName) {
		Uri packageURI = Uri.parse("package:" + packageName);
		Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
		act.startActivity(uninstallIntent);
	}

	/**
	 * 检测APK是否已经安装（针对皮肤包增加sharedUserId是否存在判定）
	 * 
	 * @param context
	 * @param pak
	 * @return
	 */
	public static boolean checkInstallAPK(Context context, String pak) {
		boolean install = false;
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(pak, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
			if (info != null && info.sharedUserId.length() > 0) {
				install = true;
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return install;
	}
/**
 * 文件（APK）下载
 * @param httpUrl 网络地址
 * @param savePath 本地存放地址
 * @return
 */
	public static File downLoadFile(String httpUrl, String savePath) {
		// TODO Auto-generated method stub
		File file = new File(savePath);
		try {
			URL url = new URL(httpUrl);
			try {
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				InputStream is = conn.getInputStream();
				FileOutputStream fos = new FileOutputStream(file);
				byte[] buf = new byte[256];
				conn.connect();
				int count = 0;
				if (conn.getResponseCode() == 200) {
					while ((count = is.read(buf)) > 0) {

						fos.write(buf, 0, count);
					}
				}
				conn.disconnect();
				fos.close();
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block

				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return file;
	}
}
