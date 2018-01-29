package com.zgy.translate.activitys;

import android.Manifest;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
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
import com.zgy.translate.global.GlobalParams;
import com.zgy.translate.managers.UserMessageManager;
import com.zgy.translate.utils.ConfigUtil;
import com.zgy.translate.utils.RedirectUtil;
import com.zgy.translate.utils.StringUtil;
import com.zgy.translate.widget.CommonBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgetPawActivity extends BaseActivity implements CommonBar.CommonBarInterface, RequestController.RequestCallInterface{

    @BindView(R.id.afpp_cb) CommonBar commonBar;
    @BindView(R.id.afpp_et_phone) EditText et_phone;
    @BindView(R.id.afpp_et_phoneCode) EditText et_code;
    @BindView(R.id.afpp_tv_sendCode) TextView tv_sendCode;

    private boolean isSend = false;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_paw);
        ButterKnife.bind(this);
        super.init();
    }

    @Override
    public void initView() {
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

    @Override
    public void checkLeftIcon() {
        finish();
    }

    @Override
    public void checkRightIcon() {

    }

    @OnClick(R.id.afpp_tv_sendCode) void sendCode(){
        String phone = et_phone.getText().toString();
        if(isSend){
            ConfigUtil.showToask(this, GlobalConstants.SEND_CODE);
            return;
        }
        if(StringUtil.isEmpty(phone)){
            ConfigUtil.showToask(this, GlobalConstants.NULL_PHONE);
            return;
        }
        isSend = true;
        super.progressDialog.show();
        CommonRequest request = new CommonRequest();
        request.setPhone(phone);
        RequestController.getInstance().init(this)
                .addRequest(RequestController.SEND_PASSWORD_CODE, request)
                .addCallInterface(this)
                .build();
    }


    @OnClick(R.id.afpp_tv_next) void next(){
        String code = et_code.getText().toString();
        String phone = et_phone.getText().toString();
        if(StringUtil.isEmpty(code) || StringUtil.isEmpty(phone)){
            ConfigUtil.showToask(this, GlobalConstants.NULL_MSG);
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("code", code);
        bundle.putString("phone", phone);
        RedirectUtil.redirect(this, FindPawActivity.class, bundle);
    }

    @Override
    public void success(CommonResponse response) {
        super.progressDialog.dismiss();
        codeTime();
    }

    @Override
    public void error(CommonResponse response) {
        super.progressDialog.dismiss();
        isSend = false;
    }

    @Override
    public void fail(String error) {
        super.progressDialog.dismiss();
        isSend = false;
    }

    private void codeTime() {
         countDownTimer = new CountDownTimer(60 * 1000, 1000) {
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
        };
        countDownTimer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(countDownTimer != null){
            countDownTimer.cancel();
            countDownTimer = null;
        }
        RequestController.getInstance().removeParams();
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
