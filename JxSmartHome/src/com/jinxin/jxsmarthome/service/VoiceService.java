package com.jinxin.jxsmarthome.service;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.cmd.device.Text2VoiceManager;
import com.jinxin.jxsmarthome.main.JxshApp;


public class VoiceService extends Service {

	private List<String> voiceList = new ArrayList<String>();
	private int pos = -1;
	
	@Override
	public void onCreate() {
		super.onCreate();
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(BroadcastManager.ACTION_VOICE_MULTIPLE_PLAY);
		mFilter.addAction(BroadcastManager.ACTION_VOICE_MULTIPLE_PLAY_NEXT);
		registerReceiver(VoicePlayBroaderCast, mFilter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(VoicePlayBroaderCast);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private BroadcastReceiver VoicePlayBroaderCast = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			List<String> tempList = intent.getStringArrayListExtra("voiceList");
			if (tempList != null && tempList.size() > 0) {
				voiceList = tempList;
			}
			if (BroadcastManager.ACTION_VOICE_MULTIPLE_PLAY.equals(action)) {
				if (voiceList == null || voiceList.size() < 1) {
					JxshApp.showToast(getApplicationContext(), "无语音可播放");
					return;
				}
				pos = 0;
				playVoice(pos);
			}else if(BroadcastManager.ACTION_VOICE_MULTIPLE_PLAY_NEXT.equals(action)){
				if (voiceList == null || voiceList.size() < 1) {
//					JxshApp.showToast(getApplicationContext(), "语音播放完毕");
					return;
				}
				if (pos < voiceList.size()-1) {
					pos++;
					playVoice(pos);
				}
			}
		}
	};
	
	private void playVoice(int pos){
		if (voiceList == null || voiceList.size() < 1) {
			JxshApp.showToast(getApplicationContext(), "语音播放完毕");
			return;
		}
		Text2VoiceManager manager = new Text2VoiceManager(getApplicationContext());
		manager.switchAndSend(voiceList.get(pos));
		
	}

}
