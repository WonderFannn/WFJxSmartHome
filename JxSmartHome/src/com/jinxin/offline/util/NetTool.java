package com.jinxin.offline.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.conn.util.InetAddressUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import com.jinxin.jxsmarthome.util.Logger;
import android.widget.Toast;

import com.jinxin.db.impl.OffLineConetenDaoImpl;
import com.jinxin.db.util.DBHelper;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.MainActivity;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.constant.CommonMethod;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.OffLineContent;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.ui.dialog.CustomProgressDialog;
import com.jinxin.jxsmarthome.ui.widget.vfad.ADManager;
import com.jinxin.jxsmarthome.ui.widget.vfad.Advertising;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.record.SharedDB;
/**
 * @author JackeyZhang
 * @company 金鑫智慧
 * 离线网关搜索类
 */
public class NetTool {
	  private int SERVERPORT = 3333;
	  
	  private String locAddress;//存储本机ip，例：本地ip ：192.168.1.
	  
	  private Runtime run = Runtime.getRuntime();//获取当前运行环境，来执行ping，相当于windows的cmd
	  
//	  private Process proc = null;
	  
	  private String ping = "ping -c 1 -w 0.5 " ;//其中 -c 1为发送的次数，-w 表示发送后等待响应的时间
	  
	  private int j;//存放ip最后一位地址 0-255
	  private AtomicInteger connCount = new AtomicInteger(0); //请求连接计数
	  
	  private static final String MSG_CONTENT = "jxsmarthome";//与网关通信消息体
	  
	  private Context ctx;//上下文
	  
	  private static final int POOL_SIZE = 20;//最低线程数
	  private static final int DEFAULT_CONNECT_TIME_OUT = 10000;
	 
	  private boolean isToast = false;
	  private CustomProgressDialog dialog = null;
	  
	  private String serviceMsg = null;
	  
	  ExecutorService executorService = null;
	  private OffLineConetenDaoImpl olcImpl = null;
	  
	  public NetTool(Context ctx){
	    this.ctx = (Activity)ctx;
	    executorService = Executors.newFixedThreadPool(run.availableProcessors() * POOL_SIZE); //ExecutorService通常根据系统资源情况灵活定义线程池大小 
	    olcImpl = new OffLineConetenDaoImpl(ctx);
		olcImpl.clear();//清空表
	  }



	  private Handler handler = new Handler(){
	    
	    @Override
		public void handleMessage(Message msg) {
		  super.handleMessage(msg);
	      switch (msg.what) {
	      case 111:
	    	  String message = "正在尝试连接："+msg.obj.toString();
			  if (dialog == null) {
				dialog = CustomProgressDialog.createDialog(ctx);
				dialog = CustomProgressDialog.createDialog(ctx).setMessage(message);
				dialog.show();
			  }else{
				  if (dialog.isShowing()) {
					  dialog.setMessage(message);
				}
			  }
    	      break;
	      case 222:// 网关返回消息接收并解析成功，关闭Loading
//	    	  if (dialog.infosShowing()) {
//				dialog.dismiss();
//	    	  }
//	    	  String _account = SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS, ControlDefine.KEY_ACCOUNT,"");
//	    	  boolean isLogin = SharedDB.loadBooleanFromDB(_account, ControlDefine.KEY_ISLOADING,false);
//	    	  SharedDB.saveBooleanDB(_account, ControlDefine.KEY_OFF_LINE_MODE, true);
//	    	  if (isLogin) {
//	    		  SharedDB.saveBooleanDB(_account, ControlDefine.KEY_ISLOADING, false);
//	    	  }else{
//	    		  goToHome(ctx);
//	    	  }
//	    	  JxshApp.showToast(ctx, CommDefines.getSkinManager().
//	    			  string(R.string.zhao_dao_ke_yong_wang_guan));
	          break;
	      case 333:
	    	  if (dialog!=null) {
	    		  CustomProgressDialog.createDialog(ctx).dismissDailog();
	    		  dialog.dismiss();
	    	  }
	    	  Logger.error("Yang", "已连接本地网关");
	    	  break;
	      case 444://扫描失败
    		  if (connCount.get() == 255) {
    			  if (dialog!=null && dialog.isShowing()) {
    				  CustomProgressDialog.createDialog(ctx).dismissDailog();
    				  dialog.dismiss();
    			  }
    			  if (!isToast) {
    				  JxshApp.showToast(ctx, CommDefines.getSkinManager().
    		    			  string(R.string.sou_suo_wang_guan_error));
    			  }else{
					  if (dialog.isShowing()) {
						  CustomProgressDialog.createDialog(ctx).dismissDailog();
						  dialog.dismiss();
			    	  }
//			    	  String _account = CommUtil.getCurrentLoginAccount();
			    	  String _account = JxshApp.lastInputID;
			    	  boolean isLogin = SharedDB.loadBooleanFromDB(_account, ControlDefine.KEY_ISLOADING,false);
			    	  SharedDB.saveBooleanDB(_account, ControlDefine.KEY_OFF_LINE_MODE, true);
			    	  if (isLogin) {
			    		  SharedDB.saveBooleanDB(_account, ControlDefine.KEY_ISLOADING, false);
			    	  }else{
			    		  goToHome(ctx);
			    	  }
    				  List<OffLineContent> lists = olcImpl.find();
    				  if (lists != null && lists.size() > 0) {
    					  JxshApp.showToast(ctx, CommDefines.getSkinManager().
    							  string(R.string.li_xian_mo_shi_qie_huan_chen_gong)
    							  +"，找到"+lists.size()+"个可用网关" );
    				  }
    				  Logger.error("Yang", "已切换到本地网关");
    			  }
    		  }
	          break;
	      }
	    }

	  };
	  
	  //向serversocket发送消息
	  private String sendMsg(String ip,String msg, String randomCode) {
	    String res = null;
	    Socket socket = null;
	    System.out.println(ip+"---msg:"+msg);
	    try {
	      socket = new Socket();
	      socket.connect(new InetSocketAddress(ip, SERVERPORT),DEFAULT_CONNECT_TIME_OUT);
		  socket.setSoTimeout(DEFAULT_CONNECT_TIME_OUT);
	      //向服务器发送消息
	      PrintWriter os = new PrintWriter(socket.getOutputStream());
	      os.println(msg);
	      os.flush();// 刷新输出流，使Server马上收到该字符串
	      
	      //从服务器获取返回消息
	      DataInputStream input = new DataInputStream(socket.getInputStream());  
//	      res = input.readUTF();
//	      System.out.println("server 返回信息：" + res);
	      byte[] bufferVer = new byte[2];
	      input.read(bufferVer);
	      String ver = new String(bufferVer);
	      System.out.println(ip+"---server 返回协议版本信息：" + ver);
	      byte[] bufferCmdType = new byte[1];
	      input.read(bufferCmdType);
	      String cmdType = new String(bufferCmdType);
	      System.out.println(ip+"---server 返回请求类型信息：" + cmdType);
	      byte[] bufferLen = new byte[5];
	      input.read(bufferLen);
	      String len = new String(bufferLen);
	      System.out.println(ip+"---server 返回内容长度：" + len);
	      byte[] bufferCon = new byte[getLength(len)];
	      input.read(bufferCon);
	      String content = new String(bufferCon);
	      System.out.println(ip+"---server 返回请求内容信息：" + content);
	      byte[] bufferCode = new byte[6];
	      input.read(bufferCode);
	      String code = new String(bufferCode);
	      System.out.println(ip+"---server 返回请求随机码：" + code);
	      System.out.println(ip+"---server 返回请求随机码：" + randomCode);
	      
	      res = ver + cmdType + content + code;
	      
	      if (randomCode.equals(code) && content !=null) {
			OffLineContent olc = null;
			String ipHead = "";
			String ipMid = "";
			String ipEnd = "";
			try {
				olc = new OffLineContent();
				olc.setVersion(content.substring(9, 11));
				ipHead = content.substring(13, 21);
				ipMid = stringFoamat(content.substring(21,24));
				ipEnd = stringFoamat(content.substring(24,28));
				String _ip = ipHead+ipMid+ipEnd;
				olc.setIp(_ip);
//				olc.setIp(content.substring(13, 28));
				olc.setSn(content.substring(30));
				String account = CommUtil.getCurrentLoginAccount();
//				DBHelper.setDbName(account);
				OffLineConetenDaoImpl olcImpl = new OffLineConetenDaoImpl(ctx);
//				olcImpl.clear();//清空表
				Logger.error(null, ip+"----->" + olc.toString());
				olcImpl.insert(olc, true);
				JxshApp.showToast(ctx, "扫描到网关："+olc.getIp());
//				handler.sendEmptyMessage(222);
			} catch (Exception e) {
				e.printStackTrace();
			}
	      }else{
	    	  Toast.makeText(ctx, "随机码不匹配！"+code, Toast.LENGTH_SHORT).show();
	    	  Logger.error("OffLine", "随机码不匹配！"+code);
	      }
	      
	    } catch (Exception e) {
//	    	e.printStackTrace();
	    	Logger.error(ip+"=", "You are trying to connect to an unknown host!");
	    } finally {
	      // 4: Closing connection
	      try {
	        if (socket != null) {
	          socket.close();
	        }
	      } catch (IOException ioException) {
	        ioException.printStackTrace();
	      }
	    }
	    return res;
	  }

	  /**
	   * 扫描局域网内ip，找到对应服务器
	   */
	  public void scan1(){
		  locAddress = getLocAddrIndex();//获取本地ip前缀
		  
		  if(locAddress.equals("")){
			  Toast.makeText(ctx, "扫描失败，请检查wifi网络", Toast.LENGTH_LONG).show();
			  return ;
		  }
		  
		  for ( int i = 0; i < 255; i++) {//创建255个线程分别去ping
			  Runnable _runnable = new Runnable() {
				  public void run() {
					  j++;
					  String p = NetTool.this.ping + locAddress + NetTool.this.j ;
					  String current_ip = locAddress+ NetTool.this.j;
					  Process proc = null;
					  try {
						  proc = run.exec(p);
						  int result = proc.waitFor();
						  if (result == 0) {
							  Logger.error("-->", "连接成功" + current_ip);
							  if (!isToast) {
								  Message message = handler.obtainMessage();
								  message.what = 111;
								  message.obj = current_ip;
//								  handler.sendMessage(message);
								  message.sendToTarget();
							  }
							  // 向服务器发送验证信息
//							  String msg = sendMsg(current_ip,"scan"+getLocAddress()+" ( "+android.os.Build.MODEL+" ) ");
							  String content = CommonMethod.createCmdOfflineVersion10(MSG_CONTENT);//向服务器发送消息内容
							  String randomCode = "";
							  if (content!=null) {
								  randomCode = content.substring(content.getBytes("utf_8").length-6,
										  content.getBytes("utf_8").length);
							  }
							  serviceMsg = sendMsg(current_ip,content, randomCode);
							  //如果验证通过...
							  if (!TextUtils.isEmpty(serviceMsg)){
								  if (serviceMsg.contains("IP")){
									  System.out.println("已找到可用网关");
									  isToast = true;
								  }
							  }
						  } else {
							  Logger.error("-->", "连接失败" + current_ip);
						  }
					  } catch (IOException e1) {
						  e1.printStackTrace();
					  } catch (InterruptedException e2) {
						  e2.printStackTrace();
					  }finally {
//						  connCount++;
//						  connCount.addAndGet(1);
						  connCount.incrementAndGet();
						  Logger.debug("Count", connCount+"");
						  handler.sendEmptyMessage(444);
						  proc.destroy();
					  }
				  }
			  };
			  if(executorService != null){
				  executorService.submit(_runnable);
			  }
		  }
		  if(executorService != null){
			  executorService.shutdown();
		  }
//		  new Thread(){
//	            public void run(){
//	               try {
//	                  Thread.sleep(100000);
//	                  handler.sendEmptyMessage(333);//搜索超过2分钟提示超时
//	               } catch (InterruptedException e) { }
//	            }
//	         }.start();
	  }
	  /**
	   * 扫描局域网内ip，找到对应服务器
	   */
	  public void scan(Handler mDownLoadHandler){
	    
	    locAddress = getLocAddrIndex();//获取本地ip前缀
	    
	    if(locAddress.equals("")){
	      Toast.makeText(ctx, "扫描失败，请检查wifi网络", Toast.LENGTH_LONG).show();
	      return ;
	    }
	    
	    for ( int i = 0; i < 256; i++) {//创建256个线程分别去ping
	      
//	      j = i ;
	      
	      Runnable _runnable = new Runnable() {
	        
	        public void run() {
//	        	 System.out.println("连接成功"+(j++));
	        	 j++;
	          String p = NetTool.this.ping + locAddress + NetTool.this.j ;
	          
	          String current_ip = locAddress+ NetTool.this.j;
	          Process proc = null;
	          try {
	        	  proc = run.exec(p);
	            
	            int result = proc.waitFor();
	            if (result == 0) {
	              System.out.println("连接成功" + current_ip);
	              // 向服务器发送验证信息
//	              String msg = sendMsg(current_ip,"scan"+getLocAddress()+" ( "+android.os.Build.MODEL+" ) ");
	              
	              //如果验证通过...
//	              if (msg != null){
//	                if (msg.contains("OK")){
//	                  System.out.println("服务器IP：" + msg.substring(8,msg.length()));
//	                  Message.obtain(handler, 333, msg.substring(2,msg.length())).sendToTarget();//返回扫描完毕消息
//	                }
//	              }
	            } else {
	              
	            }
	          } catch (IOException e1) {
	            e1.printStackTrace();
	          } catch (InterruptedException e2) {
	            e2.printStackTrace();
	          } finally {
	            proc.destroy();
	          }
	        }
	      };
	      mDownLoadHandler.post(_runnable);
	    }
	    
	  }
	  /*
	  * 扫描局域网内ip，找到对应服务器
	  */
	  private void scan(){
		  
		  locAddress = getLocAddrIndex();//获取本地ip前缀
		  
		  if(locAddress.equals("")){
			  Toast.makeText(ctx, "扫描失败，请检查wifi网络", Toast.LENGTH_LONG).show();
			  return ;
		  }
		  
		  for ( int i = 0; i < 256; i++) {//创建256个线程分别去ping
			  
			  j = i ;
			  
			  new Thread(new Runnable() {
				  
				  public void run() {
					  
					  String p = NetTool.this.ping + locAddress + NetTool.this.j ;
					  
					  String current_ip = locAddress+ NetTool.this.j;
					  Process proc = null;
					  try {
						  proc = run.exec(p);
						  
						  int result = proc.waitFor();
						  if (result == 0) {
							  System.out.println("连接成功" + current_ip);
							  
							  // 向服务器发送验证信息
							  String msg = sendMsg(current_ip,"scan"+getLocAddress()+" ( "+android.os.Build.MODEL+" ) ", null);
							  
							  //如果验证通过...
							  if (msg != null){
								  if (msg.contains("OK")){
									  System.out.println("服务器IP：" + msg.substring(8,msg.length()));
									  Message.obtain(handler, 333, msg.substring(2,msg.length())).sendToTarget();//返回扫描完毕消息
								  }
							  }
						  } else {
							  
						  }
					  } catch (IOException e1) {
						  e1.printStackTrace();
					  } catch (InterruptedException e2) {
						  e2.printStackTrace();
					  } finally {
						  proc.destroy();
					  }
				  }
			  }).start();
			  
		  }
		  
	  }

	  
	  //获取本地ip地址
	  private String getLocAddress(){
	    
	    String ipaddress = "";
	    
	    try {
	      Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
	      // 遍历所用的网络接口
	      while (en.hasMoreElements()) {
	        NetworkInterface networks = en.nextElement();
	        // 得到每一个网络接口绑定的所有ip
	        Enumeration<InetAddress> address = networks.getInetAddresses();
	        // 遍历每一个接口绑定的所有ip
	        while (address.hasMoreElements()) {
	          InetAddress ip = address.nextElement();
	          if (!ip.isLoopbackAddress()
	              && InetAddressUtils.isIPv4Address(ip.getHostAddress())) {
	            ipaddress = ip.getHostAddress();
	          }
	        }
	      }
	    } catch (SocketException e) {
	      Logger.error("", "获取本地ip地址失败");
	      e.printStackTrace();
	    }
	    
	    System.out.println("本机IP:" + ipaddress);
	    
	    return ipaddress;

	  }
	  
	  //获取IP前缀
	  private String getLocAddrIndex(){
	    
	    String str = getLocAddress();
	    
	    if(!str.equals("")){
	      return str.substring(0,str.lastIndexOf(".")+1);
	    }
	    
	    return "";
	  }
	  
	  //获取本机设备名称
	  public String getLocDeviceName() {
	    
	    return android.os.Build.MODEL;
	    
	  }
	  /**
	   * 关闭线程池（不一定能中止已存在队列）
	   */
	  public void closeScan(){
		  if(executorService != null){
			  executorService.shutdownNow();
		  }
	  }
	  
	  /**
	   * 去掉字符串前面的“0” 转成int
	   * @param str
	   * @return
	   */
	  public int getLength(String str){
		  String newStr = str.replaceFirst("^0*", "");
		  int len = Integer.parseInt(newStr)-6;
		  return len;
	  }
	  
	  /**
	   * 去掉字符串前面的“0”
	 * @param str
	 * @return
	 */
	public String stringFoamat(String str){
		  String newStr = str.replaceFirst("^0*", "");
		  return newStr;
	  }
	
	/**
	 * 跳转首页
	 */
	private static void goToHome(Context ctx) {
//		String account = CommUtil.getCurrentLoginAccount();
		 String account = JxshApp.lastInputID;
		if (!TextUtils.isEmpty(account)) {
//			DBHelper.setDbName(account);
			SharedDB.saveStrDB(SharedDB.ORDINARY_CONSTANTS,
					ControlDefine.KEY_ACCOUNT, account);
			SharedDB.saveStrDB(SharedDB.ORDINARY_CONSTANTS,
					ControlDefine.KEY_SUNACCOUNT, "");
			SharedDB.saveBooleanDB(account, ControlDefine.KEY_ISLOADING, false);
			SharedDB.saveBooleanDB(account, ControlDefine.KEY_OFF_LINE_MODE, true);
		}
		/***zj:广告假数据测试********/
		List<Advertising> adList = new ArrayList<Advertising>();
		Advertising _ad = new Advertising();
		_ad.setPath("ad/ad/pic1.png");
		adList.add(_ad);
		_ad = new Advertising();
		_ad.setPath("ad/ad/pic2.png");
		adList.add(_ad);
		_ad = new Advertising();
		_ad.setPath("ad/ad/pic3.png");
		adList.add(_ad);
		ADManager.instance().setAdvertisingList(adList);
		/***************************/
		
		// 跳转首页
		Intent intent = new Intent(ctx,MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		ctx.startActivity(intent);
		((Activity) ctx).finish();
		BroadcastManager.sendBroadcast(BroadcastManager.ACTION_UPDATE_MODE_MESSAGE, null);
	}

}
