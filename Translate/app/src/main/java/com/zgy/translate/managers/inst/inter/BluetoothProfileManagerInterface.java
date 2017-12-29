package com.zgy.translate.managers.inst.inter;

import android.bluetooth.BluetoothGatt;

/**
 * Created by zhouguangyue on 2017/12/14.
 */

public interface BluetoothProfileManagerInterface {

    void bluetoothOff(); //关闭蓝牙
    void noProfile(); //没有连接任何蓝牙设备
    void deviceConning(); //设备连接中
    void getA2DPProfileFinish(boolean result);
    void getBLEProfileFinish(BluetoothGatt gatt, boolean result);
}
