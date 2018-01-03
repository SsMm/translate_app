package com.zgy.translate.managers.inst;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.util.Log;

import com.zgy.translate.global.GlobalConstants;
import com.zgy.translate.global.GlobalGattAttributes;
import com.zgy.translate.global.GlobalParams;
import com.zgy.translate.managers.inst.inter.BluetoothProfileManagerInterface;
import com.zgy.translate.managers.inst.inter.CreateGattManagerInterface;
import com.zgy.translate.receivers.interfaces.BluetoothLeGattUpdateReceiverInterface;
import com.zgy.translate.services.BluetoothLeService;
import com.zgy.translate.utils.ConfigUtil;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
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
    private volatile BluetoothDevice connDevice;
    private CreateGattManagerInterface gattManagerInterface;
    private GattUpdateReceiverManager gattUpdateReceiverManager;
    private ScheduledExecutorService autoCloseScanExecutorService;
    private int findNum = 0;

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
        //定时线程
        autoCloseScanExecutorService = Executors.newSingleThreadScheduledExecutor();

        //初始化服务
        Intent gattServiceIntent = new Intent(mContext.getApplicationContext(), BluetoothLeService.class);
        mContext.bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

        //注册
        gattUpdateReceiverManager = new GattUpdateReceiverManager(mContext);
        gattUpdateReceiverManager.register(this);

        profileManager = new BluetoothProfileManager(mContext, this);
        profileManager.getBluetoothProfile();

    }

    public void nextGetProfile(){
        if(profileManager != null){
            profileManager.getBluetoothProfile();
        }
    }

    public void disconnectGatt(){
        if(mBluetoothLeService != null){
            mBluetoothLeService.disconnect();
            mBluetoothLeService.close();
        }
    }
    /**
     * 获取蓝牙连接信息
     * */
    @Override
    public void bluetoothOff() {
        //disconnectGatt();
        gattManagerInterface.bluetoothOff();
    }

    @Override
    public void noProfile() {
        gattManagerInterface.noProfile();
    }

    @Override
    public void deviceConning() {
        //连接中
        profileManager.getBluetoothProfile();
    }

    @Override
    public void getA2DPProfileFinish(boolean result) {
        if(result){
            profileManager.closeProfileProxy();
            if(connDevice != null){
                mBluetoothLeService.connect(connDevice.getAddress());
                return;
            }
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
            if(device.getName() == null){
                return;
            }
            String mac = GlobalParams.BLUETOOTH_MAC;
            String m = null;
            if(mac != null){
                String[] ma = mac.split(":");
                m = "C0" + ":" + ma[1] + ":" + ma[2] + ":" + ma[3] + ":" + ma[4] + ":" + ma[5];
                Log.i("ma-----", m);
            }
            if(device.getAddress().equals(m) && GlobalConstants.BLUETOOTH_BLE.equals(device.getName())){
                if(mScanning){
                    scanLeDevice(false);
                }
                if (mBluetoothLeService != null) {
                    if(autoCloseScanExecutorService != null){
                        autoCloseScanExecutorService.shutdown();
                        autoCloseScanExecutorService = null;
                    }
                    ConfigUtil.showToask(mContext, "开始连接...");
                    connDevice = device;
                    final boolean result = mBluetoothLeService.connect(device.getAddress());
                    Log.i(TAG, "Connect request result=" + result);
                }
            }
        }
    };

    /**操作开始关闭搜索*/
    private void scanLeDevice(final boolean enable){
        if(mBluetoothAdapter == null){
            return;
        }
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
                    Log.i("扫描完成", "扫描完成");
                    //ConfigUtil.showToask(mContext, "扫描完成");
                }
            },SCAN_PERIOD, TimeUnit.MILLISECONDS);*/

            mScanning = true;
            mBluetoothAdapter.startLeScan(mScanCallback);
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
        gattManagerInterface.conState(true);
    }

    @Override
    public void gattDisconnected() {
        gattManagerInterface.conState(false);
        mBluetoothLeService.disconnect();
        mBluetoothLeService.connect(connDevice.getAddress());
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
            profileManager.closeProfileProxy();
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
        if(autoCloseScanExecutorService != null){
            autoCloseScanExecutorService.shutdown();
            autoCloseScanExecutorService = null;
        }
        mBluetoothAdapter = null;
        connDevice = null;
    }
}
