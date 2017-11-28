package com.zgy.translate.domains.dtos;

/**
 * Created by zhouguangyue on 2017/11/28.
 */

public class BluetoothDeviceDTO {
    private String device_name;  //设备名称
    private String device_address;  //设备mac地址


    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getDevice_address() {
        return device_address;
    }

    public void setDevice_address(String device_address) {
        this.device_address = device_address;
    }
}
