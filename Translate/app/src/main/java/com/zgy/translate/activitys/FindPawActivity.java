package com.zgy.translate.activitys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.zgy.translate.R;
import com.zgy.translate.base.BaseActivity;
import com.zgy.translate.controllers.ActivityController;
import com.zgy.translate.controllers.RequestController;
import com.zgy.translate.domains.dtos.UserInfoDTO;
import com.zgy.translate.domains.request.CommonRequest;
import com.zgy.translate.domains.response.CommonResponse;
import com.zgy.translate.global.GlobalConstants;
import com.zgy.translate.global.GlobalParams;
import com.zgy.translate.managers.GsonManager;
import com.zgy.translate.managers.UserMessageManager;
import com.zgy.translate.managers.inst.CommonLoginManager;
import com.zgy.translate.managers.inst.inter.CommonLoginManagerInterface;
import com.zgy.translate.utils.ConfigUtil;
import com.zgy.translate.utils.RedirectUtil;
import com.zgy.translate.utils.StringUtil;
import com.zgy.translate.widget.CommonBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FindPawActivity extends BaseActivity implements CommonBar.CommonBarInterface, RequestController.RequestCallInterface,
        CommonLoginManagerInterface{

    @BindView(R.id.afp_cb) CommonBar commonBar;
    @BindView(R.id.afp_tv_phone) TextView tv_phone;
    @BindView(R.id.afp_et_paw) EditText et_paw;
    @BindView(R.id.afp_et_pawAgain) EditText et_pawAgain;

    private String code;
    private String phone;
    private String paw;
    private CommonLoginManager commonLoginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_paw);
        ButterKnife.bind(this);
        super.init();
    }

    @Override
    public void initView() {
        phone = getIntent().getStringExtra("phone");
        code = getIntent().getStringExtra("code");
        tv_phone.setText(phone);
        commonLoginManager = new CommonLoginManager(this, this);
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

    @OnClick(R.id.afp_tv_submit) void submit(){
        paw = et_paw.getText().toString();
        String pasAg = et_pawAgain.getText().toString();
        if(StringUtil.isEmpty(paw) || StringUtil.isEmpty(pasAg)){
            ConfigUtil.showToask(this, GlobalConstants.NULL_MSG);
            return;
        }
        if(!paw.equals(pasAg)){
            ConfigUtil.showToask(this, GlobalConstants.PASSWORD_NO_YI);
            return;
        }
        super.progressDialog.show();
        CommonRequest request = new CommonRequest();
        request.setPassword(paw);
        request.setPasswrodRepeat(pasAg);
        request.setPhone(phone);
        request.setPhoneCode(code);
        RequestController.getInstance().init(this)
                .addRequest(RequestController.RESET_PASSWORD, request)
                .addCallInterface(this)
                .build();
    }

    @Override
    public void success(CommonResponse response) {
        ConfigUtil.showToask(this, "修改成功");
        commonLoginManager.comLogin(phone, paw);
        ConfigUtil.showToask(this, "正在登录...");
    }

    @Override
    public void error(CommonResponse response) {
        super.progressDialog.dismiss();
    }

    @Override
    public void fail(String error) {
        super.progressDialog.dismiss();
    }

    @Override
    public void loginSuccess() {
        super.progressDialog.dismiss();
        ConfigUtil.showToask(this, "登录成功");
        ActivityController.finishActivity();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        commonLoginManager = null;
        RequestController.getInstance().removeCallInterface();
    }
}
