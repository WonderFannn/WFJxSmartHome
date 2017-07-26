
package com.jinxin.datan.net;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import com.jinxin.jxsmarthome.util.Logger;
/**
 * http连接器
 * @author JackeyZhang
 *
 */
public class HttpConnector {
    private static final int DEFAULT_CONNECT_TIME_OUT = 15 * 1000; // 超时毫秒数字符串
    private static final int DEFAULT_READ_TIME_OUT = 15 * 1000; // 超时毫秒数字符串
    private static final int MAX_DATA_SIZE = 50 * 1024 * 1024;
    private static String wapGateway = DatanAgentConnectResource.wapGateway;  //NOSONAR
    private static boolean isCmWap = DatanAgentConnectResource.isCmWap;  //NOSONAR
    public HttpURLConnection httpConnection = null;

    public byte[] connect(String reqURL, ConcurrentHashMap httpHeader, String reqMethod,
            byte[] request) {
        URLConnection con = null;

        OutputStream os = null;
        InputStream is = null;
        long startTime = System.currentTimeMillis();
        // BufferedInputStream bufIn = null;
        DataInputStream dis = null;
        byte[] bytes = null;
        try {
            String appIP = null; // 应用服务器地址
            String appURI = reqURL; // 路径
            if (isCmWap) {

                int n = appURI.indexOf("http://");
                if (n != -1) {
                    appURI = appURI.substring(n + "http://".length(), appURI.length());
                }
                n = appURI.indexOf("/");
                if (n != -1) {
                    appIP = appURI.substring(0, n);
                    appURI = appURI.substring(n + "/".length(), appURI.length());
                } else {
                    appIP = appURI;
                    appURI = "";
                }
                StringBuffer buffer = new StringBuffer();
                buffer.append(HttpConnector.wapGateway);
                buffer.append(appURI);
                reqURL = buffer.toString();
            }

            URL url = new URL(reqURL);
            con = url.openConnection();

            if (con instanceof HttpURLConnection) {
                httpConnection = (HttpURLConnection) con;
                httpConnection.setConnectTimeout(DEFAULT_CONNECT_TIME_OUT);
                httpConnection.setReadTimeout(DEFAULT_READ_TIME_OUT);
                if (reqMethod.equalsIgnoreCase("POST")) {
                    httpConnection.setRequestMethod("POST");
                    httpConnection.setDoOutput(true);
                    if (request != null)
                        httpConnection.setRequestProperty("Content-Length",
                                String.valueOf(request.length));
                }
                httpConnection.setDoInput(true);
                if (httpHeader != null) {
                    Iterator iterator = httpHeader.keySet().iterator();
                    while (iterator.hasNext()) {
                        String key = iterator.next().toString();
                        httpConnection.setRequestProperty(key, httpHeader.get(key).toString());
                    }
                }
                if (isCmWap)
                    httpConnection.setRequestProperty("X-Online-Host", appIP);

            }
            if (reqMethod.equalsIgnoreCase("post")) {
                os = httpConnection.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.write(request);
                dos.flush();
                dos.close();
            }
            is = httpConnection.getInputStream();

            if (is != null) {
                int httpCode = httpConnection.getResponseCode();
                int contentLength = httpConnection.getContentLength();
                int available = is.available();
                // if(contentLength == -1)
                // contentLength = available;
                if (httpCode == 200){//成功
                    if (contentLength > 0) {
                        bytes = this.readInputStream(is);
                    }
                }
//                if (contentLength > 0) {
//                    bytes = new byte[contentLength];
//                    dis = new DataInputStream(is);
//                    // dis.readFully(bytes);
//                    int t = contentLength;
//                    while (t > 0) {
//                        int m = dis.read(bytes, contentLength - t, t);
//                        if (m >= 0) {
//                            t -= m;
//                        } else {
//                            // 异常处理
//                            System.out.println("Read error:" + t);
//                            bytes = null;
//                        }
//                    }
//
//                } else {
//                }
            } else {
            }

        } catch (Exception e) {
        	Logger.error("Exception", e.toString());
        } finally {
            // 释放各种资源
            try {
                if (con != null)
                    con = null;
                if (httpConnection != null)
                    httpConnection.disconnect();
                if (os != null)
                    os.close();
                if (is != null)
                    is.close();
                if (dis != null)
                    dis.close();

            } catch (Exception ex) {
            	Logger.error("HttpConnector", ex.toString());
            }
        }
        return bytes;
    }

    public byte[] connect(String reqURL, String reqMethod, long startPos) {
        ConcurrentHashMap httpHeader = new ConcurrentHashMap();
        try {
            if (startPos > 0) {
                String property = "bytes=" + startPos + "-";
                httpHeader.put("RANGE", property);
            }
        } catch (Exception ex) {
        	Logger.error("HttpConnector", ex.toString());
        }
        return this.connect(reqURL, httpHeader, reqMethod, null);
    }

    /**
     * 目前主要供以get方式联网时候使用方法,（20121204_zj：增加post方式，带请求数据，给webservice类型服务器）
     * 
     * @param reqURL http://www.xxx.com/recieve.do?sl=xx&sa=xxx
     * @param reqMethod get,post
     * @param request （post有）
     * @return
     */
    public InputStream connect(String reqURL, String reqMethod, byte[] request) {
        URLConnection con = null;

        OutputStream os = null;
        InputStream is = null;
        long startTime = System.currentTimeMillis();
        // BufferedInputStream bufIn = null;
        // DataInputStream dis = null;
        try {
            String appIP = null; // 应用服务器地址
            URL url = new URL(reqURL);
            con = url.openConnection();

            if (con instanceof HttpURLConnection) {
                httpConnection = (HttpURLConnection) con;
                httpConnection.setConnectTimeout(DEFAULT_CONNECT_TIME_OUT);
                httpConnection.setReadTimeout(DEFAULT_READ_TIME_OUT);
                if (reqMethod.equalsIgnoreCase("POST")) {
                    httpConnection.setRequestMethod("POST");
                    httpConnection.setDoOutput(true);
                    if (request != null)
                        httpConnection.setRequestProperty("Content-Length",
                                String.valueOf(request.length));
                    httpConnection.setDoInput(true);
                    if (isCmWap)
                        httpConnection.setRequestProperty("X-Online-Host", appIP);

                }
            }
            if (reqMethod.equalsIgnoreCase("post")) {
                os = httpConnection.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                if (request != null)
                    dos.write(request);
                dos.flush();
                dos.close();
            }
            if(httpConnection.getResponseCode() == 200)
                is = httpConnection.getInputStream();
        } catch (Exception e) {
        	Logger.error("Exception", e.toString());
        } finally {
            // 释放各种资源
            try {
                if (con != null)
                    con = null;
                // if (httpConnection != null)
                // httpConnection.disconnect();
                if (os != null)
                    os.close();
            } catch (Exception ex) {
            	Logger.error("HttpConnector", ex.toString());
            }
        }
        return is;
    }

    public void closeHttp() {
        try {
            if (this.httpConnection != null) {
                httpConnection.disconnect();
                httpConnection = null;
            }
        } catch (Exception ex) {
        	Logger.error("HttpConnector", ex.toString());
        }
    }

/**
 * 读取流信息
 * @param inputStream
 * @return
 * @throws Exception
 */
    private byte[] readInputStream(InputStream inputStream) throws Exception {
        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            outSteam.write(buffer, 0, len);
        }
        outSteam.close();
        inputStream.close();
        return outSteam.toByteArray();
    }

}
