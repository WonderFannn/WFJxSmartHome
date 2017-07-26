
package com.jinxin.datan.net.command.cf;

import com.jinxin.datan.net.protocol.cf.AdvertCache;
import com.jinxin.datan.toolkit.internet.IUIUpdate;
import com.jinxin.datan.toolkit.internet.IdataObserver;
import com.jinxin.datan.toolkit.internet.InternetTask;
import com.jinxin.datan.toolkit.internet.NetRequest;

import android.content.Context;

import java.util.List;

/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@本地文件读写接口示例@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * 广告缓存接口
 * @author JackeyZhang 2013-7-1 下午4:58:15
 */
public class AdvertCacheTask extends InternetTask implements IdataObserver {

    private NetRequest request = null;

    /**
     * 广告缓存接口 构造方法
     * 
     * @param context
     * @param iUIUpdate 通知界面刷新
     */
    public AdvertCacheTask(Context context, List<Object> list,boolean isRead, IUIUpdate iUIUpdate) {
        this.init(list);
        super.setNetRequest(context, request, iUIUpdate);
        super.setSocketOrHttp((byte) 4);
        super.setFileReadOrWrite((byte)(isRead ? 0 : 1));
    }

    private void init(List<Object> list) {
        this.request = new NetRequest(new AdvertCache(list, this));
    }

    @Override
    public void callback(Object... obj) {
        // TODO Auto-generated method stub
    	List<Object> _list = (List<Object>)obj[0];
        super.callback(_list);
    }

    @Override
    public void onError(Object... obj) {
        // TODO Auto-generated method stub
    	List<Object> _list = (List<Object>)obj[0];
    	String _error = obj[1].toString();
        super.onError(_list,_error);
    }
}
