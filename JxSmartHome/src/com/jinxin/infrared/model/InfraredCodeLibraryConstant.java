package com.jinxin.infrared.model;

import com.jinxin.jxsmarthome.R;

public class InfraredCodeLibraryConstant {

	public static class IntentTag {
		public static final String DEVICE_TYPE = "DEVICETYPE";
		public static final String BRAND = "BRAND";
		public static final String MODEL = "MODEL";
		public static final String CODE = "CODE";
		public static final String UFO = "UFO";
		public static final String PRODUCTFUN = "PRODUCTFUN";
		public static final String FUNDETIAL = "FUNDETIAL";
	}

	public static class DataBase {
		public static final String[] TABLENAME = { 
				"AIR_TABLE",// 0
				"ARC_TABLE",// 1
				"PJT_TABLE",// 2
				"FAN_TABLE",// 3
				"TVBOX_TABLE",// 4
				"TV_TABLE",// 5
				"IPTV_TABLE",// 6
				"DVD_TABLE",// 7
				"WATER_TABLE",// 8
				"SLR_TABLE",// 9
		};
	}

	public static final String[] DEVICE_NAME_CN = { 
		"空气净化器",// 0
		"空调",// 1
		"投影仪",// 2
		"风扇",// 3
		"机顶盒",// 4
		"电视",// 5
		"IPTV",// 6
		"DVD",// 7
		"热水器",// 8
		"单反相机",// 9
	};
	public static class DeviceType {
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
		public static class AirCleaner {
			public static final int POWER = R.drawable.btn_matchcode_power;
			public static final int TIMER = R.drawable.btn_matchcode_timer;
			public static final int MUTE = R.drawable.btn_matchcode_mute;
			public static final int MODE = R.drawable.btn_matchcode_mode;
		}

		public static class AirCondition {
			public static final int POWER = R.drawable.btn_matchcode_power;
			public static final int MUTE = R.drawable.btn_matchcode_mute;
			public static final int TEMPERATURE_UP = R.drawable.btn_matchcode_temperature_up;
			public static final int MODE = R.drawable.btn_matchcode_mode;
		}

		public static class Projector {
			public static final int POWER_ON = R.drawable.btn_matchcode_power;
			public static final int POWER_OFF = R.drawable.btn_matchcode_power;
			public static final int MUTE = R.drawable.btn_matchcode_mute;
			public static final int SCREEN = R.drawable.btn_matchcode_screen;
		}

		public static class Fan {
			public static final int POWER = R.drawable.btn_matchcode_power;
			public static final int TIMER = R.drawable.btn_matchcode_timer;
			public static final int SHAKE = R.drawable.btn_matchcode_shake;
			public static final int MIDSPEED = R.drawable.btn_matchcode_midspeed;
		}

		public static class TvBox {
			public static final int POWER = R.drawable.btn_matchcode_power;
			public static final int MENU = R.drawable.btn_matchcode_menu;
			public static final int CHANNEL_ADD = R.drawable.btn_matchcode_channel_add;
			public static final int VOICE_UP = R.drawable.btn_matchcode_voice_up;
		}

		public static class Tv {
			public static final int POWER = R.drawable.btn_matchcode_power;
			public static final int MENU = R.drawable.btn_matchcode_menu;
			public static final int CHANNEL_ADD = R.drawable.btn_matchcode_channel_add;
			public static final int VOICE_UP = R.drawable.btn_matchcode_voice_up;
		}

		public static class InternetTv {
			public static final int POWER = R.drawable.btn_matchcode_power;
			public static final int MUTE = R.drawable.btn_matchcode_mute;
			public static final int CHANNEL_ADD = R.drawable.btn_matchcode_channel_add;
			public static final int VOICE_UP = R.drawable.btn_matchcode_voice_up;
		}

		public static class Dvd {
			public static final int POWER = R.drawable.btn_matchcode_power;
			public static final int MENU = R.drawable.btn_matchcode_menu;
			public static final int MODE = R.drawable.btn_matchcode_mode;
			public static final int MUTE = R.drawable.btn_matchcode_mute;
		}

		public static class Calorifier {
			public static final int POWER = R.drawable.btn_matchcode_power;
			public static final int TIMER = R.drawable.btn_matchcode_timer;
			public static final int TEMPERATURE_UP = R.drawable.btn_matchcode_temperature_up;
			public static final int MODE = R.drawable.btn_matchcode_mode;
		}

		public static class Camera {
			public static final int POWER = R.drawable.btn_matchcode_power;
			public static final int TAKE_PICTURE = R.drawable.btn_matchcode_take_picture;
			public static final int MODE = R.drawable.btn_matchcode_mode;
			public static final int TIMER = R.drawable.btn_matchcode_timer;
		}

	}
	public static class PJTKeyBoadMap {
		public static final int PJT_On = 1; // 开机
		public static final int PJT_Off = 3; // 关机
		public static final int PJT_Computer = 5; // 电脑
		public static final int PJT_Video = 7; // 视频
		public static final int PJT_SignalSource = 9; // 信号源
		public static final int PJT_FocusAdd = 11; // 变焦＋
		public static final int PJT_FocusRed = 13; // 变焦－
		public static final int PJT_PictureAdd = 15; // 画面＋
		public static final int PJT_PictureRed = 17; // 画面－
		public static final int PJT_Menu = 19; // 菜单
		public static final int PJT_Confirm = 21; // 确认
		public static final int PJT_Up = 23; // 上
		public static final int PJT_Left = 25; // 左
		public static final int PJT_Right = 27; // 右
		public static final int PJT_Down = 29; // 下
		public static final int PJT_Quit = 31; // 退出
		public static final int PJT_VolAdd = 33; // 音量＋
		public static final int PJT_VolRed = 35; // 音量－
		public static final int PJT_Mute = 37; // 静音
		public static final int PJT_Auto = 39; // 自动
		public static final int PJT_Pause = 41; // 暂停
		public static final int PJT_MCD = 43; // 亮度

	}

	public static class FanKeyBoadMap {
		public static final int FAN_Power = 1; // 开关
		public static final int FAN_On_speed = 3; // 开／风速
		public static final int FAN_Shake = 5; // 摇头
		public static final int FAN_Mode = 7; // 风类（模式）
		public static final int FAN_Timer = 9; // 定时
		public static final int FAN_Light = 11; // 灯光
		public static final int FAN_Anion = 13; // 负离子
		public static final int FAN_1 = 15; // 1
		public static final int FAN_2 = 17; // 2
		public static final int FAN_3 = 19; // 3
		public static final int FAN_4 = 21; // 4
		public static final int FAN_5 = 23; // 5
		public static final int FAN_6 = 25; // 6
		public static final int FAN_7 = 27; // 7
		public static final int FAN_8 = 29; // 8
		public static final int FAN_9 = 31; // 9
		public static final int FAN_Sleep = 33; // 睡眠
		public static final int FAN_Cold = 35; // 制冷
		public static final int FAN_AirVol = 37; // 风量
		public static final int FAN_LowSpeed = 39; // 低速
		public static final int FAN_MiddleSpeed = 41; // 中速
		public static final int FAN_HighSpeed = 43; // 高速

	}

	public static class TVBoxKeyBoadMap {
		public static final int TVBOX_Wait = 1; // 待机
		public static final int TVBOX_1 = 3; // 1
		public static final int TVBOX_2 = 5; // 2
		public static final int TVBOX_3 = 7; // 3
		public static final int TVBOX_4 = 9; // 4
		public static final int TVBOX_5 = 11; // 5
		public static final int TVBOX_6 = 13; // 6
		public static final int TVBOX_7 = 15; // 7
		public static final int TVBOX_8 = 17; // 8
		public static final int TVBOX_9 = 19; // 9
		public static final int TVBOX_Lead = 21; // 导视
		public static final int TVBOX_0 = 23; // 0
		public static final int TVBOX_Back = 25; // 返回
		public static final int TVBOX_Up = 27; // 上
		public static final int TVBOX_Left = 29; // 左
		public static final int TVBOX_Comfirm = 31; // 确定
		public static final int TVBOX_Right = 33; // 右
		public static final int TVBOX_Down = 35; // 下
		public static final int TVBOX_VolAdd = 37; // 声音＋
		public static final int TVBOX_VolRed = 39; // 声音－
		public static final int TVBOX_ChAdd = 41; // 频道＋
		public static final int TVBOX_ChRed = 43; // 频道－
		public static final int TVBOX_Menu = 45; // 菜单

	}

	public static class IPTVKeyBoadMap {
		public static final int IPTV_Power = 1; // 电源
		public static final int IPTV_Mute = 3; // 静音
		public static final int IPTV_VolAdd = 5; // 音量＋
		public static final int IPTV_VolRed = 7; // 音量－
		public static final int IPTV_ChAdd = 9; // 频道＋
		public static final int IPTV_ChRed = 11; // 频道-
		public static final int IPTV_Up = 13; // 上
		public static final int IPTV_Left = 15; // 左
		public static final int IPTV_OK = 17; // OK
		public static final int IPTV_Right = 19; // 右
		public static final int IPTV_Down = 21; // 下
		public static final int IPTV_Play = 23; // 播放／暂停
		public static final int IPTV_1 = 25; // 1
		public static final int IPTV_2 = 27; // 2
		public static final int IPTV_3 = 29; // 3
		public static final int IPTV_4 = 31; // 4
		public static final int IPTV_5 = 33; // 5
		public static final int IPTV_6 = 35; // 6
		public static final int IPTV_7 = 37; // 7
		public static final int IPTV_8 = 39; // 8
		public static final int IPTV_9 = 41; // 9
		public static final int IPTV_0 = 43; // 0
		public static final int IPTV_BACK = 45; // 返回

	}

	public static class TVKeyBoadMap {
		public static final int TV_VolAdd = 1; // 声音＋
		public static final int TV_ChAdd = 3; // 频道＋
		public static final int TV_Menu = 5; // 菜单
		public static final int TV_ChRed = 7; // 频道－
		public static final int TV_VolRed = 9; // 声音－
		public static final int TV_Power = 11; // 电源
		public static final int TV_Mute = 13; // 静音
		public static final int TV_1 = 15; // 1
		public static final int TV_2 = 17; // 2
		public static final int TV_3 = 19; // 3
		public static final int TV_4 = 21; // 4
		public static final int TV_5 = 23; // 5
		public static final int TV_6 = 25; // 6
		public static final int TV_7 = 27; // 7
		public static final int TV_8 = 29; // 8
		public static final int TV_9 = 31; // 9
		public static final int TV_Res = 33; // --/-
		public static final int TV_0 = 35; // 0
		public static final int TV_AV_TV = 37; // AV/TV
		public static final int TV_Back = 39; // 返回
		public static final int TV_Comfirm = 41; // 确定
		public static final int TV_Up = 43; // 上
		public static final int TV_Left = 45; // 左
		public static final int TV_Right = 47; // 右
		public static final int TV_Down = 49; // 下

	}

	public static class DVDKeyBoadMap {
		public static final int DVD_Left = 1; // 左
		public static final int DVD_Up = 3; // 上
		public static final int DVD_Ok = 5; // ok
		public static final int DVD_Down = 7; // 下
		public static final int DVD_Right = 9; // 右
		public static final int DVD_Power = 11; // 电源
		public static final int DVD_Mute = 13; // 静音
		public static final int DVD_FBack = 15; // 快倒
		public static final int DVD_Play = 17; // 播放
		public static final int DVD_FForwad = 19; // 快进
		public static final int DVD_Last = 21; // 上一曲
		public static final int DVD_Stop = 23; // 停止
		public static final int DVD_Next = 25; // 下一曲
		public static final int DVD_Format = 27; // 制式
		public static final int DVD_Pause = 29; // 暂停
		public static final int DVD_Title = 31; // 标题
		public static final int DVD_SK = 33; // 开关仓
		public static final int DVD_Menu = 35; // 静音
		public static final int DVD_Back = 37; // 返回

	}

	public static class ARCKeyBoadMap {
		public static final int ARC_Power = 0x01; // 电源
		public static final int ARC_Mode = 0x02; // 模式
		public static final int ARC_Vol = 0x03; // 风量
		public static final int ARC_M = 0x04; // 手动风向
		public static final int ARC_A = 0x05; // 自动风向：
		public static final int ARC_tmpAdd = 0x06; // 温度＋
		public static final int ARC_tmpRed = 0x07; // 温度－

	}

	public static class WheaterKeyBoadMap {
		public static final int WH_Power = 1; // 电源
		public static final int WH_Set = 3; // 设置
		public static final int WH_TemAdd = 5; // 温度＋
		public static final int WH_TemRed = 7; // 温度－
		public static final int WH_Mode = 9; // 模式
		public static final int WH_Confirm = 11; // 确定
		public static final int WH_Timer = 13; // 定时
		public static final int WH_Ant = 15; // 预约
		public static final int WH_Time = 17; // 时间
		public static final int WH_Stem = 19; // 保温

	}

	public static class AirKeyBoadMap {
		public static final int AIR_Power = 1; // 电源
		public static final int AIR_Auto = 3; // 自动
		public static final int AIR_AirVol = 5; // 风量
		public static final int AIR_Timer = 7; // 定时
		public static final int AIR_Mode = 9; // 模式
		public static final int AIR_Anion = 11; // 负离子
		public static final int AIR_Comfort = 13; // 舒适
		public static final int AIR_Mute = 15; // 静音

	}

	public static class SLRKeyBoadMap {
		public static final int SLR_TPIC = 1; // 拍照
	}

}
