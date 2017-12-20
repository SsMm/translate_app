package com.zgy.translate.receivers.interfaces;

import android.bluetooth.BluetoothDevice;

/**
 * Created by zhouguangyue on 2017/11/29.
 */

public interface BluetoothReceiverInterface {

    void receiverDevice(BluetoothDevice device); //返回搜索到的设备
    void receiverDeviceState(int state, BluetoothDevice device); //返回设备状态
    void receiverDevicePinState(boolean pin, BluetoothDevice device); //返回配对结果
    void receivefinished(); //搜索完成

}
