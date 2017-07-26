package com.jinxin.datan.net.protocol;

import java.io.DataInputStream;
import java.io.InputStream;
/**
 * 数据流方式联网解析父类
 * @author zj
 *
 */
public abstract class CommandBase {
	public abstract boolean fromInput(DataInputStream dis);
    
	public abstract boolean fromInput(InputStream is);

    public abstract byte[] toOutputBytes();

    public abstract String toReqString();

    public abstract String toResString();
}
