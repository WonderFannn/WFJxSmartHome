package com.jinxin.datan.net.protocol.cb;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.jinxin.datan.net.protocol.CommandBase;
import com.jinxin.datan.toolkit.task.Task;
import com.jinxin.record.FileManager;
import com.jinxin.record.FileUtil;

/**
 * 解析文件接口
 * 
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class DownLoadFile extends CommandBase {
	private Task task = null;
	private String url = null;
	private int saveType = 0;
	private boolean isHide = false;

	/**
	 * 解析文件构造方法
	 * 
	 */
	public DownLoadFile(Task task, String url, int saveType, boolean isHide) {
		this.task = task;
		this.url = url;
		this.saveType = saveType;
		this.isHide = isHide;
	}

	@Override
	public boolean fromInput(InputStream is) {
		// TODO Auto-generated method stub
		String _path = FileManager.instance().getFilePath(this.url,
				this.saveType, this.isHide);
		// 写入文件<拷贝文件工具类FileOutputStream(),便于取消操作>
		boolean success = false;
		FileOutputStream fos = null;
		try {
			if (is == null)
				return success;
			if (_path == null || _path.trim().equals(""))
				return success;
			fos = new FileOutputStream(FileUtil.reBuildFile(_path));
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				if (this.task.ismTryCancel())
					return success;
				fos.write(buffer, 0, len);
			}
			success = true;
			this.task.callback(_path);
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			success = false;
		} finally {
			FileUtil.closeFileOutputStream(fos);
			FileUtil.closeInputStream(is);
		}
		return success;

	}

	@Override
	public byte[] toOutputBytes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toReqString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toResString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean fromInput(DataInputStream dis) {
		// TODO Auto-generated method stub
		return false;
	}

}
