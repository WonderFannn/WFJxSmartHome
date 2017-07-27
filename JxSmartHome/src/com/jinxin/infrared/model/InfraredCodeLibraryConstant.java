package com.jinxin.infrared.model;

import com.jinxin.jxsmarthome.R;

public class InfraredCodeLibraryConstant {
	
	public static class IntentTag{
		public static final String DEVICE_TYPE = "DEVICETYPE";
		public static final String BRAND = "BRAND";
		public static final String MODEL = "MODEL";
		public static final String UFO = "UFO";
		public static final String PRODUCTFUN = "PRODUCTFUN";
		public static final String FUNDETIAL = "FUNDETIAL";
	}
	public static class DataBase{
		public static final String[] TABLENAME = {
			"AIR_TABLE",//0
			"ARC_TABLE",//1
			"PJT_TABLE",//2
			"FAN_TABLE",//3
			"TVBOX_TABLE",//4
			"TV_TABLE",//5
			"IPTV_TABLE",//6
			"DVD_TABLE",//7
			"WATER_TABLE",//8
			"SLR_TABLE",//9
		};
	}
	
	public static class DeviceType{
		public static final int AirCleaner = 0;
		public static final int AirCondition = 1;
		public static final int Projector = 2;
		public static final int Fan = 3;
		public static final int TvBox = 4;
		public static final int Tv = 5;
		public static final int InternetTv = 6;
		public static final int Dvd = 7;
		public static final int Calorifier = 8;
		public static final int Camera = 9;
	}
	
	public static class MatchCodeImage {
		public static class AirCleaner{
			public static final int POWER = R.drawable.btn_matchcode_power;
			public static final int TIMER = R.drawable.btn_matchcode_timer;
			public static final int MUTE = R.drawable.btn_matchcode_mute;
			public static final int MODE = R.drawable.btn_matchcode_mode;
		}
		public static class AirCondition{
			public static final int POWER = R.drawable.btn_matchcode_power;
			public static final int TIMER = R.drawable.btn_matchcode_timer;
			public static final int TEMPERATURE_UP = R.drawable.btn_matchcode_temperature_up;
			public static final int MODE = R.drawable.btn_matchcode_mode;
		}
		public static class Projector{
			public static final int POWER = R.drawable.btn_matchcode_power;
			public static final int TIMER = R.drawable.btn_matchcode_timer;
			public static final int MUTE = R.drawable.btn_matchcode_mute;
			public static final int SCREEN = R.drawable.btn_matchcode_screen;
		}
		public static class Fan{
			public static final int POWER = R.drawable.btn_matchcode_power;
			public static final int TIMER = R.drawable.btn_matchcode_timer;
			public static final int SHAKE = R.drawable.btn_matchcode_shake;
			public static final int MIDSPEED = R.drawable.btn_matchcode_midspeed;
		}
		public static class TvBox{
			public static final int POWER = R.drawable.btn_matchcode_power;
			public static final int TIMER = R.drawable.btn_matchcode_timer;
			public static final int CHANNEL_ADD = R.drawable.btn_matchcode_channel_add;
			public static final int VOICE_UP = R.drawable.btn_matchcode_voice_up;
		}
		public static class Tv{
			public static final int POWER = R.drawable.btn_matchcode_power;
			public static final int TIMER = R.drawable.btn_matchcode_timer;
			public static final int CHANNEL_ADD = R.drawable.btn_matchcode_channel_add;
			public static final int VOICE_UP = R.drawable.btn_matchcode_voice_up;
		}
		public static class InternetTv{
			public static final int POWER = R.drawable.btn_matchcode_power;
			public static final int TIMER = R.drawable.btn_matchcode_timer;
			public static final int CHANNEL_ADD = R.drawable.btn_matchcode_channel_add;
			public static final int VOICE_UP = R.drawable.btn_matchcode_voice_up;
		}
		public static class Dvd{
			public static final int POWER = R.drawable.btn_matchcode_power;
			public static final int MENU = R.drawable.btn_matchcode_menu;
			public static final int MODE = R.drawable.btn_matchcode_mode;
			public static final int MUTE = R.drawable.btn_matchcode_mute;
		}
		public static class Calorifier{
			public static final int POWER = R.drawable.btn_matchcode_power;
			public static final int TIMER = R.drawable.btn_matchcode_timer;
			public static final int TEMPERATURE_UP = R.drawable.btn_matchcode_temperature_up;
			public static final int MODE = R.drawable.btn_matchcode_mode;
		}
		public static class Camera{
			public static final int POWER = R.drawable.btn_matchcode_power;
			public static final int TAKE_PICTURE = R.drawable.btn_matchcode_take_picture;
			public static final int MODE = R.drawable.btn_matchcode_mode;
			public static final int TIMER = R.drawable.btn_matchcode_timer;
		}
		
	}
	
}
