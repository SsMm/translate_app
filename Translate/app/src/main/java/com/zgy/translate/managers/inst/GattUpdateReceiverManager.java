package com.zgy.translate.managers.inst;

import android.content.Context;
import android.content.IntentFilter;

import com.zgy.translate.receivers.BluetoothLeGattUpdateReceiver;
import com.zgy.translate.receivers.interfaces.BluetoothLeGattUpdateReceiverInterface;
import com.zgy.translate.services.BluetoothLeService;

/**
 * Created by zhouguangyue on 2017/12/6.
 */

public class GattUpdateReceiverManager {

    private Context mContext;
    private BluetoothLeGattUpdateReceiver gattUpdateReceiver;

    public GattUpdateReceiverManager(Context context){
        mContext = context;
    }

    public void register(BluetoothLeGattUpdateReceiverInterface receiverInterface){
        gattUpdateReceiver = new BluetoothLeGattUpdateReceiver(receiverInterface);
        mContext.registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    public void unRegister(){
        if(gattUpdateReceiver != null){
            mContext.unregisterReceiver(gattUpdateReceiver);
            gattUpdateReceiver = null;
        }
        if(mContext != null){
            mContext = null;
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}
