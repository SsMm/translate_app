package com.zgy.translate;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zgy.translate.activitys.BleBluetoothDeviceManagerActivity;
import com.zgy.translate.activitys.BluetoothDeviceManagerActivity;
import com.zgy.translate.activitys.VoiceTranslateActivity;
import com.zgy.translate.base.BaseActivity;
import com.zgy.translate.utils.ConfigUtil;




public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showPermission();
        super.init();
    }

    @Override
    public void initView() {
        TextView textView = (TextView) findViewById(R.id.share);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showShare();
                Intent intent = new Intent(MainActivity.this, BluetoothDeviceManagerActivity.class);
                startActivity(intent);
            }
        });
        TextView device = (TextView) findViewById(R.id.device);
        device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BleBluetoothDeviceManagerActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void initEvent() {

    }

    @Override
    public void initData() {

    }



    private void showPermission(){
        RxPermissions rxPermissions = new RxPermissions(this);

        rxPermissions.request(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(granted -> {
                    if(!granted){
                        ConfigUtil.showToask(this, "请在手机设置中打开相应权限！");
                    }
                });
    }


    @Override
    public void blueOff() {

    }

    @Override
    public void disConnected() {

    }

    @Override
    public void connected() {

    }

    @Override
    public void disNetConnected() {

    }

    @Override
    public void netConnected() {

    }
}
