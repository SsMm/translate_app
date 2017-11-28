package com.zgy.translate.domains.eventbuses;

import android.bluetooth.BluetoothDevice;

/**
 * Created by zhouguangyue on 2017/11/28.
 */

public class BluetoothDeviceEB {

    private BluetoothDevice bluetoothDevice;


    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }
}
