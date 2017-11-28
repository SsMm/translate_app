package com.zgy.translate.activitys;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.IntentFilter;
import android.graphics.LinearGradient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zgy.translate.R;
import com.zgy.translate.adapters.BluetoothDeviceAdapter;
import com.zgy.translate.adapters.interfaces.BluetoothDeviceAdapterInterface;
import com.zgy.translate.base.BaseActivity;
import com.zgy.translate.domains.dtos.BluetoothDeviceDTO;
import com.zgy.translate.domains.eventbuses.BluetoothConnectEB;
import com.zgy.translate.domains.eventbuses.BluetoothDeviceEB;
import com.zgy.translate.global.GlobalSingleThread;
import com.zgy.translate.receivers.BluetoothReceiver;
import com.zgy.translate.utils.ConfigUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeviceManagerActivity extends BaseActivity implements BluetoothDeviceAdapterInterface{
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    @BindView(R.id.adm_rv_deviceList) RecyclerView deviceRv;
    @BindView(R.id.adm_tv_deviceBonded) TextView deviceBondedTv;  //已绑定过得蓝牙设备


    private BluetoothAdapter bluetoothAdapter;
    private BluetoothReceiver bluetoothReceiver = null;
    private BluetoothDeviceAdapter mBluetoothDeviceAdapter;  //搜索到设备
    private List<BluetoothDeviceDTO> deviceDTOList;
    private List<BluetoothDeviceEB> deviceEBList;  //存放搜索到的蓝牙设备

    private BluetoothSocket bluetoothSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_manager);
        ButterKnife.bind(this);
        super.init();
    }

    @Override
    public void initView() {
        initDeviceAdapter();

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
        EventBus.getDefault().register(this);
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
        if(deviceDTOList != null && deviceDTOList.size() != 0){
            deviceDTOList.clear();
            deviceEBList.clear();
        }
        if(bluetoothAdapter != null){
            bluetoothAdapter.startDiscovery();
        }
    }

    /**关闭蓝牙*/
    private void stopBluetooth(){
        if(bluetoothAdapter != null && bluetoothAdapter.isEnabled()){
            cancelBltReceiver();
            cancelDiscovery();
            bluetoothAdapter.disable();
            Log.i("guanbi", "关闭蓝牙");
            if(bluetoothSocket != null){
                try {
                    bluetoothSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(GlobalSingleThread.bltInputStreamExecutorService != null){
                GlobalSingleThread.bltInputStreamExecutorService.shutdown();
            }
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
                    if(device.getName().isEmpty()){
                        deviceBondedTv.setText(device.getAddress());
                    }else{
                        deviceBondedTv.setText(device.getName());
                    }
                    createConnect(device);
                    Log.i("已绑定蓝牙", device.getName() + device.getAddress());
                }
            }
        }
    }

    /**重新搜索*/
    @OnClick(R.id.refresh) void refreshDiscovery(){
        startDiscovery();
    }

    private void initDeviceAdapter(){
        deviceDTOList = new ArrayList<BluetoothDeviceDTO>();
        deviceEBList = new ArrayList<>();
        deviceRv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mBluetoothDeviceAdapter = new BluetoothDeviceAdapter(this, deviceDTOList, this);
        deviceRv.setAdapter(mBluetoothDeviceAdapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void discoveryDevice(BluetoothDeviceEB deviceEB){
        deviceEBList.add(deviceEB);
        BluetoothDeviceDTO deviceDTO = new BluetoothDeviceDTO();
        deviceDTO.setDevice_name(deviceEB.getBluetoothDevice().getName());
        deviceDTO.setDevice_address(deviceEB.getBluetoothDevice().getAddress());
        deviceDTOList.add(deviceDTO);
        mBluetoothDeviceAdapter.notifyItemRangeChanged(0,deviceDTOList.size() - 1);
    }

    @Override
    public void bongDevice(BluetoothDeviceDTO deviceDTO, int position) {
        Log.i("选择蓝牙设备", position + deviceDTO.getDevice_name() + deviceDTO.getDevice_address());
    }


    /**与蓝牙耳机建立连接*/
    private void createConnect(BluetoothDevice device){
        GlobalSingleThread.bltConnectExecutorService = Executors.newSingleThreadScheduledExecutor();
        GlobalSingleThread.bltConnectExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                BluetoothConnectEB connectEB = new BluetoothConnectEB();
                try {
                    bluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                    if(bluetoothSocket.isConnected()){
                        Log.i("蓝牙已连接", "蓝牙已连接");
                        return;
                    }
                    bluetoothSocket.connect();
                    connectEB.setFlag(true);
                } catch (IOException e) {
                    e.printStackTrace();
                    if(e.getMessage().contains("closed") || e.getMessage().contains("timeout")){
                        Log.i("连接失败日志", "连接关闭或超时，重新连接");
                    }
                    try {
                        bluetoothSocket.close();
                        connectEB.setFlag(false);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                EventBus.getDefault().post(connectEB);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void returnConnectResult(BluetoothConnectEB connectEB){
        cancelDiscovery();
        if(connectEB.isFlag()){
            Log.i("连接成功", "连接成功");
            if(GlobalSingleThread.bltConnectExecutorService != null){
                GlobalSingleThread.bltConnectExecutorService.shutdown();
            }
            getBluetoothInputStream();
        }else{
            Log.i("连接失败", "连接失败");
        }
    }

    private void getBluetoothInputStream(){
        if(GlobalSingleThread.bltInputStreamExecutorService == null){
            GlobalSingleThread.bltInputStreamExecutorService = Executors.newSingleThreadScheduledExecutor();
        }

        GlobalSingleThread.bltInputStreamExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                while (bluetoothSocket != null){
                    byte[] buff = new byte[1024];
                    InputStream inputStream;
                    try {
                        inputStream = bluetoothSocket.getInputStream();
                        inputStream.read(buff);
                        processBuffer(buff, 1024);
                    } catch (IOException e) {
                        e.printStackTrace();
                        try {
                            bluetoothSocket.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }

                }
            }
        });
    }

    private void processBuffer(byte[] buff, int size){
        int length = 0;
        for (int i = 0 ; i < size ; i++){
            if(buff[i] > '\0'){
                length++;
            }else{
                break;
            }
        }

        byte[] newBuff = new byte[length];
        for (int j = 0 ; j < length ; j++){
            newBuff[j] = buff[j];
        }

        Log.i("蓝牙耳机输入字符串", new String(newBuff));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if(deviceDTOList != null && deviceDTOList.size() != 0){
            deviceDTOList.clear();
            deviceDTOList = null;
            deviceEBList.clear();
            deviceEBList = null;
        }
        if(mBluetoothDeviceAdapter != null){
            mBluetoothDeviceAdapter = null;
            deviceRv.setAdapter(null);
            deviceRv.setLayoutManager(null);
        }
    }
}

