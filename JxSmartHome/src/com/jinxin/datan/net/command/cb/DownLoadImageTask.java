package com.jinxin.datan.net.command.cb;

import android.content.Context;
import android.net.Uri;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.protocol.cb.DownLoadImage;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.internet.NetRequest;
/**
 * 取图片 通信接口
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class DownLoadImageTask extends InternetTask{
    private NetRequest request = null;
    
    /**
     * 取图片接口 构造方法
     * @param context 
     * @param url  图片中段地址
     * @param saveType 存储类型（0临时存储，1永久存储）
     * @param isHide 是否隐藏图片文件
     */
    public DownLoadImageTask(Context context, String url,int saveType, boolean isHide) {
        this.init(url,saveType,isHide);
//        super.setNetRequest(null, request,null);
        super.setNetRequest(context, request,null);
        super.setSocketOrHttp((byte) 1);
    }
    private void init(String url,int saveType,boolean isHide) {
    	StringBuffer _sbf = new StringBuffer();
    	_sbf.append(DatanAgentConnectResource.HTTP_ICON_PATH);
    	String headUrl = url.substring(0, url.lastIndexOf("/")+1);
    	String nameUrl = url.substring(url.lastIndexOf("/")+1);
    	_sbf.append(headUrl);
    	_sbf.append(Uri.encode(nameUrl, "utf-8"));
        this.request = new NetRequest(_sbf.toString(), new DownLoadImage(this,url,saveType,isHide));
    }
}
