
package com.jinxin.datan.net.command.cb;

//import com.edadao.datan.android.main.DatanAgent;
import com.jinxin.datan.net.module.AdvertInfo;
import com.jinxin.datan.net.protocol.cb.DownLoadAvatarImage;
import com.jinxin.datan.toolkit.internet.IUIUpdate;
import com.jinxin.datan.toolkit.internet.IdataObserver;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.internet.NetRequest;

import android.content.Context;
import com.jinxin.jxsmarthome.util.Logger;

/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@取网络文件流示例@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * 取广告图片接口
 * 
 * @author zj
 */
public class DownLoadAvatarImageTask extends InternetTask implements IdataObserver {
    private NetRequest request = null;

    /**
     * 取广告图片接口 构造方法
     * 
     * @param context
     * @param info 广告信息
     * @param isHD 是否原图
     * @param iUIUpdate 通知界面刷新
     */
    public DownLoadAvatarImageTask(Context context, Object info,boolean isHD, IUIUpdate iUIUpdate) {
        this.init((AdvertInfo)info,isHD);
        super.setNetRequest(context, request, iUIUpdate);
        super.setSocketOrHttp((byte) 1);
//        super.setHttpType((byte) 0);
    }

    private void init(AdvertInfo info,boolean isHD) {
        StringBuffer url = new StringBuffer();
//        url.append(info.getAdvertVO().getImgUrl());
        this.request = new NetRequest(url.toString(), new DownLoadAvatarImage(info,isHD, this));
    }

    @Override
    public void callback(Object... obj) {
        // TODO Auto-generated method stub
        super.callback(obj[0]);
    }

    @Override
    public void onError(Object... obj) {
        // TODO Auto-generated method stub
        super.onError(obj[0].toString());
    }
}
