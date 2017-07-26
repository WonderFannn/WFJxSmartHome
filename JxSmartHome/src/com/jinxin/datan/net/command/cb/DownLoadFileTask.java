package com.jinxin.datan.net.command.cb;

import android.content.Context;

import com.jinxin.datan.net.protocol.cb.DownLoadFile;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.internet.NetRequest;
/**
 * 下载文件 通信接口
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class DownLoadFileTask extends InternetTask{
    private NetRequest request = null;
    
    /**
     * 取文件接口 构造方法
     * @param context 
     * @param url   文件中段地址
     * @param saveType 存储类型（0临时存储，1永久存储）
     * @param isHide 是否隐藏文件
     */
    public DownLoadFileTask(Context context, String url,int saveType,boolean isHide) {
        this.init(url,saveType,isHide);
        super.setNetRequest(context, request,null);
        super.setSocketOrHttp((byte) 1);
//        super.setHttpType((byte) 0);
    }
    private void init(String url,int saveType,boolean isHide) {
    	StringBuffer _sbf = new StringBuffer();
    	_sbf.append("http");
    	_sbf.append(url);
        this.request = new NetRequest(_sbf.toString(), new DownLoadFile(this,url,saveType,isHide));
    }
}
