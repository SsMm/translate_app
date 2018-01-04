package com.zgy.translate.activitys;

import android.Manifest;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zgy.translate.R;
import com.zgy.translate.base.BaseActivity;
import com.zgy.translate.controllers.RequestController;
import com.zgy.translate.domains.request.CommonRequest;
import com.zgy.translate.domains.response.CommonResponse;
import com.zgy.translate.global.GlobalConstants;
import com.zgy.translate.managers.inst.CommonLoginManager;
import com.zgy.translate.managers.inst.inter.CommonLoginManagerInterface;
import com.zgy.translate.utils.ConfigUtil;
import com.zgy.translate.utils.RedirectUtil;
import com.zgy.translate.utils.StringUtil;
import com.zgy.translate.widget.CommonBar;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends BaseActivity implements CommonBar.CommonBarInterface, RequestController.RequestCallInterface,
        CommonLoginManagerInterface{
    @BindView(R.id.ar_cb) CommonBar commonBar;
    @BindView(R.id.ar_et_phoneNum) EditText et_phoneNum;
    @BindView(R.id.ar_et_phoneCode) EditText et_phoneCode;
    @BindView(R.id.ar_et_paw) EditText et_paw;
    @BindView(R.id.ar_et_pawNext) EditText et_pawNext;
    @BindView(R.id.ar_tv_sendCode) TextView tv_sendCode;

    private CommonRequest request;
    private boolean isSend = false;
    private boolean isRegister = false;
    private String phone;
    private String paw;
    private CommonLoginManager commonLoginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        super.init();
    }

    @Override
    public void initView() {
        commonLoginManager = new CommonLoginManager(this, this);
        showPermission();
    }

    @Override
    public void initEvent() {
        commonBar.setBarInterface(this);
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

    @Override
    public void checkLeftIcon() {
        finish();
    }

    @Override
    public void checkRightIcon() {

    }


    /**发送验证码*/
    @OnClick(R.id.ar_tv_sendCode) void sendCode(){
        if(isSend){
            ConfigUtil.showToask(this, "验证码已发");
            return;
        }
        String num = et_phoneNum.getText().toString();
        if(StringUtil.isEmpty(num)){
            ConfigUtil.showToask(this, GlobalConstants.NULL_PHONE);
        }else{
            isSend = true;
            request = new CommonRequest();
            request.setPhone(num);
            super.progressDialog.show();
            RequestController.getInstance().init(this)
                    .addRequest(RequestController.SEND_CODE, request)
                    .addCallInterface(this).build();
        }

    }

    /**注册*/
    @OnClick(R.id.ar_tv_goRegister) void goRegister(){
        phone = et_phoneNum.getText().toString();
        String code = et_phoneCode.getText().toString();
        paw = et_paw.getText().toString();
        String pawNext = et_pawNext.getText().toString();

        if(StringUtil.isEmpty(phone) || StringUtil.isEmpty(code) ||
                StringUtil.isEmpty(paw) || StringUtil.isEmpty(pawNext)){
            ConfigUtil.showToask(this, GlobalConstants.NULL_MSG);
            return;
        }
        if(!paw.equals(pawNext)){
            ConfigUtil.showToask(this, GlobalConstants.PASSWORD_NO_YI);
            return;
        }
        isRegister = true;
        request = new CommonRequest();
        request.setPhone(phone);
        request.setPhoneCode(code);
        request.setPassword(paw);
        request.setPasswrodRepeat(pawNext);
        super.progressDialog.show();
        RequestController.getInstance().init(this)
                .addRequest(RequestController.REGISTER, request)
                .addCallInterface(this).build();

    }


    @Override
    public void success(CommonResponse response) {
        if(isSend){
            super.progressDialog.dismiss();
            ConfigUtil.showToask(this, "发送成功");
            codeTime();
        }
        if(isRegister){
            ConfigUtil.showToask(this, "注册成功");
            commonLoginManager.comLogin(phone, paw);
            ConfigUtil.showToask(this, "正在登录...");
        }
    }

    @Override
    public void error(CommonResponse response) {
        super.progressDialog.dismiss();
       if(isSend){
           isSend = false;
           ConfigUtil.showToask(this, "发送失败");
       }
       if(isRegister){
           ConfigUtil.showToask(this, "注册失败");
       }
        if(response != null && response.getErrors() != null){
            ConfigUtil.showToask(this, response.getErrors().get(0).getMessage());
        }
    }

    @Override
    public void fail(String error) {
        super.progressDialog.dismiss();
        isSend = false;
    }


    private void codeTime(){
        CountDownTimer countDownTimer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long l) {
                long time = l / 1000;
                tv_sendCode.setText(String.valueOf(time));
            }

            @Override
            public void onFinish() {
                isSend = false;
                tv_sendCode.setText("发送验证码");
            }
        }.start();
   }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        request = null;
        commonLoginManager = null;
        RequestController.getInstance().removeCallInterface();
    }


    @Override
    public void loginSuccess() {
        super.progressDialog.dismiss();
        ConfigUtil.showToask(this, "登录成功");
        RedirectUtil.redirect(this, VoiceTranslateActivity.class);
        finish();
    }

    @Override
    public void loginError() {
        super.progressDialog.dismiss();
    }

    @Override
    public void loginFail() {
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
}
