package com.zgy.translate.receivers.interfaces;

import android.bluetooth.BluetoothGattService;

import java.util.List;

/**
 * Created by zhouguangyue on 2017/12/4.
 */

public interface BluetoothLeGattUpdateReceiverInterface {

    void gattConnected(); //与设备gatt连接成功
    void gattDisconnected(); //连接失败
    void gattServicesDiscovered(); //连接成功获取gatt可用的服务
    void gattDataAvailable(String data); //获取接收发送数据
}
