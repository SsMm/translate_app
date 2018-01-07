package com.zgy.translate.services;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.zgy.translate.activitys.BluetoothDeviceManagerActivity;
import com.zgy.translate.managers.sing.BluetoothConedThread;
import com.zgy.translate.utils.ByteTransform;
import com.zgy.translate.utils.ConfigUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class BluetoothService extends Service {

    public static final String BLUETOOTH_CONNECTED = "com.zgy.translate.blue.connected"; //连接成功
    public static final String BLUETOOTH_DISCONNECTED = "com.zgy.translate.blue.disconnected"; //连接失败
    public static final String BLUETOOTH_INPUTSTEAM = "com.zgy.translate.blue.inputStream"; //获取输入流
    public final static String EXTRA_DATA = "com.zgy.translate.blue.EXTRA_DATA";

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private final IBinder mBinder = new BluetoothService.LocalBinder();

    private volatile BluetoothSocket socket;
    private volatile InputStream is;

    public BluetoothService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    public synchronized void connectThread(final BluetoothDevice device){
        try {
            socket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    if(socket.isConnected()){
                        is = socket.getInputStream();
                        broadcastUpdate(BLUETOOTH_CONNECTED);
                        broadcastUpdate(BLUETOOTH_INPUTSTEAM, is);
                        return;
                    }

                    socket.connect();
                    broadcastUpdate(BLUETOOTH_CONNECTED);

                    is = socket.getInputStream();
                    broadcastUpdate(BLUETOOTH_INPUTSTEAM, is);
                }catch (Exception e) {
                    e.printStackTrace();
                    broadcastUpdate(BLUETOOTH_DISCONNECTED);
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void broadcastUpdate(String action){
        final Intent intent = new Intent(action);
        sendBroadcast(intent);

    }

    private void broadcastUpdate(String action, InputStream is){
        final Intent intent = new Intent(action);

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
                    intent.putExtra(EXTRA_DATA, result);
                    sendBroadcast(intent);
                }
            } catch (IOException e) {
                e.printStackTrace();
                if(e.getMessage().contains("closed")){
                    ConfigUtil.showToask(BluetoothService.this, "请检查蓝牙耳机是否开启");
                    break;
                }
            }
        }
    }


    public class LocalBinder extends Binder {
        public BluetoothService getService(){
            return BluetoothService.this;
        }
    }
}
