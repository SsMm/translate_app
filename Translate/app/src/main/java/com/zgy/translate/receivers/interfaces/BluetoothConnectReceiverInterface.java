package com.zgy.translate.receivers.interfaces;

/**
 * Created by zhouguangyue on 2018/1/6.
 */

public interface BluetoothConnectReceiverInterface {

    void blueConnected();
    void blueDisconnected();
    void getBlueInputStream(String data);
}
