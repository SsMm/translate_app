package com.zgy.translate.domains.dtos;

import android.bluetooth.BluetoothDevice;
import android.content.ServiceConnection;

import com.zgy.translate.services.BluetoothLeService;

/**
 * Created by zhouguangyue on 2017/12/13.
 */

public class BluetoothLeConnectionDTO {

    private BluetoothDevice mBluetoothDevice;
    private BluetoothLeService mBluetoothLeService;
    private ServiceConnection mServiceConnection;

    public BluetoothDevice getmBluetoothDevice() {
        return mBluetoothDevice;
    }

    public void setmBluetoothDevice(BluetoothDevice mBluetoothDevice) {
        this.mBluetoothDevice = mBluetoothDevice;
    }

    public BluetoothLeService getmBluetoothLeService() {
        return mBluetoothLeService;
    }

    public void setmBluetoothLeService(BluetoothLeService mBluetoothLeService) {
        this.mBluetoothLeService = mBluetoothLeService;
    }

    public ServiceConnection getmServiceConnection() {
        return mServiceConnection;
    }

    public void setmServiceConnection(ServiceConnection mServiceConnection) {
        this.mServiceConnection = mServiceConnection;
    }
}
