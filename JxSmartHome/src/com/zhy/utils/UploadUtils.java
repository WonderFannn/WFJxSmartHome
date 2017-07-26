package com.zhy.utils;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class UploadUtils {

	public static void postFile(File file) {
		HttpClient httpclient = new DefaultHttpClient();

		HttpPost httppost = new HttpPost("http://192.168.60.113:9088/smarthome/system/feedback/insert");

		MultipartEntity mpEntity = new MultipartEntity();
		FileBody cbFile = new FileBody(file);
		mpEntity.addPart("file", cbFile);
		httppost.setEntity(mpEntity);
		
		HttpResponse response;
		try {
			response = httpclient.execute(httppost);
			HttpEntity resEntity = response.getEntity();
			
			if (resEntity != null) {
				System.out.println(EntityUtils.toString(resEntity));
			}
			if (resEntity != null) {
				resEntity.consumeContent();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		httpclient.getConnectionManager().shutdown();
	}
}
