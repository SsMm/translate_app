package com.zgy.translate.domains.dtos;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

/**
 * Created by zhouguangyue on 2017/12/13.
 */

public class BluetoothBondedDeviceDTO{

    private BluetoothDevice device;
    private BluetoothSocket mBluetoothSocket;
    private String state;

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public BluetoothDevice getDevice() {
        return device;
    }
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setmBluetoothSocket(BluetoothSocket mBluetoothSocket) {
        this.mBluetoothSocket = mBluetoothSocket;
    }

    public BluetoothSocket getmBluetoothSocket() {
        return mBluetoothSocket;
    }

}
