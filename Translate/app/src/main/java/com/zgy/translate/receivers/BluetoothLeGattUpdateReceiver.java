package com.zgy.translate.receivers;

import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zgy.translate.services.BluetoothLeService;

import java.util.List;

/**
 * Created by zhouguangyue on 2017/12/1.
 */

public class BluetoothLeGattUpdateReceiver extends BroadcastReceiver{

    private final BluetoothLeService mBluetoothLeService;

    public BluetoothLeGattUpdateReceiver(BluetoothLeService mBluetoothLeService){
        this.mBluetoothLeService = mBluetoothLeService;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action){
            case BluetoothLeService.ACTION_GATT_CONNECTED:
                Log.i("LeGattUpdateReceiver", "连接成功");
                break;
            case BluetoothLeService.ACTION_GATT_DISCONNECTED:
                Log.i("LeGattUpdateReceiver", "连接失败");
                break;
            case BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED:
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
                break;
            case BluetoothLeService.ACTION_DATA_AVAILABLE:
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                break;
        }

    }

    private void displayGattServices(List<BluetoothGattService> gattServices){
        if(gattServices == null){
            return;
        }
        for (BluetoothGattService gattService : gattServices){
            Log.i("gattService", gattService.getUuid()+"");
        }
    }

    private void displayData(String data){
        if(data != null){
            Log.i("data---", data);
        }
    }

}
