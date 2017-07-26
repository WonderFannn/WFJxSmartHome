package com.jinxin.broad;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import org.json.JSONException;
import org.json.JSONObject;

import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.record.SharedDB;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import cn.jpush.android.api.JPushInterface;

@SuppressLint("NewApi")
public class BroadPushServerService extends Service {

    private static final int ECHOMAX = 255; // 发送或接收的信息最大字节数  

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        Thread thread = new Thread(new Runnable() {
            
            @Override
            public void run() {
                int servPort = 10000;  
                
                DatagramSocket socket;
                try {
                    socket = new DatagramSocket(servPort);
                    byte[] buffer = new byte[ECHOMAX];
                    DatagramPacket packet = new DatagramPacket(buffer, ECHOMAX);
                    while (true) { // 不断接收来自客户端的信息及作出相应的相应  
                        socket.receive(packet); // Receive packet from client
                        System.out.println("Handling client at " + packet.getAddress().getHostAddress() + " on port " + packet.getPort());  
                        String data = new String(packet.getData(),0,packet.getLength());
                        if(data.indexOf("\r\n")==-1){
                        	continue;
                        }else {
                        	data = data.substring(0, data.indexOf("\r\n"));
						}
                        System.out.println("接收数据：" + data);
                        
                        try {
                            String _account = SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS, ControlDefine.KEY_ACCOUNT,"");
                            JSONObject jObj = new JSONObject(data);
                            String target = jObj.getString("uId");
                            if(!TextUtils.isEmpty(target) && target.equals(_account) ) {
                                Intent intent = new Intent(JPushInterface.ACTION_MESSAGE_RECEIVED);
                                intent.addCategory("com.jinxin.jxsmarthome");
                                intent.putExtra(JPushInterface.EXTRA_EXTRA, data);
                                BroadPushServerService.this.sendBroadcast(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        socket.send(packet); // 将客户端发送来的信息返回给客户端  
//                        packet.setLength(ECHOMAX);   
                        // 重置packet的内部长度，因为处理了接收到的信息后，数据包的内部长度将被                                                         
                        //设置为刚处理过的信息的长度，而这个长度可能比缓冲区的原始长度还要短，  
                        //如果不重置，而且接收到的新信息长于这个内部长度，则超出长度的部分将会被截断，所以这点必须注意到。  
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }  
            }
        });
        thread.start();
    }

}
