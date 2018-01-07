package com.zgy.translate.managers.sing;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by zhouguangyue on 2018/1/6.
 */

public class BluetoothConedThread {

    public static final String BLUETOOTH_CONNECTED = "com.zgy.translate.blue.connected"; //连接成功
    public static final String BLUETOOTH_DISCONNECTED = "com.zgy.translate.blue.disconnected"; //连接失败
    public static final String BLUETOOTH_INPUTSTEAM = "com.zgy.translate.blue.inputStream"; //获取输入流

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static BluetoothConedThread bluetoothConedThread;
    private static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private volatile BluetoothSocket socket;
    private volatile InputStream is;
    private BluetoothConedThread(){}

    public static BluetoothConedThread getInstance() {
        if(bluetoothConedThread == null){
            synchronized (BluetoothConedThread.class){
                if(bluetoothConedThread == null){
                    bluetoothConedThread = new BluetoothConedThread();
                }
            }
        }
        return bluetoothConedThread;
    }

    public void connectThread(BluetoothDevice device){
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
                        return;
                    }

                    socket.connect();
                    is = socket.getInputStream();

                }catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }


    private void broadcastUpdate(String action){

    }

    private void broadcastUpdate(String action, InputStream is){

    }


}
