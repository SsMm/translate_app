package com.zgy.translate.activitys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class FindPawActivity extends BaseActivity implements CommonBar.CommonBarInterface, RequestController.RequestCallInterface{

    @BindView(R.id.afp_cb) CommonBar commonBar;
    @BindView(R.id.afp_tv_phone) TextView tv_phone;
    @BindView(R.id.afp_et_paw) EditText et_paw;
    @BindView(R.id.afp_et_pawAgain) EditText et_pawAgain;

    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_paw);
        ButterKnife.bind(this);
        super.init();
    }

    @Override
    public void initView() {
        if(GlobalParams.userInfoDTO == null){
            GlobalParams.userInfoDTO = UserMessageManager.getUserInfo(this);
        }
        tv_phone.setText(GlobalParams.userInfoDTO.getPhone());
        code = getIntent().getStringExtra("code");
        Log.i("code", code);
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

    @OnClick(R.id.afp_tv_submit) void submit(){
        String paw = et_paw.getText().toString();
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
        request.setPhone(tv_phone.getText().toString());
        request.setPhoneCode(code);
        RequestController.getInstance().init(this)
                .addRequest(RequestController.RESET_PASSWORD, request)
                .addCallInterface(this)
                .build();
    }

    @Override
    public void success(CommonResponse response) {
        super.progressDialog.dismiss();
        ConfigUtil.showToask(this, "修改成功");
        finish();
    }

    @Override
    public void error(CommonResponse response) {
        super.progressDialog.dismiss();
    }

    @Override
    public void fail(String error) {
        super.progressDialog.dismiss();
    }
}
