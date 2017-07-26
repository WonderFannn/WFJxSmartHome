package com.jinxin.datan.net.protocol.cb;


import com.jinxin.datan.net.module.AdvertInfo;
import com.jinxin.datan.net.protocol.CommandBase;
import com.jinxin.datan.net.protocol.cf.AdvertCache;
import com.jinxin.datan.toolkit.internet.IdataObserver;

import java.io.DataInputStream;
import java.io.InputStream;

/**
  * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@XML网络文件接口解析示例@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * 解析广告图片接口
 * @author zj
 *
 */
public class DownLoadAvatarImage extends CommandBase{
    private AdvertInfo info;
    private IdataObserver observer = null;
    private boolean isHD = false;
    /**
     * 解析广告图片构造方法
     * @param info 广告信息
     * @param isHD 是否原图
     * @param observer 
     */
    public DownLoadAvatarImage(AdvertInfo info,boolean isHD ,IdataObserver observer) {
        this.info = info;
        this.isHD = isHD;
        this.observer = observer;
    }
    @Override
    public boolean fromInput(InputStream is) {
        // TODO Auto-generated method stub
    	/*if(info == null)return false;
        String _path = FileManager.instance().writeImage(info.getAdvertVO().getId(), is, FileManager.TYPE_AD, isHD);
        if(_path != null){
        	//图片下载成功，修改该缓存文件
        	info.getDataCacheInfo().setOriginalImgPath(_path);
        	new AdvertCache(null, null).writeVO(info);
            observer.callback(info);
            return true; 
        }else{
            observer.onError("图片下载失败");*/
            return false;
//        }
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
