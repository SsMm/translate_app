package com.zgy.translate.activitys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.zgy.translate.R;
import com.zgy.translate.base.BaseActivity;
import com.zgy.translate.global.GlobalConstants;
import com.zgy.translate.utils.ConfigUtil;
import com.zgy.translate.utils.StringUtil;
import com.zgy.translate.widget.CommonBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends BaseActivity implements CommonBar.CommonBarInterface{
    @BindView(R.id.ar_cb) CommonBar commonBar;
    @BindView(R.id.ar_et_phoneNum) EditText et_phoneNum;
    @BindView(R.id.ar_et_phoneCode) EditText et_phoneCode;
    @BindView(R.id.ar_et_paw) EditText et_paw;
    @BindView(R.id.ar_et_pawNext) EditText et_pawNext;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
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

    /**发送验证码*/
    @OnClick(R.id.ar_tv_sendCode) void sendCode(){
        String num = et_phoneNum.getText().toString();
        if(StringUtil.isEmpty(num)){
            ConfigUtil.showToask(this, GlobalConstants.NULL_PHONE);
            return;
        }

    }

    /**注册*/
    @OnClick(R.id.ar_tv_goRegister) void goRegister(){
        String num = et_phoneNum.getText().toString();
        String code = et_phoneCode.getText().toString();
        String paw = et_paw.getText().toString();
        String pawNext = et_pawNext.getText().toString();

        if(StringUtil.isEmpty(num) || StringUtil.isEmpty(code) ||
                StringUtil.isEmpty(paw) || StringUtil.isEmpty(pawNext)){
            ConfigUtil.showToask(this, GlobalConstants.NULL_MSG);
            return;
        }



    }


}
