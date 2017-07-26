package com.zhy.imageloader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import net.tsz.afinal.bitmap.download.SimpleDownloader;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;

import com.jinxin.datan.net.DatanAgentConnectResource;

public class CommonDownloader extends SimpleDownloader {

	
	private FTPClient ftpClient;
	
	public CommonDownloader() {
		this.ftpClient = new FTPClient();
	}
	
	@Override
	public byte[] download(String urlString) {
		if (urlString == null)
			return null;

		if (urlString.trim().toLowerCase().startsWith("/")) {//FTP
			return getFromFtp(urlString);
		}
		return super.download(urlString);
	}
	
	public byte[] getFromFtp(String serverPath) {

		InputStream in = null;
		try {
			this.openConnect();

			in = ftpClient.retrieveFileStream(serverPath);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int b;
			while ((b = in.read()) != -1) {
				baos.write(b);
			}
			return baos.toByteArray();
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				this.closeConnect();
			} catch (final IOException e) {
			}
		}
	}

	public void openConnect() throws IOException {
		ftpClient.setControlEncoding("UTF-8");
		int reply;
		ftpClient.connect(DatanAgentConnectResource.FTP_SERVER, DatanAgentConnectResource.FTP_PORT);
		reply = ftpClient.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			ftpClient.disconnect();
			throw new IOException("connect fail: " + reply);
		}
		ftpClient.login(DatanAgentConnectResource.FTP_USERNAME, DatanAgentConnectResource.FTP_PASSWORD);
		reply = ftpClient.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			ftpClient.disconnect();
			throw new IOException("connect fail: " + reply);
		} else {
			FTPClientConfig config = new FTPClientConfig(ftpClient
					.getSystemType().split(" ")[0]);
			config.setServerLanguageCode("zh");
			ftpClient.configure(config);
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
		}
	}

	public void closeConnect() throws IOException {
		if (ftpClient != null) {
			ftpClient.logout();
			ftpClient.disconnect();
		}
	}

}
