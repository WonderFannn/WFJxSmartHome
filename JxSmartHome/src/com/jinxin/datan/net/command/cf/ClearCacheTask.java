
package com.jinxin.datan.net.command.cf;

import android.content.Context;

import com.jinxin.datan.net.protocol.cf.ClearCache;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.internet.NetRequest;

/**
 * 清空缓存接口(目前只有本地图片缓存)
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class ClearCacheTask extends InternetTask {

    private NetRequest request = null;

    /**
     *  清空缓存接口 构造方法
     * 
     * @param context
     */
    public ClearCacheTask(Context context) {
        this.init();
        super.setNetRequest(context, request, null);
        super.setSocketOrHttp((byte) 4);
        super.setFileReadOrWrite((byte)1);
    }

    private void init() {
        this.request = new NetRequest(new ClearCache(this));
    }

}
