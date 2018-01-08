package com.zgy.translate.managers.inst;

import android.bluetooth.BluetoothGatt;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.zgy.translate.global.GlobalParams;
import com.zgy.translate.managers.inst.inter.BluetoothProfileManagerInterface;
import com.zgy.translate.managers.inst.inter.CreateGattManagerInterface;
import com.zgy.translate.receivers.interfaces.BluetoothConnectReceiverInterface;
import com.zgy.translate.services.BluetoothService;

/**
 * Created by zhouguangyue on 2018/1/6.
 */

public class CreateBlueManager implements BluetoothProfileManagerInterface, BluetoothConnectReceiverInterface{

    private Context mContext;
    private CreateGattManagerInterface gattManagerInterface;
    private ComUpdateReceiverManager receiverManager;
    private BluetoothService mBluetoothService;
    private BluetoothProfileManager profileManager;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothService = ((BluetoothService.LocalBinder) service).getService();
            if(GlobalParams.BlUETOOTH_DEVICE != null){
                mBluetoothService.connectThread(GlobalParams.BlUETOOTH_DEVICE);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothService = null;
        }
    };

    public CreateBlueManager(Context context, CreateGattManagerInterface managerInterface){
        mContext = context;
        gattManagerInterface = managerInterface;
    }

    public void init(){

        //初始化服务
        Intent serviceIntent = new Intent(mContext.getApplicationContext(), BluetoothService.class);
        mContext.bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

        receiverManager = new ComUpdateReceiverManager(mContext);
        receiverManager.connectRrgister(this);

        profileManager = new BluetoothProfileManager(mContext, this);
        profileManager.getBluetoothProfile();

    }

    public void nextGetProfile(){
        if(profileManager != null){
            profileManager.getBluetoothProfile();
        }
    }

    public void closeSocket(){
        if(mBluetoothService != null){
            mBluetoothService.closeSocket();
        }
    }

    @Override
    public void bluetoothOff() {
        gattManagerInterface.bluetoothOff();
    }

    @Override
    public void noProfile() {
        gattManagerInterface.noProfile();
    }

    @Override
    public void deviceConning() {
        profileManager.getBluetoothProfile();
    }

    @Override
    public void getA2DPProfileFinish(boolean result) {
        if(result){
            profileManager.closeProfileProxy();
            if(GlobalParams.BlUETOOTH_DEVICE != null){
                if(mBluetoothService == null){
                    Intent serviceIntent = new Intent(mContext.getApplicationContext(), BluetoothService.class);
                    mContext.bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
                    return;
                }
                mBluetoothService.connectThread(GlobalParams.BlUETOOTH_DEVICE);
            }else{
                gattManagerInterface.noRequest();
            }
        }else{
            gattManagerInterface.noRequest();
        }
    }

    @Override
    public void getBLEProfileFinish(BluetoothGatt gatt, boolean result) {

    }



    @Override
    public void blueConnected() {
        gattManagerInterface.conState(true);
    }

    @Override
    public void blueDisconnected() {
        gattManagerInterface.conState(false);
    }

    @Override
    public void getBlueInputStream(String data) {
        if(data != null){
            gattManagerInterface.gattOrder(data);
            Log.i("data---", data);
        }
    }


    public void onMyDestroy(){
        if(receiverManager != null){
            receiverManager.unConnectRegister();
            receiverManager = null;
        }
        if(profileManager != null){
            profileManager.closeProfileProxy();
            profileManager.onMyDestroy();
            profileManager = null;
        }
        if(mBluetoothService != null){
            mBluetoothService.closeSocket();
            mBluetoothService = null;
        }
        mContext.unbindService(mServiceConnection);
        mContext = null;
    }

}
