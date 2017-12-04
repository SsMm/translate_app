package com.zgy.translate.receivers;

import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


import com.zgy.translate.receivers.interfaces.BluetoothLeGattUpdateReceiverInterface;
import com.zgy.translate.services.BluetoothLeService;

import java.util.List;

/**
 * Created by zhouguangyue on 2017/12/1.
 */

public class BluetoothLeGattUpdateReceiver extends BroadcastReceiver{

    private final BluetoothLeGattUpdateReceiverInterface receiverInterface;

    public BluetoothLeGattUpdateReceiver(BluetoothLeGattUpdateReceiverInterface receiverInterface){
        this.receiverInterface = receiverInterface;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action){
            case BluetoothLeService.ACTION_GATT_CONNECTED:
                receiverInterface.gattConnected();
                break;
            case BluetoothLeService.ACTION_GATT_DISCONNECTED:
                receiverInterface.gattDisconnected();
                break;
            case BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED:
                receiverInterface.gattServicesDiscovered();
                break;
            case BluetoothLeService.ACTION_DATA_AVAILABLE:
                receiverInterface.gattDataAvailable(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                break;
        }

    }



}
