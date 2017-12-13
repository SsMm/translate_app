package com.zgy.translate.adapters.interfaces;

import android.bluetooth.BluetoothDevice;

/**
 * Created by zhouguangyue on 2017/12/13.
 */

public interface BluetoothBondedDeviceAdapterInterface {

    void bondedToConnection(int position, BluetoothDevice device); //绑定设备去建立连接

}
