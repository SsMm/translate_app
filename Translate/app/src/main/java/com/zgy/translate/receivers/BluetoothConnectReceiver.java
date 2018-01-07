package com.zgy.translate.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zgy.translate.receivers.interfaces.BluetoothConnectReceiverInterface;
import com.zgy.translate.services.BluetoothService;


/**
 * Created by zhouguangyue on 2018/1/6.
 */

public class BluetoothConnectReceiver extends BroadcastReceiver {
    private BluetoothConnectReceiverInterface receiverInterface;

    public BluetoothConnectReceiver(BluetoothConnectReceiverInterface receiverInterface){
        this.receiverInterface = receiverInterface;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(BluetoothService.BLUETOOTH_CONNECTED.equals(action)){
            receiverInterface.blueConnected();
        }else if(BluetoothService.BLUETOOTH_DISCONNECTED.equals(action)){
            receiverInterface.blueDisconnected();
        }else if(BluetoothService.BLUETOOTH_INPUTSTEAM.equals(action)){
            String data = intent.getStringExtra(BluetoothService.EXTRA_DATA);
            receiverInterface.getBlueInputStream(data);
        }
    }
}
