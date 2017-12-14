package com.zgy.translate.adapters.interfaces;

import android.bluetooth.BluetoothDevice;
import android.view.View;
import android.widget.TextView;

import com.zgy.translate.adapters.BluetoothDeviceAdapter;
import com.zgy.translate.domains.dtos.BluetoothDeviceDTO;

/**
 * Created by zhouguangyue on 2017/11/28.
 */

public interface BluetoothDeviceAdapterInterface {

    void goBondedAndConDevice(BluetoothDevice device, int position, TextView view);  //绑定蓝牙设备

}
