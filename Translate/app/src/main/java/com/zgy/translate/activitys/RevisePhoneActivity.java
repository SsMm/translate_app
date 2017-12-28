package com.zgy.translate.activitys;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.zgy.translate.R;
import com.zgy.translate.base.BaseActivity;
import com.zgy.translate.controllers.RequestController;
import com.zgy.translate.domains.dtos.UserInfoDTO;
import com.zgy.translate.domains.request.CommonRequest;
import com.zgy.translate.domains.response.CommonResponse;
import com.zgy.translate.global.GlobalConstants;
import com.zgy.translate.global.GlobalParams;
import com.zgy.translate.managers.GsonManager;
import com.zgy.translate.managers.UserMessageManager;
import com.zgy.translate.utils.ConfigUtil;
import com.zgy.translate.utils.StringUtil;
import com.zgy.translate.widget.CommonBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RevisePhoneActivity extends BaseActivity implements CommonBar.CommonBarInterface, RequestController.RequestCallInterface{

    @BindView(R.id.arp_cb) CommonBar commonBar;
    @BindView(R.id.arp_et_phone) EditText et_phone;
    @BindView(R.id.arp_et_code) EditText et_code;
    @BindView(R.id.arp_tv_sendCode) TextView tv_sendCode;

    private boolean isSend = false;
    private boolean isSumbit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revise_phone);
        ButterKnife.bind(this);
        super.init();
    }

    @Override
    public void initView() {

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

    @OnClick(R.id.arp_tv_sendCode) void sendCode(){
        if(isSend){
            ConfigUtil.showToask(this, "验证码已发");
            return;
        }
        String num = et_phone.getText().toString();
        if(StringUtil.isEmpty(num)){
            ConfigUtil.showToask(this, GlobalConstants.NULL_PHONE);
            return;
        }
        super.progressDialog.show();
        isSend = true;
        CommonRequest request = new CommonRequest();
        request.setPhone(num);
        /*RequestController.getInstance().init(this)
                .addRequest()
                .addCallInterface(this)
                .build();*/
    }

    @OnClick(R.id.arp_tv_sumbit) void sumbit(){
        String num = et_phone.getText().toString();
        String code = et_code.getText().toString();
        if(StringUtil.isEmpty(num) || StringUtil.isEmpty(code)){
            ConfigUtil.showToask(this, GlobalConstants.NULL_MSG);
            return;
        }
        super.progressDialog.show();
        isSumbit = true;
        CommonRequest request = new CommonRequest();
        request.setPhone(num);
        RequestController.getInstance().init(this)
                .addRequest(RequestController.PROFILE, request)
                .addCallInterface(this);
    }

    @Override
    public void success(CommonResponse response) {
        super.progressDialog.dismiss();
        if(isSend){
            codeTime();
        }
        if(isSumbit){
            if(response != null) {
                if (UserMessageManager.isUserInfo(this)) {
                    UserMessageManager.deleteUserInfo(this);
                }
                UserInfoDTO userInfoDTO = new UserInfoDTO();
                userInfoDTO.setAppKey(GlobalParams.userInfoDTO.getAppKey());
                userInfoDTO.setBirthday(response.getBirthday());
                userInfoDTO.setIcon(response.getIcon());
                userInfoDTO.setName(response.getName());
                userInfoDTO.setSignature(response.getSignature());
                userInfoDTO.setSex(response.getSex());
                userInfoDTO.setMic(GlobalParams.userInfoDTO.isMic());
                userInfoDTO.setPhone(response.getPhone());
                GlobalParams.userInfoDTO = userInfoDTO;
                String user = GsonManager.getInstance().toJson(userInfoDTO);
                UserMessageManager.saveUserInfo(this, user);
                finish();
            }
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

    private void codeTime() {
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
    }
}
