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
import com.zgy.translate.global.GlobalStateCode;
import com.zgy.translate.receivers.interfaces.BluetoothReceiverInterface;
import com.zgy.translate.utils.ClsUtils;
import com.zgy.translate.utils.ConfigUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by zhouguangyue on 2017/11/22.
 */

public class BluetoothReceiver extends BroadcastReceiver {

    private BluetoothReceiverInterface receiverInterface;

    public BluetoothReceiver(BluetoothReceiverInterface receiverInterface){
        this.receiverInterface = receiverInterface;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        BluetoothDevice device;
        String pin = "1234"; //或是0000

        Log.i("action", action);
        if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
            ConfigUtil.showToask(context,"开始搜索");
        }else if(BluetoothDevice.ACTION_FOUND.equals(action)){
            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if(device.getBondState() == BluetoothDevice.BOND_BONDED){ //绑定过
                Log.i("绑定过device", device.getName() + "---" + device.getAddress());
            }else if(device.getBondState() == BluetoothDevice.BOND_BONDING){ //正在绑定
                Log.i("正在绑定device", device.getName() + "---" + device.getAddress());
            }else if(device.getBondState() == BluetoothDevice.BOND_NONE){ //没有绑定过或者取消绑定
                Log.i("没有绑定过或者取消绑定device", device.getName() + "---" + device.getAddress());
            }
            receiverInterface.receiverDevice(device);
        }else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
            ConfigUtil.showToask(context, "搜索完成");
        }else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){
            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            int status = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, 0);
            switch (status){
                case BluetoothDevice.BOND_BONDED:
                    receiverInterface.receiverDeviceState(GlobalStateCode.BONDED, device);
                    break;
                case BluetoothDevice.BOND_BONDING:
                    receiverInterface.receiverDeviceState(GlobalStateCode.BONDING, device);
                    break;
                case BluetoothDevice.BOND_NONE:
                    receiverInterface.receiverDeviceState(GlobalStateCode.BONDNONE, device);
                    break;
            }
        }else if(BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)){
            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            try {
                ClsUtils.setPairingConfirmation(device.getClass(), device, true);
                abortBroadcast();
                boolean ret = ClsUtils.setPin(device.getClass(), device, pin);
                receiverInterface.receiverDevicePinState(ret, device);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
