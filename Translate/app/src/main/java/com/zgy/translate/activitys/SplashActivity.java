package com.zgy.translate.activitys;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.zgy.translate.MainActivity;
import com.zgy.translate.R;

import com.zgy.translate.base.BaseActivity;

import com.zgy.translate.domains.dtos.UserInfoDTO;
import com.zgy.translate.global.GlobalConstants;
import com.zgy.translate.global.GlobalInit;
import com.zgy.translate.global.GlobalParams;

import com.zgy.translate.managers.UserMessageManager;
import com.zgy.translate.managers.inst.BluetoothProfileManager;
import com.zgy.translate.managers.inst.inter.BluetoothProfileManagerInterface;
import com.zgy.translate.utils.ConfigUtil;
import com.zgy.translate.utils.RedirectUtil;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;

import butterknife.ButterKnife;

public class SplashActivity extends BaseActivity implements BluetoothProfileManagerInterface{

    private static final int SOCKET = 0;
    private static final int GATT = 1;

    private Bundle bundle;
    //private ScheduledExecutorService executorService;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothProfileManager profileManager;


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
            profileManager = new BluetoothProfileManager(this, this);
            if(mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()){ //连接已开
                if(!profileManager.getBluetoothProfile()){
                    checkBle(); //蓝牙打开，没有连接设备
                }
            }else{
                //跳转到蓝牙设备页面
                checkBle();
            }
        }else{
            //跳转到登录页面

        }
    }

    @Override
    public void getProfileFinish() {
        if(GlobalInit.askBlueMap != null){
            boolean ask = false;
            Set<BluetoothDevice> set = GlobalInit.askBlueMap.keySet();
            for (BluetoothDevice device : set){
                if(GlobalInit.askBlueMap.get(device)){
                    //连接上要求的耳机，跳转到翻译页面
                    ask = true;
                    break;
                }else{
                    ask = false;
                }
            }
            if(ask){
                //RedirectUtil.redirect(this, VoiceTranslateActivity.class);
                checkBle();
            }else{
                checkBle();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //executorService.shutdown();
        //executorService = null;
        finish();
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

    /**是否支持ble*/
    private void checkBle(){
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            //RedirectUtil.redirect(this, BluetoothDeviceManagerActivity.class); //传统搜索
            RedirectUtil.redirect(this, MainActivity.class);
        }else{
            //RedirectUtil.redirect(this, BleBluetoothDeviceManagerActivity.class); //ble搜索
            RedirectUtil.redirect(this, MainActivity.class);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bundle = null;
        mBluetoothAdapter = null;
        profileManager.onMyDestroy();
    }


}
