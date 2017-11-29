package com.zgy.translate.activitys;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
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
import com.zgy.translate.global.GlobalUUID;
import com.zgy.translate.receivers.BluetoothReceiver;
import com.zgy.translate.receivers.interfaces.BluetoothReceiverInterface;
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

public class DeviceManagerActivity extends BaseActivity implements BluetoothDeviceAdapterInterface,
        BluetoothReceiverInterface{

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final int REQUEST_ENABLE_BT = 1;  //请求开启蓝牙

    @BindView(R.id.adm_rv_deviceList) RecyclerView deviceRv;
    @BindView(R.id.adm_tv_deviceBonded) TextView deviceBondedTv;  //已绑定过得蓝牙设备


    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothReceiver mBluetoothReceiver = null;
    private ConnectThread mConnectThread;
    private GetInputStreamThread mGetInputStreamThread;

    private BluetoothDeviceAdapter mBluetoothDeviceAdapter;  //搜索到设备
    private List<BluetoothDeviceDTO> deviceDTOList;
    private List<BluetoothDevice> deviceEBList;  //存放搜索到的蓝牙设备
    private int devicePosition;  //选择蓝牙设备坐标



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
        mBluetoothAdapter = null;
    }

    /**注册蓝牙广播*/
    private void registerBlueReceiver(){
        if(mBluetoothReceiver != null){
            return;
        }
        mBluetoothReceiver = new BluetoothReceiver(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mBluetoothReceiver,intentFilter);
        Log.i("注册", "注册蓝牙广播");
    }

    /**判断蓝牙是否已开启*/
    private void checkBluetooth(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null){
            ConfigUtil.showToask(this, "不支持蓝牙功能!");
            return;
        }
        Log.i("mybuluetooname", mBluetoothAdapter.getName() + "--" + mBluetoothAdapter.getAddress());
        if(mBluetoothAdapter.isEnabled()){
            getBondDevice();
            startDiscovery();
        }
    }

    /**开启蓝牙*/
    private void startBluetooth(){
        if(mBluetoothAdapter == null){
            ConfigUtil.showToask(this, "不支持蓝牙功能!");
            return;
        }
        if(!mBluetoothAdapter.isEnabled()){
            //bluetoothAdapter.enable();  //弹出蓝牙开启确认框
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_ENABLE_BT){
            if(resultCode == RESULT_OK){
                Log.i("kaiqi", "开启蓝牙");
                getBondDevice();
                startDiscovery();
            }else{
                ConfigUtil.showToask(this, "蓝牙开启失败，重新开启");
            }
        }
    }

    /**开启蓝牙搜索*/
    private void startDiscovery(){
        if(deviceDTOList != null && deviceDTOList.size() != 0){
            deviceDTOList.clear();
            deviceEBList.clear();
        }
        if(mBluetoothAdapter != null){
            registerBlueReceiver();
            mBluetoothAdapter.startDiscovery();
        }
    }

    /**关闭蓝牙*/
    private void stopBluetooth(){
        if(mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()){
            cancelDiscovery();
            mBluetoothAdapter.disable();
            Log.i("guanbi", "关闭蓝牙");
        }
    }

    /**取消蓝牙搜索*/
    private void cancelDiscovery(){
        if(mBluetoothAdapter != null){
            cancelBltReceiver();
            mBluetoothAdapter.cancelDiscovery();
        }
    }

    /**取消蓝牙广播注册*/
    private void cancelBltReceiver(){
        if(mBluetoothReceiver != null){
            unregisterReceiver(mBluetoothReceiver);
            mBluetoothReceiver = null;
        }
    }

    /**获取已绑定过得蓝牙信息*/
    private void getBondDevice(){
        if(mBluetoothAdapter != null){
            Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
            if(devices.size() > 0){
                for (BluetoothDevice device : devices){
                    autoConnectDevice(device);
                    Log.i("已绑定蓝牙", device.getName() + device.getAddress());
                }
            }
        }
    }

    /**重新搜索*/
    @OnClick(R.id.refresh) void refreshDiscovery(){
        startDiscovery();
    }

    /**初始化搜索到设备列表*/
    private void initDeviceAdapter(){
        deviceDTOList = new ArrayList<>();
        deviceEBList = new ArrayList<>();
        deviceRv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mBluetoothDeviceAdapter = new BluetoothDeviceAdapter(this, deviceDTOList, this);
        deviceRv.setAdapter(mBluetoothDeviceAdapter);
    }

    @Override
    public void receiverDevice(BluetoothDevice device) {
        deviceEBList.add(device);
        BluetoothDeviceDTO deviceDTO = new BluetoothDeviceDTO();
        deviceDTO.setDevice_name(device.getName());
        deviceDTO.setDevice_address(device.getAddress());
        deviceDTOList.add(deviceDTO);
        mBluetoothDeviceAdapter.notifyItemInserted(deviceDTOList.size() - 1);
    }

    @Override
    public void bongDevice(BluetoothDeviceDTO deviceDTO, int position) {
        Log.i("选择蓝牙设备", position + deviceDTO.getDevice_name() + deviceDTO.getDevice_address());
        deviceEBList.remove(position);
        mBluetoothDeviceAdapter.notifyItemRemoved(position);
        autoConnectDevice(deviceEBList.get(position));
    }

    /**显示当前连接设备*/
    private void autoConnectDevice(BluetoothDevice device){
        if(device.getName().isEmpty()){
            deviceBondedTv.setText(device.getAddress());
        }else{
            deviceBondedTv.setText(device.getName());
        }
        connect(device);
    }

    /**进行蓝牙耳机连接*/
    private synchronized void connect(BluetoothDevice device){
        cancelDiscovery();
        if(mConnectThread != null){
            mConnectThread.cancel();
            mConnectThread = null;
        }

        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
    }

    /**与蓝牙耳机建立连接*/
    private class ConnectThread extends Thread{
        private final BluetoothSocket mSocket;

        public ConnectThread(BluetoothDevice device){
            BluetoothSocket socket = null;
            try {
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);

            } catch (IOException e) {
                e.printStackTrace();
            }
            mSocket = socket;
        }

        @Override
        public void run() {
            BluetoothConnectEB connectEB = new BluetoothConnectEB();
            try {
                if(mSocket.isConnected()){
                    Log.i("蓝牙已连接", "蓝牙已连接");
                    return;
                }
                mSocket.connect();
                connectEB.setFlag(true);
            } catch (IOException e) {
                e.printStackTrace();
                if(e.getMessage().contains("closed") || e.getMessage().contains("timeout")){
                    Log.i("连接失败日志", "连接关闭或超时，重新连接");
                }else{
                    Log.i("连接失败日志", "连接失败，重新连接");
                }
                try {
                    mSocket.close();
                    connectEB.setFlag(false);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            connected(mSocket);

            EventBus.getDefault().post(connectEB);
        }

        public void cancel(){
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**连接成功监听蓝牙耳机输入流*/
    private synchronized void connected(BluetoothSocket socket){
        if(mConnectThread != null){
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if(mGetInputStreamThread != null){
            mGetInputStreamThread.cancel();
            mGetInputStreamThread = null;
        }

        mGetInputStreamThread = new GetInputStreamThread(socket);
        mGetInputStreamThread.start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void returnConnectResult(BluetoothConnectEB connectEB){
        if(connectEB.isFlag()){
            Log.i("连接成功", "连接成功");
        }else{
            Log.i("连接失败", "连接失败");
        }
    }


    /**获取蓝牙输入流线程*/
    private class GetInputStreamThread extends Thread{
        private final BluetoothSocket mSocket;
        private final InputStream mInputStream;

        private GetInputStreamThread(BluetoothSocket socket){
            mSocket = socket;
            InputStream is = null;

            try {
                is = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mInputStream = is;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            while (true){
                try {
                    bytes = mInputStream.read(buffer);
                    Log.i("bytes--", bytes + "");
                } catch (IOException e) {
                    e.printStackTrace();
                    if(e.getMessage().contains("closed")){
                        Log.i("耳机关闭", "请检查蓝牙耳机是否开启");
                        break;
                    }
                }
            }
        }

        public void cancel(){
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void getBluetoothInputStream(){
        if(GlobalSingleThread.bltInputStreamExecutorService == null){
            GlobalSingleThread.bltInputStreamExecutorService = Executors.newSingleThreadScheduledExecutor();
        }

        GlobalSingleThread.bltInputStreamExecutorService.submit(new Runnable() {
            @Override
            public void run() {
               /* while (mBluetoothSocket != null){
                    byte[] buff = new byte[1024];
                    int bytes;
                    InputStream inputStream;
                    try {
                        inputStream = mBluetoothSocket.getInputStream();
                        bytes = inputStream.read(buff);
                        Log.i("bytes", bytes + "");
                        processBuffer(buff, 1024);
                    } catch (IOException e) {
                        e.printStackTrace();
                        try {
                            mBluetoothSocket.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }

                }*/
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

