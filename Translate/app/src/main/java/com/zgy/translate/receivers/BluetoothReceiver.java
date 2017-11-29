package com.zgy.translate.receivers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zgy.translate.domains.dtos.BluetoothDeviceDTO;
import com.zgy.translate.domains.eventbuses.BluetoothDeviceEB;
import com.zgy.translate.utils.ConfigUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by zhouguangyue on 2017/11/22.
 */

public class BluetoothReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i("action", action);
        if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
            ConfigUtil.showToask(context,"开始搜索");
        }else if(BluetoothDevice.ACTION_FOUND.equals(action)){
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            BluetoothClass deviceClass = intent.getParcelableExtra(BluetoothDevice.EXTRA_CLASS);
            Log.i("其它信息", deviceClass + "");
            if(device.getBondState() == BluetoothDevice.BOND_BONDED){ //绑定过
                Log.i("绑定过device", device.getName() + "---" + device.getAddress());
            }else if(device.getBondState() == BluetoothDevice.BOND_BONDING){ //正在绑定
                Log.i("正在绑定device", device.getName() + "---" + device.getAddress());
            }else if(device.getBondState() == BluetoothDevice.BOND_NONE){ //没有绑定过或者取消绑定
                Log.i("没有绑定过或者取消绑定device", device.getName() + "---" + device.getAddress());
            }
            BluetoothDeviceEB deviceEB = new BluetoothDeviceEB();
            deviceEB.setBluetoothDevice(device);
            EventBus.getDefault().post(deviceEB);
        }else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
            Log.i("搜索完成", "搜索完成");
        }
    }
}
