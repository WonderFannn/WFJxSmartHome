package com.jinxin.jxsmartdoorbell.service;

interface IBellService {
  void init(String param1);
  void logout();
  int isConnected();
}