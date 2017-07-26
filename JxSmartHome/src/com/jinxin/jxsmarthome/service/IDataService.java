package com.jinxin.jxsmarthome.service;


/**
 * service数据获取接口
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public interface IDataService {
    String getSystemTime();//取系统日期时间
    int getSignal();//取信号
    int getBatteryCharge();//取电池电量
}
