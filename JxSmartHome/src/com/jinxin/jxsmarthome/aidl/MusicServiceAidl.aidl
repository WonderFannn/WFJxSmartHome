package com.jinxin.jxsmarthome.aidl;

interface MusicServiceAidl{
	String getPlayStatus();//获取播放状态
	String getCurrentTime();//获取当前播放时间
	String getDurationTime();//获取歌曲总时长
}
