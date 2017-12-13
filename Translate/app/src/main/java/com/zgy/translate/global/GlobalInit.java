package com.zgy.translate.global;

import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.meituan.android.walle.WalleChannelReader;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.zgy.translate.AppApplication;
import com.zgy.translate.domains.dtos.BluetoothLeConnectionDTO;
import com.zgy.translate.domains.dtos.BluetoothSocketDTO;
import com.zgy.translate.services.BluetoothLeService;
import com.zgy.translate.utils.ConfigUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.internal.queue.MpscLinkedQueue;

/**
 * Created by zhouguangyue on 2017/12/6.
 */

public class GlobalInit {
    private static final String TAG = GlobalInit.class.getName();

    private GlobalInit(){}
    private static GlobalInit globalInit;
    private AppApplication appApplication;
    private Context appContext;
    private final boolean isDebug = false;

    public static BluetoothLeService mBluetoothLeService;
    public static ServiceConnection mServiceConnection;

    public static List<BluetoothLeConnectionDTO> leConnectionDTOList = new ArrayList<>(); //ble蓝牙连接集合
    public static List<BluetoothSocketDTO> bluetoothSocketDTOList = new ArrayList<>(); //传统蓝牙连接集合

    public static GlobalInit getInstance() {
        if(globalInit == null){
            synchronized (GlobalInit.class){
                if(globalInit == null){
                    globalInit = new GlobalInit();
                }
            }
        }
        return globalInit;
    }

    public void onInit(Context context, AppApplication app){
        appContext = context;
        appApplication = app;
    }

    private void initBuglyCrashReport(){
        Beta.canNotifyUserRestart = true;
        String channel = WalleChannelReader.getChannel(appContext);
        Bugly.setAppChannel(appContext, channel);

        Bugly.init(appContext, "", isDebug);
    }

    /**绑定蓝牙接收通知服务*/
    private void bindBleService(){

    }


    public void onDestroy(){
        unBindBleService();

    }


    private void unBindBleService(){
        if(mBluetoothLeService != null){
            appContext.unbindService(mServiceConnection);
            mBluetoothLeService = null;
        }

    }





}
