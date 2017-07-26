package com.jinxin.jxsmarthome.service;

import java.util.ArrayList;
import java.util.List;

import org.teleal.cling.android.AndroidUpnpService;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.registry.DefaultRegistryListener;
import org.teleal.cling.registry.Registry;

import com.jinxin.db.impl.MusicLibDaoImpl;
import com.jinxin.jxsmarthome.aidl.MusicServiceAidl;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.constant.MusicSetting;
import com.jinxin.jxsmarthome.dlna.controller.UpnpController;
import com.jinxin.jxsmarthome.dlna.service.BrowserUpnpService;
import com.jinxin.jxsmarthome.entity.MusicLib;
import com.jinxin.jxsmarthome.entity.DeviceDisplay;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.record.SharedDB;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import com.jinxin.jxsmarthome.util.Logger;

/**
 * 音乐播放控制服务
 * @author YangJiJUn
 * @company 金鑫智慧
 */
@SuppressWarnings("rawtypes")
public class MusicService extends Service {
	
	private AndroidUpnpService upnpService;
	private UpnpController mCcontroller; //播放控制器
	
	private List<DeviceDisplay> deviceList;
	private DeviceDisplay mDevice = null;//播放设备
	private BrowseRegistryListener registryListener = null;
	
	private String NOW_STATUS = PLAYSTATUS.STOPPED.toString();//当前播放状态
	private String CUR_TIME = "00:00:00";//当前播放时间
	private String MAX_TIME = "00:00:00";//歌曲总时长
	
	private MusicLib musicLib;//当前播放音乐
	private List<MusicLib> musicList = null;//音乐播放列表
	private int currentPos = -1;//当前播放
	private int currentId = -1;//当前播放音乐ID
	private String musicUrl = "";
	
	private boolean isRunning = false;//后台切换歌曲
	private boolean isRepeatPlay = false;//是否单曲循环
	private boolean isNewSong = false;//是否单曲循环
	private MusicThread musicThread;
	
	/**
	 * 播放状态
	 */
	public static enum PLAYSTATUS{
		TRANSITIONING,PLAYING,PAUSED_PLAYBACK,STOPPED,NO_MEDIA_PRESENT;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		isRunning = true;
		deviceList = new ArrayList<DeviceDisplay>();
		registryListener = new BrowseRegistryListener();
		getApplicationContext().bindService(
                new Intent(this, BrowserUpnpService.class),
                deviceSearchConnection,
                Context.BIND_AUTO_CREATE
        );
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(BroadcastManager.ACTION_MUSIC_PLAY_CONTROL);
		mFilter.addAction(BroadcastManager.ACTION_MUSIC_PLAY_PAUSE);
		mFilter.addAction(BroadcastManager.ACTION_MUSIC_PLAY_SEEK);
		mFilter.addAction(BroadcastManager.ACTION_MUSIC_PRE_SONG);
		mFilter.addAction(BroadcastManager.ACTION_MUSIC_NEXT_SONG);
		mFilter.addAction(BroadcastManager.ACTION_DLNA_DEVICE_SEARCH);
		mFilter.addAction(BroadcastManager.ACTION_DLNA_DEVICE_CHOOSE);
		mFilter.addAction(BroadcastManager.ACTION_CHANG_MUSIC_IN_BACKGROUND);
		registerReceiver(playControlBroaderCast, mFilter);
		
		isNewSong = true;
		musicThread = new MusicThread();
		musicThread.start();
	}
	
	class MusicThread extends Thread{

		@Override
		public void run() {
			while (isRunning) {
				try {
					if (mCcontroller != null && mDevice != null) {
						NOW_STATUS = mCcontroller.getTransportState(mDevice.getDevice());
						if (MusicService.PLAYSTATUS.PLAYING.toString().equals(NOW_STATUS)) {
							
						}else if (PLAYSTATUS.STOPPED.toString().equals(NOW_STATUS)) {
							Logger.error("Yang", "service STOPPED--" + isNewSong);
							if (!isNewSong) {
								isRepeatPlay =  SharedDB.loadBooleanFromDB(MusicSetting.SP_NAME,
										MusicSetting.CLOUD_MUSIC_PLAY_MODE, false);
								if (!isRepeatPlay) {
									if (musicList != null && musicList.size() > 0) {
										if (currentId < musicList.size()) {
											currentId++;
										}else{
											currentId = 1;
										}
										musicLib = musicList.get(currentId-1);
										if (musicLib != null) {
											musicUrl = musicLib.getNetUrl();
											playMusic(musicUrl);
											JxshApp.instance.setMusic(musicLib);
										}
									}
								}else{
									playMusic(musicUrl);
									JxshApp.instance.setMusic(musicLib);
								}
							}
						}
					}
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		isRunning = false;
		if (musicThread != null) {
			musicThread.interrupt();
		}
		//注销广播
		unregisterReceiver(playControlBroaderCast);
		if (upnpService != null) {
            upnpService.getRegistry().removeListener(registryListener);
        }
		getApplicationContext().unbindService(deviceSearchConnection);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	 /**
     * 音乐播放控制广播接收器
     */
    private BroadcastReceiver playControlBroaderCast = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			if (BroadcastManager.ACTION_MUSIC_PLAY_CONTROL.equals(action)) {//播放、暂停控制
				if (mDevice == null) {
					JxshApp.showToast(getApplicationContext(), "未找到播放设备,请重新搜索设备");
					return;
				}
//				int musicId = intent.getIntExtra("musicId",0);
//				String musicUrl = intent.getStringExtra("musicUrl");
				musicLib  = (MusicLib) intent.getSerializableExtra("musicLib");
				if (musicLib != null) {
					musicUrl = musicLib.getNetUrl();
					int musicId = musicLib.getId();
					if (currentId != musicId) {
						currentId = musicId;
						mCcontroller.stop(mDevice.getDevice());
						playMusic(musicUrl);
						NOW_STATUS = PLAYSTATUS.PLAYING.toString();
					}else{
						if (NOW_STATUS.equals(PLAYSTATUS.PLAYING.toString())) {
							pauseMusic();
						}else {
							playMusic(musicUrl);
						}
					}
				}
			}else if(BroadcastManager.ACTION_MUSIC_PLAY_PAUSE.equals(action)){//暂停
				pauseMusic();
			}else if(BroadcastManager.ACTION_MUSIC_PRE_SONG.equals(action)){
				preMusic();
			}else if(BroadcastManager.ACTION_MUSIC_NEXT_SONG.equals(action)){
				nextMusic();
			}else if(BroadcastManager.ACTION_DLNA_DEVICE_SEARCH.equals(action)){
				searchNetwork();
			}else if(BroadcastManager.ACTION_DLNA_DEVICE_CHOOSE.equals(action)){//选中播放设备
				String name = intent.getStringExtra("name");
				if (deviceList != null && deviceList.size() > 0) {
					for (DeviceDisplay device : deviceList) {
						if (device.getDevice().getDetails().getFriendlyName().equals(name)) {
							mDevice = device;
						}
					}
				}
			}else if(BroadcastManager.ACTION_MUSIC_PLAY_SEEK.equals(action)){//跳转进度
				String seekPos = intent.getStringExtra("seekTo");
				if (!TextUtils.isEmpty(seekPos) && mDevice.getDevice() != null) {
					mCcontroller.seek(mDevice.getDevice(),seekPos);
				}
			}else if(BroadcastManager.ACTION_CHANG_MUSIC_IN_BACKGROUND.equals(action)){
				isNewSong = intent.getBooleanExtra("isNewSong", false);
				musicList = getMusicListFromDB();
			}
		
		}
	};
	
	/**
	 * 获取本地数据库音乐列表
	 * @return
	 */
	private List<MusicLib> getMusicListFromDB(){
		MusicLibDaoImpl mlDaoImpl = new MusicLibDaoImpl(getApplicationContext());
		List<MusicLib> musicList = mlDaoImpl.find();
		return musicList;
	}
	
    /**
     * 播放音乐
     * @param pos
     */
    private void playMusic(int pos){
    	if (mCcontroller == null || mDevice == null) return;
    	if (musicList.size() <1) {
			JxshApp.showToast(getApplicationContext(), "播放列表为空");
			return;
		}
    	
    	if (currentPos < 0 || currentPos > musicList.size()-1) {
			currentPos = 0;
		}
    	if (musicList.size() > 0) {
			mCcontroller.play(mDevice.getDevice(), musicList.get(pos).getNetUrl());
		}
    }
    
    /******************使用URL直接播放  目前都采用此方式********************/
    private void playMusic(String musicUrl){
    	if (mCcontroller == null || mDevice == null) return;
    	if (TextUtils.isEmpty(musicUrl)) {
    		JxshApp.showToast(getApplicationContext(), "未找到歌曲地址");
    		return;
    	}
    	
		mCcontroller.play(mDevice.getDevice(), musicUrl);
    }
    
    /**
     * 暂停音乐
     */
    private void pauseMusic(){
    	if (mCcontroller == null || mDevice == null) return;
    	mCcontroller.pause(mDevice.getDevice());
    }
    
    /**
     * 上一曲
     */
    private void preMusic(){
    	if (musicList.size() < 1) return;
    	
    	if (currentPos == 0) {
			currentPos = musicList.size();
			playMusic(currentPos);
		}else{
			currentPos--;
			playMusic(currentPos);
		}
    }
    
    /**
     * 下一曲
     */
    private void nextMusic(){
    	if (musicList.size() < 1) return;
    	if (currentPos == musicList.size()) {
			currentPos = 0;
			playMusic(currentPos);
		}else{
			playMusic(currentPos);
		}
    }
    
    private MusicServiceAidl.Stub mBinder = new MusicServiceAidl.Stub() {
		
		@Override
		public String getPlayStatus() throws RemoteException {
			if (mCcontroller != null && mDevice != null) {
				NOW_STATUS = mCcontroller.getTransportState(mDevice.getDevice());
			}
			return NOW_STATUS;
		}
		
		@Override
		public String getCurrentTime() throws RemoteException {
			if (mCcontroller != null && mDevice != null) {
				CUR_TIME = mCcontroller.getPositionInfo(mDevice.getDevice());
			}
			return CUR_TIME;
		}

		@Override
		public String getDurationTime() throws RemoteException {
			if (mCcontroller != null && mDevice != null) {
				MAX_TIME = mCcontroller.getMediaDuration(mDevice.getDevice());
			}
			return MAX_TIME;
		}
		
	};
	
	/**
	 * 搜索所有连接的设备的Connection
	 */
	
	private ServiceConnection deviceSearchConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            upnpService = (AndroidUpnpService) service;

            if (mCcontroller == null) {
				mCcontroller = new UpnpController(upnpService.getControlPoint());
			}
            // Refresh the list with all known devices
//          for (Device device : upnpService.getRegistry().getDevices()) {
//              registryListener.deviceAdded(device);
//          }

            // Getting ready for future device advertisements
            upnpService.getRegistry().addListener(registryListener);

            // Search asynchronously for all devices
            upnpService.getControlPoint().search();
        }

        public void onServiceDisconnected(ComponentName className) {
            upnpService = null;
        }
    };
    
    /**
     * 设备注册监听
     */
    public class BrowseRegistryListener extends DefaultRegistryListener {

         // Discovery performance optimization for very slow Android devices! 

        @Override
        public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) {
//            deviceAdded(device);
        }

        @Override
        public void remoteDeviceDiscoveryFailed(Registry registry, final RemoteDevice device, final Exception ex) {
//            deviceRemoved(device);
        }

        @Override
        public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
        	Logger.debug("Yang", device.getDetails().getFriendlyName());
        	String account = CommUtil.getCurrentLoginAccount();
        	String deviceName = SharedDB.loadStrFromDB(account, ControlDefine.
						KEY_DLNA_NAME, "");
        	if (device.getDetails().getFriendlyName().equals(deviceName)) {
				mDevice = new DeviceDisplay(device);
			}
            deviceAdded(device);
        }

        @Override
        public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
        	Logger.debug("Yang", "remoteDeviceRemoved");
            deviceRemoved(device);
        }

        @Override
        public void localDeviceAdded(Registry registry, LocalDevice device) {
        	Logger.debug("Yang", device.getDetails().getFriendlyName());
            deviceAdded(device);
        }

        @Override
        public void localDeviceRemoved(Registry registry, LocalDevice device) {
        	Logger.debug("Yang", "localDeviceRemoved");
            deviceRemoved(device);
        }

		public void deviceAdded(final Device device) {
        	DeviceDisplay dDevice = new DeviceDisplay(device);
        	deviceList.add(dDevice);
        	Intent intent = new Intent(BroadcastManager.ACTION_DLNA_DEVICE_LIST_ADD);
        	intent.putExtra("name", dDevice.getDevice().getDetails().getFriendlyName());
        	sendBroadcast(intent);
        }

        public void deviceRemoved(final Device device) {
        	DeviceDisplay dDevice = new DeviceDisplay(device);
        	deviceList.add(dDevice);
        	Intent intent = new Intent(BroadcastManager.ACTION_DLNA_DEVICE_LIST_REMOVE);
        	intent.putExtra("name", dDevice.getDevice().getDetails().getFriendlyName());
        	sendBroadcast(intent);
        }
    }
    
    /**
     * 搜索设备
     */
    protected void searchNetwork() {
        if (upnpService == null) return;
        upnpService.getRegistry().removeAllRemoteDevices();
        upnpService.getControlPoint().search();
    }

}
