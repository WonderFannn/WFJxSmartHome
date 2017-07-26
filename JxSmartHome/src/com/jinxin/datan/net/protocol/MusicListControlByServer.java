package com.jinxin.datan.net.protocol;

import java.io.DataInputStream;
import java.io.InputStream;

import android.text.TextUtils;

import com.jinxin.datan.net.module.RemoteJsonResultInfo;
import com.jinxin.datan.toolkit.task.Task;
/**
 * 歌曲列表解析
 * @author TangLong
 * @company 金鑫智慧
 */
public class MusicListControlByServer extends ResponseJson {
	private Task task = null;
	private byte[] requestBytes;
	
	public MusicListControlByServer(Task task, byte[] requestBytes) {
		this.task = task;
		this.requestBytes = requestBytes;
	}
	
	@Override
	public void response(InputStream in) throws Exception {
		/****test*********/
		if(in != null){
			boolean isSuccess = false;
			DataInputStream dis = null;
			byte[] buf = null;
			try{
				dis = new DataInputStream(in);
				int len = dis.readInt();
				buf = new byte[len];
				dis.read(buf);
				isSuccess = TextUtils.isEmpty(buf.toString()) ? false : true;
				if(isSuccess && buf.toString().equals("错误")){
					isSuccess = false;
				}
			}catch(Exception ex){
				ex.printStackTrace();
				isSuccess = false;
			}finally {
				 RemoteJsonResultInfo resultInfo = new RemoteJsonResultInfo();
				 
				 if(isSuccess){
					 resultInfo.validResultCode = "0000";
					 resultInfo.validResultInfo = "成功";
					 this.setResultInfo(resultInfo);
			            this.task.callback(new String(buf));
			        }else{
						 resultInfo.validResultCode = "1111";
						 resultInfo.validResultInfo = "失败";
						 this.setResultInfo(resultInfo);
			        	this.task.onError("失败");
			        }
			}
		}
	}

	@Override
	public byte[] toOutputBytes() {
		return this.requestBytes;
	}

}
