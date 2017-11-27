package com.zgy.translate.receivers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by zhouguangyue on 2017/11/22.
 */

public class BluetoothReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i("action", action);
        if(BluetoothDevice.ACTION_FOUND.equals(action)){
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if(device.getBondState() != BluetoothDevice.BOND_BONDED){ //绑定过
                Log.i("device", device.getName() + "---" + device.getAddress());
            }else if(device.getBondState() != BluetoothDevice.BOND_BONDING){ //正在绑定
                Log.i("device", device.getName() + "---" + device.getAddress());
            }else if(device.getBondState() != BluetoothDevice.BOND_NONE){ //没有绑定过或者取消绑定
                Log.i("device", device.getName() + "---" + device.getAddress());
            }
        }else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
            Log.i("搜索完成", "搜索完成");
        }
    }
}
