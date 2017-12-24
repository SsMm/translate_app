package com.zgy.translate.activitys;


import android.bluetooth.BluetoothAdapter;
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

public class SplashActivity extends BaseActivity{


    private BluetoothAdapter mBluetoothAdapter;

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
        if(!ConfigUtil.isNetWorkConnected(this)){
            ConfigUtil.showToask(this, "请检查网络!");
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isLogin()){
            if(mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()){ //连接已开
                RedirectUtil.redirect(this, VoiceTranslateActivity.class);
            }else{
                ConfigUtil.showToask(this, "请开启蓝牙，连接耳机！");
                RedirectUtil.redirect(this, VoiceTranslateActivity.class);
            }
        }else{
            //跳转到登录页面
            RedirectUtil.redirect(this, LoginActivity.class);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothAdapter = null;
    }


}
