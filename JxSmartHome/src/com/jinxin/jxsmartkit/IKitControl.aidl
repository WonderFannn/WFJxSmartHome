// IKitControl.aidl
package com.jinxin.jxsmartkit;

interface IKitControl {
    void login(String account,String password,String appId,String appKey);

    void exit();
}
