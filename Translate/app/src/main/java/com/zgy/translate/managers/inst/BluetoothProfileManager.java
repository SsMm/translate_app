package com.zgy.translate.managers.inst;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothHealth;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import com.zgy.translate.MainActivity;
import com.zgy.translate.adapters.BluetoothBondedDeviceAdapter;
import com.zgy.translate.domains.dtos.BluetoothLeConnectionDTO;
import com.zgy.translate.domains.dtos.BluetoothSocketDTO;
import com.zgy.translate.global.GlobalGattAttributes;
import com.zgy.translate.global.GlobalInit;
import com.zgy.translate.managers.inst.inter.BluetoothProfileManagerInterface;
import com.zgy.translate.utils.ConfigUtil;
import com.zgy.translate.utils.RedirectUtil;

import java.util.ArrayList;

/**
 * Created by zhouguangyue on 2017/12/14.
 */

public class BluetoothProfileManager implements BluetoothProfile.ServiceListener{

    private static final int SOCKET = 0;
    private static final int GATT = 1;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothHeadset mBluetoothHeadset;
    private BluetoothA2dp mBluetoothA2dp;
    private BluetoothHealth mBluetoothHealth;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothProfile mBluetoothProfile;
    private int pro;
    private Context mContext;
    private BluetoothProfileManagerInterface managerInterface;

    public BluetoothProfileManager(Context context,BluetoothProfileManagerInterface managerInterface){
        mContext = context;
        this.managerInterface = managerInterface;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        GlobalInit.askBlueMap.clear();
    }

    /**获取蓝牙是否连接以及连接状态*/

    public boolean getBluetoothProfile(){
        if(GlobalInit.bluetoothSocketDTOList == null){
            GlobalInit.bluetoothSocketDTOList = new ArrayList<>();
        }else{
            GlobalInit.bluetoothSocketDTOList.clear();
        }

        if(GlobalInit.leConnectionDTOList == null){
            GlobalInit.leConnectionDTOList = new ArrayList<>();
        }else {
            GlobalInit.leConnectionDTOList.clear();
        }

        int flag = -1;
        int a2dp = mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP);
        int headset = mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET);
        int health = mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEALTH);
        int gatt = mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.GATT);

        if(BluetoothProfile.STATE_CONNECTED == a2dp){
            flag = a2dp;
        }else if(BluetoothProfile.STATE_CONNECTED == headset){
            flag = headset;
        }else if(BluetoothProfile.STATE_CONNECTED == health){
            flag = health;
        }else if(BluetoothProfile.STATE_CONNECTED == gatt){
            flag = gatt;
        }else if(a2dp == 0 && headset == 0 && health == 0 && gatt == 0){
            flag = 0;
        }

        Log.i("flag--", flag +","+ a2dp +","+ headset +","+ health+","+ gatt);

        if(flag == 0){
            return false;
        }else if(flag != -1){
            mBluetoothAdapter.getProfileProxy(mContext, this, flag);
        }
        return true;
    }

    @Override
    public void onServiceConnected(int profile, BluetoothProfile proxy) {
        Log.i("profile", profile +"");
        if(profile == BluetoothProfile.HEADSET){
            pro = profile;
            mBluetoothProfile = proxy;
            Log.i("HEADSET", "HEADSET");
        }else if(profile == BluetoothProfile.A2DP){
            pro = profile;
            mBluetoothProfile = proxy;
            getConnectionDevice(SOCKET, proxy);

            Log.i("A2DP", "A2DP");
        }else if(profile == BluetoothProfile.HEALTH){
            pro = profile;
            mBluetoothProfile = proxy;
            Log.i("HEALTH", "HEALTH");
        }else if(profile == BluetoothProfile.GATT){
            pro = profile;
            mBluetoothProfile = proxy;
            getConnectionDevice(GATT, proxy);
            Log.i("GATT", "GATT");
        }
    }

    @Override
    public void onServiceDisconnected(int profile) {
        if(profile == BluetoothProfile.HEADSET){
            mBluetoothHeadset = null;
            Log.i("mBluetoothHeadset", "没有连接");
        }else if(profile == BluetoothProfile.A2DP){
            mBluetoothA2dp = null;
            Log.i("A2DP", "没有连接A2DP");
        }else if(profile == BluetoothProfile.HEALTH){
            mBluetoothHealth = null;
            Log.i("HEALTH", "没有连接HEALTH");
        }else if(profile == BluetoothProfile.GATT){
            mBluetoothGatt = null;
            Log.i("GATT", "没有连接GATT");
        }
    }

    private void getConnectionDevice(int flag, BluetoothProfile proxy){
        switch (flag){
            case SOCKET:
                mBluetoothA2dp = (BluetoothA2dp) proxy;
                for (BluetoothDevice device : mBluetoothA2dp.getConnectedDevices()){
                    if(GlobalInit.bluetoothSocketDTOList == null){
                        GlobalInit.bluetoothSocketDTOList = new ArrayList<>();
                    }else{
                        GlobalInit.bluetoothSocketDTOList.clear();
                    }
                    BluetoothSocketDTO socketDTO = new BluetoothSocketDTO();
                    socketDTO.setmBluetoothDevice(device);
                    socketDTO.setState(BluetoothBondedDeviceAdapter.CON_STATE);
                    GlobalInit.bluetoothSocketDTOList.add(socketDTO);
                    GlobalInit.askBlueMap.put(device, true);
                    Log.i("mBluetoothA2dp", device.getName() + device.getAddress());
                }
                managerInterface.getProfileFinish();
                break;
            case GATT:
                mBluetoothGatt = (BluetoothGatt) proxy;
                for (BluetoothDevice device : mBluetoothGatt.getConnectedDevices()){
                    if(GlobalInit.leConnectionDTOList == null){
                        GlobalInit.leConnectionDTOList = new ArrayList<>();
                    }else {
                        GlobalInit.leConnectionDTOList.clear();
                    }
                    BluetoothLeConnectionDTO dto = new BluetoothLeConnectionDTO();
                    dto.setmBluetoothDevice(device);
                    GlobalInit.leConnectionDTOList.add(dto);
                    GlobalInit.askBlueMap.put(device, true);
                    Log.i("mBluetoothGatt", device.getName() + device.getAddress());
                }
                managerInterface.getProfileFinish();
                break;
        }

    }

    public void onMyDestroy(){

        mBluetoothAdapter.closeProfileProxy(pro, mBluetoothProfile);
        mBluetoothAdapter = null;
    }
}