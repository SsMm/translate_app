package com.zgy.translate.global;


import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.ServiceConnection;
import android.util.Log;

import com.iflytek.cloud.SpeechUnderstander;
import com.iflytek.cloud.SpeechUtility;
import com.imnjh.imagepicker.PickerConfig;
import com.imnjh.imagepicker.SImagePicker;
import com.meituan.android.walle.WalleChannelReader;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
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
        baseInit();
    }

    private void baseInit(){
        //initLeakCanary();
        initBuglyCrashReport();
        initUtility();
        initSImagePicker();
        initShareSDK();
    }

    /**
     * 科大讯飞
     * */
    private void initUtility(){
        SpeechUtility.createUtility(appContext, "appid=" + GlobalKey.UTILITY_APP_ID);
    }

    /**
     * 友盟
     * */
    private void initShareSDK(){
        UMConfigure.init(appContext, "5a4b61148f4a9d294200008e", null, UMConfigure.DEVICE_TYPE_PHONE, "");
    }

    /**
     * 初始化LeakCanary
     * */
    private void initLeakCanary(){
        if(LeakCanary.isInAnalyzerProcess(appContext)){
            return;
        }
        LeakCanary.install(appApplication);
    }

    private void initBuglyCrashReport(){
        Beta.canNotifyUserRestart = true;
        //String channel = WalleChannelReader.getChannel(appContext);
        //Bugly.setAppChannel(appContext, channel);
        //UMConfigure.init(appContext, "", channel, UMConfigure.DEVICE_TYPE_PHONE, null);
        Bugly.init(appContext, "ac5bd004b5", false);
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


    public void onDestroy(){

    }

}
