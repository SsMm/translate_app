package com.zgy.translate.domains.dtos;


import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

/**
 * Created by zhouguangyue on 2017/12/13.
 */

public class BluetoothSocketDTO {

    private BluetoothDevice mBluetoothDevice;
    private BluetoothSocket mBluetoothSocket;

    public BluetoothDevice getmBluetoothDevice() {
        return mBluetoothDevice;
    }

    public void setmBluetoothDevice(BluetoothDevice mBluetoothDevice) {
        this.mBluetoothDevice = mBluetoothDevice;
    }

    public BluetoothSocket getmBluetoothSocket() {
        return mBluetoothSocket;
    }

    public void setmBluetoothSocket(BluetoothSocket mBluetoothSocket) {
        this.mBluetoothSocket = mBluetoothSocket;
    }
}
