package com.zgy.translate.activitys;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.zgy.translate.R;
import com.zgy.translate.base.BaseActivity;
import com.zgy.translate.receivers.BluetoothReceiver;
import com.zgy.translate.utils.ConfigUtil;

import java.util.Iterator;
import java.util.Set;

public class DeviceManagerActivity extends BaseActivity {

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothReceiver bluetoothReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_manager);
        super.init();
    }

    @Override
    public void initView() {
        Button start = (Button) findViewById(R.id.start_blue);
        Button stop = (Button) findViewById(R.id.stop_blue);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBluetooth();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopBluetooth();
            }
        });

    }

    @Override
    public void initEvent() {

    }

    @Override
    public void initData() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        registerBlueReceiver();
        checkBluetooth();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cancelBltReceiver();
        cancelDiscovery();
        bluetoothAdapter = null;
    }

    /**注册蓝牙广播*/
    private void registerBlueReceiver(){
        if(bluetoothReceiver != null){
            return;
        }
        bluetoothReceiver = new BluetoothReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(bluetoothReceiver,intentFilter);
        Log.i("注册", "注册蓝牙广播");
    }

    /**判断蓝牙是否已开启*/
    private void checkBluetooth(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null){
            ConfigUtil.showToask(this, "不支持蓝牙功能!");
            return;
        }
        getBondDevice();
        Log.i("mybuluetooname", bluetoothAdapter.getName() + "--" + bluetoothAdapter.getAddress());
        if(bluetoothAdapter.isEnabled()){
            startDiscovery();
        }
    }

    /**开启蓝牙*/
    private void startBluetooth(){
        if(!bluetoothAdapter.isEnabled()){
            registerBlueReceiver();
            bluetoothAdapter.enable();
            Log.i("kaiqi", "开启蓝牙");
            getBondDevice();
            startDiscovery();
        }
    }

    /**开启蓝牙搜索*/
    private void startDiscovery(){
        bluetoothAdapter.startDiscovery();
    }

    /**关闭蓝牙*/
    private void stopBluetooth(){
        if(bluetoothAdapter != null && bluetoothAdapter.isEnabled()){
            cancelBltReceiver();
            cancelDiscovery();
            bluetoothAdapter.disable();
            Log.i("guanbi", "关闭蓝牙");
        }
    }

    /**取消蓝牙搜索*/
    private void cancelDiscovery(){
        if(bluetoothAdapter != null){
            bluetoothAdapter.cancelDiscovery();
        }
    }

    /**取消蓝牙广播注册*/
    private void cancelBltReceiver(){
        if(bluetoothReceiver != null){
            unregisterReceiver(bluetoothReceiver);
            bluetoothReceiver = null;
        }
    }

    /**获取已绑定过得蓝牙信息*/
    private void getBondDevice(){
        if(bluetoothAdapter != null){
            Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
            if(devices.size() > 0){

                for (Iterator<BluetoothDevice> it = devices.iterator() ; it.hasNext() ; ){
                    BluetoothDevice device = it.next();
                    Log.i("已绑定蓝牙", device.getName() + device.getAddress());
                }
            }
        }
    }

}

