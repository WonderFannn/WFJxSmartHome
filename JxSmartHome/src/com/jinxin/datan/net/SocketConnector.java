package com.jinxin.datan.net;

import com.jinxin.datan.net.util.Base64;
import com.jinxin.datan.net.util.DataChange;
import com.jinxin.datan.net.util.URLParser;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Iterator;

import com.jinxin.jxsmarthome.util.Logger;

/**
 * socket连接器
 * 
 * @author JackeyZhang
 * 
 */
public class SocketConnector {
	// public static int DEFAULT_READ_TIME_OUT = 30 * 1000; // 超时毫秒数字符串
	// public static int MAX_DATA_SIZE = 5 * 1024 * 1024;
	// public static String wapGatewayIP =
	// "tpclient.118100.cn";//"tpclient.118100.cn";//"10.0.0.200";
	// public static int wapGatewayPort = 90;//80;
	// public static boolean isWap = IMusicConnectResource.isCmWap;

	// public final Hashtable prop = new Hashtable();
	public Socket socket;
	public OutputStream outputStream = null;
	public InputStream inputStream = null;
	/*
	 * 0、初始（代表正常网络进行中） 1、客户端强行终止； 2、当前终止完毕； 只有0，才表示网络正常
	 */
	private static byte stop = 2;

	public static byte getStop() {
		return stop;
	}

	public static void setStop(byte stop) {
		SocketConnector.stop = stop;
	}

	public byte[] connect(String appURL, byte[] request) {
		OutputStream os = null;
		// InputStream is = null;
		DataInputStream dis = null;
		byte[] bytes = null;
		ByteArrayOutputStream bas = null;
		DataOutputStream dos = null;
		byte[] flag = new byte[2];
		String ip = null;
		int port = 0;
		URLParser urlParser = new URLParser(appURL);
		ip = urlParser.host;
		port = urlParser.port;
		try {
			if (socket == null) {
				try {
					java.net.InetAddress[] x = java.net.InetAddress
							.getAllByName(ip);
					ip = x[0].getHostAddress();
				} catch (UnknownHostException ex) {
					Logger.error("SocketConnector", ex.toString());
				}
				socket = new Socket();
				socket.connect(new InetSocketAddress(ip, port),
						DatanAgentConnectResource.DEFAULT_CONNECT_TIME_OUT);
			}
			socket.setSoTimeout(DatanAgentConnectResource.DEFAULT_CONNECT_TIME_OUT);
			os = socket.getOutputStream();
			// 获取请求头
			// int reqLength = 0;
			// if (request != null){
			// reqLength = request.length;
			// }
			// String reqHeader = getHttpHeader(ip, port,
			// IMusicConnectResource.appURL, "post",
			// null, reqLength);
			// Tracer.debug("reqHeader..." + reqHeader);
			// os.write(reqHeader.getBytes());
			if (request != null)
				os.write(request);
			os.flush();
			dis = new DataInputStream(socket.getInputStream());
			// byte[] oo = new byte[1];
			// int _index = 0;
			// while (_index < 10) {
			// dis.read(oo);
			// Tracer.debug(new Integer(oo[0]).toString());
			// _index++;
			// }
			long start = System.currentTimeMillis();
			while (dis.available() < 10
					&& stop != 0
					&& (System.currentTimeMillis() - start) < DatanAgentConnectResource.DEFAULT_CONNECT_TIME_OUT) {
				Thread.sleep(60);
			}
			if (stop > 0
					|| (System.currentTimeMillis() - start) > DatanAgentConnectResource.DEFAULT_CONNECT_TIME_OUT) {
				closeSocket();
				if (os != null) {
					os.close();
				}
				if (dis != null) {
					dis.close();
				}
				stop = 2;
				return null;
			}
		} catch (Exception e) {
			// Tracer.debug("HttpConnector error......" + reqURL);
			closeSocket();
			stop = 2;
			return null;
			// e.printStackTrace();
		} finally {
			// 释放各种资源
			stop = 2;
			try {
				if (socket != null)
					socket.close();
				// socket = null;
				// if (con != null)
				// con = null;
				// if (httpConnection != null)
				// httpConnection.disconnect();
				if (os != null)
					os.close();
				// if (is != null)
				// is.close();
				if (dis != null)
					dis.close();
				if (bas != null)
					bas.close();
				if (dos != null)
					dos.close();

			} catch (Exception ex) {
				stop = 2;
				Logger.error("SocketConnector", ex.toString());
			}
			stop = 2;
		}
		stop = 2;
		return bytes;
	}

	public InputStream connectToIs(String appURL, byte[] request) {
		// OutputStream os = null;
		// InputStream is = null;
		InputStream is = null;
		// byte[] bytes = null;
		// ByteArrayOutputStream bas = null;
		// DataOutputStream dos = null;
		// byte[] flag = new byte[2];
		String ip = null;
		int port = 0;
		URLParser urlParser = new URLParser(appURL);
		ip = urlParser.host;
		port = urlParser.port;
		try {
			if (socket == null) {
				try {
					java.net.InetAddress[] x = java.net.InetAddress
							.getAllByName(ip);
					ip = x[0].getHostAddress();
				} catch (UnknownHostException ex) {
					Logger.error("SocketConnector", ex.toString());
				}
				socket = new Socket();
				socket.connect(new InetSocketAddress(ip, port),
						DatanAgentConnectResource.DEFAULT_CONNECT_TIME_OUT);
			}
			socket.setSoTimeout(DatanAgentConnectResource.DEFAULT_CONNECT_TIME_OUT);

			outputStream = socket.getOutputStream();
			// 获取请求头
			// int reqLength = 0;
			// if (request != null){
			// reqLength = request.length;
			// }
			// String reqHeader = getHttpHeader(ip, port,
			// IMusicConnectResource.appURL, "post",
			// null, reqLength);
			// Tracer.debug("reqHeader..." + reqHeader);
			// os.write(reqHeader.getBytes());
			if (socket.isConnected()) {
				if (request != null)
					outputStream.write(request);
				outputStream.flush();

				is = socket.getInputStream();
			}
			// byte[] oo = new byte[1];
			// int _index = 0;
			// while (_index < 10) {
			// dis.read(oo);
			// Tracer.debug(new Integer(oo[0]).toString());
			// _index++;
			// }
			// long start = System.currentTimeMillis();
			// while (dis.available() < 10 && stop != 0
			// && (System.currentTimeMillis() - start) <
			// DatanAgentConnectResource.DEFAULT_CONNECT_TIME_OUT) {
			// Thread.sleep(60);
			// }
			// if (stop > 0 || (System.currentTimeMillis() - start) >
			// DatanAgentConnectResource.DEFAULT_CONNECT_TIME_OUT) {
			// closeSocket();
			// if (os != null) {
			// os.close();
			// }
			// if (dis != null) {
			// dis.close();
			// }
			// stop = 2;
			// return null;
			// }
		} catch (Exception e) {
			// Tracer.debug("HttpConnector error......" + reqURL);
			// closeSocket();
			// stop = 2;
			return null;
			// e.printStackTrace();
		} finally {
			// 释放各种资源
			// stop = 2;
			try {
				// closeSocket();
				// if (socket != null)
				// socket.close();
				// socket = null;
				// if (con != null)
				// con = null;
				// if (httpConnection != null)
				// httpConnection.disconnect();
				// if (os != null)
				// os.close();
				// if (is != null)
				// is.close();
				// if (dis != null)
				// dis.close();
				// if (bas != null)
				// bas.close();
				// if (dos != null)
				// dos.close();

			} catch (Exception ex) {
				// stop = 2;
				Logger.error("SocketConnector", ex.toString());
			}
			// stop = 2;
			// return is;
		}
		// stop = 2;
		return is;
	}
	/**
	 * 长连接方式（socket需手动关闭）
	 */
	public InputStream connectToLong(String appURL, byte[] request) {
		// OutputStream os = null;
		// InputStream is = null;
//		InputStream is = null;
		// byte[] bytes = null;
		// ByteArrayOutputStream bas = null;
		// DataOutputStream dos = null;
		// byte[] flag = new byte[2];
		String ip = null;
		int port = 0;
		URLParser urlParser = new URLParser(appURL);

		ip = urlParser.host;
		port = urlParser.port;
		
		try {
			if (socket == null) {
				try {
					java.net.InetAddress[] x = java.net.InetAddress
							.getAllByName(ip);
					ip = x[0].getHostAddress();
				} catch (UnknownHostException ex) {
					Logger.error("SocketConnector", "zj test "+ex.toString());
				}
				socket = new Socket();
				socket.connect(new InetSocketAddress(ip, port),
						DatanAgentConnectResource.DEFAULT_CONNECT_TIME_OUT);
			
			socket.setSoTimeout(DatanAgentConnectResource.DEFAULT_READ_TIME_OUT);
			}
			if (socket.isConnected()) {
//				Logger.error("SocketConnector", "zj test "+socket.isConnected());
				outputStream = socket.getOutputStream();
				inputStream = socket.getInputStream();
				if (request != null)
					outputStream.write(request);
				outputStream.flush();
			}
//			Logger.debug("SocketConnector", "zj test "+"socket = conecting...");
		} catch (Exception e) {
			Logger.error("SocketConnector", "zj test "+"Socket 异常"+e.toString());
			return null;
			// e.printStackTrace();
		} finally {
			// 释放各种资源
			// stop = 2;
			try {
				// closeSocket();
				// if (socket != null)
				// socket.close();
				// socket = null;
				// if (con != null)
				// con = null;
				// if (httpConnection != null)
				// httpConnection.disconnect();
				// if (os != null)
				// os.close();
				// if (is != null)
				// is.close();
				// if (dis != null)
				// dis.close();
				// if (bas != null)
				// bas.close();
				// if (dos != null)
				// dos.close();

			} catch (Exception ex) {
				// stop = 2;
				Logger.error("SocketConnector", ex.toString());
			}
			// stop = 2;
			// return is;
		}
		// stop = 2;
		return inputStream;
	}
	public byte[] connectAsWap(String appURL, byte[] request) {
		OutputStream os = null;
		// InputStream is = null;
		DataInputStream dis = null;
		byte[] bytes = null;
		ByteArrayOutputStream bas = null;
		DataOutputStream dos = null;
		byte[] flag = new byte[2];
		String ip = null;
		int port = 0;
		URLParser urlParser = new URLParser(appURL);
		ip = urlParser.host;
		port = urlParser.port;
		String path = urlParser.path;
		// ip = "10.0.0.200";
		// port = 80;
		URLParser wapParser = new URLParser(
				DatanAgentConnectResource.wapGateway);
		String wapIp = wapParser.host;
		int wapPort = wapParser.port;
		try {
			try {
				if (socket == null) {
					// try {
					// java.net.InetAddress[] x = java.net.InetAddress
					// .getAllByName(ip);
					// ip = x[0].getHostAddress();
					// } catch (UnknownHostException e) {
					// e.printStackTrace();
					// }
					Logger.error("connectAsWap---------->>>>", ip + ":" + port);
					socket = new Socket();
					socket.connect(new InetSocketAddress(wapIp, wapPort),
							DatanAgentConnectResource.DEFAULT_CONNECT_TIME_OUT);
				}
				socket.setSoTimeout(DatanAgentConnectResource.DEFAULT_CONNECT_TIME_OUT);
				os = socket.getOutputStream();
				// 获取请求头
				int reqLength = 0;
				if (request != null) {
					reqLength = request.length;
				}
				String reqHeader = getHttpHeader(urlParser.host,
						urlParser.port, path, "POST", null, reqLength);
				os.write(reqHeader.getBytes());
				if (request != null)
					os.write(request);
				os.flush();
				dis = new DataInputStream(socket.getInputStream());
				// byte[] oo = new byte[1];
				// int _index = 0;
				// while (_index < 10) {
				// dis.read(oo);
				// Tracer.debug(new Integer(oo[0]).toString());
				// _index++;
				// }
				long start = System.currentTimeMillis();
				while (dis.available() < 10
						&& stop != 0
						&& (System.currentTimeMillis() - start) < DatanAgentConnectResource.DEFAULT_CONNECT_TIME_OUT) {
					Thread.sleep(60);
				}
				if (stop > 0
						|| (System.currentTimeMillis() - start) > DatanAgentConnectResource.DEFAULT_CONNECT_TIME_OUT) {
					closeSocket();
					if (os != null) {
						os.close();
					}
					if (dis != null) {
						dis.close();
					}
					stop = 2;
					return null;
				}
				this.parseHttpHeader(dis);
				dis.read(flag);
			} catch (Exception ex) {
				Logger.error("SocketConnector", ex.toString());
			}

		} catch (Exception e) {
			// Tracer.debug("HttpConnector error......" + reqURL);
			closeSocket();
			stop = 2;
			Logger.error("Exception", "[connector]..." + e.toString());
			return null;
		} finally {
			// 释放各种资源
			stop = 2;
			try {
				if (socket != null)
					socket.close();
				// socket = null;
				// if (con != null)
				// con = null;
				// if (httpConnection != null)
				// httpConnection.disconnect();
				if (os != null)
					os.close();
				// if (is != null)
				// is.close();
				if (dis != null)
					dis.close();
				if (bas != null)
					bas.close();
				if (dos != null)
					dos.close();

			} catch (Exception ex) {
				stop = 2;
				Logger.error("SocketConnector", ex.toString());
			}
			stop = 2;
		}
		stop = 2;
		return bytes;
	}

	public String getHttpHeader(String ip, int port, String url,
			String reqMethod, Hashtable httpHeader, int contentLength) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(reqMethod);
		buffer.append(" ");
		// buffer.append(" http://");
		// buffer.append(ip);
		// buffer.append(":");
		// buffer.append(port);
		if (url != null && !url.equals("")) {
			buffer.append(url);
		}
		// buffer.append("/ease/servlet/ease?sid=%d HTTP/1.1\r\n");
		buffer.append(" HTTP/1.1\r\n");
		buffer.append("Proxy-Authorization: Basic ");
		// buffer.append(Base64.encode(DataChange.toUTF8Bytes("ctwap@mysdma.cn:vnet.mobi:vnet.mobi"))+"\r\n");
		buffer.append(Base64.encode(DataChange.toUTF8Bytes("ctwap@mysdma.cn"))
				+ ":" + Base64.encode(DataChange.toUTF8Bytes("vnet.mobi"))
				+ "\r\n");

		buffer.append("Host: ");
		buffer.append(ip);
		buffer.append(":");
		buffer.append(port);
		buffer.append("\r\n");
		// buffer.append("x-up-tpd-session-headers: User-Agent, Accept-Charset, Accept-Language, x-wap-profile, Accept-Encoding, x-up-proxy-enable-trust, x-up-devcap-cc\r\n");
		buffer.append("Accept-Charset: utf-8\r\n");
		buffer.append("Accept: */*\r\n");
		// buffer.append("Accept-Language: zh-cn\r\n");
		// buffer.append("x-wap-profile: \"http://developer.openwave.com/uaprof/OPWVSDK62.xml\"\r\n");
		// buffer.append("Accept-Encoding: deflate\r\n");
		// buffer.append("x-up-proxy-enable-trust: 1\r\n");
		// buffer.append("x-up-devcap-cc: 1\r\n\r\n");
		buffer.append("Content-Type: text/vnd.wap.wml\r\n");
		// if (!IMusicConnectResource.isCmWap)
		// buffer.append("X-Up-Calling-Line-Id: 18908229476\r\n");//
		// 模拟器测试订购等操作时使用

		buffer.append("Content-Length: ");
		buffer.append(contentLength);
		buffer.append("\r\n");
		// buffer.append("Accept: application/smil, application/vnd.phonecom.mmc-xml, application/vnd.uplanet.bearer-choice, application/vnd.uplanet.bearer-choice-wbxml, application/vnd.wap.wmlc, application/vnd.wap.xhtml+xml, application/xhtml+xml, image/bmp, image/gif, image/jpeg, image/png, image/vnd.wap.wbmp, image/x-up-wpng, multipart/mixed, multipart/related, text/html, text/plain, text/vnd.sun.j2me.app-descriptor, text/vnd.wap.wml, audio/x-wav, audio/wav, audio/imelody, audio/midi, audio/AMR, audio/AMR-WB, audio/mpeg, video/mpeg, text/x-pcs-gcd, application/x-pmd\r\n");

		if (httpHeader != null) {
			Iterator iterator = httpHeader.keySet().iterator();
			while (iterator.hasNext()) {
				String key = iterator.next().toString();
				buffer.append(key);
				buffer.append(": ");
				buffer.append(httpHeader.get(key).toString());
				buffer.append("\r\n");
			}
		}

		buffer.append("Proxy-Connection: Keep-Alive\r\n\r\n");
		// buffer.append("\r\n");

		return buffer.toString();

	}

	public Hashtable parseHttpHeader(DataInputStream is) {
		Hashtable prop = new Hashtable();
		int bufferSize = 4096;
		byte[] readBuffer = new byte[bufferSize];
		int t = bufferSize;
		// StringBuffer sb = new StringBuffer();

		int read = 0;
		try {
			while (read < bufferSize) {
				readBuffer[read] = is.readByte();
				// System.out.println("readBuffer[read]......"
				// + read);
				read++;
				if (read >= 4) {
					if (readBuffer[read - 1] == (byte) 0x0A
							&& readBuffer[read - 2] == (byte) 0x0D
							&& readBuffer[read - 3] == (byte) 0x0A
							&& readBuffer[read - 4] == (byte) 0x0D) {
						break;
					}
				}
			}

			byte[] resHeaderBytes = new byte[read];
			System.arraycopy(readBuffer, 0, resHeaderBytes, 0, read);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					new ByteArrayInputStream(resHeaderBytes)));
			String line = null;
			while ((line = in.readLine()) != null) {
				String s[] = line.split(": ");
				if (s.length == 2) {
					prop.put(s[0], s[1]);
				}
			}

		} catch (Exception ex) {
			Logger.error("SocketConnector", ex.toString());
		}
		return prop;
	}

	/*
	 * public byte[] connectAsWap(String reqURL, Hashtable httpHeader, String
	 * reqMethod, byte[] request) { stop = 0; // URLConnection con = null; //
	 * HttpURLConnection httpConnection = null; OutputStream os = null;
	 * InputStream is = null; // long startTime = System.currentTimeMillis(); //
	 * // BufferedInputStream bufIn = null; DataInputStream dis = null; byte[]
	 * bytes = null; String ip = null; int port = 80; // reqURL =
	 * "http://118.123.205.195/test.mp3"; // reqMethod = "GET"; // request =
	 * null; // Socket socket = null; try { URLParser ul = new
	 * URLParser(reqURL); Tracer.debug("reqURL..." + reqURL); if (isWap) { ip =
	 * SocketConnector.wapGatewayIP; port = SocketConnector.wapGatewayPort; }
	 * else { ip = ul.host; port = ul.port; }
	 * 
	 * Tracer.debug("Socket..." + ip + ":" + port); if (socket == null) { //
	 * socket = new Socket(ip,port); // SocketAddress isa =
	 * socket.getRemoteSocketAddress(); // socket.close(); // socket = new
	 * Socket(); // socket.connect(isa,DEFAULT_CONNECT_TIME_OUT); ///////////
	 * try { java.net.InetAddress[] x = java.net.InetAddress .getAllByName(ip);
	 * ip = x[0].getHostAddress(); Logger.error("aaaaaaa", ""+ip); } catch
	 * (UnknownHostException e) { e.printStackTrace(); } socket = new Socket();
	 * socket.connect(new InetSocketAddress(ip, port),
	 * DEFAULT_CONNECT_TIME_OUT);
	 * 
	 * /////////// // socket = new Socket(); // socket.connect(new
	 * InetSocketAddress(ip, port), // DEFAULT_CONNECT_TIME_OUT); }
	 * socket.setSoTimeout(DEFAULT_CONNECT_TIME_OUT);
	 * Tracer.debug("socket.isConnected()......" + socket.isConnected());
	 * 
	 * os = socket.getOutputStream(); int reqLength = 0; if (request != null)
	 * reqLength = request.length; // 获取请求头 // String reqHeader =
	 * getHttpHeader(ip, port, reqURL, reqMethod, // httpHeader, reqLength);
	 * String reqHeader = getHttpHeader(ul.host, ul.port, reqURL, reqMethod,
	 * httpHeader, reqLength); Tracer.debug("reqHeader..." + reqHeader);
	 * os.write(reqHeader.getBytes()); if (request != null) os.write(request);
	 * os.flush();
	 * 
	 * // < HTTP/1.1 200 OK // < Date: Mon, 09 Nov 2009 10:24:25 GMT // <
	 * Content-Length: 19174 // < Content-Type: text/html // < Content-Location:
	 * http://emouze.com/index.htm // < Last-Modified: Wed, 11 Jun 2008 09:14:16
	 * GMT // < Accept-Ranges: bytes // < ETag: "0248185a3cbc81:1e466" // <
	 * Server: Microsoft-IIS/6.0 // < X-Powered-By: ASP.NET dis = new
	 * DataInputStream(socket.getInputStream());
	 * Tracer.debug("dis.available()......" + dis.available()); // byte[] oo =
	 * new byte[1]; // int index = 0; // while (index < 10) { // dis.read(oo);
	 * // Tracer.debug(new Integer(oo[0]).toString()); // index++; // }
	 * 
	 * long start = System.currentTimeMillis(); while (dis.available() < 10 &&
	 * stop != 0 && (System.currentTimeMillis() - start) <
	 * DEFAULT_CONNECT_TIME_OUT) { Tracer.debug("dis.available()......" +
	 * dis.available()); Thread.sleep(60); }
	 * 
	 * Tracer.debug("dis.available()......" + dis.available() + ":" +
	 * (System.currentTimeMillis() - start)); // 20091220 if (stop > 0 ||
	 * (System.currentTimeMillis() - start) > DEFAULT_CONNECT_TIME_OUT) {
	 * closeSocket(); stop = 2;
	 * 
	 * return null; }
	 * 
	 * // is = socket.getInputStream();
	 * 
	 * Hashtable table = parseHttpHeader(dis);
	 * Tracer.debug("dis.available()......" + dis.available()); int
	 * contentLength = dis.available(); if (table.get("Content-Length") != null)
	 * contentLength = Integer.parseInt(table.get("Content-Length")
	 * .toString()); Tracer.debug("contentLength......" + contentLength); bytes
	 * = new byte[contentLength]; // dis.readFully(responseBytes); int t =
	 * contentLength; start = System.currentTimeMillis(); while (t > 0) { //
	 * Platform.println(" start "); int m = dis.read(bytes, contentLength - t,
	 * t); if (m >= 0) { t -= m; } else { // 异常处理 // 异常处理
	 * System.out.println("Read error:" + t); closeSocket(); stop = 2; return
	 * null; } }
	 * 
	 * // 20091220 if (stop > 0) { closeSocket(); stop = 2; return null; }
	 * 
	 * stop = 2; } catch (Exception e) { //
	 * Tracer.debug("HttpConnector error......" + reqURL); closeSocket(); stop =
	 * 2; System.out.println("[connector]..." + e.toString()); return null; //
	 * e.printStackTrace(); } finally { // 释放各种资源 stop = 2; try { if (socket !=
	 * null) socket.close(); socket = null; // if (con != null) // con = null;
	 * // if (httpConnection != null) // httpConnection.disconnect(); // if (os
	 * != null) // os.close(); // if (is != null) // is.close(); // if (dis !=
	 * null) // dis.close();
	 * 
	 * } catch (Exception ex) { ex.printStackTrace(); } } return bytes; }
	 */

	/*
	 * public int getHttpContentLength(String reqURL, Hashtable httpHeader,
	 * String reqMethod, byte[] request) { // URLConnection con = null; //
	 * HttpURLConnection httpConnection = null; OutputStream os = null;
	 * InputStream is = null; // long startTime = System.currentTimeMillis(); //
	 * // BufferedInputStream bufIn = null; DataInputStream dis = null; byte[]
	 * bytes = null; String ip = null; int port = 80; // reqURL =
	 * "http://118.123.205.195/test.mp3"; // reqMethod = "GET"; // request =
	 * null; int contentLength = -1;
	 * 
	 * // Socket socket = null; try { URLParser ul = new URLParser(reqURL);
	 * Tracer.debug("reqURL..." + reqURL); if (isWap) { ip =
	 * SocketConnector.wapGatewayIP; port = SocketConnector.wapGatewayPort; }
	 * else { ip = ul.host; port = ul.port; }
	 * 
	 * Tracer.debug("Socket..." + ip + ":" + port); if (socket == null) { socket
	 * = new Socket(); socket.connect(new InetSocketAddress(ip, port),
	 * DEFAULT_CONNECT_TIME_OUT); }
	 * socket.setSoTimeout(DEFAULT_CONNECT_TIME_OUT); os =
	 * socket.getOutputStream();
	 * 
	 * int reqLength = 0; if (request != null) reqLength = request.length; //
	 * 获取请求头 String reqHeader = getHttpHeader(ip, port, reqURL, reqMethod,
	 * httpHeader, reqLength); Tracer.debug("reqHeader..." + reqHeader);
	 * os.write(reqHeader.getBytes()); if (request != null) os.write(request);
	 * os.flush();
	 * 
	 * // < HTTP/1.1 200 OK // < Date: Mon, 09 Nov 2009 10:24:25 GMT // <
	 * Content-Length: 19174 // < Content-Type: text/html // < Content-Location:
	 * http://emouze.com/index.htm // < Last-Modified: Wed, 11 Jun 2008 09:14:16
	 * GMT // < Accept-Ranges: bytes // < ETag: "0248185a3cbc81:1e466" // <
	 * Server: Microsoft-IIS/6.0 // < X-Powered-By: ASP.NET dis = new
	 * DataInputStream(socket.getInputStream()); // is =
	 * socket.getInputStream(); Hashtable table = parseHttpHeader(dis); Object
	 * object = table.get("Content-Length"); if (object != null) contentLength =
	 * Integer.parseInt(object.toString()); Tracer.debug("contentLength......" +
	 * contentLength);
	 * 
	 * } catch (Exception e) { // Tracer.debug("HttpConnector error......" +
	 * reqURL); System.out.println(e.toString()); // e.printStackTrace(); }
	 * finally { // 释放各种资源 try { if (socket != null) socket.close(); socket =
	 * null; // if (httpConnection != null) // httpConnection.disconnect(); //
	 * if (os != null) // os.close(); // if (is != null) // is.close(); // if
	 * (dis != null) // dis.close();
	 * 
	 * } catch (Exception ex) { ex.printStackTrace(); } } return contentLength;
	 * }
	 */

	/*
	 * public DataInputStream connectAsBpWap(String reqURL, Hashtable
	 * httpHeader, String reqMethod, byte[] request, int contentLength, long
	 * startPos) { // URLConnection con = null; // HttpURLConnection
	 * httpConnection = null; OutputStream os = null; InputStream is = null; //
	 * long startTime = System.currentTimeMillis(); // // BufferedInputStream
	 * bufIn = null; DataInputStream dis = null; byte[] bytes = null; String ip
	 * = null; int port = 80; // reqURL = "http://118.123.205.195/test.mp3"; //
	 * reqMethod = "GET"; // request = null; // Socket socket = null; try {
	 * URLParser ul = new URLParser(reqURL); Tracer.debug("reqURL..." + reqURL);
	 * // if (isWap) { // ip = SocketConnector.wapGatewayIP; // port =
	 * SocketConnector.wapGatewayPort; // } else { ip = ul.host; port = ul.port;
	 * // }
	 * 
	 * Tracer.debug("Socket..." + ip + ":" + port); if (socket == null) { socket
	 * = new Socket(); socket.connect(new InetSocketAddress(ip, port),
	 * DEFAULT_CONNECT_TIME_OUT); }
	 * socket.setSoTimeout(DEFAULT_CONNECT_TIME_OUT); os =
	 * socket.getOutputStream(); int reqLength = 0; if (request != null)
	 * reqLength = request.length; // 获取请求头 String reqHeader = getHttpHeader(ip,
	 * port, reqURL, reqMethod, httpHeader, reqLength);
	 * Tracer.debug("reqHeader..." + reqHeader); os.write(reqHeader.getBytes());
	 * if (request != null) os.write(request); os.flush();
	 * 
	 * // < HTTP/1.1 200 OK // < Date: Mon, 09 Nov 2009 10:24:25 GMT // <
	 * Content-Length: 19174 // < Content-Type: text/html // < Content-Location:
	 * http://emouze.com/index.htm // < Last-Modified: Wed, 11 Jun 2008 09:14:16
	 * GMT // < Accept-Ranges: bytes // < ETag: "0248185a3cbc81:1e466" // <
	 * Server: Microsoft-IIS/6.0 // < X-Powered-By: ASP.NET dis = new
	 * DataInputStream(socket.getInputStream()); // is =
	 * socket.getInputStream(); Hashtable table = parseHttpHeader(dis);
	 * 
	 * } catch (Exception e) { // Tracer.debug("HttpConnector error......" +
	 * reqURL); System.out.println(e.toString()); } finally { // 释放各种资源 try { //
	 * if (socket != null) // socket.close(); // if (httpConnection != null) //
	 * httpConnection.disconnect(); // if (os != null) // os.close(); // if (is
	 * != null) // is.close(); // if (dis != null) // dis.close();
	 * 
	 * } catch (Exception ex) { ex.printStackTrace(); } }
	 * 
	 * return dis;
	 * 
	 * }
	 */

	public void closeSocket() {
		try {
			if (socket != null) {
				socket.close();
				socket = null;
			}
		} catch (Exception ex) {
			Logger.error("SocketConnector", ex.toString());
		}
	}

	public void closeOutputStream() {
		try {
			if (outputStream != null) {
				outputStream.close();
				outputStream = null;
			}
		} catch (Exception ex) {
			Logger.error("SocketConnector", ex.toString());
		}
	}
	public void closeInputStream() {
		try {
			if (inputStream != null) {
				inputStream.close();
				inputStream = null;
			}
		} catch (Exception ex) {
			Logger.error("SocketConnector", ex.toString());
		}
	}
}
