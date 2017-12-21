package com.zgy.translate.managers;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import com.zgy.translate.activitys.BleBluetoothDeviceManagerActivity;
import com.zgy.translate.activitys.BluetoothDeviceManagerActivity;
import com.zgy.translate.domains.dtos.BluetoothSocketDTO;
import com.zgy.translate.global.GlobalConstants;
import com.zgy.translate.global.GlobalInit;
import com.zgy.translate.utils.ConfigUtil;
import com.zgy.translate.utils.RedirectUtil;

import java.io.IOException;

/**
 * Created by zhouguangyue on 2017/12/14.
 */

public class AppReceiverManager {


    public static BluetoothConnectionStateReceiver buildBlueConnStaRec(){
        return new BluetoothConnectionStateReceiver();
    }



   public static class BluetoothConnectionStateReceiver extends BroadcastReceiver{

       @Override
       public void onReceive(Context context, Intent intent) {
           String action = intent.getAction();
           BluetoothDevice device;
           if(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED.equals(action)){
               int status = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, 0);
               device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

               switch (status){
                   case BluetoothAdapter.STATE_DISCONNECTED: //断开连接
                       removeBluetoothDeviceCon(device);
                       ConfigUtil.showToask(context, GlobalConstants.STATE_DISCONNECTED);
                       if(!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
                           RedirectUtil.redirect(context, BluetoothDeviceManagerActivity.class); //传统搜索
                       }else{
                           //RedirectUtil.redirect(this, BleBluetoothDeviceManagerActivity.class); //ble搜索
                           RedirectUtil.redirect(context, BluetoothDeviceManagerActivity.class); //传统搜索
                       }
                       ((Activity) context).finish();
                       break;
               }
           }

       }

       private void removeBluetoothDeviceCon(BluetoothDevice device){
           if(GlobalInit.bluetoothSocketDTOList != null && GlobalInit.bluetoothSocketDTOList.size() > 0){
               for (BluetoothSocketDTO d : GlobalInit.bluetoothSocketDTOList){
                   if(device.getAddress().equals(d.getmBluetoothDevice().getAddress())){
                       try {
                           if(d.getmBluetoothSocket() != null){
                               d.getmBluetoothSocket().close();
                           }
                           if(d.getmBluetoothSocketConThread() != null){
                               d.getmBluetoothSocketConThread().join();
                           }
                           GlobalInit.bluetoothSocketDTOList.remove(d);
                           break;
                       } catch (Exception e) {
                           e.printStackTrace();
                           break;
                       }
                   }
               }
           }
       }

   }


   /**连接状态*/
   public static IntentFilter connectionStateIntentFilter(){
       final IntentFilter intentFilter = new IntentFilter();

       intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);

       return intentFilter;
   }

}
