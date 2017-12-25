package com.zgy.translate.base;



import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.zgy.translate.controllers.ActivityController;
import com.zgy.translate.managers.AppReceiverManager;


/**
 * Created by zhou on 2017/4/27.
 */

public abstract class BaseActivity extends AppCompatActivity implements AppReceiverManager.BluetoothConnectionStateInterface{


    private AppReceiverManager.BluetoothConnectionStateReceiver bluetoothConnectionStateReceiver; //蓝牙连接状态

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityController.addActivity(this);
        registerBluetoothConState();
    }

    public void init(){
        initView();
        initEvent();
        initData();
    }

    public abstract void initView();
    public abstract void initEvent();
    public abstract void initData();

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    /**注册广播接收蓝牙连接状态*/
    private void registerBluetoothConState(){
        bluetoothConnectionStateReceiver = AppReceiverManager.buildBlueConnStaRec(this);
        registerReceiver(bluetoothConnectionStateReceiver, AppReceiverManager.connectionStateIntentFilter());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityController.removeActivity(this);
        unRegisterBluetoothConState();
    }


    /**解除状态绑定*/
    private void unRegisterBluetoothConState(){
        if(bluetoothConnectionStateReceiver != null){
            unregisterReceiver(bluetoothConnectionStateReceiver);
            bluetoothConnectionStateReceiver = null;
        }
    }


}
