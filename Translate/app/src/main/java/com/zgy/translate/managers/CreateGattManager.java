package com.zgy.translate.managers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.zgy.translate.activitys.BleBluetoothDeviceManagerActivity;
import com.zgy.translate.global.GlobalConstants;
import com.zgy.translate.global.GlobalGattAttributes;
import com.zgy.translate.global.GlobalInit;
import com.zgy.translate.managers.inst.BluetoothProfileManager;
import com.zgy.translate.managers.inst.GattUpdateReceiverManager;
import com.zgy.translate.managers.inst.inter.BluetoothProfileManagerInterface;
import com.zgy.translate.managers.inst.inter.CreateGattManagerInterface;
import com.zgy.translate.receivers.interfaces.BluetoothLeGattUpdateReceiverInterface;
import com.zgy.translate.services.BluetoothLeService;
import com.zgy.translate.utils.ConfigUtil;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhouguangyue on 2017/12/22.
 */

public class CreateGattManager implements BluetoothProfileManagerInterface, BluetoothLeGattUpdateReceiverInterface {

    private static final String TAG = CreateGattManager.class.getSimpleName();
    private static final UUID[] MY_UUID = {UUID.fromString(GlobalGattAttributes.DEVICE_SERVICE)};
    private static final long SCAN_PERIOD = 10000;


    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothProfileManager profileManager;
    private BluetoothLeService mBluetoothLeService;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private Context mContext;
    private volatile boolean mScanning;
    private CreateGattManagerInterface gattManagerInterface;
    private GattUpdateReceiverManager gattUpdateReceiverManager;


    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if(!mBluetoothLeService.initialize()){
                Log.i(TAG, "Unable to initialize Bluetooth");
            }
            //mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothLeService = null;
        }
    };

    public CreateGattManager(Context context, CreateGattManagerInterface gattManagerInterface){
        mContext = context;
        this.gattManagerInterface = gattManagerInterface;
    }

    public CreateGattManager setParams(BluetoothAdapter bluetoothAdapter){
        mBluetoothAdapter = bluetoothAdapter;

        return this;
    }

    public void init(){
        profileManager = new BluetoothProfileManager(mContext, this);
        profileManager.getBluetoothProfile();

        //初始化服务
        Intent gattServiceIntent = new Intent(mContext.getApplicationContext(), BluetoothLeService.class);
        mContext.bindService(gattServiceIntent, mServiceConnection, mContext.BIND_AUTO_CREATE);

        //注册
        gattUpdateReceiverManager = new GattUpdateReceiverManager(mContext);
        gattUpdateReceiverManager.register(this);

    }

    /**
     * 获取蓝牙连接信息
     * */
    @Override
    public void noProfile() {
        gattManagerInterface.noProfile();
    }

    @Override
    public void getA2DPProfileFinish(boolean result) {
        if(result){
            scanLeDevice(true);
        }else{
         gattManagerInterface.noRequest();
        }
    }

    @Override
    public void getBLEProfileFinish(BluetoothGatt gatt, boolean result) {
        if(result){
            displayGattServices(gatt.getServices());
        }else{
            gattManagerInterface.noRequest();
        }
    }

    private BluetoothAdapter.LeScanCallback mScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            Log.i("device", device.getName() + device.getAddress());
            if (mBluetoothLeService != null) {
                if(mScanning){
                    scanLeDevice(false);
                }
                ConfigUtil.showToask(mContext, "开始连接...");
                final boolean result = mBluetoothLeService.connect(device.getAddress());
                Log.i(TAG, "Connect request result=" + result);
            }
        }
    };

    /**操作开始关闭搜索*/
    private void scanLeDevice(final boolean enable){
        if(!mBluetoothAdapter.isEnabled()){
            ConfigUtil.showToask(mContext, "请开启蓝牙");
            return;
        }
        if(enable){
            /*autoCloseScanExecutorService.schedule(new Runnable() {
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
            }, SCAN_PERIOD, TimeUnit.MILLISECONDS);*/

            mScanning = true;
            mBluetoothAdapter.startLeScan(MY_UUID, mScanCallback);
            ConfigUtil.showToask(mContext, "开始扫描");
        }else{
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mScanCallback);
            ConfigUtil.showToask(mContext, "扫描完成");
        }
    }

    /**
     * 与蓝牙设备建立关联
     * */
    @Override
    public void gattConnected() {
        ConfigUtil.showToask(mContext, "连接成功");
    }

    @Override
    public void gattDisconnected() {
        ConfigUtil.showToask(mContext, "连接失败");
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
            gattManagerInterface.gattOrder(data);
            Log.i("data---", data);
        }
    }

    public void onMyDestroy(){
        if(profileManager != null){
            profileManager.onMyDestroy();
            profileManager = null;
        }
        if(gattUpdateReceiverManager != null){
            gattUpdateReceiverManager.unRegister();
            gattUpdateReceiverManager = null;
        }
        mContext.unbindService(mServiceConnection);
        if(mBluetoothLeService != null){
            mBluetoothLeService.disconnect();
            mBluetoothLeService.close();
            mBluetoothLeService = null;
        }

        mBluetoothAdapter = null;
    }
}
