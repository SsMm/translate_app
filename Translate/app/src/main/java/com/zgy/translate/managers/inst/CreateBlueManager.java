package com.zgy.translate.managers.inst;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothSocket;
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
import com.zgy.translate.utils.ConfigUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by zhouguangyue on 2018/1/6.
 */

public class CreateBlueManager implements BluetoothProfileManagerInterface{

    private Context mContext;
    private CreateGattManagerInterface gattManagerInterface;
    private BluetoothProfileManager profileManager;
    private volatile BluetoothSocket socket;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ScheduledExecutorService executorService;

    public CreateBlueManager(Context context, CreateGattManagerInterface managerInterface){
        mContext = context;
        gattManagerInterface = managerInterface;
    }

    public void init(){

        profileManager = new BluetoothProfileManager(mContext, this);
        profileManager.getBluetoothProfile();

    }

    public void nextGetProfile(){
        if(profileManager != null){
            profileManager.getBluetoothProfile();
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
                connectThread(GlobalParams.BlUETOOTH_DEVICE);
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

    private synchronized void connectThread(final BluetoothDevice device){
        createThread();
        try {
            socket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                InputStream is;
                try {
                    if(socket.isConnected()){
                        is = socket.getInputStream();
                        broadcastUpdate(is);
                        return;
                    }

                    socket.connect();
                    blueConnected();
                    is = socket.getInputStream();
                    broadcastUpdate(is);
                }catch (Exception e) {
                    e.printStackTrace();
                    blueDisconnected();
                   /* if(e.getMessage().contains("closed") || e.getMessage().contains("timeout")){
                        ConfigUtil.showToask(BluetoothService.this, "连接失败");
                    }else{
                        ConfigUtil.showToask(BluetoothService.this, "连接失败");
                    }*/
                    closeSocket();
                }

            }
        });
    }


    public void closeSocket(){
        if(socket != null){
            try {
                socket.close();
                socket = null;
                if(executorService != null){
                    executorService.shutdown();
                    executorService = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void broadcastUpdate(InputStream is){

        byte[] buffer = new byte[1024];
        int bytes;
        while (true){
            try {
                int bytesAvailable = is.available();
                if(bytesAvailable > 0){
                    bytes = is.read(buffer);
                    String result = null;
                    try {
                        //result = ByteTransform.bytes2String(buffer);
                        result = new String(buffer);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.i("bytes--", bytes + "");
                    Log.i("result---", result);
                    getBlueInputStream(result);
                }
            } catch (IOException e) {
                e.printStackTrace();
                if(e.getMessage().contains("closed")){
                    ConfigUtil.showToask(mContext, "请检查蓝牙耳机是否开启");
                    break;
                }
            }
        }
    }



    private void blueConnected() {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gattManagerInterface.conState(true);
            }
        });
    }


    private void blueDisconnected() {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gattManagerInterface.conState(false);
            }
        });
    }


    private void getBlueInputStream(String data) {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(data != null){
                    gattManagerInterface.gattOrder(data);
                    Log.i("data---", data);
                }
            }
        });
    }


    public void onMyDestroy(){
        if(profileManager != null){
            profileManager.closeProfileProxy();
            profileManager.onMyDestroy();
            profileManager = null;
        }
        if(executorService != null){
            executorService.shutdown();
            executorService = null;
        }
        mContext = null;
    }


    private void createThread(){
        if(executorService != null){
            executorService.shutdown();
            executorService = null;
        }
        executorService = Executors.newSingleThreadScheduledExecutor();
    }

}
