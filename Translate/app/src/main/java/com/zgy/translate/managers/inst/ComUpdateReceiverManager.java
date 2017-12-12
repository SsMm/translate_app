package com.zgy.translate.managers.inst;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.IntentFilter;

import com.zgy.translate.receivers.BluetoothReceiver;
import com.zgy.translate.receivers.interfaces.BluetoothReceiverInterface;

/**
 * Created by zhouguangyue on 2017/12/6.
 */

public class ComUpdateReceiverManager {

    private Context mContext;
    private BluetoothReceiver bluetoothReceiver;

    public ComUpdateReceiverManager(Context context){
        mContext = context;
    }

    public void register(BluetoothReceiverInterface receiverInterface){
        bluetoothReceiver = new BluetoothReceiver(receiverInterface);
        mContext.registerReceiver(bluetoothReceiver, makeGattUpdateIntentFilter());
    }

    public void unRegister(){
        if(bluetoothReceiver != null){
            mContext.unregisterReceiver(bluetoothReceiver);
            bluetoothReceiver = null;
        }
        if(mContext != null){
            mContext = null;
        }
    }


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        //intentFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        return intentFilter;
    }

}
