package com.jinxin.datan.net.protocol.cb;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.jinxin.datan.net.protocol.CommandBase;
import com.jinxin.datan.toolkit.task.Task;
import com.jinxin.record.FileManager;
//import com.edadao.eddhc.util.record.FileManager;
//import com.edadao.eddhc.util.record.FileUtil;
import com.jinxin.record.FileUtil;

/**
 * 下载APK接口接口
 * @author zj
 *
 */
public class CBLoadAPK extends CommandBase{
//    private IdataObserver observer;
	private Task task = null;
    /**
     * 下载APK接口构造方法
     * @param observer 
     */
    public CBLoadAPK(Task task) {
    	this.task = task;
    }
    @Override
    public boolean fromInput(InputStream is) {
        // TODO Auto-generated method stub
    	String _path = FileManager.instance().getAPKPath();
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
	/*	FileOutputStream fos = null;
        boolean success = false;
        String _path = "";
//        _path = FileManager.instance().getFilePath(FileManager.TYPE_APK)+FileManager.APK_NAME;
		try {
//        _path = FileManager.instance().writeAPK(_path, is);
		if (is == null)
			return success;
		if (_path == null || _path.trim().equals(""))
			return success;
		//提示：任务中途检测任务是否被中止，同时可以直接返回任务取消数据（如有必要）
	    	if(this.task.ismTryCancel()){
	    		this.task.onCancel("被取消了");
	    		return false;
	    	}
			fos = new FileOutputStream(FileUtil.reBuildFile(_path));
			byte[] buffer = new byte[1024];
			int len = 0;
	    	if(this.task.ismTryCancel())return false;
			while ((len = is.read(buffer)) != -1) {
				if(this.task.ismTryCancel())return false;
				fos.write(buffer, 0, len);
			}
			success = true;
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			success = false;
		} finally {
			if(this.task.ismTryCancel())_path = null;
	        if(_path != null){
	            this.task.callback(_path);
	        }else{
	        	this.task.onError("APK下载更新失败！");
	        }
			FileUtil.closeFileOutputStream(fos);
			FileUtil.closeInputStream(is);
		}
		return success;
       */
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
