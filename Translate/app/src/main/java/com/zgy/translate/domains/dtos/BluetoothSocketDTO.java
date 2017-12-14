package com.zgy.translate.domains.dtos;


import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.zgy.translate.activitys.BluetoothDeviceManagerActivity;

/**
 * Created by zhouguangyue on 2017/12/13.
 */

public class BluetoothSocketDTO {

    private BluetoothDevice mBluetoothDevice;
    private BluetoothSocket mBluetoothSocket;
    private BluetoothDeviceManagerActivity.ConnectThread mBluetoothSocketConThread;
    private String state;

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

    public void setmBluetoothSocketConThread(BluetoothDeviceManagerActivity.ConnectThread mBluetoothSocketConThread) {
        this.mBluetoothSocketConThread = mBluetoothSocketConThread;
    }

    public BluetoothDeviceManagerActivity.ConnectThread getmBluetoothSocketConThread() {
        return mBluetoothSocketConThread;
    }
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
