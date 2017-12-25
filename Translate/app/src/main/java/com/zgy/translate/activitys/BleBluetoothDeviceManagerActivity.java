package com.zgy.translate.activitys;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.print.PageRange;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.zgy.translate.global.GlobalConstants;
import com.zgy.translate.global.GlobalGattAttributes;
import com.zgy.translate.global.GlobalInit;
import com.zgy.translate.managers.inst.GattUpdateReceiverManager;
import com.zgy.translate.receivers.BluetoothLeGattUpdateReceiver;
import com.zgy.translate.receivers.interfaces.BluetoothLeGattUpdateReceiverInterface;
import com.zgy.translate.services.BluetoothLeService;
import com.zgy.translate.utils.ConfigUtil;
import com.zgy.translate.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BleBluetoothDeviceManagerActivity extends BaseActivity implements BluetoothDeviceAdapterInterface,
        BluetoothLeGattUpdateReceiverInterface{

    @BindView(R.id.abldm_tv_deviceBonded) TextView tv_deviceBonded; //绑定设备
    @BindView(R.id.abldm_tv_deviceBondState) TextView tv_deviceBondedState; //绑定状态
    @BindView(R.id.abldm_rv_deviceList) RecyclerView rv_scanDeviceList; //搜索到设备列表

    private final static String TAG = BleBluetoothDeviceManagerActivity.class.getSimpleName();
    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 10000;

    private BluetoothAdapter mBluetoothAdapter;

    private boolean mScanning;
    private ScheduledExecutorService autoCloseScanExecutorService;
    private List<BluetoothDevice> mBluetoothDeviceList;
    private BluetoothDeviceAdapter mBluetoothDeviceAdapter;
    private BluetoothLeService mBluetoothLeService;
    private String mDeviceAddress;
    private BluetoothLeGattUpdateReceiver mGattUpdateReceiver;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private GattUpdateReceiverManager gattUpdateReceiverManager;


    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if(!mBluetoothLeService.initialize()){
                Log.i(TAG, "Unable to initialize Bluetooth");
                finish();
            }

            //mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothLeService = null;

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_bluetooth_device_manager);
        ButterKnife.bind(this);
        super.init();
    }

    @Override
    public void initView() {
        baseInit();
    }

    @Override
    public void initEvent() {

    }

    @Override
    public void initData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**开启蓝牙*/
    @OnClick(R.id.abldm_start_blue) void startBle(){
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, REQUEST_ENABLE_BT);
    }

    /**关闭蓝牙*/
    @OnClick(R.id.abldm_stop_blue) void stopBle(){
        if(mBluetoothAdapter != null){
            mBluetoothAdapter.disable();
        }
        if(mBluetoothDeviceList != null && mBluetoothDeviceList.size() != 0){
            mBluetoothDeviceList.clear();
            mBluetoothDeviceAdapter.notifyDataSetChanged();
        }
    }

    /**重新搜索*/
    @OnClick(R.id.abldm_refresh) void refreshBle(){
        isHave = false;
        if(mBluetoothDeviceList != null && mBluetoothDeviceList.size() != 0){
            mBluetoothDeviceList.clear();
            mBluetoothDeviceAdapter.notifyDataSetChanged();
        }
        scanLeDevice(true);
    }

    @Override
    public void goBondedAndConDevice(BluetoothDevice device, int position, TextView view) {
        mDeviceAddress = device.getAddress();
        if (mBluetoothLeService != null) {
            if(mScanning){
                scanLeDevice(false);
            }
            ConfigUtil.showToask(this, "开始连接...");
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.i(TAG, "Connect request result=" + result);
        }
    }

    /**初始化参数*/
    private void baseInit(){

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if(mBluetoothAdapter == null){
            ConfigUtil.showToask(this, GlobalConstants.NO_BLUETOOTH);
            finish();
        }

        //初始化设备列表
        mBluetoothDeviceList = new ArrayList<>();
        mBluetoothDeviceAdapter = new BluetoothDeviceAdapter(this, mBluetoothDeviceList, this);
        rv_scanDeviceList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rv_scanDeviceList.setAdapter(mBluetoothDeviceAdapter);

        //定时线程
        autoCloseScanExecutorService = Executors.newSingleThreadScheduledExecutor();

        //初始化服务
        Intent gattServiceIntent = new Intent(getApplicationContext(), BluetoothLeService.class);
        getApplicationContext().bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        //注册
        gattUpdateReceiverManager = new GattUpdateReceiverManager(this);
        gattUpdateReceiverManager.register(this);

        //获取已绑定设备
        getLeBondedDevice();

        //检测蓝牙是否开启
        if(mBluetoothAdapter.isEnabled()){
            scanLeDevice(true);
        }

    }

    /**得到已绑定设备*/
    private void getLeBondedDevice(){
        if(mBluetoothAdapter != null){
            Log.i("mybluetooth--", mBluetoothAdapter.getName() + mBluetoothAdapter.getAddress());
            for (BluetoothDevice device : mBluetoothAdapter.getBondedDevices()){
                Log.i("BondedDevices--", device.getName() + device.getAddress());
                showLeBondedDevice(device);
                scanLeDevice(false);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_ENABLE_BT){
            if(resultCode == RESULT_OK){
                scanLeDevice(true);
            }
        }
    }

    /**操作开始关闭搜索*/
    private void scanLeDevice(final boolean enable){
        if(!mBluetoothAdapter.isEnabled()){
            ConfigUtil.showToask(this, "请开启蓝牙");
            return;
        }
        if(enable){
            autoCloseScanExecutorService.schedule(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mScanCallback);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ConfigUtil.showToask(BleBluetoothDeviceManagerActivity.this, "扫描完成");
                        }
                    });
                }
            }, SCAN_PERIOD, TimeUnit.MILLISECONDS);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mScanCallback);
            ConfigUtil.showToask(BleBluetoothDeviceManagerActivity.this, "开始扫描");
        }else{
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mScanCallback);
            ConfigUtil.showToask(BleBluetoothDeviceManagerActivity.this, "扫描完成");
        }
    }

    private void showLeBondedDevice(BluetoothDevice device){
        tv_deviceBonded.setVisibility(View.VISIBLE);
        tv_deviceBondedState.setVisibility(View.VISIBLE);

        if(StringUtil.isEmpty(device.getName())){
            tv_deviceBonded.setText(device.getAddress());
        }else{
            tv_deviceBonded.setText(device.getName());
        }
    }

    private boolean isHave = false;

    private BluetoothAdapter.LeScanCallback mScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mBluetoothDeviceList != null && mBluetoothDeviceList.size() != 0){
                        for (BluetoothDevice device2 : mBluetoothDeviceList){
                            if(device.getAddress().equals(device2.getAddress())){
                               isHave = true;
                                break;
                            }else{
                                isHave = false;
                            }
                        }
                    }

                    if(mBluetoothDeviceList != null && !isHave){
                        mBluetoothDeviceList.add(device);
                        mBluetoothDeviceAdapter.notifyItemRangeChanged(0, mBluetoothDeviceList.size() - 1);
                    }
                }
            });
        }
    };

    @Override
    public void gattConnected() {
        ConfigUtil.showToask(this, "连接成功");
    }

    @Override
    public void gattDisconnected() {
        ConfigUtil.showToask(this, "连接失败");
    }

    @Override
    public void gattServicesDiscovered() {
        displayGattServices(mBluetoothLeService.getSupportedGattServices());
    }

    @Override
    public void gattDataAvailable(String data) {
        displayData(data);
    }

    private void displayGattServices(List<BluetoothGattService> gattServices){
        if(gattServices == null){
            return;
        }
        findService(gattServices.get(gattServices.size() - 1));

        /*for (BluetoothGattService gattService : gattServices){
            Log.e("------", "-----------------------------");
            Log.i("gattService--id", gattService.getUuid().toString());

            if(gattService.getUuid().toString().equals(GlobalGattAttributes.DEVICE_SERVICE)){
                List<BluetoothGattCharacteristic> characteristics = gattService.getCharacteristics();
                for (BluetoothGattCharacteristic characteristic : characteristics){
                    Log.i("characteristic--id", characteristic.getUuid().toString());
                    if(characteristic.getUuid().toString().equals(GlobalGattAttributes.DEVICE_SERVICE_CHAR)){
                        int charaProp = characteristic.getProperties();
                        Log.i("flag---", charaProp + "--" + characteristic.getPermissions());
                        if((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0){
                            Log.i("可读", "可读");
                            Log.i("可读", (charaProp | BluetoothGattCharacteristic.PROPERTY_READ) + "");
                            if(mNotifyCharacteristic != null){
                                mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, false);
                                mNotifyCharacteristic = null;
                            }
                            mBluetoothLeService.readCharacteristic(characteristic);
                        }
                        if((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0){
                            Log.i("可通知", "可通知");
                            Log.i("可通知", (charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) + "");
                            mNotifyCharacteristic = characteristic;
                            mBluetoothLeService.setCharacteristicNotification(characteristic, true);
                        }
                        break;
                    }
                }
                break;
            }
        }*/
    }

    private void findService(BluetoothGattService gattService){
        List<BluetoothGattCharacteristic> characteristics = gattService.getCharacteristics();
        for (BluetoothGattCharacteristic characteristic : characteristics) {
            Log.i("characteristic--id", characteristic.getUuid().toString());
            if (characteristic.getUuid().toString().equals(GlobalGattAttributes.DEVICE_SERVICE_CHAR)) {
                int charaProp = characteristic.getProperties();
                Log.i("flag---", charaProp + "--" + characteristic.getPermissions());
                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                    Log.i("可读", "可读");
                    Log.i("可读", (charaProp | BluetoothGattCharacteristic.PROPERTY_READ) + "");
                    if (mNotifyCharacteristic != null) {
                        mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, false);
                        mNotifyCharacteristic = null;
                    }
                    mBluetoothLeService.readCharacteristic(characteristic);
                }
                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                    Log.i("可通知", "可通知");
                    Log.i("可通知", (charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) + "");
                    mNotifyCharacteristic = characteristic;
                    mBluetoothLeService.setCharacteristicNotification(characteristic, true);
                }
            }
        }
    }

    private void displayData(String data){
        if(data != null){
            Log.i("data---", data);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unbindService(mServiceConnection);
        //mBluetoothLeService = null;
        if(gattUpdateReceiverManager != null){
            gattUpdateReceiverManager.unRegister();
        }
        autoCloseScanExecutorService.shutdown();
    }

    @Override
    public void disConnected() {

    }

    @Override
    public void disNetConnected() {

    }

    @Override
    public void netConnected() {

    }
}
