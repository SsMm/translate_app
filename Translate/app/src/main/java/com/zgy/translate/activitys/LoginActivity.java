package com.zgy.translate.activitys;


import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zgy.translate.R;
import com.zgy.translate.base.BaseActivity;
import com.zgy.translate.controllers.RequestController;
import com.zgy.translate.domains.dtos.UserInfoDTO;
import com.zgy.translate.domains.request.CommonRequest;
import com.zgy.translate.domains.response.CommonResponse;
import com.zgy.translate.global.GlobalInit;
import com.zgy.translate.global.GlobalParams;
import com.zgy.translate.managers.GsonManager;
import com.zgy.translate.managers.UserMessageManager;
import com.zgy.translate.utils.ConfigUtil;
import com.zgy.translate.utils.RedirectUtil;
import com.zgy.translate.utils.StringUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity implements RequestController.RequestCallInterface{

    @BindView(R.id.al_et_phoneNum) EditText et_phoneNum; //手机号
    @BindView(R.id.al_et_phonePaw) EditText et_phonePaw; //密码


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        super.init();
    }

    @Override
    public void initView() {
        showPermission();
    }

    @Override
    public void initEvent() {

    }

    @Override
    public void initData() {

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

    @OnClick(R.id.al_tv_login) void login(){
        String num = et_phoneNum.getText().toString();
        String paw = et_phonePaw.getText().toString();
        if(StringUtil.isEmpty(num) || StringUtil.isEmpty(paw)){
            ConfigUtil.showToask(this, "信息不能为空");
        }else{
            CommonRequest request = new CommonRequest();
            request.setPhone(num);
            request.setPassword(paw);
            request.setAppId("earbud_app");
            request.setDevice(ConfigUtil.phoneDevice() + ConfigUtil.phoneMsg(this));
            super.progressDialog.show();
            RequestController.getInstance().init(this)
                    .addRequest(RequestController.LOGIN, request)
                    .addCallInterface(this)
                    .build();
        }

    }

    @OnClick(R.id.al_tv_register) void register(){
        RedirectUtil.redirect(this, RegisterActivity.class);
        finish();
    }

    /**忘记密码*/
    @OnClick(R.id.al_ll_forgetMsg) void forget(){
        RedirectUtil.redirect(this, ForgetPawActivity.class);
        finish();
    }


    @Override
    public void success(CommonResponse response) {
        super.progressDialog.dismiss();
        if(response != null){
            if(UserMessageManager.isUserInfo(this)){
                UserMessageManager.deleteUserInfo(this);
            }
            UserInfoDTO userInfoDTO = new UserInfoDTO();
            userInfoDTO.setAppKey(response.getAppKey());
            userInfoDTO.setBirthday(response.getBirthday());
            userInfoDTO.setIcon(response.getIcon());
            userInfoDTO.setName(response.getName());
            userInfoDTO.setSignature(response.getSignature());
            userInfoDTO.setSex(response.getSex());
            userInfoDTO.setMic(true);
            userInfoDTO.setPhone(et_phoneNum.getText().toString());
            GlobalParams.userInfoDTO = userInfoDTO;
            String user = GsonManager.getInstance().toJson(userInfoDTO);
            UserMessageManager.saveUserInfo(this, user);
            RedirectUtil.redirect(this, VoiceTranslateActivity.class);
            finish();
        }
    }

    @Override
    public void error(CommonResponse response) {
        super.progressDialog.dismiss();
    }

    @Override
    public void fail(String error) {
        super.progressDialog.dismiss();
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
    protected void onDestroy() {
        super.onDestroy();
    }
}
