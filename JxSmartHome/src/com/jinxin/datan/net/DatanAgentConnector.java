package com.jinxin.datan.net;



import com.jinxin.datan.net.module.RemoteErrorInfo;
import com.jinxin.datan.net.module.RemoteJsonResultInfo;
import com.jinxin.datan.net.protocol.CommandBase;
import com.jinxin.datan.net.protocol.ResponseJson;
import com.jinxin.datan.net.protocol.ResponseXml;
import com.jinxin.datan.toolkit.task.Task;
import com.jinxin.jxsmarthome.util.Logger;

import com.jinxin.jxsmarthome.util.Logger;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
/**
 * 网络数据代理器
 * @author JackeyZhang
 *
 */
public class DatanAgentConnector {
    public SocketConnector sc;
    public HttpConnector hc;
    // public static byte[] getRootBytes;
    public byte[] resBytes;
    private static long firstConnectTime;
    public short processStatus = 0; // 0--处理成功 1－网络数据异常 2- 超时 3－用户取消 4- 数据解析异常\
    public String msg = null;// 服务器错误信息

    /**
     * 联网
     * 
     * @param cmd
     *            协议命令
     * @param cmdBase
     *            接口模块
     * @param desFlag
     *            加密条件（0不加1加密）
     * @param connectType
     *            联网模式（0普通连接，1wap连接）
     */
    public int connectSocket(byte cmd, CommandBase cmdBase, byte desFlag, byte connectType) {
        SocketConnector.setStop((byte)0);
        this.msg = "";
        int state = Task.STATE_INIT;
        byte[] requestBody = cmdBase.toOutputBytes();
//        EncHeader encHeader = new EncHeader();
//        encHeader.encrpt = desFlag;
//        ReqHeader reqHeader = new ReqHeader();
//        reqHeader.command = cmd;
//        byte[] requestHeader = reqHeader.toBytes(requestBody.length);
        byte[] request = requestBody;//encHeader.toBytes(requestHeader, requestBody);
        ByteArrayInputStream bis = null;
        DataInputStream dis = null;

        try {
            // resBytes = HttpConnector.connect(appURL, null, "POST", request);
            // SocketConnector sc = new SocketConnector();
            // resBytes = sc.connectAsWap(appURL, null, "POST", request);

            if ((System.currentTimeMillis() - firstConnectTime) > DatanAgentConnectResource.DEFAULT_CONNECT_TIME_OUT) {
                if (sc != null)
                    sc.closeSocket();
                sc = null;
            }

            if (sc == null) {
                firstConnectTime = System.currentTimeMillis();
                sc = new SocketConnector();
            }

            /*
             * if (cmd == IMusicConnectResource.CM_GETROOT) { if (getRootBytes
             * != null) { resBytes = new byte[getRootBytes.length];
             * System.arraycopy(getRootBytes, 0, resBytes, 0,
             * getRootBytes.length); } else { // request = IMusicDemoActivity.b;
             * // resBytes = sc.connectAsWap(appURL, null, "POST", request); //
             * resBytes = sc.connect1(appURL, request); resBytes =
             * sc.connect(ip,port, request);
             * 
             * Logger.error("ddddddddddddddddddddd", ""+resBytes.length); //失败重连接一次
             * if(SocketConnector.stop != 0) { SocketConnector.stop = 0; //
             * resBytes = sc.connectAsWap(appURL, null, "POST", request); //
             * resBytes = sc.connect1(appURL, request); resBytes =
             * sc.connect(ip,port, request);
             * 
             * Logger.error("ffffffffffffffffffffff", ""+resBytes.length);
             * 
             * }
             * 
             * if (resBytes != null) { getRootBytes = new byte[resBytes.length];
             * System.arraycopy(resBytes, 0, getRootBytes, 0, resBytes.length);
             * } } } else { if(socketIP == null ||socketPort == null) { //解析域名
             * //ip = InetAddress.getByName(ip);
             * 
             * } resBytes = sc.connect(ip, port, request); }
             */
            switch (connectType) {
            case 0:
                resBytes = sc.connect(DatanAgentConnectResource.appURL, request);
                break;
            case 1:
                resBytes = sc.connectAsWap(DatanAgentConnectResource.appURL, request);
                break;
            }
            if (resBytes == null) {
                processStatus = 1;
                state = Task.STATE_FAIL;
            } else {
                bis = new ByteArrayInputStream(resBytes);
                dis = new DataInputStream(bis);
//                encHeader.fromInputStream(dis);
//                dis.close();
//                bis = new ByteArrayInputStream(encHeader.encrptBytes);
//                dis = new DataInputStream(bis);
//                ResHeader resHeader = new ResHeader();
//                resHeader.fromInputStream(dis);
                String message = null;
                this.msg = message;
                // Tracer.debug("encHeader.encrpt*******************......" +
                // encHeader.encrpt);
   
                if (message == null || message.equals("")) {
                    //解析
                    cmdBase.fromInput(dis);
                    state = Task.STATE_SUCCESS;
                } else {
                    state = Task.STATE_FAIL;
                }
            }
            firstConnectTime = System.currentTimeMillis();
        } catch (Exception ex) {
            processStatus = 4;
            // getRootBytes = null;
            sc.closeSocket();
            sc = null;
            SocketConnector.setStop((byte)2);
            Logger.error("DatanAgentConnector", ex.toString());
            state = Task.STATE_FAIL;
        } finally {
            SocketConnector.setStop((byte)2);
            try {
                if (dis != null) {
                    dis.close();
                }
                if (bis != null) {
                    bis.close();
                }
            } catch (Exception ex) {
            	Logger.error("DatanAgentConnector", ex.toString());
            }
        }

        return state;
    }
    /**
     * 联网并解析json接口
     * @param url 连接地址
     * @param response 解析接口
     * @return 联网状态
     */
    public int connectSocketForJson(String url,ResponseJson response){
        int state = Task.STATE_INIT;
        InputStream is = null;
        try {
            if ((System.currentTimeMillis() - firstConnectTime) > DatanAgentConnectResource.DEFAULT_CONNECT_TIME_OUT) {
                if (sc != null)
                	sc.closeSocket();
                sc = null;
            }
            if (sc == null) {
                firstConnectTime = System.currentTimeMillis();
                sc = new SocketConnector();
            }
//            switch (connectType) {
//            case 0:
//                is = hc.connect(url, "post", response.toOutputBytes());
//                break;
//            case 1:
//                is = hc.connect(url, "get", null);
//                break;
//            }
            is = sc.connectToIs(url, response.toOutputBytes());
//            Logger.e("url=", url);
            if (is == null) {
                state = Task.STATE_FAIL;
            } else {
//                String message = response.getErrorInfo().ValidErrorInfo;//resHeader.statusMSG.cluv.getClvString();
//                this.msg = message;
                // Tracer.debug("encHeader.encrpt*******************......" +
                // encHeader.encrpt);
                    //解析
                    response.response(is);
                    this.msg = response.getResultInfo().validResultInfo;
//                    Logger.e("--msg------>", this.msg);
                if ("0000".equals(response.getResultInfo().validResultCode)) {
                    state = Task.STATE_SUCCESS;
                } else {
                    state = Task.STATE_FAIL;
                }
            }
            firstConnectTime = System.currentTimeMillis();
        } catch (Exception ex) {
            // getRootBytes = null;
        	if (sc != null)
        		sc.closeSocket();
        	 	sc = null;
        	 	Logger.error("DatanAgentConnector", ex.toString());
        	 	state = Task.STATE_FAIL;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (sc != null)
                	sc.closeSocket();
                	sc.closeOutputStream();
                	sc = null;
            } catch (Exception ex) {
            	Logger.error("DatanAgentConnector", ex.toString());
            }
        }
        return state;
        
    }
    /**
     * 清socket连接器
     */
    public void clearSocketConnector(){
    	if (sc != null){
			sc.closeOutputStream();
			if (sc != null) {
				sc.closeInputStream();
			}
			if (sc != null) {
				sc.closeSocket();
			}
        	sc = null;
        }
    }
    /**
     * 联网并解析json接口(socket为长连接模式，需手动关闭socket)
     * @param url 连接地址
     * @param response 解析接口
     * @return 联网状态
     */
    public int connectSocketLongForJson(String url,ResponseJson response){
        int state = Task.STATE_INIT;
        InputStream is = null;
        try {
            if ((System.currentTimeMillis() - firstConnectTime) > DatanAgentConnectResource.DEFAULT_CONNECT_TIME_OUT) {
                if (sc != null){
                	sc.closeInputStream();
                	sc.closeSocket();
                	sc = null;
                }
            }
            if (sc == null) {
//                firstConnectTime = System.currentTimeMillis();
                sc = new SocketConnector();
            }
//            switch (connectType) {
//            case 0:
//                is = hc.connect(url, "post", response.toOutputBytes());
//                break;
//            case 1:
//                is = hc.connect(url, "get", null);
//                break;
//            }
            is = sc.connectToLong(url, response.toOutputBytes());
//            Logger.e("url=", url);
            if (is == null) {
            	RemoteJsonResultInfo resultInfo = new RemoteJsonResultInfo();
            	resultInfo.validResultCode = "0404";
				resultInfo.validResultInfo = "网络异常";
				response.setResultInfo(resultInfo);
                state = Task.STATE_FAIL;
            } else {
//                String message = response.getErrorInfo().ValidErrorInfo;//resHeader.statusMSG.cluv.getClvString();
//                this.msg = message;
                // Tracer.debug("encHeader.encrpt*******************......" +
                // encHeader.encrpt);
                    //解析
                    response.response(is);
                    this.msg = response.getResultInfo().validResultInfo;
//                    Logger.e("--msg------>", this.msg);
                if ("0000".equals(response.getResultInfo().validResultCode)) {
                	Logger.debug("DatanAgentConnector", "go success");
                    state = Task.STATE_SUCCESS;
                } else {
                	Logger.debug("DatanAgentConnector", "go fail");
                    state = Task.STATE_FAIL;
                }
            }
            firstConnectTime = System.currentTimeMillis();
        } catch (Exception ex) {
        	Logger.debug("DatanAgentConnector", "go exception");
        	ex.printStackTrace();
            // getRootBytes = null;
        	if (sc != null){
        		sc.closeInputStream();
        		sc.closeSocket();
        	 	sc = null;
        	}
        	 	Logger.error("DatanAgentConnector", ex.toString());
        	 	state = Task.STATE_FAIL;
        } finally {
//            try {
//                if (is != null) {
//                    is.close();
//                }
//                if (sc != null)
//                	sc.closeSocket();
//                	sc.closeOutputStream();
//                	sc = null;
//            } catch (Exception ex) {
//            	Logger.error("DatanAgentConnector", ex.toString());
//            }
        }
        return state;
        
    }
    /**
     * 联网
     * 
     * @param cmd
     *            协议命令
     * @param cmdBase
     *            接口模块
     * @param desFlag
     *            加密条件（0不加1加密）
     * @param connectType
     *            联网模式（0普通连接，1wap连接）
     */
    public int connectHttp(byte cmd, CommandBase cmdBase, byte desFlag, byte connectType) {
        this.msg = "";
        int state = Task.STATE_INIT;
        byte[] requestBody = cmdBase.toOutputBytes();
//        EncHeader encHeader = new EncHeader();
//        encHeader.encrpt = desFlag;
//        ReqHeader reqHeader = new ReqHeader();
//        reqHeader.command = cmd;
//        byte[] requestHeader = reqHeader.toBytes(requestBody.length);
        byte[] request = requestBody;//encHeader.toBytes(requestHeader, requestBody);
        ByteArrayInputStream bis = null;
        DataInputStream dis = null;

        try {
            // resBytes = HttpConnector.connect(appURL, null, "POST", request);
            // SocketConnector sc = new SocketConnector();
            // resBytes = sc.connectAsWap(appURL, null, "POST", request);

            if ((System.currentTimeMillis() - firstConnectTime) > DatanAgentConnectResource.DEFAULT_CONNECT_TIME_OUT) {
                if (hc != null)
                    hc.closeHttp();
                hc = null;
            }

            if (hc == null) {
                firstConnectTime = System.currentTimeMillis();
                hc = new HttpConnector();
            }

            /*
             * if (cmd == IMusicConnectResource.CM_GETROOT) { if (getRootBytes
             * != null) { resBytes = new byte[getRootBytes.length];
             * System.arraycopy(getRootBytes, 0, resBytes, 0,
             * getRootBytes.length); } else { // request = IMusicDemoActivity.b;
             * // resBytes = sc.connectAsWap(appURL, null, "POST", request); //
             * resBytes = sc.connect1(appURL, request); resBytes =
             * sc.connect(ip,port, request);
             * 
             * Logger.error("ddddddddddddddddddddd", ""+resBytes.length); //失败重连接一次
             * if(SocketConnector.stop != 0) { SocketConnector.stop = 0; //
             * resBytes = sc.connectAsWap(appURL, null, "POST", request); //
             * resBytes = sc.connect1(appURL, request); resBytes =
             * sc.connect(ip,port, request);
             * 
             * Logger.error("ffffffffffffffffffffff", ""+resBytes.length);
             * 
             * }
             * 
             * if (resBytes != null) { getRootBytes = new byte[resBytes.length];
             * System.arraycopy(resBytes, 0, getRootBytes, 0, resBytes.length);
             * } } } else { if(socketIP == null ||socketPort == null) { //解析域名
             * //ip = InetAddress.getByName(ip);
             * 
             * } resBytes = sc.connect(ip, port, request); }
             */
            switch (connectType) {
            case 0:
                resBytes = hc.connect(DatanAgentConnectResource.appURL, null, "post", request);
                break;
            case 1:
                resBytes = hc.connect(DatanAgentConnectResource.appURL, null, "get", request);
                break;
            }
            if (resBytes == null) {
                processStatus = 1;
                state = Task.STATE_FAIL;
            } else {
                bis = new ByteArrayInputStream(resBytes);
                dis = new DataInputStream(bis);
//                encHeader.fromInputStream(dis);
//                dis.close();
//                bis = new ByteArrayInputStream(encHeader.encrptBytes);
//                dis = new DataInputStream(bis);
//                ResHeader resHeader = new ResHeader();
//                resHeader.fromInputStream(dis);
                String message = null;//resHeader.statusMSG.cluv.getClvString();
                this.msg = message;
                // Tracer.debug("encHeader.encrpt*******************......" +
                // encHeader.encrpt);
                if (message == null || message.equals("")) {
                    //解析
                    cmdBase.fromInput(dis);
                    state = Task.STATE_SUCCESS;
                } else {
                    state = Task.STATE_FAIL;
                }
            }
            firstConnectTime = System.currentTimeMillis();
        } catch (Exception ex) {
            processStatus = 4;
            // getRootBytes = null;
            if (hc != null)
            hc.closeHttp();
            hc = null;
            Logger.error("DatanAgentConnector", ex.toString());
            state = Task.STATE_FAIL;
        } finally {
            try {
                if (dis != null) {
                    dis.close();
                }
                if (bis != null) {
                    bis.close();
                }
            } catch (Exception ex) {
            	Logger.error("DatanAgentConnector", ex.toString());
            }
        }

        return state;
    }
    /**
     * 联网
     * 
     * @param url
     *            连接地址
     * @param cmdBase
     *            接口模块
     * @param desFlag
     *            加密条件（0不加1加密）
     * @param connectType
     *            联网模式（0普通连接，1wap连接）
     */
    public int connectHttp(String url, CommandBase cmdBase, byte desFlag, byte connectType) {
        this.msg = "";
        int state = Task.STATE_INIT;
        byte[] requestBody = cmdBase.toOutputBytes();
//        EncHeader encHeader = new EncHeader();
//        encHeader.encrpt = desFlag;
//        ReqHeader reqHeader = new ReqHeader();
//        reqHeader.command = cmd;
//        byte[] requestHeader = reqHeader.toBytes(requestBody.length);
        byte[] request = requestBody;//encHeader.toBytes(requestHeader, requestBody);
        ByteArrayInputStream bis = null;
        DataInputStream dis = null;

        try {
            // resBytes = HttpConnector.connect(appURL, null, "POST", request);
            // SocketConnector sc = new SocketConnector();
            // resBytes = sc.connectAsWap(appURL, null, "POST", request);

            if ((System.currentTimeMillis() - firstConnectTime) > DatanAgentConnectResource.DEFAULT_CONNECT_TIME_OUT) {
                if (hc != null)
                    hc.closeHttp();
                hc = null;
            }

            if (hc == null) {
                firstConnectTime = System.currentTimeMillis();
                hc = new HttpConnector();
            }

            /*
             * if (cmd == IMusicConnectResource.CM_GETROOT) { if (getRootBytes
             * != null) { resBytes = new byte[getRootBytes.length];
             * System.arraycopy(getRootBytes, 0, resBytes, 0,
             * getRootBytes.length); } else { // request = IMusicDemoActivity.b;
             * // resBytes = sc.connectAsWap(appURL, null, "POST", request); //
             * resBytes = sc.connect1(appURL, request); resBytes =
             * sc.connect(ip,port, request);
             * 
             * Logger.error("ddddddddddddddddddddd", ""+resBytes.length); //失败重连接一次
             * if(SocketConnector.stop != 0) { SocketConnector.stop = 0; //
             * resBytes = sc.connectAsWap(appURL, null, "POST", request); //
             * resBytes = sc.connect1(appURL, request); resBytes =
             * sc.connect(ip,port, request);
             * 
             * Logger.error("ffffffffffffffffffffff", ""+resBytes.length);
             * 
             * }
             * 
             * if (resBytes != null) { getRootBytes = new byte[resBytes.length];
             * System.arraycopy(resBytes, 0, getRootBytes, 0, resBytes.length);
             * } } } else { if(socketIP == null ||socketPort == null) { //解析域名
             * //ip = InetAddress.getByName(ip);
             * 
             * } resBytes = sc.connect(ip, port, request); }
             */
            switch (connectType) {
            case 0:
                resBytes = hc.connect(url, null, "post", request);
                break;
            case 1:
                resBytes = hc.connect(url, null, "get", request);
                break;
            }
            if (resBytes == null) {
                processStatus = 1;
                state = Task.STATE_FAIL;
            } else {
                bis = new ByteArrayInputStream(resBytes);
                dis = new DataInputStream(bis);
//                encHeader.fromInputStream(dis);
//                dis.close();
//                bis = new ByteArrayInputStream(encHeader.encrptBytes);
//                dis = new DataInputStream(bis);
//                ResHeader resHeader = new ResHeader();
//                resHeader.fromInputStream(dis);
                String message = null;//resHeader.statusMSG.cluv.getClvString();
                this.msg = message;
                // Tracer.debug("encHeader.encrpt*******************......" +
                // encHeader.encrpt);
                if (message == null || message.equals("")) {
                    //解析
                    cmdBase.fromInput(dis);
                    state = Task.STATE_SUCCESS;
                } else {
                    state = Task.STATE_FAIL;
                }
            }
            firstConnectTime = System.currentTimeMillis();
        } catch (Exception ex) {
            processStatus = 4;
            // getRootBytes = null;
            if (hc != null)
            hc.closeHttp();
            hc = null;
            Logger.error("DatanAgentConnector", ex.toString());
            state = Task.STATE_FAIL;
        } finally {
            try {
                if (dis != null) {
                    dis.close();
                }
                if (bis != null) {
                    bis.close();
                }
            } catch (Exception ex) {
            	Logger.error("DatanAgentConnector", ex.toString());
            }
        }

        return state;
    }
    /**
     * 联网并解析输入流接口
     * @param url 连接地址
     * @param response 解析接口
     * @param connectType 传1为get方式，0为post方式
     * @return 联网状态
     */
    public int connectHttp(String url,CommandBase commandBase,byte connectType){
        int state = Task.STATE_INIT;
        InputStream is = null;
        try {
            if ((System.currentTimeMillis() - firstConnectTime) > DatanAgentConnectResource.DEFAULT_CONNECT_TIME_OUT) {
                if (hc != null)
                    hc.closeHttp();
                hc = null;
            }
            if (hc == null) {
                firstConnectTime = System.currentTimeMillis();
                hc = new HttpConnector();
            }
            switch (connectType) {
            case 0:
                is = hc.connect(url, "post", commandBase.toOutputBytes());
                break;
            case 1:
                is = hc.connect(url, "get", commandBase.toOutputBytes());
                break;
            }
            if (is == null) {
                state = Task.STATE_FAIL;
            } else {
//                String message = response.getErrorInfo().ValidErrorInfo;//resHeader.statusMSG.cluv.getClvString();
//                this.msg = message;
                // Tracer.debug("encHeader.encrpt*******************......" +
                // encHeader.encrpt);
                
                    //解析
                if(commandBase.fromInput(is)){
                    state = Task.STATE_SUCCESS;
                }else{
                    state = Task.STATE_FAIL;
                }
                    
//                    RemoteErrorInfo info = response.getErrorInfo();
//                    if(info != null){
//                        this.msg = info.ValidErrorInfo;  
//                    }
//                if (this.msg == null || this.msg.equals("")) {
//                    state = Task.STATE_SUCCESS;
//                } else {
//                    state = Task.STATE_FAIL;
//                }
            }
            firstConnectTime = System.currentTimeMillis();
        } catch (Exception ex) {
            // getRootBytes = null;
        	if (hc != null)
            hc.closeHttp();
            hc = null;
            Logger.error("DatanAgentConnector", ex.toString());
            state = Task.STATE_FAIL;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (hc != null)
                hc.closeHttp();
                hc = null;
            } catch (Exception ex) {
            	Logger.error("DatanAgentConnector", ex.toString());
            }
        }
        return state;
        
    }
    /**
     * 联网并解析xml接口
     * @param url get方式连接地址
     * @param response 解析接口
     * @param connectType 传1为get方式，0为post方式
     * @return 联网状态
     */
    public int connectHttpForXML(String url,ResponseXml response,byte connectType){
        int state = Task.STATE_INIT;
        InputStream is = null;
        try {
            if ((System.currentTimeMillis() - firstConnectTime) > DatanAgentConnectResource.DEFAULT_CONNECT_TIME_OUT) {
                if (hc != null)
                    hc.closeHttp();
                hc = null;
            }
            if (hc == null) {
                firstConnectTime = System.currentTimeMillis();
                hc = new HttpConnector();
            }
            switch (connectType) {
            case 0:
                is = hc.connect(url, "post", response.toOutputBytes());
                break;
            case 1:
                is = hc.connect(url, "get", null);
                break;
            }
            if (is == null) {
                state = Task.STATE_FAIL;
            } else {
//                String message = response.getErrorInfo().ValidErrorInfo;//resHeader.statusMSG.cluv.getClvString();
//                this.msg = message;
                // Tracer.debug("encHeader.encrpt*******************......" +
                // encHeader.encrpt);
                
                    //解析
                    response.response(is);
                    RemoteErrorInfo info = response.getErrorInfo();
                    if(info != null){
                        this.msg = info.validErrorInfo;  
                    }
                if (this.msg == null || this.msg.equals("")) {
                    state = Task.STATE_SUCCESS;
                } else {
                    state = Task.STATE_FAIL;
                }
            }
            firstConnectTime = System.currentTimeMillis();
        } catch (Exception ex) {
            // getRootBytes = null;
        	if (hc != null)
            hc.closeHttp();
            hc = null;
            Logger.error("DatanAgentConnector", ex.toString());
            state = Task.STATE_FAIL;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (hc != null)
                hc.closeHttp();
                hc = null;
            } catch (Exception ex) {
            	Logger.error("DatanAgentConnector", ex.toString());
            }
        }
        return state;
        
    }
    /**
     * 联网并解析json接口
     * @param url 连接地址
     * @param response 解析接口
     * @param connectType 传1为get方式，0为post方式
     * @return 联网状态
     */
    public int connectHttpForJson(String url,ResponseJson response,byte connectType){
        int state = Task.STATE_INIT;
        InputStream is = null;
        try {
//            if ((System.currentTimeMillis() - firstConnectTime) > DatanAgentConnectResource.DEFAULT_CONNECT_TIME_OUT) {
//                if (hc != null)
//                    hc.closeHttp();
//                hc = null;
//            }
            if (hc == null) {
//                firstConnectTime = System.currentTimeMillis();
                hc = new HttpConnector();
            }
            switch (connectType) {
            case 0:
                is = hc.connect(url, "post", response.toOutputBytes());
                break;
            case 1:
                is = hc.connect(url, "get", null);
                break;
            }
//            Logger.e("url=", url);
            if (is == null) {
                state = Task.STATE_FAIL;
            } else {
//                String message = response.getErrorInfo().ValidErrorInfo;//resHeader.statusMSG.cluv.getClvString();
//                this.msg = message;
                // Tracer.debug("encHeader.encrpt*******************......" +
                // encHeader.encrpt);
                    //解析
                    response.response(is);
                    this.msg = response.getResultInfo().validResultInfo;
//                    Logger.e("--msg------>", this.msg);
                if ("0000".equals(response.getResultInfo().validResultCode)) {
                    state = Task.STATE_SUCCESS;
                } else {
                    state = Task.STATE_FAIL;
                }
            }
//            firstConnectTime = System.currentTimeMillis();
        } catch (Exception ex) {
            // getRootBytes = null;
        	if (hc != null)
            hc.closeHttp();
            hc = null;
            Logger.error("DatanAgentConnector", ex.toString());
            state = Task.STATE_FAIL;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (hc != null)
                hc.closeHttp();
                hc = null;
            } catch (Exception ex) {
            	Logger.error("DatanAgentConnector", ex.toString());
            }
        }
        return state;
        
    }
    
}
