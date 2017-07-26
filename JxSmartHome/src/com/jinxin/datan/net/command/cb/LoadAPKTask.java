
package com.jinxin.datan.net.command.cb;

//import com.edadao.datan.android.main.DatanAgent;

import com.jinxin.datan.net.protocol.cb.CBLoadAPK;
import com.jinxin.datan.toolkit.internet.IUIUpdate;
import com.jinxin.datan.toolkit.internet.IdataObserver;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.internet.NetRequest;

import android.content.Context;

/**
 * 下载APK接口
 * 
 * @author zj
 */
public class LoadAPKTask extends InternetTask implements IdataObserver {
    public String savedPath = null;// 存储路径

    private NetRequest request = null;

	/**
     * 下载APK接口 构造方法
     * 
     * @param context
     * @param iUIUpdate 通知界面刷新
     */
    public LoadAPKTask(Context context,String url) {
        this.init(url);
        super.setNetRequest(context, request, null);
        super.setSocketOrHttp((byte) 1);
//        super.setHttpType((byte) 0);
    }

    private void init(String path) {
        StringBuffer url = new StringBuffer();
        url.append(path);
//        url.append(DatanAgentConnectResource.HOST+DatanAgentConnectResource.URL+"/images/ad/"+info.getName()+".jpg");
        this.request = new NetRequest(url.toString(), new CBLoadAPK(this));

    }

}
