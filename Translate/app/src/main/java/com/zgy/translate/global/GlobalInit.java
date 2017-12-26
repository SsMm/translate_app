package com.zgy.translate.global;


import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.ServiceConnection;
import android.util.Log;

import com.imnjh.imagepicker.PickerConfig;
import com.imnjh.imagepicker.SImagePicker;
import com.meituan.android.walle.WalleChannelReader;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.zgy.translate.AppApplication;
import com.zgy.translate.R;
import com.zgy.translate.activitys.BluetoothDeviceManagerActivity;
import com.zgy.translate.domains.dtos.BluetoothLeConnectionDTO;
import com.zgy.translate.domains.dtos.BluetoothSocketDTO;
import com.zgy.translate.services.BluetoothLeService;
import com.zgy.translate.utils.GlideImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




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
    public static Map<BluetoothDevice, Boolean> askBlueMap = new HashMap<>();


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
        baseInit();
    }

    private void baseInit(){
        //initBuglyCrashReport();
        initSImagePicker();
    }

    private void initBuglyCrashReport(){
        Beta.canNotifyUserRestart = true;
        //String channel = WalleChannelReader.getChannel(appContext);
        //Bugly.setAppChannel(appContext, channel);

        Bugly.init(appContext, "ac5bd004b5", isDebug);
    }

    /**
     * 初始化相册
     * */
    private void initSImagePicker(){
        SImagePicker.init(new PickerConfig.Builder()
                .setAppContext(appContext)
                .setImageLoader(new GlideImageLoader())
                .setToolbaseColor(R.color.colorCommon)
                .build());
    }

    private void initAndroidAudioConverter(){

    }

    /**绑定蓝牙接收通知服务*/
    private void bindBleService(){

    }

    public void onDestroy(){
        //unBindBleService();
    }


    private void unBindBleService(){
        if(mBluetoothLeService != null){
            appContext.unbindService(mServiceConnection);
            mBluetoothLeService = null;
        }

    }





}
