package com.jinxin.datan.toolkit.internet;

import com.jinxin.datan.net.protocol.CommandBase;
import com.jinxin.datan.net.protocol.ResponseJson;
import com.jinxin.datan.net.protocol.ResponseXml;
/**
 * 通信事件请求基础接口
 * @author zj
 * 
 */
public interface IRequest {
    public byte getRequestId();

    public CommandBase getCommandBase();

    public void setCommandBase(CommandBase cmd);

    public byte getIntdesFlag();// 加密条件（0不加1加密）
    public String getUrl();
    public void setUrl(String url);
    public ResponseXml getResponseXml();
    public ResponseJson getResponseJson();

}
