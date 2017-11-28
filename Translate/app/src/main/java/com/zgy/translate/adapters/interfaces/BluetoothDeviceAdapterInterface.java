package com.zgy.translate.adapters.interfaces;

import com.zgy.translate.domains.dtos.BluetoothDeviceDTO;

/**
 * Created by zhouguangyue on 2017/11/28.
 */

public interface BluetoothDeviceAdapterInterface {

    void bongDevice(BluetoothDeviceDTO deviceDTO, int position);  //绑定蓝牙设备

}
