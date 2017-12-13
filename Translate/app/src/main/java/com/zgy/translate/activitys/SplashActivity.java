package com.zgy.translate.activitys;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothHealth;
import android.bluetooth.BluetoothProfile;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.zgy.translate.MainActivity;
import com.zgy.translate.R;
import com.zgy.translate.base.BaseActivity;
import com.zgy.translate.domains.dtos.BluetoothLeConnectionDTO;
import com.zgy.translate.domains.dtos.BluetoothSocketDTO;
import com.zgy.translate.domains.dtos.UserInfoDTO;
import com.zgy.translate.global.GlobalConstants;
import com.zgy.translate.global.GlobalInit;
import com.zgy.translate.global.GlobalParams;
import com.zgy.translate.global.GlobalStateCode;
import com.zgy.translate.managers.UserMessageManager;
import com.zgy.translate.utils.ConfigUtil;
import com.zgy.translate.utils.RedirectUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import butterknife.ButterKnife;

public class SplashActivity extends BaseActivity {

    private static final int SOCKET = 0;
    private static final int GATT = 1;

    private Bundle bundle;
    //private ScheduledExecutorService executorService;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothHeadset mBluetoothHeadset;
    private BluetoothA2dp mBluetoothA2dp;
    private BluetoothHealth mBluetoothHealth;
    private BluetoothGatt mBluetoothGatt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
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

    private void baseInit(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null){
            ConfigUtil.showToask(this, GlobalConstants.NO_BLUETOOTH);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isLogin()){
            if(mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()){ //连接已开
                getBluetoothProfile();
            }else{
                //跳转到蓝牙设备页面
                //checkBle();
                RedirectUtil.redirect(this, MainActivity.class);
            }
        }else{
            //跳转到登录页面

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //executorService.shutdown();
        //executorService = null;
    }

    /**登录情况*/
    private boolean isLogin(){
        UserInfoDTO userInfoDTO = UserMessageManager.quickGetUserInfo(this);
        if(userInfoDTO == null){
            return false;
        }
        GlobalParams.userInfoDTO = userInfoDTO;
        return true;
    }

    /**获取蓝牙是否连接以及连接状态*/
    private BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            Log.i("profile", profile +"");
            if(profile == BluetoothProfile.HEADSET){

                Log.i("HEADSET", "HEADSET");
            }else if(profile == BluetoothProfile.A2DP){

                getConnectionDevice(SOCKET, proxy);

                Log.i("A2DP", "A2DP");
            }else if(profile == BluetoothProfile.HEALTH){

                Log.i("HEALTH", "HEALTH");
            }else if(profile == BluetoothProfile.GATT){

                getConnectionDevice(GATT, proxy);
                Log.i("GATT", "GATT");
            }

        }

        @Override
        public void onServiceDisconnected(int profile) {
            if(profile == BluetoothProfile.HEADSET){
                mBluetoothHeadset = null;
                Log.i("mBluetoothHeadset", "没有连接");
            }else if(profile == BluetoothProfile.A2DP){
                mBluetoothA2dp = null;
                Log.i("A2DP", "没有连接A2DP");
            }else if(profile == BluetoothProfile.HEALTH){
                mBluetoothHealth = null;
                Log.i("HEALTH", "没有连接HEALTH");
            }else if(profile == BluetoothProfile.GATT){
                mBluetoothGatt = null;
                Log.i("GATT", "没有连接GATT");
            }
        }
    };

    private void getConnectionDevice(int flag, BluetoothProfile proxy){
        switch (flag){
            case SOCKET:
                mBluetoothA2dp = (BluetoothA2dp) proxy;
                for (BluetoothDevice device : mBluetoothA2dp.getConnectedDevices()){
                    if(GlobalInit.bluetoothSocketDTOList == null){
                        GlobalInit.bluetoothSocketDTOList = new ArrayList<>();
                    }
                    BluetoothSocketDTO socketDTO = new BluetoothSocketDTO();
                    socketDTO.setmBluetoothDevice(device);
                    GlobalInit.bluetoothSocketDTOList.add(socketDTO);
                    Log.i("mBluetoothA2dp", device.getName() + device.getAddress());
                }
                RedirectUtil.redirect(this, MainActivity.class);
                break;
            case GATT:
                mBluetoothGatt = (BluetoothGatt) proxy;
                for (BluetoothDevice device : mBluetoothGatt.getConnectedDevices()){
                    if(GlobalInit.leConnectionDTOList == null){
                        GlobalInit.leConnectionDTOList = new ArrayList<>();
                    }
                    BluetoothLeConnectionDTO dto = new BluetoothLeConnectionDTO();
                    dto.setmBluetoothDevice(device);
                    GlobalInit.leConnectionDTOList.add(dto);
                    Log.i("mBluetoothGatt", device.getName() + device.getAddress());
                }
                RedirectUtil.redirect(this, MainActivity.class);
                break;
        }

    }

    private void getBluetoothProfile(){
        int flag = -1;
        int a2dp = mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP);
        int headset = mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET);
        int health = mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEALTH);
        int gatt = mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.GATT);

        if(BluetoothProfile.STATE_CONNECTED == a2dp){
            flag = a2dp;
        }else if(BluetoothProfile.STATE_CONNECTED == headset){
            flag = headset;
        }else if(BluetoothProfile.STATE_CONNECTED == health){
            flag = health;
        }else if(BluetoothProfile.STATE_CONNECTED == gatt){
            flag = gatt;
        }else if(a2dp == 0 && headset == 0 && health == 0 && gatt == 0){
            flag = 0;
        }

        Log.i("flag--", flag +","+ a2dp +","+ headset +","+ health+","+ gatt);

        if(flag == 0){
            //checkBle(); //蓝牙打开，没有连接设备
            RedirectUtil.redirect(this, MainActivity.class);
        }else if(flag != -1){
            mBluetoothAdapter.getProfileProxy(this, mProfileListener, flag);
        }
    }

    /**是否支持ble*/
    private void checkBle(){
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            RedirectUtil.redirect(this, BluetoothDeviceManagerActivity.class); //传统搜索
        }else{
            RedirectUtil.redirect(this, BleBluetoothDeviceManagerActivity.class); //ble搜索
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bundle = null;
    }
}
